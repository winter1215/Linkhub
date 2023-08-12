package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群组成员
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="GroupMember对象", description="群组成员")
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String groupId;

    private String userId;

    @ApiModelProperty(value = "role json")
    private String roles;

    @ApiModelProperty(value = "禁言截至时间")
    private LocalDateTime muteUtil;


}
