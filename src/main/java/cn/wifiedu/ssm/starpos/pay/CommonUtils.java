package cn.wifiedu.ssm.starpos.pay;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import cn.wifiedu.ssm.util.DateUtil;

import java.util.Date;

/**
 * Created by bibei on 2017/6/23.
 */
public class CommonUtils {

	/*生成nonceStr
	 */
	public static String generateNonceStr(){
		return DateFormatUtils.format(new Date(),DateUtil.TIMESTAMP_FOMART_OTHER) + RandomStringUtils.random(4, true, true);
	}

	public static String getRefunfOrder(){
		return "R"+getCommonOrder();
	}
	public static String getPayOrder(){
		return "P"+getCommonOrder();
	}

	public static String getCommonOrder() {
		String  rannum= RandomStringUtils.random(5,false,true);// 获取随机数
		String nowTimeStr = DateFormatUtils.format(new Date(),DateUtil.TIMESTAMP_FOMART_OTHER) ;// 当前时间
		return nowTimeStr+rannum;
	}
	
}
