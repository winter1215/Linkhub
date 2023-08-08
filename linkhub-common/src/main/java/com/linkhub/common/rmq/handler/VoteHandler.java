package com.linkhub.common.rmq.handler;

import com.linkhub.common.annotation.RmqHandler;
import com.linkhub.common.rmq.EventHandler;
import com.linkhub.common.rmq.EventModel;
import com.linkhub.common.rmq.EventType;

/**
 * 点赞的事件处理器
 * @author winter
 * @create 2023-01-15 上午12:31
 */

@RmqHandler(topic = {EventType.VOTE, EventType.COMMENT})
public class VoteHandler implements EventHandler {
    @Override
    public void doHandler(EventModel eventModel) {
        System.out.println("vote handler: " + eventModel);
    }
}
