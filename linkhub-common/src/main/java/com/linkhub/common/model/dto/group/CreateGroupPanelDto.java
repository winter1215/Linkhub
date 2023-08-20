package com.linkhub.common.model.dto.group;

import com.linkhub.common.model.pojo.GroupPanel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author winter
 * @create 2023-08-20 下午6:31
 */
@Data
public class CreateGroupPanelDto {
    @NotNull
    private String groupId;
    @NotNull
    private String name;
    @NotNull
    private Integer type;
    private String parentId;
    private String provider;
    private String pluginPanelName;
    private GroupPanel.Meta meta;
}
