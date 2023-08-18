package com.linkhub.common.model.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.linkhub.common.model.pojo.Group;
import com.linkhub.common.model.pojo.GroupMember;
import com.linkhub.common.model.pojo.GroupPanel;
import com.linkhub.common.model.pojo.GroupRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author winter
 * @create 2023-08-18 上午1:04
 */
@Data
public class GroupVo {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    @ApiModelProperty(value = "创建者")
    private String owner;

    @ApiModelProperty(value = "群组描述")
    private String description;

    @ApiModelProperty(value = "群组头像")
    private String avatar;

    private List<GroupMember> members;
    private List<GroupPanel> panels;
    private List<GroupRole> roles;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Group.Config config;

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
