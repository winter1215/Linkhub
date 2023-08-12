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
    public String content;
    public String converseId;
    public Meta meta;
    public String groupId;
    public String plain;


    /**
     * 额外信息
     */
    @Data
    public static class Meta {
        /**
         * @ 的好友数组
         */
        public List<String> mentions;
        public Reply reply;
    }

    @Data
    public static class Reply {
        /**
         * 回复的消息id
         */
        public String _id;

        public String author;
        public String content;

    }
}
