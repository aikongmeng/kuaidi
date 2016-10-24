package com.kuaibao.skuaidi.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    private Context context;

    public static final String YYYY_MM_DD_CH = "yyyy年MM月dd日";// 不可以直接定义"MM-dd"这样的类型，否则时间不准
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_CH = "yyyy年MM月dd日 HH:mm:ss";

    public static final int TIME_TODAY = 0;
    public static final int TIME_TOMORROW = 1;
    public static final int TIME_THEDAY_AFTER_TOMORROW = 2;

    /**
     * 工具类不必实例化，另外，名字建议改下。
     */
    private DateHelper() {
    }

    /**
     * 获取当前日期yyyy-MM-dd
     * style: 时间格式
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate(String style) {
        SimpleDateFormat sdf = new SimpleDateFormat(style);
        return sdf.format(new Date());
    }

    /**
     * 获取以今天为起点的往前或往后几天的日期
     *
     * @param mark 为整数，可负可正，例为1：代表明天;2:代表后天；-1代表昨天;-2代表前天
     */
    @SuppressLint("SimpleDateFormat")
    public static String getAppointDate(int mark, String TimeType) {
        String dateStr = "";
        Date date = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TimeType);
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(sdf.format(date)));
            cal.add(Calendar.DAY_OF_YEAR, mark);
            dateStr = sdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateStr;
    }

    /**
     * 获取以今天为起点的往前或往后几天的时间戳(yyyy-MM-dd HH:mm)
     *
     * @param mark 为整数，可负可正，例为1：代表明天;2:代表后天；-1代表昨天;-2代表前天
     * @param hms  代表小时和分钟 格式HH:mm 不能为空
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Long getTimeStamp(int mark, String hms, String timeType) {
        long timeStamp = 0;
        String time;
        try {
            SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            if (hms == null || hms.equals("")) {
                time = getAppointDate(mark, timeType);
            } else {
                time = getAppointDate(mark, timeType) + " " + hms;
            }
            Date date = format.parse(time);
            timeStamp = date.getTime() / 1000L;
        } catch (Exception e) {
        }
        return timeStamp;
    }

    /**
     * 获取指定时间格式指定时间的时间戳
     *
     * @param time
     * @param timeType
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Long getTimeStamp(String time, String timeType) {
        long timeStamp = 0;
        SimpleDateFormat format = new SimpleDateFormat(timeType);
        try {
            Date date = format.parse(time);
            timeStamp = date.getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    private static String today = null;// 今天
    private static String tomorrow = "";// 明天
    private static String theDayAfterTomorrow = "";// 后天

    /**
     * @param timeString 传入的参数格式（例：2015-12-04 12:12:12）
     * @return String
     * @Title: getSpecifiTime
     * @Description:获取具体时间-（例：明天 12：12）
     * @return（例：明天 12：12）
     * @author: 顾冬冬
     */
    public static String getSpecifiTime(String timeString) {
        /**通过获取今天，明天，后天的时间 与当前时间戳比较，相同则为相同的那一天，否则直接显示时间格式时间**/
        // 获取今天的时间格式
        today = getAppointDate(0, "yyyy-MM-dd");
        // 获取明天的时间格式
        tomorrow = getAppointDate(1, "yyyy-MM-dd");
        // 获取后天的时间格式
        theDayAfterTomorrow = getAppointDate(2, "yyyy-MM-dd");
        // 将传递进来的时间截取到年月日
        String ymd = timeString.substring(0, 10);
        // 将传递进来的时间从小时截取到分钟
        String hm = timeString.substring(11, 16);
        if (ymd.equals(today)) {
            return "今天 " + hm;
        } else if (ymd.equals(tomorrow)) {
            return "明天 " + hm;
        } else if (ymd.equals(theDayAfterTomorrow)) {
            return "后天 " + hm;
        } else {
            return timeString.substring(0, 16);
        }
    }

    /**
     * @param date DateHelper.TIME_TODAY....
     * @return String
     * @Title: getDate
     * @Description:获取今天，明天，后天的时间-格式为MM月dd日
     * @author: 顾冬冬
     */
    public static String getDate(int date, String dateFormate) {
        /**通过获取今天，明天，后天的时间 与当前时间戳比较，相同则为相同的那一天，否则直接显示时间格式时间**/
        // 获取今天的时间格式
        today = getAppointDate(TIME_TODAY, dateFormate);
        tomorrow = getAppointDate(TIME_TOMORROW, dateFormate);
        theDayAfterTomorrow = getAppointDate(TIME_THEDAY_AFTER_TOMORROW, dateFormate);

        if (date == TIME_TODAY) {
            return today;
        } else if (date == TIME_TOMORROW) {
            return tomorrow;
        } else if (date == TIME_THEDAY_AFTER_TOMORROW) {
            return theDayAfterTomorrow;
        } else {
            return "";
        }
    }

    // 将时间戳转成字符型时间

    /**
     * @param timeStamp--传入格式为时间戳
     * @return String
     * @Title: getTimeStamp
     * @Description:将时间戳转换成字符型的时间
     * @return--返回格式为(例：yyyy年MM月dd日 HH:mm:ss)
     * @author: 顾冬冬
     */
    public static String getTimeStamp(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_CH);
        return sdf.format(timeStamp * 1000);
    }

    public static String getTimeStampReal(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
        return sdf.format(timeStamp);
    }

    /**
     * 判断两日期是否是同一天
     */
    public static boolean isSameDay(String date1, String date2) {

        if (TextUtils.isEmpty(date1) || TextUtils.isEmpty(date2)) {
            return false;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(date1);
            d2 = format.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

}
