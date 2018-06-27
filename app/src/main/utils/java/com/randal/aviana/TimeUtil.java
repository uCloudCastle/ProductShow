package com.randal.aviana;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by yanbinbin on 16/7/27.
 */
public final class TimeUtil {

    private TimeUtil(){
        throw new UnsupportedOperationException("DO NOT INSTANTIATE THIS CLASS");
    }

    private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_TIME_FORMAT_DAY = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT_WITHOUT_DAY = "HH:mm:ss";

    // 获取 timestamp 日期的零点
    public static Calendar getMidnightByTimestamp(long timestamp) {
        String day = timestamp2string(timestamp, DEFAULT_TIME_FORMAT_DAY);
        return TimeUtil.string2calendar(day + " 00:00:00", DEFAULT_TIME_FORMAT);
    }

    // 获取 Calendar 日期的零点
    public static Calendar getMidnightByCalendar(Calendar  calendar) {
        String day = calendar2string(calendar, DEFAULT_TIME_FORMAT_DAY);
        return TimeUtil.string2calendar(day + " 00:00:00", DEFAULT_TIME_FORMAT);
    }

    // 获取 Calendar 日期的最后一秒
    public static Calendar getLastSecondByCalendar(Calendar calendar) {
        String day = calendar2string(calendar, DEFAULT_TIME_FORMAT_DAY);
        return TimeUtil.string2calendar(day + " 23:59:59", DEFAULT_TIME_FORMAT);
    }

    // 获取今日的零点
    public static Calendar getMidnightToday() {
        String day = timestamp2string(currentTimestamp(), DEFAULT_TIME_FORMAT_DAY);
        return TimeUtil.string2calendar(day + " 00:00:00", DEFAULT_TIME_FORMAT);
    }

    // 判断当前时间是否在这两天之间
    public static int ifNowBetweenTheDays(String day1, String day2, String format) {
        Calendar now = Calendar.getInstance();

        Calendar startCal = TimeUtil.string2calendar(day1 + " 00:00:00", format + DEFAULT_TIME_FORMAT_WITHOUT_DAY);
        Calendar endCal = TimeUtil.string2calendar(day2 + " 23:59:59", DEFAULT_TIME_FORMAT);

        if (now.before(startCal)) {
            return -1;
        } else if (now.after(endCal)) {
            return 1;
        }
        return 0;
    }

    public static long currentTimestamp(){
        return System.currentTimeMillis();
    }

    public static String currentDate(String format){
        return timestamp2string(System.currentTimeMillis(), format);
    }

    /**
     * Parse the Timestamp for day
     */
    public static long getTimestampForDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTimeInMillis();
    }

    /**
     * Parse the format string to calendar
     */
    public static Calendar string2calendar(String str, String format) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            LogUtils.w("Parse Exception!" + str);
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String calendar2string(Calendar cal) {
        return calendar2string(cal, DEFAULT_TIME_FORMAT);
    }

    /**
     * Parse the calendar to format string
     */
    public static String calendar2string(Calendar cal, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    /**
     * Parse the format string to the timestamp
     */
    public static long string2timestamp(String str, String format) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            LogUtils.w("Parse Exception!" + str);
            e.printStackTrace();
            return 0;
        }
        return date.getTime();
    }

    /**
     * Parse the calendar to the timestamp
     */
    public static long calendar2timestamp(Calendar cal) {
        return cal.getTimeInMillis();
    }

    /**
     * Parse the timestamp to the format date
     */
    public static String timestamp2string(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(timestamp);
    }

    /**
     * Parse the timestamp to calendar
     */
    public static Calendar timestamp2calendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }

    /**
     * Parse the duration to timestamp
     */
    public static long timeLengthToTimestamp(String timeLength){
        String[] times = timeLength.split("分钟");
        String time = times[0];
        long realTime = Long.valueOf(time) * 60 * 1000;
        return realTime;
    }
}
