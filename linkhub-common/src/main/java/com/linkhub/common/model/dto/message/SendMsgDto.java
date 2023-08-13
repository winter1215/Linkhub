package com.linkhub.common.model.dto.message;

import lombok.Data;

import java.util.List;

/**
 * 发送消息的 DTO
 * @author winter
 * @create 2023-08-12 上午12:52
 */
@Data
public class SendMsgDto {
    private String content;
    private String converseId;
    private Meta meta;
    private String groupId;
    private String plain;


    /**
     * 额外信息
     */
    @Data
    public static class Meta {
        /**
         * @ 的好友数组
         */
        private List<String> mentions;
        private Reply reply;
    }

    @Data
    public static class Reply {
        /**
         * 回复的消息id
         */
        private String _id;

        private String author;
        private String content;

    }
}
