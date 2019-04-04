package com.jiayue.util;

/**
 * Created by IntelliJ IDEA.
 * User: http
 * Date: 11-10-6
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {

	public static final int HTTP_ARGUMENT_ERR = -1001;// HTTP 参数错误
	public static final int HTTP_RESPONSE_EMPTY = -1002;// Http Response is
														// Empty
	public static final int HTTP_URL_ERR = -1003;// Url格式错误
	public static final int HTTP_GZIP_ERR = -1004;// 响应数据解压缩失败
	public static final int HTTP_CANCELED = -1005;// 当前下载已取消
	public static final int HTTP_EXCEPTION = -1006;// 发生异常
	public static final int TIMEOUT = 30000;// 超时时间30秒
	private static final int BUF_LEN = 512;// 数据缓冲长度

	HttpURLConnection connection = null;
	private boolean bIsStop = false;
	protected Object objAbort = new Object();
	private int lastErrCode = 0;// 最近一次出错的错误代码
	private boolean bNeedGzip = false;
	byte[] tmpBuf = new byte[BUF_LEN];
	byte[] tmpBuf2 = new byte[BUF_LEN * 2];

	private Handler mHandler = null;
	public static final int POST_PROGRESS_NOTIFY = 101;

	public HttpUtil() {
	}

	public int getLastErrCode() {
		return lastErrCode;
	}

	public void setGzip(boolean bNeedGzip) {
		this.bNeedGzip = bNeedGzip;
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	private HttpURLConnection getConnection(Context context, URL url) throws Exception {
		String[] apnInfo = null;
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int wifiState = wifiManager.getWifiState();
		if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
			// 如果没连wifi，判断一下默认的apn是否需要代理
			apnInfo = checkApnInfo(context);
		}
		HttpURLConnection conn = null;
		// 设置代理
		if (apnInfo != null && !TextUtils.isEmpty(apnInfo[0]) && !TextUtils.isEmpty(apnInfo[1])) {
			InetSocketAddress addr = new InetSocketAddress(apnInfo[0], Integer.valueOf(apnInfo[1]));
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
			conn = (HttpURLConnection) url.openConnection(proxy);
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		return conn;
	}

	/** 发送GET请求 */
	public String get(Context context, final String strUrl) {
		if (TextUtils.isEmpty(strUrl) || context == null) {
			lastErrCode = HTTP_ARGUMENT_ERR;
			return null;
		}
		final String fixurl;
		if (bNeedGzip) {
			fixurl = strUrl + "&gzip=1";
		} else {
			fixurl = strUrl;
		}
		URL getUrl = null;

		try {
			getUrl = new URL(fixurl);
		} catch (MalformedURLException ex) {
			Log.e("HttpUtil", "get MalformedURL", ex);
			lastErrCode = HTTP_URL_ERR;
			return null;
		}
		bIsStop = false;
		InputStream input = null;
		ByteArrayOutputStream byteOutStream = null;
		HttpURLConnection conn = null;
		byte[] outData = null;
		try {
			conn = getConnection(context, getUrl);
			connection = conn;
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.setDoInput(true);

			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return null;
			}
			conn.connect();
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return null;
			}
			input = conn.getInputStream();
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return null;
			}
			String webcontent = null;

			BufferedReader br = new BufferedReader(new UnicodeReader(input, Charset.defaultCharset().name()));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			webcontent = sb.toString();
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return null;
			}
			return webcontent;
		} catch (Exception ex) {
			Log.e("HttpUtil", "get", ex);
			// return ex.getMessage();
		} finally {
			try {
				outData = null;
				if (input != null) {
					input.close();
					input = null;
				}
				if (byteOutStream != null) {
					byteOutStream.close();
					byteOutStream = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			} catch (Exception ex) {
				Log.e("HttpUtil", "get finally", ex);
				// return ex.getMessage();
			}
			if (bIsStop) {
				synchronized (objAbort) {
					objAbort.notify();
				}
			}
		}
		if (bIsStop) {
			lastErrCode = HTTP_CANCELED;
		} else {
			lastErrCode = HTTP_EXCEPTION;
		}
		return null;
	}

	/**
	 * 是否可以压缩
	 * 
	 * @author Saifei
	 * @param picPath
	 * @return
	 */
	public static boolean canCompress(String picPath) {
		Options ops = new Options();
		ops.inJustDecodeBounds = true;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(picPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BitmapFactory.decodeStream(fis, null, ops);
		if (ops.outHeight > 960 || ops.outWidth > 720) {
			return true;
		}
		return false;
	}

	/**
	 * 下载图片并保存为文件
	 * 
	 * @param strUrl
	 *            图片地址
	 * @param fileName
	 *            图片文件名
	 * @return
	 */
	public synchronized boolean downloadImage(Context context, String strUrl, String fileName) {
		if (TextUtils.isEmpty(strUrl)) {
			lastErrCode = HTTP_ARGUMENT_ERR;
			return false;
		}

		URL getUrl = null;
		try {
			getUrl = new URL(strUrl);
		} catch (MalformedURLException ex) {
			Log.e("HttpUtil", "get MalformedURL", ex);
			lastErrCode = HTTP_URL_ERR;
			return false;
		}
		bIsStop = false;
		InputStream input = null;
		HttpURLConnection conn = null;
		// File tmpBmpFile = new File(fileName + ".tmp");
		File tmpBmpFile = new File(fileName);
		if (!tmpBmpFile.exists()) {
			try {
				tmpBmpFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try {
			conn = getConnection(context, getUrl);
			connection = conn;
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.setDoInput(true);
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return false;
			}
			conn.connect();
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return false;
			}
			input = conn.getInputStream();

			fos = new FileOutputStream(tmpBmpFile);

			if (getResponse(input, fos, tmpBuf2)) {
				if (fos != null) {
					fos.close();
					fos = null;
				}
				boolean ret = tmpBmpFile.renameTo(new File(fileName));
				return ret;
			}
		} catch (Exception ex) {
			Log.e("HttpUtil", "downloadImage", ex);
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
					fos = null;
				}
				if (input != null) {
					input.close();
					input = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
				if (bIsStop) {
					synchronized (objAbort) {
						objAbort.notify();
					}
				}
			} catch (Exception ex) {
				Log.e("HttpUtil", "downloadImage finally", ex);
			}
		}
		if (bIsStop) {
			lastErrCode = HTTP_CANCELED;
		} else {
			lastErrCode = HTTP_EXCEPTION;
		}
		return false;
	}

	public File downloadFile(Context context, String urlStr, String outPutPath, String name) {

		if (TextUtils.isEmpty(urlStr)) {
			return null;
		}

		String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

		URL url = null;
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		RandomAccessFile outputStream = null;
		File outFile = null;
		int length = 0;
		bIsStop = false;

		try {
			url = new URL(urlStr);
			System.out.println("url==>" + url);
			httpURLConnection = getConnection(context, url);

			httpURLConnection.setAllowUserInteraction(true);
			// 设置当前线程下载的起点，终点
			length = httpURLConnection.getContentLength();
			int startPosition = 0;

			// 设置断点续传的开始位置
			// httpURLConnection.setRequestProperty("Range", "bytes=" +
			// (length-startPosition));
			// ????????????????????????Cannot set method after connection is
			// made

			inputStream = httpURLConnection.getInputStream();

			// //设置User-Agent
			// httpURLConnection.setRequestProperty("User-Agent","NetFox");
			// //设置断点续传的开始位置
			// httpURLConnection.setRequestProperty("RANGE","bytes=4096");

			outFile = new File(outPutPath + "/" + fileName + ".tmp");
			if (!outFile.getParentFile().exists()) {
				// 如果目标文件所在的目录不存在，则创建父目录
				System.out.println("目标文件所在目录不存在，准备创建它！");
				if (!outFile.getParentFile().mkdirs()) {
					System.out.println("创建目标文件所在目录失败！");
				}
			}

			// 使用java中的RandomAccessFile 对文件进行随机读写操作
			outputStream = new RandomAccessFile(outFile, "rw");
			// 设置开始写文件的位置
			outputStream.seek(startPosition);

			byte[] buf = new byte[1024 * 10];
			int read = 0;
			int curSize = startPosition;
			Log.d("debug", "buf：" + buf.length);
			while (!bIsStop) {

				read = inputStream.read(buf);
				if (read == -1) {
					break;
				}
				outputStream.write(buf, 0, read);
				curSize = curSize + read;

				Message msg = mHandler.obtainMessage();
				msg.what = POST_PROGRESS_NOTIFY;
				msg.arg1 = (int) (curSize * 100.0f / length);
				mHandler.sendMessage(msg);

				if (curSize == length) {
					break;
				}
				// Thread.sleep(10);
			}
			// 没有下载完成
			if (bIsStop) {
				System.out.println("执行 取消的方法");
				outFile.delete();
				lastErrCode = HTTP_CANCELED;
				return null;
			}

			if (name != null) {// 命名成指定的名字
				File f = new File(outPutPath + "/" + name);
				if (outFile.renameTo(f)) {
					return f;
				} else {
					return null;
				}
			} else {
				// if (outFile.renameTo(new File(outPutPath + "/" + fileName)))
				// {
				return null;
				// }
			}

		} catch (MalformedURLException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		} finally {

			try {
				if (inputStream != null)
					inputStream.close();
				if (outputStream != null)
					outputStream.close();

				if (httpURLConnection != null)
					httpURLConnection.disconnect();

			} catch (IOException e) {
				e.printStackTrace();
			}
			if (bIsStop) {
				synchronized (objAbort) {
					objAbort.notify();
				}
			}
		}
	}

	/**
	 * 下载图片并保存为文件
	 * 
	 * @param strUrl
	 *            图片地址
	 * @return
	 */
	/*
	 * public Bitmap downloadImage(String strUrl) { Bitmap bitmap = null;
	 * 
	 * if (TextUtils.isEmpty(strUrl)) { lastErrCode = HTTP_ARGUMENT_ERR; return
	 * null; }
	 * 
	 * URL getUrl = null; try { getUrl = new URL(strUrl); } catch
	 * (MalformedURLException ex) { Log.e("HttpUtil", "get MalformedURL", ex);
	 * lastErrCode = HTTP_URL_ERR; return null; } bIsStop = false; InputStream
	 * input = null; ByteArrayOutputStream byteOutStream = null;
	 * HttpURLConnection conn = null; byte[] outData = null; try { conn =
	 * (HttpURLConnection) getUrl .openConnection(); connection = conn;
	 * conn.setConnectTimeout(TIMEOUT); conn.setReadTimeout(TIMEOUT);
	 * conn.setDoInput(true); if(bIsStop){ lastErrCode = HTTP_CANCELED; return
	 * null; } conn.connect(); if(bIsStop){ lastErrCode = HTTP_CANCELED; return
	 * null; } input = conn.getInputStream(); byteOutStream = new
	 * ByteArrayOutputStream(); //byte[] buf = new byte[BUF_LEN];
	 * 
	 * outData = getResponse(input, byteOutStream, tmpBuf); if(bIsStop){
	 * lastErrCode = HTTP_CANCELED; return null; } if(outData != null &&
	 * outData.length > 0){ bitmap = BitmapFactory.decodeByteArray(outData, 0,
	 * outData.length); } if(bIsStop){ lastErrCode = HTTP_CANCELED; return null;
	 * } return bitmap; } catch (Exception ex) { Log.e("HttpUtil",
	 * "downloadImage", ex); } catch(OutOfMemoryError ex){ ex.printStackTrace();
	 * } finally { try { outData = null; if (input != null){ input.close();
	 * input = null; } if (conn != null){ conn.disconnect(); conn = null; } if
	 * (byteOutStream != null){ byteOutStream.close(); byteOutStream = null; }
	 * if(bIsStop){ synchronized (objAbort) { objAbort.notify(); } } } catch
	 * (Exception ex) { Log.e("HttpUtil", "downloadImage finally", ex); } }
	 * if(bIsStop){ lastErrCode = HTTP_CANCELED; } else{ lastErrCode =
	 * HTTP_EXCEPTION; } return null; }
	 */

	public String post(Context context, final String strUrl, InputStream dataStream, int iStreamLen) {

		if (TextUtils.isEmpty(strUrl) || dataStream == null || iStreamLen <= 0) {
			lastErrCode = HTTP_ARGUMENT_ERR;
			return null;
		}

		URL postUrl = null;
		try {
			postUrl = new URL(strUrl);
		} catch (MalformedURLException ex) {
			Log.e("HttpUtil", "get MalformedURL", ex);
			lastErrCode = HTTP_URL_ERR;
			return null;
		}
		bIsStop = false;
		InputStream input = null;
		DataOutputStream ds = null;
		ByteArrayOutputStream byteOutStream = null;
		HttpURLConnection conn = null;
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		byte[] outData = null;
		try {
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return null;
			}
			conn = getConnection(context, postUrl);
			connection = conn;
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return null;
			}
			// byte[] data = new byte[BUF_LEN];
			ds = new DataOutputStream(conn.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; name=\"file1\";filename=\"test.jpg\"" + end);
			ds.writeBytes(end);
			int len = 0;
			int postLen = 0;
			while (!bIsStop && ((len = dataStream.read(tmpBuf2)) != -1)) {
				ds.write(tmpBuf2, 0, len);
				// notify post progress
				postLen += len;
				if (mHandler != null) {
					Message msg = new Message();
					msg.what = POST_PROGRESS_NOTIFY;
					msg.arg1 = (postLen * 80) / iStreamLen;
					mHandler.sendMessage(msg);
				}
			}
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return null;
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.flush();

			input = conn.getInputStream();

			// byteOutStream = new ByteArrayOutputStream();
			//
			// outData = getResponse(input, byteOutStream, tmpBuf);

			int ch;

			StringBuffer b = new StringBuffer();
			while ((ch = input.read()) != -1) {
				b.append((char) ch);
			}

			if (mHandler != null) {
				Message msg = new Message();
				msg.what = POST_PROGRESS_NOTIFY;
				msg.arg1 = 90;
				mHandler.sendMessage(msg);
			}

			String webcontent = b.toString();

			return webcontent;
		} catch (Exception ex) {
			Log.e("HttpUtil", "post", ex);
		} catch (OutOfMemoryError ex) {
			Log.e("HttpUtil", "post OutOfMemoryError", ex);
		} finally {
			try {
				outData = null;
				if (input != null) {
					input.close();
					input = null;
				}
				if (ds != null) {
					ds.close();
					ds = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
				if (byteOutStream != null) {
					byteOutStream.close();
					byteOutStream = null;
				}
				if (bIsStop) {
					synchronized (objAbort) {
						objAbort.notify();
					}
				}
				if (mHandler != null) {
					Message msg = new Message();
					msg.what = POST_PROGRESS_NOTIFY;
					msg.arg1 = 100;
					mHandler.sendMessage(msg);
				}
			} catch (Exception ex) {
				Log.e("HttpUtil", "post finally", ex);
			}
		}
		if (bIsStop) {
			lastErrCode = HTTP_CANCELED;
		} else {
			lastErrCode = HTTP_EXCEPTION;
		}
		return null;
	}

	private boolean getResponse(InputStream input, OutputStream os, byte[] data) throws IOException {
		if (input == null || os == null || data == null) {
			return false;
		}
		int i = 0;
		while (!bIsStop && (i = input.read(data)) != -1) {
			os.write(data, 0, i);
			os.flush();
		}
		os.flush();
		if (bIsStop) {
			lastErrCode = HTTP_CANCELED;
			return false;
		}
		return true;
	}

	private byte[] getResponse(InputStream input, ByteArrayOutputStream byteOutStream, byte[] data) throws IOException {
		if (input == null || byteOutStream == null || data == null) {
			return null;
		}
		int i = 0;
		while (!bIsStop && (i = input.read(data)) != -1) {
			byteOutStream.write(data, 0, i);
		}
		if (bIsStop) {
			lastErrCode = HTTP_CANCELED;
			return null;
		}
		byte[] bmpData = byteOutStream.toByteArray();
		return bmpData;
	}

	public synchronized void cancel() {
		try {
			bIsStop = true;
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
			synchronized (objAbort) {
				objAbort.wait(50);
			}
		} catch (Exception ex) {
			Log.v("HttpUtil", "canel", ex);
		}
	}

	public String[] checkApnInfo(Context context) {
		if (context == null) {
			return null;
		}
		String[] apnInfo = null;
		/*
		 * ConnectivityManager connectivity = (ConnectivityManager)
		 * context.getSystemService(Context.CONNECTIVITY_SERVICE); if
		 * (connectivity == null) { return null; } NetworkInfo info =
		 * connectivity.getActiveNetworkInfo(); if(info.isAvailable()){ return
		 * null; } String strApn = info.getExtraInfo(); Cursor mCursor =
		 * context.getContentResolver().query(
		 * Uri.parse("content://telephony/carriers"), new String[] { "apn",
		 * "Proxy", "Port" }, "current=1", null, null); if (mCursor != null) {
		 * try { while(mCursor.moveToNext()){ String apn = mCursor.getString(0);
		 * if(strApn.compareTo(apn) == 0){//找到当前使用的apn了 apnInfo = new String[2];
		 * apnInfo[0] = mCursor.getString(1); apnInfo[1] = mCursor.getString(2);
		 * } } } catch (Exception ex) { ex.printStackTrace(); } finally {
		 * mCursor.close(); } }
		 */
		Cursor mCursor = context.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), null, null, null, null);
		if (mCursor != null) {
			try {
				mCursor.moveToFirst();
				apnInfo = new String[2];
				apnInfo[0] = mCursor.getString(mCursor.getColumnIndex("proxy"));
				apnInfo[1] = mCursor.getString(mCursor.getColumnIndex("port"));
				// apnInfo[2] =
				// mCursor.getString(mCursor.getColumnIndex("user"));
				// apnInfo[3] =
				// mCursor.getString(mCursor.getColumnIndex("password"));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				mCursor.close();
			}
		}
		return apnInfo;
	}

	public static int checkNetworkAvailable(Context context) {
		if (context == null) {
			return -1;
		}

		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return -1;
		}

		/*
		 * NetworkInfo info = connectivity.getActiveNetworkInfo(); if(info ==
		 * null){ return false; } if(info.isAvailable()){ return true; }
		 */
		// 20100622，为三星双网双待手机增加的网络检查
		/*
		 * TelephonyManager telephonyManager = (TelephonyManager)
		 * context.getSystemService(Context.TELEPHONY_SERVICE); TelephonyManager
		 * telephonyManager2 = (TelephonyManager)
		 * context.getSystemService(Context.TELEPHONY_SERVICE_2);
		 * if(telephonyManager != null && telephonyManager2 != null){ int
		 * nSimState = telephonyManager.getSimState(); int nSimStateGsm =
		 * telephonyManager2.getSimState(); WifiManager wifiManager =
		 * (WifiManager) context.getSystemService(Context.WIFI_SERVICE); int
		 * wifiState = wifiManager.getWifiState(); if(wifiState ==
		 * WifiManager.WIFI_STATE_DISABLED) { if(nSimState !=
		 * TelephonyManager.SIM_STATE_READY && nSimStateGsm ==
		 * TelephonyManager.SIM_STATE_READY) { return 0; } } }
		 */

		NetworkInfo[] info = connectivity.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return 1;
				}
			}
		}
		/*
		 * try{ //DialogUtil.showKXAlertDialog(context,
		 * "startUsingNetworkFeature", null);
		 * if(connectivity.startUsingNetworkFeature
		 * (ConnectivityManager.TYPE_MOBILE, "internet") != -1){
		 * 
		 * synchronized (context) { context.wait(10); }
		 * 
		 * return true; } } catch(Exception ex){
		 * //DialogUtil.showKXAlertDialog(context, ex.getMessage(), null);
		 * Log.v("HttpUtil", "startUsingNetworkFeature", ex); }
		 */
		return -1;
	}

	/**
	 * 检查网络状态，如果网络无法连接则给出相应的提示 return 0 网络正常 return -1 联网失败
	 * */
	public static int checkNetworkAndHint(Context context) {
		if (checkNetworkAvailable(context) < 0) {
			// ActivityUtils.showMessage(context, "无网络信号");
			return -1;
		} else if (HttpUtil.checkNetworkAvailable(context) == 0) {
			// ActivityUtils.showMessage(context, "网络连接失败");
			return -1;
		}
		return 0;
	}

	public Boolean postWithoutResponse(Context context, final String strUrl, InputStream dataStream, int iStreamLen) {

		if (TextUtils.isEmpty(strUrl) || dataStream == null || iStreamLen <= 0) {
			lastErrCode = HTTP_ARGUMENT_ERR;
			return false;
		}

		URL postUrl = null;
		try {
			postUrl = new URL(strUrl);
		} catch (MalformedURLException ex) {
			Log.e("HttpUtil", "get MalformedURL", ex);
			lastErrCode = HTTP_URL_ERR;
			return false;
		}
		bIsStop = false;
		InputStream input = null;
		DataOutputStream ds = null;
		ByteArrayOutputStream byteOutStream = null;
		HttpURLConnection conn = null;
		byte[] outData = null;
		try {
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return false;
			}
			conn = getConnection(context, postUrl);
			connection = conn;
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(TIMEOUT * 3);
			conn.setReadTimeout(TIMEOUT * 3);

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/octet-stream");
			conn.setRequestProperty("Content-Length", String.valueOf(iStreamLen));
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return false;
			}
			// byte[] data = new byte[BUF_LEN];
			ds = new DataOutputStream(conn.getOutputStream());
			int len = 0;
			int postLen = 0;
			while (!bIsStop && ((len = dataStream.read(tmpBuf2)) != -1)) {
				ds.write(tmpBuf2, 0, len);
				ds.flush();
				// notify post progress
				postLen += len;
				if (mHandler != null) {
					Message msg = new Message();
					msg.what = POST_PROGRESS_NOTIFY;
					msg.arg1 = (postLen * 80) / iStreamLen;
					mHandler.sendMessage(msg);
				}
			}
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
				return false;
			}
			ds.flush();
			ds.close();
			ds = null;

			if (mHandler != null) {
				Message msg = new Message();
				msg.what = POST_PROGRESS_NOTIFY;
				msg.arg1 = 90;
				mHandler.sendMessage(msg);
			}
			return true;
		} catch (Exception ex) {
			Log.e("HttpUtil", "post", ex);
			if (bIsStop) {
				lastErrCode = HTTP_CANCELED;
			} else {
				lastErrCode = HTTP_EXCEPTION;
			}
			return false;
		} finally {
			try {
				outData = null;
				if (input != null) {
					input.close();
					input = null;
				}
				if (ds != null) {
					ds.close();
					ds = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
				if (byteOutStream != null) {
					byteOutStream.close();
					byteOutStream = null;
				}
				if (bIsStop) {
					synchronized (objAbort) {
						objAbort.notify();
					}
				}
				if (mHandler != null) {
					Message msg = new Message();
					msg.what = POST_PROGRESS_NOTIFY;
					msg.arg1 = 100;
					mHandler.sendMessage(msg);
				}
			} catch (Exception ex) {
				Log.e("HttpUtil", "post finally", ex);
			}
		}

	}

	public static boolean checkWifiState(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int wifiState = wifiManager.getWifiState();
		if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
			// 如果没连wifi，判断一下默认的apn是否需要代理
			return false;
		}
		return true;
	}
}
