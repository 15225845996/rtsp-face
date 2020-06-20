package com.zs.rtspface.ws;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class WsIntercept extends HttpSessionHandshakeInterceptor {

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
        HttpServletResponse response = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
        String header = request.getHeader("sec-websocket-protocol");
        if (StringUtils.isNotEmpty(header)) {
            response.addHeader("sec-websocket-protocol",header);
        }
        super.afterHandshake(serverHttpRequest,serverHttpResponse,webSocketHandler,e);
    }
}
