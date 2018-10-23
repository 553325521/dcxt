package cn.wifiedu.ssm.util.waimai;
//import com.google.gson.Gson;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import cn.wifiedu.ssm.util.CommonUtil;

public class EBWaiMai {
	
	public static String ebUrl = "https://api-be.ele.me";
	/**
	 * 
	 * @author lps
	 * @date 2018年10月17日 下午5:25:57 
	 * 
	 * @description: 初始化请求参数并签名
	 * @return String
	 */
    public static String initParams(Map<String, Object> initMap) {
        Map<String, Object> params = new HashMap<>();
        params.putAll(initMap);
        params.put("encrypt", "");//如果encrypt没有，直接用""，不要用null表示
        Map map2 = new HashMap();
    	map2.put("secret","ab1a243587a5c2bd");
        params.put("source", "62863");
        params.put("ticket", SignUtil.getTicket());
        params.put("version", "3");
        params.put("timestamp", System.currentTimeMillis()/1000);//当前时间戳

        String sign = SignUtil.getSign(params,map2);
        params.put("sign", sign);
       
        StringBuilder requestparams = new StringBuilder();
        for (Map.Entry<String, Object> map : params.entrySet()) {
            requestparams.append(map.getKey()+"="+map.getValue() + "&");
        }
        return requestparams.substring(0, requestparams.length() - 1).toString();
    }

  
    /**
     * 中文转unicode,如果参数中有中文，需要转一下
     *
     * @param gbString
     * @return
     */
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }
    
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午2:45:26 
     * 
     * @description: 查询订单详情
     * @return String
     * @throws Exception 
     */
    public static String EBOrderGet(String orderId) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.get");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午2:46:46 
     * 
     * @description: 确认订单
     * @return String
     * @throws Exception 
     */
    public static String EBOrderConfirm(String orderId) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.confirm");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 下午6:45:10 
     * @param 	orderId : 饿百订单id，必须
     * 			type : 取消原因分类	1	不在配送范围内
									2	餐厅已打烊
									3	美食已售完
									4	菜品价格发生变化
									5	用户取消订单
									6	重复订单
									7	餐厅太忙
									8	联系不上用户
									9	假订单
									53	API商户系统向门店推送订单失败
									-1	自定义输入
     * 			reason : 取消原因描述			
     * 
     * @description: 取消订单
     * @return String
     * @throws Exception 
     */
    public static String EBOrderCancel(String orderId, String type, String reason) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.cancel");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	body.put("type", "1");
//    	body.put("reason", orderId);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午3:01:31 
     * @param 	orderId : 订单id,必须
     * 			phone : 配送员电话，为空取商家联系电话
     * @description: 订单送出（自配送）
     * @return String
     * @throws Exception 
     */
    public static String EBOrderSendout(String orderId, String phone) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.sendout");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	if(StringUtils.isNotEmpty(phone)) {
    		body.put("phone", phone);
    	}
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午3:04:56 
     * @param 	orderId : 订单id,必须
     * 			phone : 配送员电话，为空取商家联系电话
     * @description: 订单送达（自配送）
     * @return String
     * @throws Exception 
     */
    public static String EBOrderComplete(String orderId, String phone) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.complete");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	if(StringUtils.isNotEmpty(phone)) {
    		body.put("phone", phone);
    	}
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午3:19:29 
     * @param 	orderId : 订单id,必须
     * @description: 同意用户取消订单
     * @return String
     * @throws Exception 
     */
    public static String EBOrderAgreerefund(String orderId) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.agreerefund");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午3:19:29 
     * @param 	orderId : 订单id,必须
     * 			 refuse_reason : 拒绝原因,不超过100字
     * @description: 拒绝用户取消订单
     * @return String
     * @throws Exception 
     */
    public static String EBOrderDisagreerefund(String orderId, String reason) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.disagreerefund");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	body.put("refuse_reason", reason);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午3:48:27 
     * @param 	orderId : 订单id,必须
     * @description: 呼叫配送
     * @return String
     */
    public static String EBOrderCallDelivery(String orderId) {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.callDelivery");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	String result = HttpRequestUtil.sendPost(ebUrl, initParams);
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午3:49:43 
     * @param 	orderId : 订单id,必须
     * @description: 取消呼叫配送
     * @return String
     * @throws Exception 
     */
    public static String EBOrderCancelDelivery(String orderId) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.cancelDelivery");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午3:50:53 
     * @param 	orderId : 订单id,必须
     * @description: 获取配送费
     * @return String
     * @throws Exception 
     */
    public static String EBOrderGetDeliveryFeeForCrowd(String orderId) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.getDeliveryFeeForCrowd");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
   /**
    *  
    * @author lps
    * @date 2018年10月17日 上午3:53:55 
    * @param 	orderId : 订单id,必须
    * @description: 获取订单配送信息
    * @return String
 * @throws Exception 
    */
    public static String EBOrderDeliveryGet(String orderId) throws Exception {
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("cmd", "order.delivery.get");
    	
    	//在body中添加需要输入的参数
    	Map<String, Object> body = new HashMap<>();
    	body.put("order_id", orderId);
    	map.put("body", body);
    	map.put("body",JSON.toJSON(map.get("body")));
    	
    	String initParams = initParams(map);
    	
    	String result = CommonUtil.posts(ebUrl, initParams,"UTF-8");
    	System.out.print(result);
    	
    	return result;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
    	try {
	    	String orderId = "15398542101242";
	//    	EBOrderConfirm(orderId);//确认订单ß
	//    	EBOrderCancel(orderId);//取消订单
	//    	EBOrderSendout(orderId,"");//订单送出  -- 只支持饿了么
	//    	EBOrderComplete(orderId, "17865218840"); //-- 只支持饿了么
	//    	EBOrderAgreerefund(orderId);//同意用户取消
	//    	EBOrderDisagreerefund(orderId, "不可能");//拒绝用户取消
	//      String requestParams = initParams();
	//      String result = HttpRequestUtil.sendPost(ebUrl, requestParams);
	//      System.out.print(result);
    	
			System.out.println(EBOrderGet(orderId));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}


