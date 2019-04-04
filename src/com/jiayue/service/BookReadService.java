package com.jiayue.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.jiayue.dto.base.BookPage;
import com.jiayue.dto.base.TextPart;
import com.jiayue.util.MyPreferences;

import android.util.Log;
import android.util.Xml;

/**
 * 分页
 * 
 * @author Administrator mingjuan.liang
 * 
 */
public class BookReadService {
	public int totalPage = 0;

	/**
	 * 获取制定页的内容
	 * 
	 * @param path
	 *            文件的完整路径
	 * @param pageNo
	 *            页码
	 * @param imagePath
	 *            图片地址的前缀
	 * @return 此页的内容
	 * @throws Exception
	 */
	public BookPage getBookPageInfo(String path, int pageNo, String imagePath)
			throws Exception {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		InputStream is = new BufferedInputStream(fis);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");

		BookPage bookPage = null;

		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
				case XmlPullParser.START_TAG :
					if ("umybook".equals(parser.getName())) {
						totalPage = Integer.parseInt(parser
								.getAttributeValue(0));
						Log.i("BookReadService", parser.getName() + ":"
								+ totalPage);
					} else if ("page".equals(parser.getName())
							&& pageNo == Integer.parseInt(parser
									.getAttributeValue(0))) {

						bookPage = new BookPage();
						bookPage.setPageNo(pageNo);
						Log.i("BookReadService", parser.getName() + ":"
								+ pageNo);
					} else if ("p".equals(parser.getName())) {
						if (null != bookPage) {
							String str = parser.nextText();
							bookPage.addPart(new TextPart(MyPreferences.P_TYPE,
									str));
							Log.i("BookReadService", parser.getName() + ":"
									+ str);
						}
					} else if ("img".equals(parser.getName())) {
						if (null != bookPage) {
							String str = parser.getAttributeValue(0);
							bookPage.addPart(new TextPart(
									MyPreferences.IMG_TYPE, imagePath + str));
							Log.i("BookReadService", parser.getName() + ":"
									+ str);
						}
					}
					break;
				case XmlPullParser.END_TAG :
					if ("page".equals(parser.getName()) && null != bookPage) {
						Log.i("BookReadService", parser.getName() + ":end");
						return bookPage;
					}
					break;
			}
			type = parser.next();
		}
		return bookPage;
	}
}
