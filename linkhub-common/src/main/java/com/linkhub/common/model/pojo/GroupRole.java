package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群组的权限组
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="GroupRole对象", description="群组的权限组")
@TableName(autoResultMap = true)
public class GroupRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "哪个 group 的权限组")
    private String groupId;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "该组拥有的权限列表")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> permissions;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


}
