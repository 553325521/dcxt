package cn.wifiedu.ssm.controller;

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
	 * @param wangjinglong
	 */
	@RequestMapping(value="/Shop_save_data",method = RequestMethod.POST)
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
	
	
}
