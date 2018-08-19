package cn.wifiedu.ssm.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;


/**
 * @author wangjinglong
 *商品类别与数据库交互
 */
@Controller
@Scope("prototype")
public class GoodsTypeController extends BaseController {
	
	@Resource
	OpenService openService;

	@Resource
	PlatformTransactionManager transactionManager;
	
	@Resource
	private JedisClient jedisClient;
	
	public OpenService getOpenService() {
		return openService;
	}

	public void setOpenService(OpenService openService) {
		this.openService = openService;
	}
	
	/**
	 * wjl
	 * 获取商品类别的序号
	 */
	@RequestMapping(value="/GoodsType_select_loadGoodsTypeOrder",method = RequestMethod.POST)
	public void GoodsType_select_loadGoodsTypeOrder() {
		// 如果未认证，跳转完善信息界面
		try {
			Map<String, Object> map  = getParameterMap();
			map.put("sqlMapId", "loadGoodsTypeOrder");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			output("0000",reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * wjl
	 * 添加商品类别
	 */
	@RequestMapping(value="/GoodsType_insert_insertGoodsType",method = RequestMethod.POST)
	public void GoodsType_insert_insertGoodsType() {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			map.put("GTYPE_ATTACH", 0);
			map.put("SHOP_FK", userObj.get("FK_SHOP"));
			map.put("CREATE_BY", userObj.get("USER_NAME"));
			map.put("sqlMapId", "insertGoodsType");
			String resultStr = openService.insert(map);
			if(resultStr!=null){
				if(map.get("GTYPE_PID").toString().equals("0")){
					map.put("GTYPE_PATH",0+"/"+resultStr);
				}else{
					map.put("GTYPE_PID",map.get("GTYPE_PID"));
					map.put("sqlMapId", "selectGoodsTypePNameByPID");
					Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
					map.put("GTYPE_PATH",reMap.get("GTYPE_PATH")+"/"+resultStr);
				}
				map.put("GTYPE_PK",resultStr);
				map.put("sqlMapId", "updateGoodsTypePath");
				boolean updateResult = openService.update(map);
				if(updateResult){
					output("0000", "保存成功");
				}else{
					output("9999","保存失败");
				}
			}else{
				output("9999","保存失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * wjl
	 * 显示商品类别
	 */
	@RequestMapping(value="/GoodsType_select_loadGoodsTypeListByPID",method = RequestMethod.POST)
	public void loadGoodsTypeListByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadGoodsTypeListByPID");
			List<Map<String, Object>> reList = openService.queryForList(map);
			for(int i = 0;i<reList.size();i++){
				String goodsTypeArea_JsonStr = reList.get(i).get("GTYPE_AREA").toString();
				JSONArray goodsTypeArea_JsonArray = JSON.parseArray(goodsTypeArea_JsonStr);
				String goodsTypeArea = "";
				for(int j = 0;j<goodsTypeArea_JsonArray.size();j++){
					JSONObject jsonObject = goodsTypeArea_JsonArray.getJSONObject(j);
					if(jsonObject.getBooleanValue("checked")){
						goodsTypeArea +=jsonObject.getString("name")+"/";
					}
				}
				if(goodsTypeArea.lastIndexOf("/") == goodsTypeArea.length()-1){
					goodsTypeArea = goodsTypeArea.substring(0, goodsTypeArea.length()-1);
				}
				reList.get(i).put("GTYPE_AREA", goodsTypeArea);
			}
			if(reList!=null&&reList.size()!=0){
				output("0000",reList);
			}else{
				output("9999","查询失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * wjl
	 * 根据PID查询下一级商品类别的数量和商品的数量
	 */
	@RequestMapping(value="/GoodsType_select_selectLastRecordCountByPID",method = RequestMethod.POST)
	public void selectLastRecordCountByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "loadGoodsTypeListByPID");
			List<Map<String, Object>> reList = openService.queryForList(map);
			map.put("sqlMapId", "selectGoodsByGoodsType");
			List<Map<String, Object>> goodsList = openService.queryForList(map);
			if(reList.size() ==0 && goodsList.size() == 0){
				output("0000","00");
			}else if(reList.size() ==0 && goodsList.size() != 0){
				output("0000","01");
			}else if(reList.size() !=0 && goodsList.size() == 0){
				output("0000","10");
			}else{
				output("0000","11");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * wjl
	 * 根据商品分类ID查询商品分类名称
	 */
	@RequestMapping(value="/GoodsType_select_selectGoodsTypePNameByPID",method = RequestMethod.POST)
	public void selectGoodsTypePNameByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectGoodsTypePNameByPID");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			output("0000",reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * wjl
	 * 查询商品分类编辑时候的父分类(查询跟父分类同级且没有商品的分类)
	 */
	@RequestMapping(value="/GoodsType_select_selectGTypeNameButNoGoodsByPID",method = RequestMethod.POST)
	public void selectGTypeNameButNoGoodsByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectGTypeNameButNoGoodsByPID");
			List<Map<String, Object>> reList = openService.queryForList(map);
			output("0000",reList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * wjl
	 * 查询商品分类编辑时候的父分类(查询跟父分类同级且没有商品的分类)
	 */
	@RequestMapping(value="/GoodsType_select_selectGTypeOrderByPID",method = RequestMethod.POST)
	public void selectGTypeOrderByPID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectGTypeOrderByPID");
			List<Map<String, Object>> reList = openService.queryForList(map);
			output("0000",reList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * wjl
	 * 添加商品类别
	 */
	@RequestMapping(value="/GoodsType_update_updateGoodsType",method = RequestMethod.POST)
	public void updateGoodsType() {
		try {
			Map<String, Object> map = getParameterMap();
			String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
			String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
			JSONObject userObj = JSON.parseObject(userJson);
			boolean updateOrderResult = false;
			if(Integer.parseInt(map.get("GTYPE_OLD_ORDER").toString()) < Integer.parseInt(map.get("GTYPE_ORDER").toString())){
				map.put("sqlMapId", "updateGoodsTypeOrderChangeLarge");
				updateOrderResult = openService.update(map);
			}else{
				map.put("sqlMapId", "updateGoodsTypeOrderChangeSmall");
				updateOrderResult = openService.update(map);
			}
			map.put("UPDATE_BY", userObj.get("USER_NAME"));
			map.put("sqlMapId", "updateGoodsType");
			boolean result = openService.update(map);
			if(result&&updateOrderResult){
				output("0000", "修改成功");
			}else{
				output("9999","修改失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * wjl
	 * 删除商品分类以及它下面的子分类
	 */
	@RequestMapping(value="/GoodsType_delete_deleteGoodsTypeByID",method = RequestMethod.POST)
	public void deleteGoodsTypeByID() {
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "deleteGoodsTypeByID");
			boolean deleteResult = openService.delete(map);
			if(deleteResult){
				output("0000","删除成功");
			}else{
				output("0000","删除失败");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @date 2018年8月17日 上午2:22:22 
	 * @author lps
	 * 
	 * @Description:  根据类别id查询类别的名字
	 * @return void 
	 *
	 */
	@RequestMapping(value="/GoodsType_select_findGoodsNameById")
	public void findGoodsNameById(){
		try {
			Map<String, Object> map = getParameterMap();
			map.put("sqlMapId", "selectGtypeNameById");
			Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
			output("0000",reMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}