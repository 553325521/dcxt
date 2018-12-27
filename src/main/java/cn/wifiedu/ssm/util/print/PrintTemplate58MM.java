package cn.wifiedu.ssm.util.print;

import java.util.List;
import java.util.Map;

import cn.wifiedu.ssm.util.StringDeal;

/**
 * 58mm打印机
 * 
 * @author kqs
 * @time 2018年11月12日 - 下午9:12:47
 * @description:生成各类打印样式模板 每行32个英文字母，16个中文字母
 */
public class PrintTemplate58MM extends PrintTemplate {

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

	// 订单
	private Map<String, Object> order;
	// 店铺
	private Map<String, Object> shop;

	public PrintTemplate58MM(Map<String, Object> order, Map<String, Object> shop) {
		this.order = order;
		this.shop = shop;
	}

	// public PrintTemplate58MM(Activity activity, Shop shop) {
	// this.activity = activity;
	// this.shop = shop;
	// }

	// 换行
	private static final String LINE = "*";
	// 星号换行 31个*
	private static final String STARLINE = "\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*";
	// 减号换行 31个-
	private static final String SUBLINE = "-------------------------------";
	// 加粗
	private static final String BOLD = "<S011>";
	// 大号字体
	private static final String BIG = "<big>";
	// 居左加粗
	private static final String LEFT_BOLD = "<S0110>";
	// 居中加粗
	private static final String CENTER_BOLD = "<S0111>";
	// 居右加粗
	private static final String RIGHT_BOLD = "<S0112>";
	// 居中
	private static final String CENTER = "<S0101>";
	// 居左
	private static final String LEFT = "<S0100>";
	// 居右
	private static final String RIGHT = "<S0102>";
	// 二维码
	private static final String QRC = "<qrc>";
	private static final String CENTER_BLANK = "                ";

	@Override
	public String getInStoreBWTemplate() {
		StringBuilder result = new StringBuilder();
		// 计算空格
		List<Map<String, Object>> orderGoodsList = (List<Map<String, Object>>) order.get("orderGoodsList");
		String dishesSize = orderGoodsList.size() + "";
		// 总份数
		int total = 0;
		String blankSpace = "";
		for (int i = 0; i < 9 - dishesSize.length(); i++) {
			blankSpace += " ";
		}
		String blank = "";
		// 人数
		int dinnerCount = Integer.valueOf(order.get("ORDER_RS").toString());
		// 桌台备注
		String tableInfoRemark = order.get("TABLES_NAME").toString();
		for (int i = 0; i < 19 - (dinnerCount + getStrLen(tableInfoRemark)); i++) {
			blank += " ";
		}
		String billData = "";
		// 菜品信息
		int n = 0;
		for (Map<String, Object> orderGoods : orderGoodsList) {
			// 计算空格
			int nameLen = getStrLen(orderGoods.get("ORDER_DETAILS_GNAME").toString());
			String middleBlank1 = "";
			for (int i = 0; i < 16 - nameLen; i++) {
				middleBlank1 += " ";
			}
			String countStr = (n + 1) + "";
			String middleBlank2 = "";
			String ORDER_DETAILS_FORMAT = "大份";
			if (orderGoods.containsKey("ORDER_DETAILS_FORMAT")) {
				ORDER_DETAILS_FORMAT = orderGoods.get("ORDER_DETAILS_FORMAT").toString();
			}
			for (int i = 0; i < (14 - getStrLen(ORDER_DETAILS_FORMAT)) - countStr.length(); i++) {
				middleBlank2 += " ";
			}
			middleBlank2 = middleBlank2.substring(0, middleBlank2.length() - 1);
			billData += BOLD + (orderGoods.get("ORDER_DETAILS_GNAME").toString()) + middleBlank1 + ORDER_DETAILS_FORMAT + middleBlank2 + (n + 1)
					+ orderGoods.get("ORDER_DETAILS_DW").toString() + LINE;
			total += Integer.valueOf(orderGoods.get("ORDER_DETAILS_FS").toString());
		}
		return result.append(CENTER_BOLD).append(order.get("SHOP_NAME").toString()).append(BWL).append(LINE).append(STARLINE)
				.append(LINE).append(BOLD).append(BILLID).append(dealBillId(order.get("ORDER_CODE").toString())).append(LINE).append(BOLD)
				.append(BILL_CREATE_TIME).append(getTimeString(StringDeal.strToDateLong(StringDeal.getStringDate()))).append(LINE).append(BOLD)
				.append(DINNER_COUNT).append(dinnerCount).append(DINNER_UNIT).append(blank).append(TABLE_SIT)
				.append(tableInfoRemark).append(LINE).append(SUBLINE).append(LINE).append(BOLD)
				.append(DISHES_NAME).append("        ").append(NOTES).append("       ").append(COUNTS).append(LINE)
				.append(billData).append(SUBLINE).append(LINE).append(BOLD).append(CENTER_BLANK).append(TOTAL)
				.append(blankSpace).append(total).append("份").append(LINE).append(RIGHT_BOLD).append("")
				.toString();
	}

	@Override
	public String getActivityForecastTemplate() {
		return null;
	}

}
