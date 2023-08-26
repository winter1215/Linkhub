package com.linkhub.portal.service;

import com.linkhub.common.enums.GroupPermissionEnum;
import com.linkhub.common.model.dto.group.*;
import com.linkhub.common.model.pojo.Group;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.vo.GroupVo;

import java.util.List;

/**
 * <p>
 * 群组 info 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-12
 */
public interface IGroupService extends IService<Group> {
    /**
    * 查询群组成员
    */
    List<GroupMember> selectMemberList(String groupId);


    /**
    * 检查某个用户在群某组中
    */
    GroupMember checkUserInGroup(String groupId, String userId);

    /**
    * 检查登录用户是否在群组
    */
    GroupMember checkMeInGroup(String groupId);

    /**
    * 检查用户是否为群主
    */
    boolean checkUserIsOwner(String groupId, String userId);

    /**
    * 检查用户在指定群组是否指定权限(如果传入 groupVo, 则不查数据库, 否则传入 null)
    */
    boolean checkUserPermission(String userId, String groupId, GroupPermissionEnum deleteMessage, GroupVo groupVo);

    List<GroupVo> getUserGroups(String userId);

    Group getGroupBasicInfo(String groupId);

    // todo: 这个和下面两个接口待优化: 不是fieldName 和 value, 而是一个对象 (暂时无法修改所有人权限)
    void updateGroupField(UpdateGroupFieldDto updateGroupFieldDto);

    void updateGroupConfig(UpdateGroupConfigDto updateGroupConfigDto);

    GroupVo createGroup(CreateGroupDto createGroupDto);

    void quitGroup(String groupId);

    void handleGroupMemberRoles(HandleMemberRolesDto appendMemberRolesDto, boolean isAppend);

    boolean createGroupPanel(CreateGroupPanelDto createGroupPanelDto);

    GroupVo modifyGroupPanel(ModGroupPanelDto modGroupPanelDto);

    GroupVo deleteGroupPanel(DeleteGroupPanelDto deleteGroupPanelDto);
    /**
    * 获取群组大厅 id
    */
    String getGroupLobbyConverseId(String groupId);

    GroupVo createGroupRole(CreateGroupRoleDto createGroupRoleDto);

    GroupVo deleteGroupRole(DeleteGroupRoleDto deleteGroupRoleDto);

    GroupVo updateGroupRoleName(UpdateGroupRoleDto updateGroupRoleDto);

    GroupVo muteGroupMember(MuteGroupMemberDto muteGroupMemberDto);

    GroupVo deleteGroupMember(DeleteGroupMemberDto deleteGroupMemberDto);
}
