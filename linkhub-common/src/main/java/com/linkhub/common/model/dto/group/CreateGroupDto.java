package com.linkhub.common.model.dto.group;

import com.linkhub.common.model.pojo.GroupPanel;
import lombok.Data;

import java.util.List;

/**
 * @author winter
 * @create 2023-08-18 上午12:38
 */
@Data
public class CreateGroupDto {
    private String name;
    private List<GroupPanel> panels;
}
