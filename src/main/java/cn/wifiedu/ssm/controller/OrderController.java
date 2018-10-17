package cn.wifiedu.ssm.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

@Controller
@Scope("prototype")
public class OrderController extends BaseController{

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
	* <p>Title: loadOrderDataByTime</p>
	* <p>Description: 根据时间、状态查询订单列表</p>
	*/
	@RequestMapping(value = "/Order_load_loadOrderDataByTime", method = RequestMethod.POST)
	public void loadOrderDataByTime() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("CREATE_TIME") || StringUtils.isBlank(map.get("CREATE_TIME").toString())||
						!map.containsKey("END_TIME") || StringUtils.isBlank(map.get("END_TIME").toString())) {
					output("9999", "时间参数无效");
					return;
				}
				if (!map.containsKey("ORDER_PAY_STATE") || StringUtils.isBlank(map.get("ORDER_PAY_STATE").toString())) {
					output("9999", "订单支付状态参数无效");
					return;
				}
				if(map.get("ORDER_PAY_STATE").toString().equals("2")){
					map.put("ORDER_PAY_STATE",null);
				}
				map.put("sqlMapId","selectOrderByTime");
				List<Map<String,Object>> orderDataList = openService.queryForList(map);
				output("0000", orderDataList);
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* <p>Title: loadOrderDetailByOrderPK</p>
	* <p>Description:根据订单id查询订单详情 </p>
	*/
	@RequestMapping(value = "/Order_load_loadOrderDetailByOrderPK", method = RequestMethod.POST)
	public void loadOrderDetailByOrderPK() {
		try {
			Map<String, Object> map = getParameterMap();
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + map.get("openid").toString());
			if (StringUtils.isNotBlank(userJson)) {
				if (!map.containsKey("ORDER_PK") || StringUtils.isBlank(map.get("ORDER_PK").toString())) {
					output("9999", "订单ID无效");
					return;
				}
				map.put("sqlMapId","selectOrderDetailByOrderPK");
				List<Map<String,Object>> orderDataList = openService.queryForList(map);
				output("0000", orderDataList);
			} else {
				output("9999", "token无效");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
