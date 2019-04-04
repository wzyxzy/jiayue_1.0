package com.jiayue.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Path;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class ImageUtil {

	private Hashtable<String, Bitmap> imgcache = new Hashtable<String, Bitmap>();
	private ArrayList<String> urlList = new ArrayList<String>();
	private String m_strPicCachePath = ActivityUtils.getSDPath()
			.getAbsolutePath() + "/images/";
	private int mMaxMemCount = 20;
	private int mDelCount = 5;
	private int mFileCount = 200;
	private String mFileExName = ".kxbmp";
	private boolean bNeedRecycle = false;
	private static ImageUtil instance;

	public static HashMap<String, SoftReference<Bitmap>> imageBitMap = new HashMap();

	private ImageUtil() {
	}

	public static synchronized ImageUtil getInstance() {
		if (instance == null) {
			instance = new ImageUtil();
			// Log.i("ImageUtil", "getInstance");
		}
		return instance;
	}

	private static ImageUtil friendLogo;

	public static synchronized ImageUtil getFriendLogo() {
		if (friendLogo == null) {
			friendLogo = new ImageUtil();
			// Log.i("ImageUtil", "getFriendLogo");
		}
		return friendLogo;
	}

	private static ImageUtil largePic;

	public static synchronized ImageUtil getLargePic() {
		if (largePic == null) {
			largePic = new ImageUtil();
			largePic.bNeedRecycle = true;
			// Log.i("ImageUtil", "largePic");
		}
		return largePic;
	}

	/** 设置缓存图片文件的最大数量 */
	public void setMaxFileCount(int count) {
		mFileCount = count;
	}

	/** 设置图片内存缓存的最大数量 */
	public void setMaxMemCount(int maxCnt, int delCnt) {
		mMaxMemCount = maxCnt;
		mDelCount = delCnt;
	}

	public void setExName(String strExName) {
		if (strExName != null) {
			mFileExName = strExName;
		}
	}

	/** 读取缓存图片 */
	public synchronized Bitmap createSafeImage(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		Bitmap img = imgcache.get(url);
		if (img == null) {// 内存缓存没有则读文件
			img = getCacheBmp(url);
		}
		imageBitMap.put(url, new SoftReference<Bitmap>(img));
		if (img != null && img.isRecycled()) {
			imageBitMap.remove(url);
			return null;
		}
		if (img != null) {
			int bmpWidth = img.getWidth();
			int bmpHeight = img.getHeight();
			// 计算出这次要缩小的比例
			float scaleWidth = 1;
			float scaleHeight = 1;
			scaleWidth = (float) (1);
			scaleHeight = (float) (1);
			// 产生resize后的Bitmap对象
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);

			return Bitmap.createBitmap(img, 0, 0, bmpWidth, bmpHeight, matrix,
					true);

		}
		return img;
	}

	/** 将图片保存在缓存中 */

	// public synchronized boolean putServerImage(String url, Bitmap image){
	// if(TextUtils.isEmpty(url) || image == null){
	// return false;
	// }
	// if(urlList.size() == mMaxMemCount){
	// 内存缓存图片数量达到最大值 removeOldCache();
	// }
	// try { imgcache.put(url, image); urlList.add(url); //保存缓存文件
	// saveCacheBmp(url, image); return true; } catch (OutOfMemoryError ex) {
	// Log.e("ImageUtil", "putServerImage", ex); } return false; }

	/** 清除掉一部分内存缓存图片 */
	private void removeOldCache() {
		for (int i = 0; i < mDelCount && urlList.size() > 0; i++) {
			String strUrlDel = urlList.remove(i);
			Bitmap bmp = imgcache.remove(strUrlDel);
			if (!bmp.isRecycled() && bNeedRecycle) {
				bmp.recycle();
				bmp = null;
				// Log.i("removeOldCache", "recycle");
			}
		}
	}

	/** 内存缓存中是否存在 */
	/*
	 * public synchronized boolean isMemCacheContains(String key){
	 * if(TextUtils.isEmpty(key)){ return false; } return
	 * imgcache.containsKey(key); }
	 */

	/** 检查指定url的图片是否在缓存中存在，如果内存中没有而文件系统中有则加入到内存中 */
	public synchronized boolean isInCache(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		if (imgcache.containsKey(url)) {
			return true;
		}
		if (isCacheFileExists(url)) {
			putBmpFiletoMem(url);
			return true;
		}
		return false;
	}

	/** 将指定url对应的位图缓存文件放入内存中 */
	public Bitmap putBmpFiletoMem(String url) {
		Bitmap img = getCacheBmp(url);
		if (img != null) {
			if (urlList.size() == mMaxMemCount) {// 内存缓存图片数量达到最大值
				removeOldCache();
			}
			imgcache.put(url, img);
			urlList.add(url);
		}
		return img;
	}

	/** 清空内存和文件缓存 */
	public void clear() {
		clearMemoryCahe();
		clearCache();
	}

	/** 清空内存缓存 */
	public void clearMemoryCahe() {
		if (this.bNeedRecycle) {
			Enumeration<Bitmap> enm = imgcache.elements();
			while (enm.hasMoreElements()) {
				Bitmap bmp = enm.nextElement();
				if (!bmp.isRecycled()) {
					// Log.i("clearMemoryCahe", "recycle");
					bmp.recycle();
					bmp = null;
				}
			}
			System.gc();
		}
		urlList.clear();
		imgcache.clear();
	}
	public void recycleBitmap() {
		Collection<SoftReference<Bitmap>> cll = ImageUtil.imageBitMap.values();
		for (SoftReference<Bitmap> sr : cll) {
			if (sr.get() != null) {
				System.out.println("回收了：" + sr.get().getRowBytes());
				Bitmap bit = sr.get();
				if (bit != null && !bit.isRecycled())
					bit = null;
			}
		}
		ImageUtil.imageBitMap.clear();
		System.gc();
	}
	/** 设置图片缓存路径 */
	private void setCachePath(String strPicCachePath) {
		if (TextUtils.isEmpty(strPicCachePath)) {
			return;
		}
		m_strPicCachePath = strPicCachePath;
		File cacheFile = new File(strPicCachePath);
		if (!cacheFile.exists()) {
			cacheFile.mkdir();
		}
	}

	/** 设置图片缓存路径 */
	public void setImageCache(Context context, String strSubDir) {
		String strPath = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdFile = ActivityUtils.getSDPath();
			strPath = sdFile.getAbsolutePath() + "/images/";
			File cacheFile = new File(strPath);
			if (!cacheFile.exists()) {
				cacheFile.mkdir();
			}
			strPath += strSubDir;
		} else {
			String strCacheDir = context.getCacheDir().getAbsolutePath();
			strPath = strCacheDir + "/pic/";
		}
		setCachePath(strPath);
	}

	/** 获得图片缓存路径 */
	public String getCachePath() {
		return m_strPicCachePath;
	}

	/** 从文件缓存中读取图片 */
	public Bitmap getCacheBmp(String strUrl) {
		if (TextUtils.isEmpty(m_strPicCachePath) || TextUtils.isEmpty(strUrl)) {
			return null;
		}
		Long start = new Date().getTime();
		String strBmpFile = m_strPicCachePath + StringUtil.MD5Encode(strUrl)
				+ mFileExName;// ".bmp";
		File bmpFile = new File(strBmpFile);
		if (!bmpFile.exists()) {
			return null;
		}
		Bitmap bmp = null;
		FileInputStream fis = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inDither = false; // Disable Dithering mode

			opt.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared

			opt.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future

			opt.inTempStorage = new byte[32 * 1024];

			long fileSize = bmpFile.length();
			int maxSize = 100 * 1024;
			if (fileSize <= maxSize) {
				opt.inSampleSize = 1;
			} else if (fileSize <= maxSize * 4) {
				opt.inSampleSize = 2;
			} else {
				long times = fileSize / maxSize;
				opt.inSampleSize = (int) (Math.log(times) / Math.log(2.0)) + 1;
			}
			// System.out.println(strUrl+"1====================="+(new
			// Date().getTime()-start));
			fis = new FileInputStream(bmpFile);
			bmp = BitmapFactory.decodeStream(fis, null, opt);// decodeFile(strBmpFile,
			// System.out.println(strUrl+"2====================="+(new
			// Date().getTime()-start));
		} catch (Exception ex) {
			ex.printStackTrace();
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
			clearMemoryCahe();
		} finally {
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return bmp;
	}

	/** 从文件缓存中读取图片 */
	public InputStream getCacheBmpStream(String strUrl) {
		if (TextUtils.isEmpty(m_strPicCachePath) || TextUtils.isEmpty(strUrl)) {
			return null;
		}
		Long start = new Date().getTime();
		String strBmpFile = m_strPicCachePath + StringUtil.MD5Encode(strUrl)
				+ mFileExName;// ".bmp";
		File bmpFile = new File(strBmpFile);
		if (!bmpFile.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			long fileSize = bmpFile.length();
			int maxSize = 100 * 1024;
			if (fileSize <= maxSize) {
				opt.inSampleSize = 1;
			} else if (fileSize <= maxSize * 4) {
				opt.inSampleSize = 2;
			} else {
				long times = fileSize / maxSize;
				opt.inSampleSize = (int) (Math.log(times) / Math.log(2.0)) + 1;
			}
			// System.out.println(strUrl+"1====================="+(new
			// Date().getTime()-start));
			fis = new FileInputStream(bmpFile);
			// System.out.println(strUrl+"2====================="+(new
			// Date().getTime()-start));
		} catch (Exception ex) {
			ex.printStackTrace();
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		}
		return fis;
	}
	/** 文件缓存中是否存在 */
	public boolean isCacheFileExists(String strUrl) {
		if (TextUtils.isEmpty(m_strPicCachePath) || TextUtils.isEmpty(strUrl)) {
			return false;
		}
		String strBmpFile = m_strPicCachePath + StringUtil.MD5Encode(strUrl)
				+ mFileExName;// ".bmp";
		File bmpFile = new File(strBmpFile);
		if (bmpFile.exists())
			return true;
		else
			return false;
	}

	/** 将图片保存在文件缓存 */
	/*
	 * public synchronized boolean saveCacheBmp(String strUrl, Bitmap bmp){
	 * if(TextUtils.isEmpty(m_strPicCachePath) || TextUtils.isEmpty(strUrl)){
	 * return false; } if(isCacheFileExists(strUrl)){//检查图片是否已存在 return true; }
	 * 
	 * //保存图片之前先判断是否有需要删除旧图 try{ File dataCache = new File(m_strPicCachePath);
	 * File[] files = dataCache.listFiles(); int len = files.length; if(len ==
	 * mFileCount){ FileUtil.delOldestFile(files); } }catch(Exception ex){
	 * ex.printStackTrace(); } String strBmpFile = m_strPicCachePath +
	 * StringUtil.MD5Encode(strUrl) + mFileExName;//".bmp"; File BmpFile = new
	 * File(strBmpFile); BufferedOutputStream bos = null; FileOutputStream fos =
	 * null; try { fos = new FileOutputStream(BmpFile); bos = new
	 * BufferedOutputStream(fos); // 采用压缩转档方法
	 * bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos); //
	 * 调用flush()方法，更新BufferStream bos.flush(); // 结束OutputStream return true; }
	 * catch (Exception ex) { Log.e("ImageUtil", "saveCacheBmp", ex); }
	 * catch(OutOfMemoryError ex){ ex.printStackTrace(); } finally { try{ if(bos
	 * != null){ bos.close(); bos = null; } if(fos != null){ fos.close(); fos =
	 * null; } } catch(Exception ex){ Log.e("ImageUtil", "saveCacheBmp finally",
	 * ex); } } return false; }
	 */

	/** 根据Url获得缓存文件的完整路径和名称 */
	public String getCacheBmpPath(String strUrl) {
		if (TextUtils.isEmpty(m_strPicCachePath) || TextUtils.isEmpty(strUrl)) {
			return "";
		}
		return m_strPicCachePath + StringUtil.MD5Encode(strUrl) + mFileExName;// ".bmp";
	}

	/** 删除图片缓存 */
	public synchronized boolean clearCache() {
		boolean ret = false;
		try {
			if (TextUtils.isEmpty(m_strPicCachePath)) {
				return false;
			}
			File file = new File(m_strPicCachePath);
			ret = FileUtil.deleteDirectory(file);
			file.mkdir();
		} catch (Exception ex) {
			Log.e("ImageUtil", "clearCache", ex);
		}
		return ret;
	}
	/**
	 * bitmap的缩放
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}
	/**
	 * 从sd卡里读取image
	 * 
	 * @return
	 */
	public static Bitmap getImageFromSD(String imagePath, int width, int height) {
		Bitmap bm = BitmapFactory.decodeFile(imagePath);
		return zoomBitmap(bm, width, height);
	}
	/**
	 * 判断文件是否存在
	 * 
	 * @param imagePath
	 * @return
	 */
	public static boolean isExitedImage(String imagePath) {
		File mfile = new File(imagePath);
		if (mfile.exists()) {
			return true;
		}
		return false;
	}
}
