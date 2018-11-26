package cn.wifiedu.ssm.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.alibaba.fastjson.JSON;
import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;
	
			/**
		 * 
		 * @author lps
		 * @Description:新大陆星POS支付
		 * @version V1.0
		 *
		 */
		@Controller
		@Scope("prototype")
		public class StarPosPayController extends BaseController {

			private static Logger logger = Logger.getLogger(StarPosPayController.class);

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
			
			/**
			 * 
			 * @date 22018年8月29日23:06:04
			 * @author lps
			 * 
			 * @Description: 星pos支付异步通知
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			
			@RequestMapping(value = "/starPosPay_async_notify", method = RequestMethod.POST)
			public void findAgentInfoById(HttpServletRequest request,HttpSession seesion, HttpServletResponse reponse){
				String params = null;
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "gbk"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
					sb.append(temp);
					}
					br.close();
					params = sb.toString();
					logger.info("-------------pos-------------------");
					logger.info(params);
					
					Map<String, String> reMap = (Map<String, String>)JSON.parse(params);
					
					//更新至数据库
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("OPEN_ID", reMap.get("UserId"));
					map.put("NOTIFY_BAL_DATE", reMap.get("BalDate"));
					map.put("NOTIFY_TRADING_TIME", reMap.get("TxnDate") + reMap.get("TxnTime"));
					map.put("TRADE_NO", reMap.get("AgentId"));
					map.put("MERC_ID", reMap.get("BusinessId"));
					map.put("TRM_NO", reMap.get("SDTermNo"));
					map.put("NOTIFY_TXN_CODE", reMap.get("TxnCode"));
					map.put("NOTIFY_PAY_CHANNEL", reMap.get("PayChannel"));
					map.put("NOTIFY_TXNAMT", reMap.get("TxnAmt"));
					map.put("NOTIFY_TXN_STATUS", reMap.get("TxnStatus"));
					map.put("BANK_TYPE", JSON.toJSONString(reMap.get("BankType")));
					map.put("OFFICE_ID", reMap.get("OfficeId"));
					map.put("SEL_ORDER_NO", reMap.get("ChannelId"));
					map.put("CRD_FLG", JSON.toJSONString(reMap.get("CrdFlg")));
					map.put("LOG_NO", reMap.get("logNo"));
					map.put("UPDATE_BY", "admin");
					
					map.put("sqlMapId", "updateStarPosPayByLogNo");
					boolean update = openService.update(map);
					if(!update){
						throw new Exception("更新失败，新大陆异步通知返回结果插入数据库失败");
					}
					
					//回调url要传递的参数，为了保持和传入的时候参数名一致，所以新生成一个map转换
					Map<String, Object> postMap = new HashMap<String, Object>();
					postMap.put("mercId", reMap.get("BusinessId"));
					postMap.put("logNo", reMap.get("logNo"));
					postMap.put("openid", reMap.get("UserId"));
					postMap.put("tradingTime", reMap.get("TxnDate") + reMap.get("TxnTime"));
					postMap.put("officeId", reMap.get("OfficeId"));
					map.put("notifyTxnStatus", reMap.get("TxnStatus"));
					
					//判断有没有回调，有的话调用url
					if(jedisClient.isExit(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo"))
					&& StringUtils.isNotBlank(jedisClient.get(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo")))){
						String mess = jedisClient.get(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo"));//取出之前存在redis的map信息
						Map<String, Object> messMap = JSON.parseObject(mess);
						String callBackUrl = (String) messMap.get("callBackUrl");
						messMap.remove("callBackUrl");
						postMap.putAll(messMap);
						String retStr = CommonUtil.posts(CommonUtil.getPath("project_url").replace("DATA", callBackUrl), JSON.toJSONString(postMap), "utf-8");
						jedisClient.del(RedisConstants.STARPOS_PAY_CALLBACK_URL + reMap.get("logNo"));
					}
					
//					output("000000","success                                                     ");
					//RspCode	RspDes
					reponse.getWriter().write("{\"RspCode\":\"000000\",\"RspDes\":\"success                                                     \"}");
					return;
				} catch (Exception e) {
					logger.error(e);
					logger.error("return message---->"+params);
					output("999999", " Exception ", e);
					return;
				}
			}
			
			
			

			@RequestMapping(value = "/aaa_aaa_aaa", method = RequestMethod.POST)
			public String test(HttpServletRequest request,HttpSession seesion){
				logger.info("-------------------test success----------------");
				Map map = null;
				try {
					map = getParameterMap();
				} catch (ExceptionVo e) {
					e.printStackTrace();
					return "9999";
				}
				logger.info(map);
				return "0000";
			}
			
			
			
		}

