package com.jiayue;

import com.jiayue.R;
import com.jiayue.util.MyPreferences;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestActivity extends BaseActivity {
//	TextView tv_header_title;
//	LinearLayout ll_test;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		getApplicationContext().addActivity(this);
//		setContentView(R.layout.test_info);
//		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
//		ll_test = (LinearLayout) findViewById(R.id.ll_test);
//		Integer testId = getIntent().getIntExtra("testId", 0);
//		switch (testId) {
//			case MyPreferences.GOUTONG_CHAT :
//				tv_header_title.setText("我的论坛");
//				// ll_test.setBackgroundResource(R.drawable.goutong_luntan);
//				break;
//			case MyPreferences.GOUTONG_QUAN :
//				tv_header_title.setText("朋友圈");
//				// ll_test.setBackgroundResource(R.drawable.goutong_quanzi);
//				break;
//			case MyPreferences.GOUTONG_QUESTION :
//				tv_header_title.setText("提问");
//				// ll_test.setBackgroundResource(R.drawable.goutong_wenda);
//				break;
//			case MyPreferences.GOUTONG_OTHER :
//				tv_header_title.setText("其他");
//				// ll_te/tBackgroundResource(R.drawable.goutong_qita);
//				break;
//
//			default :
//				break;
//		}
//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pay);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		getApplicationContext().removeActivity(this);
		super.onDestroy();
	}

	public void btnBack(View v) {
		this.finish();
	}
}
