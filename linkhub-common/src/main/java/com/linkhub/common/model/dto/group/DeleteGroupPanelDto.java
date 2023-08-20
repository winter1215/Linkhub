package com.linkhub.common.model.dto.group;

import com.linkhub.common.model.pojo.GroupPanel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author winter
 * @create 2023-08-20 下午6:31
 */
@Data
public class DeleteGroupPanelDto {
    @NotNull
    private String groupId;
    @NotNull
    private String panelId;
}
