package cn.wifiedu.ssm.controller;


	import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
	import java.util.Map;

	import javax.annotation.Resource;
import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpSession;

	import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;
import org.springframework.context.annotation.Scope;
	import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import cn.wifiedu.core.controller.BaseController;
	import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.StringDeal;

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

			private static Logger logger = Logger.getLogger(UserTagController.class);

			@Resource
			OpenService openService;
			
			@Resource
			PlatformTransactionManager transactionManager;

			public OpenService getOpenService() {
				return openService;
			}

			public void setOpenService(OpenService openService) {
			this.openService = openService;
			}
			
			//优惠策略
			Map discountsMap = JSON.parseObject("{\"12\" : 2,\"24\" : 5 ,\"36\" :  9 ,\"48\" : 12,\"60\" : 15}");

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
				DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
			    defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			    TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
		
				try {
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					Map<String, Object> map = getParameterMap();
					
					
					map.put("sqlMapId", "findServicePriceById");
					map.put("SERVICE_PK",map.get("SERVICE_ID"));
					//购买的时长
					Integer buyTime = Integer.parseInt((String)map.get("BUY_TIME"));
					Map serviceMap = (Map)openService.queryForObject(map);
					if (serviceMap == null || buyTime <= 0){
						output("9999", " 购买失败！   ");
						return;
					}
					//从数据库获取该服务的单价
					Integer servicePrice = Integer.parseInt((String)serviceMap.get("SERVICE_PRICE"));
					//查看是否享有优惠
					Integer afterTime = buyTime;
					if(discountsMap.containsKey(buyTime.toString())){
						afterTime =  buyTime - (Integer)discountsMap.get(buyTime.toString());
					}
					//计算所需金额
					Integer needMoney = afterTime * servicePrice;
					//判断所需金额是否等于缴纳金额
					if(needMoney != Integer.parseInt((String)map.get("TRANSACTION_MONEY"))){
						output("9999", " 数据异常！   ");
						return;
					}
					//判断余额够不够，够得话直接付款，不够的话微信支付
					//TODO
					
					//微信生成订单
					
					
					
					
					
					//检验完毕，开始插入流程
					//先获取当前过期时间
					map.put("sqlMapId", "SelectByPrimaryKey");
					String shopId = (String) map.get("SHOP_ID");
					map.put("SHOP_FK",shopId);
					//从数据库获取当前店铺信息
					Map shopMap = (Map)openService.queryForObject(map);
					//格式化日期
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String overDateStr = (String)shopMap.get("OVER_DATA");
					
					Date overDate = null;
					if(overDateStr == null){//如果为空，说明还没购买过服务
						overDate = new Date();
					}
					//解析日期字符串
					overDate = sdf.parse(overDateStr);
			        if (overDate.before(new Date())){
			        	//如果过期时间在今天之前，说明早过期了，不必进行在原来的时间基础上计算
			        	overDate = new Date();
			        }
			        //开始日期计算
			        GregorianCalendar gc=new GregorianCalendar(); 
			        gc.setTime(overDate); 
			        gc.add(2,buyTime); 
					String buyAfterData = sdf.format(gc.getTime());
					
					map.put("sqlMapId","UpdateOverDate");
					map.put("OVER_DATA",buyAfterData);
					
					boolean buyResult = openService.update(map);
					
					if(!buyResult){
						throw new Exception("系统异常");
					}
					
					
					//店铺过期时间更新成功，开始插入购买服务表
					map.put("sqlMapId","insertShopPurchaseRecord");
					map.put("OVER_DATA",buyAfterData);
					map.put("USER_FK","admin");
					map.put("SERVICE_FK", map.get("SERVICE_PK"));
					map.put("CREATE_BY", "admin");
					
					String shopPurchaseId = openService.insert(map);
					
					if(shopPurchaseId == null){
						throw new Exception("系统异常");
					}
					
					
					
					//插入购买记录成功，开始插入提成记录表
					//获取一下是代付还是续费
					String TRANSACTION_TYPE = (String) map.get("TRANSACTION_TYPE");
					//先清除一下map，感觉里边的数据太乱了，避免重复
					map.clear();
					
					//插入记录表之前先查询代理商的userid
					map.put("SHOP_ID",shopId);
					//先判断当前登录是不是代理商，是的话就直接获取userid
					
					String agentUserId = "";
					if("1".equals(TRANSACTION_TYPE)){
						//是代理商，直接
						map.put("sqlMapId","selectAgentIdByShopId");
						Map agentUserIdMap = (Map) openService.queryForObject(map);
						agentUserId = (String) agentUserIdMap.get("FK_USER");
					}else{
						map.put("sqlMapId","selectAgentIdByShopId");
						Map agentUserIdMap = (Map) openService.queryForObject(map);
						agentUserId = (String) agentUserIdMap.get("FK_USER");
					}
					
					
					
					
					//正式开始插入提成记录表
					map.put("sqlMapId","insertTradingRecord");
					map.put("USER_ID", agentUserId);
					map.put("SHOP_PURCHASE_ID", shopPurchaseId);
					
					Integer commissionMoney =  (int)(needMoney * 0.3);//TODO 还没处理提成比例,暂时是写死的
					map.put("TRADE_MONEY", commissionMoney);
					
					//交易类型，10 代付提成  11续费提成
					String TRADE_TYPE = "0".equals(TRANSACTION_TYPE) ? "10" : "11";
					
					map.put("TRADE_TYPE", TRADE_TYPE);
					map.put("CREATE_BY", "admin");
					
					String insert = openService.insert(map);

					if (insert != null) {
						transactionManager.commit(status);
						output("0000", "支付成功!");
						return;
					}
					throw new Exception("系统异常");
				} catch (Exception e) {
					transactionManager.rollback(status);
					output("9999", " Exception ", e);
					return;
				}
			}
			
		
		}

