package com.linkhub.portal.im.handler;

import cn.hutool.json.JSONUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.linkhub.common.model.vo.FriendVo;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.service.IConverseService;
import com.linkhub.portal.service.IFriendService;
import com.linkhub.portal.service.IGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 事件监听器
 * @author winter
 * @create 2023-08-13 下午4:05
 */
@Component
@Slf4j
public class DefaultEventListener {
    @Resource
    private IConverseService converseService;
    @Resource
    private IGroupService groupService;

    @Resource
    private IFriendService friendService;

    @OnEvent("chat.converse.findAndJoinRoom")
    public void findAndJoinRoom(SocketIOClient client, Object data, AckRequest ackSender) {
        log.info("event listener");
        String userId = IMUtil.getUserIdByClient(client);
        Set<String> converseIds = converseService.getUserAllConverseIds(userId);
        IMUtil.joinRoom(converseIds);
    }

    @OnEvent("friend.getAllFriends")
    public void getAllFriends(SocketIOClient client, Object data, AckRequest ackSender) {
        String userId = IMUtil.getUserIdByClient(client);
        List<FriendVo> friendVoList = friendService.getAllFriendsById(userId);
        String json = JSONUtil.toJsonStr(friendVoList);
        client.sendEvent(null, json);
    }
    @OnEvent("group.getUserGroups")
    public void getUserGroups(SocketIOClient client, Object data, AckRequest ackSender) {
        String userId = IMUtil.getUserIdByClient(client);
        List<GroupVo> groups = groupService.getUserGroups(userId);
        String json = JSONUtil.toJsonStr(groups);
        client.sendEvent(null, json);
    }

}
