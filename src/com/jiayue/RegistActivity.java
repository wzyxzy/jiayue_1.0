package com.jiayue;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.SmsBean;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.AesUtil;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;

import org.xutils.common.Callback;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;

import static com.jiayue.constants.Preferences.phoneMatcher;

public class RegistActivity extends BaseActivity {
    private TextView tv_header_title;
    EditText et_code;
    LinearLayout ll_code;
    // ImageView iv_clear_code;
    EditText et_phone;
    LinearLayout ll_phone;
    ImageView iv_clear_phone;
    EditText et_password01;
    LinearLayout ll_password01;
    EditText et_password02;
    LinearLayout ll_password02;
    Button btn_send;
    LinearLayout ll_jycode;
    TextView second;
    String verifCode;
    private Button btn_regist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().addActivity(this);
        setContentView(R.layout.regist);
        initView();
    }

    /**
     * 初始化修改密码界面
     */
    public void initView() {
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        tv_header_title.setText("用户注册");
        btn_send = (Button) findViewById(R.id.btn_send);
        et_code = (EditText) findViewById(R.id.et_code);
        ll_code = (LinearLayout) findViewById(R.id.ll_code);
        et_phone = (EditText) findViewById(R.id.et_phone);
        ll_phone = (LinearLayout) findViewById(R.id.ll_phone);
        et_password01 = (EditText) findViewById(R.id.et_password01);
        ll_password01 = (LinearLayout) findViewById(R.id.ll_password01);
        et_password02 = (EditText) findViewById(R.id.et_password02);
        ll_password02 = (LinearLayout) findViewById(R.id.ll_password02);
        btn_regist = (Button) findViewById(R.id.btn_regist);
    }

    public void btnBack(View v) {
        this.finish();
    }

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private String[] myPermissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
    };

    private void permission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
                myPermissions);
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
        reginst();
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("加阅需要访问“读取本机识别码”权限，请到 “设置 -> 应用权限” 中授予！");
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

    private void doRrgist() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //当前系统大于等于6.0
            permission();
        } else {
            //当前系统小于6.0，直接调用拍照
            doBackup();
        }
    }

    public void btnConfirm(View v) {
        String phone = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {
            et_phone.setText(phone);
        } else if (phone.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(RegistActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(phone)) {
            ActivityUtils.showToastForFail(getApplication(), "请输入手机号码！");
            return;
        } else {
            ActivityUtils.showToastForFail(getApplication(), "手机号码格式不正确！");
            return;
        }
        String password1 = et_password01.getText().toString().trim();
        if (!TextUtils.isEmpty(password1) && password1.length() >= 6 && password1.length() <= 16) {
            et_password01.setText(password1);
        } else if (password1.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(RegistActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(password1)) {
            ActivityUtils.showToastForFail(getApplication(), "请输入密码！");
            return;
        } else {
            ActivityUtils.showToastForFail(RegistActivity.this, "密码请输入6-16位");
            return;
        }
        String password2 = et_password02.getText().toString().trim();
        if (!TextUtils.isEmpty(password1) && password2.length() >= 6 && password2.length() <= 16) {
            et_password01.setText(password1);
        } else if (password2.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(RegistActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(password2)) {
            ActivityUtils.showToastForFail(getApplication(), "请输入确认密码！");
            return;
        } else {
            ActivityUtils.showToastForFail(RegistActivity.this, "密码请输入6-16位");
            return;
        }
        if (!password1.equals(password2)) {
            ActivityUtils.showToastForFail(getApplication(), "密码与确认密码不一致，请重新输入");
            return;
        }
        if (!ActivityUtils.isNetworkAvailable(this)) {
            ActivityUtils.showToastForFail(this, "无法连接网络");
            return;
        }
        if (code.equals(verifCode)) {
            if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {
                System.out.println("phone=>>" + phone);
                doRrgist();
            }
        } else {
            ActivityUtils.showToast(RegistActivity.this, "验证码错误");
        }

    }

    /**
     * 获取设备唯一标识符(Android 10)
     *
     * @return 唯一标识符
     */
    public String getDeviceId() {
        return Settings.System.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    private void reginst() {
        userName = et_phone.getText().toString().trim();
        userPwd = et_password01.getText().toString().trim();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        String imei = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            imei = telephonyManager.getDeviceId();
        else
            imei = getDeviceId();
        RequestParams params = new RequestParams(Preferences.REGINST_URL);
        params.addQueryStringParameter("userName", userName);
        params.addQueryStringParameter("userPwd", userPwd);
        params.addQueryStringParameter("userEmail", et_code.getText().toString().trim());
        params.addQueryStringParameter("phoneId", imei);

        DialogUtils.showMyDialog(RegistActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG_NO, null, "正在发送中...", null);
        btn_regist.setClickable(false);
        x.http().post(params, new CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    ActivityUtils.showToast(RegistActivity.this, bean.getCodeInfo());
                    SPUtility.putSPString(RegistActivity.this, "username", userName);
                    SPUtility.putSPString(RegistActivity.this, "userPwd", userPwd);
                    Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    DialogUtils.showMyDialog(RegistActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "注册失败", bean.getCodeInfo(), null);
                    btn_regist.setClickable(true);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(RegistActivity.this, "注册失败,请检查网络。");
                btn_regist.setClickable(true);
            }

            @Override
            public void onCancelled(CancelledException e) {
                btn_regist.setClickable(true);
            }

            @Override
            public void onFinished() {
                btn_regist.setClickable(true);
            }
        });
    }

    TimeCount time = new TimeCount(60000, 1000);
    private String userName;
    private String userPwd;
    private String phone;

    public void btn_login(View v) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void btn_send(View v) {
        phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) && !phone.matches(phoneMatcher)) {
            return;
        }
        DialogUtils.showMyDialog(RegistActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "发送短信", "<center>您确认要给" + phone + "发送短信吗？</center>", new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ActivityUtils.isNetworkAvailable(RegistActivity.this)) {
                    ActivityUtils.showToast(RegistActivity.this, "无法连接网络，发送失败");
                    DialogUtils.dismissMyDialog();
                    return;
                }

                if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {
                    RequestParams params = new RequestParams(Preferences.SEND_SMS1);
//                    RequestParams params = new RequestParams(Preferences.SEND_SMS);
                    try {
                        params.addQueryStringParameter("phone", AesUtil.getInstance().encrypt(phone));
//                        params.addQueryStringParameter("phone", phone);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ActivityUtils.showToast(RegistActivity.this, "正在发送中...");
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Gson gson = new Gson();
                            java.lang.reflect.Type type = new TypeToken<SmsBean>() {
                            }.getType();
                            SmsBean bean = gson.fromJson(s, type);

                            if (bean != null && bean.getCode().equals("SUCCESS")) {
                                verifCode = bean.getData().getCheckCode();
                            } else {
                                assert bean != null;
                                ActivityUtils.showToast(RegistActivity.this, "验证码获取失败:" + bean.getCodeInfo());
                            }
                            DialogUtils.dismissMyDialog();
                            time.start();
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            DialogUtils.dismissMyDialog();
                            ActivityUtils.showToast(RegistActivity.this, "验证码获取失败,请检查网络。");
                        }

                        @Override
                        public void onCancelled(CancelledException e) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                } else {
                    ActivityUtils.showToast(RegistActivity.this, "您输入的手机号格式不正确！");
                    DialogUtils.dismissMyDialog();
                }
            }
        });

    }

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

    /**
     * 清除用户名输入框
     *
     * @param
     */
    // public void clearClick(View v) {
    // int id = v.getId();
    // if (id == R.id.iv_clear_code) {
    // et_code.setText("");
    // v.setVisibility(View.GONE);
    // } else if (id == R.id.iv_clear_phone) {
    // et_phone.setText("");
    // v.setVisibility(View.GONE);
    // } else if (id == R.id.iv_delete01) {
    // et_password01.setText("");
    // v.setVisibility(View.GONE);
    // } else if (id == R.id.iv_delete02) {
    // et_password02.setText("");
    // v.setVisibility(View.GONE);
    // } else {
    // }
    //
    // }

    // /**
    // * 验证输入的邮箱格式是否符合
    // *
    // * @param email
    // * @return 是否合法
    // */
    // public static boolean emailFormat(String email) {
    // boolean tag = true;
    // final String pattern1 =
    // "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$";
    // final Pattern pattern = Pattern.compile(pattern1);
    // final Matcher mat = pattern.matcher(email);
    // if (!mat.find()) {
    // tag = false;
    // }
    // return tag;
    // }
    @Override
    protected void onDestroy() {
        getApplicationContext().removeActivity(this);
        super.onDestroy();
    }
}
