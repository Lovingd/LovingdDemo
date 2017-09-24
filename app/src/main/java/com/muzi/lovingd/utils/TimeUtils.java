package com.muzi.lovingd.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author: 17040880
 * @time: 2017/7/5 16:37
 */
public class TimeUtils {


    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime() / 1000;
    }

    /**
     * 起始日期和结束日期比较大小
     *
     * @param starTime
     * @param endTime
     * @return
     */
    public static boolean comparisonOfSize(String starTime, String endTime) {
        boolean isSize = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            if (diff >= 0) {
                isSize = true;
            } else {
                isSize = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isSize;
    }

    /**
     * 计算时间差
     *
     * @param starTime 开始时间
     * @param endTime  结束时间
     * @return 返回时间差
     */
    public static boolean isTimeFor7(String starTime, String endTime) {
        boolean is7 = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            long time7 = 7 * 24 * 60 * 60 * 1000;
            if (diff <= time7) {
                is7 = true;
            } else {
                is7 = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return is7;

    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如昨天等
     *
     * @param timeStamp
     * @return
     */

    public static String convertTimeToFormat(long timeStamp) {

        long curTime = getNetCurrentTime() / (long) 1000;
        long time = curTime - timeStamp;
        if (time < 3600 * 24) {
            return new SimpleDateFormat("HH:mm").format(timeStamp * 1000);
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return "昨天";
        } else {
            return new SimpleDateFormat("yyyy-MM-dd").format(timeStamp * 1000);
        }
    }

    /**
     * 获取HH:mm 这个时间格式的数据
     *
     * @return
     */
    public static String getYyyy(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(time);
            return new SimpleDateFormat("yyyy").format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取HH:mm 这个时间格式的数据
     *
     * @return
     */
    public static String getMM(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(time);
            return new SimpleDateFormat("MM").format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getYMDHM(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = sdf.parse(time);
            return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取HH:mm 这个时间格式的数据
     *
     * @return
     */
    public static String getHhMmTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        try {
            date = sdf.parse(time);
            return new SimpleDateFormat("HH:mm").format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取HH:mm 这个时间格式的数据
     *
     * @return
     */
    public static String getDD(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(time);
            return new SimpleDateFormat("dd").format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date getConverDate() {
        long time = getNetCurrentTime();
        Date date = new Date(time);
        return date;
    }

    public static Calendar getConverCalendar() {
        long time = getNetCurrentTime();
        Date date = new Date(time);
        Calendar pre = Calendar.getInstance();
        pre.setTime(date);
        return pre;
    }

    public static String showConvertTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (IsToday(time)) {
            return new SimpleDateFormat("HH:mm").format(date.getTime());
        } else if (IsYesterday(time)) {
            return "昨天";
        } else {
            return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
        }
    }

    /**
     * 获取服务器当前时间戳
     * （获取不到，返回本地时间）
     *
     * @return
     */
    public static long getNetCurrentTime() {
        long time = System.currentTimeMillis();
        return time;
    }


    public static int thisMonthOfDay(String day) {
        int oneDay = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar pre = Calendar.getInstance();
            Date date = new Date();
            date = sdf.parse(day);
            pre.setTime(date);
            oneDay = pre.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            oneDay = 0;
            e.printStackTrace();
        }
        return oneDay;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(String day) {
        boolean isToday = false;
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(getNetCurrentTime());
            pre.setTime(predate);
            Calendar cal = Calendar.getInstance();
            Date date = getDateFormat().parse(day);
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                        - pre.get(Calendar.DAY_OF_YEAR);
                if (diffDay == 0) {
                    isToday = true;
                }
            }
        } catch (Exception e) {
            isToday = false;
            e.printStackTrace();
        }

        return isToday;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsYesterday(String day) {
        boolean isYesterday = false;
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(getNetCurrentTime());
            pre.setTime(predate);

            Calendar cal = Calendar.getInstance();
            Date date = getDateFormat().parse(day);
            cal.setTime(date);

            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                        - pre.get(Calendar.DAY_OF_YEAR);
                if (diffDay == -1) {
                    isYesterday = true;
                }
            }
        } catch (Exception e) {
            isYesterday = false;
            e.printStackTrace();
        }
        return isYesterday;
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }



}
