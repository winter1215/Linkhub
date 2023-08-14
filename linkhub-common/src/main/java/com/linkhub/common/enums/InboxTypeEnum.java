package com.linkhub.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-08-14 上午11:36
 */
public enum InboxTypeEnum {
    /**
    * 个人消息
    */
    DM(0, "个人消息"),
    GROUP_MESSAGE(1, "群组消息");
    private final Integer code;

    /**
     * 信息
     */
    private final String message;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(InboxTypeEnum::getCode).collect(Collectors.toList());
    }

    public static InboxTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (InboxTypeEnum groupPermissionEnum : values()) {
            if (groupPermissionEnum.code.equals(value)) {
                return groupPermissionEnum;
            }
        }
        return null;
    }

    InboxTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
