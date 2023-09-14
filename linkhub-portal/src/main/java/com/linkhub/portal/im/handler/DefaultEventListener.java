package com.linkhub.portal.im.handler;

import cn.hutool.json.JSONUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.linkhub.common.model.pojo.FriendRequest;
import com.linkhub.common.model.pojo.Inbox;
import com.linkhub.common.model.vo.FriendVo;
import com.linkhub.common.model.vo.GroupVo;
import com.linkhub.portal.im.util.IMUtil;
import com.linkhub.portal.service.*;
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
    private IInboxService iInboxService;
    @Resource
    private IGroupService groupService;

    @Resource
    private IFriendService friendService;
    @Resource
    private IFriendRequestService friendRequestService;

    @OnEvent("chat.converse.findAndJoinRoom")
    public void findAndJoinRoom(SocketIOClient client, Object data, AckRequest ackSender) {
        String userId = IMUtil.getUserIdByClient(client);
        Set<String> converseIds = converseService.getUserAllConverseIds(userId);
        IMUtil.joinRoom(converseIds, userId);
        ackSender.sendAckData(converseIds);
    }

    @OnEvent("user.dmlist.getAllConverse")
    public void getAllConverse(SocketIOClient client, Object data, AckRequest ackSender) {
        String userId = IMUtil.getUserIdByClient(client);
        Set<String> converseIds = converseService.getDMConverseIds(userId);
        ackSender.sendAckData(converseIds);
    }

    @OnEvent("friend.getAllFriends")
    public void getAllFriends(SocketIOClient client, Object data, AckRequest ackSender) {
        String userId = IMUtil.getUserIdByClient(client);
        List<FriendVo> friendVoList = friendService.getAllFriendsById(userId);
        ackSender.sendAckData(friendVoList);
    }
    @OnEvent("friend.request.allRelated")
    public void allRelated(SocketIOClient client, Object data, AckRequest ackSender) {
        String userId = IMUtil.getUserIdByClient(client);
        List<FriendRequest> friendRequests = friendRequestService.allRelated(userId);
        ackSender.sendAckData(friendRequests);
    }
    @OnEvent("group.getUserGroups")
    public void getUserGroups(SocketIOClient client, Object data, AckRequest ackSender) {
        String userId = IMUtil.getUserIdByClient(client);
        List<GroupVo> groups = groupService.getUserGroups(userId);
        String json = JSONUtil.toJsonStr(groups);
        ackSender.sendAckData(json);

    }

    @OnEvent("chat.inbox.add")
    public void allInboxes(SocketIOClient client, Object data, AckRequest ackSender) {
        String userId = IMUtil.getUserIdByClient(client);
        List<Inbox> all = iInboxService.all(userId);
        ackSender.sendAckData(all);

    }

}
