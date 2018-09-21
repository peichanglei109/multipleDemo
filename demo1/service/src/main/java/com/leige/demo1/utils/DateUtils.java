package com.leige.demo1.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {

    public static final String SPIT_TAG=",";

    public static Date getDate(String time){
        Date date = new Date(Long.parseLong(time));
        return date;
    }

    public static String getTimeString(Long expiryData){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTimeMillis = System.currentTimeMillis();
        long lastTime=currentTimeMillis-expiryData*3600*24*1000;
        String formatDateTime = sdf.format(new Date(lastTime));
        return formatDateTime;

    }

    public static String getTimeStringByLongTime(Long time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = sdf.format(time);
        return formatDateTime;
    }

    public static String getTimeStringToDay(Long expiryData){
        SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-dd");
        String formatDateTime = sdf.format(expiryData);
        return formatDateTime;

    }

}
