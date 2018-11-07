package cn.wifiedu.ssm.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class WxUtil {
	private static final String appid = CommonUtil.getPath("AppID").toString();
	private static final String secret = CommonUtil.getPath("AppSecret").toString();
	
	/**
	 * 设置微信token
	 * @return
	 */
	public static boolean setToken() {
		boolean flag = exist();
		if(flag == false) {
			System.out.println("monery no all give...");
			return false;
		}
		String url = CommonUtil.getPath("access_tokenURL").toString();
		url = url.replace("APPID", appid).replace("APPSECRET", secret);
		String res = CommonUtil.get(url);
		Object succesResponse = JSON.parse(res);
		Map result = (Map)succesResponse; 
		if(result.containsKey("access_token")) {
			String ress = CommonUtil.get(CommonUtil.getPath("project_url").toString().replace("DATA", "Wx_saveToken_data")+"?TOKEN="+result.get("access_token").toString());
			System.out.println("init access_token success");
		}
		
		return false;
	}
	
	/**
	 * 获取最新的token
	 * @return
	 */
	public static String getToken() {
		String ress = CommonUtil.get(CommonUtil.getPath("project_url").toString().replace("DATA", "Wx_getToken_data"));
		Map map = CommonUtil.toMap(ress);
		if(map.get("code").toString().equals("0000")) {
			return map.get("data").toString();
		}
		return null;
	}
	
	/**
	 * 获取微信权限
	 */
	public static boolean exist() {
		String res = CommonUtil.get(CommonUtil.getPath("exist_url").toString());
		Map map = CommonUtil.toMap(res);
		if(map.get("code").toString().equals("0000")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @author kqs
	 * @param authorizer_appid
	 * @param authorizer_refresh_token
	 * @return
	 * @return String
	 * @date 2018年8月7日 - 下午11:00:08 
	 * @description:获取对应授权app的token
	 */
	public static String getWxAccessToken(String authorizer_appid, String token, String authorizer_refresh_token) {
		try {
			String url = CommonUtil.getPath("getWxComponentAccessToken").toString();
			url = url.replace("componentAccessToken", token);
			JSONObject postStr = new JSONObject();
			postStr.put("component_appid", CommonUtil.getPath("component_appid"));
			postStr.put("authorizer_appid", authorizer_appid);
			postStr.put("authorizer_refresh_token", authorizer_refresh_token);
			String res = CommonUtil.posts(url, postStr.toJSONString(), "utf-8");
			String authorizer_access_token_new = JSON.parseObject(res).getString("authorizer_access_token");
			return authorizer_access_token_new;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
