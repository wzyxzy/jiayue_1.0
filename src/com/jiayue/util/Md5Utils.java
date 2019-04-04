package com.jiayue.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
	/**
	 * md5加密方法
	 * 
	 * @param password
	 *            要加密的密码
	 * @return 返回加密后的密文
	 */
	public static String encode(String password) {

		try {
			// 得到一个信息摘要器
			MessageDigest digest = MessageDigest.getInstance("md5");
			StringBuffer buffer = new StringBuffer();
			byte[] result = digest.digest(password.getBytes());
			for (byte b : result) {
				// 每byte与8个二进制位做与运算 0xff;
				int number = b & 0xff;// 加盐
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					// 在前面加上一个0
					buffer.append("0");
				}
				buffer.append(str);
				// System.out.println(str);

			}

			// 得到了标准的Md5加密后的结果buffer.toString()
			System.out.println(buffer.toString());
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}
	/**
	 * md5加密方法
	 * 
	 * @param password
	 *            要加密的密码
	 * @return 返回加密后的密文
	 */
	public static String getFileMd5(File file) {

		try {
			// 得到一个信息摘要器
			MessageDigest digest = MessageDigest.getInstance("md5");
			StringBuffer sb = new StringBuffer();
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {// 用流的方式 去获取每一个byte数组的数组摘要
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			for (byte b : result) {
				// 每byte与8个二进制位做与运算 0xff;
				int number = b & 0xff;// 加盐
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					// 在前面加上一个0
					sb.append("0");
				}
				sb.append(str);
				// System.out.println(str);

			}
			// 得到了标准的Md5加密后的结果buffer.toString()
			System.out.println(sb.toString());
			return sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}
}
