package com.linkhub.common.rmq.handler;

import com.linkhub.common.annotation.RmqHandler;
import com.linkhub.common.rmq.EventHandler;
import com.linkhub.common.rmq.EventModel;
import com.linkhub.common.rmq.EventType;


/**
 * @author winter
 * @create 2023-01-16 下午7:58
 */
@RmqHandler(topic = {EventType.VOTE})
public class CommentHandler implements EventHandler {
    @Override
    public void doHandler(EventModel eventModel) {
        System.out.println("comment handler: " + eventModel);
    }
}
