package com.jiayue.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiayue.MediaPlayerActivity;
import com.jiayue.R;
import com.jiayue.util.SPUtility;
import com.jiayue.view.ProgressWebview;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Browse extends Fragment {

	private View mRootView;
	private String filepath = "http://www.pndoo.com/mbook_link_list.html";
	// private String filepath =
	// "http://www.hoopoe8.com/moudle_3d/fbxTest2/?model=3d/xsi_man_skinning.fbx.txt";
	// String filepath =
	// "file:///android_asset/3dFbxTest/index.html?model="+"file://"+ActivityUtils.getSDPath().getAbsolutePath()+"/3d/xsi_man_skinning.fbx.txt";
	// String filepath = "file:///android_asset/webplayer/grass/grass.html";
	// String filepath =
	// "file://"+ActivityUtils.getSDPath().getAbsolutePath()+"/3dFbxTest/index.htm?model=/3d/xsi_man_skinning.fbx.txt";
	private ProgressWebview mWebView;
	private GifImageView img_music;
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
		img_music = (GifImageView) mRootView.findViewById(R.id.img_music);
		tv_header_title.setText(name);

		mRootView.findViewById(R.id.music_img).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), MediaPlayerActivity.class);
				startActivity(intent);
			}
		});
		if (model == MODEL_SHANGCHENG) {
			close.setVisibility(View.INVISIBLE);
			ll_header.setVisibility(View.INVISIBLE);
			tv_header_title.setText("商城拼购");
		}
		int wv_brower = getResources().getIdentifier("wv_brower", "id", getActivity().getPackageName());
		mWebView = (ProgressWebview) mRootView.findViewById(wv_brower);
		WebSettings ws = mWebView.getSettings();
//		webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		ws.setDomStorageEnabled(true);
		ws.setJavaScriptEnabled(true);
		ws.setUseWideViewPort(true);
		ws.setLoadWithOverviewMode(true);
		ws.setAllowFileAccess(true);
		ws.setAllowFileAccessFromFileURLs(true);
		ws.setAllowUniversalAccessFromFileURLs(true);	
		// 是否允许缩放
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(true);
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ws.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
		}




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
//					Intent intent = new Intent(getActivity(), BrowerActivity.class);
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
	public void onResume() {
		super.onResume();
		mWebView.onResume();
		if (!TextUtils.isEmpty(SPUtility.getSPString(getContext(), "isPlay")) && SPUtility.getSPString(getContext(), "isPlay").equals("true")) {
			img_music.setImageResource(R.drawable.music_play2);
		} else {
			img_music.setImageResource(R.drawable.music_right);

		}
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
