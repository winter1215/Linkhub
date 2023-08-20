package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.enums.ErrorCode;
import com.linkhub.common.enums.GroupPermissionEnum;
import com.linkhub.common.mapper.GroupMapper;
import com.linkhub.common.mapper.GroupMemberMapper;
import com.linkhub.common.model.pojo.Group;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.pojo.GroupRole;
import com.linkhub.common.mapper.GroupRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.portal.service.IGroupRoleService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 群组的权限组 服务实现类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-17
 */
@Service
public class GroupRoleServiceImpl extends ServiceImpl<GroupRoleMapper, GroupRole> implements IGroupRoleService {
    @Resource
    private GroupMemberMapper groupMemberMapper;
    @Resource
    private GroupMapper groupMapper;

    private final List<String> allPermissions = GroupPermissionEnum.getMessages();

    @Override
    public List<String> getUserAllPermissions(String groupId, String userId) {
        Group group = groupMapper.selectOne(new LambdaQueryWrapper<Group>()
                .eq(Group::getId, groupId)
                .select(Group::getFallbackPermission)
                .select(Group::getOwner));

        if (ObjectUtils.isEmpty(group)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        // 拿到群组的所有人权限
        List<String> fallbackPermission = group.getFallbackPermission();

        // 该用户是群主, 拥有所有权限
        if (userId.equals(group.getOwner())) {
            return allPermissions;
        }

        // 查询成员
        GroupMember member = groupMemberMapper.selectOne(new LambdaQueryWrapper<GroupMember>()
                .select(GroupMember::getRoles)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUserId, userId));
        if (ObjectUtils.isEmpty(member)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        List<String> roles = member.getRoles();

        // 查询分配给用户的权限列表
        List<GroupRole> groupRoles = this.listByIds(roles);
        // flatMap 将二维的 list 扁平化
        List<String> userPermissions = groupRoles.stream()
                .flatMap(groupRole -> groupRole.getPermissions().stream())
                .collect(Collectors.toList());
        userPermissions.addAll(fallbackPermission);

        return userPermissions.stream().distinct().collect(Collectors.toList());
    }
}
