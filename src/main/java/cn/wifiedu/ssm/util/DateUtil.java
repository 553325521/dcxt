package cn.wifiedu.ssm.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author lps
 * @Description:日期计算
 * @version V1.0
 *
 */
public class DateUtil {
	public static final String TIMESTAMP_FOMART = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String TIMESTAMP_FOMART_OTHER = "yyyyMMddHHmmssSSS";
	public static final String DATE_TIME_FMT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_TIME_FMT_OTHER = "yyyyMMddHHmmss";
	public static final String DATE_FMT = "yyyy-MM-dd";
	public static final String TIME_FMT = "HH:mm:ss";

	GregorianCalendar gc = new GregorianCalendar();

	/**
	 * @author lps
	 * @Description: 日期字符串转换成日期格式
	 * @param DateStr
	 *            要解析的字符串
	 * @param format
	 *            解析的格式 如: yyyy-MM-dd HH:mm:ss
	 * 
	 */
	public static Date parseDate(String DateStr, String format) throws ParseException {
		if (StringUtils.isNotBlank(format)) {
			return new SimpleDateFormat(format).parse(DateStr);
		}
		return null;
	}

	/**
	 * @author lps
	 * @Description: 默认格式 yyyy-MM-dd HH:mm:ss 解析日期字符串
	 * @param DateStr
	 *            要解析的字符串
	 * 
	 */
	public static Date parseDate(String DateStr) throws ParseException {
		return parseDate(DateStr, DATE_TIME_FMT);
	}

	/**
	 * 
	 * @author lps
	 * @Description: 日期计算
	 * @param preDate
	 *            初始日期
	 * @param time
	 *            计算的数量大小
	 * @param type
	 *            计算的类型 如：Calendar.YEAR、Calendar.MONTH、Calendar.DATE
	 * @return Date
	 *
	 */

	public static Date calculateDate(Date preDate, Integer time, Integer type) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(preDate);
		gc.add(type, time);
		return gc.getTime();
	}

	/**
	 * @date 2018年8月12日 下午7:12:40
	 * @author lps
	 * @Description: 日期计算
	 * @param preDateStr
	 *            初始日期
	 * @param time
	 *            计算的数量大小
	 * @param type
	 *            计算的类型 如：Calendar.YEAR、Calendar.MONTH、Calendar.DATE
	 * @param format
	 *            解析的格式 如: yyyy-MM-dd HH:mm:ss
	 * @return Date
	 *
	 */
	public static Date calculateDate(String preDateStr, Integer time, Integer type, String format)
			throws ParseException {
		return calculateDate(new SimpleDateFormat(format).parse(preDateStr), time, type);
	}

	/**
	 * 
	 * @date 2018年8月12日 下午7:14:58
	 * @author lps
	 * @Description: 日期计算 默认格式 yyyy-MM-dd HH:mm:ss 解析日期字符串
	 * @param preDateStr
	 *            初始日期
	 * @param time
	 *            计算的数量大小
	 * @param type
	 *            计算的类型 如：Calendar.YEAR、Calendar.MONTH、Calendar.DATE
	 * @return Date
	 *
	 */
	public static Date calculateDate(String preDateStr, Integer time, Integer type) throws ParseException {
		return calculateDate(new SimpleDateFormat(DATE_TIME_FMT).parse(preDateStr), time, type);
	}

	/**
	 * 
	 * @author lps
	 * @Description: 计算两个日期相隔的时间
	 * @param preDate
	 * @param afterDate
	 * @return 返回Period类型，可以获得年月日
	 * @return String
	 *
	 */
	public static Period getintervalTime(Date preDate, Date afterDate) {
		Instant instant = preDate.toInstant();
		Instant instant2 = afterDate.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();

		// atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
		LocalDate preLocalDate = instant.atZone(zoneId).toLocalDate();
		LocalDate afterLocalDate = instant2.atZone(zoneId).toLocalDate();

		Period pe = Period.between(preLocalDate, afterLocalDate);

		return pe;
	}

	/**
	 * 
	 * @author lps
	 * @Description: 计算两个日期相隔的时间
	 * @param preDate
	 * @param afterDate
	 * @return 返回Period类型，可以获得年月日
	 * @return String
	 * @throws ParseException
	 *
	 */
	public static Period getintervalTime(String preDateStr, String afterDateStr, String format) throws ParseException {
		return getintervalTime(new SimpleDateFormat(format).parse(preDateStr),
				new SimpleDateFormat(format).parse(afterDateStr));
	}

	/**
	 * 
	 * @author lps
	 * @Description: 计算两个日期相隔的时间
	 * @param preDate
	 * @param afterDate
	 * @return 返回Period类型，可以获得年月日
	 * @return String
	 * @throws ParseException
	 *
	 */
	public static Period getintervalTime(String preDateStr, String afterDateStr) throws ParseException {
		return getintervalTime(new SimpleDateFormat(DATE_TIME_FMT).parse(preDateStr),
				new SimpleDateFormat(DATE_TIME_FMT).parse(afterDateStr));
	}

	/**
	 * 
	 * @author lps
	 * @Description: 计算两个日期相隔的时间
	 * @param preDate
	 * @param afterDate
	 * @return 返回Period类型，可以获得年月日
	 * @return String
	 * @throws ParseException
	 *
	 */
	public static Period getintervalTime(Date preDate, String afterDateStr) throws ParseException {
		return getintervalTime(preDate, new SimpleDateFormat(DATE_TIME_FMT).parse(afterDateStr));
	}

	/**
	 * yyyy-MM-dd日期格式字符串转换成时间戳
	 * 
	 * @param date
	 *            字符串日期
	 * @param format
	 * @return
	 */
	public static String date2TimeStamp(String date_str) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT);
			return String.valueOf(sdf.parse(date_str).getTime() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * <p>
	 * Title: addDayTimeStamp
	 * </p>
	 * <p>
	 * Description:当前时间增加天数返回时间戳
	 * </p>
	 * 
	 * @param num
	 * @return
	 */
	public static long addDayTimeStamp(int num) {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, num);
		date = c.getTime();
		return date.getTime() / 1000;
	}

	/**
	 * <p>
	 * Title: returnTodayStartAndEnd
	 * </p>
	 * <p>
	 * Description: 计算当前日期的0:00和23:59:59
	 * </p>
	 * 
	 * @return
	 */
	public static String[] returnTodayStartAndEnd() {
		String[] dateArray = new String[2];
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FMT);
		String today = sdf.format(new Date());
		String start = today.substring(0, 11) + "00:00:00";
		String end = today.substring(0, 11) + "23:59:59";
		dateArray[0] = start;
		dateArray[1] = end;
		return dateArray;
	}

	/**
	 * <p>
	 * Title: selectTime
	 * </p>
	 * <p>
	 * Description:计算 今天、昨天、本周、本月的时间段
	 * </p>
	 * 
	 * @param dateStr
	 * @return
	 */
	public static String[] selectTime(String dateStr) {
		String[] dateArray = new String[2];
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FMT);
		if (dateStr.equals("今天")) {
			String today = sdf.format(new Date());
			String start = today.substring(0, 11) + "00:00:00";
			String end = today.substring(0, 11) + "23:59:59";
			dateArray[0] = start;
			dateArray[1] = end;
		} else if (dateStr.equals("昨天")) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			Date time = cal.getTime();
			String today = sdf.format(time);
			String start = today.substring(0, 11) + "00:00:00";
			String end = today.substring(0, 11) + "23:59:59";
			dateArray[0] = start;
			dateArray[1] = end;
		} else if (dateStr.equals("本周")) {
			Date currentWeekStart = getBeginDayOfWeek();
			Date currentWeekEnd = getEndDayOfWeek();
			String currentWeekStartStr = sdf.format(currentWeekStart);
			String currentWeekEndStr = sdf.format(currentWeekEnd);
			dateArray[0] = currentWeekStartStr;
			dateArray[1] = currentWeekEndStr;
		} else {
			Date currentMonthStart = getBeginDayOfMonth();
			Date currentMonthEnd = getEndDayOfMonth();
			String currentWeekStartStr = sdf.format(currentMonthStart);
			String currentWeekEndStr = sdf.format(currentMonthEnd);
			dateArray[0] = currentWeekStartStr;
			dateArray[1] = currentWeekEndStr;
		}
		return dateArray;
	}

	// 获取本周的开始时间
	public static Date getBeginDayOfWeek() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayofweek == 1) {
			dayofweek += 7;
		}
		cal.add(Calendar.DATE, 2 - dayofweek);
		return getDayStartTime(cal.getTime());
	}

	// 获取本周的结束时间
	public static Date getEndDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getBeginDayOfWeek());
		cal.add(Calendar.DAY_OF_WEEK, 6);
		Date weekEndSta = cal.getTime();
		return getDayEndTime(weekEndSta);
	}

	// 获取某个日期的开始时间
	public static Timestamp getDayStartTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d)
			calendar.setTime(d);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTimeInMillis());
	}

	// 获取某个日期的结束时间
	public static Timestamp getDayEndTime(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d)
			calendar.setTime(d);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return new Timestamp(calendar.getTimeInMillis());
	}

	// 获取今年是哪一年
	public static Integer getNowYear() {
		Date date = new Date();
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return Integer.valueOf(gc.get(1));
	}

	// 获取本月是哪一月
	public static int getNowMonth() {
		Date date = new Date();
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return gc.get(2) + 1;
	}

	// 获取本月的开始时间
	public static Date getBeginDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		return getDayStartTime(calendar.getTime());
	}

	// 获取本月的结束时间
	public static Date getEndDayOfMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getNowYear(), getNowMonth() - 1, 1);
		int day = calendar.getActualMaximum(5);
		calendar.set(getNowYear(), getNowMonth() - 1, day);
		return getDayEndTime(calendar.getTime());
	}

	public static void main(String[] args) {
		System.out.println(selectTime("本月")[1]);
	}
	/*
	 * public static void main(String[] args) { String a = "12321312313"; String
	 * [] array = a.split(","); System.out.println(array.length); }
	 */
}
