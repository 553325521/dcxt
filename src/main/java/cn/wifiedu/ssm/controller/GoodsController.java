package cn.wifiedu.ssm.controller;


	import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import cn.wifiedu.core.controller.BaseController;
import cn.wifiedu.core.service.OpenService;
import cn.wifiedu.ssm.util.CookieUtils;
import cn.wifiedu.ssm.util.PictureUtil;
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
			
			public static final long ONE_PIC_MAXSIZE = 2100000L;	//允许的单张图片最大大小
			public static final long ALL_PICMAX_SIZE = 6300000L;	//允许的所有图片最大大小
			public static final int MAX_PIC_NUM = 10;				//允许的图片最大数量

			private static final String GOODS_PICPATH = "assets/img/goodspic";	//图片存储位置
			
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
					
					//先查询出商品图片信息
					map.put("sqlMapId", "selectGoosByGid");
					Map goodsMap = (Map) openService.queryForObject(map);
					
					//开始删除
					map.put("sqlMapId", "deleteGoodsById");
					boolean delete = openService.delete(map);
					if(delete){
						//删除图片
						deletePicByGoodId(goodsMap);
						
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
					//先查询当前类别下所有商品的总数量
					map.put("sqlMapId", "findGoodsCountByGtypeId");
					Map reMap = (Map)openService.queryForObject(map);
					long goodsCount = (long) reMap.get("goodsCount");
					Integer pxxh = Integer.parseInt((String) map.get("GOODS_PXXH"));
					//判断序号如果小于0或大于总数，返回错误
					if(pxxh <= 0){
						output("9999", "排序序号不允许为负数！");
						return;
					}else if(pxxh > goodsCount + 1){
						output("9999", "排序序号不允许大于商品总数量！");
						return;
					}
					
					//元转换成分存数据库
					map.put("GOODS_PRICE", (long)(Double.parseDouble((String)map.get("GOODS_PRICE"))*100));
					String true_price = (String)map.get("GOODS_TRUE_PRICE");
					if(StringUtils.isNotBlank(true_price)){
						map.put("GOODS_TRUE_PRICE", (long)(Double.parseDouble(true_price)*100));
					}
					
					
					//检验图片大小是否超出限制
					String picURLStr = (String)map.get("PICTURE_URL");
					String checkUploadPic = checkUploadPic(picURLStr);
					if(checkUploadPic != null){
						output("9999", checkUploadPic);
						return;
					}
					
					JSONArray base64Pic = JSON.parseArray(picURLStr);
					Map picMap = new LinkedHashMap<String, String>();
					//开始保存图片
					for (Object pic : base64Pic) {
						String picStr = (String)pic;
						long picSize = picStr.length()*3/4;
						String picUrl = PictureUtil.base64ToImage(picStr, GOODS_PICPATH);
						picMap.put(picUrl, picSize);
					}
					
					map.put("PICTURE_URL", JSON.toJSONString(picMap));
					
					/**排序序号操作*/
					
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
					//判断序号如果小于0，返回错误
					if(after_pxxh <= 0){
						output("9999", "排序序号不允许为负数！");
						return;
					}
					
					//查询该类别下商品总数量
					map.put("sqlMapId", "findGoodsCountByGoodsId");
					Map reMap2 = (Map)openService.queryForObject(map);
					long goodsCount = (long) reMap2.get("goodsCount");
					//判断序号如果大于总数，返回错误
					if(after_pxxh > goodsCount){
						output("9999", "排序序号不允许大于商品总数量！");
						return;
					}
					
					//取出新的pic信息和旧的pic信息
					List<String> newPicList = (List<String>)JSON.parse((String) map.get("PICTURE_URL"));
					Map<String,Long> picUrlMap = JSON.parseObject((String) reMap.get("PICTURE_URL"), LinkedHashMap.class,Feature.OrderedField);
					if(newPicList != null && newPicList.size() > MAX_PIC_NUM){
						output("9999", "上传图片不能超过"+ MAX_PIC_NUM +"张");
						return;
					}
					
					//两两结合判断
					int index = 0;
					String base64Pic = "";
					//判断总容量和单张图片有没有超出大小
					String checkUploadPic = checkUploadPic(newPicList,picUrlMap);
					if(checkUploadPic != null){
						output("9999", checkUploadPic);
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
							String picUrl = PictureUtil.base64ToImage(base64Pic, GOODS_PICPATH);
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
							String picUrl = PictureUtil.base64ToImage(base64Pic, GOODS_PICPATH);
							picMap.put(picUrl, base64Pic.length()*3/4);
						}
					}
					map.put("PICTURE_URL", JSON.toJSONString(picMap));
					
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
					String true_price = (String)map.get("GOODS_TRUE_PRICE");
					if(StringUtils.isNotBlank(true_price)){
						map.put("GOODS_TRUE_PRICE", (long)(Double.parseDouble(true_price)*100));
					}
					
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
			
			
			
			
			/**
			 * 
			 * @date 2018年8月27日 下午12:30:51 
			 * @author lps
			 * 
			 * @Description:  根据shopid获取该店铺所有商品
			 * @return void 
			 *
			 */
			@RequestMapping("Shop_query_findAllGoodsBySopIdGradeByCate")
			public void findAllGoodsByShopId(){
				try {
					
					String token = CookieUtils.getCookieValue(request, "DCXT_TOKEN");
					String userJson = jedisClient.get(RedisConstants.REDIS_USER_SESSION_KEY + token);
					JSONObject userObj = JSON.parseObject(userJson);
					
					Map map = getParameterMap();
					map.put("SHOP_ID", userObj.get("FK_SHOP"));
					//先根据shopId查询出该店铺所有商品
					map.put("sqlMapId", "findAllGoodsByShopId");
					map.put("IS_USE", true);
					List<Map<String,String>> goodsList =  openService.queryForList(map);
					
					if(goodsList == null || goodsList.size() == 0){
						output("9999", "该商铺还没有商品");
						return;
					}
					//list转Map，Key为类别id
					Map<Object, List<Map<String, String>>> goodsMap = goodsList.stream().
							collect(Collectors.groupingBy(Map->Map.get("GTYPE_FK")));
					
					//查询该店铺所有类别
					map.put("sqlMapId", "findAllGtypeIdByShopId");
					List<Map<String,String>> catesList =  openService.queryForList(map);
					//list转map，key为类别id
					Map<String, Map<String, String>> collect = catesList.stream().collect(Collectors.toMap(Map->Map.get("GTYPE_PK"), a->a,(k1,k2)->k1));
					
					//遍历商品map,把新数据放到新Map中,新map的key转换成所有父类别集合名字
					Map newGoodsMap = new HashMap<String, List<Map<String, String>>>();
					goodsMap.forEach((key,value)->{
						String cateName = "";
						String CatePath = collect.get(key).get("GTYPE_PATH");//类别路径
						String[] cates = CatePath.split("/");
						
						for(int i=1; i<cates.length;i++){
							cateName += collect.get(cates[i]).get("GTYPE_NAME") + "/";
						}
						cateName = cateName.substring(0, cateName.length()-1);
						
						newGoodsMap.put(cateName, value);
					});
					
					
					output("0000", newGoodsMap);
					
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("error", e);
					output("9999", " Exception ", e);
				}
				
			}
			
			
			/**
			 * 
			 * @author lps
			 * 
			 * @Description:  通过商品Id修改商品数量
			 * @return void 
			 *
			 */
			@RequestMapping("Shop_update_updateGoodsNumByGoodsId")
			public void updateGoodsNumByGoodsId(){
				try {
					Map map = getParameterMap();
					map.put("sqlMapId", "updateGoodsNumByGoodId");
					map.put("GOODS_NUM", "0");
					map.put("GOODS_ID", map.get("CURRENT_CLICK"));
					
					boolean b = openService.update(map);
					
					if(b){
						output("0000", "操作成功");
						return;
					}
					output("9999", "操作失败");
					return;
					
				} catch (Exception e) {
					e.printStackTrace();
					output("9999", "操作失败");
					return;
					
				}
			}
			
			
			/**
			 * 
			 * @date 2018年8月18日 下午3:50:42 
			 * @author lps
			 * 
			 * @Description: 		根据数据库的图片信息和要插入的图片信息进行比较大小有没有超出限制
			 * @param newPicList	新插入的图片信息
			 * @param picUrlMap		数据库存储的图片信息
			 * @return 
			 * @return String 
			 *
			 */
			private String checkUploadPic(List<String> newPicList, Map<String, Long> picUrlMap) {
				int index = 0;
				long onePicSize = 0;
				long allPicSize = 0;
				String base64Pic = "";
				for (Entry<String, Long> entry : picUrlMap.entrySet()) {
					base64Pic = newPicList.get(index);
					if(base64Pic == null){
						break;
					}
					if(base64Pic.indexOf("data:image/") != -1){//说明该位置被更换，
						onePicSize = base64Pic.length()*3/4;
						if(onePicSize > ONE_PIC_MAXSIZE){
							return "单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE/1000000) + "M";
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
						if(onePicSize > ONE_PIC_MAXSIZE){
							return "单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE/1000000) + "M";
						}
						allPicSize += onePicSize;
					}
				}
				
				if(allPicSize > ALL_PICMAX_SIZE){
					return "图片总大小不允许超过" + Math.floor(ALL_PICMAX_SIZE/1000000) + "M";
				}
				return null;
			}

			/**
			 * 
			 * @date 2018年8月18日 下午3:39:12 
			 * @author lps
			 * 
			 * @Description: 	检查图片大小限制
			 * @param picURLStr
			 * @return 
			 * @return String 
			 *
			 */
			private String checkUploadPic(String picURLStr) {
				if(StringUtils.isNotBlank(picURLStr) && picURLStr.length()*3/4 > ALL_PICMAX_SIZE){//根据base64字符填充规则，base64大小*（3/4）即为原图片大小
					return "图片总大小不允许超过" + Math.floor(ALL_PICMAX_SIZE/1000000) + "M";
				}
				JSONArray base64Pic = JSON.parseArray(picURLStr);
				if(base64Pic.size() > MAX_PIC_NUM){
					return "上传图片不能超过"+ MAX_PIC_NUM +"张";
				}
				//检验单张图片大小限制
				for (Object pic : base64Pic) {//循环判断每张base64图片
					String picStr = (String)pic;
					long picSize = picStr.length()*3/4;
					if(picSize > ONE_PIC_MAXSIZE){
						return "单张图片大小不允许超过" + Math.floor(ONE_PIC_MAXSIZE/1000000) + "M";
					}
				}
				return null;
			}
			
			/**
			 * 
			 * @date 2018年8月18日 下午3:56:53 
			 * @author lps
			 * 
			 * @Description: 	根据商品id删除商品图片
			 * @param map		包含GOODS_ID的的map
			 * @return 
			 * @return boolean 
			 * @throws Exception 
			 *
			 */
			private boolean deletePicByGoodId(Map<String, Object> goodsMap) throws Exception {
				Map<String,Long> picUrlMap = JSON.parseObject((String) goodsMap.get("PICTURE_URL"), LinkedHashMap.class,Feature.OrderedField);
				
				for (Entry<String, Long> entry : picUrlMap.entrySet()) {
					//删除图片
					boolean deletePic = PictureUtil.deletePic(entry.getKey());
					if(!deletePic) return false;
				}
				return true;
			}
			
			
			
			
			
		}
