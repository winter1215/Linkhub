package com.linkhub.common.model.dto.group;

import lombok.Data;

/**
 * @author winter
 * @create 2023-08-18 上午11:56
 */
@Data
public class UpdateGroupConfigDto {
    private String groupId;
    private String configName;
    private String configValue;

}
