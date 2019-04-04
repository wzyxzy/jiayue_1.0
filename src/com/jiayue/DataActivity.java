package com.jiayue;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class DataActivity extends Activity {
	private final String TAG = getClass().getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		startActivity(new Intent(this, SplashActivity.class));
		
		Uri uri = getIntent().getData();
		if (uri != null) {
		    // 完整的url信息
		    String url = uri.toString();
		    Log.e(TAG, "url: " + uri);
		    // scheme部分
		    String scheme = uri.getScheme();
		    Log.e(TAG, "scheme: " + scheme);
		    // host部分
		    String host = uri.getHost();
		    Log.e(TAG, "host: " + host);
		    //port部分
		    int port = uri.getPort();
		    Log.e(TAG, "host: " + port);
		    // 访问路劲
		    String path = uri.getPath();
		    Log.e(TAG, "path: " + path);
		    List<String> pathSegments = uri.getPathSegments();
		    // Query部分
		    String query = uri.getQuery();
		    Log.e(TAG, "query: " + query);
		    //获取指定参数值
		    String goodsId = uri.getQueryParameter("goodsId");
		    Log.e(TAG, "goodsId: " + goodsId);
		}
		finish();
	}
}
