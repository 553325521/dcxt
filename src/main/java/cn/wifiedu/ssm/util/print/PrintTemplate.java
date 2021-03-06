package cn.wifiedu.ssm.util.print;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author kqs
 * @time 2018年11月12日 - 下午9:25:04
 * @description:
 */
public abstract class PrintTemplate {
    public static final String DINNER_UNIT = "人";
    public static final String BILLID = "订单编号：";
    public static final String BILL_CREATE_TIME = "下单时间：";
    public static final String DINNER_COUNT = "就餐人数：";
    public static final String TABLE_SIT = "餐位号：";
    public static final String DISHES_NAME = "商品名称";
    public static final String NOTES = "描述";
    public static final String COUNTS = "数量";
    public static final String TOTAL = "合计";
    public static final String BWL = "备物联";
    public static final String DZL = "对账联";
    public static final String JSL = "结算联";
    public static final String PREV = "预订单";

    private static final SimpleDateFormat HOURSANDMINUTES = new SimpleDateFormat("HH:mm");

    public static String dealBillId(String billId) {
        StringBuilder result = new StringBuilder();
        String part1 = billId.substring(0, 4);
        String part2 = billId.substring(4, 8);
        String part3 = billId.substring(8, 12);
        String part4 = billId.substring(12, 14);
        return result.append(part1).append(" ")
                .append(part2).append(" ")
                .append(part3).append(" ")
                .append(part4).toString();
    }

    public static String getTimeString(Date date) {
        return HOURSANDMINUTES.format(date);
    }

    // 堂点备物
    public abstract String getInStoreBWTemplate();
    // 外卖备物
    public abstract String getOutStoreBWTemplate();
    // 堂点对账
    public abstract String getInStoreDZTemplate();
    // 外卖对账
    public abstract String getOutStoreDZTemplate();
    // 堂点结算
    public abstract String getInStoreJSTemplate();
    // 外卖结算
    public abstract String getOutStoreJSTemplate();
    
    public static int getStrLen(String data) {
        int length = 0;
        try {
            length = data.getBytes("GB2312").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return length;
    }

}
