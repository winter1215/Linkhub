package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.linkhub.common.model.dto.message.SendMsgDto;
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

    @ApiModelProperty(value = "snow")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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

    public static Message convertToDomain(SendMsgDto sendMsgDto, String author) {
        Message message = new Message();
        message.setContent(sendMsgDto.getContent());
        message.setAuthor(author);
        message.setConverseId(sendMsgDto.getConverseId());
        if (sendMsgDto.getMeta() != null && sendMsgDto.getMeta().getReply() != null) {
            message.setReplyId(sendMsgDto.getMeta().getReply().get_id());
        }
        if (sendMsgDto.getMeta() != null && sendMsgDto.getMeta().getReply() != null) {
            message.setReplyContent(sendMsgDto.getMeta().getReply().getContent());
        }
        if (sendMsgDto.getMeta() != null && sendMsgDto.getMeta().getReply() != null) {
            message.setReplyAuthor(sendMsgDto.getMeta().getReply().getAuthor());
        }
        message.setGroupId(sendMsgDto.getGroupId());
        message.setCreateAt(LocalDateTime.now());
        message.setUpdateAt(LocalDateTime.now());
        return message;
    }
}
