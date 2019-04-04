package com.jiayue;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.jiayue.main.CrashHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.skytree.epub.BookInformation;
import com.skytree.epubtest.CustomFont;
import com.skytree.epubtest.SkyDatabase;
import com.skytree.epubtest.SkySetting;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.xutils.x;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

/**
 * 默认参数
 * 
 */
public class MyApplication extends MultiDexApplication {
	private static Context context;
	// 任务管理
	ArrayList<Activity> activitylist = new ArrayList<Activity>();
	int ADD_FLAG = -1;
	String notice = "";
	public static boolean isLogin = true;

	@Override
	public void onCreate() {

		super.onCreate();
		context = getApplicationContext();
		x.Ext.init(this);
        x.Ext.setDebug(true); //是否输出debug日志，开启debug会影响性能。
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		initImageLoader(getApplicationContext());
		/**
		 * 以下是skyTest的内容
		 */
		sd = new SkyDatabase(this);
		reloadBookInformations();

		loadSetting();

		JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this);
		// 开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
		Config.DEBUG = false;
		Config.isJumptoAppStore = true;
		UMShareAPI.get(this);
		
	}
	
	//各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin("wxb5c69e77124b6557", "d7cf4e1d3096f8cbfbfa7f91dcd7916c");
        PlatformConfig.setSinaWeibo("491304448", "f6192e4455dc39a492cd523ccdab8c60","http://www.pndoo.com");
        PlatformConfig.setQQZone("1105281436", "O5SPjXocx1QWTdsf");
    }



	public void addActivity(Activity activity) {
		activitylist.add(activity);
	}

	public void finishAllActivity() {
		while (activitylist.size() > 0) {
			activitylist.get(0).finish();
			activitylist.remove(0);
		}
	}

	public void removeActivity(Activity activity) {
		activitylist.remove(activity);
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().discCacheSize(50 * 1024 * 1024)
				.memoryCache(new WeakMemoryCache()).memoryCacheSize((int) (2 * 1024 * 1024)).memoryCacheSizePercentage(13).build();
		ImageLoader.getInstance().init(config);
	}

	public static Context getContext() {
		return context;
	}

	/**
	 * 以下是skyTest的内容
	 */
	public String message = "We are the world.";
	public ArrayList<BookInformation> bis;
	public ArrayList<CustomFont> customFonts = new ArrayList<CustomFont>();
	public SkySetting setting;
	public SkyDatabase sd = null;
	public int sortType = 0;

	public void reloadBookInformations() {
		this.bis = sd.fetchBookInformations(sortType, "");
	}

	public void reloadBookInformations(String key) {
		this.bis = sd.fetchBookInformations(sortType, key);
	}

	public void loadSetting() {
		this.setting = sd.fetchSetting();
	}

	public void saveSetting() {
		sd.updateSetting(this.setting);
	}

}
