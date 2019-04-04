package com.jiayue.util;
public class UnRepairString {

	public static String removerepeatedchar(String s) {
		if (s == null)
			return s;

		StringBuffer sb = new StringBuffer();
		int i = 0, len = s.length();
		while (i < len) {
			char c = s.charAt(i);
			sb.append(c);
			i++;
			while (i < len && s.charAt(i) == c) {// 这个是如果这两个值相等，就让i+1取下一个元素
				i++;
			}
		}
		return sb.toString();
	}

}
