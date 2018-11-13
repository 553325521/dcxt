package cn.wifiedu.ssm.controller;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.print.PrintTemplate58MM;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

/**
 * @author kqs
 * @time 2018年7月24日 - 下午10:34:43
 * @description:打印机模块
 */
@Controller
@Scope("prototype")
public class PrinterController extends BaseController {

	private static Logger logger = Logger.getLogger(PrinterController.class);

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

	@Autowired
	private TransactionManagerController txManagerController;

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年7月24日 - 下午10:37:23
	 * @description:打印机设置
	 */
	@RequestMapping("/Print_insert_addPinterInfo")
	public void findPlatformTypeList(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("INSERT_TIME", StringDeal.getStringDate());
			map.put("sqlMapId", "addPinterInfo");
			txManagerController.createTxManager();
			String res = openService.insert(map);
			if (res != null) {
				map.put("sqlMapId", "updatePrinterToUse");
				if (openService.update(map)) {
					txManagerController.commit();
					output("0000", "保存成功!");
					return;
				}
			}
		} catch (Exception e) {
			txManagerController.rollback();
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月27日 - 下午2:42:35
	 * @description:查询已购打印机
	 */
	@RequestMapping("/Print_queryForList_loadPrintList")
	public void loadPrintList(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "loadPrintList");
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null) {
				output("0000", res);
				return;
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
		return;
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月14日 - 下午11:08:32
	 * @description:查询打印机相关信息
	 */
	@RequestMapping("/Print_queryForList_loadPrintRelevantList")
	public void loadPrintRelevantList(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadPrintRelevantList");
			List<Map<String, Object>> res = openService.queryForList(map);
			output("0000", res);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月14日 - 下午11:08:47
	 * @description:查询打印机价格
	 */
	@RequestMapping("/Print_queryForList_findPriceURL")
	public void findPriceURL(HttpServletRequest request, HttpSession session) {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "findPriceURL");
			Map<String, Object> res = (Map<String, Object>) openService.queryForObject(map);
			output("0000", res);
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月17日 - 上午12:19:50
	 * @description:打印机购买
	 */
	@RequestMapping("/Print_queryForList_addPirntBug")
	public void addPirntBug(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("INSERT_TIME", StringDeal.getStringDate());
			map.put("CREATER", userObj.getString("USER_WX"));
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "addPirntBug");
			String res = openService.insert(map);
			if (res != null) {
				output("0000", "购买成功！");
				return;
			}
			output("9999", "购买失败！");
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}
	
	/**
	 * 
	 * @author kqs
	 * @param request
	 * @param session
	 * @return void
	 * @date 2018年8月27日 - 下午2:42:35
	 * @description:查询已添加打印机
	 */
	@RequestMapping("/Print_queryForList_loadInUsePrintList")
	public void loadInUsePrintList(HttpServletRequest request, HttpSession session) {
		try {
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);

			Map<String, Object> map = getParameterMap();
			map.put("FK_SHOP", userObj.getString("FK_SHOP"));
			map.put("sqlMapId", "loadInUsePrintList");
			List<Map<String, Object>> res = openService.queryForList(map);
			if (res != null) {
				output("0000", res);
				return;
			}
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
		return;
	}
	
	// @RequestMapping("/Print_insert_doPrint")
	@Test
	public void doPrint1() {
		try {
			String host = "119.23.71.153"; // 要连接的服务端IP地址 119.23.71.153
			int port = 8008; // 要连接的服务端对应的监听端口
			// 发送内容
			// shopId!!!deviceId!!!orderId!!!content#
			StringBuilder printStr = new StringBuilder();
			Map<String, Object> map = new HashMap<>();
			// shopId
			printStr.append(UUID.randomUUID().toString().replace("-", ""))
					.append("!!!")
					// deviceId
					.append("1234567")
					.append("!!!")
					// orderId
					.append("2018111200001001")
					.append("!!!")
					// content
					.append("&!*42018111200001001*" + new PrintTemplate58MM(map, map).getInStoreBWTemplate() + "*<qrcA7>www.chsail.com*<BMP203>*<BEEP5000,1,1,2>*<cutA1>#");

			System.out.println(printStr);

			// 与服务端建立连接
			Socket client = new Socket(host, port);
			Writer writer = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
			writer.write(printStr.toString());
			writer.close();
			client.close();
		} catch (Exception e) {
			output("9999", " Exception ", e);
		}
	}

}
