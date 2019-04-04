package com.jiayue;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.PayRecordAdapter;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.RecordBean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.MyPreferences;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class PayRecordActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refresh_view;
    private ListView listview;
    private PayRecordAdapter adapter;
    private List<RecordBean.Data> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);

        initView();
    }

    private void initView() {
        refresh_view = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        listview = (ListView) findViewById(R.id.listview);
        adapter = new PayRecordAdapter(this,list);
        listview.setAdapter(adapter);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        refresh_view.setRefreshing(true);
        getData();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.setUri(Preferences.ATTACHORDER_RECORD);
        params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                Log.d("payRecord","payRecord="+s);
                java.lang.reflect.Type type = new TypeToken<RecordBean>() {
                }.getType();
                RecordBean bookJson = gson.fromJson(s, type);

                if (bookJson.getCode().equals(MyPreferences.SUCCESS) && null != bookJson.getData()) {
                    adapter.setList(bookJson.getData());
                    adapter.notifyDataSetChanged();

                } else if (bookJson.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToast(PayRecordActivity.this, "加载失败," + bookJson.getCodeInfo());
                }
                refresh_view.setRefreshing(false);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(PayRecordActivity.this, "加载失败,请检查网络！");
                refresh_view.setRefreshing(false);
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    public void btnBack(View v) {
        this.finish();
    }

    @Override
    public void onRefresh() {
        getData();
    }
}
