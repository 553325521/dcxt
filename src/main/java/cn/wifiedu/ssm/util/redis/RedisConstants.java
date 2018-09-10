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
	
	public final static String REDIS_USER_SHOP_SESSION_KEY = "redis_user_shop_session_key:";// 存储用户对应的唯一商铺信息
	
	public final static String REDIS_USER_SHOP_LIST_KEY = "redis_user_shop_list_key:";// 存储用户对应的所有商铺信息
	
	public final static String WX_REFRESH_TOKEN = "wx_refresh_token:";// 用户刷新access_token
	
	public final static String WX_ACCESS_TOKEN = "wx_access_token:";// 网页授权接口调用凭证
	
	public final static String WX_COMPONENT_ACCESS_TOKEN = "wx_component_access_token:";

	public static final String WX_COMPONENT_PRE_AUTH_CODE = "wx_pre_auth_code:";
	
	public static final String WX_BUTTON_TOKEN = "wx_button_token:";
	
	public static final String WX_JS_API_Ticker = "wx_js_api_ticker:";

	public static final String STARPOS_PAY_CALLBACK_URL = "starpos_pay_callback_url:";
	
	public static final String WX_UNIONID = "wx_unionid:";
	
	public static final String WX_SESSION_KEY = "wx_session_key:";
	
}
