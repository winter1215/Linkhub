package com.linkhub.common.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    private Integer conserveId;

    private String messageId;

    private String messageAuthor;

    private String messageSnippet;

    @ApiModelProperty(value = "回复纯文本")
    private String messagePlainContent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;


}
