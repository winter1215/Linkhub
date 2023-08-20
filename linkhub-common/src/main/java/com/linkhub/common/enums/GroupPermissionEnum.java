package com.linkhub.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-08-16 下午10:35
 */
public enum GroupPermissionEnum {
    OWNER(0, "__group_owner__"),
    MESSAGE(1, "core.message"),
    INVITE(2, "core.invite"),
    UN_LIMITED_INVITE(3, "core.unlimitedInvite"),
    GROUP_DETAIL(4, "core.groupDetail"),
    GROUP_CONFIG(5, "core.groupConfig"),
    MANAGE_USER(6, "core.manageUser"),
    MANAGE_PANEL(7, "core.managePanel"),
    MANAGE_INVITE(8, "core.manageInvite"),
    MANAGE_ROLES(9, "core.manageRoles"),
    DELETE_MESSAGE(10, "core.deleteMessage"),
    EDIT_INVITE(11, "core.editInvite");

    private final Integer code;
    /**
     * 信息
     */
    private final String message;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(GroupPermissionEnum::getCode).collect(Collectors.toList());
    }
    public static List<String> getMessages(){
        return Arrays.stream(values()).map(GroupPermissionEnum::getMessage).collect(Collectors.toList());
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
