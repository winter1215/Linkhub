package com.linkhub.portal.service.impl;
import java.util.List;

import com.linkhub.common.enums.GroupPanelTypeREnum;
import com.linkhub.common.model.dto.group.CreateGroupDto.Panel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.errorprone.annotations.Var;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.enums.GroupPermissionEnum;
import com.linkhub.common.mapper.GroupMemberMapper;
import com.linkhub.common.model.dto.group.GroupInviteRequest;
import com.linkhub.common.mapper.GroupPanelMapper;
import com.linkhub.common.model.dto.group.CreateGroupDto;
import com.linkhub.common.model.dto.group.UpdateGroupConfigDto;
import com.linkhub.common.model.dto.group.UpdateGroupFieldDto;
import com.linkhub.common.model.pojo.Group;
import com.linkhub.common.mapper.GroupMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.common.model.pojo.GroupPanel;
import com.linkhub.common.model.pojo.GroupRole;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IGroupRoleService;
import com.linkhub.portal.service.IGroupService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 群组 info 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-12
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements IGroupService {
    @Resource
    private GroupMemberMapper groupMemberMapper;
    @Resource
    private GroupPanelMapper groupPanelMapper;
    @Resource
    private IGroupRoleService groupRoleService;

    @Override
    public List<GroupMember> selectMemberList(String groupId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        return groupMemberMapper.selectList(wrapper);
    }

    @Override
    public GroupMember checkUserInGroup(String groupId, String userId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId);
        wrapper.eq(GroupMember::getUserId, userId);
        return groupMemberMapper.selectOne(wrapper);
    }

    @Override
    public GroupMember checkMeInGroup(String groupId) {
        String userId = SecurityUtils.getLoginUserId();
        return checkUserInGroup(groupId, userId);
    }

    @Override
    public boolean checkUserIsOwner(String groupId, String userId) {
        Group group = baseMapper.selectById(groupId);
        return userId.equals(group.getOwner());
    }

    @Override
    public boolean checkUserPermission(String userId, String groupId, GroupPermissionEnum groupPermission) {
        // 获取用户的所有权限
        List<String> userAllPermissions = groupRoleService.getUserAllPermissions(groupId, userId);
        return userAllPermissions.contains(groupPermission.getMessage());
    }

    @Override
    public List<GroupVo> getUserGroups(String userId) {
        List<GroupVo> groupVos = new ArrayList<>();

        List<GroupMember> groupMembers = groupMemberMapper.selectList(new LambdaQueryWrapper<GroupMember>()
                .select(GroupMember::getGroupId)
                .eq(GroupMember::getUserId, userId));
        if (ObjectUtils.isEmpty(groupMembers)) {
            // 空集合
            return groupVos;
        }
        // 获取 groupIds
        List<String> groupIds = groupMembers.stream().map(GroupMember::getGroupId).collect(Collectors.toList());
        // 获取 Group
        List<Group> groups = this.listByIds(groupIds);
        // 获取 member
        List<GroupMember> members = groupMemberMapper.selectList(new LambdaQueryWrapper<GroupMember>()
                .in(GroupMember::getGroupId, groupIds));
        Map<String, List<GroupMember>> groupIdMembersMap = members.stream()
                .collect(Collectors.groupingBy(GroupMember::getGroupId));
        // 获取 panels
        List<GroupPanel> panels = groupPanelMapper.selectList(new LambdaQueryWrapper<GroupPanel>()
                .in(GroupPanel::getGroupId, groupIds));
        Map<String, List<GroupPanel>> groupIdPanelsMap = panels.stream()
                .collect(Collectors.groupingBy(GroupPanel::getGroupId));
        // 获取 roles
        List<GroupRole> roles = groupRoleService.list(new LambdaQueryWrapper<GroupRole>()
                .in(GroupRole::getGroupId, groupIds));
        Map<String, List<GroupRole>> groupIdRolesMap = roles.stream()
                .collect(Collectors.groupingBy(GroupRole::getGroupId));
        // 组装

        groups.forEach(group -> {
            String groupId = group.getId();
            GroupVo groupVo = new GroupVo();
            BeanUtils.copyProperties(group, groupVo);
            groupVo.setMembers(groupIdMembersMap.get(groupId));
            groupVo.setPanels(groupIdPanelsMap.get(groupId));
            groupVo.setRoles(groupIdRolesMap.get(groupId));
            groupVos.add(groupVo);
        });
        return groupVos;
    }

    @Override
    public Group getGroupBasicInfo(String groupId) {
        return baseMapper.selectById(groupId);
    }

    @Override
    public void updateGroupField(UpdateGroupFieldDto updateGroupFieldDto) {
        String groupId = updateGroupFieldDto.getGroupId();
        String fieldName = updateGroupFieldDto.getFieldName();
        String fieldValue = updateGroupFieldDto.getFieldValue();
        String userId = SecurityUtils.getLoginUserId();

        // 检查字段是否允许修改
        String[] allowedFields = {"name", "avatar", "description", "panels", "roles", "fallbackPermissions"};
        List<String> list = Arrays.stream(allowedFields).filter(item -> item.equals(fieldName)).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(list)) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR, "该字段不可被修改");
        }

        Group group = baseMapper.selectById(groupId);
        if (ObjectUtils.isEmpty(group)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        // 检查权限
        List<String> userAllPermissions = groupRoleService.getUserAllPermissions(groupId, userId);
        boolean canManageRole = userAllPermissions.contains(GroupPermissionEnum.MANAGE_ROLES.getMessage());

        if ("fallbackPermissions".equals(fieldName)) {
            if (!canManageRole) {
                throw new GlobalException(ErrorCode.NO_AUTH_ERROR, "权限不足,无法更改");
            }
        } else {
            if (!userId.equals(group.getOwner())) {
                throw new GlobalException(ErrorCode.NO_AUTH_ERROR, "权限不足,无法更改");
            }
        }

        UpdateWrapper<Group> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", groupId);
        wrapper.set(fieldName, fieldValue);
        this.update(wrapper);
    }

    @Override
    public void updateGroupConfig(UpdateGroupConfigDto updateGroupConfigDto) {
        String groupId = updateGroupConfigDto.getGroupId();
        String configName = updateGroupConfigDto.getConfigName();
        String configValue = updateGroupConfigDto.getConfigValue();
        String userId = SecurityUtils.getLoginUserId();

        Group group = baseMapper.selectById(groupId);
        if (ObjectUtils.isEmpty(group)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        boolean permission = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_ROLES);
        if (!permission) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }

        // todo: 待改造
        if ("hideMemberInfo".equals(configName)) {
            //config.setHideMemberInfo(configValue);
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GroupVo createGroup(CreateGroupDto createGroupDto) {
        String name = createGroupDto.getName();
        List<Panel> panels = createGroupDto.getPanels();
        String userId = SecurityUtils.getLoginUserId();

        Group group = new Group();
        group.setName(name);
        group.setOwner(userId);
        baseMapper.insert(group);
        // 插入 owner 为 member

        // 处理 panel 数据(parentId)
        //panels.stream().filter()
        // 加入 socket 房间

        return null;
    }

    @Override
    public GroupVo createGroupInvite(String userId, GroupInviteRequest groupInviteRequest) {

        return null;
    }
}
