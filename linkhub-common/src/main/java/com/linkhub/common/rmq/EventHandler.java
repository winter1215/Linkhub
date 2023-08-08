package com.linkhub.common.rmq;

/**
 * 事件处理器的抽象接口
 * @author winter
 * @create 2023-01-14 下午5:49
 */
public interface EventHandler {
    /**
     * 事件处理器的具体行为
     * @return: void
     * @author: winter
     * @date: 2023/1/14 下午5:52
     * @description:
     */
    void doHandler(EventModel eventModel);

}
