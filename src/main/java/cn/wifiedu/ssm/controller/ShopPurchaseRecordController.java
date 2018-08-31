package cn.wifiedu.ssm.controller;


	import java.lang.reflect.Field;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.starpos.pay.StarPosPay;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.DateUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

		/**
		 * 
		 * @author lps
		 * @date 2018年7月25日 上午4:51:31
		 * @Description:
		 * @version V1.0
		 *
		 */
		@Controller
		@Scope("prototype")
		public class ShopPurchaseRecordController extends BaseController {

			private static final String Map = null;

			private static Logger logger = Logger.getLogger(ShopPurchaseRecordController.class);

			@Resource
			OpenService openService;
			
			@Resource
			private JedisClient jedisClient;
			
			@Resource
			private StarPosPay starPosPay;
			
			
			@Resource
			PlatformTransactionManager transactionManager;

			public OpenService getOpenService() {
				return openService;
			}

			public void setOpenService(OpenService openService) {
			this.openService = openService;
			}
			
			//优惠策略
			//Map discountsMap = JSON.parseObject("{\"12\" : 2,\"24\" : 5 ,\"36\" :  9 ,\"48\" : 12,\"60\" : 15}");

			/**
			 * 
			 * @date 2018年7月25日 上午5:05:17 
			 * @author lps
			 * 
			 * @Description: 插入商铺交易记录
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/ShopPurchaseRecord_insert_insertShopPurchaseRecord")
			public void addTransactionRecord(HttpServletRequest request,HttpSession seesion){
				TransactionStatus status = null;
				try {	
					Map<String, Object> map = getParameterMap();
					
					/**数据验证*/
					/*查询服务类型*/
					map.put("sqlMapId", "findServicePriceById");
					map.put("SERVICE_PK",map.get("SERVICE_ID"));
					
					Integer buyTime = Integer.parseInt((String)map.get("BUY_TIME")); //购买的时长
					Map serviceMap = (Map)openService.queryForObject(map);
					if (serviceMap == null || buyTime <= 0){
						output("9999", " 购买失败！   ");
						return;
					}
					Integer servicePrice = Integer.parseInt((String)serviceMap.get("SERVICE_PRICE"));//从数据库获取该服务的单价
					
					/*查询优惠规则表*/
					List<Map<String,Object>> serviceRule = ServiceTypeController.getServiceRule(openService);
					Map<String,String> discountsMap = new LinkedHashMap<String,String>();//存储优惠策略
					for (Map<String, Object> m : serviceRule) {
						discountsMap.put((String)m.get("BUYSERVICE_RULE_SJYS"), (String)m.get("BUYSERVICE_RULE_YHYS"));
					}
					
					/*判断是升级服务还是购买服务*/
					String shopId = (String) map.get("SHOP_ID");
					map.put("SHOP_ID",shopId);
					map.put("sqlMapId", "selectNODSTAndODByShopId");
					Map<String, Object> servicePrePriceMap = (Map<String, Object>) openService.queryForObject(map);
					int discountsMoney = 0;	//抵扣金额
					boolean isUpdateService = false;
					if(servicePrePriceMap != null){
						//如果servicePrePriceMap不为null，说明之前购买的还未过期，现在购买的服务类型不能低于之前购买的服务类型
						Integer servicePrePrice = Integer.parseInt((String) servicePrePriceMap.get("SERVICE_PRICE")); //之前购买的服务类型的价格
						if(servicePrice < servicePrePrice){
							output("9999", " 升级服务不允许降级购买！   ");
							return;
						}
						if(servicePrice > servicePrePrice){//如果大于之前购买的，说明是升级服务，计算出抵扣价格
							isUpdateService = true;
							discountsMoney = getDiscountsMoney(servicePrePriceMap, serviceRule);
						}
					}
			
					/*根据优惠规则判断是不是享有优惠*/
					Integer afterTime = buyTime;
					if(discountsMap.containsKey(buyTime.toString())){
						afterTime =  buyTime - Integer.parseInt(discountsMap.get(buyTime.toString()));//享有优惠，计算优惠后的月数
					}
					
					Integer needMoney = afterTime * servicePrice - discountsMoney;//计算所需金额
					needMoney = needMoney < 0 ? 0 : needMoney;
					if(needMoney != Integer.parseInt((String)map.get("TRANSACTION_MONEY"))){//判断所需金额是否等于缴纳金额
						output("9999", " 数据异常！   ");
						return;
					}
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSONObject.parseObject(userJson);
					
					
					/**数据验证结束*/
					//判断支付方式
					String payType = (String) map.get("PAY_TYPE");
					if("1".equals(payType)){//微信支付
						//微信生成订单
						map.put("USER_ID", userObj.get("USER_PK"));//发起订单的用户
						map.put("openid", userObj.get("USER_WX"));//发起订单的用户openid
						map.put("amount", 1);
						Map<String, Object> pubSigPay = starPosPay.pubSigPay(map, "aaa_aaa_aaa", userObj);
						
						if("000000".equals(pubSigPay.get("returnCode"))){
							output("5555", pubSigPay);
							return;
						}
						output("9999", "微信支付失败");
						return;
					}else{
						
						//准备插入操作，开启事务
						DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
					    defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
					    status = transactionManager.getTransaction(defaultTransactionDefinition);
						
						//余额支付
						//判断当前用户余额够不够付款的
						map.put("sqlMapId", "selectUserByPrimaryKey");
						map.put("USER_PK", userObj.get("USER_PK"));
						Map userMap = (java.util.Map) openService.queryForObject(map);
						Double USER_BALANCE = Double.parseDouble((String) userMap.get("USER_BALANCE"));
						if(USER_BALANCE - needMoney < 0){
							throw new Exception("余额不足");
						}
						//插入新的余额
						map.put("sqlMapId", "updateUserBalance");
						map.put("USER_BALANCE", (int)(USER_BALANCE - needMoney));
						String insert2 = openService.insert(map);
						
					}
				
					
				    	
					/*检验完毕，开始插入流程*/
					//先获取当前过期时间
					map.put("sqlMapId", "SelectByPrimaryKey");
					map.put("SHOP_FK",shopId);
					//从数据库获取当前店铺信息
					Map shopMap = (Map)openService.queryForObject(map);
					String overDateStr = (String)shopMap.get("OVER_DATA");
					
					Date overDate = null;
					if(overDateStr == null || isUpdateService){//如果为空或者是升级服务，那就重新计算日期
						overDate = new Date();
					}else{
						//解析日期字符串
						overDate = DateUtil.parseDate(overDateStr);
				        if (overDate.before(new Date())){
				        	//如果过期时间在今天之前，说明早过期了，不必进行在原来的时间基础上计算
				        	overDate = new Date();
				        }
					}
					
			        //计算购买后的日期
			        Date buyAfterData = DateUtil.calculateDate(overDate, buyTime, Calendar.MONTH);
					map.put("sqlMapId","UpdateOverDateAndServiceType");
					map.put("OVER_DATA",buyAfterData);
					boolean buyResult = openService.update(map);
					
					if(!buyResult){
						throw new Exception("系统异常");
					}
					
					//判断是代理支付还是商家续费
					//获取当前购买店铺的代理商的userid
					String agentUserId = "";
					String roleId = (String) userObj.get("FK_ROLE");
					map.put("SHOP_ID",shopId);
					Boolean isAgent = "7".equals(roleId) ? true : false;
					String TRANSACTION_TYPE = "1";
					if(isAgent){
						//是代理
						TRANSACTION_TYPE = "2";
						agentUserId = (String) userObj.get("USER_PK");
					}else{					
						//如果不是代理商支付，通过商铺id获取代理商信息
						map.put("sqlMapId","selectAgentIdByShopId");
						Map agentUserIdMap = (Map) openService.queryForObject(map);
						agentUserId = (String) agentUserIdMap.get("FK_USER");
					}
					
					//店铺过期时间更新成功，开始插入购买服务表
					map.put("TRANSACTION_TYPE", TRANSACTION_TYPE);
					map.put("sqlMapId","insertShopPurchaseRecord");
					map.put("OVER_DATA",buyAfterData);
					map.put("USER_FK","admin");
					map.put("SERVICE_FK", map.get("SERVICE_PK"));
					map.put("CREATE_BY", "admin");
					
					String shopPurchaseId = openService.insert(map);
					
					if(shopPurchaseId == null){
						throw new Exception("系统异常");
					}
					
					map.clear();//先清除一下map，感觉里边的数据太乱了，避免混用
					
					/*插入购买记录成功，开始插入提成记录表*/
					//根据代理提成比例获取佣金，查询当前代理的提成比例
					map.put("sqlMapId", "selectCommissionPercentageByAgentId");
					map.put("USER_ID", agentUserId);
					Map cpMap = (Map)openService.queryForObject(map);
					String  percentNumber = (String) cpMap.get("COMMISSION_PERCENTAGE");
					String substring = percentNumber.substring(0, percentNumber.indexOf("%"));
					Integer commissionMoney = (int) Math.ceil(Integer.parseInt(substring)* needMoney / 100.00);
					//正式开始插入提成记录表
					map.put("TRADE_MONEY", commissionMoney);
					map.put("sqlMapId","insertTradingRecord");
					map.put("SHOP_PURCHASE_ID", shopPurchaseId);
					
					//交易类型，10 代付提成  11续费提成
					String TRADE_TYPE = isAgent ? "10" : "11";
					map.put("SHOP_ID",shopId);
					map.put("TRADE_TYPE", TRADE_TYPE);
					map.put("CREATE_BY", "admin");
					
					String insert = openService.insert(map);
					if(insert == null){
						throw new Exception("系统异常");
					}
					//更新代理余额
					map.put("sqlMapId", "selectUserByPrimaryKey");
					map.put("USER_PK", agentUserId);
					Map userMap = (Map) openService.queryForObject(map);
					Double USER_BALANCE = Double.parseDouble((String) userMap.get("USER_BALANCE"));
					//插入新的余额
					map.put("sqlMapId", "updateUserBalance");
					map.put("USER_BALANCE", (int)(USER_BALANCE + commissionMoney));
					String insert2 = openService.insert(map);
					
					if (insert2 != null) {
						transactionManager.commit(status);
						output("0000", "支付成功!");
						return;
					}
					throw new Exception("系统异常");
				} catch (Exception e) {
					transactionManager.rollback(status);
					output("9999", e);
					return;
				}
			}
			
			
			/**
			 * 
			 * @date 2018年8月12日 下午10:44:08 
			 * @author lps
			 * 
			 * @Description: 根据优惠规则和之前的购买信息获取抵扣金额
			 * @param perServiceMess
			 * @param serviceRule
			 * @return
			 * @throws Exception 
			 * @return Integer 
			 *
			 */
			public static Integer getDiscountsMoney(Map perServiceMess, List<Map<String,Object>> serviceRule) throws Exception{
				int discountsMoney = 0;
				//计算抵扣金额
				Map<String,String> discountsMap = new LinkedHashMap<String,String>();//存储优惠策略
				for (Map<String, Object> m : serviceRule) {
					discountsMap.put((String)m.get("BUYSERVICE_RULE_SJYS"), (String)m.get("BUYSERVICE_RULE_YHYS"));
				}
				
				String overDate = (String) perServiceMess.get("OVER_DATA");
				//计算相差几个月
				Period intervalTime = DateUtil.getintervalTime(new Date(), overDate);
				Integer intervalMonths = intervalTime.getYears() * 12 + intervalTime.getMonths();
				boolean flag = false;
				int dmInt = 0;
				int discountsMouth = 0;
				for (String dmStr : discountsMap.keySet()) {//抵扣规则按照高的进行运算，详情见计算规则表
					dmInt = Integer.parseInt(dmStr);
					if(dmInt >= intervalMonths){
						flag = true;
						discountsMouth = Integer.parseInt(discountsMap.get(dmStr.toString()));
						break;
					}
				}
				if(!flag){//如果没匹配到，说明比最多的还高
					//获取最后一个元素
					Field tail = discountsMap.getClass().getDeclaredField("tail");
				    tail.setAccessible(true);
				    Entry<String, String> entry = (Entry<String, String>)tail.get(discountsMap);
				    dmInt = Integer.parseInt(entry.getKey());
				    discountsMouth = intervalMonths/dmInt * Integer.parseInt(entry.getValue());
				}
				
				discountsMoney = (intervalMonths - discountsMouth) * Integer.parseInt((String) perServiceMess.get("SERVICE_PRICE"));
				return discountsMoney;
			}
			
		 
		}

