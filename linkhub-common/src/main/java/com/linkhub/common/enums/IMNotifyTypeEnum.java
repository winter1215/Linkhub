package com.linkhub.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-08-14 上午12:08
 */
public enum IMNotifyTypeEnum {
    UNICAST(0, "单播"),
    LIST_CAST(1, "列播"),
    /**
    * 组播: 将指定的事件和数据发送给房间内的用户
    */
    ROOM_CAST(2, "组播"),
    BROAD_CAST(3, "广播");

    private final Integer code;

    /**
     * 信息
     */
    private final String message;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(IMNotifyTypeEnum::getCode).collect(Collectors.toList());
    }

    public static IMNotifyTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (IMNotifyTypeEnum groupPermissionEnum : values()) {
            if (groupPermissionEnum.code.equals(value)) {
                return groupPermissionEnum;
            }
        }
        return null;
    }

    IMNotifyTypeEnum(Integer code, String message) {
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
