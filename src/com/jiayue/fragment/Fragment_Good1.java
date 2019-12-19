package com.jiayue.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.R;
import com.jiayue.adapter.GoodsAdapter;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.GoodsListBean;
import com.jiayue.rest.LastOrNextListener;
import com.jiayue.util.ActivityUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Good1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private View mRootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listview;
    private GoodsAdapter adapter;
    private LastOrNextListener listener;
    private int courseId;
    private final String TAG = getClass().getSimpleName();
    private long lasttime;

    public void setLastOrNextListener(LastOrNextListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fagment_goods1,null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
//        loadDate();
    }

    public void refreshFragment1(){
        loadDate();
    }

    private void loadDate(){
//        if (System.currentTimeMillis() - lasttime <= 1000)
//            return;
//        lasttime = System.currentTimeMillis();
    	
    	RequestParams params = new RequestParams(Preferences.ZHIBO_GOODSLIST);
		params.addQueryStringParameter("courseId", courseId+"");

        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<GoodsListBean>() {
                }.getType();
                GoodsListBean bean = gson.fromJson(s, type);
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    adapter.setList(bean.getData());
                    adapter.notifyDataSetChanged();
                } else {
                    ActivityUtils.showToastForFail(getActivity(), "获取信息失败");
                }
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(getActivity(), "获取信息失败");
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
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
        courseId =  getArguments().getInt("courseId");
        swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        listview = (ListView) mRootView.findViewById(R.id.listView);
        adapter = new GoodsAdapter(getActivity(),null,listener);
        listview.setAdapter(adapter);
        loadDate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        loadDate();
    }
}
