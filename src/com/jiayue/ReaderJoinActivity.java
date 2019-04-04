package com.jiayue;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.MyTextWatcher;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-9-10 上午10:08:47 项目名称：wyst
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：ReaderJoinActivity.java 类说明：
 *        ------------------------------------------------------------------
 */
public class ReaderJoinActivity extends BaseActivity {
	private EditText tv_title;
	private EditText tv_content;
	private TextView tv_header_title;
	private Button btn_send_message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader_send_join);
		initViews();
	}

	private void initViews() {
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText(getIntent().getStringExtra("title"));
		tv_title = (EditText) findViewById(R.id.tv_title);
		tv_content = (EditText) findViewById(R.id.tv_content);
		btn_send_message = (Button) findViewById(R.id.btn_send_message);
		MyTextWatcher textWatcher = new MyTextWatcher() {

			@Override
			public void changeClick() {
				if (!TextUtils.isEmpty(tv_content.getText().toString().trim()) && !TextUtils.isEmpty(tv_title.getText().toString().trim())) {
					btn_send_message.setBackgroundColor(getResources().getColor(R.color.background));
				} else {
					btn_send_message.setBackgroundColor(getResources().getColor(R.color.login_line));
				}

			}

		};
		tv_content.addTextChangedListener(textWatcher);
		tv_title.addTextChangedListener(textWatcher);
	}

	private void loadData(String title, String content) {
		RequestParams params = new RequestParams(Preferences.AUTHOR_ANSWER_QUESTION);
		params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
		params.addQueryStringParameter("Id", getIntent().getStringExtra("id"));
		params.addQueryStringParameter("subCon.subTitle", title);
		params.addQueryStringParameter("subCon.subContent", content);

		DialogUtils.showMyDialog(ReaderJoinActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在发送中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<Bean>() {
				}.getType();
				Bean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.reader.join");
					sendBroadcast(intent);
					ActivityUtils.showToastForSuccess(ReaderJoinActivity.this, "感谢您的参与！");
					finish();
				} else {
					DialogUtils.showMyDialog(ReaderJoinActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "获取失败", bean.getCodeInfo(), null);
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(ReaderJoinActivity.this, "信息获取失败，请检查网络");
			}

			@Override
			public void onCancelled(CancelledException e) {

			}

			@Override
			public void onFinished() {

			}
		});
	}

	public void btn_send(View v) {
		String title = tv_title.getText().toString().trim();
		String content = tv_content.getText().toString().trim();
		if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
			loadData(title, content);
		} else if (TextUtils.isEmpty(title)) {
			ActivityUtils.showToastForFail(this, "请填写标题");
		} else if (TextUtils.isEmpty(content)) {
			ActivityUtils.showToastForFail(this, "请填写内容");
		}
	}

	public void btnBack(View v) {
		this.finish();
	}
}
