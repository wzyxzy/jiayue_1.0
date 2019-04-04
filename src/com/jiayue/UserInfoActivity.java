package com.jiayue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jiayue.model.UserUtil;
import com.jiayue.util.MyPreferences;

public class UserInfoActivity extends BaseActivity {
	TextView tv_username;
	TextView tv_email;
	private TextView tv_header_title;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApplicationContext().addActivity(this);
		setContentView(R.layout.user_info);
		initView();
	}
	public void initView() {
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("我的信息");
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_username.setText(UserUtil.getInstance(getApplicationContext()).getUserName());

		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_email.setText(UserUtil.getInstance(getApplicationContext())
				.getEmail());
	}

	public void btnBack(View v) {
		this.finish();
	}
	public void updateInfo(View v) {
		Intent intent;
		int id = v.getId();
		if (id == R.id.ll_password) {
			intent = new Intent(this, UpdateUserInfoActivity.class);
			intent.putExtra("updateFlag", MyPreferences.UPDATE_PASSWORD);
			startActivityForResult(intent, MyPreferences.UPDATE_PASSWORD);
		} else {
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (null == data) {
			return;
		}
		switch (requestCode) {
			case MyPreferences.UPDATE_USERNAME :
				tv_username.setText(data.getStringExtra("username"));
				break;


			default :
				break;
		}
	}
	@Override
	protected void onDestroy() {
		getApplicationContext().removeActivity(this);
		super.onDestroy();
	}

}
