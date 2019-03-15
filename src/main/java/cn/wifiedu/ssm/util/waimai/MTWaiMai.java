package cn.wifiedu.ssm.util.waimai;

import com.sankuai.meituan.waimai.opensdk.api.API;
import com.sankuai.meituan.waimai.opensdk.api.OrderAPI;
import com.sankuai.meituan.waimai.opensdk.constants.ParamRequiredEnum;
import com.sankuai.meituan.waimai.opensdk.constants.PoiQualificationEnum;
import com.sankuai.meituan.waimai.opensdk.exception.ApiOpException;
import com.sankuai.meituan.waimai.opensdk.exception.ApiSysException;
import com.sankuai.meituan.waimai.opensdk.factory.APIFactory;
import com.sankuai.meituan.waimai.opensdk.vo.PoiParam;
import com.sankuai.meituan.waimai.opensdk.vo.SystemParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyuanbo02 on 15/12/9.
 */
public class MTWaiMai extends API{

    private final static SystemParam sysPram = new SystemParam("3987", "ff17fa297a9a89ffa62be0ff9ebf92bf");
    //    private final static String appPoiCode = "ceshi_POI_II";
    private final static String appPoiCode = "ceshi_02";

    public static void main(String[] args) {
//        poiGetIds();
//    	poiGetIds();
    	orderConfirm(1L);
//    	poiOnline("2898393");// 门店上线
    }

    public static void poiSave() {
        PoiParam PoiPram = new PoiParam();
        PoiPram.setApp_poi_code("ceshi_poi1");
        PoiPram.setName("我的门店");
        PoiPram.setAddress("我的门店的地址");
        PoiPram.setLatitude(40.810249f);
        PoiPram.setLongitude(117.502289f);
        PoiPram.setPhone("13425355733");
        PoiPram.setShipping_fee(2f);
        PoiPram.setShipping_time("09:00-13:30,19:00-21:40");
        PoiPram.setPic_url("http://cdwuf.img46.wal8.com/img46/525101_20150811114016/144299728957.jpg");
        PoiPram.setOpen_level(1);
        PoiPram.setIs_online(1);
        PoiPram.setPre_book_min_days(1);
        PoiPram.setPre_book_max_days(2);
        PoiPram.setApp_brand_code("zhajisong");
        PoiPram.setThird_tag_name("麻辣烫");

        try {
            String result = APIFactory.getPoiAPI().poiSave(sysPram, PoiPram);
            System.out.println(result);
        } catch (ApiOpException e) {
            e.printStackTrace();
        } catch (ApiSysException e) {
            e.printStackTrace();
        }
    }
    
    
    // 门店上线
    public static void poiOnline(String appPoiCode) {
   	 try {
   		 APIFactory.getPoiAPI().poiOnline(sysPram, appPoiCode);
        } catch (ApiOpException e) {
            e.printStackTrace();
        } catch (ApiSysException e) {
            e.printStackTrace();
        }
   }
    

    // 商家确认订单
    public static void orderConfirm(Long orderId) {
    	 try {
//    		 APIFactory.getOrderAPI().orderConfirm(sysPram, orderId);
    	     String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    	        //组织应用级参数
    	        Map<String, String> applicationParamsMap = new HashMap<>();
    	        applicationParamsMap.put("order_id", String.valueOf(orderId));
    	        
    	        //组织应用级参数
    	        
//    	        orderAPI.beforeMethod(sysPram, applicationParamsMap, ParamRequiredEnum.OrderConfirm);

    	        
    	        System.out.println(requestApi(methodName, sysPram, applicationParamsMap));
    	        
         } catch (Exception e) {
        	 e.printStackTrace();
         }
    }
    
    // 商家取消订单
    public static void orderCancel(Long orderId, String reason, String reasonCode) {
    	 try {
    		 APIFactory.getOrderAPI().orderCancel(sysPram, orderId, reason, reasonCode);
         } catch (ApiOpException e) {
             e.printStackTrace();
         } catch (ApiSysException e) {
             e.printStackTrace();
         }
    }

    
    // 订单送达
    public static void orderArrived(Long orderId) {
   	 try {
   		 APIFactory.getOrderAPI().orderArrived(sysPram, orderId);
        } catch (ApiOpException e) {
            e.printStackTrace();
        } catch (ApiSysException e) {
            e.printStackTrace();
        }
   }
    
    
    

//    public static void main(String[] args) {
//        byte[] imgData = {85,112,108,111,97,100,115,47,80,114,111,100,117,99,116,115,47,49,53,48,55,47,48,49,47,105,109,103,46,106,112,103};
//        try {
//            System.out.println(
//                APIFactory.getImageApi().imageUpload(systemParam, appPoiCode, imgData, "ceshi.jpg"));
//        } catch (ApiOpException e) {
//            e.printStackTrace();
//        } catch (ApiSysException e) {
//            e.printStackTrace();
//        }
//    }


}
