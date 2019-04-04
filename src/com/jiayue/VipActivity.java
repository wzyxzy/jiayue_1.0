package com.jiayue;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.VipAdapter;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.VipBean;
import com.jiayue.model.UserUtil;
import com.jiayue.rest.OnRefreshAdapterListener;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by BAO on 2016-09-27.
 */
public class VipActivity extends BaseActivity implements OnRefreshAdapterListener{

    private ListView listview;
    private TextView tv_header_title;
    private VipAdapter adapter;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        initView();
        initData();
    }

    private void initData() {
    	
    	RequestParams params = new RequestParams(Preferences.GET_VIP_LIST);
		params.addQueryStringParameter("userid", UserUtil.getInstance(this).getUserId());

        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<VipBean>() {
                }.getType();
                VipBean bean = gson.fromJson(s, type);
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    adapter.setList(bean.getData());
                    adapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
        
    }

    private void initView() {
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        tv_header_title.setText("资源获取");
        listview = (ListView) findViewById(R.id.listview);
        adapter = new VipAdapter(this,null);
        listview.setAdapter(adapter);
    }

    public void onInputClick(View v){
        DialogUtils.showGoodsDialog(this, MyPreferences.SHOW_VIP_INPUT,null,this);
    }

    public void btnBack(View v) {
        this.finish();
    }

    @Override
    public void onRefreshAdapter() {
        initData();
    }
}
