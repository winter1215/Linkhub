package com.linkhub.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linkhub.common.enums.GroupPermissionEnum;
import com.linkhub.common.mapper.GroupMemberMapper;
import com.linkhub.common.model.dto.group.GroupInviteRequest;
import com.linkhub.common.model.pojo.Group;
import com.linkhub.common.mapper.GroupMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.portal.security.LinkhubUserDetails;
import com.linkhub.portal.service.IGroupService;
import com.linkhub.security.util.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
        LinkhubUserDetails user = SecurityUtils.getLoginObj();
        String userId = user.getUser().getId();
        return checkUserInGroup(groupId, userId);
    }

    @Override
    public boolean checkUserIsOwner() {
        Group group = baseMapper.selectById("1");
        System.out.println(group);
        // todo: 待补充
        return true;
    }

    @Override
    public boolean checkUserPermission(String userId, String groupId, GroupPermissionEnum groupPermission) {
        // todo: 待补充
        return true;
    }

    @Override
    public GroupVo createGroupInvite(String userId, GroupInviteRequest groupInviteRequest) {

        return null;
    }
}
