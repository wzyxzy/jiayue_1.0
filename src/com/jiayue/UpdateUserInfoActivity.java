package com.jiayue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;

public class UpdateUserInfoActivity extends BaseActivity {
	EditText et_username;
	LinearLayout ll_username;
	ImageView iv_delete;

	EditText et_password;
	LinearLayout ll_password;

	EditText et_password01;
	LinearLayout ll_password01;
	ImageView iv_delete01;

	EditText et_password02;
	LinearLayout ll_password02;
	ImageView iv_delete02;

	EditText et_code;
	LinearLayout ll_code;

	EditText et_content;
	TextView tv_header_title;
	int upadteflag;

	private int inputbox_click;
	private int inputbox_normal;
	private ImageButton btn_header_right;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApplicationContext().addActivity(this);
		inputbox_click = getResources().getIdentifier("inputbox_click", "drawable",getPackageName());
		inputbox_normal = getResources().getIdentifier("inputbox_normal", "drawable",getPackageName());
		initPassWordView();
	}

	/**
	 * 初始化修改密码界面
	 */
	public void initPassWordView() {
		setContentView(R.layout.update_password);
		int btn_right = getResources().getIdentifier("btn_right", "drawable", getPackageName());
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("修改密码");
		btn_header_right = (ImageButton) findViewById(R.id.btn_header_right);
		btn_header_right.setVisibility(View.VISIBLE);
		btn_header_right.setBackgroundResource(btn_right);
		btn_header_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (et_password == null
						|| et_password.getText().toString().trim().equals("")) {
					ActivityUtils.showToastForFail(getApplication(), "请输入当前密码");
					return;
				}
				 if(et_password01 ==null ||
				 et_password01.getText().toString().trim().equals("")){
				 ActivityUtils.showToastForFail(getApplication(), "请输入新密码");
				 return;
				 }
				String password1 = et_password01.getText().toString().trim();
				if (!TextUtils.isEmpty(password1) && password1.length() >= 6
						|| password1.length() <= 16) {
					et_password01.setText(password1);

				} else if (password1.matches("[\u4E00-\u9FA5a]+")) {
					ActivityUtils.showToast(UpdateUserInfoActivity.this,
							"请不要输入中文!");
					return;
				} else if (TextUtils.isEmpty(password1)) {
					ActivityUtils.showToastForFail(getApplication(), "请输入新密码！");
					return;
				} else {
					ActivityUtils.showToastForFail(UpdateUserInfoActivity.this,
							"密码请输入6-16位!");
					return;
				}
			
				String password2 = et_password02.getText().toString().trim();
				if (!TextUtils.isEmpty(password1) && password1.length() >= 6
						|| password1.length() <= 16) {
					et_password01.setText(password1);

				} else if (password1.matches("[\u4E00-\u9FA5a]+")) {
					ActivityUtils.showToast(UpdateUserInfoActivity.this,
							"请不要输入中文!");
					return;
				} else if (TextUtils.isEmpty(password1)) {
					ActivityUtils
							.showToastForFail(getApplication(), "请输入确认密码！");
					return;
				} else {
					ActivityUtils.showToastForFail(UpdateUserInfoActivity.this,
							"密码请输入6-16位!");
					return;
				}
				if(!et_password.getText().toString().trim().equals(SPUtility.getSPString(UpdateUserInfoActivity.this, "password"))){
					ActivityUtils.showToastForFail(getApplication(), "当前密码输入错误！");
					return;
				}
				if (!password1.equals(password2)) {
					ActivityUtils.showToastForFail(getApplication(),
							"密码与确认密码不一致，请重新输入");
					return;
				}
				updatePassword();
				
			}
		});
		et_password = (EditText) findViewById(R.id.et_password);
		ll_password = (LinearLayout) findViewById(R.id.ll_password);
		iv_delete = (ImageView) findViewById(R.id.iv_delete);

		et_password.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				temp = s;
			}
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() <= 0) {
					iv_delete.setVisibility(View.GONE);
				} else {
					iv_delete.setVisibility(View.VISIBLE);
				}
			}
		});

		et_password01 = (EditText) findViewById(R.id.et_password01);
		ll_password01 = (LinearLayout) findViewById(R.id.ll_password01);
		iv_delete01 = (ImageView) findViewById(R.id.iv_delete01);

		et_password01.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				temp = s;
			}
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() <= 0) {
					iv_delete01.setVisibility(View.GONE);
				} else {
					iv_delete01.setVisibility(View.VISIBLE);
				}
			}
		});

		et_password02 = (EditText) findViewById(R.id.et_password02);
		ll_password02 = (LinearLayout) findViewById(R.id.ll_password02);
		iv_delete02 = (ImageView) findViewById(R.id.iv_delete02);

		et_password02.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				temp = s;
			}
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() <= 0) {
					iv_delete02.setVisibility(View.GONE);
				} else {
					iv_delete02.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	/**
	 * 初始化修改用户名页面
	 */
	public void initUserNameView() {
		setContentView(R.layout.update_user_info);
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("修改用户名");
		et_username = (EditText) findViewById(R.id.et_username);
		ll_username = (LinearLayout) findViewById(R.id.ll_username);
		iv_delete = (ImageView) findViewById(R.id.iv_delete);
		et_username.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					ll_username
							.setBackgroundResource(inputbox_click);
					if (!et_username.getText().toString().equals("")) {
						iv_delete.setVisibility(View.VISIBLE);
					}
				} else {
					ll_username
							.setBackgroundResource(inputbox_normal);
					iv_delete.setVisibility(View.GONE);
				}
			}
		});
		et_username.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				temp = s;
			}
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() <= 0) {
					iv_delete.setVisibility(View.GONE);
				} else {
					iv_delete.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	public void btnBack(View v) {
		this.finish();
	}

	/**
	 * 修改密码
	 */
	private void updatePassword() {
		
		RequestParams params = new RequestParams(Preferences.SENDEMAIL_URL);
		params.addQueryStringParameter("_method", "updatePassword");
		params.addQueryStringParameter("userId", UserUtil.getInstance(UpdateUserInfoActivity.this)
				.getUserId());
		params.addQueryStringParameter("userPwd", et_password01.getText().toString().trim());
		
		DialogUtils.showMyDialog(UpdateUserInfoActivity.this,MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在处理中...",null);
		
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Log.d("1", "1111111111========="+s);
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<Bean>() {
				}.getType();
				Bean bean = gson.fromJson(s, type);
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					DialogUtils.showMyDialog(
							UpdateUserInfoActivity.this,
							MyPreferences.SHOW_ERROR_DIALOG, "操作成功",
							"密码修改成功，需要重新登录！", new OnClickListener() {

								@Override
								public void onClick(View v) {
									DialogUtils.dismissMyDialog();
									Intent intent = new Intent(
											UpdateUserInfoActivity.this,
											LoginActivity.class);
									SPUtility.putSPString(UpdateUserInfoActivity.this, "username",
											"");
									SPUtility.putSPString(UpdateUserInfoActivity.this, "password",
											"");
									startActivity(intent);
									UpdateUserInfoActivity.this
											.finish();

								}
							});
				} else {
					DialogUtils.showMyDialog(
							UpdateUserInfoActivity.this,
							MyPreferences.SHOW_ERROR_DIALOG, "操作失败",
							bean.getCodeInfo(), null);
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
			}

			@Override
			public void onCancelled(CancelledException e) {
			}

			@Override
			public void onFinished() {
			}
		});
	}
	
	/**
	 * 清除用户名输入框
	 * 
	 * @param v
	 */
	public void deleteUsername(View v) {
		et_username.setText("");
		v.setVisibility(View.GONE);
	}
	/**
	 * 清除用户名输入框
	 * 
	 * @param v
	 */
	public void deletepassword(View v) {
		int id = v.getId();
		if (id == R.id.iv_delete) {
			et_password.setText("");
			v.setVisibility(View.GONE);
		} else if (id == R.id.iv_delete01) {
			et_password01.setText("");
			v.setVisibility(View.GONE);
		} else if (id == R.id.iv_delete02) {
			et_password02.setText("");
			v.setVisibility(View.GONE);
		} else {
		}

	}
	/**
	 * 清除用户名输入框
	 * 
	 * @param v
	 */
	public void deleteCode(View v) {
		et_code.setText("");
		v.setVisibility(View.GONE);
	}
	/**
	 * 验证输入的邮箱格式是否符合
	 * 
	 * @param email
	 * @return 是否合法
	 */
	public static boolean emailFormat(String email) {
		boolean tag = true;
		final String pattern1 = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}
	@Override
	protected void onDestroy() {
		getApplicationContext().removeActivity(this);
		super.onDestroy();
	}

}
