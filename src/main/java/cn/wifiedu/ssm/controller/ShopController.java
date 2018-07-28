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
 * 商铺与数据库交互
 * @author wangjinglong
 *
 */

@Controller
@Scope("prototype")
public class ShopController extends BaseController {

	@Resource
	OpenService openService;

	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	/**
	 * 添加商户
	 * @author wangjinglong
	 * 
	 */
	@RequestMapping(value="/Shop_insert_insertShop",method = RequestMethod.POST)
	public void saveShopData() {
		try {
			Map<String, Object> map = getParameterMap();
			
			map.put("SHOPTYPE",map.get("SHOP_TYPE_FIRSET")+" "+map.get("SHOP_TYPE_SECOND"));
			map.put("sqlMapId", "insertShop");
			openService.insert(map);
			output("0000","保存成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","保存失败");
		}
	}
	
	/**
	 * 显示当前登录代理商下的商铺信息
	 * @author wangjinglong
	 */
	@RequestMapping(value="/Shop_select_findAgentShopInfo",method = RequestMethod.POST)
	public void showAgentShopInfo() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("USER_WX","4b8cea73b03a4ddfacf8fbaf7a31028d");
			map.put("sqlMapId", "findAgentShopInfo");
			List<Map<String, Object>> reMap  = openService.queryForList(map);
			for(int i = 0;i< reMap.size();i++){
				Map<String,Object> singleMap = reMap.get(i);
				reMap.get(i).put("DAYS", countDays(singleMap.get("OVER_DATA").toString()));
			}
			output("0000",reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","查询失败");
		}
	}
	
	private String countDays(String date){
		//算两个日期间隔多少天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null;;
		try {
			date1 = format.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int a = (int) ((date1.getTime() - new Date().getTime()) / (1000*3600*24));
		if(a < 0 ){
		  	return "已过期";
		}
		return a+"天";
	}
	
	
}
