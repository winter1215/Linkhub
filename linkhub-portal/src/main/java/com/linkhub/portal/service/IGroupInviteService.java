package com.linkhub.portal.service;

import com.linkhub.common.model.dto.group.GroupEditRequest;
import com.linkhub.common.model.dto.group.GroupInviteApplyRequest;
import com.linkhub.common.model.dto.group.GroupInviteRequest;
import com.linkhub.common.model.pojo.GroupInvite;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linkhub.common.model.pojo.User;

import java.util.List;

/**
 * <p>
 * 群组邀请码 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-20
 */
public interface IGroupInviteService extends IService<GroupInvite> {

    /**
     * 默认7天过期，或者永久
     * @param userId
     * @param groupInviteRequest
     * @return
     */
    GroupInvite createGroupInvite(String userId, GroupInviteRequest groupInviteRequest);

    int editGroupInvite(String userId, GroupEditRequest groupEditRequest);

    List<GroupInvite> getAllGroupInviteCode(String userId, String groupId);

    GroupInvite findGroupInviteByCode(String code);

    void applyInvite(User user, GroupInviteApplyRequest groupInviteApplyRequest);
}
