package com.jiayue;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.constants.Preferences;
import com.jiayue.download.DownloadProgressListener;
import com.jiayue.download.FileDownloader;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.LoginBean;
import com.jiayue.dto.base.SmsBean;
import com.jiayue.dto.base.UpdateBean;
import com.jiayue.dto.base.UpdateBean.Data;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;
import com.jiayue.util.UpdateInfo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import cn.jiguang.net.HttpUtils;

import static com.jiayue.constants.Preferences.phoneMatcher;

public class LoginActivity extends BaseActivity {

    UpdateInfo info;
    LinearLayout ll_username;
    LinearLayout ll_password;
    EditText et_username;
    EditText et_password;
    String client_version;
    private ProgressBar pb_syn;
    private String file;
    private int inputbox_click;
    private int inputbox_normal;
    private TextView publishName;
    private final String TAG = getClass().getSimpleName();
    // public final static String IS_REGISTER = "IS_REGISTER";
    // private boolean mIs_Register = false;


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(LoginActivity.this, CastUpdateActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    UpdateBean.Data update = (Data) msg.obj;
                    if (update != null) {
                        Log.i("update", "update=" + update.toString() + "LoginActivity");
                        if ("2".equals(update.getIsUpdate())) {
                            if (ActivityUtils.isWifiEnabled(LoginActivity.this)) {
                                intent = new Intent(LoginActivity.this, CastUpdateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("update", update);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else {
                                ActivityUtils.showToastForFail(LoginActivity.this, "请连接WIFI后进行更新~");
                            }
                        } else if ("1".equals(update.getIsUpdate())) {
                            if (ActivityUtils.isWifiEnabled(LoginActivity.this)) {
                                intent = new Intent(LoginActivity.this, CastUpdateActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("update", update);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            } else {
                                ActivityUtils.showToastForFail(LoginActivity.this, "请连接WIFI后进行更新~");
                            }
                        } else if ("0".equals(update.getIsUpdate())) {
                            doLogin();
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private String imei;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().addActivity(this);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.login_land);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.login);
        }
        DialogUtils.dismissMyDialog();
        initView();


        pb_syn = (ProgressBar) findViewById(R.id.pb_syn);
        client_version = getClientVersion();
        checkUpdateVerson();

    }

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private String[] myPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO
    };

    private void permission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
                myPermissions);
        Log.d(TAG, "isAllGranted==" + isAllGranted);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            doBackup();
            return;
        }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(
                this,
                myPermissions,
                MY_PERMISSION_REQUEST_CODE
        );
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            Log.d(TAG, "isAllGranted222====" + isAllGranted);
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                doBackup();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 第 4 步: 后续操作
     */
    private void doBackup() {
        // 本文主旨是讲解如果动态申请权限, 具体备份代码不再展示, 就假装备份一下
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            imei = tm.getDeviceId();
        } catch (Exception e) {
            // TODO: handle exception
        }
        String userName = SPUtility.getSPString(this, "username");
        String userPwd = SPUtility.getSPString(this, "password");
        Log.d(TAG, "login---------userName=" + userName + "--------password=" + userPwd);
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPwd) && ActivityUtils.isNetworkAvailable(this)) {
            login(userName, userPwd);
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("加阅需要访问 “位置” 、 “外部存储器”和“读取本机识别码”权限，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void doLogin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //当前系统大于等于6.0
            permission();
        } else {
            //当前系统小于6.0，直接调用拍照
            doBackup();
        }
    }


    // 完成文件的下载
    private void download(File saveDir, String path) {
        new Thread(new DownloadFileTask(saveDir, path)).start();
    }

    private final class DownloadFileTask implements Runnable {
        private File saveDir;
        private String path;

        public DownloadFileTask(File saveDir, String path) {
            this.saveDir = saveDir;
            this.path = path;
        }

        public void run() {
            try {
                FileDownloader loader = new FileDownloader(getApplicationContext(), path, saveDir, 1, "jiayue.apk");
                pb_syn.setMax(loader.getFileSize());// 设置进度条的最大刻度为文件长度
                loader.download(new DownloadProgressListener() {
                    public void onDownloadSize(int size) {
                        Message msg = new Message();
                        msg.what = 3;
                        msg.getData().putInt("size", size);
                        handler.sendMessage(msg);
                    }
                });
            } catch (Exception e) {
                handler.sendMessage(handler.obtainMessage(-1));
            }
        }

    }


    /**
     * 忘记密码按钮
     */

    public void btnForgot(View v) {
        Intent intent = new Intent(LoginActivity.this, UserResetActivity.class);
        startActivity(intent);

//        Intent intent = new Intent(LoginActivity.this, WorkWebActivity.class);
//        startActivity(intent);

    }

    private void checkUpdateVerson() {
        getVerisonUpdate();

    }

    private void getVerisonUpdate() {
        if (!LoginActivity.this.isFinishing())
            DialogUtils.showMyDialog(LoginActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "检测中...", null);
        RequestParams params = new RequestParams(Preferences.VERISON_UPDATE_URL);
        params.addQueryStringParameter("version", getClientVersion());
        params.addQueryStringParameter("systemType", "android");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "getVerisonUpdate=========" + s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<UpdateBean>() {
                }.getType();
                UpdateBean bean = gson.fromJson(s, type);
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    handler.sendMessage(handler.obtainMessage(2, bean.getData()));
                } else {
                    doLogin();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d(TAG, "getVerisonUpdate=====throwable====" + throwable.getMessage());
                doLogin();
            }

            @Override
            public void onCancelled(CancelledException e) {
                doLogin();
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
            Log.d(TAG, "packInfo.versionName======" + packInfo.versionName);
            return packInfo.versionName;
        } catch (NameNotFoundException e) {
            return "100";
        }
    }

    public void initView() {
        inputbox_click = getResources().getIdentifier("inputbox_click", "drawable", getPackageName());
        inputbox_normal = getResources().getIdentifier("inputbox_normal", "drawable", getPackageName());
        publishName = (TextView) findViewById(R.id.tv_publishName);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        ll_username = (LinearLayout) findViewById(R.id.ll_username);
        ll_password = (LinearLayout) findViewById(R.id.ll_password);
        publishName.setText("用户登录");
        String username = SPUtility.getSPString(this, "username");

        // mIs_Register = getIntent().getBooleanExtra(IS_REGISTER, false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * 注册
     */
    public void btnRegist(View v) {
        if (!ActivityUtils.isNetworkAvailable(getBaseContext())) {
            ActivityUtils.showToastForFail(getBaseContext(), "对不起,不能进行离线注册");
        } else {
            Intent intent = new Intent(this, RegistActivity.class);
            startActivity(intent);
        }
    }


    /**
     * 登录按钮
     */
    public void btnLogin(View v) {
        String username = et_username.getText().toString().trim();
        String password1 = SPUtility.getSPString(LoginActivity.this, username);
        if (!TextUtils.isEmpty(password1) && !ActivityUtils.isNetworkAvailable(this)) {
            et_password.setText(password1);
        }
        if (!TextUtils.isEmpty(username) && username.matches("^[a-zA-Z0-9][a-zA-Z0-9_]{5,16}$")) {
            et_username.setText(username);

        } else if (username.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(LoginActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(username)) {
            ActivityUtils.showToastForFail(getApplication(), "请输入用户名");
            return;
        } else {
            ActivityUtils.showToastForFail(LoginActivity.this, "账号请输入6-16位字母或字母+数字!");
            return;
        }
        String password = et_password.getText().toString().trim();

        if (!TextUtils.isEmpty(password) && password.length() >= 6 || password.length() <= 16) {
            et_password.setText(password);
        } else if (password.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(LoginActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(password)) {
            ActivityUtils.showToastForFail(getApplication(), "请输入密码！");
            return;
        } else {
            ActivityUtils.showToastForFail(LoginActivity.this, "您输入的密码不正确!");
            return;
        }

        login(username, password);
    }

    private void login(String username, final String password) {
        String name = SPUtility.getSPString(getBaseContext(), "username");
        String pwd = SPUtility.getSPString(getBaseContext(), "password");
        if (!ActivityUtils.isNetworkAvailable(this)) {
            if (name.equalsIgnoreCase(username) && pwd.equalsIgnoreCase(password)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("update_flag", true);
                startActivity(intent);
                LoginActivity.this.finish();
            } else if (TextUtils.isEmpty(name) && TextUtils.isEmpty(pwd)) {
                DialogUtils.showMyDialog(this, MyPreferences.SHOW_CONFIRM_DIALOG, "网络设置提示", "网络连接不可用,是否进行设置?", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                });
            } else {
                ActivityUtils.showToastForFail(getBaseContext(), "账号密码不符");
            }
            return;
        }

        RequestParams params = new RequestParams(Preferences.LOGIN_URL);
        params.addQueryStringParameter("userName", username);
        params.addQueryStringParameter("userPwd", password);
        params.addQueryStringParameter("phoneId", imei);

        if (!LoginActivity.this.isFinishing())
            DialogUtils.showMyDialog(LoginActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在登录中...", null);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<LoginBean>() {
                }.getType();
                LoginBean bean = gson.fromJson(s, type);
                DialogUtils.dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    LoginBean.Data data = bean.getData();
                    if (data == null) {
                        return;
                    }
                    data.setPassword(password);
                    setSpUserVo(data);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("update_flag", true);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else if (bean != null && bean.getCode().equals("FAIL")) {
                    if (!TextUtils.isEmpty(bean.getCodeInfo()) && !LoginActivity.this.isFinishing()) {// 获取数据出现异常
                        if (bean.getCodeInfo().contains("解绑")) {
                            DialogUtils.showMyDialog(LoginActivity.this, MyPreferences.SHOW_JIESUO_DIALOG, "", "", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    DialogUtils.dismissMyDialog();
                                    showYanzhengDialog();
                                }

                            });
                        } else {
                            DialogUtils.showMyDialog(LoginActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "登录失败", bean.getCodeInfo(), null);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    Button btn_send;
    String verifCode;

    private void showYanzhengDialog() {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.my_dialog);
        dialog.setContentView(R.layout.dialog_login_jiesuorenzheng);
        final TextView textView = (TextView) dialog.findViewById(R.id.dialog_titile);
        if (!TextUtils.isEmpty(et_username.getText().toString()))
            textView.setText(et_username.getText().toString().trim());
        else
            textView.setText(SPUtility.getSPString(this, "username"));
        final EditText editText = (EditText) dialog.findViewById(R.id.editText1);
        btn_send = (Button) dialog.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                btn_send(textView.getText().toString().trim());
            }
        });
        Button button07 = (Button) dialog.findViewById(R.id.btn_right);
        ImageButton button = (ImageButton) dialog.findViewById(R.id.imageButton1);
        button07.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (editText.getText().toString().trim().equals(verifCode)) {
                    RequestParams params = new RequestParams(Preferences.JIESUO);
                    params.addQueryStringParameter("userName", et_username.getText().toString().trim());

                    HttpUtils http = new HttpUtils();
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.d(TAG, s);
                            Gson gson = new Gson();
                            java.lang.reflect.Type type = new TypeToken<Bean>() {
                            }.getType();
                            Bean bean = gson.fromJson(s, type);

                            if (bean.getCode().equals("SUCCESS")) {
                                ActivityUtils.showToast(LoginActivity.this, "已成功解绑，请登录");
                                dialog.dismiss();
                            } else {
                                ActivityUtils.showToastForFail(LoginActivity.this, "解绑失败");
                            }
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            ActivityUtils.showToastForFail(LoginActivity.this, "解绑失败");
                        }

                        @Override
                        public void onCancelled(CancelledException e) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                } else {
                    ActivityUtils.showToast(LoginActivity.this, "验证码错误");
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public void btn_send(String phone) {
        if (TextUtils.isEmpty(phone) && !phone.matches(phoneMatcher)) {
            return;
        }
        if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {

            RequestParams params = new RequestParams(Preferences.SEND_SMS);
            params.addQueryStringParameter("phone", phone);

            ActivityUtils.showToast(LoginActivity.this, "正在发送中...");
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, s);
                    Gson gson = new Gson();
                    java.lang.reflect.Type type = new TypeToken<SmsBean>() {
                    }.getType();
                    SmsBean bean = gson.fromJson(s, type);

                    if (bean != null && bean.getCode().equals("SUCCESS")) {
                        verifCode = bean.getData().getCheckCode();
                    } else {
                        ActivityUtils.showToast(LoginActivity.this, "验证码获取失败");
                    }
                    time.start();
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ActivityUtils.showToast(LoginActivity.this, "验证码获取失败,请检查网络。");
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        }

    }

    TimeCount time = new TimeCount(60000, 1000);

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            btn_send.setText("重新验证");
            btn_send.setTextColor(getResources().getColor(R.color.background));
            btn_send.setClickable(true);
            int regist_sms_button1 = getResources().getIdentifier("regist_sms_button1", "drawable", getPackageName());
            btn_send.setBackgroundResource(regist_sms_button1);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            int regist_sms_button2 = getResources().getIdentifier("regist_sms_button2", "drawable", getPackageName());
            btn_send.setBackgroundResource(regist_sms_button2);
            btn_send.setClickable(false);
            btn_send.setTextColor(getResources().getColor(R.color.login_hint_color));
            btn_send.setText("(" + millisUntilFinished / 1000 + "秒)");
        }

    }

    public void deleteUsername(View v) {
        et_username.setText("");
        v.setVisibility(View.GONE);
    }

    /**
     * 将User写入xml
     */
    private void setSpUserVo(LoginBean.Data user) {
        SPUtility.putSPBoolean(this, R.string.islogin, true);
        SPUtility.putSPString(this, "userid", user.getUserId());
        SPUtility.putSPString(this, "username", user.getUserName());
        SPUtility.putSPString(this, "password", user.getPassword());
        SPUtility.putSPString(this, "email", user.getUserEmail());
        SPUtility.putSPInteger(this, "userStatus", user.getUserStatus());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getApplicationContext().finishAllActivity();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        getApplicationContext().removeActivity(this);
        super.onDestroy();
    }
}
