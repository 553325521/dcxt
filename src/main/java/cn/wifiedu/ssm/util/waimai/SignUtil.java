package cn.wifiedu.ssm.util.waimai;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class SignUtil {
    /**
     * 根据参数获取签名
     *
     * @param
     * @return String
     */
    public static String getSign(Map<String, Object> data, Map<String, Object> config) {
        TreeMap<String, Object> arr = new TreeMap<String, Object>();
        arr.put("body", data.get("body"));
        arr.put("cmd", data.get("cmd"));
        arr.put("encrypt", "");
        arr.put("secret", config.get("secret"));
        arr.put("source", data.get("source"));
        arr.put("ticket", data.get("ticket"));
        arr.put("timestamp", data.get("timestamp"));
        arr.put("version", data.get("version"));
        StringBuilder strSignTmp = new StringBuilder("");
        Iterator it = arr.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            strSignTmp.append(key + "=" + arr.get(key) + "&");
        }
        String strSign = strSignTmp.toString().substring(0, strSignTmp.length() - 1);
        String sign = getMD5(strSign.toString());
        return sign;
    }

    /**
     * 校验签名是否正确
     *
     * @param
     * @return boolean
     */
    public static boolean checkSign(Map<String, Object> data, Map<String, Object> config) {
        String signFrom = getSign(data, config);
        String sign = data.get("sign").toString();
        if (signFrom.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取MD5
     *
     * @param
     * @return String
     */
    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * 
     * @author lps
     * @date 2018年10月17日 上午1:22:21 
     * 
     * @description: 获取ticket
     * @return String
     */
    public static String getTicket() {
        String md5 = getMD5(System.currentTimeMillis() +""+ new Random(Integer.MAX_VALUE).nextInt(Integer.MAX_VALUE));
        
        return new StringBuilder().append(md5.substring(0, 8)).append("-").append(md5.substring(8, 12))
        		.append("-").append(md5.substring(12, 16)).append("-").append(md5.substring(16, 20)).append("-").append(md5.substring(20)).toString();
    }
    
    
    
    public static void main(String[] args) {
    	System.out.println(getTicket());
    	
    	
    	
    	
    	
    	Map map = new HashMap();
////    	map.put("body", "{\"baidu_shop_id\":\"2234098958\"}");
////    	map.put("body", "{\"baidu_shop_id\":\"2234098958\",\"business_time\":[{\"start\":\"00:00\",\"end\":\"23:59\"}]}");
////    	map.put("body", "{\"baidu_shop_id\":2234098958,\"platformFlag\":\"2\",\"name\":\"王景龙大排挡\"，\"business_time[]\":\"[\"start\":\"00:00\",\"end\":\"23:56\"]\"}");
    	map.put("body", "{\"order_id\":\"15397181282021\"}");
//    	//{\"delivery_fee\":\"0\",\"min_order_price\":\"0\",\"delivery_time\":\"60\",\"min_buy_free\":\"0\",\"name\":\"玩玩\"}
    	map.put("cmd","order.create");
//    	//，"business_time[]":["start":"00:00","end":"23:56"]	，\"business_time[]\":[\"start\":\"00:00\",\"end\":\"23:56\"]	
    	map.put("source","62863");
    	map.put("ticket","CBB291F6-33BE-57CC-8FE3-441FE6E7BA6C");
    	map.put("timestamp","1539938842");
    	map.put("version","3");
    	map.put("encrypt", "");
    	Map map2 = new HashMap();
    	map2.put("secret","ab1a243587a5c2bd");
    	String sign = getSign(map,map2);
    	System.out.println(map);
    	System.out.println(sign);
    	map.put("sign", sign);
    	
    	System.out.println(SignUtil.checkSign(map, map2));
    	
	}
}











