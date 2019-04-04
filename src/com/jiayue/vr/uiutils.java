/**   
* 类名称：uiutils   
* 类描述：   
* Copyright:BesTV. All Rights Reserved.
* 版权:百视通公司版权所有
* 创建人：xu.chaokun   
* 创建时间：2013-4-16 下午1:40:25
*/
package com.jiayue.vr;

import android.content.Context;
import android.content.SharedPreferences;


public class uiutils {	
	public static final String PREFS_NAME = "SnailVideoPlayer";  
		
	public static String getPreferenceKeyValue(Context ct, String key, String defValue){
		String ret=defValue;
		try {
			SharedPreferences settings = ct.getSharedPreferences(PREFS_NAME, 0);  
			ret = settings.getString(key, defValue);  
		} catch (Throwable e){
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void setPreferenceKeyValue(Context ct, String key, String value){  
		try {
			SharedPreferences settings = ct.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  		
			editor.putString(key, value);  
			editor.commit();  
		} catch (Throwable e){
			e.printStackTrace();
		}
	}
	
	public static int getPreferenceKeyIntValue(Context ct, String key, int defValue){
		int ret=defValue;
		try {
			SharedPreferences settings = ct.getSharedPreferences(PREFS_NAME, 0);  
			ret = settings.getInt(key, defValue);  
		} catch (Throwable e){
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void setPreferenceKeyIntValue(Context ct, String key, int value){  
		try {
			SharedPreferences settings = ct.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  		
			editor.putInt(key, value);  
			editor.commit();  
		} catch (Throwable e){
			e.printStackTrace();
		}
	}
	
	
	public static boolean getPreferenceKeyBooleanValue(Context ct, String key, boolean defValue){
		boolean ret=defValue;
		try {
			SharedPreferences settings = ct.getSharedPreferences(PREFS_NAME, 0);  
			ret = settings.getBoolean(key, defValue);  
		} catch (Throwable e){
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void setPreferenceKeyBooleanValue(Context ct, String key, boolean value){  
		try {
			SharedPreferences settings = ct.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  		
			editor.putBoolean(key, value);  
			editor.commit();  
		} catch (Throwable e){
			e.printStackTrace();
		}
	}
	
	public static void removePreferenceKey(Context ct, String key){  
		try {
			SharedPreferences settings = ct.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  		
			editor.remove(key); 
			editor.commit();  
		} catch (Throwable e){
			e.printStackTrace();
		}
	}
	
	public static void clearPrerence(Context ct){
		try {
			SharedPreferences settings = ct.getSharedPreferences(PREFS_NAME, 0);  
			SharedPreferences.Editor editor = settings.edit();  		
			editor.clear();
			editor.commit();  
		} catch (Throwable e){
			e.printStackTrace();
		}
	}	
}
