package cn.wifiedu.ssm.controller;


	import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
	import java.util.Map;

	import javax.annotation.Resource;
import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;
import org.apache.poi.ss.usermodel.Picture;
import org.springframework.context.annotation.Scope;
	import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.wifiedu.core.controller.BaseController;
	import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CommonUtil;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.PictureUtil;
import cn.wifiedu.ssm.util.StringDeal;
import cn.wifiedu.ssm.util.redis.JedisClient;
import cn.wifiedu.ssm.util.redis.RedisConstants;

		/**
		 * 
		 * @author lps
		 * @date 2018年8月14日 上午1:26:23
		 * @Description:
		 * @version V1.0
		 *
		 */
		@Controller
		@Scope("prototype")
		public class GoodsController extends BaseController {

			private static Logger logger = Logger.getLogger(GoodsController.class);
			
			public static final long onePicMaxSize = 2100000L;
			public static final long allPicMaxSize = 6300000L;
			public static final int maxPicNum = 10;
			
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
			 * 
			 * @date 2018年8月1日 上午12:25:36 
			 * @author lps
			 * 
			 * @Description: 查询商品基本信息
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/Goods_query_findBaseGoosList")
			public void findBaseGoosList(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					map.put("sqlMapId", "selectBaseGoosListByGid");
					
					List<Map<String, Object>> reMap = openService.queryForList(map);
					
					output("0000", reMap);
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			/**
			 * 
			 * @date 2018年8月14日 上午2:10:17 
			 * @author lps
			 * 
			 * @Description: 根据商品id删除商品
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			
			@RequestMapping("/Goods_delete_removeGoodsById")
			public void removeGoodsById(HttpServletRequest request,HttpSession seesion){
				try {
					Map<String, Object> map = getParameterMap();
					
					map.put("sqlMapId", "selectGoodsCountByGoodsId");
					Map reMap = (Map)openService.queryForObject(map);
					long areaCount = (long) reMap.get("goodsCount");//获取商品当前类别下商品的总数量
					long bef_pxxh =  Long.parseLong((String) reMap.get("GOODS_PXXH"));//获取当前桌位的排序序号
					
					//判断当前是不是最后一个数据
					if(areaCount != bef_pxxh){
						//如果不等于，进行排序序号重置
						map.put("sqlMapId", "updateGoodsPxxhById");
						map.put("sub", true);
						map.put("GTYPE_ID", reMap.get("GTYPE_FK"));
						map.put("SMALL_GOODS_PXXH", bef_pxxh);
						
						boolean update = openService.update(map);
						if(!update){
							output("9999", "删除失败！");
							return;
						}
					}
					//开始删除
					map.put("sqlMapId", "deleteGoodsById");
					boolean delete = openService.delete(map);
					if(delete){
						output("0000", "删除成功");
						return;
					}
					output("9999", "删除失败");
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
			}
			
			
			/**
			 * 
			 * @date 2018年8月16日 下午5:18:01 
			 * @author lps
			 * 
			 * @Description: 添加商品
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/Goods_insert_insertGoods")
			public void insertGoods(HttpServletRequest request,HttpSession seesion){
		
				try {
					Map<String, Object> map = getParameterMap();
					
					//base64转图片
					String picURLStr = (String)map.get("PICTURE_URL");
					if(StringUtils.isNotBlank(picURLStr) && picURLStr.length()*3/4 > allPicMaxSize){//根据base64字符填充规则，base64大小*（3/4）即为原图片大小
						output("9999", "图片总大小不允许超过6M");
						return;
					}
					JSONArray base64Pic = JSON.parseArray(picURLStr);
					Map picMap = new LinkedHashMap<String, String>();
					if(base64Pic.size() > maxPicNum){
						output("9999", "上传图片不能超过"+ maxPicNum +"张");
						return;
					}
					for (Object pic : base64Pic) {//循环判断每张base64图片
						String picStr = (String)pic;
						long picSize = picStr.length()*3/4;
						if(picSize > onePicMaxSize){
							output("9999", "单张图片大小不允许超过2M");
							return;
						}
						String picUrl = PictureUtil.base64ToImage(picStr, "assets/goodspic");
						picMap.put(picUrl, picSize);
					}
					
					map.put("PICTURE_URL",picMap.toString());
					
					/**排序序号操作*/
					//先查询当前商品的类别下所有商品的总数量
					map.put("sqlMapId", "findGoodsCountByGtypeId");
					
					Map reMap = (Map)openService.queryForObject(map);
					long goodsCount = (long) reMap.get("goodsCount");
					Integer pxxh = Integer.parseInt((String) map.get("GOODS_PXXH"));
					//判断，如果当前排序序号不是最后一个，开始把当前序号后边的依次加一
					if(pxxh - 1 != goodsCount){
						map.put("sqlMapId", "updateGoodsPxxhById");
						map.put("SMALL_GOODS_PXXH", pxxh);
						boolean b = openService.update(map);
						if(!b){
							output("9999", "添加失败！");
							return;
						}
					}
					
					
					//开始插入
					map.put("sqlMapId", "insertGoods");
					String insert = openService.insert(map);
					if(insert == null){
						output("0000", "添加失败");
						return;
					}
					output("0000", "添加成功");
					return;
				} catch (Exception e) {
					output("9999", e);
					return;
				}
			}
		
	

		}
