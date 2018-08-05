package cn.wifiedu.ssm.util.redis;

/**
 * 
 * @ClassName: RedisConstants
 * @Description: redis静态变量
 * @author kqs
 * @date 2018-04-12 16:10:13
 * @version : 1.0
 */
public class RedisConstants {

	public final static String REDIS_USER_SESSION_KEY = "redis_user_session_key:";// 存储用户信息

	public final static String WX_REFRESH_TOKEN = "wx_refresh_token:";// 用户刷新access_token
	
	public final static String WX_ACCESS_TOKEN = "wx_access_token:";// 网页授权接口调用凭证
	
	public final static String WX_COMPONENT_ACCESS_TOKEN = "wx_component_access_token:";

	public static final String WX_COMPONENT_PRE_AUTH_CODE = "wx_pre_auth_code:";
}
