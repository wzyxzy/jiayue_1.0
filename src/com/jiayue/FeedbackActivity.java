package com.jiayue;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class FeedbackActivity extends BaseActivity {
	
	EditText title,content,phone;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_feedback);
		initView();
	}
	
	public void initView() {
		title = (EditText) findViewById(R.id.title);
		content = (EditText) findViewById(R.id.content);
		phone = (EditText) findViewById(R.id.phone);
		phone.setText(UserUtil.getInstance(this).getUserName());
	}

	public void btnBack(View v) {
		this.finish();
	}
	
	
	public void btnSubmit(View v) {
		String t = title.getText().toString();
		String c = content.getText().toString();
		String p = phone.getText().toString();
		if(TextUtils.isEmpty(t)){
			ActivityUtils.showToast(this, "请填写标题!");
			return;
		}
		
		if (!TextUtils.isEmpty(p) && p.matches("^[a-zA-Z0-9][a-zA-Z0-9_]{5,16}$")) {
			
		} else if (p.matches("[\u4E00-\u9FA5a]+")) {
			ActivityUtils.showToast(this, "请不要输入中文!");
			return;
		} else if (TextUtils.isEmpty(p)) {
			ActivityUtils.showToast(this,"请输入用户名");
			return;
		} else {
			ActivityUtils.showToast(this, "请正确输入手机号码!");
			return;
		}
		
		if(TextUtils.isEmpty(c)||c.length()>300){
			ActivityUtils.showToast(this, "建议栏文字长度不能为空或者大于300字!");
			return;
		}
		RequestParams params = new RequestParams(Preferences.FEEDBACK_URL);
		params.addBodyParameter("userTel", p);
		params.addBodyParameter("title", t);
		params.addBodyParameter("content", c);
		params.addBodyParameter("phoneType", android.os.Build.MODEL);
		params.addBodyParameter("phone_version", android.os.Build.VERSION.RELEASE);
		params.addBodyParameter("software_version", getClientVersion());
		
		Log.d("FEEDBACK", c.length()+"--------userTel=="+p+"---------"+t+"--------"+c+"------"+android.os.Build.MODEL+"---------"+android.os.Build.VERSION.RELEASE+"-----"+getClientVersion());
		DialogUtils.showMyDialog(FeedbackActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在提交...", null);
		x.http().post(params, new Callback.CommonCallback<String>(){
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<Bean>() {
				}.getType();
				Bean bean = gson.fromJson(s, type);
				if (bean.getCode().equals("SUCCESS")) {
					ActivityUtils.showToast(FeedbackActivity.this, "提交成功，谢谢您的建议。");
					finish();
				} else {
					ActivityUtils.showToast(FeedbackActivity.this, "提交失败");
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
				DialogUtils.dismissMyDialog();
			}
		});
	}
	
	private String getClientVersion() {
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "100";
        }
    }

}
