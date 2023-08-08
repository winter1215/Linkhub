package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户的会话列表
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Userdmlist对象", description="用户的会话列表")
public class Userdmlist implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "uuid")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;

    @ApiModelProperty(value = "会话id")
    private String converseId;


}
