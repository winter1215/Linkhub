package com.linkhub.portal.service;

import com.linkhub.common.model.dto.message.SendMsgDto;
import com.linkhub.common.model.pojo.Inbox;
import com.baomidou.mybatisplus.extension.service.IService;

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
    void insertMsgInbox(SendMsgDto sendMsgDto);
}
