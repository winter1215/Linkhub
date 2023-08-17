package com.linkhub.portal.service;

import com.linkhub.common.enums.GroupPermissionEnum;
import com.linkhub.common.model.pojo.Group;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linkhub.common.model.pojo.GroupMember;

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

    boolean checkUserIsOwner();

    /**
    * 检查用户在指定群组是否指定权限
    */
    boolean checkUserPermission(String userId, String groupId, GroupPermissionEnum deleteMessage);
}
