package com.jiayue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiayue.BrowerActivity;
import com.jiayue.R;
import com.jiayue.view.ProgressWebview;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Browse extends Fragment {

	private View mRootView;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.brower,null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		initView();
    }

    public void initView() {
		title_layout = (LinearLayout) mRootView.findViewById(R.id.title_layout);
		close = (TextView) mRootView.findViewById(R.id.close);
		ll_header = (LinearLayout) mRootView.findViewById(R.id.ll_header);
		tv_header_title = (TextView) mRootView.findViewById(R.id.tv_header_title);
		tv_header_title.setText(name);

		if (model == MODEL_SHANGCHENG) {
			close.setVisibility(View.INVISIBLE);
			ll_header.setVisibility(View.INVISIBLE);
			tv_header_title.setText("商城");
		}
		int wv_brower = getResources().getIdentifier("wv_brower", "id", getActivity().getPackageName());
		mWebView = (ProgressWebview) mRootView.findViewById(wv_brower);
		WebSettings ws = mWebView.getSettings();

		ws.setJavaScriptEnabled(true);
		ws.setAllowFileAccess(true);
		ws.setAllowFileAccessFromFileURLs(true);
		ws.setAllowUniversalAccessFromFileURLs(true);	
		// 是否允许缩放
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(true);
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);

		mWebView.setWebViewClient(new WebViewClient() {
			// 这个函数我们可以做很多操作，比如我们读取到某些特殊的URL，于是就可以不打开地址，取消这个操作，进行预先定义的其他操作，这对一个程序是非常必要的。
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				Log.d(TAG, "url====" + url);
				// ActivityUtils.showToast(JDWebViewActivity.this,
				// webView.getUrl().equals(URL)+url);
				if (model == MODEL_SHANGCHENG && !url.equals("http://www.pndoo.com/link_list.html")) {
					Log.d(TAG, "111111111111111111111====" + url);
					Intent intent = new Intent(getActivity(), BrowerActivity.class);
					intent.putExtra("filePath", url);
					intent.putExtra("model", BrowerActivity.MODEL_BROWSER);
					startActivity(intent);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		
		mWebView.loadUrl(filepath);
	}

	@Override
	public void onResume() {
		super.onResume();
		mWebView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public boolean canGoBack(){
		return mWebView.canGoBack();
	}
	
	public void goBack(){
		mWebView.goBack();
	}
}
