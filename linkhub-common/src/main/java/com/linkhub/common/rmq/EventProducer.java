package com.linkhub.common.rmq;

import com.linkhub.common.config.redis.RedisCache;
import com.linkhub.common.enums.RedisPrefix;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 消息的生产者，负责将消息推进 redis 队列中
 * @author winter
 * @create 2023-01-14 上午10:45
 */
@Component
@AllArgsConstructor
public class EventProducer {
    private RedisCache redisCache;

    /**
     * 触发某个主题的事件
     * @param model: 事件模型
     * @return: boolean
     * @author: winter
     * @date: 2023/1/15 上午12:27
     * @description:
     */
    public boolean fireEvent(EventModel model) {
        try {
            redisCache.lPushList(RedisPrefix.EVENT_QUEUE, model);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
