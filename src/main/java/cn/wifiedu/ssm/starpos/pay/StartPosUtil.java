package cn.wifiedu.ssm.starpos.pay;

public class StartPosUtil {
	
	public static String checkPayWay(String userAgent) {
		if (userAgent != null && userAgent.contains("AlipayClient")) {
			return StarPosPay.PAY_CHANNEL_ALIPAY;
		}else if (userAgent != null && userAgent.contains("MicroMessenger")) {
			return StarPosPay.PAY_CHANNEL_WEIXIN;
		}
		return "";
	}
}
