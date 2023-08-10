package com.linkhub.portal.im.util;

import com.alibaba.fastjson.JSONObject;
import com.linkhub.portal.im.model.message.Message;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 工具类，提供客户端连接的管理等功能
 */
public class WebSocketUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketUtil.class);

    // ========== 会话相关 ==========

    /**
     * Session 与用户的映射
     */
    private static final Map<WebSocketSession, Long> SESSION_USER_MAP = new ConcurrentHashMap<>();
    /**
     * 用户与 Session 的映射
     */
    private static final Map<Long, WebSocketSession> USER_SESSION_MAP = new ConcurrentHashMap<>();
    /**
     * 用户与其好友列表的映射
     */
    private static final Map<Long, Set<Long>> USERID_FRIEND_SET_MAP = new ConcurrentHashMap<>();


    /**
     * 添加 Session 。在这个方法中，会添加用户和 Session 之间的映射
     *
     * @param session Session
     * @param userId  用户
     */
    public static void addSession(WebSocketSession session, Long userId, Set<Long> friendList) {
        // 更新 USER_SESSION_MAP
        USER_SESSION_MAP.put(userId, session);
        // 更新 SESSION_USER_MAP
        SESSION_USER_MAP.put(session, userId);
        USERID_FRIEND_SET_MAP.put(userId, friendList);
    }

    /**
     * 移除 Session 。
     *
     * @param session Session
     */
    public static void removeSession(WebSocketSession session) {
        // 从 SESSION_USER_MAP 中移除
        Long userId = SESSION_USER_MAP.remove(session);
        // 从 USER_SESSION_MAP 和 friendList 中移除
        if (ObjectUtils.isNotEmpty(userId)) {
            USER_SESSION_MAP.remove(userId);
            USERID_FRIEND_SET_MAP.remove(userId);
        }
    }
    // ========== 消息相关 ==========

    /**
     * 判断 friend id 是否为当前登录用户好友
     *
     * @param friendId:
     * @return: boolean
     * @author: winter
     * @date: 2023/5/12 上午10:41
     * @description:
     */
    public static boolean isMyFriend(Long friendId, WebSocketSession session) {
        Long loginUserId = (Long) session.getAttributes().get("loginUserId");
        Set<Long> set = USERID_FRIEND_SET_MAP.get(loginUserId);
        if (ObjectUtils.isNotEmpty(set)) {
            return set.contains(friendId);
        }
        return false;
    }

    /**
     * 检查 userId 是否已存在(重复)
     * @param userId:
     * @return: boolean
     * @author: winter
     * @date: 2023/5/17 下午10:19
     * @description:
     */
    public static boolean checkUserIdExist(Long userId) {
        return USER_SESSION_MAP.containsKey(userId);
    }

    /**
     * 广播发送消息给所有在线用户
     *
     * @param type    消息类型
     * @param message 消息体
     * @param <T>     消息类型
     */
    public static <T extends Message> void broadcast(String type, T message) {
        // 创建消息
        TextMessage textMessage = buildTextMessage(type, message);
        // 遍历 SESSION_USER_MAP ，进行逐个发送
        for (WebSocketSession session : SESSION_USER_MAP.keySet()) {
            sendTextMessage(session, textMessage);
        }
    }

    /**
     * 发送消息给单个用户的 Session
     *
     * @param session Session
     * @param type    消息类型
     * @param message 消息体
     * @param <T>     消息类型
     */
    public static <T extends Message> void send(WebSocketSession session, String type, T message) {
        // 创建消息
        TextMessage textMessage = buildTextMessage(type, message);
        // 遍历给单个 Session ，进行逐个发送
        sendTextMessage(session, textMessage);
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId  指定用户
     * @param type    消息类型
     * @param message 消息体
     * @param <T>     消息类型
     * @return 发送是否成功你那个
     */
    public static <T extends Message> boolean send(Long userId, String type, T message) {
        // 获得用户对应的 Session
        WebSocketSession session = USER_SESSION_MAP.get(userId);
        if (session == null) {
            LOGGER.error("[send][userId({}) 对方不在线,将消息持久化 DB]", userId);
            return false;
        }
        // 发送消息
        send(session, type, message);
        return true;
    }

    /**
     * 构建完整的消息
     *
     * @param type    消息类型
     * @param message 消息体
     * @param <T>     消息类型
     * @return 消息
     */
    private static <T extends Message> TextMessage buildTextMessage(String type, T message) {
        JSONObject messageObject = new JSONObject();
        messageObject.put("type", type);
        messageObject.put("body", message);
        return new TextMessage(messageObject.toString());
    }

    /**
     * 真正发送消息
     *
     * @param session     Session
     * @param textMessage 消息
     */
    private static void sendTextMessage(WebSocketSession session, TextMessage textMessage) {
        if (session == null) {
            LOGGER.error("[sendTextMessage][session 为 null]");
            return;
        }
        try {
            session.sendMessage(textMessage);
        } catch (IOException e) {
            LOGGER.error("[sendTextMessage][session({}) 发送消息{}) 发生异常",
                    session, textMessage, e);
        }
    }

    /**
     * 更新 userid - friends id List 缓存(防止连接后好友变化,聊天异常)
     *
     * @param loginUserId: 用户id
     * @param friendId:    变化的好友 id
     * @param isAdd:       true: 新增好友, false: 删除好友
     * @return: void
     * @author: winter
     * @date: 2023/5/12 下午4:15
     * @description:
     */
    public static void updateUserFriendSet(Long loginUserId, Long friendId, boolean isAdd) {
        Set<Long> userFriendIdSet = USERID_FRIEND_SET_MAP.get(loginUserId);
        Set<Long> friendFriendIdSet = USERID_FRIEND_SET_MAP.get(friendId);
        // 如果好友变化, 更新双方的缓存
        if (ObjectUtils.isNotEmpty(userFriendIdSet)) {
            if (isAdd) {
                userFriendIdSet.add(friendId);
            } else {
                userFriendIdSet.remove(friendId);
            }
        }
        if (ObjectUtils.isNotEmpty(friendFriendIdSet)) {
            if (isAdd) {
                friendFriendIdSet.add(loginUserId);
            } else {
                friendFriendIdSet.remove(loginUserId);
            }
        }



        LOGGER.info("监听到好友变化: loginUserId: {} friend id: {}", loginUserId, userFriendIdSet);

    }
}
