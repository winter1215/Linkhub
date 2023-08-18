package com.linkhub.common.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LinkCastling
 * @date 2023/8/17 22:46
 */
@Data
public class ConverseVo {
    private String _id;

    private String type;

    private List<String> members;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
