package cn.wifiedu.ssm.util;

/**
 * 
 * @ClassName: Constants
 * @Description: 微信开发接口返回状态编码静态变量
 * @author kqs
 * @date 2018-07-23 23:50:52
 * @version : 1.0
 */
public class WxConstants {

	// 常用
	public final static String ERRORCODE_0 = "0";
	public final static String ERRORCODE_0_MSG = "操作成功";
	public final static String ERRORCODE_1 = "-1";
	public final static String ERRORCODE_1_MSG = "系统繁忙";

	// 用户标签相关
	public final static String ERRORCODE_45157 = "45157";
	public final static String ERRORCODE_45157_MSG = "标签名非法，请注意不能和其他标签重名";
	public final static String ERRORCODE_45158 = "45158";
	public final static String ERRORCODE_45158_MSG = "标签名长度超过30个字节";
	public final static String ERRORCODE_45056 = "45056";
	public final static String ERRORCODE_45056_MSG = "创建的标签数过多，请注意不能超过100个";
	public final static String ERRORCODE_45057 = "45057";
	public final static String ERRORCODE_45057_MSG = "该标签下粉丝数超过10w，不允许直接删除";
	public final static String ERRORCODE_45058 = "45058";
	public final static String ERRORCODE_45058_MSG = "不能修改0/1/2这三个系统默认保留的标签";
	public final static String ERRORCODE_45159 = "45159";
	public final static String ERRORCODE_45159_MSG = "非法的标签";
	public final static String ERRORCODE_45059 = "45059";
	public final static String ERRORCODE_45059_MSG = "有粉丝身上的标签数已经超过限制，即超过20个";
	public final static String ERRORCODE_40003 = "40003";
	public final static String ERRORCODE_40003_MSG = "传入非法的openid";
	public final static String ERRORCODE_49003 = "49003";
	public final static String ERRORCODE_49003_MSG = "传入的openid不属于此AppID";
	public final static String ERRORCODE_40032 = "40032";
	public final static String ERRORCODE_40032_MSG = "每次传入的openid列表个数不能超过50个";
	public static final String ERRORCODE_50005 = "50005";
	
}
