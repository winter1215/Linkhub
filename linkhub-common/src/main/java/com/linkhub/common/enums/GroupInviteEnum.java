package com.linkhub.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linkcastling
 * @create 2023-08-18 上午16:17
 */
public enum GroupInviteEnum {
    /**
     * 新增消息
     */
    NORMAL_INVITE(0, "normal"),
    PERMANENT_INVITE(1, "permanent");
    private final Integer code;

    /**
     * 信息
     */
    private final String message;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(GroupInviteEnum::getCode).collect(Collectors.toList());
    }

    public static GroupInviteEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (GroupInviteEnum groupPermissionEnum : values()) {
            if (groupPermissionEnum.code.equals(value)) {
                return groupPermissionEnum;
            }
        }
        return null;
    }

    GroupInviteEnum(Integer code, String message) {
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
