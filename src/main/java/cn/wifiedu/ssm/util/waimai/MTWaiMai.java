package cn.wifiedu.ssm.util.waimai;

import com.alibaba.fastjson.JSON;

import cn.wifiedu.ssm.controller.WaiMaiController;
import cn.wifiedu.ssm.util.CommonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

/*
 *
  Created by zhangyuanbo02 on 15/12/9.
 */
public class MTWaiMai{
	
	// 美团外卖 APP ID 
	private final static String appid = "3987";
	// 美团外卖APP Secret
	private final static String appSecret = "ff17fa297a9a89ffa62be0ff9ebf92bf";
	
	// 美团外卖请求的Base url
	private final static String MTWMUrl = "https://waimaiopen.meituan.com/api/v1/";
	
	/**
	 * 门店类
	 */
	// 	门店设置为上线状态
	private final static String poiOnline = "poi/online";
	
	// 营业状态
	private final static String poiOpen = "poi/open";
	
	
	/**
	 * 订单类
	 */
	
	// 商家订单确认url	 (必接)						
	private final static String OrderConfirmUrl = "order/confirm";
	// 商家取消订单url （必接）
	private final static String OrderCancelUrl = "order/cancel";
	// 订单确认退款请求。（必接）
	private final static String OrderRefundAgree = "order/refund/agree";
	// 通过接口驳回订单退款申请。（必接）
	private final static String OrderRefundReject = "order/refund/reject";
	// 拉取用户真实手机号。（必接）
	private final static String OrderBatchPullPhoneNumber = "order/batchPullPhoneNumber";
	// 商家确认已完成出餐
	private final static String OrderPreparationMealComplete = "order/preparationMealComplete";
	// 订单配送中
	private final static String OrderDelivering = "order/delivering";
	// 订单已送达
	private final static String OrderArrived = "order/arrived";
	// 下发美团配送订单
	private final static String OrderLogisticsPush = "order/logistics/push";
	// 取消美团配送订单
	private final static String OrderLogisticsCancel = "order/logistics/cancel ";
	// 美团外卖订单推送url
	private final static String ORDER_ACCEPTORDER = "order/acceptOrder";
	// 订单配送中 (已下发美团无需使用)
	private final static String orderDelivering = "order/delivering";
	// 订单已送达
	private final static String orderArrived = "order/arrived";
	// 众包发配送
	private final static String zhongbaoDispatch = "zhongbao/dispatch";
	
	
	private final static String appPoiCode = "ceshi_POI_II";


    
	
    public static Map<String, String> poiSave(String appPoiCode) {
    	return null;
    }
    
    /**
	 * 
	 * @author lps
	 * @date Mar 29, 2019 9:47:34 PM 
	 * 
	 * @description: 门店置位营业状态
	 * @return Map<String,String>
	 */
    // 门店上线
    public static Map<String, String> poiOnline(String appPoiCode) {
    	String url = MTWMUrl + poiOnline + "?";
    	String signUrl = MTSignUtil(url + "app_poi_code=" + appPoiCode);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
   }
    
    
    public static Map<String, String> poiOpen(String appPoiCode) {
    	String url = MTWMUrl + poiOpen + "?";
    	String signUrl = MTSignUtil(url + "app_poi_code=" + appPoiCode);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    }

    /**
     * 
     * @author lps
     * @date Mar 26, 2019 12:25:20 AM 
     *  
     * @description: 商家确认订单
     * @return void
     */
    public static Map<String, String> orderConfirm(String orderId) {
    	String url = MTWMUrl + OrderConfirmUrl + "?";
    	String signUrl = MTSignUtil(url + "order_id=" + orderId);
    	
    	System.out.println(url + signUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    }
    
    /**
     * 
     * @author lps
     * @date Mar 26, 2019 11:27:12 PM 
     * 
     * @description:  商家取消订单     orderId: 订单id		reason: 取消原因		reasonCode: 规范化取消原因code 
     * 
     * 	取消订单原因code列表
		美团发送的取消原因列表
		
		原因code
		原因描述
		1001	系统取消，超时未确认
		1002	系统取消，在线支付订单15分钟未支付
		1101	用户取消，在线支付中取消
		1102	用户取消，商家确认前取消
		1103	用户取消，用户退款取消
		1201	客服取消，用户下错单
		1202	客服取消，用户测试
		1203	客服取消，重复订单
		1204	客服取消，其他原因
		1301	其他原因
     */
    public static Map<String, String> orderCancel(String orderId, String reason, String reasonCode) {
    	String url = MTWMUrl + OrderCancelUrl + "?";
    	String paramUrl = new StringBuffer().
    			append(url).
    			append("order_id=").append(orderId)
    			.append("&reason=").append(reason)
    			.append("&reasonCode=").append(reasonCode)
    			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    }

    
    // 订单送达
    public static Map<String, String> orderArrived(String orderId) {
    	String url = MTWMUrl + OrderArrived + "?";
    	String paramUrl = new StringBuffer().
    			append(url).
    			append("order_id=").append(orderId)
    			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	System.out.println(paramUrl);
    	System.out.println(url + signUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    	
   }
    
    // 订单配送中
    public static Map<String, String> orderDelivering(String orderId) {
    	String url = MTWMUrl + OrderDelivering + "?";
    	String paramUrl = new StringBuffer().
    			append(url).
    			append("order_id=").append(orderId)
    			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    	
   }
    
    // 商家确认已完成出餐
    public static Map<String, String> orderPreparationMealComplete(String orderId) {
    	String url = MTWMUrl + OrderPreparationMealComplete + "?";
    	String paramUrl = new StringBuffer().
    			append(url).
    			append("order_id=").append(orderId)
    			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    	
   }
    
    // 订单确认退款请求
    public static Map<String, String> orderRefundAgree (String orderId, String reason) {
    	String url = MTWMUrl + OrderRefundAgree + "?";
    	String paramUrl = new StringBuffer().
    			append(url).
    			append("order_id=").append(orderId)
    			.append("&reason=").append(reason)
    			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    }
    
    // 驳回订单退款申请
    public static Map<String, String> orderRefundReject(String orderId, String reason) {
    	String url = MTWMUrl + OrderRefundAgree + "?";
    	String paramUrl = new StringBuffer().
    			append(url).
    			append("order_id=").append(orderId)
    			.append("&reason=").append(reason)
    			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    }
    
    // 下发美团配送订单
    public static Map<String, String> orderLogisticsPush(String orderId) {
    	String url = MTWMUrl + OrderLogisticsPush + "?";
    	String paramUrl = new StringBuffer().
			append(url).
			append("order_id=").append(orderId)
			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    }
    
    // 取消美团配送订单
    public static Map<String, String> orderLogisticsCancel(String orderId) {
    	String url = MTWMUrl + OrderLogisticsCancel + "?";
    	String paramUrl = new StringBuffer().
    			append(url).
    			append("order_id=").append(orderId)
    			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    }
    


    
    // 订单配送中
  /*  public static Map<String, String> orderDelivering(String orderId, String courierName, String courierPhone) {
    	String url = MTWMUrl + orderDelivering + "?";
    	String paramUrl = new StringBuffer().
    			append(url).
    			append("order_id=").append(orderId)
    			.toString();
    	String signUrl = MTSignUtil(paramUrl);
    	String string = CommonUtil.get(url + signUrl);
    	
    	return (Map<String, String>) JSON.parse(string);
    }*/
    
    
    // 推送订单接口验证
    public static boolean orderAcceptOrderValidation(Map<String, Object> map) {
    	return PushSigValidation(ORDER_ACCEPTORDER, map);
    }
    
    
    /**
     * 美团签名
     * 
     * 计算签名
		在调用接口进行编码前，开发者需先计算签名，计算方式如下：
		
		将所有参数（sig除外）按照参数名的字母顺序排序，并用&连接： app_id=1235123121&app_poi_code=31&timestamp=1389751221
		按照请求url + ? + 排序后的参数 + consumer_secret的顺序进行连接，得到加密前的字符串: http://waimaiopen.meituan.com/api/v1/poi/mget?app_id=1235123121&app_poi_codes=31&timestamp=1389751221d31ba58fd73c71db697ab5e4946d52d
		
		注：参数中包含中文时，处理方法：中文保持原文即可，无需对其单独转码，示例(参数未列全部)如下： http://waimaiopen.meituan.com/api/v1/poi/save?app_id=1235123121&app_poi_code=31&name=丽华快餐&address=北苑路北站K酷时代广场4层&timestamp=1389751221d31ba58fd73c71db697ab5e4946d52d
		
		对加密前的字符串进行MD5加密，得到签名：00934d00d0aea6f12161edfb6456143d
		将得到的签名赋给sig作为请求参数：http://waimaiopen.meituan.com/api/v1/poi/mget?app_poi_codes=31&app_id=1235123121& timestamp=1389751221&sig=5c29938735e259f287480c2bbaaf2c18
		POST、GET及其他请求方式都需使用上述方式计算签名。
     */
    /**
     * 
     * @author lps
     * @date Mar 25, 2019 11:30:57 PM 
     * 
     * @description: //签名
     * @return String
     */
    public static String MTSignUtil (String url) {
    	
    	String[] httpUrl = url.split("\\?");
    	
    	String[] strings = httpUrl[1].split("&");
    	
    	Set<String> set = new TreeSet<>();
    	
    	for (int i = 0; i < strings.length; i++) {
    		set.add(strings[i]);
    	}
    	
    	set.add("app_id=" + appid);
    	String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
    	set.add("timestamp=" + timestamp);
    	
    	StringBuilder sb = new StringBuilder();
    	
    	Iterator<String> iterator = set.iterator();
    	
    	while (iterator.hasNext()) {
    		sb.append(iterator.next()).append("&");
    	}
    	System.out.println("加密时候: " + httpUrl[0] + "?" + sb.toString().substring(0, sb.toString().length()-1)+ appSecret);
    	String md5 = SignUtil.getMD5(httpUrl[0] + "?" + sb.toString().substring(0, sb.toString().length()-1) + appSecret);
    	sb = new StringBuilder();
    	
    	iterator = set.iterator();
    	
    	while (iterator.hasNext()) {
    		sb.append(iterator.next()).append("&");
    	} 	
    	sb.append("sig=").append(md5.toLowerCase());
    	
    	return sb.toString();
    }

    /**
     * 推送订单签名验证
     */
    public static boolean PushSigValidation(String url, Map<String, Object> map) {
    	if (!map.containsKey("sig")) {
    		return false;
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	Set<String> set = new TreeSet<>();
    	set.addAll(map.keySet());
    	Iterator<String> iterator = set.iterator();
    	while(iterator.hasNext()) {
    		String key = iterator.next();
    		if ("OperatingSystem".equals(key) || "sig".equals(key) || "AccessIp".equals(key) || "token".equals(key) || "Browser".equals(key) || "userInfo".equals(key) || "sessionId".equals(key)) {
    			continue;
    		}
    		sb.append(key).append("=").append(map.get(key)).append("&");
    	}
    	sb.insert(0, "?").insert(0, url);
    	
    	
    	String substring = sb.substring(0, sb.length() - 1);
    	
    	substring += appSecret;
    	
    	System.out.println(substring);
    	
    	String sign =  SignUtil.getMD5(substring).toLowerCase();
    	
    	System.out.println(sign);
    	
    	if (sign.equals((String)map.get("sig"))) {
    		return true;
    	}
    	
    	return false;
    }

    
    public static void main(String[] args){
    	
    	Map<String, Object> map = new HashMap();
    	String appPoiCode = "2898393";
    	System.out.println(poiOpen(appPoiCode));
//    	System.out.println(orderArrived("28983932771969648"));
    	
    	
    	/**
    	 * ]{OperatingSystem=??, original_price=0.02, userInfo=null, wm_poi_name=t_kys6miJE, backup_recipient_phone=%5B%5D, 
    	 * invoice_title=, latitude=29.77389, extras=%5B%7B%7D%5D, delivery_time=0, is_poi_first_order=false, 
    	 * sig=13d861501a6d19bfbee42cd7919d9e6e, total=0.02, avg_send_time=4220.0, ctime=1553709836, 
    	 * dinners_number=0, pay_type=2, caution=, app_id=3987, timestamp=1553709856, longitude=95.36876, 
    	 * AccessIp=101.236.11.36, utime=1553709836, is_third_shipping=0, logistics_code=0000, shipper_phone=, 
    	 * recipient_address=%E8%89%B2%E9%87%91%E6%8B%89%40%23%E8%A5%BF%E8%97%8F%E8%87%AA%E6%B2%BB%E5%8C%BA%E6%9E%97%E8%8A%9D%E5%B8%82%E5%A2%A8%E8%84%B1%E5%8E%BF%E8%89%B2%E9%87%91%E6%8B%89,
    	 *  sessionId=F91E4F34CD9A1C8A062857C578F66D7C, token=null, shipping_fee=0.01, wm_poi_id=2898393, app_poi_code=2898393, is_favorites=true,
    	 *   has_invoiced=0, taxpayer_id=, day_seq=1, 
    	 *   poi_receive_detail=%7B%22actOrderChargeByMt%22%3A%5B%7B%22comment%22%3A%22%E6%B4%BB%E5%8A%A8%E6%AC%BE%22%2C%22feeTypeDesc%22%3A%22%E6%B4%BB%E5%8A%A8%E6%AC%BE%22%2C%22feeTypeId%22%3A10019%2C%22moneyCent%22%3A0%7D%5D%2C%22actOrderChargeByPoi%22%3A%5B%5D%2C%22foodShareFeeChargeByPoi%22%3A0%2C%22logisticsFee%22%3A1%2C%22onlinePayment%22%3A2%2C%22wmPoiReceiveCent%22%3A2%7D,
    	 *    detail=%5B%7B%22app_food_code%22%3A%2281JP3Ub5ehYXVpZd__865715985%22%2C%22box_num%22%3A1%2C%22box_price%22%3A0%2C%22cart_id%22%3A0%2C%22food_discount%22%3A1%2C%22food_name%22%3A%22MT-05%22%2C%22food_property%22%3A%22%22%2C%22price%22%3A0.01%2C%22quantity%22%3A1%2C%22sku_id%22%3A%223IDJhh9CPqPpgAmc__939716097%22%2C%22spec%22%3A%22%E4%BB%BD%22%2C%22unit%22%3A%221%22%7D%5D, 
    	 *    wm_poi_address=%E5%8D%97%E6%9E%81%E6%B4%B204%E5%8F%B7%E7%AB%99, 
    	 *    recipient_name=%E9%99%86%28%E5%85%88%E7%94%9F%29, order_id=28983933726634026, wm_poi_phone=4009208801, 
    	 *    wm_order_id_view=28983933726634026, city_id=999999, status=2, recipient_phone=17865218840, Browser=??}


delivery_time=0&total=0.02&utime=1553709836&
wm_poi_name=t_kys6miJE&
detail=%255B%257B%2522app_food_code%2522%253A%252281JP3Ub5ehYXVpZd__865715985%2522%252C%2522box_num%2522%253A1%252C%2522box_price%2522%253A0%252C%2522cart_id%2522%253A0%252C%2522food_discount%2522%253A1%252C%2522food_name%2522%253A%2522MT-05%2522%252C%2522food_property%2522%253A%2522%2522%252C%2522price%2522%253A0.01%252C%2522quantity%2522%253A1%252C%2522sku_id%2522%253A%25223IDJhh9CPqPpgAmc__939716097%2522%252C%2522spec%2522%253A%2522%25E4%25BB%25BD%2522%252C%2522unit%2522%253A%25221%2522%257D%255D%2C+wm_poi_address%3D%25E5%258D%2597%25E6%259E%2581%25E6%25B4%25B204%25E5%258F%25B7%25E7%25AB%2599
&caution=null&original_price=0.02&recipient_name=%25E9%2599%2586%2528%25E5%2585%2588%25E7%2594%259F%2529
&order_id=28983933726634026&wm_poi_phone=4009208801&city_id=999999&timestamp=1553712982&pay_type=2
&longitude=95.36876&status=2&invoice_title=null&app_poi_code=2898393&shipper_phone=null
&is_third_shipping=0&ctime=1553709836&shipping_fee=0.01&has_invoiced=0
&extras=%255B%257B%257D%255D&recipient_phone=17865218840
&wm_poi_address=%25E5%258D%2597%25E6%259E%2581%25E6%25B4%25B204%25E5%258F%25B7%25E7%25AB%2599
&wm_order_id_view=28983933726634026&app_id=3987&latitude=29.77389
&recipient_address=%25E8%2589%25B2%25E9%2587%2591%25E6%258B%2589%2540%2523%25E8%25A5%25BF%25E8%2597%258F%25E8%2587%25AA%25E6%25B2%25BB%25E5%258C%25BA%25E6%259E%2597%25E8%258A%259D%25E5%25B8%2582%25E5%25A2%25A8%25E8%2584%25B1%25E5%258E%25BF%25E8%2589%25B2%25E9%2587%2591%25E6%258B%2589
&sig=cf93082126939465946270791efc85d21


https://m.ddera.com/json/mt/test/callback/waimai/order/acceptOrder?app_id=3987&app_poi_code=1&caution=1&city_id=1&ctime=1&delivery_time=1&detail=1&extras=1&has_invoiced=1&invoice_title=1&is_third_shipping=1&latitude=1&longitude=1&order_id=1&original_price=1&pay_type=1&recipient_address=1&recipient_name=1&recipient_phone=1&shipper_phone=1&shipping_fee=1&status=1&timestamp=1553783015&total=1&utime=1&wm_order_id_view=1&wm_poi_address=1&wm_poi_name=1&wm_poi_phone=1ff17fa297a9a89ffa62be0ff9ebf92bf
    	 */
    	/*map.put("original_price", "0.02");
    	map.put("wm_poi_name", "t_kys6miJE");
    	
    	map.put("backup_recipient_phone", "[]");
    	map.put("invoice_title", "");
    	map.put("latitude", "29.77389");
    	
    	map.put("extras", "[{}]");
    	
    	map.put("delivery_time", "0");
    	map.put("is_poi_first_order", "false");
    	map.put("sig", "0be37674272ccaf1d008fe295df1fcde");
    	map.put("total", "0.02");
    	map.put("avg_send_time", "4218.0");
    	map.put("ctime", "1553791926");
    	map.put("dinners_number", "0");
    	
    	map.put("pay_type", "2");
  map.put("caution", "");
    	map.put("app_id", "3987");
    	map.put("timestamp", "1553791944");
    	map.put("longitude", "95.36876");
    	map.put("utime", "1553791926");
    	map.put("is_third_shipping", "0");
    	map.put("logistics_code", "0000");
    	
  map.put("shipper_phone", "");
    	map.put("recipient_address", "色金拉@#西藏自治区林芝市墨脱县色金拉");
    	map.put("shipping_fee", "0.01");
    	map.put("wm_poi_id", "2898393");
    	map.put("app_poi_code", "2898393");
    	map.put("is_favorites", "true");map.put("has_invoiced", "0");
  map.put("taxpayer_id", "");
    	map.put("day_seq", "1");
    	map.put("poi_receive_detail", "{\"actOrderChargeByMt\":[{\"comment\":\"活动款\",\"feeTypeDesc\":\"活动款\",\"feeTypeId\":10019,\"moneyCent\":0}],\"actOrderChargeByPoi\":[],\"foodShareFeeChargeByPoi\":0,\"logisticsFee\":1,\"onlinePayment\":2,\"wmPoiReceiveCent\":2}");
    	map.put("detail", "[{\"app_food_code\":\"81JP3Ub5ehYXVpZd__865715985\",\"box_num\":1,\"box_price\":0,\"cart_id\":0,\"food_discount\":1,\"food_name\":\"MT-05\",\"food_property\":\"\",\"price\":0.01,\"quantity\":1,\"sku_id\":\"3IDJhh9CPqPpgAmc__939716097\",\"spec\":\"份\",\"unit\":\"1\"}]");
    	map.put("wm_poi_address", "南极洲04号站");
//    	map.put("poi_receive_detail", "%7B%22actOrderChargeByMt%22%3A%5B%7B%22comment%22%3A%22%E6%B4%BB%E5%8A%A8%E6%AC%BE%22%2C%22feeTypeDesc%22%3A%22%E6%B4%BB%E5%8A%A8%E6%AC%BE%22%2C%22feeTypeId%22%3A10019%2C%22moneyCent%22%3A0%7D%5D%2C%22actOrderChargeByPoi%22%3A%5B%5D%2C%22foodShareFeeChargeByPoi%22%3A0%2C%22logisticsFee%22%3A1%2C%22onlinePayment%22%3A2%2C%22wmPoiReceiveCent%22%3A2%7D, detail=%5B%7B%22app_food_code%22%3A%2289GphuMetpT8xXGm__865729988%22%2C%22box_num%22%3A1%2C%22box_price%22%3A0%2C%22cart_id%22%3A0%2C%22food_discount%22%3A1%2C%22food_name%22%3A%22MT-02%22%2C%22food_property%22%3A%22%22%2C%22price%22%3A0.01%2C%22quantity%22%3A1%2C%22sku_id%22%3A%225vc4Vu2D1ffcNDSh__939734390%22%2C%22spec%22%3A%22%E4%BB%BD%22%2C%22unit%22%3A%221%E4%BB%BD%22%7D%5D");
    	map.put("recipient_name", "陆(先生)");
    	
    	map.put("order_id", "28983933105028871");
    	map.put("wm_poi_phone", "4009208801");
    	
    	map.put("wm_order_id_view", "28983933105028871");
    	map.put("city_id", "999999");
    	map.put("status", "2");
    	map.put("recipient_phone", "17865218840");
//    	map.put("status", "1");
    	
    	WaiMaiController.encodeURI(map);
    	
    	System.out.println(PushSigValidation(ORDER_ACCEPTORDER, map));*/
    	
    	
//    	System.out.println(MTSignUtil(ORDER_ACCEPTORDER + "?"+"delivery_time=1&total=1&utime=1&wm_poi_name=1&detail=1&caution=1&original_price=1&recipient_name=1&order_id=1&wm_poi_phone=1&city_id=1&timestamp=1553783015&pay_type=1&longitude=1&status=1&invoice_title=1&app_poi_code=1&shipper_phone=1&is_third_shipping=1&ctime=1&shipping_fee=1&has_invoiced=1&extras=1&recipient_phone=1&wm_poi_address=1&wm_order_id_view=1&app_id=3987&latitude=1&recipient_address=1"));
    	
    	
    	
//    	System.out.println(SignUtil.getMD5("https://m.ddera.com/json/mt/test/callback/waimai/order/acceptOrder?app_id=3987&app_poi_code=1&caution=1&city_id=1&ctime=1&delivery_time=1&detail=1&extras=1&has_invoiced=1&invoice_title=1&is_third_shipping=1&latitude=1&longitude=1&order_id=1&original_price=1&pay_type=1&recipient_address=1&recipient_name=1&recipient_phone=1&shipper_phone=1&shipping_fee=1&status=1&timestamp=1553783015&total=1&utime=1&wm_order_id_view=1&wm_poi_address=1&wm_poi_name=1&wm_poi_phone=1ff17fa297a9a89ffa62be0ff9ebf92bf"));
    	
    	// 确认订单
//    	System.out.println(orderConfirm(orderId));
    	
    	// 订单送达
//    	System.out.println(orderArrived(orderId));
    	
    	// 订单取消
//    	System.out.println(orderCancel(orderId, "不想卖了", "1001"));
    	
    	
    	
    	
    	
    	
    	
    }


}
