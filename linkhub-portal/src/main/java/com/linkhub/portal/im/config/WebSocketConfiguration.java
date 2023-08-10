package com.linkhub.portal.im.config;

import com.tower.portal.im.websocket.TowerWebSocketShakeInterceptor;
import com.tower.portal.im.websocket.WordWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // 开启 Spring WebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    @Autowired
    private WordWebSocketHandler wordWebSocketHandler;
    @Autowired
    private TowerWebSocketShakeInterceptor towerWebSocketShakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wordWebSocketHandler, "/im") // 配置处理器
                .addInterceptors(towerWebSocketShakeInterceptor) // 配置拦截器
                .setAllowedOrigins("*"); // 解决跨域问题
    }

}
