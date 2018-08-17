package cn.wifiedu.ssm.controller;


	import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
	import java.util.Map;
import java.util.Map.Entry;

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
import com.alibaba.fastjson.parser.Feature;

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
					//元转换成分存数据库
					map.put("GOODS_PRICE", (long)(Double.parseDouble((String)map.get("GOODS_PRICE"))*100));
					map.put("GOODS_TRUE_PRICE", (long)(Double.parseDouble((String)map.get("GOODS_TRUE_PRICE"))*100));
					
					
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
					
					map.put("PICTURE_URL", JSONArray.toJSONString(picMap));
					
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
					map.put("CREATE_BY", "admin");
					String insert = openService.insert(map);
					if(insert == null){
						output("0000", "添加失败");
						return;
					}
					output("0000", "添加成功");
					return;
				} catch (Exception e) {
					e.printStackTrace();
					output("9999", e);
					return;
				}
			}
			
			/**
			 * 
			 * @date 2018年8月16日 下午10:31:31 
			 * @author lps
			 * 
			 * @Description: 根据商品id查询单个商品信息
			 * @param request
			 * @param seesion 
			 * @return void 
			 *
			 */
			@RequestMapping("/Goods_query_queryGoods")
			public void findGoodsById(HttpServletRequest request,HttpSession seesion){
				try {
					Map<String, Object> map = getParameterMap();
					map.put("sqlMapId", "selectGoosByGid");
					
					Map<String, Object> reMap = (Map<String, Object>) openService.queryForObject(map);
					if(reMap == null){
						output("9999", "出错啦");
						return;
					}
					
					output("0000", reMap);
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
				
				
				
			}
			
			/**
			 * 
			 * @date 2018年8月17日 上午3:29:11 
			 * @author lps
			 * 
			 * @Description:  根据商品ID修改商品信息
			 * @return void 
			 *
			 */
			@RequestMapping("/Goods_update_updateGoodsById")
			public void updateGoodsById(){
				try {
					Map<String, Object> map = getParameterMap();
					
					//先查询出来以前的一些信息，排序序号，该类别商品总数量，图片信息
					map.put("sqlMapId", "selectGoodsPartMessById");
					map.put("GOODS_ID", map.get("GOODS_PK"));
					Map reMap = (Map) openService.queryForObject(map);
					Integer bef_pxxh = Integer.parseInt((String)reMap.get("GOODS_PXXH"));//之前的排序序号
					Integer after_pxxh = Integer.parseInt((String)map.get("GOODS_PXXH"));//之后的排序序号
					//判断是否设置已启用而父节点未启用
					if("0".equals(reMap.get("GTYPE_STATE")) && "1".equals(map.get("IS_USE"))){
						output("9999", "该分类已停用，不允许启用该商品！");
						return;
					}
					
					//取出新的pic信息和旧的pic信息
					List<String> newPicList = (List<String>)JSONObject.parse((String) map.get("PICTURE_URL"));
					Map<String,Long> picUrlMap = (Map)JSON.parseObject((String) reMap.get("PICTURE_URL"), LinkedHashMap.class,Feature.OrderedField);
					if(newPicList != null && newPicList.size() > maxPicNum){
						output("9999", "图片总大小不允许超过6M");
						return;
					}
					
					//两两结合判断
					int index = 0;
					long onePicSize = 0;
					long allPicSize = 0;
					String base64Pic = "";
					//判断总容量和单张图片有没有超出大小
					for (Entry<String, Long> entry : picUrlMap.entrySet()) {
						base64Pic = newPicList.get(index);
						if(base64Pic == null){
							break;
						}
						if(base64Pic.indexOf("data:image/") != -1){//说明该位置被更换，
							onePicSize = base64Pic.length()*3/4;
							if(onePicSize > onePicMaxSize){
								output("9999", "单张图片大小不允许超过2M");
								return;
							}
						}else{
							onePicSize = Long.parseLong(entry.getValue()+"");
						}
						allPicSize += onePicSize;
						index ++;
					}
					//遍历完成，判断新的picList遍历完了吗
					if(newPicList.size() - index > 0){
						//还没有遍历完
						for (int i = index; i < newPicList.size(); i++) {
							base64Pic = newPicList.get(index);
							onePicSize = base64Pic.length()*3/4;
							if(onePicSize > onePicMaxSize){
								output("9999", "单张图片大小不允许超过2M");
								return;
							}
							allPicSize += onePicSize;
						}
					}
					
					if(allPicSize > allPicMaxSize){
						output("9999", "图片总大小不允许超过6M");
						return;
					}
					//到此处，说明图片大小不存在问题，开始插入
					index = 0;
					Map picMap = new LinkedHashMap<String, Long>();
					for (Entry<String, Long> entry : picUrlMap.entrySet()) {
						base64Pic = newPicList.get(index);
						if(base64Pic == null){
							PictureUtil.deletePic(entry.getKey());
							continue;
						}
						if(base64Pic.indexOf("data:image/") != -1){
							//旧的图片被替换，先删除旧的图片
							PictureUtil.deletePic(entry.getKey());
							//插入新的图片
							String picUrl = PictureUtil.base64ToImage(base64Pic, "assets/goodspic");
							picMap.put(picUrl, base64Pic.length()*3/4);
						}else{
							picMap.put(entry.getKey(), entry.getValue());
						}
						index++;
					}
					//遍历完成，判断新的picList遍历完了吗
					if(newPicList.size() - index > 0){
						//还没有遍历完
						for (int i = index; i < newPicList.size(); i++) {
							base64Pic = newPicList.get(index);
							//插入新的图片
							String picUrl = PictureUtil.base64ToImage(base64Pic, "assets/goodspic");
							picMap.put(picUrl, base64Pic.length()*3/4);
						}
					}
					map.put("PICTURE_URL", JSONArray.toJSONString(picMap));
					
					//判断当前商品排序等于之前的排序吗
					if(bef_pxxh != after_pxxh){
						Integer small_pxxh = 0;
						Integer big_pxxh = 0;
						//如果更新前的序号大于更新后的，那就把更新后的后边的序号依次加一
						small_pxxh = bef_pxxh > after_pxxh ? after_pxxh : bef_pxxh;
						big_pxxh = bef_pxxh > after_pxxh ? bef_pxxh : after_pxxh;
						if(bef_pxxh < after_pxxh){//如果更新前的序号小于更新后的，那就把更新后的后边的序号依次减一
							map.put("sub", true);
						}
						map.put("SMALL_GOODS_PXXH", small_pxxh);
						map.put("BIG_GOODS_PXXH", big_pxxh);
						map.put("sqlMapId", "updateGoodsPxxhById");
						boolean b = openService.update(map);
						if(!b){
							output("9999", "修改失败！");
							return;
						}
					}
					
					//开始修改信息
					map.put("sqlMapId", "updateGoodsById");
					map.put("UPDATE_BY", "admin");
					
					//元转换成分
					map.put("GOODS_PRICE", (long)(Double.parseDouble((String)map.get("GOODS_PRICE"))*100));
					map.put("GOODS_TRUE_PRICE", (long)(Double.parseDouble((String)map.get("GOODS_TRUE_PRICE"))*100));
					
					boolean update = openService.update(map);
					if(!update){
						output("9999", "修改失败");
						return;
					}
					output("0000", "修改成功");
					return;
				} catch (Exception e) {
					output("9999", " Exception ", e);
					return;
				}
				
			}
			public static void main(String[] args) {
				Map picMap = new LinkedHashMap<String, Long>();
				
				picMap.put("assets/goodspic/4defca04-203f-450e-921e-4bf72913a977.jpg", "5210");
				picMap.put("assets/goodspic/a1273806-7c69-4913-81ba-98ca12e73265.png", "81799");
				
				
				System.out.println(JSONArray.toJSONString(picMap));
			}

		}
