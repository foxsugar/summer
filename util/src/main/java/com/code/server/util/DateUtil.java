package com.code.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    //获得前一天的日期
    public static String getPreviousDay(String currentDay){
        Date date = convertDay2Date(currentDay);
        LocalDate localDate = previousDay(date, -1);
//        String ret = localDate.toString().replace("LocalDate = ", "");
//        return ret;
        return localDate.toString();
    }

    public static LocalDate previousDay(Date date,int offset){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        LocalDate ll = localDate.plusDays(offset);
//        System.out.println("LocalDate = " + ll);
        return ll;
    }

    public static String timeStampToTimeString(long timeStamp){
        //long timeStamp = 1495777335060;//直接是时间戳
//      /获取当前时间戳,也可以是你自已给的一个随机的或是别人给你的时间戳(一定是long型的数据)
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(timeStamp));   // 时间戳转换成时间
        System.out.println(sd);//打印出你要的时间
        return sd;
    }

    //把 2000-1-1这种不标准的字符串转化为 2000-01-01这种标准的字符串
    public static String becomeStandardSTime(String time){

        if (time.length() == 10){
            return time;
        }
        String[] strings = time.split("-");
        List<String> list = new ArrayList<>();
        for (String s : strings){
            if (s.length() == 1){
                String st = "0" + s;
                list.add(st);
            }else {
                list.add(s);
            }
        }
        return list.get(0) + "-" + list.get(1) + "-" + list.get(2);
    }

    public static List<String> getDateListIn(String current, String end){

        List<String> list = new ArrayList<>();

        if (current.equals(end)){
            list.add(current);
            return list;
        }

        list.add(current);
        for (int i = 1; i < 90; i++){
            current = DateUtil.getPreviousDay(current);
            if (current.equals(end)){
                list.add(end);
                break;
            }
            list.add(current);
        }
        return list;
    }

    public static LocalDate parse(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
