package com.linkhub.common.model.dto.group;

import lombok.Data;

import java.util.List;

/**
 * @author winter
 * @create 2023-08-18 上午12:38
 */
@Data
public class CreateGroupDto {
    private String name;
    private List<Panel> panels;

    @Data
    public class Panel {
        private Integer id;
        private String name;
        private String parentId;
        private Integer type;
    }
}
