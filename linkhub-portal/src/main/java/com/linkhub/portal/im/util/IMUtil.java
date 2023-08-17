package com.linkhub.portal.im.util;


import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.linkhub.common.config.redis.RedisCache;
import com.linkhub.common.enums.ClientEventEnum;
import com.linkhub.common.enums.IMNotifyTypeEnum;
import com.linkhub.common.model.pojo.User;
import com.linkhub.portal.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author winter
 * @create 2023-08-13 下午8:48
 */
@Component
@Slf4j
public class IMUtil {

    private static RedisCache redisCache;
    private static SocketIOServer server;

    public IMUtil(SocketIOServer server, RedisCache redisCache) {
        IMUtil.server = server;
        IMUtil.redisCache = redisCache;
    }

    public static String getUserIdByClient(SocketIOClient client) {
        User user =  (User)client.get("user");
        return user == null ? "" : user.getId();
    }

    /**
     * 构建用户单独的房间Id, 让给指定用户发送消息成为可能
     */
    public static String buildUserRoomId(String userId) {
        return "u-" + userId;
    }
    /**
    * 构建用户存储 redis 的在线 key
    */
    public static String buildUserOnlineKey(String userId) {
        return "linkhub-online:" + userId;
    }

    public static void joinOrLeaveRoom(Set<String> roomIds, boolean isJoin) {
        String loginUserId = SecurityUtils.getLoginUserId();
        String userRoomId = buildUserRoomId(loginUserId);
        Collection<SocketIOClient> clients = server.getRoomOperations(userRoomId).getClients();
        if (ObjectUtils.isEmpty(clients)) {
            log.error("can not fetch clients in room({}), check connection event?", userRoomId);
            return;
        }
        // 通常情况下 clients 只有一个
        clients.forEach(client -> {
            if (isJoin) {
                client.joinRooms(roomIds);
            } else {
                client.leaveRooms(roomIds);
            }
            log.info("user({}) join rooms({}) successfully ", loginUserId, roomIds);
        });
    }

    /**
     * 加入房间
     */
    public static void joinRoom(Set<String> roomIds) {
        joinOrLeaveRoom(roomIds, true);
    }
    /**
    * 离开群组
    */
    public static void leaveRoom(Set<String> roomIds) {
        joinOrLeaveRoom(roomIds, false);
    }

    /**
     * 向客户端推送消息
     * @param target: 组播: 发送对象的会话 id; 单播,列播: userId; 广播: null; 除了列播,集合应该都只带一个元素
     * @param type: ImNotifyTypeEnum 枚举对象,选择扩散的类型
     * @param eventName: 事件名称
     * @param data: 数据
     * @return: void
     * @author: winter
     * @date: 2023/8/14 上午11:26
     * @description:
     */
    public static void notify(String[] target, IMNotifyTypeEnum type, ClientEventEnum event, Object data) {
        String eventName = event.getMessage();

        if (type == IMNotifyTypeEnum.UNICAST) {
            server.getRoomOperations(buildUserRoomId(target[0])).sendEvent(eventName, data);
        } else if (type == IMNotifyTypeEnum.LIST_CAST) {
            Arrays.stream(target).forEach(userId -> {
                server.getRoomOperations(buildUserRoomId(userId)).sendEvent(eventName, data);
            });
        } else if (type == IMNotifyTypeEnum.ROOM_CAST) {
            server.getRoomOperations(target[0]).sendEvent(eventName, data);
        } else if (type == IMNotifyTypeEnum.BROAD_CAST) {
            server.getBroadcastOperations().sendEvent(eventName, data);
        } else {
            log.error("Wrong notify type({}), please check twice", type);
        }
    }

    /**
    * 检查用户在线情况
    */
    public static List<Boolean> checkUserOnline(List<String> userIds) {
        List<String> list = userIds.stream().map(IMUtil::buildUserOnlineKey).collect(Collectors.toList());
        List<String> userUUIDs = redisCache.multiGetCacheObject(list);
        return userUUIDs.stream().map(ObjectUtils::isNotEmpty).collect(Collectors.toList());
    }

}
