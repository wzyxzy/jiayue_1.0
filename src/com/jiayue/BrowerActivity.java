package com.jiayue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiayue.view.ProgressWebview;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class BrowerActivity extends BaseActivity {

	private String filepath = "http://www.pndoo.com/link_list.html";
	// private String filepath =
	// "http://www.hoopoe8.com/moudle_3d/fbxTest2/?model=3d/xsi_man_skinning.fbx.txt";
	// String filepath =
	// "file:///android_asset/3dFbxTest/index.html?model="+"file://"+ActivityUtils.getSDPath().getAbsolutePath()+"/3d/xsi_man_skinning.fbx.txt";
	// String filepath = "file:///android_asset/webplayer/grass/grass.html";
	// String filepath =
	// "file://"+ActivityUtils.getSDPath().getAbsolutePath()+"/3dFbxTest/index.htm?model=/3d/xsi_man_skinning.fbx.txt";
	private ProgressWebview mWebView;
	int sum = 0;// 记录进去页面次数，为1时，退出
	private final String TAG = getClass().getSimpleName();
	public static final int MODEL_SHANGCHENG = 0x01;
	public static final int MODEL_BROWSER = 0x02;
	public static final int MODEL_3D = 0x03;
	private int model = MODEL_SHANGCHENG;
	private TextView tv_header_title, close;
	private LinearLayout title_layout, ll_header;
	private String name = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.brower);
		if (getIntent().getStringExtra("filePath") != null && (!getIntent().getStringExtra("filePath").equals(""))) {
			filepath = getIntent().getStringExtra("filePath");
		}
		if (getIntent().getStringExtra("name") != null && (!getIntent().getStringExtra("name").equals(""))) {
			name = getIntent().getStringExtra("name");
		}
		model = getIntent().getIntExtra("model", MODEL_SHANGCHENG);
		Log.d(TAG, filepath + "------model=" + (model == MODEL_BROWSER)+"----filepath=="+filepath);

		// ActivityUtils.showToast(this, filepath);
		initView();
		// File file = new
		// File(ActivityUtils.getSDPath().getAbsolutePath()+"/3d/xsi_man_skinning.fbx.txt");
		// if(file.exists())
		// Toast.makeText(this, "eeeeeeeeeeeeee", 1).show();
	}

	public void initView() {
		title_layout = (LinearLayout) findViewById(R.id.title_layout);
		close = (TextView) findViewById(R.id.close);
		ll_header = (LinearLayout) findViewById(R.id.ll_header);
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText(name);

		if (model == MODEL_SHANGCHENG) {
			close.setVisibility(View.INVISIBLE);
			ll_header.setVisibility(View.INVISIBLE);
			tv_header_title.setText("商城");
		}
		int wv_brower = getResources().getIdentifier("wv_brower", "id", getPackageName());
		mWebView = (ProgressWebview) findViewById(wv_brower);
		WebSettings ws = mWebView.getSettings();

		ws.setJavaScriptEnabled(false);
		ws.setAllowFileAccess(true);
		ws.setAllowFileAccessFromFileURLs(true);
		ws.setAllowUniversalAccessFromFileURLs(true);	
		// 是否允许缩放
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(true);

		mWebView.setWebViewClient(new WebViewClient() {
			// 这个函数我们可以做很多操作，比如我们读取到某些特殊的URL，于是就可以不打开地址，取消这个操作，进行预先定义的其他操作，这对一个程序是非常必要的。
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//				Log.d(TAG, "url====" + url);
//				// ActivityUtils.showToast(JDWebViewActivity.this,
//				// webView.getUrl().equals(URL)+url);
//				if (model == MODEL_SHANGCHENG && !url.equals("http://www.pndoo.com/link_list.html")) {
//					Log.d(TAG, "111111111111111111111====" + url);
//					Intent intent = new Intent(BrowerActivity.this, BrowerActivity.class);
//					intent.putExtra("filePath", url);
//					intent.putExtra("model", BrowerActivity.MODEL_BROWSER);
//					startActivity(intent);
//					return true;
//				}
				return false;
			}
		});
		
		mWebView.loadUrl(filepath);
	}

	@Override
	public void onAttachedToWindow() {
		/*此处我们呼应下面代码中禁用JavaScript的支持的部分代码
		 * 原因也已经解释的非常详细了
		 * 但是此处需要注意，就是先reload再次启用JavaScript这个顺序不要乱掉，否则
		 * 可能还没有调用reload之前，前一个页面已经执行了JavaScript导致页面上面的埋点两次执行。
		 *
		 * 关于性能的隐忧，由于我们重新reload了页面，地址链接并没有改变，因此并不会去服务器上面重新获取页面
		 * 此处的性能隐忧，应该是不存在的
		 *
		 * 至于是不是需要手工设置一下Chrome内核的缓存时间，这个在目前的实际实验观察看来，是不需要的。
		 *
		 * */
		mWebView.reload();
		mWebView.getSettings().setJavaScriptEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void btnBack(View v) {
		if (mWebView.canGoBack()) {
			mWebView.goBack();// 返回上一页面
			// Log.d(TAG, mWebView.getUrl());
		} else {
			// System.exit(0);//退出程序
			finish();
		}
	}

	public void BtnFinish(View view) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();// 返回上一页面
				// Log.d(TAG, mWebView.getUrl());
				return true;
			} else {
				// System.exit(0);//退出程序
				finish();
				return true;
			}
		}
		return false;
	}
}
