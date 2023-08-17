package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.linkhub.common.model.dto.message.SendMsgDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群组 info
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Group对象", description="群组 info")
@TableName(value = "`group`", autoResultMap = true)
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    @ApiModelProperty(value = "创建者")
    private String owner;

    @ApiModelProperty(value = "群组描述")
    private String description;

    @ApiModelProperty(value = "群组头像")
    private String avatar;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Config config;

    @ApiModelProperty(value = "权限列表: Enum, Json")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> fallbackPermission;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;

    @Data
    public static class Config {
        @ApiModelProperty(value = "是否隐藏用户的随机码")
        private Boolean hideMemberInfo;

        @ApiModelProperty(value = "群组背景图片")
        private String groupBackgroundImage;
    }
}
