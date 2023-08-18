package com.linkhub.common.model.dto.group;

import lombok.Data;

/**
 * @author winter
 * @create 2023-08-18 上午11:56
 */
@Data
public class UpdateGroupFieldDto {
    private String groupId;
    private String fieldName;
    private String fieldValue;

}
