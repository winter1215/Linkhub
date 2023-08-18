package com.linkhub.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-08-14 上午11:36
 */
public enum GroupPanelTypeREnum {
    /**
    * 新增消息
    */
    TEXT(0, "text"),
    GROUP(1, "group"),
    PLUGIN(2, "plugin");

    private final Integer code;

    /**
     * 信息
     */
    private final String message;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(GroupPanelTypeREnum::getCode).collect(Collectors.toList());
    }

    public static GroupPanelTypeREnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (GroupPanelTypeREnum groupPermissionEnum : values()) {
            if (groupPermissionEnum.code.equals(value)) {
                return groupPermissionEnum;
            }
        }
        return null;
    }

    GroupPanelTypeREnum(Integer code, String message) {
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
