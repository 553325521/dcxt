//package cn.wifiedu.ssm.util.print;
//
//import net.food.remotecommon.entity.Activity;
//import net.food.remotecommon.entity.Order;
//import net.food.remotecommon.entity.OrderGoods;
//import net.food.remotecommon.entity.Shop;
//
//import java.util.List;
//
///**
// * 80mm打印机
// * 生成各类打印样式模板
// * Created by Matrix on 2016/10/25.
// */
//public class PrintTemplate80MM extends PrintTemplate {
//    private Order order;
//    private Activity activity;
//    private Shop shop;
//
//    public PrintTemplate80MM(Order order, Shop shop) {
//        this.order = order;
//        this.shop = shop;
//    }
//
//    public PrintTemplate80MM(Activity activity, Shop shop) {
//        this.activity = activity;
//        this.shop=shop;
//    }
//
//    //换行
//    private static final String LINE = "*";
//    //星号换行 47个*
//    private static final String STARLINE = "\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*" +
//            "\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*";
//    //减号换行 47个-
//    private static final String SUBLINE = "-----------------------------------------------";
//    //加粗
//    private static final String BOLD = "<S00100>";
//    //大号字体
//    private static final String BIG = "<big>";
//    //居左加粗
//    private static final String LEFT_BOLD = "<S00100>";
//    //居中加高加粗
//    private static final String CENTER_BIG = "<S01101>";
//    //居中加粗
//    private static final String CENTER_BOLD = "<S00101>";
//    //居右加粗
//    private static final String RIGHT_BOLD = "<S00102>";
//    //居左加高加粗
//    private static final String LEFT_BIG = "<S01100>";
//    //居左加高加粗
//    private static final String RIGHT_BIG = "<S01102>";
//    //居左
//    private static final String LEFT = "<S00000>";
//    //居中
//    private static final String CENTER = "<S00001>";
//    //居右
//    private static final String RIGHT = "<S00002>";
//    //二维码
//    private static final String QRC = "<qrcA>";
//    /**
//     * 切纸
//     */
//    private static final String CUT = "****<cutA1>";
//    private static final String CENTER_BLANK = "                        ";
//
//    @Override
//    public String getActivityForecastTemplate() {
//        return "";
//    }
//
//    @Override
//    public String getInStoreBWTemplate() {
//        StringBuilder result = new StringBuilder();
//        // 计算空格
//        String dishesSize = order.getOrderGoodsList().size() + "";
//        //总份数
//        int total=0;
//        String blankSpace = "";
//        for (int i = 0; i < 17 - dishesSize.length(); i++) {
//            blankSpace += " ";
//        }
//        String blank = "";
//        for (int i = 0; i < 27 - (order.getDinnerCount() + getStrLen(order.getTableInfo().getRemark())); i++) {
//            blank += " ";
//        }
//        String billData = "";
//        List<OrderGoods> orderGoodsList = order.getOrderGoodsList();
//        // 菜品信息
//        for (OrderGoods orderGoods : orderGoodsList) {
//            // 计算空格
//            int nameLen = getStrLen(orderGoods.getGoods().getGoodsName());
//            String middleBlank1 = "";
//            for (int i = 0; i < 32 - nameLen; i++) {
//                middleBlank1 += " ";
//            }
//            String countStr = orderGoods.getCount() + "";
//            String middleBlank2 = "";
//            for (int i=0; i < (14 - getStrLen("大份")) - countStr.length(); i ++) {
//                middleBlank2 += " ";
//            }
//            middleBlank2 = middleBlank2.substring(0, middleBlank2.length() - 1);
//            billData += LEFT_BIG +
//                   orderGoods.getGoods().getGoodsName() + middleBlank1 + "大份" + middleBlank2 + orderGoods.getCount() + orderGoods.getGoods().getUnit() +LINE;
//            total+=orderGoods.getCount();
//        }
//        return result.append(CENTER_BIG).append(shop.getShopName()).append(BWL).append(LINE)
//                .append(STARLINE).append(LINE)
//                .append(BOLD).append(BILLID).append(dealBillId(order.getId())).append(LINE)
//                .append(BOLD).append(BILL_CREATE_TIME).append(getTimeString(order.getCreateTime())).append(LINE)
//                .append(BOLD).append(DINNER_COUNT).append(order.getDinnerCount()).append(DINNER_UNIT).append(blank)
//                .append(TABLE_SIT).append(order.getTableInfo().getRemark()).append(LINE)
//                .append(SUBLINE).append(LINE)
//                .append(BOLD).append(DISHES_NAME).append("                        ").append(NOTES).append("        ").append(COUNTS).append(LINE)
//                .append(billData)
//                .append(SUBLINE).append(LINE)
//                .append(RIGHT_BIG).append(CENTER_BLANK).append(TOTAL).append(blankSpace).append(total).append("份").append(LINE)
//                .append(RIGHT_BIG).append(order.getRemark())
//                .append(CUT)
//                .toString();
//    }
//
//}
