package com.jiayue;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiayue.model.JSToAndroid;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.view.ProgressWebview;

import java.io.File;

public class WorkWebActivity extends BaseActivity {

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private final String TAG = getClass().getSimpleName();

    private String filepath = "";
    private ProgressWebview mWebView;
    int sum = 0;// 记录进去页面次数，为1时，退出
    private TextView tv_header_title, close;
    private LinearLayout title_layout, ll_header;
    private String title_name = "";
    private boolean isCheck = true;
    private String file_name = "";
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志
    String deletepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        setContentView(R.layout.activity_work_web);
        initView();
    }

    private void initView() {

        if (getIntent().getStringExtra("filePath") != null && (!getIntent().getStringExtra("filePath").equals(""))) {
            filepath = getIntent().getStringExtra("filePath");
        }
        if (getIntent().getStringExtra("name") != null && (!getIntent().getStringExtra("name").equals(""))) {
            title_name = getIntent().getStringExtra("name");
        }

        deletepath = new File(filepath).getParentFile().getAbsolutePath().replace("file:", "");
//        filepath = "file:///android_asset/question1/index.html";
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        tv_header_title.setText(title_name);

        int wv_brower = getResources().getIdentifier("wv_brower", "id", getPackageName());
        mWebView = (ProgressWebview) findViewById(wv_brower);
        WebSettings ws = mWebView.getSettings();

        ws.setJavaScriptEnabled(false);
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名

        mWebView.addJavascriptInterface(new JSToAndroid(this, filepath), "android");//AndroidtoJS类对象映射到js的test对象


        ws.setAllowFileAccess(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        // 是否允许缩放
        ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    url = request.getUrl().toString();
                } else {
                    url = request.toString();
                }
                if (url.startsWith("file://") || url.contains("jiayue"))
                    view.loadUrl(url);
                else
                    ActivityUtils.showToast(WorkWebActivity.this, "非加阅应用选项，不可操作！");
                return true;
//                return false;
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
        mWebView.onResume();
        super.onResume();

    }

    @Override
    protected void onPause() {
//        //如果当前web服务不是null
//        if (webChromeClient != null)
//            //通知app当前页面要隐藏它的自定义视图。
//            webChromeClient.onHideCustomView();
        //让webview重新加载，用于停掉音视频的声音
        mWebView.reload();
        //先重载webview再暂停webview，这时候才真正能够停掉音视频的声音，api 2.3.3 以上才能暂停
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mWebView.onPause(); // 暂停网页中正在播放的视频
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //webview停止加载
        mWebView.stopLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.deletesiftfile(deletepath);
            }
        }).start();
    }

    public void btnBack(View v) {
        showExitDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            showHomeDialog();
            return true;
        }
        return false;
    }

    private void showExitDialog() {
        Log.d(TAG, new File(filepath).getParentFile().getAbsolutePath());
        DialogUtils.showMyDialog(WorkWebActivity.this, MyPreferences.SHOW_BUY_DIALOG, "", "是否确认退出本次测试？", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DialogUtils.dismissMyDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ActivityUtils.deletesiftfile(deletepath);
                    }
                }).start();
                finish();
            }
        });
    }

    private void showHomeDialog() {
        DialogUtils.showMyDialog(WorkWebActivity.this, MyPreferences.SHOW_BUY_DIALOG, "", "返回系统主界面，可能导致本次测试退出，是否确认返回？", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DialogUtils.dismissMyDialog();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                startActivity(intent);
            }

        });
    }


//    /**
//     * 第 3 步: 申请权限结果返回处理
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
//            boolean isAllGranted = true;
//
//            // 判断是否所有的权限都已经授予了
//            for (int grant : grantResults) {
//                if (grant != PackageManager.PERMISSION_GRANTED) {
//                    isAllGranted = false;
//                    break;
//                }
//            }
//            Log.d(TAG, "isAllGranted222====" + isAllGranted);
//            if (isAllGranted) {
//                // 如果所有的权限都授予了, 则执行备份代码
//                jstoandroid.doBackup(file_name);
//
//            } else {
//                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
//                jstoandroid.openAppDetails();
//            }
//        }
//
//    }
}
