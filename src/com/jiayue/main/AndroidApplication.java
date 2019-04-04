package com.jiayue.main;

/**
 * Created by IntelliJ IDEA.
 * User: yanwt
 * Date: 11-5-25
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
import java.util.Stack;

import android.app.Activity;
import android.app.Application;

public class AndroidApplication extends Application {

	private Stack<Activity> activities; // 管理activity的栈
	public static String notification_url;
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * 添加activity
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		if (activities == null) {
			activities = new Stack<Activity>();
		}
		activities.add(activity);
	}
	/**
	 * 销毁指定的activity
	 * 
	 * @param activity
	 */
	public void finishActivity(Activity activity) {
		if (activities != null && !activities.isEmpty()) {
			activity.finish();
			activities.remove(activity);
		}
	}
	/**
	 * 清空指定activity
	 */
	public void finishAllActivity() {
		if (activities != null) {
			for (Activity activity : activities) {
				activity.finish();
			}
			activities.clear();
			activities = null;
		}

	}

	public void finishActivity2(Class c) {
		if (activities != null && !activities.isEmpty()) {
			for (Activity ac : activities) {
				if (ac.getClass() == c) {
					ac.finish();
					activities.remove(ac);
				}
			}

		}
	}
}
