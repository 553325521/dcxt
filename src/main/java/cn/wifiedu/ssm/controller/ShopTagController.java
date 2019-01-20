package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.DateUtil;
import cn.wifiedu.ssm.util.QRCode;
import cn.wifiedu.ssm.util.WxConstants;
import cn.wifiedu.ssm.util.WxUtil;
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
			
			//查询商品标签
			map.put("sqlMapId", "selectShopGoodsTagById");
			List reList =  openService.queryForList(map);
			
			//查询打印标签
			map.put("sqlMapId", "selectShopGoodsPrintTagById");
			List printLabelList =  openService.queryForList(map);
			
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
