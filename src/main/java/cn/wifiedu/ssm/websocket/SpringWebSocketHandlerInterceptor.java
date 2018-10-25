package cn.wifiedu.ssm.websocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
* <p>Title: SpringWebSocketHandlerInterceptor</p>
* <p>Description:WebSocket拦截器 </p>
* <p>Company: feixu</p>
* @author    wangjinglong
* @date       2018年10月25日
*/
public class SpringWebSocketHandlerInterceptor extends  HttpSessionHandshakeInterceptor {

    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Before Handshake");
        //获取当前Session
        if (request instanceof ServletServerHttpRequest) {
        	ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            attributes.put("openId",session.getAttribute("openId"));
            attributes.put("shopId",session.getAttribute("shopId"));
            System.out.println("用户openId"+session.getAttribute("openId"));
        }
        System.out.println("连接到我了");
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        // TODO Auto-generated method stub
        super.afterHandshake(request, response, wsHandler, ex);
    }

}

