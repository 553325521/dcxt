package cn.wifiedu.ssm.util;

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
import org.apache.log4j.chainsaw.Main;


/**
 * 
 * @author lps
 * @Description:日期计算
 * @version V1.0
 *
 */
public class DateUtil{
    public static final String TIMESTAMP_FOMART="yyyy-MM-dd HH:mm:ss.SSS";
	public static final String TIMESTAMP_FOMART_OTHER="yyyyMMddHHmmssSSS";
	public static final String DATE_TIME_FMT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_TIME_FMT_OTHER="yyyyMMddHHmmss";
	public static final String DATE_FMT = "yyyy-MM-dd";
	public static final String TIME_FMT = "HH:mm:ss";
	
	GregorianCalendar gc = new GregorianCalendar(); 
	
    /**
     * @author lps
     * @Description: 	日期字符串转换成日期格式
     * @param DateStr 	要解析的字符串
     * @param format 	解析的格式 如: yyyy-MM-dd HH:mm:ss
     * 
     */
    public static Date parseDate(String DateStr, String format) throws ParseException{
    	if(StringUtils.isNotBlank(format)){
    		return new SimpleDateFormat(format).parse(DateStr);
    	}
    	return null;
    }
    
    /**
     * @author lps
     * @Description:	 默认格式 yyyy-MM-dd HH:mm:ss 解析日期字符串
     * @param DateStr	要解析的字符串
     * 
     */
    public static Date parseDate(String DateStr) throws ParseException{
		return parseDate(DateStr,DATE_TIME_FMT);
    }
    
    /**
     * 
     * @author lps
     * @Description: 	日期计算
     * @param preDate 	初始日期
     * @param time		计算的数量大小 
     * @param type		计算的类型	如：Calendar.YEAR、Calendar.MONTH、Calendar.DATE
     * @return Date 
     *
     */
    
    public static Date calculateDate(Date preDate, Integer time, Integer type){
        GregorianCalendar gc=new GregorianCalendar(); 
        gc.setTime(preDate); 
        gc.add(type,time); 
    	return gc.getTime();
    }
    
    /**
     * @date 2018年8月12日 下午7:12:40 
     * @author lps
     * @Description: 		日期计算
     * @param preDateStr	初始日期 
     * @param time			计算的数量大小
     * @param type			计算的类型	如：Calendar.YEAR、Calendar.MONTH、Calendar.DATE
     * @param format		解析的格式  如: yyyy-MM-dd HH:mm:ss 
     * @return Date 
     *
     */
    public static Date calculateDate(String preDateStr, Integer time, Integer type, String format) throws ParseException{
    	return calculateDate(new SimpleDateFormat(format).parse(preDateStr), time, type);
    }
    
    /**
     * 
     * @date 2018年8月12日 下午7:14:58 
     * @author lps
     * @Description: 		日期计算	默认格式 yyyy-MM-dd HH:mm:ss 解析日期字符串
     * @param preDateStr	初始日期 
     * @param time			计算的数量大小
     * @param type			计算的类型	如：Calendar.YEAR、Calendar.MONTH、Calendar.DATE
     * @return Date 
     *
     */
    public static Date calculateDate(String preDateStr, Integer time, Integer type) throws ParseException{
    	return calculateDate(new SimpleDateFormat(DATE_TIME_FMT).parse(preDateStr), time, type);
    }
    
    /**
     * 
     * @author lps
     * @Description: 计算两个日期相隔的时间
     * @param preDate
     * @param afterDate
     * @return 		返回Period类型，可以获得年月日
     * @return String 
     *
     */
    public static Period  getintervalTime(Date preDate, Date afterDate){
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
     * @return 		返回Period类型，可以获得年月日
     * @return String 
     * @throws ParseException 
     *
     */
    public static Period  getintervalTime(String preDateStr, String afterDateStr,String format) throws ParseException{
    	  return getintervalTime(new SimpleDateFormat(format).parse(preDateStr), new SimpleDateFormat(format).parse(afterDateStr));
    }
    
    
    /**
     * 
     * @author lps
     * @Description: 计算两个日期相隔的时间
     * @param preDate
     * @param afterDate
     * @return 		返回Period类型，可以获得年月日
     * @return String 
     * @throws ParseException 
     *
     */
    public static Period  getintervalTime(String preDateStr, String afterDateStr) throws ParseException{
    	  return getintervalTime(new SimpleDateFormat(DATE_TIME_FMT).parse(preDateStr), new SimpleDateFormat(DATE_TIME_FMT).parse(afterDateStr));
    }
    
    /**
     * 
     * @author lps
     * @Description: 计算两个日期相隔的时间
     * @param preDate
     * @param afterDate
     * @return 		返回Period类型，可以获得年月日
     * @return String 
     * @throws ParseException 
     *
     */
    public static Period  getintervalTime(Date preDate, String afterDateStr) throws ParseException{
    	  return getintervalTime(preDate, new SimpleDateFormat(DATE_TIME_FMT).parse(afterDateStr));
    }
    /** 
     * yyyy-MM-dd日期格式字符串转换成时间戳 
     * @param date 字符串日期 
     * @param format
     * @return 
     */  
    public static String date2TimeStamp(String date_str){  
        try {  
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT);  
            return String.valueOf(sdf.parse(date_str).getTime()/1000);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "";  
    }
    
    /**
    * <p>Title: addDayTimeStamp</p>
    * <p>Description:当前时间增加天数返回时间戳 </p>
    * @param num
    * @return
    */
    public static long addDayTimeStamp(int num){
		Date date = new Date();
		Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        c.add(Calendar.DAY_OF_MONTH, num); 
        date = c.getTime();
        return date.getTime()/1000;
	}
    
    /**
    * <p>Title: returnTodayStartAndEnd</p>
    * <p>Description: 计算当前日期的0:00和23:59:59</p>
    * @return
    */
    public static String[] returnTodayStartAndEnd(){
    	String [] dateArray = new String[2];
    	 SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FMT);
    	 String today = sdf.format(new Date());
    	 String start = today.substring(0, 11)+"00:00:00";
    	 String end = today.substring(0, 11)+"23:59:59";
    	 dateArray[0] = start;
    	 dateArray[1] = end;
    	 return dateArray;
    }
    public static void main(String[] args) {
		DateUtil.returnTodayStartAndEnd();
	}
   /* public static void main(String[] args) {
		String a = "12321312313";
		String [] array = a.split(",");
		System.out.println(array.length);
	}*/
}
