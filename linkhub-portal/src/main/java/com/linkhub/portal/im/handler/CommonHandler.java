package com.linkhub.portal.im.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.linkhub.portal.im.model.message.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author winter
 * @create 2023-08-11 下午2:39
 */
@Component
@Slf4j
public class CommonHandler {

    /**
     * ConcurrentHashMap保存当前SocketServer用户ID对应关系
     */
    private Map<String, UUID> clientMap = new ConcurrentHashMap<>(16);

    public Map<String, UUID> getClientMap() {
        return clientMap;
    }

    public void setClientMap(Map<String, UUID> clientMap) {
        this.clientMap = clientMap;
    }

    /**
     * socketIOServer
     */
    private final SocketIOServer socketIOServer;

    @Autowired
    public CommonHandler(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    /**
     * 当客户端发起连接时调用
     *
     */
    @OnConnect
    public void onConnect(SocketIOClient socketIOClient) {
        log.info("handshake: {}", socketIOClient.getHandshakeData());
        String userName = socketIOClient.getHandshakeData().getSingleUrlParam("userName");
        if (StringUtils.isNotBlank(userName)) {
            log.info("用户{}开启长连接通知, NettySocketSessionId: {}, NettySocketRemoteAddress: {}",
                    userName, socketIOClient.getSessionId().toString(), socketIOClient.getRemoteAddress().toString());
            // 保存
            clientMap.put(userName, socketIOClient.getSessionId());
            // 发送上线通知
            //this.sendMsg(null, null,new MessageDto(userName, null, MsgTypeEnum.ONLINE.getValue(), null));
        }
    }

    /**
     * 客户端断开连接时调用，刷新客户端信息
     */
    @OnDisconnect
    public void onDisConnect(SocketIOClient socketIOClient) {
        String userName = socketIOClient.getHandshakeData().getSingleUrlParam("userName");
        if (StringUtils.isNotBlank(userName)) {
            log.info("用户{}断开长连接通知, NettySocketSessionId: {}, NettySocketRemoteAddress: {}",
                    userName, socketIOClient.getSessionId().toString(), socketIOClient.getRemoteAddress().toString());
            // 移除
            clientMap.remove(userName);
            // 发送下线通知
            //this.sendMsg(null, null,new MessageDto(userName, null, MsgTypeEnum.OFFLINE.getValue(), null));
        }
    }

    /**
     * sendMsg发送消息事件
     */
    @OnEvent("sendMsg")
    public void sendMsg(SocketIOClient socketIOClient, AckRequest ackRequest, SendResponse response) {
        if (response != null) {
            // 全部发送
            clientMap.forEach((key, value) -> {
                if (value != null) {
                    socketIOServer.getClient(value).sendEvent("receiveMsg", response);
                }
            });
        }
    }

}
