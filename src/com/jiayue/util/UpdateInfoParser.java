package com.jiayue.util;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class UpdateInfoParser {

	/**
	 * 
	 * @param is
	 *            xml�ļ���������
	 * @return updateinfo��bean�����Ϣ
	 * @throws Exception
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");
		UpdateInfo info = new UpdateInfo();
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {

			switch (type) {
				case XmlPullParser.START_TAG :
					if ("version".equals(parser.getName())) {
						info.setVersion(parser.nextText());
					} else if ("description".equals(parser.getName())) {
						info.setDescription(parser.nextText());
					} else if ("url".equals(parser.getName())) {
						info.setUrl(parser.nextText());
					}
					break;
			}
			type = parser.next();
		}
		return info;
	}
}
