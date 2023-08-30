package com.linkhub.common.model.dto.ack;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author winter
 * @create 2023-08-28 上午10:17
 */
@Data
public class UpdateAckDto {
    @NotNull
    private String converseId;
    @NotNull
    private Long lastMessageId;
}
