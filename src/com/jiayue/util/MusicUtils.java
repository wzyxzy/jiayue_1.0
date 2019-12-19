package com.jiayue.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import android.net.Uri;


public class MusicUtils {
	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

	public MusicUtils() {
		// 转换成字符串的时间
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

	}

	/**
	 * 把毫秒转换成：1:20:30这里形式
	 * @param timeMs
	 * @return
	 */
	public String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;
		
		int minutes = (totalSeconds / 60) % 60;
		
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}
	/**
	 * 得到当前系统的时间
	 * @return
	 */
	public String getSystemTime(){
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.format(new Date());
	}
	/**
	 * 判断是否是网络播放资源
	 * @param uri
	 * @return
	 */
	public boolean isNetUri(Uri uri){
		return uri != null && uri.toString().contains("http")
				|| uri.toString().contains("mms")||
				uri.toString().contains("rtps");
		
	}
}
