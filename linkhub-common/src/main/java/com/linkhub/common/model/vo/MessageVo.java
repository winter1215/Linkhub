package com.linkhub.common.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.linkhub.common.model.pojo.Reaction;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author winter
 * @create 2023-08-08 下午5:49
 */
@Data
public class MessageVo {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "uuid")
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

    private List<Reaction> reactions;
    private List<String> mentions;

    @TableField("createAt")
    private LocalDateTime createAt;

    @TableField("updateAt")
    private LocalDateTime updateAt;
}
