package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

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
public class Inbox implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;

    @ApiModelProperty(value = "单人会话:0, 群组: 1")
    private Integer type;

    private Boolean readed;

    private String conserveId;

    private String messageId;

    private String messageAuthor;

    private String messageSnippet;

    @ApiModelProperty(value = "回复纯文本")
    private String messagePlainContent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;

    public static Inbox sendMsgDtoConvertDomain(SendMsgDto sendMsgDto, String mention) {
        InboxTypeEnum typeEnum =  ObjectUtils.isEmpty(sendMsgDto.getGroupId())
                ? InboxTypeEnum.DM
                : InboxTypeEnum.GROUP_MESSAGE;

        Inbox inbox = new Inbox();
        inbox.setUserId(mention);
        inbox.setType(typeEnum.getCode());
        if (typeEnum == InboxTypeEnum.DM) {
            inbox.setConserveId(sendMsgDto.getConverseId());
        } else if (typeEnum == InboxTypeEnum.GROUP_MESSAGE){
            inbox.setConserveId(sendMsgDto.getGroupId());
        }
        inbox.setMessageId(sendMsgDto.getMeta().getReply().get_id());
        inbox.setMessageAuthor(sendMsgDto.getMeta().getReply().getAuthor());
        inbox.setMessageSnippet(sendMsgDto.getMeta().getReply().getContent());
        inbox.setMessagePlainContent(sendMsgDto.getPlain());
        return inbox;
    }
}
