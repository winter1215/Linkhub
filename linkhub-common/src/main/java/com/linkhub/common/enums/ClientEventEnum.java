package com.linkhub.common.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-08-14 上午11:36
 */
public enum ClientEventEnum {
    /**
    * 新增消息
    */
    MESSAGE_ADD(0, "notify:chat.message.add"),
    MESSAGE_UPDATE(1, "notify:chat.message.update"),
    MESSAGE_DELETE(2, "notify:chat.message.delete"),
    CONVERSE_UPDATEDM(3, "notify:chat.converse.updateDMConverse"),
    GROUP_REMOVE(3, "notify:chat.group.remove"),
    GROUP_UPDATE_INFO(4, "notify:chat.group.updateInfo"),
    INBOX_APPEND(5,"notify:chat.inbox.append"),
    INBOX_UPDATED(6, "notify:chat.inbox.updated"),
    ADD_FRIEND_REQUEST(7, "notify:friend.request.add");

    private final Integer code;

    /**
     * 信息
     */
    private final String message;

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(ClientEventEnum::getCode).collect(Collectors.toList());
    }

    public static ClientEventEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ClientEventEnum groupPermissionEnum : values()) {
            if (groupPermissionEnum.code.equals(value)) {
                return groupPermissionEnum;
            }
        }
        return null;
    }

    ClientEventEnum(Integer code, String message) {
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
