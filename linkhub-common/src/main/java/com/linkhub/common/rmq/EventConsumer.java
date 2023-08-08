package com.linkhub.common.rmq;

import com.linkhub.common.annotation.RmqHandler;
import com.linkhub.common.config.redis.RedisCache;
import com.linkhub.common.enums.RedisPrefix;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author winter
 * @create 2023-01-14 上午10:46
 */
@Slf4j
@Component
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    /**
     * 主题与事件处理器对象的map
     */
    private final Map<EventType, List<EventHandler>> map = new HashMap<>();
    @Autowired
    private RedisCache redisCache;
    private final Thread consumer = new Thread(consumer());
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 注册主题与事件处理器对象的列表
        // key: bean name, value: bean instance
        Map<String, Object> handlers = applicationContext.getBeansWithAnnotation(RmqHandler.class);
        handlers.forEach((name, handler) -> {
            Class<?> clazz = handler.getClass();
            // 校验(只是加了注解，不一定实现了接口，所以上面也没法直接强转）
            if (!(handler instanceof EventHandler)) {
                log.warn("注意到被 @QmqHandler 注解的类：{} 未实现接口 EventHandler, 已被放弃注册", clazz.getCanonicalName());
                return;
            }
            // 获取 topics
            EventType[] topics = clazz.getAnnotation(RmqHandler.class).topic();

            // 将 topic： eventHandler 注册进 map
            if (ObjectUtils.allNotNull(topics)) {
                Arrays.stream(topics).forEach(topic -> {
                    if (!map.containsKey(topic)) {
                        map.put(topic, new ArrayList<>());
                    }
                    map.get(topic).add((EventHandler) handler);
                });
            }
            log.info("事件已被注册 --> {}", name);
        });

        // redis consumer thread start
        consumer.start();
    }

    private Runnable consumer() {
        return () -> {
            // 不断的去 redis 的对列中阻塞的取值
            while (true) {
                EventModel eventModel;
                try {
                    eventModel = redisCache.bRPopList(RedisPrefix.EVENT_QUEUE);
                    EventType topic = eventModel.getTopic();
                    List<EventHandler> handlers = map.get(topic);
                    if (ObjectUtils.isEmpty(handlers)) {
                        log.error("该事件类型未被注册： {}", topic);
                        continue;
                    }
                    // 处理事件
                    handlers.forEach(handler -> {
                        handler.doHandler(eventModel);
                    });
                    log.info("topic: {} 已被处理", topic);
                } catch (Exception e) {
                    log.info("EventConsumer Brpop 超时，正在等待重试");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        };
    }


}
