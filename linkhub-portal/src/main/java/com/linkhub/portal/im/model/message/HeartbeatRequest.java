package com.linkhub.portal.im.model.message;

import lombok.Data;

/**
 * @author winter
 * @create 2023-05-17 下午2:08
 */
@Data
public class HeartbeatRequest implements Message{
    public static final String TYPE = "HEARTBEAT";

    /**
    * ping
    */
    private String value;
}
