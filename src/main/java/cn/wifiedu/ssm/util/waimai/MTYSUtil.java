package cn.wifiedu.ssm.util.waimai;

import java.security.MessageDigest;

public class MTYSUtil {
	
	
	/**
	 * 
	 * @author lps
	 * @date Jan 5, 2019 3:52:52 PM 
	 * 
	 * @description: 获取门店映射URL
	 * @return String
	 */
	public static String getYSUrl(String shopId, String shopName) {
		
		String timestamp = System.currentTimeMillis()+"";
		
		String signBefore = "hy6gv1gonp0r0kuqbusinessId2charsetUTF-8developerId101471ePoiId"+shopId+"ePoiName"+shopName+"timestamp"+timestamp;
		String sign = sha1Encrypt(signBefore);
		
		return "https://open-erp.meituan.com/storemap?charset=UTF-8&developerId=101471&businessId=2&ePoiId="+shopId+"&ePoiName="+shopName+"&timestamp="+timestamp+"&sign="+sign;
	}
	
	/**
	 * 
	 * @author lps
	 * @date Jan 5, 2019 3:57:03 PM 
	 * 
	 * @description: 门店解绑url
	 * @return String
	 */
	public static String getcancalYSUrl(String appAuthToken) {
		return "https://open-erp.meituan.com/releasebinding?signKey=hy6gv1gonp0r0kuq&businessId=2&appAuthToken="+appAuthToken;
	}
	
	
    /**
     * 使用SHA1算法对字符串进行加密
     * @param str
     * @return
     */
    public static String sha1Encrypt(String str) {

        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };

        try {

            MessageDigest mdTemp = MessageDigest.getInstance("SHA-1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(buf);

        } catch (Exception e) {
            return null;
        }
    }

	public static void main(String[] args) {
		System.out.println(sha1Encrypt("2d11ab8eappAuthToken74d301d082f366b730043dc0a55b403ac0f7ac9e63c93cedd3ab9759a06a1bbf3fc953de835ccharsetUTF-8orderId12341234timestamp1502357030327"));
	}
}
