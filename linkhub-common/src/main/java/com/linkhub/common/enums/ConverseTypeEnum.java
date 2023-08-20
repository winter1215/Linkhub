package com.linkhub.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-08-14 上午11:36
 */
public enum ConverseTypeEnum {
    /**
    * 新增消息
    */
    CONVERSE_TYPE_DM(0, "DM"),
    CONVERSE_TYPE_MULTI(1, "Multi");

    private final Integer code;
    /**
     * 信息
     */
    private final String message;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(ConverseTypeEnum::getCode).collect(Collectors.toList());
    }

    public static ConverseTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ConverseTypeEnum groupPermissionEnum : values()) {
            if (groupPermissionEnum.code.equals(value)) {
                return groupPermissionEnum;
            }
        }
        return null;
    }

    ConverseTypeEnum(Integer code, String message) {
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
