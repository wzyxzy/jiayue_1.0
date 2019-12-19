package com.jiayue;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.Utils.DownloadManager.DownloadListener;
import com.jiayue.download2.db.DataBaseFiledParams;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.StringUtil;

public class SynManageActivity extends BaseActivity {
	DownloadManager manager;
	ListView syn_manage_lv;
	List<DocInfo> infos;
	BroadcastReceiver broadcastReciver;
	ScrollView sv_container;
	MyAdapter adapter;
	private int doing_click;
	private int stop_click;
	private TextView tv_header_title;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.syn_manage);
		manager = DownloadManager.getInstance(this);
		infos = manager.getListDoing();
		initView();
	}
	public void initView() {
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("同步管理");
		doing_click = getResources().getIdentifier("doing_click", "drawable",
				getPackageName());
		stop_click = getResources().getIdentifier("stop_click", "drawable",
				getPackageName());
		syn_manage_lv = (ListView) findViewById(R.id.syn_manage_lv);
		adapter = new MyAdapter(this);
		syn_manage_lv.setAdapter(adapter);
		sv_container = (ScrollView) findViewById(R.id.sv_container);
		broadcastReciver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals("android.intent.action.test")) {
					String flag = intent.getStringExtra("flag");
					Bundle bundle = intent.getBundleExtra("bundle");
					DocInfo info = (DocInfo) bundle.getSerializable("info");
					if (null == info) {
						return;
					}
					ProgressBar pb_syn = (ProgressBar) sv_container
							.findViewWithTag(info.getId());

					if (null == pb_syn) {
						return;
					}
					pb_syn.setProgress(info.getDownloadProgress());
					Log.i("SynManager", info.getDownloadProgress() + "");
					for (int i = 0; i < infos.size(); i++) {
						if (infos.get(i).getId() == info.getId()) {
							infos.remove(i);
							infos.add(i, info);
							adapter.notifyDataSetChanged();
							break;
						}
					}
					if (flag.equals("update")) {
						return;
					} else if (flag.equals("success")) {
						for (int i = 0; i < infos.size(); i++) {
							if (infos.get(i).getId() == info.getId()) {
								infos.remove(i);
								adapter.notifyDataSetChanged();
								return;
							}
						}
					} else if (flag.equals("failed")) {
						ActivityUtils.showToastForFail(SynManageActivity.this,
								info.getBookName() + ":下载失败");
						manager.cancel(info);
						infos.remove(info);
						adapter.notifyDataSetChanged();
						return;
					}

				}
			}
		};
		registerReceiver(broadcastReciver, new IntentFilter(
				"android.intent.action.test"));
	}

	public void btnBack(View v) {
		this.finish();
	}
	public void btnRightClick(View v) {
		startActivity(new Intent(this, SynManageActivity01.class));
		// this.finish();
	}
	
	@Override
	protected void onDestroy() {
		if (null != broadcastReciver) {
			this.unregisterReceiver(broadcastReciver);
		}

//		String android_id = Settings.System.getString(
//				getContentResolver(), Settings.Secure.ANDROID_ID);
		setResult(1000);
		super.onDestroy();
	}

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		View view;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				view = inflater.inflate(R.layout.syn_manage_list, parent,false);
			} else {
				view = convertView;
			}

			// RelativeLayout rl_syn = (RelativeLayout)
			// view.findViewWithTag("r1_syn");

			ProgressBar pb_syn = (ProgressBar) view.findViewById(R.id.pb_syn);

			TextView tv_syn_name = (TextView) view.findViewById(R.id.tv_syn_name);
			tv_syn_name.setText((position + 1)
					+ "、"
					+ StringUtil.subString(infos.get(position).getBookName(),
							8, "...")/*
									 * +"　　"+infos.get(position).getDownloadProgress
									 * ()+"%"
									 */);
			pb_syn.setTag(infos.get(position).getId());
			pb_syn.setMax(100);
			pb_syn.setProgress(infos.get(position).getDownloadProgress());

			TextView tv_syn_status = (TextView) view
					.findViewById(R.id.tv_syn_status);
			tv_syn_status.setText(getStringByStatus(infos.get(position)
					.getStatus()));

			ImageView iv_syn_cancel = (ImageView) view
					.findViewById(R.id.iv_syn_cancel);
			iv_syn_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					manager.cancel(infos.get(position));
					infos.remove(position);
					adapter.notifyDataSetChanged();
				}

			});
			final ImageView iv_syn_status = (ImageView) view
					.findViewById(R.id.iv_syn_status);
			if (infos.get(position).getStatus() == DataBaseFiledParams.LOADING) {
				iv_syn_status.setImageResource(doing_click);
			} else {
				iv_syn_status.setImageResource(stop_click);
			}
			iv_syn_status.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DocInfo info = infos.get(position);
					if (info.getStatus() == DataBaseFiledParams.LOADING) {
						manager.pause(infos.get(position));
						iv_syn_status.setImageResource(stop_click);
						info.setStatus(DataBaseFiledParams.PAUSING);
						// infos.remove(position);
						// infos.add(position, info);
					} else {
						manager.startForActivity(infos.get(position));
						iv_syn_status.setImageResource(doing_click);
						info.setStatus(DataBaseFiledParams.LOADING);
						manager.removeDownloadListener();
						manager.addDownloadListener(new DownloadListener() {
							@Override
							public void onUpdateProgress(DocInfo info) {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.test");// action与接收器相同
								Bundle bundle = new Bundle();
								bundle.putSerializable("info", info);
								intent.putExtra("bundle", bundle);
								intent.putExtra("flag", "update");
								sendBroadcast(intent);
								System.out.println("progress----------------"
										+ info.getDownloadProgress());
								System.out.println("speed----------------"
										+ info.getSpeed());
								System.out.println("name----------------"
										+ info.getName());
							}

							public void onDownloadFailed(DocInfo info) {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.test");// action与接收器相同
								Bundle bundle = new Bundle();
								bundle.putSerializable("info", info);
								intent.putExtra("bundle", bundle);
								intent.putExtra("flag", "failed");
								sendBroadcast(intent);
								System.out.println("progress----------------"
										+ info.getName());
						

							}

							@Override
							public void onDownloadCompleted(DocInfo info) {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.test");// action与接收器相同
								Bundle bundle = new Bundle();
								bundle.putSerializable("info", info);
								intent.putExtra("bundle", bundle);
								intent.putExtra("flag", "success");
								sendBroadcast(intent);
							
							

							}
						});
						/*
						 * infos.remove(position); infos.add(position, info);
						 */
					}
					// tv_syn_status.setText(getStringByStatus(infos.get(position).getStatus()));
				}

			});
			return view;
		}

		public String getStringByStatus(int status) {
			String flag = "";
			if (status == DataBaseFiledParams.DONE) {
				flag = "下载完毕";
			} else if (status == DataBaseFiledParams.FAILED) {
				flag = "服务器繁忙";
				
			} else if (status == DataBaseFiledParams.LOADING) {
				flag = "下载中";
			} else if (status == DataBaseFiledParams.PAUSING) {
				flag = "暂停";
			} else if (status == DataBaseFiledParams.WAITING) {
				flag = "等待下载";
			}
			return flag;
		}

	}

}
