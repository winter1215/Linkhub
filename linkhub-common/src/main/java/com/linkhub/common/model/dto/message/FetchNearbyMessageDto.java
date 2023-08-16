package com.linkhub.common.model.dto.message;

import lombok.Data;

/**
 * @author winter
 * @create 2023-08-15 下午11:58
 */
@Data
public class FetchNearbyMessageDto {
    private String converseId;
    private Long messageId; // msgId
    private Integer num;

}
