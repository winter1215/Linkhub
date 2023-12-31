package com.linkhub.portal.service;

import com.linkhub.common.model.dto.converse.AppendDMConverseMemberRequest;
import com.linkhub.common.model.pojo.Converse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.linkhub.common.model.pojo.User;
import com.linkhub.common.model.vo.ConverseVo;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 会话表 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-13
 */
public interface IConverseService extends IService<Converse> {

    /**
    * 获取用户的 converseIds (dmConverseIds, groupIds, paneIds)
    */
    Set<String> getUserAllConverseIds(String userId);

    ConverseVo creatDMconverse(User user, String[] memberIds);

    ConverseVo appendDMConverseMembers(User user, AppendDMConverseMemberRequest appendMemberRequest);

    ConverseVo findConverseInfo(String userId, String converseId);

    Set<String> getDMConverseIds(String userId);
}
