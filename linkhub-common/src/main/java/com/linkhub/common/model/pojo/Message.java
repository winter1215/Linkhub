package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 聊天记录
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Message对象", description="聊天记录")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "uuid")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String content;

    @ApiModelProperty(value = "userId")
    private String author;

    private String converseId;

    @ApiModelProperty(value = "撤回")
    private Boolean hasRecall;

    @ApiModelProperty(value = "回复 msg 的id")
    private String replyId;

    @ApiModelProperty(value = "回复的消息内容(长度限制)")
    private String replyContent;

    private String replyAuthor;

    @ApiModelProperty(value = "群组id")
    private String groupId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


}
