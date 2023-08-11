package com.linkhub.portal.im.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * SpringBoot启动之后执行
 *
 * @author winter
 */
@Component
@Order(1)
@Slf4j
public class SocketServer implements CommandLineRunner {

    /**
     * socketIOServer
     */
    private final SocketIOServer socketIOServer;

    @Autowired
    public SocketServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @Override
    public void run(String... args) {
        log.info("---------- Netty-Socket is starting up ----------");
        socketIOServer.start();
        log.info("---------- Netty-Socket has been activated ----------");
    }

}
