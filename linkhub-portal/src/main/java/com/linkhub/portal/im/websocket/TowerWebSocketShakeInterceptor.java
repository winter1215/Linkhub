package com.linkhub.portal.im.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * 自定义 HttpSessionHandshakeInterceptor 拦截器
 *
 * 因为 WebSocketSession 无法获得 ws 地址上的请求参数，所以只好通过该拦截器，获得 accessToken 请求参数，设置到 attributes 中
 */
@Component
public class TowerWebSocketShakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override // 拦截 Handshake 事件
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 获得 accessToken
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            String accessToken = serverRequest.getServletRequest().getParameter("accessToken");
            // 所有请求都应该握手,这样可以向 client 传递消息
            accessToken = accessToken == null ? "" : accessToken;
            attributes.put("accessToken", accessToken);
        }
        // 调用父方法，继续执行逻辑
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
