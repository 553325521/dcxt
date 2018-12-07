package cn.wifiedu.ssm.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;

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
    
    
    private static Map<String,Map<String,WebSocketSession>> shopUsersSessionMap = new Hashtable<String,Map<String,WebSocketSession>>();
    
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
    	JSONObject msgJson = JSONObject.parseObject(schatMessage);
    	//连接成功后，接收客户端发来的用户和商铺信息
    	if(msgJson.containsKey("msgType") && msgJson.get("msgType").toString().equals("0")){
    		JSONObject msgContent = (JSONObject)msgJson.get("msgContent");
    		Map<String,WebSocketSession> userMap = new Hashtable<String, WebSocketSession>();
    		if(msgContent.containsKey("shopid") && msgContent.get("shopid") != null){
    			if(shopUsersSessionMap.containsKey(msgContent.get("shopid"))){
    				userMap = shopUsersSessionMap.get(msgContent.get("shopid"));
    			}
    			shopUsersSessionMap.put(msgContent.get("shopid").toString(), userMap);
    		}
    		if(msgContent.containsKey("openid") && msgContent.get("openid") != null){
    			userMap.put(msgContent.get("openid").toString(), session);
    		}
    		
    		sendMessageToUser(msgContent.getString("shopid"),msgContent.getString("openid"),new TextMessage("123"));
    	}
    	logger.info("店员端在线数量:"+shopUsersSessionMap.size());
    	 logger.info("用户输入:" + schatMessage);
        
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    	
    	String shopKey = "";
    	String openKey = "";
    	for(String key : shopUsersSessionMap.keySet()){
    		Map<String,WebSocketSession> mapValue = shopUsersSessionMap.get(key);
    		for(String openIdKey:mapValue.keySet()){
    			WebSocketSession currentSession = mapValue.get(openIdKey);
    			if(currentSession == session){
    				shopKey = key;
    				openKey = openIdKey;
    				break;
    			}
    		}
    		if(!shopKey.equals("") && !openKey.equals("")){
    			break;
    		}
    	}
    	if(!shopKey.equals("") && !openKey.equals("")){
    		shopUsersSessionMap.get(shopKey).remove(openKey);
    	}
        if (session.isOpen()) {
            session.close();
        }
        logger.info("因传输错误退出websocket");
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
    	 
    	String shopKey = "";
    	String openKey = "";
    	for(String key : shopUsersSessionMap.keySet()){
    		Map<String,WebSocketSession> mapValue = shopUsersSessionMap.get(key);
    		for(String openIdKey:mapValue.keySet()){
    			WebSocketSession currentSession = mapValue.get(openIdKey);
    			if(currentSession == session){
    				shopKey = key;
    				openKey = openIdKey;
    				break;
    			}
    		}
    		if(!shopKey.equals("") && !openKey.equals("")){
    			break;
    		}
    	}
    	if(!shopKey.equals("") && !openKey.equals("")){
    		shopUsersSessionMap.get(shopKey).remove(openKey);
    	}
    	if (session.isOpen()) {
            session.close();
        }
    	 logger.info("安全退出");
    }
    
    /**
     * 给所有的用户发送消息
     */
    public void sendMessagesToUsers(TextMessage message) {
    	
    	for(String key : shopUsersSessionMap.keySet()){
    		Map<String,WebSocketSession> mapValue = shopUsersSessionMap.get(key);
    		for(String openIdKey:mapValue.keySet()){
    			WebSocketSession currentSession = mapValue.get(openIdKey);
    			  // isOpen()在线就发送
                if (currentSession.isOpen()) {
                	try {
						currentSession.sendMessage(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
    		}
    	}
    }
    
    /**
     * 更新订单数量和数据发送消息给指定的用户
     */
    public void sendMessageToUser(String shopId,String openId, TextMessage message) {
    	try {
    		if(shopUsersSessionMap.containsKey(shopId) && shopUsersSessionMap.get(shopId).containsKey(openId)){
    			WebSocketSession session = shopUsersSessionMap.get(shopId).get(openId);
    			if(session.isOpen()){
    				session.sendMessage(message);
    			}else{
    				logger.info("当前用户不在线");
    			}
    		}else{
    			logger.info("当前用户不在线");
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
}
