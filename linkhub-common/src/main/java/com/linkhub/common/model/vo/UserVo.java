package com.linkhub.common.model.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author winter
 * @create 2023-03-06 下午8:29
 */
@Data
public class UserVo {
    private String id;

    private String token;
    private String email;

    private String nickname;

    private Boolean temporary;

    private String avatar;

    private String type;

    private Boolean emailVerified;

    private Boolean banned;

    private String discriminator;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
