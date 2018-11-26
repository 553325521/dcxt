package cn.wifiedu.ssm.starpos.pay;


import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import com.alibaba.fastjson.JSON;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

@Controller
@Scope("prototype")
public class StarPosPay {
	
	public static final String PAY_CHANNEL_WEIXIN = "WXPAY";//微信支付
	public static final String PAY_CHANNEL_ALIPAY = "ALIPAY";//支付宝支付
	public static final String PAY_CHANNEL_YLPAY = "YLPAY";//银联支付
	
	private static Logger logger = Logger.getLogger(StarPosPay.class);

    public static String testMchId="800690000005418";
    public static String testTrmNo="XA080976";
    public static String testKey="150F7C9C8A6E2EFE58829A77B2BDF927";
    public static String testHeadUrl="http://gateway.starpos.com.cn/adpweb/ehpspos3";
    public static String testOrgNo="9137";

    
    @Resource
	OpenService openService;
    
    @Resource
	private JedisClient jedisClient;
    
    public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
	this.openService = openService;
	}
    
	//初始化要支付的参数，微信公众号支付不用初始化
    public static Map<String, Object> initParams(Map<String, Object> map)
    {
    	if(!map.containsKey("mercId")){
    		map.put("mercId",CommonUtil.getPath("spMchId"));
    	}
    	if(!map.containsKey("orgNo")){
    		map.put("orgNo",CommonUtil.getPath("spOrgNo"));         //机构号
    	}
    	if(!map.containsKey("trmNo")){
    		map.put("trmNo",CommonUtil.getPath("spTrmNo"));
    	}
    	 if(!map.containsKey("total_amount")){
    		 //订单总金额
    		 map.put("total_amount", map.get("amount")); 
    	 }
        /**参数准备**/
        map.put("opSys","3");         //操作系统
        map.put("characterSet","01"); //字符集
        String tradeNo = CommonUtils.getPayOrder();
        map.put("tradeNo", tradeNo);     //商户单号
        map.put("txnTime",  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));    //设备端交易时间
        map.put("signType","MD5");           //签名方式
        map.put("version","V1.0.1");       //版本号
        map.put("selOrderNo", tradeNo);
        return map;
    }

    /**
     * 
     * @date 2018年8月29日 下午10:07:58 
     * @author lps
     * 
     * @Description: 获取微信支付需要的字段
     * @param paramsMap	第一个要传入的参数map信息如下。  第二个参数为用户支付成功后要调用的url,以便完成后续逻辑。
     * 					mercId : 星pos商户号	默认为重庆览拓公司商户号	
     * 					orgNo ： 星pos机构号	默认为重庆览拓公司机构号
     * 					trmNo ： 星pos设备号	默认为重庆览拓公司设备号
     * 				以上三项不允许单独设置一个，要么全设置，要么全不设置
     * 					code ： 要支付的用户所在公众号的授权code，此处公众号为所填merId下绑定的公众号
     * 					openid :  要支付的用户所在公众号的openid，与code只能同时填一个
     * 					amount : 实付金额，单位为分
     * 					total_amount : (选填)订单总金额，单位为分。默认为amount
     * 					subject : (选填)订单标题
     * 					selOrderNo : (选填)订单号				
     * 					goods_tag ： (选填)订单优惠说明		
     * 					attach ：(选填)附加字段
     * 
     * @param callBackUrl 支付完成后要回调的链接
     * @param callBackParamMap 支付完成后回调函数所需要的数据
     *
     * @throws Exception 
     * @return Map	返回的map
     * 	 				returnCode ：000000为成功
     * 					sysTime ：系统交易时间。如：20180829203006
     * 					message ：返回信息
     * 					mercId ：商户号
     * 					LogNo ：系统流水号。可用于名单查询接口查询结果
     * 					result ：交易结查	A-等待授权 Z-交易未知 
     *					orderNo ：支付渠道订单号
     *					amount ：实付金额，单位为分
     *					total_amount : 订单总金额
     *					PrepayId ：预支付id
     *					----以下是h5页面内调用微信支付所需要的参数，请务必传给前端
     *					apiAppid :	支付公众号 ID 
     *					apiTimestamp : 支付时间戳 
     *					apiNoncestr : 支付随机字符串
     *					apiPackage : 订单详情扩展字符串
     *					apiSigntype : 签名方式
     *					apiPaysign : 签名
     *					------------
     *					以下为你请求之前传入的值
     *					subject 
     *					selOrderNo 
     *					goodsTag 
     *					attach 
     */
    public Map<String, Object> pubSigPay(Map<String, Object> paramsMap, String callBackUrl, Map<String, Object> callBackParamMap) throws Exception {
    	pubSigQry();
    	if(!paramsMap.containsKey("amount")){
    		paramsMap.put("returnCode", "");
    		paramsMap.put("message", "amount不能为空！");
    		return paramsMap;
    	}
    	if(!paramsMap.containsKey("USER_ID") || StringUtils.isBlank((String)paramsMap.get("USER_ID"))){
    		paramsMap.put("returnCode", "");
    		paramsMap.put("message", "userId不能为空！");
    		return paramsMap;
    	}
    	if(!paramsMap.containsKey("mercId")){
    		paramsMap.put("mercId",testMchId);
    	}
    	if(!paramsMap.containsKey("orgNo")){
    		paramsMap.put("orgNo",testOrgNo);         //机构号
    	}
    	if(!paramsMap.containsKey("trmNo")){
    		 paramsMap.put("trmNo",testTrmNo);
    	}
    	if(!(paramsMap.containsKey("amount") && paramsMap.containsKey("openid") || paramsMap.containsKey("code"))){
    		paramsMap.clear();
    		paramsMap.put("returnCode", "参数不完整");
    		return paramsMap;
    	}
    	if(!paramsMap.containsKey("total_amount")){
    		paramsMap.put("total_amount", paramsMap.get("amount"));
    	}
         paramsMap.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
         paramsMap.put("version","V1.0.1");
         logger.info("------------------qingqiiu_Map---------------------------");
         logger.info(paramsMap);
         
         String reqUrl = testHeadUrl + "/pubSigPay.json";
         String posts = URLDecoder.decode(CommonUtil.posts(reqUrl, JSON.toJSONString(paramsMap), "UTF-8"),"UTF-8");
         Map<String, Object> reMap = JSON.parseObject(posts, Map.class);
         
         //把交易信息插入数据库
         String insertMessage = insertMessage(paramsMap, reMap);
         if(insertMessage == null){
        	 logger.error("------------------插入数据库失败---------------------------");
        	 logger.error("request before message" + paramsMap);
             logger.error("request after message" + reMap);
             throw new Exception("插入数据库失败");
         }
         
         logger.info("------------------back_Map---------------------------");
         logger.info(reMap);
         
         //如果回调url不为空，把回调信息插入redis
         if(StringUtils.isNotBlank(callBackUrl)){
        	 if(callBackParamMap == null){
        		 callBackParamMap = new HashMap<String, Object>();
        	 }
        	 callBackParamMap.put("callBackUrl", callBackUrl);
        	 //把回调url存入redis，收到新大陆的异步消息后立马回调
      		jedisClient.set(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo"), JSON.toJSONString(callBackParamMap));
      		//设置过期时间2小时
      		jedisClient.expire(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo"), 3600 * 2);
      		
      		logger.info("------------------starPosPay189redis---------------------------");
    		logger.info(jedisClient.get(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo")));
         }
 		
 		 
         
        return reMap;
   }
    
    
    /**
     * 
     * @date 2018年9月1日 上午12:04:47 
     * @author lps
     * 
     * @Description: 商户主扫
     * @param paramsMap	请求参数
     * 必填项：
     * amount ：金额，单位为分
     * authCode :要扫的条形码。扫码支付授权码，设备读取用户微信或支付宝中的条码或者二维码信息 
     * payChannel ：支付渠道。见静态变量
     * 
     * 
     * 以下为选填：
     * orgNo ：机构号，不填默认为重庆览拓公司机构号
     * mercId ：商户号，不填默认为重庆览拓公司商户号
     * trmNo ：设备号，不填默认为重庆览拓公司设备号
     * total_amount:不填默认为amount
     * 
     * 
     * latitude：纬度值
     * longitude ：经度值
     * oprId ：操作员号
     * trmTyp ：设备类型。	P-智能POS 	A- app扫码 	C-PC端 	T-台牌扫码 
     * addField ：附加字段 
     * subject ：订单标题
     * goods_tag :订单优惠说明
     * attach :附加数据 
     * 
     * 
     * @throws Exception 
     * @return Map
     * 	返回值：
     *  logNo : 系统流水号，可用于查询订单
     * 	tradeNo ：商户单号
     * 	returnCode 返回码（6位） 000000表示成功
     *	sysTime ：系统交易时间
     *	message ：返回信息
     *	mercId ：商户号
     *	addField ：附加字段
     *	Result ：交易结查 S-交易成功 F-交易失败 A-等待授权 Z-交易未知
     *	orderNo ：交易成功（result为S）返回的与用户支付订单中条码一致，可用于退货；建议使用offfice_id退货
     *	amount ：实付金额，单位为分
     *	total_amount ：订单总金额，单位为分
     *	subject ：订单标题
     *	selOrderNo ：订单号
     *	goodsTag ：订单优惠说明
     *	attach : 附加数据 
     *	
     */
    public Map<String, Object> pay(Map<String, Object> paramsMap) throws Exception {
    	if(!paramsMap.containsKey("amount")){
    		paramsMap.put("returnCode", "");
    		paramsMap.put("message", "amount不能为空！");
    		return paramsMap;
    	}
    	
    	if(!paramsMap.containsKey("authCode") || StringUtils.isBlank((String)paramsMap.get("authCode"))){
    		paramsMap.put("returnCode", "");
    		paramsMap.put("message", "authCode不能为空！");
    		return paramsMap;
    	}
    	
//    	if(!paramsMap.containsKey("USER_ID") || StringUtils.isBlank((String)paramsMap.get("USER_ID"))){
//    		paramsMap.put("returnCode", "");
//    		paramsMap.put("message", "userId不能为空！");
//    		return paramsMap;
//    	}
//    	
//    	if(!paramsMap.containsKey("DCXT_ORDER_FK") || StringUtils.isBlank((String)paramsMap.get("DCXT_ORDER_FK"))){
//    		paramsMap.put("returnCode", "");
//    		paramsMap.put("message", "DCXT_ORDER_FK不能为空！");
//    		return paramsMap;
//    	}
    	
    	
          paramsMap=initParams(paramsMap);

          String preStr = HttpParamsUtils.buildPayValues(paramsMap,false,true);
          System.out.println("排序后："+preStr);
          String sign = MD5.sign(preStr, CommonUtil.getPath("spKey"), "utf-8");
          System.out.println("signValue："+sign);
          paramsMap.put("signValue",sign);

          logger.info("------------------qingqiiu_Map---------------------------");
          logger.info(paramsMap);

          String reqUrl = testHeadUrl + "/sdkBarcodePay.json";
          String posts = URLDecoder.decode(CommonUtil.posts(reqUrl, JSON.toJSONString(paramsMap), "UTF-8"),"UTF-8");
          Map<String, Object> reMap = JSON.parseObject(posts, Map.class);
          
          logger.info("------------------back_Map---------------------------");
          logger.info(reMap);
          
          String insertMessage = insertMessage(paramsMap, reMap);
          if(insertMessage == null){
         	 logger.error("------------------插入数据库失败---------------------------");
         	 logger.error("request before message" + paramsMap);
              logger.error("request after message" + reMap);
              throw new Exception("插入数据库失败");
          }
          
          return reMap;
    }


   /**
    * 
    * @date 2018年9月1日 上午1:17:47 
    * @author lps
    * 
    * @Description: 客户主扫
    * @param paramsMap 
    * 必填项：
     * amount ：金额，单位为分
     * payChannel ：支付渠道。见静态变量
     * 
     * 以下为选填：
     * orgNo ：机构号，不填默认为重庆览拓公司机构号
     * mercId ：商户号，不填默认为重庆览拓公司商户号
     * trmNo ：设备号，不填默认为重庆览拓公司设备号
     * total_amount:不填默认为amount
     * 
     * 
     * latitude：纬度值
     * longitude ：经度值
     * oprId ：操作员号
     * trmTyp ：设备类型。	P-智能POS 	A- app扫码 	C-PC端 	T-台牌扫码 
     * addField ：附加字段 
     * subject ：订单标题
     * goods_tag :订单优惠说明
     * attach :附加数据 
     * 
    * 
    * 
    * @return
    * @throws Exception 
    * @return Map<String,Object> 
    * 	返回值：
    * 	payCode : 二维码地址 二维码生成地址字符串。这是最重要的，根据这个二维码地址生成二维码供用户扫码
     *  logNo : 系统流水号，可用于查询订单
     * 	tradeNo ：商户单号
     * 	returnCode 返回码（6位） 000000表示成功
     *	sysTime ：系统交易时间
     *	message ：返回信息
     *	mercId ：商户号
     *	addField ：附加字段
     *	Result ：交易结查 S-交易成功 F-交易失败 Z-交易未知 
     *	orderNo ：交易成功（result为S）返回的与用户支付订单中条码一致，可用于退货；建议使用offfice_id退货
     *	amount ：实付金额，单位为分
     *	total_amount ：订单总金额，单位为分
     *	subject ：订单标题
     *	selOrderNo ：订单号
     *	goodsTag ：订单优惠说明
     *	attach : 附加数据 
    */
    public Map<String, Object> psoPay(Map<String, Object> paramsMap) throws Exception {
    	if(!paramsMap.containsKey("amount")){
    		paramsMap.put("returnCode", "");
    		paramsMap.put("message", "amount不能为空！");
    		return paramsMap;
    	}
    	if(!paramsMap.containsKey("payChannel")){
    		paramsMap.put("returnCode", "");
    		paramsMap.put("message", "payChannel不能为空！");
    		return paramsMap;
    	}
    	if(!paramsMap.containsKey("USER_ID") || StringUtils.isBlank((String)paramsMap.get("USER_ID"))){
    		paramsMap.put("returnCode", "");
    		paramsMap.put("message", "userId不能为空！");
    		return paramsMap;
    	}
    	if(!paramsMap.containsKey("DCXT_ORDER_FK") || StringUtils.isBlank((String)paramsMap.get("DCXT_ORDER_FK"))){
    		paramsMap.put("returnCode", "");
    		paramsMap.put("message", "DCXT_ORDER_FK不能为空！");
    		return paramsMap;
    	}
        paramsMap = initParams(paramsMap);
        /** 按key排序，将value拼接字符串**/
        String preStr = HttpParamsUtils.buildPayValues(paramsMap,false,true);
        System.out.println("排序后："+preStr);
        /**md5加密**/
        String sign = MD5.sign(preStr, testKey, "utf-8");
        System.out.println("signValue："+sign);
        paramsMap.put("signValue",sign);
        System.out.println("paramsMap = " + paramsMap);
        
        logger.info("------------------qingqiiu_Map---------------------------");
        logger.info(paramsMap);

        /** 发起post请求**/
        String reqUrl = testHeadUrl + "/sdkBarcodePosPay.json";
        
        String posts = URLDecoder.decode(CommonUtil.posts(reqUrl, JSON.toJSONString(paramsMap), "UTF-8"),"UTF-8");
        Map<String, Object> reMap = JSON.parseObject(posts, Map.class);
        
        logger.info("------------------back_Map---------------------------");
        logger.info(reMap);
        
        String insertMessage = insertMessage(paramsMap, reMap);
        if(insertMessage == null){
       	 logger.error("------------------插入数据库失败---------------------------");
       	 logger.error("request before message" + paramsMap);
            logger.error("request after message" + reMap);
            throw new Exception("插入数据库失败");
        }
        
        return reMap;
    }

    /**
     * 订单查询
     * @param qryNo
     * @throws Exception
     */
    public static Map<String, Object> query(String qryNo) throws Exception {

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap = initParams(paramsMap);
        paramsMap.put("qryNo", qryNo);
        paramsMap.remove("total_amount");
        paramsMap.remove("selOrderNo");
        String preStr = HttpParamsUtils.buildPayValues(paramsMap,false,true);
        String sign = MD5.sign(preStr, testKey, "UTF-8");
        paramsMap.put("signValue",sign);

        logger.info("------------------qingqiiu_Map---------------------------");
        logger.info(paramsMap);

        String reqUrl = testHeadUrl + "/sdkQryBarcodePay.json";
        String posts = URLDecoder.decode(CommonUtil.posts(reqUrl, JSON.toJSONString(paramsMap), "UTF-8"),"UTF-8");
        Map<String, Object> reMap = JSON.parseObject(posts, Map.class);
        
        logger.info("------------------back_Map---------------------------");
        logger.info(reMap);
        
        return reMap;
    }

    /**
     * 退款
     * @param orderNo	支付渠道订单号
     * @param txnAmt	退款金额，为空时傲视全额退款
     * @throws Exception
     */
    public static void refundBarcodePay(String orderNo,String txnAmt) throws Exception {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap = initParams(paramsMap);
        paramsMap.put("orderNo", orderNo);
        if(!StringUtils.isEmpty(txnAmt))
        {
            paramsMap.put("txnAmt", txnAmt);
        }
        String preStr = HttpParamsUtils.buildPayValues(paramsMap,false,true);
        System.out.println(preStr);
        String sign = MD5.sign(preStr, testKey, "UTF-8");
        paramsMap.put("signValue",sign);


        System.out.println("paramsMap = " + paramsMap);

        String reqUrl = testHeadUrl + "/sdkRefundBarcodePay.json";
        String posts = URLDecoder.decode(CommonUtil.posts(reqUrl, JSON.toJSONString(paramsMap), "UTF-8"),"UTF-8");
        Map<String, Object> reMap = JSON.parseObject(posts, Map.class);
        
        System.out.println("map = " + reMap);
    }

    /**
     * 交易未完成，撤单，目前因通道方原因暂不支持
     * @param qryNo 原交易的tradeNo
     * @throws Exception
     */
    public static void revokeBarcodepay(String qryNo) throws Exception {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap = initParams(paramsMap);
        paramsMap.put("qryNo", qryNo);

        String preStr = HttpParamsUtils.buildPayValues(paramsMap,false,true);
        String sign = MD5.sign(preStr, testKey, "UTF-8");
        paramsMap.put("signValue",sign);

        System.out.println("paramsMap = " + paramsMap);

        String reqUrl = testHeadUrl + "/RevokeBarcodepay.json";
        String posts = URLDecoder.decode(CommonUtil.posts(reqUrl, JSON.toJSONString(paramsMap), "UTF-8"),"UTF-8");
        Map<String, Object> reMap = JSON.parseObject(posts, Map.class);
        System.out.println("map = " + reMap);
    }


    /**
     * 公众号查询
     * 必须再生产环境下调试，若要自己得公众号支付必须先配置再调试
     */
    public static void pubSigQry() throws Exception {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("orgNo",testOrgNo);         //机构号
        paramsMap.put("mercId",testMchId);
        paramsMap.put("trmNo",testTrmNo);
        paramsMap.put("txnTime",  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        paramsMap.put("signType","MD5");

        paramsMap.put("version","V1.0.1");
        String preStr = HttpParamsUtils.buildPayValues(paramsMap,false,true);
        String sign = MD5.sign(preStr, testKey, "UTF-8");
        paramsMap.put("attach","1231");
        paramsMap.put("signValue",sign);


        String reqUrl = testHeadUrl + "/pubSigQry.json";
        String posts = URLDecoder.decode(CommonUtil.posts(reqUrl, JSON.toJSONString(paramsMap), "UTF-8"),"UTF-8");
        Map<String, Object> reMap = JSON.parseObject(posts, Map.class);
        System.out.println("map = " + reMap);
    }

    
    /**ok
     * 
     * @date 2018年8月7日 下午7:09:20 
     * @author lps
     * 
     * @Description: 授权码查询 openID（qryAuthorizationcode） 
     * @throws Exception 
     * @return void 
     *
     */
    public static void qryAuthorizationcode(String userData) throws Exception {
    	 Map<String, Object> paramsMap = new HashMap<String, Object>();
         paramsMap.put("mercId",testMchId);
         paramsMap.put("trmNo",testTrmNo);
         paramsMap.put("txnTime",  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
         paramsMap.put("userData",userData);
         paramsMap.put("version","V1.0.0");
         paramsMap.put("signType","MD5");
         String preStr = HttpParamsUtils.buildPayValues(paramsMap,false,true);
         String sign = MD5.sign(preStr, testKey, "UTF-8");
         paramsMap.put("signValue",sign);
    	 String reqUrl = testHeadUrl + "/qryAuthorizationcode.json";
    	  String posts = URLDecoder.decode(CommonUtil.posts(reqUrl, JSON.toJSONString(paramsMap), "UTF-8"),"UTF-8");
          Map<String, Object> reMap = JSON.parseObject(posts, Map.class);
         
         System.out.println("map = " + reMap);
    }
    
    
    /**
     * 
     * @date 2018年8月31日 上午3:23:48 
     * @author lps
     * 
     * @Description: 传入一个请求之前的map和一个返回的map，生成插入数据库的map
     * @param paramsMap
     * @param reMap
     * @return 
     * @return Map<String,Object> 
     *
     */
    private String insertMessage(Map<String, Object> paramsMap, Map<String, Object> reMap) {
    	try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("MERC_ID", paramsMap.get("mercId"));
			map.put("TRM_NO", paramsMap.get("trmNo"));
			map.put("ORG_NO", paramsMap.get("orgNo"));
			map.put("AMOUNT", paramsMap.get("amount"));
			map.put("TOTAL_AMOUNT", paramsMap.get("total_amount"));
			map.put("USER_ID", paramsMap.get("USER_ID"));
			map.put("DCXT_ORDER_FK", paramsMap.get("DCXT_ORDER_FK"));
			
			if(paramsMap.containsKey("characterSet")){
				map.put("CHARACTERSET", paramsMap.get("characterSet"));
			}
			
			if(paramsMap.containsKey("authCode")){
				map.put("AUTH_CODE", paramsMap.get("authCode"));
			}
			

			if(paramsMap.containsKey("tradeNo")){
				map.put("TRADE_NO", paramsMap.get("tradeNo"));
			}
			
			if(paramsMap.containsKey("subject")){
				map.put("SUBJECT", paramsMap.get("subject"));
			}
			
			if(paramsMap.containsKey("selOrderNo")){
				map.put("SEL_ORDER_NO", paramsMap.get("selOrderNo"));
			}
			
			map.put("ORDER_CREATE_TIME", paramsMap.get("txnTime"));
			
			if(paramsMap.containsKey("payChannel")){
				map.put("REQUEST_PAY_CHANNEL", paramsMap.get("payChannel"));
			}
			
			if(paramsMap.containsKey("goods_tag")){
				map.put("GOODS_TAG", paramsMap.get("goods_tag"));
			}
			
			if(paramsMap.containsKey("attach")){
				map.put("ATTACH", paramsMap.get("attach"));
			}
			
			if(paramsMap.containsKey("openid")){
				map.put("OPEN_ID", paramsMap.get("openid"));
			}
			
			
			if(paramsMap.containsKey("latitude")){
				map.put("LATITUDE", paramsMap.get("latitude"));
			}
			if(paramsMap.containsKey("longitude")){
				map.put("LONGITUDE", paramsMap.get("longitude"));
			}
			if(paramsMap.containsKey("oprId")){
				map.put("OPRID", paramsMap.get("oprId"));
			}
			if(paramsMap.containsKey("trmTyp")){
				map.put("TRM_TYPE", paramsMap.get("trmTyp"));
			}
			if(paramsMap.containsKey("addField")){
				map.put("ADD_FIELD", paramsMap.get("addField"));
			}      
			
			map.put("RETURN_CODE", reMap.get("returnCode"));
			map.put("RETURN_RESULT", reMap.get("result"));
			map.put("LOG_NO", reMap.get("logNo"));
			map.put("RETURN_MESSAGE", reMap.get("message"));
			map.put("ORDER_NO", reMap.get("orderNo"));
			
			map.put("CREATE_BY", "admin");
			
			map.put("sqlMapId", "insertStarPosPayOrder");
			
			String insert = openService.insert(map);
			if(insert == null){
				throw new Exception("insert Database fail!");
			}
			
			return insert;
		} catch (Exception e) {
			 logger.info("交易完成后插入数据库失败，The insert database failed after the transaction was completed-----当前交易订单为->");
			 logger.info(reMap);
			 logger.info(e);
		}
    	return null;
	}
    
    

public static void main(String[] args) {
	Map<String, Object> map = new HashMap<String, Object>();
	
	try {
		
		
		//pubSigQry();
//		amount ：金额，单位为分
//	     * authCode :要扫的条形码。扫码支付授权码，设备读取用户微信或支付宝中的条码或者二维码信息 
//	     * payChannel ：支付渠道。见静态变量
//		Map<String, Object> paramsMap = new HashMap<String, Object>();
//		paramsMap.put("amount", "1");
//		paramsMap.put("openid", "o93Dw0G2WF0ffPkMAvN9MP0sUL-w");
//		paramsMap.put("USER_ID", "lupishan");
//		paramsMap.put("DCXT_ORDER_FK", "lps");
//////////		paramsMap.put("payChannel", StarPosPay.PAY_CHANNEL_YLPAY);
//		Map map2 = new StarPosPay().pubSigPay(paramsMap,null,null);
//		Map map2 = new StarPosPay().psoPay(paramsMap);
//		query("201811171641962991");
//		pubSigQry();//286929898784
//		System.out.println(map2);
		
		
//商户扫
		//map.put("USER_ID", "2222222");
		map.put("amount", "1"); 
		map.put("authCode", "22222222");
		//map.put("DCXT_ORDER_FK", "lps");
		map.put("payChannel", StarPosPay.PAY_CHANNEL_WEIXIN); 
		map.put("authCode", "134533491064161093");
		
		Map map2 = new StarPosPay().pay(map);
		
		System.out.println(map2);
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}
}


}

