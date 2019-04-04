package com.jiayue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.YuGaoAdapter;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.OrderBean;
import com.jiayue.dto.base.YuGaoChildBean;
import com.jiayue.dto.base.YuGaoDataBean;
import com.jiayue.dto.base.ZhiboTimeBean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.TimeFormate;
import com.jiayue.view.ListViewForScrollView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YuGaoActivity extends BaseActivity {

    private TextView tv_header_title;
    private TextView title, price, content, teacher, teacher_content, time;
    private RadioGroup radioGroup;
    private ListViewForScrollView listview;
    private Map<Integer, Boolean> selectmap = new HashMap<>();
    private YuGaoAdapter adapter;
    private YuGaoDataBean bean;
    private List<YuGaoChildBean> list = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();
    private boolean isZhengTao = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_yugao);
        initView();
        initData();
    }

    private void initView() {
        // TODO Auto-generated method stub
        bean = (YuGaoDataBean) getIntent().getSerializableExtra("bean");
        list = bean.getChildren();
        Log.d(TAG, bean.toString());
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        tv_header_title.setText("订阅信息");

        title = (TextView) findViewById(R.id.textView1);
        price = (TextView) findViewById(R.id.price);
        content = (TextView) findViewById(R.id.textView4);
        teacher = (TextView) findViewById(R.id.textView7);
        teacher_content = (TextView) findViewById(R.id.textView6);
        time = (TextView) findViewById(R.id.textView10);
        listview = (ListViewForScrollView) findViewById(R.id.listView1);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (!isZhengTao) {
                    if (selectmap.isEmpty()) {
                        selectmap.put(position, true);
                    } else {
                        if (selectmap.containsKey(position)) {
                            selectmap.remove(position);
                        } else {
                            selectmap.put(position, true);
                        }
                    }
                    adapter.setSelectmap(selectmap);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adapter = new YuGaoAdapter(this, selectmap, list);
        listview.setAdapter(adapter);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        RadioButton b = (RadioButton) findViewById(R.id.radio0);
        b.setId(0);
        b.setChecked(true);

        findViewById(R.id.radio1).setId(1);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                Log.d(TAG, "checkedId===" + checkedId);
                if (checkedId == 0) {
                    isZhengTao = true;
                    selectmap.clear();
                    adapter.setSelectmap(selectmap);
                    adapter.notifyDataSetChanged();
                } else {
                    isZhengTao = false;
                }
            }
        });
        ((ScrollView) findViewById(R.id.scrollView1)).smoothScrollTo(0, 0);
    }

    private void initData() {
        // TODO Auto-generated method stub
        title.setText(bean.getCourseName());
        if (bean.getChildren() == null)
            price.setVisibility(View.INVISIBLE);
        else
            price.setText(String.format(getResources().getString(R.string.yugao_dingyue), bean.getPrice() + "", bean.getChildren().size(), (bean.getPrice() * bean.getChildren().size()) + ""));
        content.setText(bean.getCourseDisc());
        teacher.setText(bean.getExpert());
        teacher_content.setText(bean.getExpertInfo());

        ZhiboTimeBean start = bean.getStartTime();
        ZhiboTimeBean end = bean.getEndTime();
        String minutes = start.getMinutes() > 9 ? start.getMinutes() + "" : "0" + start.getMinutes();
        String date = String
                .format(getResources().getString(R.string.yugao_time), "20" + start.getYear() % 100 + "-" + (start.getMonth() + 1) + "-" + start.getDate(), "20" + end.getYear() % 100 + "-" + (end
                        .getMonth() + 1) + "-" + end.getDate(), TimeFormate.getDay(start.getDay()), start.getHours() + ":" + minutes);
        time.setText(date);

    }

    public void btnBack(View v) {
        this.finish();
    }

    public void onClickEnter(View view) {
        DialogUtils.showMyDialog(YuGaoActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在努力加载...", null);
        String courseIds = "";
        String pid = "";
        if (radioGroup.getCheckedRadioButtonId() == 0) {
            courseIds = bean.getCourseId() + "";
        } else {
            if (selectmap.isEmpty()) {
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(this, "请选择购买课程");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (selectmap.containsKey(i)) {
                        courseIds += list.get(i).getCourseId() + ",";
                    }
                }
                courseIds = courseIds.substring(0, courseIds.length() - 1);
                pid = bean.getCourseId() + "";
            }
        }
        buycourse(courseIds, pid);
    }

    private void buycourse(String courseIds, String pid) {
    	
    	RequestParams params = new RequestParams(Preferences.SUBMIT_COURSE_ORDER);
		params.addQueryStringParameter("courseIds", courseIds);
		params.addQueryStringParameter("userId", UserUtil.getInstance(YuGaoActivity.this).getUserId());
		params.addQueryStringParameter("pid", pid);

        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<OrderBean>() {
                }.getType();
                OrderBean bean = gson.fromJson(s, type);

                if (bean.getCode().equals("SUCCESS")) {
                    Message msg = new Message();
                    msg.obj = bean;
                    msg.what = CMD_ORDER;
                    mHandler.sendMessage(msg);
                } else {
                    ActivityUtils.showToast(YuGaoActivity.this, bean.getCodeInfo());
                }
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(YuGaoActivity.this, "订单提交失败");
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

    private final int CMD_ORDER = 0x01;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CMD_ORDER:
//                    OrderBean orderbean = (OrderBean) msg.obj;
//                    Log.d(TAG, "bean------------==" + orderbean.getCodeInfo() + "============data===" + orderbean.getData());
//                    if (orderbean.getData().getTotalPrice().equals("0.0")) {
//                        ActivityUtils.showToastForSuccess(YuGaoActivity.this, "预订成功");
//                        finish();
//                    } else {
//                        String name = "";
//                        String body = "";
//                        if (radioGroup.getCheckedRadioButtonId() == 0) {
//                            name = bean.getCourseName() + "整套";
//                        } else {
//                            name = bean.getCourseName() + "分集";
//                        }
//                        body = "直播付费";
//                        Intent intent = new Intent(YuGaoActivity.this, PayChooseActivity.class);
//                        Bundle b = new Bundle();
//                        b.putString(PaySubmitActivity.ORDER_PRICE, orderbean.getData().getTotalPrice());
//                        b.putString(PaySubmitActivity.ORDER_CODE, orderbean.getData().getOrderCode());
//                        b.putString(PaySubmitActivity.ORDER_URL, orderbean.getData().getNotify_url());
//                        b.putString(PaySubmitActivity.ORDER_NAME, name);
//                        b.putString(PaySubmitActivity.ORDER_BODY, body);
//                        intent.putExtra(PaySubmitActivity.ORDER, b);
//                        startActivityForResult(intent, 99);
//                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 999:
                finish();
                break;

            default:
                break;
        }
    }

    ;
}
