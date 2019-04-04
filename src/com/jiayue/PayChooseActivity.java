package com.jiayue;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.alipay.PayResult;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.AliBean;
import com.jiayue.dto.base.WXBean;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.DecimalFormat;

public class PayChooseActivity extends BaseActivity {


    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    private LinearLayout mAliPay, mWxPay;
    private CheckBox cb_alipay, cb_wx;

    private DisplayImageOptions options;
    private String bookId;
    private String bookName;
    private String image_url;
    private String attachName;
    private float price;
    private String orderCode;
    private int choose_Type = 0;//0为支付宝 1微信
    private final String TAG = getClass().getSimpleName();
    private Button btn_pay;
    public static final int PAY_SUCCESS = 0x033;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
//                    Toast.makeText(PayChooseActivity.this, resultInfo.toString(), Toast.LENGTH_SHORT).show();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayChooseActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        setResult(PAY_SUCCESS);
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayChooseActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayChooseActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(PayChooseActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay);


        Bundle b = getIntent().getBundleExtra("data");

        bookId = b.getString("bookId");
        bookName = b.getString("bookName");
        image_url = b.getString("image_url_str");
        attachName = b.getString("attachName");
        price = b.getFloat("price");
        orderCode = b.getString("ordercode");

        initView();
        initListener();


    }

    private boolean isWXpay = false;
    @Override
    protected void onResume() {
        super.onResume();
        if(isWXpay){
            setResult(PAY_SUCCESS);
            finish();
        }
    }

    private void initView() {
        // TODO Auto-generated method stub

        int cover_normal = getResources().getIdentifier("cover_normal", "drawable", getPackageName());
        options = new DisplayImageOptions.Builder().showStubImage(cover_normal)
                // 设置图片下载期间显示的图片
                .showImageForEmptyUri(cover_normal)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(cover_normal)
                // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)
                // 设置下载的图片是否缓存在内存中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        String geff = df.format(price);//返回的是String类型的

        imageLoader.displayImage(image_url, (ImageView) findViewById(R.id.iv_book), options);
        ((TextView) findViewById(R.id.tv_bookname)).setText(bookName);
        ((TextView) findViewById(R.id.tv_attach_name)).setText("图书附件 " + attachName);
        ((TextView) findViewById(R.id.tv_price)).setText("¥" + geff);
        mAliPay = (LinearLayout) findViewById(R.id.layout_alipay);
        mWxPay = (LinearLayout) findViewById(R.id.layout_wx);
        cb_alipay = (CheckBox) findViewById(R.id.cb_alipay);
        cb_wx = (CheckBox) findViewById(R.id.cb_wx);

        ((TextView) findViewById(R.id.tv_total)).setText("总计：¥" + geff);
        btn_pay = (Button) findViewById(R.id.btn_submit);
    }

    private void initListener() {
        // TODO Auto-generated method stub

        mAliPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_Type = 0;
                cb_alipay.setChecked(true);
                cb_wx.setChecked(false);
            }
        });

        mWxPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_Type = 1;
                cb_alipay.setChecked(false);
                cb_wx.setChecked(true);
            }
        });

        btn_pay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (choose_Type == 0)
                    pay();
                else if (choose_Type == 1)
                    wxPay();
            }
        });
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay() {

        DialogUtils.showMyDialog(this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "获取订单中...", null);
        btn_pay.setEnabled(false);
        RequestParams params = new RequestParams();
        params.setUri(Preferences.ALIPAY);
        params.addQueryStringParameter("attachCode", orderCode);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "attachone---------=" + s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<AliBean>() {
                }.getType();
                final AliBean bean = gson.fromJson(s, type);

                DialogUtils.dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(MyPreferences.SUCCESS) && null != bean.getData()) {
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(PayChooseActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(bean.getData(), true);

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };

                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToastForFail(PayChooseActivity.this, "加载失败," + bean.getCodeInfo());
                }
                btn_pay.setEnabled(true);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(PayChooseActivity.this, "加载失败,请检查网络!");
                DialogUtils.dismissMyDialog();
                btn_pay.setEnabled(true);
            }

            @Override
            public void onCancelled(CancelledException e) {
                DialogUtils.dismissMyDialog();
                btn_pay.setEnabled(true);
            }

            @Override
            public void onFinished() {
                DialogUtils.dismissMyDialog();
                btn_pay.setEnabled(true);
            }
        });


    }

    public void btnBack(View v) {
        finish();
    }


    /***
     * 微信支付
     */
    private IWXAPI api;

    private void wxPay() {

        DialogUtils.showMyDialog(this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "获取订单中...", null);

        final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

        // 将该app注册到微信
        msgApi.registerApp(Preferences.WX_APP_ID);
        api = WXAPIFactory.createWXAPI(this, Preferences.WX_APP_ID);

        btn_pay.setEnabled(false);


        RequestParams params = new RequestParams();
        params.setUri(Preferences.WXPAY);
        params.addQueryStringParameter("attachCode", orderCode);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "order----wx---=" + s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<WXBean>() {
                }.getType();
                final WXBean bean = gson.fromJson(s, type);

                DialogUtils.dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(MyPreferences.SUCCESS) && null != bean.getData()) {
                    PayReq req = new PayReq();
                    req.appId = bean.getData().getAppid();
                    req.partnerId = bean.getData().getPartnerid();//商户号id
                    req.prepayId =bean.getData().getPrepayid();
                    req.nonceStr = bean.getData().getNoncestr();
                    req.timeStamp = bean.getData().getTimestamp();
                    req.packageValue = "Sign=WXPay";
                    req.sign = bean.getData().getSign();
                    req.extData = "app data"; // optional
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                    isWXpay = true;
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToastForFail(PayChooseActivity.this, "加载失败," + bean.getCodeInfo());
                }
                btn_pay.setEnabled(true);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(PayChooseActivity.this, "加载失败,请检查网络!");
                DialogUtils.dismissMyDialog();
                btn_pay.setEnabled(true);
            }

            @Override
            public void onCancelled(CancelledException e) {
                DialogUtils.dismissMyDialog();
                btn_pay.setEnabled(true);
            }

            @Override
            public void onFinished() {
                DialogUtils.dismissMyDialog();
                btn_pay.setEnabled(true);
            }
        });
    }

}
