package com.linkhub.portal.im.handler;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.linkhub.portal.service.IUserService;
import com.linkhub.security.util.JwtTokenUtil;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * ws 连接建立的鉴权
 * @author winter
 * @create 2023-08-11 下午4:24
 */
@Component
@Slf4j
public class DefaultAuthListener implements AuthorizationListener {
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private JwtTokenUtil tokenUtil;

    private static final String TOKEN_PARAM = "token";

    @Override
    public boolean isAuthorized(HandshakeData data) {
        String token = data.getSingleUrlParam(TOKEN_PARAM);
        // 如果未传递 accessToken 或者没有带头
        if (StringUtils.isEmpty(token) || !token.startsWith(tokenHead)) {
            return false;
        }
        
        token = token.substring(tokenHead.length());
        String username = tokenUtil.getUsernameFromToken(token);
        // context 绑定了一个线程(如果当前线程首次请求没有 set userDetail, 则 set, 方便后续请求调用)
        if (username != null) {
            if (tokenUtil.validateToken(token)) {
                ArrayList<String> usernameList = new ArrayList<>();
                usernameList.add(username);
                data.getUrlParams().put("username", usernameList);
                log.info("Socket IO Authenticated User:[username: {}]", username);
                return true;
            }
        }
        return false;
    }
}