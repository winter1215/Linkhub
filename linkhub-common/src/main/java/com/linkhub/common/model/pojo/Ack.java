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
 * 
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Ack对象", description="")
public class Ack implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String converseId;

    private String userId;

    private Long lastMessageId;


}
