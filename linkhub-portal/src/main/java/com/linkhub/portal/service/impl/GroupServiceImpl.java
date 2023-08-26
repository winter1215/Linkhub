package com.linkhub.portal.service.impl;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.linkhub.common.enums.*;
import com.linkhub.common.model.dto.group.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.mapper.GroupMemberMapper;
import com.linkhub.common.mapper.GroupPanelMapper;
import com.linkhub.common.model.pojo.Group;
import com.linkhub.common.mapper.GroupMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.common.model.pojo.GroupPanel;
import com.linkhub.common.model.pojo.GroupRole;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.security.SecurityUtils;
import com.linkhub.portal.service.IGroupMemberService;
import com.linkhub.portal.service.IGroupPanelService;
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
    private IGroupMemberService groupMemberService;
    @Resource
    private GroupPanelMapper groupPanelMapper;
    @Resource
    private IGroupPanelService groupPanelService;
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
    public boolean checkUserPermission(String userId, String groupId, GroupPermissionEnum groupPermission, GroupVo groupVo) {
        if (ObjectUtils.isEmpty(groupVo)) {
            // 获取用户的所有权限
            List<String> userAllPermissions = groupRoleService.getUserAllPermissions(groupId, userId);
            return userAllPermissions.contains(groupPermission.getMessage());
        } else {
            // 群主拥有所有权限
            if (userId.equals(groupVo.getOwner())) {
                return true;
            }
            List<GroupMember> members = groupVo.getMembers();
            List<GroupRole> rolesList = groupVo.getRoles();
            // 个人所有的权限列表
            List<String> permissions = groupVo.getFallbackPermission();
            GroupMember member = members
                    .stream()
                    .filter(item -> userId.equals(item.getUserId()))
                    .findFirst()
                    // 群组不存在当前用户则抛出异常
                    .orElseThrow(() -> new GlobalException(ErrorCode.NO_AUTH_ERROR));
            // 存在 member, 收集自身 roles 的 permission
            member.getRoles().forEach(roleId -> {
                rolesList.stream().filter(item -> roleId.equals(item.getId()))
                        .findFirst()
                        .ifPresent(groupRole -> permissions.addAll(groupRole.getPermissions()));
            });

            return permissions.contains(groupPermission.getMessage());
        }
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

        boolean permission = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_ROLES, null);
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
        List<GroupPanel> panels = createGroupDto.getPanels();
        String userId = SecurityUtils.getLoginUserId();

        // 插入 group
        Group group = new Group();
        group.setName(name);
        group.setOwner(userId);
        baseMapper.insert(group);
        String groupId = group.getId();

        // 插入 owner 为 member
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        groupMemberMapper.insert(member);

        // 处理 panel 数据(parentId)
        // 分出 parent 与 notParent, 将 parent 插入数据库,获取回填 id, 再分别设置到 notParent 对应的 parentId 上面
        List<GroupPanel> parent = new ArrayList<>();
        List<GroupPanel> notParent = new ArrayList<>();
        panels.forEach(panel -> {
            if (panel.getType().equals(GroupPanelTypeREnum.GROUP.getCode())) {
                parent.add(panel);
            } else {
                notParent.add(panel);
            }
        });
        Map<String, GroupPanel> originIdParentMap = parent.stream().collect(Collectors.toMap(GroupPanel::getId, panel -> panel));
        // 插入 parent,获取回填的 Id
        panels.forEach(item -> {
            item.setGroupId(groupId);
            item.setId(null);
        });
        groupPanelService.saveBatch(parent);

        notParent.forEach(panel -> {
            // 这个节点有父亲,设置相应的parent id
            String parentId = panel.getParentId();
            if (ObjectUtils.isNotEmpty(parentId)) {
                GroupPanel parentPanel = originIdParentMap.get(parentId);
                if (parentPanel != null) {
                    panel.setParentId(parentPanel.getId());
                }
            }
        });
        groupPanelService.saveBatch(notParent);
        // 加入 socket 房间
        Set<String> roomIdsWithTextPanel = notParent.stream().map(GroupPanel::getId).collect(Collectors.toSet());
        // todo: featurePanelId
        roomIdsWithTextPanel.add(groupId);
        IMUtil.joinRoom(roomIdsWithTextPanel, userId);

        return null;
    }

    @Override
    public GroupVo createGroupInvite(String userId, GroupInviteRequest groupInviteRequest) {

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quitGroup(String groupId) {
        String userId = SecurityUtils.getLoginUserId();

        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "没找到群组");
        }
        boolean match = groupVo.getMembers().stream().allMatch(item -> item.getUserId().equals(userId));
        if (match) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "用户不在该群组");
        }
        // 群主
        if (userId.equals(groupVo.getOwner())) {
            // 删除群组相关信息
            baseMapper.deleteById(groupId);
            groupMemberMapper.delete(new LambdaQueryWrapper<GroupMember>()
                    .eq(GroupMember::getGroupId, groupId));
            groupRoleService.remove(new LambdaQueryWrapper<GroupRole>()
                    .eq(GroupRole::getGroupId, groupId));
            groupPanelService.remove(new LambdaQueryWrapper<GroupPanel>()
                    .eq(GroupPanel::getGroupId, groupId));

            // 通知群友被解散
            String json = JSONUtil.createObj().set("groupId", groupId).toString();
            // 广播给群组内的所有用户删除掉群组
            IMUtil.notify(new String[]{groupId}, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_REMOVE, json);
            // 离开房间
            HashSet<String> roomIds = new HashSet<>();
            roomIds.add(groupId);
            IMUtil.leaveRoom(roomIds, userId);
        } else {
            // 普通成员, 删除成员
            groupMemberMapper.delete(new LambdaQueryWrapper<GroupMember>()
                    .eq(GroupMember::getGroupId, groupId)
                    .eq(GroupMember::getUserId, userId));
            groupVo.getMembers().removeIf(item -> item.getUserId().equals(userId));

            // 用户离开房间要做的通知
            memberLeaveRoom(groupVo, userId);
        }
    }

    @Override
    public void handleGroupMemberRoles(HandleMemberRolesDto appendMemberRolesDto, boolean isAppend) {
        String userId = SecurityUtils.getLoginUserId();
        String groupId = appendMemberRolesDto.getGroupId();
        List<String> memberIds = appendMemberRolesDto.getMemberIds();
        List<String> roles = appendMemberRolesDto.getRoles();

        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        List<GroupRole> rolesList = groupVo.getRoles();
        List<String> permissions = groupVo.getFallbackPermission();
        List<GroupMember> members = groupVo.getMembers();

        // 检查权限
        boolean match = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_ROLES, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }

        // 拥有权限, 修改 roles
        List<GroupMember> updateMembers = new ArrayList<>();
        memberIds.forEach(memberId -> {
            // 找到对应的 member, 修改其 roles
            members.stream().filter(item -> memberId.equals(item.getUserId()))
                    .findFirst()
                    .ifPresent(item -> {
                        // 去重(增添或是移除)
                        if (isAppend) {
                            item.getRoles().addAll(roles);
                        } else {
                            item.getRoles().removeAll(roles);
                        }
                        item.setRoles(item.getRoles().stream().distinct().collect(Collectors.toList()));
                        updateMembers.add(item);
                    });
        });
        groupMemberService.updateBatchById(updateMembers);

        // 发送更新通知
        String json = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{ groupId }, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, json);
    }

    @Override
    public boolean createGroupPanel(CreateGroupPanelDto groupPanelDto) {
        String groupId = groupPanelDto.getGroupId();
        String loginUserId = SecurityUtils.getLoginUserId();

        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        boolean match = checkUserPermission(loginUserId, groupId, GroupPermissionEnum.MANAGE_PANEL, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }

        // 插入
        GroupPanel panel = new GroupPanel();
        BeanUtils.copyProperties(groupPanelDto, panel);
        boolean flag = groupPanelService.save(panel);
        if (!flag) {
            return false;
        }

        if (GroupPanelTypeREnum.TEXT.getCode().equals(groupPanelDto.getType())) {
            // 将全部 member 加入到当前 panel
            List<GroupMember> members = groupVo.getMembers();
            members.forEach(member -> {
                String userId = member.getUserId();
                HashSet<String> roomIdSet = new HashSet<>();
                roomIdSet.add(panel.getId());
                IMUtil.joinRoom(roomIdSet, userId);
            });
        }
        // 发送更新通知
        String json = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{ groupId }, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, json);
        return true;
    }

    @Override
    public GroupVo modifyGroupPanel(ModGroupPanelDto groupPanelDto) {
        String userId = SecurityUtils.getLoginUserId();
        String groupId = groupPanelDto.getGroupId();
        String panelId = groupPanelDto.getPanelId();

        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        boolean match = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_PANEL, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }
        // 修改的 panel 是否存在, 权限校验
        GroupPanel panel = groupVo.getPanels()
                .stream()
                .filter(item -> panelId.equals(item.getId()))
                .findFirst().orElse(null);
        if (ObjectUtils.isEmpty(panel) || !groupId.equals(panel.getGroupId())) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }
        BeanUtils.copyProperties(groupPanelDto, panel);
        // todo: 这里 vo 的 panel 修改了吗
        boolean flag = groupPanelService.updateById(panel);
        if (!flag) {
            return null;
        }
        // 发送更新通知
        String json = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{ groupId }, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, json);
        return groupVo;
    }

    @Override
    public GroupVo deleteGroupPanel(DeleteGroupPanelDto deleteGroupPanelDto) {
        String groupId = deleteGroupPanelDto.getGroupId();
        String panelId = deleteGroupPanelDto.getPanelId();
        String userId = SecurityUtils.getLoginUserId();


        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        boolean match = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_PANEL, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }
        // 修改的 panel 是否存在, 权限校验
        GroupPanel panel = groupVo.getPanels()
                .stream()
                .filter(item -> panelId.equals(item.getId()))
                .findFirst().orElse(null);
        if (ObjectUtils.isEmpty(panel) || !groupId.equals(panel.getGroupId())) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR, "找不到该面板");
        }

        boolean remove = groupPanelService.removeById(panelId);
        if (!remove) {
            return null;
        }
        groupVo.getRoles().removeIf(item -> panelId.equals(item.getId()));
        // 发送更新通知
        String json = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{ groupId }, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, json);
        return groupVo;
    }

    @Override
    public String getGroupLobbyConverseId(String groupId) {
        Group group = baseMapper.selectById(groupId);
        if (ObjectUtils.isEmpty(group)) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR);
        }
        // 获取 text 类型的 panels
        LambdaQueryWrapper<GroupPanel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupPanel::getGroupId, groupId);
        wrapper.eq(GroupPanel::getType, GroupPanelTypeREnum.TEXT.getCode());
        List<GroupPanel> panels = groupPanelService.list(wrapper);
        if (ObjectUtils.isEmpty(panels)) {
            return null;
        }
        GroupPanel panel = panels.get(0);
        return panel.getId();
    }

    @Override
    public GroupVo createGroupRole(CreateGroupRoleDto createGroupRoleDto) {
        String groupId = createGroupRoleDto.getGroupId();
        String name = createGroupRoleDto.getName();
        List<String> permissions = createGroupRoleDto.getPermissions();
        String userId = SecurityUtils.getLoginUserId();
        // 校验权限
        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        boolean match = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_ROLES, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }

        GroupRole groupRole = new GroupRole();
        groupRole.setName(name);
        groupRole.setPermissions(permissions);
        boolean save = groupRoleService.save(groupRole);
        if (!save) {
            return null;
        }

        groupVo.getRoles().add(groupRole);
        // 发送更新通知
        String json = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{ groupId }, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, json);
        return groupVo;
    }

    @Override
    public GroupVo deleteGroupRole(DeleteGroupRoleDto deleteGroupRoleDto) {
        String groupId = deleteGroupRoleDto.getGroupId();
        String roleId = deleteGroupRoleDto.getRoleId();
        String userId = SecurityUtils.getLoginUserId();
        // 校验权限
        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        boolean match = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_ROLES, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }
        GroupRole groupRole = groupVo.getRoles().stream()
                .filter(item -> roleId.equals(item.getId()))
                .findFirst()
                .orElse(null);

        if (ObjectUtils.isEmpty(groupRole)) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR, "该权限组不存在");
        }
        boolean remove = groupRoleService.removeById(roleId);
        if (!remove) {
            return null;
        }

        // 发送更新通知
        groupVo.getRoles().removeIf(item -> roleId.equals(item.getId()));
        String json = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{ groupId }, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, json);
        return groupVo;
    }

    @Override
    public GroupVo updateGroupRoleName(UpdateGroupRoleDto updateGroupRoleDto) {
        String groupId = updateGroupRoleDto.getGroupId();
        String roleId = updateGroupRoleDto.getRoleId();
        String name = updateGroupRoleDto.getName();
        List<String> permissions = updateGroupRoleDto.getPermissions();
        String userId = SecurityUtils.getLoginUserId();

        // 校验权限
        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        boolean match = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_ROLES, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }

        GroupRole groupRole = groupVo.getRoles().stream()
                .filter(item -> roleId.equals(item.getId()))
                .findFirst()
                .orElse(null);

        if (ObjectUtils.isEmpty(groupRole)) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR, "该权限组不存在");
        }
        if (name != null) {
            groupRole.setName(name);
        }
        if (permissions != null) {
            groupRole.setPermissions(permissions);
        }
        boolean update = groupRoleService.updateById(groupRole);
        String json = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{ groupId }, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, json);
        return groupVo;
    }

    @Override
    public GroupVo muteGroupMember(MuteGroupMemberDto muteGroupMemberDto) {
        String groupId = muteGroupMemberDto.getGroupId();
        String memberId = muteGroupMemberDto.getMemberId();
        Integer muteMs = muteGroupMemberDto.getMuteMs();
        // 负数代表接触禁言
        boolean isMute = muteMs > 0;
        String userId = SecurityUtils.getLoginUserId();

        // 校验权限
        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        boolean match = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_USER, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }
        GroupMember member = groupVo.getMembers().stream()
                .filter(item -> userId.equals(item.getUserId()))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.OPERATION_ERROR, "成员未中找到"));

        LocalDateTime muteUtil = isMute ? LocalDateTime.now().plus(muteMs, ChronoUnit.MILLIS) : null;
        member.setMuteUtil(muteUtil);

        boolean update = groupMemberService.update(new LambdaUpdateWrapper<GroupMember>()
                .eq(GroupMember::getUserId, userId)
                .set(GroupMember::getMuteUtil, muteUtil));
        if (!update) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR);
        }
        String json = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{ groupId }, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, json);

        // todo: 发送系统消息(禁言或者解禁)
        return groupVo;
    }

    @Override
    public GroupVo deleteGroupMember(DeleteGroupMemberDto deleteGroupMemberDto) {
        String groupId = deleteGroupMemberDto.getGroupId();
        String memberId = deleteGroupMemberDto.getMemberId();
        String userId = SecurityUtils.getLoginUserId();

        // 校验权限
        GroupVo groupVo = baseMapper.selectGroupVoById(groupId);
        if (ObjectUtils.isEmpty(groupVo)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        boolean match = checkUserPermission(userId, groupId, GroupPermissionEnum.MANAGE_USER, groupVo);
        if (!match) {
            throw new GlobalException(ErrorCode.NO_AUTH_ERROR);
        }
        GroupMember member = groupVo.getMembers().stream()
                .filter(item -> userId.equals(item.getUserId()))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.OPERATION_ERROR, "成员未中找到"));
        // 校验是否踢出核心成员或自己
        if (userId.equals(memberId)) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR, "不允许踢出自己");
        }
        if (memberId.equals(groupVo.getOwner())) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR, "不允许提出 OP");
        }
        // 删除 DB
        boolean remove = groupMemberService.remove(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getUserId, memberId)
                .eq(GroupMember::getGroupId, groupId));

        if (!remove) {
            throw new GlobalException(ErrorCode.OPERATION_ERROR);
        }
        groupVo.getMembers().removeIf(item -> memberId.equals(item.getUserId()));
        // 用户离开房间要做的通知
        memberLeaveRoom(groupVo, memberId);
        return groupVo;
    }

    /**
    * 用户离开房间要做的通知
    */
    private void memberLeaveRoom(GroupVo groupVo, String memberId) {
        String groupId = groupVo.getId();
        Set<String> roomIds = groupVo.getPanels()
                .stream()
                .map(GroupPanel::getId).collect(Collectors.toSet());
        roomIds.add(groupId);
        IMUtil.leaveRoom(roomIds, memberId);
        // 单播给用户, remove 掉群组
        String json = JSONUtil.createObj().set("groupId", groupId).toString();
        IMUtil.notify(new String[]{memberId}, IMNotifyTypeEnum.UNICAST, ClientEventEnum.GROUP_REMOVE, json);
        // 通知群组内用户更新群组信息
        String groupVoJson = JSONUtil.toJsonStr(groupVo);
        IMUtil.notify(new String[]{groupId}, IMNotifyTypeEnum.ROOM_CAST, ClientEventEnum.GROUP_UPDATE_INFO, groupVoJson);

    }


}
