package com.zs.rtspface.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * @author volume
 **/
@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private WsIntercept wsIntercept;

    @Autowired
    private WsHandler wsHandler;

//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(wsHandler,"/rtsp").addInterceptors(wsIntercept).setAllowedOrigins("*");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(10*1024);
        container.setMaxBinaryMessageBufferSize(10*1024);
        return container;
    }
}
