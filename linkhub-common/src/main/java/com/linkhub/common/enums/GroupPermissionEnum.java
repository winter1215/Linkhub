package com.linkhub.common.enums;

import io.swagger.models.auth.In;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-08-12 上午10:32
 */
public enum GroupPermissionEnum {
    ;
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String message;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(GroupPermissionEnum::getCode).collect(Collectors.toList());
    }

    public static GroupPermissionEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (GroupPermissionEnum groupPermissionEnum : values()) {
            if (groupPermissionEnum.code.equals(value)) {
                return groupPermissionEnum;
            }
        }
        return null;
    }

    GroupPermissionEnum(Integer code, String message) {
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
