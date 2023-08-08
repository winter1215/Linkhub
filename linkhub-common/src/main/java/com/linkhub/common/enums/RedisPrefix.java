package com.linkhub.common.enums;

/**
 * @author winter
 * @create 2022-11-22 下午5:19
 */
public interface RedisPrefix {
    /**
     * 验证码前缀
     */
    String PREFIX_VERIFY_CODE = "verify:code:%s";
    String EVENT_QUEUE = "event_queue";
    /**
     * 验证码可用时间
     */
    Integer CODE_AVAILABLE_TIME = 30;
}