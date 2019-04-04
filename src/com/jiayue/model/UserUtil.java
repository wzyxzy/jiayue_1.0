package com.jiayue.model;

import com.jiayue.R;
import com.jiayue.dto.base.UserVO;
import com.jiayue.util.SPUtility;

import android.content.Context;

public class UserUtil {

	private static UserVO instance;
	public static Boolean isLogin = false;

	// 当前登录用户信息
	public static synchronized UserVO getInstance(Context context) {
		instance = loadUser(context);
		return instance;
	}
	public static UserVO loadUser(Context context) {
		isLogin = SPUtility.getSPBoolean(context, R.string.islogin);
		if (isLogin) {
			UserVO userVO = new UserVO();
			userVO.setUserId(SPUtility.getSPString(context, "userid"));
			userVO.setUserName(SPUtility.getSPString(context, "username"));
			userVO.setPassword(SPUtility.getSPString(context, "password"));
			userVO.setEmail(SPUtility.getSPString(context, "email"));
			userVO.setUserStatus(SPUtility.getSPInteger(context,"userStatus"));
			// userVO.setPublishName(SPUtility.getSPString(context,
			// "publishName"));
			// userVO.setPublishId(SPUtility.getSPString(context, "publishId"));
			return userVO;
		} else {
			return null;
		}
	}
}
