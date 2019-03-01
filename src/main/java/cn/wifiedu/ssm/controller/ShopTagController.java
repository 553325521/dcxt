package cn.wifiedu.ssm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * 商铺与数据库交互
 * 
 * @author wangjinglong
 *
 */

@Controller
@Scope("prototype")
public class ShopTagController extends BaseController {

	private static Logger logger = Logger.getLogger(ShopTagController.class);

	@Resource
	OpenService openService;

	@Resource
	PlatformTransactionManager transactionManager;

	@Autowired
	WxController wxControllerl;

	@Autowired
	private InterfaceController interCtrl;

	@Resource
	private JedisClient jedisClient;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	@RequestMapping("/ShopTag_insert_addNewShopTag")
	public void addNewShopTag(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			Map<String, Object> map = getParameterMap();
			map.put("IS_USE", String.valueOf(map.get("IS_USE")));
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "addNewShopTag");
			logger.info("insert addNewShopTag: info [ " + map + "]");
			if (StringUtils.isNotBlank(openService.insert(map))) {
				output("0000", "保存成功！");
				return;
			}
			output("9999", "保存失败！");
			return;
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 显示当前登录代理商下的商铺信息 2018年8月8日23:50:27 修改 lps
	 * 
	 * @author wangjinglong
	 */
	@RequestMapping("/ShopTag_select_selectShopGoodsTagById")
	public void selectShopGoodsTagById() {
		try {
			// 如果未认证，跳转完善信息界面
			Map<String, Object> map = getParameterMap();
			// 获取当前session信息
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("SHOP_ID", userObj.get("FK_SHOP"));

			// 查询商品标签
			map.put("sqlMapId", "selectShopGoodsTagById");
			List reList = openService.queryForList(map);

			// 查询打印标签
			map.put("sqlMapId", "selectShopGoodsPrintTagById");
			List printLabelList = openService.queryForList(map);

			Map labelMap = new HashMap<String, List>();

			labelMap.put("goodTag", reList);
			labelMap.put("printTag", printLabelList);

			output("0000", labelMap);
		} catch (Exception e) {
			e.printStackTrace();
			output("9999", "查询失败");
		}
	}

}
