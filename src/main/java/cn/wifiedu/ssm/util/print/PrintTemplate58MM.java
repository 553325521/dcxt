package cn.wifiedu.ssm.util.print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wifiedu.ssm.util.StringDeal;

/**
 * 58mm打印机
 * 生成各类打印样式模板
 * 每行32个英文字母，16个中文字母
 * Created by Matrix on 2016/10/21.
 */
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
		//List<Map<String, Object>> orderGoodsList = (List<Map<String, Object>>) order.get("orderGoodsList");
		List<Map<String, Object>> orderGoodsList = new ArrayList<Map<String, Object>>(){
			{
				add(new HashMap<>());
				add(new HashMap<>());
				add(new HashMap<>());
				add(new HashMap<>());
				add(new HashMap<>());
				add(new HashMap<>());
			}
		};
		String dishesSize = orderGoodsList.size() + "";
		// 总份数
		int total = 0;
		String blankSpace = "";
		for (int i = 0; i < 9 - dishesSize.length(); i++) {
			blankSpace += " ";
		}
		String blank = "";
		// 人数
		int dinnerCount = 5;
		// order.getTableInfo().getRemark() 桌台备注
		String tableInfoRemark = "桌台01";
		for (int i = 0; i < 19 - (dinnerCount + getStrLen(tableInfoRemark)); i++) {
			blank += " ";
		}
		String billData = "";
		// 菜品信息
		int n = 0;
		for (Map<String, Object> orderGoods : orderGoodsList) {
			// 计算空格
			int nameLen = getStrLen("菜品" + n);
			String middleBlank1 = "";
			for (int i = 0; i < 16 - nameLen; i++) {
				middleBlank1 += " ";
			}
			String countStr = (n + 1) + "";
			String middleBlank2 = "";
			for (int i = 0; i < (14 - getStrLen("大份")) - countStr.length(); i++) {
				middleBlank2 += " ";
			}
			middleBlank2 = middleBlank2.substring(0, middleBlank2.length() - 1);
			billData += BOLD + ("菜品" + n) + middleBlank1 + "大份" + middleBlank2 + (n + 1)
					+ "份" + LINE;
			total += (n + 1);
		}
		return result.append(CENTER_BOLD).append("地锅香大酒店").append(BWL).append(LINE).append(STARLINE)
				.append(LINE).append(BOLD).append(BILLID).append(dealBillId("2018111200001001")).append(LINE).append(BOLD)
				.append(BILL_CREATE_TIME).append(getTimeString(StringDeal.strToDateLong(StringDeal.getStringDate()))).append(LINE).append(BOLD)
				.append(DINNER_COUNT).append(dinnerCount).append(DINNER_UNIT).append(blank).append(TABLE_SIT)
				.append(tableInfoRemark).append(LINE).append(SUBLINE).append(LINE).append(BOLD)
				.append(DISHES_NAME).append("        ").append(NOTES).append("       ").append(COUNTS).append(LINE)
				.append(billData).append(SUBLINE).append(LINE).append(BOLD).append(CENTER_BLANK).append(TOTAL)
				.append(blankSpace).append(total).append("份").append(LINE).append(RIGHT_BOLD).append("一定要好吃！")
				.toString();
	}

	@Override
	public String getActivityForecastTemplate() {
		return null;
	}

}
