package com.jiayue;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.util.ActivityUtils;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-10-16 下午2:10:41 项目名称：wyst
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：TeacherBindLogin.java 类说明：
 *        ------------------------------------------------------------------
 */
public class TeacherBindLoginActivity extends BaseActivity {
	private EditText et_username;
	private EditText et_password;
	LinearLayout ll_username;
	LinearLayout ll_password;
	private TextView tv_header_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_bind);
		initViews();
	}

	/**
	 * <p>
	 * Title
	 * </p>
	 * : initViews Description: 初始化view对象
	 */
	private void initViews() {
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		ll_username = (LinearLayout) findViewById(R.id.ll_username);
		ll_password = (LinearLayout) findViewById(R.id.ll_password);
		tv_header_title.setText("教师绑定");
	}

	/**
	 * <p>
	 * Title
	 * </p>
	 * : btnLogin Description:确认绑定按钮
	 * 
	 * @param v
	 */
	public void btnLogin(View v) {
		String username = et_username.getText().toString().trim();
		String password = et_password.getText().toString().trim();

		if (!TextUtils.isEmpty(username)
				&& username.matches("^[a-zA-Z0-9][a-zA-Z0-9_]{5,16}$")) {
			et_username.setText(username);

		} else if (username.matches("[\u4E00-\u9FA5a]+")) {
			ActivityUtils.showToast(this, "请不要输入中文!");
			return;
		} else if (TextUtils.isEmpty(username)) {
			ActivityUtils.showToastForFail(getApplication(), "请输入用户名");
			return;
		} else {
			ActivityUtils.showToastForFail(this, "账号请输入6-16位字母或字母+数字!");
			return;
		}
		if (!TextUtils.isEmpty(password) && password.length() >= 6
				|| password.length() <= 16) {
			et_password.setText(password);
		} else {
			ActivityUtils.showToastForFail(this, "您输入的密码不正确!");
			return;
		}
		if (password.matches("[\u4E00-\u9FA5a]+")) {
			ActivityUtils.showToast(this, "请不要输入中文!");
			return;
		}
		if (TextUtils.isEmpty(password)) {
			ActivityUtils.showToastForFail(getApplication(), "请输入密码！");
			return;
		}
	}

	/**
	 * <p>
	 * Title
	 * </p>
	 * : btnBack Description: 返回按钮
	 * 
	 * @param v
	 */
	public void btnBack(View v) {
		this.finish();
	}
}
