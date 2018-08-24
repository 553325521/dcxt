package cn.wifiedu.ssm.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * 
 * @author lps
 * @Description:	JSSDK 相关
 *
 */
public class WXJSUtil {
	
	private static final String INVOKING_URL = "http://m.ddera.com/";
	
	
	public static Map getWxConfigMess(Map<String, Object> map){
		try {
			Map newMap = new HashMap<String, String>();
			
			if(!map.containsKey("noncestr")){
				newMap.put("noncestr", getRandomString());
			}else{
				newMap.put("noncestr", map.get("noncestr"));
			}
			if(!map.containsKey("timestamp")){
				newMap.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			}else{
				newMap.put("timestamp", map.get("timestamp"));
			}
			if(!map.containsKey("url")){
				newMap.put("url", INVOKING_URL);
			}else{
				newMap.put("url", map.get("url"));
			}
			newMap.put("jsapi_ticket",  map.get("jsapi_ticket"));
			
			//排序进行微信签名
			StringBuilder sb=new StringBuilder();
			List<String> keys = new ArrayList<String>(newMap.keySet());
			Collections.sort(keys);
			
			for(String key : keys){
				Object value=newMap.get(key);
				if(StringUtils.isEmpty(value)){
					continue;
				}
	
				sb.append(key).append("=");
				String valueStr=value.toString();
				sb.append(valueStr);
				sb.append("&");
			}
			sb.setLength(sb.length() - 1);
			//SHA1签名
			String signature = SHA1(sb.toString());
			newMap.remove("jsapi_ticket");
			newMap.remove("url");
			newMap.put("signature", signature);
			return newMap;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

	/**
	 * 
	 * @author lps
	 * 
	 * @Description: SHA1签名
	 * @param str
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @return String 
	 *
	 */
	public static String SHA1(String str) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(str.getBytes());
		byte[] digest = md.digest();

		StringBuffer hexstr = new StringBuffer();
		String shaHex = "";
		for (int i = 0; i < digest.length; i++) {
			shaHex = Integer.toHexString(digest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexstr.append(0);
			}
			hexstr.append(shaHex);
		}
		return hexstr.toString();
	}
	
	
	//获取随机字符串
	public static String getRandomString(){
		try {
			return SHA1(System.currentTimeMillis()+""+Math.random()).substring(0, 15);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	public static void main(String[] args) {
//		Map map = new HashMap<String, String>();
//		map.put("jsapi_ticket", "gdzxN2g622vVfv3lEXnBsnhKVsdgGwRkGg6in-fSSz-26Nbn8uj-bhga4qMbxAoNLiLo7cVG4FBryOleP7LB2w");
//		map.put("timestamp", 1535097394);
//		map.put("noncestr", "8b80fb540a17c12");
//		System.out.println(getWxConfigMess(map));
//	}
}
