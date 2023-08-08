package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author CYY&winter
 * @since 2022-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id唯一标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户名,用户名不可重复")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "个性签名")
    private String introduction;

    @ApiModelProperty(value = "描述该账号所处状态，0：正常  1：禁言，不能评论与发帖（admin）2: 封禁，不能登录（admin）")
    private Integer status;

    @ApiModelProperty(value = "学号")
    private String studentNo;

    @ApiModelProperty(value = "姓名")
    private String realName;

    @ApiModelProperty(value = "性别  0 保密 1 男 2 女")
    private Integer gender;

    @ApiModelProperty(value = "邮箱")
    private String mail;

    @ApiModelProperty(value = "被禁言次数")
    private Integer bannedCount;

    private LocalDateTime bannedBegin;

    private LocalDateTime bannedEnd;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "逻辑删除 0：正常   1：删除")
    @TableLogic
    private Boolean isDelete;

}
