package com.jiayue;

import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.CommonAdapter;
import com.jiayue.adapter.ViewHolder;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.AuthorReplyBean;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.view.MyXListView;
import com.jiayue.view.MyXListView.IXListViewListener;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-9-9 上午11:44:18 项目名称：jiayue
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：ReaderDescriptionItemActivity.java 类说明：
 *        ------------------------------------------------------------------
 */
public class ReaderDescriptionItemActivity extends BaseActivity
		implements
			IXListViewListener {

	private MyXListView listview;
	private TextView tv_content;

	private TextView tv_time;

	private CommonAdapter<AuthorReplyBean.Data> adapter;
	private List<AuthorReplyBean.Data> readerJoins;
	private String id;
	private String addTime;
	private String content;
	private BroadcastReceiver broadcastReciver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader_desc_item);
		content = getIntent().getStringExtra("content");
		id = getIntent().getStringExtra("id");
		addTime = getIntent().getStringExtra("addTime");
		loadData();
		initViews();
	}

	private void initViews() {
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_content.setText(content);
		tv_time.setText(addTime);

		listview = (MyXListView) findViewById(R.id.listview);
		listview.setPullRefreshEnable(false);
		listview.setPullLoadEnable(false);
		// listview.setPullLoadEnable(!(list.size() <= 5));
		// listview.setXListViewListener(this);
		initBroadcast();
	}
	private void initBroadcast() {
		broadcastReciver = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals("android.intent.action.reader.join")) {
					loadData();
				}
			};
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.reader.join");
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(broadcastReciver, filter);

	}

	private ListAdapter initListViewAdapter() {
		adapter = new CommonAdapter<AuthorReplyBean.Data>(this, readerJoins,
				R.layout.communication_desc_item) {

			@Override
			public void convert(ViewHolder helper, AuthorReplyBean.Data item, int position) {
				if (position == readerJoins.size() - 1) {
					helper.setDisplay(R.id.btn_join);
				} else {
					helper.setHiddle(R.id.btn_join);
					helper.setDisplay(R.id.ll_item);
				}
				helper.setText(R.id.tv_return_content, item.getSubContent());
				helper.setText(R.id.tv_nick_name, item.getSurname());
				helper.setText(R.id.tv_add_time, item.getAddTime());

			}
		};
		return adapter;
	}
	private void loadData() {
		
		RequestParams params = new RequestParams(Preferences.AUTHOR_POST_LIST);
		params.addQueryStringParameter("Id", id);
		DialogUtils.showMyDialog(ReaderDescriptionItemActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在发送中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<AuthorReplyBean>() {
				}.getType();
				AuthorReplyBean bean = gson.fromJson(s, type);
				
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					readerJoins = bean.getData();
					findViewById(R.id.btn_join).setVisibility(
							readerJoins.toString().equalsIgnoreCase(
									"[]") ? View.VISIBLE : View.GONE);
					listview.setAdapter(initListViewAdapter());
					Log.i("msg", readerJoins.toString());
					Intent intent = new Intent();
					intent.setAction("android.intent.action.reader.desc");
					sendBroadcast(intent);
				} else {
					DialogUtils.showMyDialog(ReaderDescriptionItemActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "获取失败", bean.getCodeInfo(), null);
				}
				
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(ReaderDescriptionItemActivity.this, "信息获取失败，请检查网络");
			}

			@Override
			public void onCancelled(CancelledException e) {

			}

			@Override
			public void onFinished() {

			}
		});
	}
	public void btnBack(View v) {
		this.finish();
	}

	@Override
	public void onRefresh() {

	}
	public void btn_join(View v) {
		Intent intent = new Intent(ReaderDescriptionItemActivity.this,
				ReaderJoinActivity.class);
		intent.putExtra("title", "参与");
		intent.putExtra("id", id);
		startActivity(intent);

	}
	@Override
	public void onLoadMore() {
		// if (list.size() <= 5 * position) {
		// counts = list.size();
		// listview.setPullLoadEnable(false);
		// adapter.notifyDataSetChanged();
		// listview.stopLoadMore();
		// ActivityUtils.showToastForFail(this, "没有更多数据了");
		// } else if (list.size() > 5 * position && list.size() > counts) {
		// counts = 5 * position + 5;
		// adapter.notifyDataSetChanged();
		// listview.stopLoadMore();
		// }
		// position++;
	}
	@Override
	protected void onDestroy() {
		if (broadcastReciver != null) {
			unregisterReceiver(broadcastReciver);
		}
		super.onDestroy();
	}
}
