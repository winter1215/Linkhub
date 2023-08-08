package com.linkhub.common.rmq;

import org.apache.commons.lang3.ObjectUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-01-14 上午10:46
 */
public enum EventType {
    /**
    * 点赞
    */
    VOTE(1),
    /**
    * 评论
    */
    COMMENT(2),
    /**
    * 关注
    */
    FOLLOW(3),
    ;
    private int code;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(item -> item.getCode()).collect(Collectors.toList());
    }

    public static EventType getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (EventType eventType : values()) {
            if (eventType.code == value) {
                return eventType;
            }
        }
        return null;
    }

    EventType(int code) {
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
