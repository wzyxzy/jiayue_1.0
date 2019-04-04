package com.jiayue;

import android.app.ActivityGroup;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
@SuppressWarnings("deprecation")
public class BaseActivity extends ActivityGroup {
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public MyApplication getApplicationContext() {
		return (MyApplication) super.getApplicationContext();
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
