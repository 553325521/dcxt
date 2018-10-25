package cn.wifiedu.ssm.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import cn.wifiedu.ssm.controller.WxController;

/**
* <p>Title: SystemWebSocketHandler</p>
* <p>Description:WebSocket拦截器 </p>
* <p>Company: feixu</p>
* @author    wangjinglong
* @date       2018年10月25日
*/
@Service
public class SystemWebSocketHandler implements WebSocketHandler  {
    
    private static Map<String,WebSocketSession> userMap = new HashMap<String, WebSocketSession>();
    
    private static Map<String,String> userShopMap = new HashMap<String,String>();
    
    private static ArrayList<WebSocketSession> users = new ArrayList<WebSocketSession>();
    
	private static Logger logger = Logger.getLogger(WxController.class);
    /* （非 Javadoc）
    * <p>Title: afterConnectionEstablished</p>
    * <p>Description: 建立连接之后的调用方法</p>
    * @param session
    * @throws Exception
    * @see org.springframework.web.socket.WebSocketHandler#afterConnectionEstablished(org.springframework.web.socket.WebSocketSession)
    */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //System.out.print("连接成功");
        logger.info("连接成功");
        String openId = session.getHandshakeAttributes().get("openId").toString();
        String shopId = session.getHandshakeAttributes().get("shopId").toString();
        userMap.put(openId, session);
        userShopMap.put(openId,shopId);
        users.add(session);
        logger.info("当前用户数量: " + userMap.keySet().size());
        sendMessagesToUsers(new TextMessage("今天晚上服务器维护,请注意"));
    }

    /* （非 Javadoc）
    * <p>Title: handleMessage</p>
    * <p>Description:处理客户端发来的消息 </p>
    * @param session
    * @param message
    * @throws Exception
    * @see org.springframework.web.socket.WebSocketHandler#handleMessage(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.WebSocketMessage)
    */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    	String schatMessage = (String)message.getPayload();//用户输入
    	 logger.info("用户输入:" + schatMessage);
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
    	 userMap.remove(session.getHandshakeAttributes().get("openId").toString());
         userShopMap.remove(session.getHandshakeAttributes().get("openId").toString());
        if (session.isOpen()) {
            session.close();
        }
    }

    /* （非 Javadoc）
    * <p>Title: afterConnectionClosed</p>
    * <p>Description:连接关闭后调用的函数 </p>
    * @param session
    * @param closeStatus
    * @throws Exception
    * @see org.springframework.web.socket.WebSocketHandler#afterConnectionClosed(org.springframework.web.socket.WebSocketSession, org.springframework.web.socket.CloseStatus)
    */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	 
    	userMap.remove(session.getHandshakeAttributes().get("openId").toString());
    	 userShopMap.remove(session.getHandshakeAttributes().get("openId").toString());
    	if (session.isOpen()) {
            session.close();
        }
    	 logger.info("安全退出");
    }
    
    /**
     * 给所有的用户发送消息
     */
    public void sendMessagesToUsers(TextMessage message) {
        for (WebSocketSession user : userMap.values()) {
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
    
    /**
     * 更新订单数量和数据发送消息给指定的用户
     */
    public void sendMessageToUser(String shopId, TextMessage message) {
    	try {
	    	String [] userKeys = SystemWebSocketHandler.getKeys(userShopMap, shopId);
	    	if(userKeys.length!=0){
	    		for(String k:userKeys){
	    			WebSocketSession user = userMap.get(k);
	    			if(user.isOpen()){
						user.sendMessage(message);
	    			}
	    		}
	    	}
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    private static String[] getKeys(Map<String,String> map,String value){
		StringBuilder key=new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if(value.equals(entry.getValue())){
				key.append(entry.getKey()).append(",");
			}
		}
		String[] result = key.deleteCharAt(key.length() - 1).toString().split(",");
		return result;
	}
}
