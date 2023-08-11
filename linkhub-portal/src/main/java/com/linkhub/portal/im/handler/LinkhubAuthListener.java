package com.linkhub.portal.im.handler;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.linkhub.security.util.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ws 连接建立的鉴权
 * @author winter
 * @create 2023-08-11 下午4:24
 */
@Component
public class LinkhubAuthListener implements AuthorizationListener {
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Override
    public boolean isAuthorized(HandshakeData data) {
        String token = data.getSingleUrlParam("token");
        // 如果未传递 accessToken 或者没有带头
        if (StringUtils.isEmpty(token) || !token.startsWith(tokenHead)) {
            return false;
        }
        // 截取出真实的 token 出来
        token = token.substring(tokenHead.length());
        // token 鉴权
        String username = tokenUtil.getUsernameFromToken(token);

        if (username == null) {
            return false;
        }
        return true;
    }
}
