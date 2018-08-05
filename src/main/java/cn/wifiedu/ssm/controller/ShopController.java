package cn.wifiedu.ssm.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.google.zxing.WriterException;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.core.vo.ExceptionVo;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.QRCode;
import cn.wifiedu.ssm.util.StringDeal;
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

	@Resource
	PlatformTransactionManager transactionManager;
	
	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}

	/**
	 * 添加商户
	 * @author wangjinglong    2018年8月4日23:21:41 修改 lps   添加或修改商铺
	 * 
	 */
	@RequestMapping(value="/Shop_insert_insertShop",method = RequestMethod.POST)
	public void saveShopData() {
		DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
	    defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	    TransactionStatus status = transactionManager.getTransaction(defaultTransactionDefinition);
		try {
			Map<String, Object> map = getParameterMap();
			//合二为一
			map.put("SHOP_TYPE",map.get("SHOP_TYPE_FIRSET")+" "+map.get("SHOP_TYPE_SECOND"));
			//判断是添加还是修改
			String shopId = (String) map.get("SHOP_ID");
			if(shopId != null && !"".equals(shopId.trim())){//是修改
				map.put("sqlMapId", "updateShopBaseInfoById");
				map.put("UPDATE_BY", "admin");
				
				boolean b = openService.update(map);
				if(b){
					output("0000","修改成功");
				}else{
					output("9999","修改失败");
				}
				return;
			}
			
			//是添加
			//先查询出来商铺的服务类型
			map.put("sqlMapId", "findServiceTypeIdByName");
			Map serviceMap = (Map) openService.queryForObject(map);
			String serviceId = (String) serviceMap.get("SERVICE_PK");
			if(serviceId == null){
				output("9999","请确认服务类型是否有误");
				return;
			}
			
			map.put("sqlMapId", "insertShop");
			map.put("CREATE_BY", "admin");
			map.put("SERVICETYPE_FK", serviceId);
			String insert = openService.insert(map);
			
			if(insert == null){
				output("9999","保存失败");
				return;
			}
			//插入用户商铺中间表
			map.put("sqlMapId", "insertUserShop");
			map.put("USER_ID", "4b8cea73b03a4ddfacf8fbaf7a31028d");
			map.put("SHOP_ID", insert);
			
			String insert2 = openService.insert(map);
			
			if(insert2 != null){
				output("0000","保存成功");
			}else{
				output("9999","保存失败");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
		
			transactionManager.rollback(status);
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
			map.put("USER_FK","4b8cea73b03a4ddfacf8fbaf7a31028d");
			map.put("sqlMapId", "findAgentShopInfo");
			List<Map<String, Object>> reMap  = openService.queryForList(map);
			for(int i = 0;i< reMap.size();i++){
				Map<String,Object> singleMap = reMap.get(i);
				if(singleMap.get("OVER_DATA")!=null){
					reMap.get(i).put("DAYS", countDays(singleMap.get("OVER_DATA").toString()));
				}else{
					reMap.get(i).put("DAYS","已过期");
				}
			}
			output("0000",reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			output("9999","查询失败");
		}
	}
	
	/**
	 * 创建商户认领店铺二维码
	 * @author wangjinglong
	 */
	@RequestMapping(value="/Shop_create_createShopClaimQrCode",method = RequestMethod.GET)
	public void createShopClaimQrCode(){
		try {
			Map<String, Object> map = getParameterMap();
			String url = CommonUtil.getPath("Auth-wx-qrcode-url");
			url = url.replace("STATE",map.get("SHOPID").toString()).replace("REDIRECT_URI", URLEncoder.encode(CommonUtil.getPath("project_url").replace("DATA", "responseShopClaim"), "UTF-8"));
			BufferedImage image = QRCode.genBarcode(url,
							200, 200);
			response.setContentType("image/png");
			response.setHeader("pragma", "no-cache");
			response.setHeader("cache-control", "no-cache");
			response.reset();
			ImageIO.write(image, "png", response.getOutputStream());
			
		} catch (ExceptionVo | WriterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 认领店铺用户扫码后的功能实现
	 * @author wangjinglong
	 */
	@RequestMapping("/responseShopClaim")
	public void responseShopClaim(HttpServletResponse reponse){
		try {
			String code = request.getParameter("code");
			if (null != code && !"".equals(code)) {
			String openId = getOpenIdByCode(code);
			String state = request.getParameter("state");
			Map<String,Object> param = new HashMap<String,Object>();
			PrintWriter out = reponse.getWriter();
			String userId = "";
			String tagId = "";
			String roleId = "";
			/*查询店员端标签ID*/
			param.put("USER_TAG_NAME", "店员端");
			param.put("sqlMapId","findUserTagIdByUserTagName");
			Map<String, Object> resultUserTagMap = (Map<String, Object>)openService.queryForObject(param);
			if(resultUserTagMap!=null && resultUserTagMap.get("USER_TAG_ID") != null){
				tagId = resultUserTagMap.get("USER_TAG_ID").toString();
			}
			/*查询店长角色ID*/
			param.clear();
			param.put("ROLE_NAME", "店长");
			param.put("sqlMapId","findRolePKByRoleName");
			Map<String, Object> resultRoleMap = (Map<String, Object>)openService.queryForObject(param);
			if(resultRoleMap!=null && resultRoleMap.get("ROLE_PK") != null){
				roleId = resultRoleMap.get("ROLE_PK").toString();
			}
			if (openId!=null && !openId.equals("") ) {
				param.clear();
			/*	判断当前用户openID是否存在*/
				param.put("OPENID", openId);
				param.put("sqlMapId", "checkUserExits");
				List<Map<String, Object>> checkUserList = openService.queryForList(param);
				if (checkUserList.size() == 0) {
					/*没存在插入到用户表*/
					param.clear();
					param.put("OPENID", openId);
					param.put("sqlMapId", "insertUserInitOpenId");
					userId = openService.insert(param);
				}else{
					userId = checkUserList.get(0).get("USER_PK").toString();
				}
			}
			/*添加到用户商铺中间表里*/
			param.clear();
			param.put("FK_USER", userId);
			param.put("FK_ROLE", roleId);
			param.put("FK_USER_TAG", tagId);
			param.put("FK_SHOP", state);
			param.put("INSERT_TIME",StringDeal.getStringDate());
			param.put("sqlMapId", "insertUserShop");
			String insertResult = openService.insert(param);
			/*插入成功修改商铺认领状态*/
			if(insertResult!=null&&insertResult.equals("")){
				param.clear();
				param.put("SHOP_FK", state);
				param.put("SHOP_STATE", 1);
				param.put("sqlMapId", "UpdateShopState");
				openService.update(param);
				output("9999","认领成功");
			}
			} 
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
	private String getOpenIdByCode(String code) {
		String url = CommonUtil.getPath("WX_GET_OPENID_URL");
		url = url.replace("CODE", code);
		System.out.println("getOpenIdByCode=" + url);
		String res = CommonUtil.get(url);
		Object succesResponse = JSON.parse(res);
		Map result = (Map) succesResponse;

		String openId = result.get("openid").toString();
		System.out.println(openId);
		return openId;
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
	
	/**
	 * 
	 * @date 2018年8月4日 下午8:05:45 
	 * @author lps
	 * 
	 * @Description:  根据商铺id查询商铺信息
	 * @return void 
	 *
	 */
	@RequestMapping(value="/Shop_select_findShopInfoById")
	public void findShopInfoById(){
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "SelectByPrimaryKey");
			map.put("SHOP_FK", map.get("shopid"));
			Map reMap = (Map)openService.queryForObject(map);
			if(reMap == null){
				output("9999","查询失败");
				return;
			}
			output("0000",reMap);
		} catch (Exception e) {
			e.printStackTrace();
			output("9999","查询失败");
		}
		
		
		
	}
	
	
}
