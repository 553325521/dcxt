package cn.wifiedu.ssm.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.wifiedu.ssm.controller.WxController;

public class WxUtil {
	private static final String appid = CommonUtil.getPath("AppID").toString();
	private static final String secret = CommonUtil.getPath("AppSecret").toString();
	
	/**
	 * 设置微信token
	 * @return
	 */
	public static boolean setToken() {
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
}
