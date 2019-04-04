package com.jiayue.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import android.text.TextUtils;

public class StringUtil {

	private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {

		}
		return resultString;
	}

	public static String getApiName(String url) {
		int posstart = url.indexOf("/");
		int posend = url.indexOf("?");
		posstart = posstart > 0 ? posstart : 0;
		String apiname = posend > 0 ? url.substring(posstart, posend) : url
				.substring(posstart);
		return apiname;
	}

	/*
	 * public static boolean checkEmail(String email) { String tag1 = "@";
	 * String tag2 = "."; if ((email.indexOf(tag1) != -1) &&
	 * (email.indexOf(tag2) != -1)) return true; else { return false; } }
	 */

	public static String toUtf8(String str) {
		try {
			byte[] strb = null;
			strb = str.getBytes("UTF-8");
			String newStr = new String(strb);
			return newStr;
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	public static String removeAllToken(String originalString, String token) {
		int index = originalString.indexOf(token);
		while (index >= 0) {
			String str1 = originalString.substring(0, index);
			String str2 = originalString.substring(index + token.length());
			if (index == 0) {
				originalString = str2;
			} else if (index + token.length() == originalString.length()) {
				originalString = str1;
			} else {
				originalString = str1 + str2;
			}
			index = originalString.indexOf(token);
		}
		return originalString;
	}

	public static String replaceTokenWith(String originalString, String token,
			String replacement) {
		int index = originalString.indexOf(token);
		if (index != -1) {
			String ret = originalString.substring(0, index) + replacement
					+ originalString.substring(index + token.length());
			return ret;
		} else {
			return originalString;
		}
	}

	public static boolean isChinese(String str) {
		if (TextUtils.isEmpty(str)) {
			return true;
		}
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char word = str.charAt(i);
			if ((word >= 0x4e00) && (word <= 0x9fbb)) {
				continue;// 是汉字
			}
			return false;
		}
		return true;
	}

	public static int charLength(String str) {
		int size = str.length();
		int len = 0;
		for (int i = 0; i < size; i++) {
			char c = str.charAt(i);
			if ((c >= 0x4e00) && (c <= 0x9fbb)) {
				len += 2;// 是汉字
			} else {
				len += 1;
			}
		}
		return len;
	}

	public static String subString(String text, int length, String endWith) {
		if (text == null || text.equals("")) {
			return "";
		}
		int textLength = text.length();
		int byteLength = 0;
		StringBuffer returnStr = new StringBuffer();
		for (int i = 0; i < textLength && byteLength < length * 2; i++) {
			String str_i = text.substring(i, i + 1);
			if (str_i.getBytes().length == 1) {// 英文
				byteLength++;
			} else {// 中文
				byteLength += 2;
			}
			returnStr.append(str_i);
		}
		try {
			if (byteLength < text.getBytes("GBK").length) {// getBytes("GBK")每个汉字长2，getBytes("UTF-8")每个汉字长度为3
				returnStr.append(endWith);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return returnStr.toString();
	}

}
