package com.linkhub.portal.im.handler;

import com.linkhub.portal.im.model.message.AuthRequest;
import com.linkhub.portal.service.IUserService;
import com.linkhub.security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;

@Component
@Slf4j
public class AuthMessageHandler implements MessageHandler<AuthRequest> {
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private JwtTokenUtil tokenUtil;

    @Autowired
    private IUserService userService;

    @Autowired
    //private IFriendService friendService;

    @Override
    public void execute(WebSocketSession session, AuthRequest message) {
        // 如果未传递 accessToken
        String token = message.getAccessToken();
        // 如果 token 有 token head 的话,就截取出真实的 token 出来
        if (token.startsWith(tokenHead)) {
            token = token.substring(tokenHead.length());
        }
        // token 鉴权
        String username = tokenUtil.getUsernameFromToken(token);

        //if (username == null) {
        //    WebSocketUtil.send(session, AuthResponse.TYPE,
        //            new AuthResponse().setCode(1).setMessage("认证失败,连接已断开"));
        //    try {
        //        session.close();
        //    } catch (IOException e) {
        //        log.warn("验证失败后主动关闭连接失败");
        //    }
        //    return;
        //}

        //// 将登录用户 id 与 session 关联
        //User user = userService.getUserByUsername(username);
        //Long loginUserId = user.getId();
        //// todo: 检查重复连接
        //// 向 session 中设置 loginUserId, 方便后面能从 session 中取值
        //session.getAttributes().put("loginUserId", loginUserId);
        //
        //// 加载登录的好友列表到内存
        //Set<Long> userFriendIdSet = friendService.getUserFriendIdSet(loginUserId);
        //// 添加到 session 中
        //WebSocketUtil.addSession(session, loginUserId, userFriendIdSet);
        //
        //// 响应用户
        //WebSocketUtil.send(session, AuthResponse.TYPE, new AuthResponse().setCode(0).setMessage(username));


    }

    @Override
    public String getType() {
        return AuthRequest.TYPE;
    }

}
