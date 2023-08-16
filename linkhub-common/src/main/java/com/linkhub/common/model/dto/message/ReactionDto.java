package com.linkhub.common.model.dto.message;

import lombok.Data;

/**
 * @author winter
 * @create 2023-08-16 下午6:48
 */
@Data
public class ReactionDto {
    private Long messageId;
    private String emoji;
}
