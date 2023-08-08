package com.linkhub.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.linkhub.common.rmq.EventType;

/**
 *
 * @author: winter
 * @date: 2023/1/15 上午12:33
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RmqHandler {
    /**
     * 队列主题
     */
    EventType[] topic() ;
}