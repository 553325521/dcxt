package cn.wifiedu.ssm.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
public class SystemWebSocketHandler extends TextWebSocketHandler {
    
    private static Map<String,WebSocketSession> userMap = new HashMap<String, WebSocketSession>();
    
    private static Map<String,String> userShopMap = new HashMap<String,String>();
    
    private static ArrayList<WebSocketSession> users = new ArrayList<WebSocketSession>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.print("连接成功");
        users.add(session);
        System.out.println("当前用户数量: " + users.size());
        sendMessagesToUsers(new TextMessage("今天晚上服务器维护,请注意"));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	String schatMessage = message.getPayload();//用户输入
        System.out.println("用户输入" + schatMessage);
        session.sendMessage(message);  
     // 将消息进行转化，因为是消息是json数据，可能里面包含了发送给某个人的信息，所以需要用json相关的工具类处理之后再封装成TextMessage，
        // 我这儿并没有做处理，消息的封装格式一般有{from:xxxx,to:xxxxx,msg:xxxxx}，来自哪里，发送给谁，什么消息等等
       /*  TextMessage msg = (TextMessage)message.getPayload();*/
        // 给所有用户群发消息
       // sendMessagesToUsers(msg);
        // 给指定用户群发消息
        //sendMessageToUser(userId, msg);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
       /* users.remove(session);*/
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	if (session.isOpen()) {
            session.close();
        }
       /* users.remove(session);
        System.out.println("安全退出了系统");
        System.out.println("用户数量: " + users.size());*/
    }
    
    private void sendMessageToUsers(WebSocketMessage<?> message) {
        for (WebSocketSession user : users) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 给所有的用户发送消息
     */
    public void sendMessagesToUsers(TextMessage message) {
        for (WebSocketSession user : users) {
            try {
                // isOpen()在线就发送
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
