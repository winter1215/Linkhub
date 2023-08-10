package com.linkhub.portal.im.handler;

import com.linkhub.common.model.pojo.Message;
import com.linkhub.portal.im.model.message.SendResponse;
import com.linkhub.portal.im.model.message.SendToOneRequest;
import com.linkhub.portal.im.model.message.SendToUserRequest;
import com.linkhub.portal.im.util.WebSocketUtil;
import com.linkhub.portal.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class SendToOneHandler implements MessageHandler<SendToOneRequest> {
    private static final int BATCH_SIZE = 10;
    private static final List<Message> MESSAGE_BUFFER = new ArrayList<>();

    @Autowired
    private IMessageService messagesService;

    @Override
    public void execute(WebSocketSession session, SendToOneRequest message) {
        Long toUser = message.getToUser();
        // 好友才能发送消息
        if (!WebSocketUtil.isMyFriend(toUser, session)) {
            SendResponse sendResponse = new SendResponse()
                    .setMsgId(message.getMsgId())
                    .setMessage("发送失败,请先添加对方好友");
                    //.setCode(ImMessageErrorEnum.ERROR.getCode());
            //WebSocketUtil.send(session, SendResponse.TYPE, sendResponse);
            return;
        }
        // 发给自己的
        SendResponse sendResponse = new SendResponse().setMsgId(message.getMsgId()).setCode(0);
        //WebSocketUtil.send(session, SendResponse.TYPE, sendResponse);

        // 创建转发的消息
        String content = message.getContent();
        SendToUserRequest sendToUserRequest = new SendToUserRequest().setMsgId(message.getMsgId())
                .setContent(content);
        // 发送给好友
        //WebSocketUtil.send(toUser, SendToUserRequest.TYPE, sendToUserRequest);

        handleMessage(session, toUser, content);
    }

    private void handleMessage(WebSocketSession session, Long toUser, String content) {
        //if (MESSAGE_BUFFER.size() >= BATCH_SIZE) {
        //    //拷贝
        //    ArrayList<Messages> messages = new ArrayList<>(MESSAGE_BUFFER);
        //    asyncBatchInsertMessages(messages);
        //    MESSAGE_BUFFER.clear();
        //}
        //Messages messages = new Messages();
        //Long loginUserId = (Long) session.getAttributes().get("loginUserId");
        //messages.setSenderId(loginUserId);
        //messages.setReceiverId(toUser);
        //messages.setConversationId(messagesService.genConversationId(loginUserId, toUser));
        //messages.setContent(content);
        //messages.setIsRead(false);
        //messages.setCreateTime(LocalDateTime.now());
        //
        //MESSAGE_BUFFER.add(messages);
    }

    /**
     * 异步将 messages 持久化到数据库
     * @param messages:
     * @return: void
     * @author: winter
     * @date: 2023/5/12 下午7:43
     * @description:
     */
    private void asyncBatchInsertMessages(ArrayList<Message> messages) {
        CompletableFuture.supplyAsync(() -> messagesService.saveBatch(messages))
                .exceptionally(ex -> {
                    log.warn("message 持久化失败");
                    return false;
                })
                .thenAccept(res -> {
                    log.info("消息持久化状态: {}", res);
        });
    }

    /**
     * 获取 message_buffer
     * @return: java.util.List<com.tower.common.model.pojo.Messages>
     * @author: winter
     * @date: 2023/5/18 上午9:47
     * @description:
     */
    public static List<Message> getMessageBuffer() {
        return MESSAGE_BUFFER;
    }


    @Override
    public String getType() {
        return SendToOneRequest.TYPE;
    }

}
