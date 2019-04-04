package com.jiayue;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.db.DataBaseFiledParams;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.SPUtility;
import com.jiayue.util.StringUtil;

public class SynManageActivity01 extends BaseActivity {
	DownloadManager manager;
	ListView syn_manage_lv;
	List<DocInfo> infos;
	BroadcastReceiver broadcastReciver;
	ScrollView sv_container;
	MyAdapter adapter;
	ImageButton btn_right;
	LinearLayout ll_done;
	private TextView tv_header_title;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.syn_manage);
		manager = DownloadManager.getInstance(this);
		infos = manager.getListDone();
		
		initView();
	}
	public void initView() {
		int btn_clear = getResources().getIdentifier("btn_clear", "drawable", getPackageName());
		syn_manage_lv = (ListView) findViewById(R.id.syn_manage_lv);
		adapter = new MyAdapter(this);
		syn_manage_lv.setAdapter(adapter);
		if (SPUtility.getSPString(this, "clear").equals("true")) {
			if (null == infos) {
				return;
			}
			for (DocInfo info : infos) {
				manager.cancel(info);
			}
			infos.clear();
			adapter.notifyDataSetChanged();
			SPUtility.putSPString(this, "clear", "false");	
		}
		sv_container = (ScrollView) findViewById(R.id.sv_container);
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("同步管理");
		btn_right = (ImageButton) findViewById(R.id.btn_header_right);
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setBackgroundResource(btn_clear);
		btn_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null == infos) {
					return;
				}
				for (DocInfo info : infos) {
					manager.cancel(info);
				}
				infos.clear();
				adapter.notifyDataSetChanged();
			}
		});
		ll_done = (LinearLayout) findViewById(R.id.ll_done);
		ll_done.setVisibility(View.GONE);
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
						ActivityUtils.showToast(SynManageActivity01.this,
								info.getName() + ":下载失败");
						return;
					}

				} 
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.test");
		filter.addAction("android.intent.action.cleardata");
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(broadcastReciver, filter);
	}
	
	public void btnBack(View v) {
		// startActivity(new Intent(this,SynManageActivity.class));
		this.finish();
	}
	
	@Override
	protected void onDestroy() {
		if (null != broadcastReciver) {
			this.unregisterReceiver(broadcastReciver);
		}
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
				view = inflater.inflate(R.layout.syn_manage_list01, parent,false);
			} else {
				view = convertView;
			}

			// RelativeLayout rl_syn = (RelativeLayout)
			// view.findViewWithTag("r1_syn");
			ProgressBar pb_syn = (ProgressBar) view.findViewById(R.id.pb_syn);
			pb_syn.setTag(infos.get(position).getId());
			pb_syn.setMax(100);
			pb_syn.setProgress(infos.get(position).getDownloadProgress());
			String flag = "";
			if (infos.get(position).getStatus() == DataBaseFiledParams.DONE) {
				flag = "下载完毕";
			} else if (infos.get(position).getStatus() == DataBaseFiledParams.FAILED) {
				flag = "下载失败";
			} else if (infos.get(position).getStatus() == DataBaseFiledParams.LOADING) {
				flag = "下载中";
			} else if (infos.get(position).getStatus() == DataBaseFiledParams.PAUSING) {
				flag = "暂停";
			} else if (infos.get(position).getStatus() == DataBaseFiledParams.WAITING) {
				flag = "等待下载";
			}
			TextView tv_syn_status = (TextView) view
					.findViewById(R.id.tv_syn_status);
			tv_syn_status.setText(flag);

			TextView tv_syn_name = (TextView) view
					.findViewById(R.id.tv_syn_name);
			tv_syn_name.setText((position + 1)+ "、"+ StringUtil.subString(infos.get(position).getBookName(),
							8, "..."));

			ImageView iv_syn_cancel = (ImageView) view
					.findViewById(R.id.iv_syn_cancel);
			iv_syn_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// manager.cancel(infos.get(position));
					// infos.remove(position);
					// Intent intent = new
					// Intent(SynManageActivity.this,TestService.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// Bundle bundle = new Bundle();
					// bundle.putSerializable("info", infos.get(position));
					// intent.putExtra("bundle", bundle);
					// startService(intent);
					manager.cancel(infos.get(position));
					infos.remove(position);
					adapter.notifyDataSetChanged();
				}

			});

			return view;
		}

	}

}
