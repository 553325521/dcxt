package cn.wifiedu.ssm.util.print;

import java.util.List;
import java.util.Map;

import cn.wifiedu.ssm.util.Arith;
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
	public static final String MEAL_TIME = "配餐时间：";
	public static final String DINNER_COUNT = "就餐人数：";
	public static final String TABLE_SIT = "餐位号：";
	public static final String SHIP_METHOD = "配送方式：";
	public static final String DISHES_NAME = "商品名称";
	public static final String NOTES = "描述";
	public static final String COUNTS = "数量";
	public static final String TOTAL = "合计";
	public static final String BWL = "备物联";
	public static final String DZL = "对账联";
	public static final String JSL = "结算联";
	public static final String PREV = "预订单";
	public static final String DANJIA = "单价";
	public static final String XIAOJI = "小计";
	public static final String YOUHUI = "优惠";
	public static final String HYKH = "会员卡号：";
	public static final String KAQUAN = "卡券：";
	public static final String JIFEN = "积分：";
	public static final String CHUZHI = "储值：";
	public static final String FKFS = "付款方式：";
	public static final String FKSJ = "付款时间：";
	public static final String LXDH = "联系电话：";

	// 订单
	private Map<String, Object> order;
	// 店铺
	private Map<String, Object> shop;

	public PrintTemplate58MM(Map<String, Object> order, Map<String, Object> shop) {
		this.order = order;
		this.shop = shop;
	}

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
	private static final String QRC = "<qrcAA>";
	private static final String CENTER_BLANK = "                ";
	// 打印声音
	private static final String BEEP = "<BEEP5000,1,1,2>";
	// 切纸
	private static final String CUT = "<cutA1>";
	// 结束
	private static final String OVER = "#";
	// 人民币符号
	private static final String RMB = "¥";

	@Override
	// 堂点备物联
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
		for (Map<String, Object> orderGoods : orderGoodsList) {
			// 计算空格
			int nameLen = getStrLen(orderGoods.get("ORDER_DETAILS_GNAME").toString());
			String middleBlank1 = "";
			for (int i = 0; i < 16 - nameLen; i++) {
				middleBlank1 += " ";
			}
			String countStr = orderGoods.get("ORDER_DETAILS_FS").toString() + "";
			String middleBlank2 = "";
			String ORDER_DETAILS_FORMAT = "大份";
			if (orderGoods.containsKey("ORDER_DETAILS_FORMAT")) {
				ORDER_DETAILS_FORMAT = orderGoods.get("ORDER_DETAILS_FORMAT").toString();
			}
			for (int i = 0; i < (14 - getStrLen(ORDER_DETAILS_FORMAT)) - countStr.length(); i++) {
				middleBlank2 += " ";
			}
			middleBlank2 = middleBlank2.substring(0, middleBlank2.length() - 1);
			billData += BOLD + (orderGoods.get("ORDER_DETAILS_GNAME").toString()) + middleBlank1 + ORDER_DETAILS_FORMAT
					+ middleBlank2 + orderGoods.get("ORDER_DETAILS_FS").toString()
					+ orderGoods.get("ORDER_DETAILS_DW").toString() + LINE;
			total += Integer.valueOf(orderGoods.get("ORDER_DETAILS_FS").toString());
		}
		return result.append(CENTER_BOLD).append(order.get("SHOP_NAME").toString()).append(BWL).append(LINE)
				.append(STARLINE).append(LINE).append(BOLD).append(BILLID)
				.append(dealBillId(order.get("ORDER_CODE").toString())).append(LINE).append(BOLD)
				.append(BILL_CREATE_TIME).append(getTimeString(StringDeal.strToDateLong(StringDeal.getStringDate())))
				.append(LINE).append(BOLD).append(DINNER_COUNT).append(dinnerCount).append(DINNER_UNIT).append(blank)
				.append(TABLE_SIT).append(tableInfoRemark).append(LINE).append(SUBLINE).append(LINE).append(BOLD)
				.append(DISHES_NAME).append("        ").append(NOTES).append("       ").append(COUNTS).append(LINE)
				.append(billData).append(SUBLINE).append(LINE).append(BOLD).append(CENTER_BLANK).append(TOTAL)
				.append(blankSpace).append(total).append("份").append(LINE).append(RIGHT_BOLD)
				.append(order.get("WM_ORDER_REMARK").toString()).append(LINE).append(STARLINE).append(OVER).toString();
	}

	@Override
	// 外卖备物联
	public String getOutStoreBWTemplate() {
		StringBuilder result = new StringBuilder();
		// 计算空格
		List<Map<String, Object>> orderGoodsList = (List<Map<String, Object>>) order.get("orderGoodsList");
		String dishesSize = orderGoodsList.size() + "";
		int total = 0;
		String blankSpace = "";
		for (int i = 0; i < 9 - dishesSize.length(); i++) {
			blankSpace += " ";
		}
		String blank = "";
		// 人数
		int dinnerCount = Integer.valueOf(order.get("ORDER_RS").toString());
		// 配送方式
		String WM_ORDER_DELIVERY_PARTY = "";
		if ("0".equals(order.get("WM_ORDER_DELIVERY_PARTY").toString())) {
			WM_ORDER_DELIVERY_PARTY = "商家自送";
		} else if ("1".equals(order.get("WM_ORDER_DELIVERY_PARTY").toString())) {
			WM_ORDER_DELIVERY_PARTY = "平台配送";
		}
		for (int i = 0; i < 19 - (dinnerCount + getStrLen(WM_ORDER_DELIVERY_PARTY)); i++) {
			blank += " ";
		}

		String billData = "";
		// 菜品信息
		for (Map<String, Object> orderGoods : orderGoodsList) {
			// 计算空格
			int nameLen = getStrLen(orderGoods.get("ORDER_DETAILS_GNAME").toString());
			String middleBlank1 = "";
			for (int i = 0; i < 16 - nameLen; i++) {
				middleBlank1 += " ";
			}
			String countStr = orderGoods.get("ORDER_DETAILS_FS").toString() + "";
			String middleBlank2 = "";
			String ORDER_DETAILS_FORMAT = "大份";
			if (orderGoods.containsKey("ORDER_DETAILS_FORMAT")) {
				ORDER_DETAILS_FORMAT = orderGoods.get("ORDER_DETAILS_FORMAT").toString();
			}
			for (int i = 0; i < (14 - getStrLen(ORDER_DETAILS_FORMAT)) - countStr.length(); i++) {
				middleBlank2 += " ";
			}
			middleBlank2 = middleBlank2.substring(0, middleBlank2.length() - 1);
			billData += BOLD + (orderGoods.get("ORDER_DETAILS_GNAME").toString()) + middleBlank1 + ORDER_DETAILS_FORMAT
					+ middleBlank2 + orderGoods.get("ORDER_DETAILS_FS").toString()
					+ orderGoods.get("ORDER_DETAILS_DW").toString() + LINE;
			total += Integer.valueOf(orderGoods.get("ORDER_DETAILS_FS").toString());
		}

		return result.append(CENTER_BOLD).append(order.get("SHOP_NAME").toString()).append(BWL).append(LINE)
				.append(STARLINE).append(LINE).append(BOLD).append(BILLID)
				.append(dealBillId(order.get("ORDER_CODE").toString())).append(LINE).append(BOLD).append(MEAL_TIME)
				.append(order.get("WM_ORDER_SEND_TIME").toString()).append(LINE).append(BOLD).append(DINNER_COUNT)
				.append(dinnerCount).append(DINNER_UNIT).append(blank).append(SHIP_METHOD)
				.append(WM_ORDER_DELIVERY_PARTY).append(LINE).append(SUBLINE).append(LINE).append(BOLD)
				.append(DISHES_NAME).append("        ").append(NOTES).append("       ").append(COUNTS).append(LINE)
				.append(billData).append(SUBLINE).append(LINE).append(BOLD).append(CENTER_BLANK).append(TOTAL)
				.append(blankSpace).append(total).append("份").append(LINE).append(RIGHT_BOLD)
				.append(order.get("WM_ORDER_REMARK").toString()).append(LINE).append(STARLINE).append(OVER).toString();
	}

	@Override
	// 堂点对账联
	public String getInStoreDZTemplate() {
		StringBuilder result = new StringBuilder();
		// 计算空格
		List<Map<String, Object>> orderGoodsList = (List<Map<String, Object>>) order.get("orderGoodsList");
		String dishesSize = orderGoodsList.size() + "";
		// 人数
		int dinnerCount = Integer.valueOf(order.get("ORDER_RS").toString());
		// 第一行间隔
		String blank1 = "";
		for (int i = 0; i < 2 - (order.get("ORDER_RS").toString()).length(); i++) {
			blank1 += " ";
		}
		// 第二行间隔
		String blank2 = "";
		// 桌台备注
		String tableInfoRemark = order.get("TABLES_NAME").toString();
		for (int i = 0; i < 8 - getStrLen(tableInfoRemark); i++) {
			blank2 += " ";
		}
		// 总份数
		int total = 0;
		String blankSpace = "";
		for (int i = 0; i < 9 - dishesSize.length(); i++) {
			blankSpace += " ";
		}
		String billData = "";
		String totalAll = "0";
		// 菜品信息
		for (Map<String, Object> orderGoods : orderGoodsList) {
			// 计算空格
			int nameLen = getStrLen(orderGoods.get("ORDER_DETAILS_GNAME").toString());
			String middleBlank1 = "";
			for (int i = 0; i < 13 - nameLen; i++) {
				middleBlank1 += " ";
			}
			// 单价
			String ORDER_DETAILS_GMONEY = orderGoods.get("ORDER_DETAILS_GMONEY").toString();
			ORDER_DETAILS_GMONEY = Arith.div(Double.valueOf(ORDER_DETAILS_GMONEY), 100, 2) + "";
			String middleBlank2 = "";
			for (int i = 0; i < (7 - getStrLen(ORDER_DETAILS_GMONEY)); i++) {
				middleBlank2 += " ";
			}
			// 小计
			String totalMoney = Arith.mul(Double.valueOf(ORDER_DETAILS_GMONEY),
					Double.valueOf(orderGoods.get("ORDER_DETAILS_FS").toString())) + "";
			totalAll = Arith.add(Double.valueOf(totalMoney), Double.valueOf(totalAll)) + "";
			String middleBlank3 = "";
			for (int i = 0; i < (11 - getStrLen(
					orderGoods.get("ORDER_DETAILS_FS").toString() + orderGoods.get("ORDER_DETAILS_DW").toString()))
					- getStrLen(totalMoney); i++) {
				middleBlank3 += " ";
			}
			billData += BOLD + (orderGoods.get("ORDER_DETAILS_GNAME").toString()) + middleBlank1 + ORDER_DETAILS_GMONEY
					+ middleBlank2 + orderGoods.get("ORDER_DETAILS_FS").toString()
					+ orderGoods.get("ORDER_DETAILS_DW").toString() + middleBlank3 + totalMoney + LINE;
			total += Integer.valueOf(orderGoods.get("ORDER_DETAILS_FS").toString());
		}
		String middleBlank4 = "";
		for (int i = 0; i < 13 - getStrLen(total + ""); i++) {
			middleBlank4 += " ";
		}
		// 优惠金额
		String ORDER_YHMONEY = order.get("ORDER_YHMONEY").toString();
		ORDER_YHMONEY = Arith.div(Double.valueOf(ORDER_YHMONEY), 100, 2) + "";
		// 支付方式
		String ORDER_PAY_WAY = order.get("ORDER_PAY_WAY").toString();
		if ("0".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "待支付";
		} else if ("1".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "现金支付";
		} else if ("2".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "扫一扫";
		} else if ("21".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "微信扫一扫收款";
		} else if ("22".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "支付宝扫一扫收款";
		} else if ("23".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "银联扫一扫收款";
		} else if ("3".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "二维码";
		} else if ("31".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "微信二维码收款";
		} else if ("32".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "支付宝二维码收款";
		} else if ("4".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "pos支付";
		} else if ("5".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "储值支付";
		} else if ("6".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "其他";
		}
		// 应付金额
		String ORDER_YFMONEY = order.get("ORDER_YFMONEY").toString();
		ORDER_YFMONEY = Arith.div(Double.valueOf(ORDER_YFMONEY), 100, 2) + "";
		// 备注 order.get("WM_ORDER_REMARK").toString()
		return result.append(CENTER_BOLD).append(order.get("SHOP_NAME").toString()).append(DZL).append(LINE)
				.append(STARLINE).append(LINE).append(BOLD).append(BILLID)
				.append(dealBillId(order.get("ORDER_CODE").toString())).append(blank1).append(dinnerCount)
				.append(DINNER_UNIT).append(LINE).append(BOLD).append(BILL_CREATE_TIME)
				.append(getTimeString(StringDeal.strToDateLong(StringDeal.getStringDate()))).append(blank2)
				.append(TABLE_SIT).append(tableInfoRemark).append(LINE).append(BOLD).append(DISHES_NAME).append("     ")
				.append(DANJIA).append("   ").append(COUNTS).append("   ").append(XIAOJI).append(LINE).append(billData)
				.append(BOLD).append(middleBlank4).append(total).append("份").append(blankSpace).append(TOTAL)
				.append(totalAll).append(LINE).append(RIGHT_BOLD).append(YOUHUI).append(ORDER_YHMONEY).append(LINE)
				.append(RIGHT_BOLD).append(ORDER_PAY_WAY).append(ORDER_YFMONEY).append(LINE).append(STARLINE)
				.append(OVER).toString();
	}

	@Override
	// 堂点结算联
	public String getInStoreJSTemplate() {
		StringBuilder result = new StringBuilder();
		// 计算空格
		List<Map<String, Object>> orderGoodsList = (List<Map<String, Object>>) order.get("orderGoodsList");
		String dishesSize = orderGoodsList.size() + "";
		// 人数
		int dinnerCount = Integer.valueOf(order.get("ORDER_RS").toString());
		// 第一行间隔
		String blank1 = "";
		for (int i = 0; i < 2 - (order.get("ORDER_RS").toString()).length(); i++) {
			blank1 += " ";
		}
		// 第二行间隔
		String blank2 = "";
		// 桌台备注
		String tableInfoRemark = order.get("TABLES_NAME").toString();
		for (int i = 0; i < 8 - getStrLen(tableInfoRemark); i++) {
			blank2 += " ";
		}
		// 总份数
		int total = 0;
		String blankSpace = "";
		for (int i = 0; i < 9 - dishesSize.length(); i++) {
			blankSpace += " ";
		}
		String billData = "";
		String totalAll = "0";
		// 菜品信息
		for (Map<String, Object> orderGoods : orderGoodsList) {
			// 计算空格
			int nameLen = getStrLen(orderGoods.get("ORDER_DETAILS_GNAME").toString());
			String middleBlank1 = "";
			for (int i = 0; i < 13 - nameLen; i++) {
				middleBlank1 += " ";
			}
			// 单价
			String ORDER_DETAILS_GMONEY = orderGoods.get("ORDER_DETAILS_GMONEY").toString();
			ORDER_DETAILS_GMONEY = Arith.div(Double.valueOf(ORDER_DETAILS_GMONEY), 100, 2) + "";
			String middleBlank2 = "";
			for (int i = 0; i < (7 - getStrLen(ORDER_DETAILS_GMONEY)); i++) {
				middleBlank2 += " ";
			}
			// 小计
			String totalMoney = Arith.mul(Double.valueOf(ORDER_DETAILS_GMONEY),
					Double.valueOf(orderGoods.get("ORDER_DETAILS_FS").toString())) + "";
			totalAll = Arith.add(Double.valueOf(totalMoney), Double.valueOf(totalAll)) + "";
			String middleBlank3 = "";
			for (int i = 0; i < (11 - getStrLen(
					orderGoods.get("ORDER_DETAILS_FS").toString() + orderGoods.get("ORDER_DETAILS_DW").toString()))
					- getStrLen(totalMoney); i++) {
				middleBlank3 += " ";
			}
			billData += BOLD + (orderGoods.get("ORDER_DETAILS_GNAME").toString()) + middleBlank1 + ORDER_DETAILS_GMONEY
					+ middleBlank2 + orderGoods.get("ORDER_DETAILS_FS").toString()
					+ orderGoods.get("ORDER_DETAILS_DW").toString() + middleBlank3 + totalMoney + LINE;
			total += Integer.valueOf(orderGoods.get("ORDER_DETAILS_FS").toString());
		}
		String middleBlank4 = "";
		for (int i = 0; i < 13 - getStrLen(total + ""); i++) {
			middleBlank4 += " ";
		}
		// 优惠金额
		String ORDER_YHMONEY = order.get("ORDER_YHMONEY").toString();
		ORDER_YHMONEY = Arith.div(Double.valueOf(ORDER_YHMONEY), 100, 2) + "";
		// 支付方式
		String ORDER_PAY_WAY = order.get("ORDER_PAY_WAY").toString();
		if ("0".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "待支付";
		} else if ("1".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "现金支付";
		} else if ("2".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "扫一扫";
		} else if ("21".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "微信扫一扫收款";
		} else if ("22".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "支付宝扫一扫收款";
		} else if ("23".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "银联扫一扫收款";
		} else if ("3".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "二维码";
		} else if ("31".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "微信二维码收款";
		} else if ("32".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "支付宝二维码收款";
		} else if ("4".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "pos支付";
		} else if ("5".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "储值支付";
		} else if ("6".equals(ORDER_PAY_WAY)) {
			ORDER_PAY_WAY = "其他";
		}
		// 应付金额
		String ORDER_YFMONEY = order.get("ORDER_YFMONEY").toString();
		ORDER_YFMONEY = Arith.div(Double.valueOf(ORDER_YFMONEY), 100, 2) + "";
		// 备注 order.get("WM_ORDER_REMARK").toString()
		return result.append(CENTER_BOLD).append(order.get("SHOP_NAME").toString()).append(JSL).append(LINE)
				.append(STARLINE).append(LINE).append(BOLD).append(BILLID)
				.append(dealBillId(order.get("ORDER_CODE").toString())).append(blank1).append(dinnerCount)
				.append(DINNER_UNIT).append(LINE).append(BOLD).append(BILL_CREATE_TIME)
				.append(getTimeString(StringDeal.strToDateLong(StringDeal.getStringDate()))).append(blank2)
				.append(TABLE_SIT).append(tableInfoRemark).append(LINE).append(BOLD).append(DISHES_NAME).append("     ")
				.append(DANJIA).append("   ").append(COUNTS).append("   ").append(XIAOJI).append(LINE).append(billData)
				.append(BOLD).append(middleBlank4).append(total).append("份").append(blankSpace).append(TOTAL)
				.append(totalAll).append(LINE).append(RIGHT_BOLD).append(YOUHUI).append(ORDER_YHMONEY).append(LINE)
				.append(RIGHT_BOLD).append(ORDER_PAY_WAY).append(ORDER_YFMONEY).append(LINE).append(SUBLINE)
				.append(LINE).append(LEFT_BOLD).append(LXDH).append(shop.get("SHOP_TEL").toString()).append(LINE)
				.append(LEFT_BOLD).append(shop.get("SHOP_ADDRESS").toString()).append(LINE).append(STARLINE).append(OVER).toString();
	}

}
