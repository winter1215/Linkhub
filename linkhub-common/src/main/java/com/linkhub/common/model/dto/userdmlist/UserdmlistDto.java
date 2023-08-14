package com.linkhub.common.model.dto.userdmlist;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LinkCastling
 * @date 2023/8/12 19:20
 */
@Data
public class UserdmlistDto {
//    @ApiModelProperty(value = "uuid")
//    @TableId(value = "id", type = IdType.ASSIGN_UUID)
//    private String id;

    private String userId;

    @ApiModelProperty(value = "会话id")
    private List<String> converseIds;
}
