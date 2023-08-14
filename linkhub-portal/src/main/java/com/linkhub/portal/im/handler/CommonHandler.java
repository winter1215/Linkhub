package com.linkhub.portal.im.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.linkhub.common.config.redis.RedisCache;
import com.linkhub.portal.im.model.message.SendResponse;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author winter
 * @create 2023-08-11 下午2:39
 */
@Component
@Slf4j
public class CommonHandler {
    /**
    * 在线用户人数
    */
    public static AtomicInteger ONLINE_COUNT = new AtomicInteger();
    @Resource
    private RedisCache redisCache;
    /**
     * socketIOServer
     */
    @Resource
    private SocketIOServer socketIOServer;

    /**
     * 当客户端发起连接时调用
     *
     */
    @OnConnect
    public void onConnect(SocketIOClient socketIOClient) {
        String loginUserId = SecurityUtils.getLoginUserId();
        String onlineKey = IMUtil.buildUserOnlineKey(loginUserId);
        String userRoomId = IMUtil.buildUserRoomId(loginUserId);
        // 加入用户单独的房间(维护一个 userid 与 client 的映射)
        socketIOClient.joinRoom(userRoomId);
        // redis 存储在线的信息
        redisCache.setCacheObject(onlineKey, socketIOClient.getSessionId().toString(), 2, TimeUnit.DAYS);
        // 增加在线用户人数
        ONLINE_COUNT.incrementAndGet();
        log.info("connected ws server, user: {}", loginUserId);
    }

    /**
     * 客户端断开连接时调用，刷新客户端信息
     */
    @OnDisconnect
    public void onDisConnect(SocketIOClient socketIOClient) {
        // 不用手动移除 room
        // 移除 redis 的用户在线信息
        String loginUserId = SecurityUtils.getLoginUserId();
        String onlineKey = IMUtil.buildUserOnlineKey(loginUserId);
        redisCache.deleteObject(onlineKey);
        // 减少在线人数
        ONLINE_COUNT.decrementAndGet();
        log.info("Disconnected ws server , user: {} ", loginUserId);
    }

}
