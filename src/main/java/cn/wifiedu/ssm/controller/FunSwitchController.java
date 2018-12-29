package cn.wifiedu.ssm.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.redis.JedisClient;

/**
 * 
 * @author lu
 *
 */
@Controller
@Scope("prototype")
public class FunSwitchController extends BaseController {

	private static Logger logger = Logger.getLogger(FunSwitchController.class);

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	@Resource
	private JedisClient jedisClient;


	
	/**
	 * 
	 * @author lps
	 * @date Dec 12, 2018 5:21:20 PM 
	 * 
	 * @description: 小程序获取配置开关
	 * @return void
	 */

	public Map getFuncSwitch(String shopId) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			
				map.put("FK_SHOP", shopId);
				map.put("sqlMapId", "loadFuncSwitchList");
				Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
				return reMap;
			
		} catch (Exception e) {
			output("9999", " Exception ", e);
			e.printStackTrace();
		}
		return null;
	}
}
