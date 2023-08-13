package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 好友表
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Friend对象", description="好友表")
public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "主角")
    private String from;

    @ApiModelProperty(value = "主角的好友")
    private String to;

    @ApiModelProperty(value = "备注")
    private String remark;


}
