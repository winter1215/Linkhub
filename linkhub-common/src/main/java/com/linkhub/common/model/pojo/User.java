package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="用户")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "uuid")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String email;

    private String nickname;

    @ApiModelProperty(value = "临时帐号")
    private Boolean temporary;

    private String password;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "用户类型")
    private String type;

    @ApiModelProperty(value = "邮箱验证")
    private Boolean emailVerified;

    @ApiModelProperty(value = "封禁")
    private Boolean banned;

    @ApiModelProperty(value = "帐号随机标识符")
    private String discriminator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


}
