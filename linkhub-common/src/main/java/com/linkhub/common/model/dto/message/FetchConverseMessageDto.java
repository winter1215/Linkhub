package com.linkhub.common.model.dto.message;

import lombok.Data;

/**
 * @author winter
 * @create 2023-08-15 下午11:58
 */
@Data
public class FetchConverseMessageDto {
    private String converseId;
    private Long startId; // msgId
}
