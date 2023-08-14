package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 好友申请表
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="FriendRequest对象", description="好友申请表")
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("`from`")
    private String from;

    @TableField("`to`")
    private String to;

    @ApiModelProperty(value = "好友申请的信息")
    @TableField("`remark`")
    private String remark;


}
