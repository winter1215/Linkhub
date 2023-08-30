package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.linkhub.common.enums.InboxTypeEnum;
import com.linkhub.common.model.dto.message.SendMsgDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;

/**
 * <p>
 * 收件箱
 * </p>
 *
 * @author ku&winter
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Inbox对象", description="收件箱")
@TableName(autoResultMap = true)
public class Inbox implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;

    private String type;

    private Boolean readed;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Payload payload;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;

    public static Inbox sendMsgDtoConvertDomain(SendMsgDto sendMsgDto, String mention) {
        Inbox inbox = new Inbox();
        inbox.setUserId(mention);
        inbox.setType(InboxTypeEnum.MESSAGE.getMessage());
        if (ObjectUtils.isNotEmpty(sendMsgDto.getGroupId())) {
            inbox.getPayload().setGroupId(sendMsgDto.getGroupId());
        } else {
            inbox.getPayload().setConserveId(sendMsgDto.getConverseId());
        }
        inbox.getPayload().setMessageId(sendMsgDto.getMeta().getReply().get_id());
        inbox.getPayload().setMessageAuthor(sendMsgDto.getMeta().getReply().getAuthor());
        inbox.getPayload().setMessageSnippet(sendMsgDto.getMeta().getReply().getContent());
        inbox.getPayload().setMessagePlainContent(sendMsgDto.getPlain());
        return inbox;
    }

    @Data
    public static class Payload {
        private String conserveId;
        private String groupId;

        private String messageId;

        private String messageAuthor;

        private String messageSnippet;
        @ApiModelProperty(value = "回复纯文本")
        private String messagePlainContent;
    }
}
