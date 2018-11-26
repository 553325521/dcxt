package cn.wifiedu.ssm.starpos.pay;


import java.net.URLEncoder;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by bibei on 2017/3/24.
 * 网络参数工具类
 */
public class HttpParamsUtils {

	public static String buildPayParams(Map<String, String> payParams){
		return buildPayParams(payParams,false,false);
	}

	/**
	 * @author
	 * @param payParams
	 * @return
	 */
	public static String buildPayParams(Map<String, String> payParams,boolean encoding){
	       return  buildPayParams(payParams,encoding,false);
	}

	public static String buildPayParams(Map<String, String> payParams,boolean encoding,boolean isSort){
		StringBuilder sb=new StringBuilder();
		List<String> keys = new ArrayList<String>(payParams.keySet());
		if(isSort) {
			Collections.sort(keys);
		}
		for(String key : keys){
			String value = payParams.get(key);
			if(StringUtils.isEmpty(value)){
				continue;
			}

			sb.append(key).append("=");
			String valueStr=value.toString();
			if(encoding){
				sb.append(urlEncode(valueStr,"UTF-8"));
			}else{
				sb.append(valueStr);
			}
			sb.append("&");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public static String buildPayValues(Map<String, Object> payParams, boolean encoding, boolean isSort){
		StringBuilder sb=new StringBuilder();
		List<String> keys = new ArrayList<String>(payParams.keySet());
		if(isSort) {
			Collections.sort(keys);
		}
		for(String key : keys){
			if(key.contains("USER_ID") || key.contains("DCXT_ORDER_FK")) {
				continue;
			}
			String value = (String)payParams.get(key);
			if(StringUtils.isEmpty(value)){
				continue;
			}
			String valueStr=value.toString();
			if(encoding){
				sb.append(urlEncode(valueStr,"GBK"));
			}else{
				sb.append(valueStr);
			}
		}
		sb.setLength(sb.length());
		return sb.toString();
	}


	public static String urlEncode(String str,String enc){
		try {
			return URLEncoder.encode(str, enc);
		} catch (Throwable e) {
			return str;
		}
	}

	public static Map<String, String> paraEmptyFilter(Map<String, String> sArray) {
		Map<String, String> result = new HashMap<String, String>(sArray.size());
		if (sArray == null || sArray.size() <= 0) {
			return result;
		}
		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (StringUtils.isBlank(value)) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}

}
