package com.zs.rtspface.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsHandler extends BinaryWebSocketHandler {

    /**
     * 存放所有在线的客户端
     */
    private static Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("新的加入");
        clients.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        clients.remove(session.getId());
    }

    public void sendVideo(byte[] data) {
        try{
            BinaryMessage binaryMessage = new BinaryMessage(data);
            for (Map.Entry<String, WebSocketSession> sessionEntry : clients.entrySet()) {
                try {
                    WebSocketSession session = sessionEntry.getValue();
                    if (session.isOpen()) {
                        session.sendMessage(binaryMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    public void byte2image(byte[] data,String path){
        if(data.length<3||path.equals("")) return;
        try{
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch(Exception ex) {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }
}
