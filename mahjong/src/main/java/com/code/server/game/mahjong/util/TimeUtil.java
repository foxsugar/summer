package com.code.server.game.mahjong.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    
    /**
     * 获取1900年到今天的天数
     * @return
     */
    public static int getTodayIntValue() {
        return (int) TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().getTimeInMillis() + TimeUnit.HOURS.toMillis(8));
    }
    
    public static int getNextDayZeroHourLeftMinute() {
    	Calendar todayZero = Calendar.getInstance();
    	todayZero.set(Calendar.HOUR_OF_DAY, 0);
    	todayZero.set(Calendar.SECOND, 0);
    	todayZero.set(Calendar.MINUTE, 0);
    	todayZero.set(Calendar.MILLISECOND, 0);
        Calendar now = Calendar.getInstance();
        return (int) (TimeUnit.DAYS.toMinutes(1) - TimeUnit.MILLISECONDS.toMinutes(now.getTimeInMillis() - todayZero.getTimeInMillis()));
    }
    
    public static int getWeekOfYear() {
    	int weekOfYear = -1;
    	Calendar cal = Calendar.getInstance();
    	if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
    		weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
    	} else {
    		weekOfYear = cal.get(Calendar.WEEK_OF_YEAR) - 1;
    		if(weekOfYear == 0) {
    			weekOfYear = 52;
    		}
    	}
    	
    	return weekOfYear;
    }
    
    public static int getDateIntValue(Date date) {
        return (int) TimeUnit.MILLISECONDS.toDays(date.getTime() + TimeUnit.HOURS.toMillis(8));
    }
    
    public static int getDateIntValue(long time) {
        return (int) TimeUnit.MILLISECONDS.toDays(time + TimeUnit.HOURS.toMillis(8));
    }
    
    public static long getTodayZeroHourMill() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    public static long getDayBeforeZeroHourMill(int beforeDay) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() - TimeUnit.DAYS.toMillis(beforeDay);
    }
    
    public static int getCurrentDay() {
    	Calendar calendar = Calendar.getInstance();
    	return (int)calendar.get(Calendar.DAY_OF_YEAR);
    }
    
    public static long getTodayAppointHourMill(int hour) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    public static Date getTodayZeroHourDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static Date getLastDayZeroHourDate() {
        return new Date(getDayBeforeZeroHourMill(1));
    }
    
    
    public static int getHourMinutes() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    public static Date getMonthFirstDayZeroTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getLastMonthFirstDayZeroTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static Date getZeroTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * 返回今天是星期几
     */
    public static int getWeekOfDate() { 
    	Date dt = new Date();
        Calendar cal = Calendar.getInstance(); 
        cal.setTime(dt); 
        int weekOriginal = cal.get(Calendar.DAY_OF_WEEK) - 1; 
        if (weekOriginal < 0) 
            weekOriginal = 0; 
        
        return weekOriginal; 
    }
    
    public static int getWeekByDate(Date date) {
        Calendar cal = Calendar.getInstance(); 
        cal.setTime(date); 
        int weekOriginal = cal.get(Calendar.DAY_OF_WEEK) - 1; 
        if (weekOriginal < 0) 
            weekOriginal = 0; 
        
        return weekOriginal; 
    }
    
    public static long calSpecifiedHourMillisecond(final String hour) {
    	long millisecond = 0l;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
			Calendar instance = Calendar.getInstance();
			
			int year = instance.get(Calendar.YEAR);
			int month = instance.get(Calendar.MONTH) + 1;
			int day = instance.get(Calendar.DAY_OF_MONTH);
			
			StringBuffer sb = new StringBuffer();
			
			sb.append(year).append("-").append(month).append("-").append(day).append(" ").append(hour);
			
			Date date = sdf.parse(sb.toString());
			
			millisecond = date.getTime();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return millisecond;
    }
    
    public static int getHalfHourLeftSecond() {
    	Calendar calendar = Calendar.getInstance();
    	int minute = calendar.get(Calendar.MINUTE);
    	int second = calendar.get(Calendar.SECOND);
    	if(minute < 30) {
    		return (30 - minute - 1) * 60 + 60 - second;
    	} else {
    		return (60 - minute - 1) * 60 + 60 - second;
    	}
    }
    
    public static int getQuarterHourLeftSecond() {
    	Calendar calendar = Calendar.getInstance();
    	int minute = calendar.get(Calendar.MINUTE);
    	int second = calendar.get(Calendar.SECOND);
    	if(minute < 15) {
    		return (15 - minute - 1) * 60 + 60 - second;
    	} else if(minute >= 15 && minute < 30){
    		return (30 - minute - 1) * 60 + 60 - second;
    	} else if(minute >= 30 && minute < 45) {
    		return (45 - minute - 1) * 60 + 60 - second;
    	} else {
    		return (60 - minute - 1) * 60 + 60 - second;
    	}
    }
}
