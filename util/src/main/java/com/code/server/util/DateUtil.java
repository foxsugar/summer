package com.code.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sunxianping on 2017/9/1.
 */
public final class DateUtil {

    public static final String DATE_TIME_FORMAT_YYYYMMDD_HH_MI = "yyyyMMdd HH:mm";

    public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI = "yyyy-MM-dd HH:mm";
    //年月日
    public static final String DATE_TIME_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static boolean isSameDate(long time1, long time2) {

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTimeInMillis(time1);
        calendar2.setTimeInMillis(time2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);

    }

    //获取当天的开始时间
    public static java.util.Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    //获取当天的结束时间
    public static java.util.Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    //获取今年开始时间
    public static Date getThisYearStart(){
        LocalDate now = LocalDate.now();
        LocalDate localDate = LocalDate.of(now.getYear(), 1, 1);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    // todo
    public static Date convert2Date(String string){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_YYYYMMDD_HH_MI);
        Date date = null;
        try {
            date = simpleDateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date convertDay2Date(String string){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_YYYY_MM_DD);
        Date date = null;
        try {
            date = simpleDateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String convert2DayString(Date date){
        return convert2String(date, DATE_TIME_FORMAT_YYYY_MM_DD);
    }

//    public static String convert2String(Date date){
//        return convert2String(date, DATE_TIME_FORMAT_YYYYMMDD_HH_MI);
//    }

    public static String convert2String(Date date){
        return convert2String(date, DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
    }

    public static String convert2String(Date date, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            return formater.format(date);
        } catch (Exception e) {
            return null;
        }
    }
}
