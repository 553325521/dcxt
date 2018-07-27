package cn.wifiedu.ssm.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.WxUtil;


/**
 * 交易记录与数据库交互
 * @author wangjinglong
 *
 */

@Controller
@Scope("prototype")
public class TradingRecordController extends BaseController {

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	/**
	 * 显示当前登录代理商下的交易记录
	 * @author wangjinglong
	 */
	@RequestMapping(value="/fingTradingRecord",method = RequestMethod.POST)
	public void fingTradingRecord() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("USER_WX","4b8cea73b03a4ddfacf8fbaf7a31028d");
			map.put("sqlMapId", "fingTradingRecord");
			List<Map<String, Object>> reMap  = openService.queryForList(map);
			output("0000",reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","查询失败");
		}
	}
}
