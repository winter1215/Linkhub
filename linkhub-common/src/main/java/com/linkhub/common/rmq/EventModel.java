package com.linkhub.common.rmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author winter
 * @create 2023-01-14 上午10:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventModel {
    /**
    * 事件类型:分发给可以处理该事件的处理器进行处理
    */
    private EventType topic;

    /**
    * id: 谁触发的该事件
    */
    private Integer sourceId;
    /**
    *  id：需要通知谁
    */
    private Integer targetId;
    /**
    * 扩展字段
    */
    private Map<String, String> exts;

}
