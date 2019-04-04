//package com.jiayue.util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//import android.content.Context;
//
//import com.jiayue.BookReadActivity;
//import com.jiayue.BookSynActivity;
//import com.jiayue.dto.base.BookPage;
//import com.jiayue.dto.base.BookVO;
//import com.jiayue.soap.BookJson;
//
//public class SerializableUtil {
//	/**
//	 * 将BookVO对象序列化到本地
//	 */
//	public static void serializeBooKVO(Context context, BookVO bookVO) {
//		try {
//			File target = new File(context.getCacheDir(), bookVO.getBookId());//
//			ObjectOutputStream oo = new ObjectOutputStream(
//					new FileOutputStream(target, false));
//			oo.writeObject(bookVO);
//			System.out.println(BookSynActivity.class.getName()
//					+ "=====bookVO对象序列化成功！");
//			oo.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 将BookPage对象序列化到本地
//	 */
//	public static void serializeBookPage(Context context, BookPage page) {
//		try {
//			File target = new File(context.getCacheDir(), page.getPageNo() + "");//
//			ObjectOutputStream oo = new ObjectOutputStream(
//					new FileOutputStream(target, false));
//			oo.writeObject(page);
//			System.out.println(BookReadActivity.class.getName()
//					+ "=====BookPage对象序列化成功！");
//			oo.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/*
//	 * 反序列化BookPage
//	 */
//	public static BookPage unSerializeBookPage(Context context, String pageNo) {
//		BookPage bv = null;
//		try {
//			File source = new File(context.getCacheDir(), pageNo);
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
//					source));
//			bv = (BookPage) ois.readObject();
//			System.out.println(BookReadActivity.class.getName()
//					+ "=====bookVO对象反序列化成功！");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return bv;
//	}
//
//	/*
//	 * 反序列化BooKVO
//	 */
//	public static BookVO unSerializeBookVO(Context context, String bookId) {
//		BookVO bv = null;
//		try {
//			File source = new File(context.getCacheDir(), bookId);
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
//					source));
//			bv = (BookVO) ois.readObject();
//			System.out.println(BookSynActivity.class.getName()
//					+ "=====bookVO对象反序列化成功！");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return bv;
//	}
//	/**
//	 * 将BookVO对象序列化到本地
//	 */
//	public static void serializeBookJson(Context context, String bookId,
//			BookJson bookJson) {
//		try {
//			File target = new File(context.getCacheDir(), bookId + ".json");//
//			ObjectOutputStream oo = new ObjectOutputStream(
//					new FileOutputStream(target, false));
//			oo.writeObject(bookJson);
//			System.out.println(SerializableUtil.class.getName()
//					+ "=====bookJson对象序列化成功！");
//			oo.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/*
//	 * 反序列化BooKVO
//	 */
//	public static BookJson unSerializeBookJson(Context context, String bookId) {
//		BookJson bj = null;
//		try {
//			File source = new File(context.getCacheDir(), bookId + ".json");
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
//					source));
//			bj = (BookJson) ois.readObject();
//			System.out.println(SerializableUtil.class.getName()
//					+ "=====BookJson对象反序列化成功！");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return bj;
//	}
//
//}
