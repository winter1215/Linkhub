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
 * 用户设置
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="UserSetting对象", description="用户设置")
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    @ApiModelProperty(value = "通知")
    private Boolean notification;

    @ApiModelProperty(value = "消息列表虚拟化")
    private Boolean msgListVirtual;


}
