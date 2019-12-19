package com.jiayue;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.jiguang.net.HttpUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.SmsBean;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.AesUtil;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;

import static com.jiayue.constants.Preferences.phoneMatcher;

/**
 * 重置密码
 *
 * @author BAO
 */
public class UserResetActivity extends BaseActivity {

    private EditText et_phone;
    private EditText et_code;
    private EditText et_password;
    private EditText et_confirm;
    TimeCount time = new TimeCount(60000, 1000);
    private TextView tv_send;
    private TextView tv_second;
    private LinearLayout ll_code;
    private String phone;
    private String confirm;
    private String verifCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reset_password);
        initView();
    }

    boolean flag;

    private String code;

    public void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone1);
        et_code = (EditText) findViewById(R.id.et_code);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confirm = (EditText) findViewById(R.id.et_confirm);
        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_second = (TextView) findViewById(R.id.tv_second);
        ll_code = (LinearLayout) findViewById(R.id.ll_code);
    }

    public void btn_send(View v) {
        phone = et_phone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {

            RequestParams params = new RequestParams(Preferences.SEND_SMS1);
            try {
                params.addQueryStringParameter("phone", AesUtil.getInstance().encrypt(phone));
//                params.addQueryStringParameter("phone", phone);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ActivityUtils.showToast(UserResetActivity.this, "正在发送中...");
            HttpUtils http = new HttpUtils();
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
                        ActivityUtils.showToast(UserResetActivity.this, "验证码获取失败:" + bean.getCodeInfo());
                    }
                    time.start();
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ActivityUtils.showToast(UserResetActivity.this, "验证码获取失败,请检查网络。");
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        } else {
            ActivityUtils.showToast(this, "您输入的手机号格式不正确！");
        }
    }

    public void Confirm(View v) {

        code = et_code.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || !phone.matches(phoneMatcher)) {
            ActivityUtils.showToastForFail(this, "手机号码为空或格式不正确");
            return;
        }
        String password = et_password.getText().toString().trim();
        if (!TextUtils.isEmpty(password) && password.length() >= 6 || password.length() <= 16) {
            et_password.setText(password);

        } else if (password.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(UserResetActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(password)) {
            ActivityUtils.showToastForFail(getApplication(), "请输入密码！");
            return;
        } else {
            ActivityUtils.showToastForFail(UserResetActivity.this, "密码请输入6-16位!");
            return;
        }
        confirm = et_confirm.getText().toString().trim();
        if (!TextUtils.isEmpty(confirm) && confirm.length() >= 6 || confirm.length() <= 16) {
            et_confirm.setText(password);
        } else if (confirm.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(UserResetActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(confirm)) {
            ActivityUtils.showToastForFail(getApplication(), "请输入确认密码！");
            return;
        } else {
            ActivityUtils.showToastForFail(UserResetActivity.this, "密码请输入6-16位!");
            return;
        }
        if (!password.equals(confirm)) {
            ActivityUtils.showToastForFail(getApplication(), "密码与确认密码不一致，请重新输入");
            return;
        }
        if (!ActivityUtils.isNetworkAvailable(this)) {
            ActivityUtils.showToastForFail(this, "无法连接网络");
            return;
        }

        if (code.equals(verifCode)) {
            reset();
        } else {
            ActivityUtils.showToast(UserResetActivity.this, "验证码错误");
        }
    }

    public void reset() {
        RequestParams params = new RequestParams(Preferences.RESET_PASSWORD);
        params.addQueryStringParameter("userName", phone);
        params.addQueryStringParameter("userPwd", confirm);

        DialogUtils.showMyDialog(UserResetActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在发送中...", null);
        HttpUtils http = new HttpUtils();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(s, type);
                DialogUtils.dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    ActivityUtils.showToastForSuccess(UserResetActivity.this, "密码重置成功！");
                    finish();
                } else {
                    DialogUtils.showMyDialog(UserResetActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "密码重置失败", bean.getCodeInfo(), null);
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(UserResetActivity.this, "密码重置失败,请检查网络。");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            tv_send.setText("重新验证");
            tv_second.setText("(60秒)");
            ll_code.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            ll_code.setClickable(false);
            tv_second.setText("(" + millisUntilFinished / 1000 + "秒)");
        }
    }

    public void btnBack(View v) {

        this.finish();
    }
}
