package com.zs.rtspface.controller;

import com.zs.rtspface.ws.WsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/rtsp")
public class RtspController {

    @Autowired
    private WsHandler wsHandler;

    @RequestMapping("/receive")
    @ResponseBody
    public String receive(HttpServletRequest request, Object response) {
        System.out.println("method:" + request.getMethod());
        try {
            ServletInputStream inputStream = request.getInputStream();
            int len = -1;
            while ((len =inputStream.available()) !=-1) {
                byte[] data = new byte[len];
                inputStream.read(data);
//                for (int i = 0; i < data.length; i++) {
//                    System.out.print(data[i] + " ");
//                }
//                System.out.println("");
//                System.out.println(len);
//                System.out.println("--------------------------------------------------------");
                wsHandler.sendVideo(data);
            }
        } catch (Exception e) {
        e.printStackTrace();
    }
        System.out.println("over");
        return "1";
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test(HttpServletRequest request, HttpServletResponse response) {
        return "1";
    }

}
