package com.jiayue.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.AccountActivity;
import com.jiayue.BookAllActivity;
import com.jiayue.FeedbackActivity;
import com.jiayue.GoodsListActivity;
import com.jiayue.LoginActivity;
import com.jiayue.R;
import com.jiayue.SynManageActivity;
import com.jiayue.TeacherBindLoginActivity;
import com.jiayue.TeacherQuizActivity;
import com.jiayue.UserInfoActivity;
import com.jiayue.VipActivity;
import com.jiayue.constants.Preferences;
import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.dto.base.Bean;
import com.jiayue.model.UserUtil;
import com.jiayue.rest.MainListener;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Setting extends Fragment implements OnClickListener{

	private View mRootView;

	Button btn_header_left;
	Button btn_header_right;
	// TextView tv_header_title;
	// TextView tv_cacheSize;
	// ImageView iv_update;
	private ToggleButton toggleButton;
	private TextView tv_username;
	private TextView tv_publishName;
	private DownloadManager manager;
	Map<String, String> params;
	private MainListener listener;

	
	public MainListener getListener() {
		return listener;
	}

	public void setListener(MainListener listener) {
		this.listener = listener;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.setting, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViews();
	}

	private void initViews() {

		tv_publishName = (TextView) mRootView.findViewById(R.id.tv_publishName);
		tv_publishName.setText("设置");
		toggleButton = (ToggleButton) mRootView.findViewById(R.id.online);
		toggleButton.setChecked(Boolean.parseBoolean(SPUtility.getSPString(getActivity(), "switchKey")));
		tv_username = (TextView) mRootView.findViewById(R.id.tv_username);
		tv_username.setText(UserUtil.getInstance(getActivity()).getUserName());
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SPUtility.putSPString(getActivity(), "switchKey", String.valueOf(isChecked));
			}
		});
		
//		((LinearLayout)mRootView.findViewById(R.id.teacherBind)).setOnClickListener(this);
//		((LinearLayout)mRootView.findViewById(R.id.examSystem)).setOnClickListener(this);
		((LinearLayout)mRootView.findViewById(R.id.onGoodsClick)).setOnClickListener(this);
		((LinearLayout)mRootView.findViewById(R.id.onVipClick)).setOnClickListener(this);
		((LinearLayout)mRootView.findViewById(R.id.onFeedbackClick)).setOnClickListener(this);
		((LinearLayout)mRootView.findViewById(R.id.onBookClick)).setOnClickListener(this);
		((Button)mRootView.findViewById(R.id.btnExitClick)).setOnClickListener(this);
		((LinearLayout)mRootView.findViewById(R.id.clearCache)).setOnClickListener(this);
		((LinearLayout)mRootView.findViewById(R.id.synManage)).setOnClickListener(this);
		((LinearLayout)mRootView.findViewById(R.id.userManage)).setOnClickListener(this);
		((LinearLayout)mRootView.findViewById(R.id.myAccount)).setOnClickListener(this);

		((TextView)mRootView.findViewById(R.id.tv_version)).setText("当前版本："+getClientVersion());
	}
	
	
	public void teacherBind() {
		Intent intent = new Intent(getActivity(), TeacherBindLoginActivity.class);
		startActivity(intent);
	}

	public void examSystem() {
		Intent intent = new Intent(getActivity(), TeacherQuizActivity.class);
		startActivity(intent);
	}

	public void onGoodsClick() {
		Intent intent = new Intent(getActivity(), GoodsListActivity.class);
		startActivity(intent);
	}

	public void onVipClick() {
		Intent intent = new Intent(getActivity(), VipActivity.class);
		startActivity(intent);
	}

	public void onFeedbackClick() {
		Intent intent = new Intent(getActivity(), FeedbackActivity.class);
		startActivity(intent);
	}

	public void onBookClick() {
		Intent intent = new Intent(getActivity(), BookAllActivity.class);
		startActivity(intent);
	}

	@SuppressWarnings("deprecation")
	public void btnExitClick() {
		DialogUtils.showMyDialog(getActivity(), MyPreferences.SHOW_BUTTON_DIALOG, "退出登录", "取        消", new OnClickListener() {
			@Override
			public void onClick(View v) {

				DialogUtils.dismissMyDialog();
				cancel();
				SPUtility.clear(getActivity());
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				listener.onFinish();
			}

		});
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = DialogUtils.dialog.getWindow().getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		lp.height = (int) (display.getHeight()); // 设置高度
		DialogUtils.dialog.getWindow().setAttributes(lp);
	}

	public void cancel() {
		String userId = UserUtil.getInstance(getActivity()).getUserId();
		RequestParams params = new RequestParams(Preferences.LOGOUT_URL);
		params.addQueryStringParameter("userId", userId);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<Bean>() {
				}.getType();
				Bean bean = gson.fromJson(s, type);

				if (bean != null && bean.getCode().equals("SUCCESS")) {

				} else {
					ActivityUtils.showToast(getActivity(), bean.getCodeInfo());
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				ActivityUtils.showToast(getActivity(), "退出失败,请检查网络。");
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
	 * 清除数据
	 * 
	 * @param
	 */
	public void clearCache() {
		DialogUtils.showMyDialog(getActivity(), MyPreferences.SHOW_CONFIRM_DIALOG, "清除数据", "<font color='red'>" + "清除后将无法恢复，确定要清除所有数据？" + "</font>", new OnClickListener() {
			@Override
			public void onClick(View v) {
				manager = DownloadManager.getInstance(getActivity());
				List<DocInfo> infos = manager.getListDone();
				for (int i = 0; i < infos.size(); i++) {
					manager.cancel(infos.get(i));
				}
				deleteFilesByDirectory(getActivity().getCacheDir());

				new Thread(new Runnable() {
					@Override
					public void run() {
						ActivityUtils.deletesiftfile(Environment.getExternalStorageDirectory().getPath() + "/jiayue");
						Looper.prepare();
						ActivityUtils.showToastForSuccess(getActivity(), "数据清除完毕");
						Looper.loop();
					}
				}).start();
				ImageLoader.getInstance().clearDiscCache();
				ImageLoader.getInstance().clearMemoryCache();
				DialogUtils.dismissMyDialog();
			}
		});
	}

	/** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				boolean delete = item.delete();
				Log.i("flag", item.getPath() + ":" + delete);
			}
		}
	}

	/**
	 * 同步管理
	 * 
	 * @param
	 */
	public void synManage() {
		startActivity(new Intent(getActivity(), SynManageActivity.class));
	}

	/**
	 *我的账户
	 */
	public void MyAccount() {
		startActivity(new Intent(getActivity(), AccountActivity.class));
	}
	/**
	 * 我的信息
	 * 
	 * @param
	 */
	public void userManage() {
		 startActivity(new Intent(getActivity(), UserInfoActivity.class));
	}

	/**
	 * 版本更新
	 * 
	 * @param
	 */
//	public void updateApp(View v) {
		// startActivity(new Intent(this, UpdateActivity.class));
//	}
	private String getClientVersion() {
		try {
			PackageManager packageManager = getActivity().getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(
					getActivity().getPackageName(), 0);
			return packInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return "100";
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.onGoodsClick:
				onGoodsClick();
				break;
			case R.id.onVipClick:
				onVipClick();
				break;
			case R.id.onFeedbackClick:
				onFeedbackClick();
				break;
			case R.id.onBookClick:
				onBookClick();
				break;
			case R.id.btnExitClick:
				btnExitClick();
				break;
			case R.id.clearCache:
				clearCache();
				break;
			case R.id.synManage:
				synManage();
				break;
			case R.id.userManage:
				userManage();
				break;
			case R.id.myAccount:
				MyAccount();
				break;
			default:
				break;
		}
	}
	
}
