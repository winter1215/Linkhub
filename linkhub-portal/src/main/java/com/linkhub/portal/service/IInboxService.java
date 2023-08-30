package com.linkhub.portal.service;

import com.linkhub.common.model.dto.inbox.RemoveInboxDto;
import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.Inbox;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 收件箱 服务类
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-14
 */
public interface IInboxService extends IService<Inbox> {

/**
* 将 @ 的消息放入对方的收件箱
*/
    void asyncInsertMsgInbox(SendMsgDto sendMsgDto);
    boolean removeMsgInbox(RemoveInboxDto removeInboxDto);

    /**
    * 获取用户所有收件箱
    */
    List<Inbox> all(String userId);

    /**
    * 已读收件箱
    */
    boolean ack(List<String> inboxIds);
    /**
    * 清除用户所有收件箱
    */
    void clearAll();
}
