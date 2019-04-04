package com.jiayue.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;

/**
 * 自定义格式化视频时长类
 * 
 * @author lmj
 * 
 */
public class TimeFormate {
	private int milliseconds;
	public TimeFormate(int milliseconds) {
		this.milliseconds = milliseconds;
	}
	public String formatetime() {
		String hour = String.valueOf(milliseconds / 3600000);
		String minute = String.valueOf((milliseconds % 3600000) / 60000);
		String second = String
				.valueOf(((milliseconds % 3600000) % 60000) / 1000);
		hour = deal(hour);
		minute = deal(minute);
		second = deal(second);
		return hour + ":" + minute + ":" + second;
	}
	private String deal(String time) {
		if (time.length() == 1) {
			if (time.equals("0"))
				time = "00";
			else
				time = "0" + time;
		}
		return time;
	}
	
	 /**
     * 判断当前日期是星期几
     *
     * @param pTime 设置的需要判断的时间 //格式如2012-09-08
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */

    // String pTime = "2012-03-12";
    @SuppressLint("SimpleDateFormat")
    public static String getWeek(String pTime) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "六";
        }

        return "每周" + Week;
    }

    public static String getDay(int day) {
        String Week = "每周";
        switch (day) {
            case 1:
                Week += "一";
                break;
            case 2:
                Week += "二";
                break;
            case 3:
                Week += "三";
                break;
            case 4:
                Week += "四";
                break;
            case 5:
                Week += "五";
                break;
            case 6:
                Week += "六";
                break;
            default:
                Week += "日";
                break;
        }
        return Week;
    }


    public static String getlongtime(long start, long end) {
//      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      try {
//          Date d1 = df.parse(start);
//          Date d2 = df.parse(end);
          long diff = Math.abs(end - start);//这样得到的差值是微秒级别

          long days = diff / (1000 * 60 * 60 * 24);
          long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
          long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);


          return (int)(hours * 60 + minutes) + "分钟";
      } catch (Exception e) {
      }
      return "30分钟";
    }
}
