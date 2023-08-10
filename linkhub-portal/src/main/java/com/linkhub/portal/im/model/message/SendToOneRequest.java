package com.linkhub.portal.im.model.message;

/**
 * 发送给指定人的私聊消息的 Message
 */
public class SendToOneRequest implements Message {

    public static final String TYPE = "SEND_TO_ONE_REQUEST";

    /**
     * 发送给的用户
     */
    private Long toUser;
    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 内容
     */
    private String content;

    public Long getToUser() {
        return toUser;
    }

    public SendToOneRequest setToUser(Long toUser) {
        this.toUser = toUser;
        return this;
    }

    public String getMsgId() {
        return msgId;
    }

    public SendToOneRequest setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SendToOneRequest setContent(String content) {
        this.content = content;
        return this;
    }
}
