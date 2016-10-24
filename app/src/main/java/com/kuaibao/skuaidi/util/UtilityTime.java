package com.kuaibao.skuaidi.util;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author 顾冬冬
 * @ClassName: UtilityTime
 * @Description: 时间工具类
 * @date 2015-9-30 下午5:02:34
 */
public class UtilityTime {

    /**
     * yyyy-MM-dd HH:mm:ss
     **/
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    /**
     * HH:mm
     **/
    public static final String HH_MM = "HH:mm";
    /**
     * yyy-MM-dd
     **/
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * @param timeStamp
     * @param type
     * @return String
     * @Title: getDateTimeByMillisecond
     * @Description:将时间戳字符串转换成long类型时间戳再格式化指定格式时间
     */
    public static String getDateTimeByMillisecond(String timeStamp, String type) {
        Date date = new Date(Long.valueOf(timeStamp));
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat(type);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        String time = format.format(date);
        return time;
    }

    /**
     * @param timeStamp
     * @param type
     * @return String
     * @Title: getDateTimeByMillisecond
     * @Description: 将long类型时间戳再格式化指定格式时间
     */
    public static String getDateTimeByMillisecond(long timeStamp, String type) {
        Date date = new Date(timeStamp * 1000);
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat(type);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        String time = format.format(date);
        return time;
    }

    public static String getDateTimeByMillisecond2(long timeStamp, String type) {
        Date date = new Date(timeStamp);
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat(type);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        String time = format.format(date);
        return time;
    }

    public static String getDateTimeByMillisecond3(Date date, String type) {
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat(type);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        String time = format.format(date);
        return time;
    }

    public static Date formatTimeByStr(String timeStr,String type){
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        Date date = null;
        try {
             date = sdf.parse(timeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date==null ? new Date():date;
    }

    /*
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        //Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
//	    if(milliSecond > 0) {  
//	        sb.append(milliSecond+"毫秒");  
//	    }  
        return sb.toString();
    }

    /**
     * 判断内存保存的日期是否是今天【作用范围：局部】
     **/
    public static boolean isToday(Context context, String receiveTheSavedCurDate) {
        String curDate = getDateTimeByMillisecond2(System.currentTimeMillis(), YYYY_MM_DD);
        String saveDate = receiveTheSavedCurDate;

        if (!Utility.isEmpty(saveDate)) {
            // 是今天
// 不是今天
            return curDate.equals(saveDate);
        } else {
            return false;// 不是今天
        }
    }

    /**将时间转换成时间戳**/
    public static long timeStringToTimeStamp(String time,String timeFormat){
        SimpleDateFormat format =  new SimpleDateFormat(timeFormat);
        Date date = null;
        try {
            date = format.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**当前时间戳转换为【今天 HH:mm|昨天 HH:mm|yyyy年MM月dd HH:mm】**/
    public static String timeFormat(long timeStamp){
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis());

        String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(timeStamp);

        if (now.substring(0, 10).equals(updateTime.substring(0, 10))) {
            return "今天 " +updateTime.substring(10, 16);
        } else if (now.substring(0, 8).equals(updateTime.substring(0, 8)) && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(updateTime.substring(8, 10)) == 1) {
            return "昨天 "+updateTime.substring(10, 16);
        } else {
            return updateTime.substring(0, 16);
        }
    }

    public static String timeFormat2(long timeStamp){
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis());
        String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(timeStamp);
        if (now.substring(0, 10).equals(updateTime.substring(0, 10))) {
            return updateTime.substring(10, 16);
        } else if (now.substring(0, 8).equals(updateTime.substring(0, 8)) && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(updateTime.substring(8, 10)) == 1) {
            return "昨天 "+updateTime.substring(10, 16);
        } else {
            return updateTime.substring(0, 16);
        }
    }

    /**
     * 获取指定年份-月份的天数
     * @param year
     * @param month
     * @return
     */
    public static int getDaysOfYearAndMonth(int year,int month){
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
        String[] months_little = { "4", "6", "9", "11" };

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month))) {
            return 31;
        } else if (list_little.contains(String.valueOf(month))) {
            return 30;
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                return 29;
            else
                return 28;
        }
    }

}
