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
}
