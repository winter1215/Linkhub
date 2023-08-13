package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
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

    @ApiModelProperty(value = "是否隐藏用户的随机码")
    private Boolean hideMemberInfo;

    @ApiModelProperty(value = "群组背景图片")
    private String groupBackgroundImage;

    @ApiModelProperty(value = "权限列表: Enum, Json")
    private String fallbackPermission;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


}
