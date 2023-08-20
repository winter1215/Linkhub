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
 * 群组邀请码
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="GroupInvite对象", description="群组邀请码")
public class GroupInvite implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String code;

    private String creator;

    private String groupId;

    @TableField("expireAt")
    private LocalDateTime expireat;

    @ApiModelProperty(value = "使用次数")
    private Integer usage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


}
