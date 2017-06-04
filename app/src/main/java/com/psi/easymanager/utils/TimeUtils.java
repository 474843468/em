package com.psi.easymanager.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * User: ylw
 * Date: 2017-02-08
 * Time: 17:47
 * FIXME
 */
public class TimeUtils {
  public static String getWeekOfDate(Date dt) {
    String[] weekDays = { "7", "1", "2", "3", "4", "5", "6" };
    Calendar cal = Calendar.getInstance();
    cal.setTime(dt);
    int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (w < 0) w = 0;
    return weekDays[w];
  }
}  