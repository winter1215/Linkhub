package com.linkhub.portal.service;

import com.linkhub.common.model.pojo.GroupRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 群组的权限组 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-17
 */
public interface IGroupRoleService extends IService<GroupRole> {

    /**
    * 获取用户在某个群组中的所有权限
    */
    List<String> getUserAllPermissions(String groupId, String userId);

}
