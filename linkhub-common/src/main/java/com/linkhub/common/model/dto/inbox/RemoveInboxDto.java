package com.linkhub.common.model.dto.inbox;

import lombok.Data;

/**
 * @author winter
 * @create 2023-08-26 下午4:52
 */
@Data
public class RemoveInboxDto {
    private String userId;
    private String type;
    private String groupId;
    private String conserveId;
    private String messageId;
}
