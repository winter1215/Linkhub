package com.linkhub.common.model.pojo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 消息反应和mention
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="MsgExtra对象", description="消息反应和mention")
public class MsgExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private Long msgId;

    @ApiModelProperty(value = "reaction name")
    private String name;

    private String author;

    private String mention;


}
