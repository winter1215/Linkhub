package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群组面板
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="GroupPanel对象", description="群组面板")
@TableName(autoResultMap = true)
public class GroupPanel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "群组id")
    private String groupId;

    @ApiModelProperty(value = "面板名字")
    private String name;

    @ApiModelProperty(value = "面板类型(0, 1, 2, 3)")
    private Integer type;

    @ApiModelProperty(value = "父亲面板 id")
    private String parentId;

    @ApiModelProperty(value = "面板的配置信息")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String meta;

    private String provider;

    @TableField("pluginPaneName")
    private String pluginPanelName;

    private LocalDateTime creatAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


}
