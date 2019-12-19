package com.jiayue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.MediaPlayerActivity;
import com.jiayue.R;
import com.jiayue.ZhiYueDetailActivity;
import com.jiayue.adapter.ZhiYueAdapter;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.RecommendBean;
import com.jiayue.model.BannerImageLoader;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.SPUtility;
import com.jiayue.view.SuperSwipeRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_ZhiYue extends Fragment implements OnRefreshListener {

    private View mRootView;
    private final String TAG = getClass().getSimpleName();
    private SuperSwipeRefreshLayout refresh_view;
    private RecyclerView recyclerView;
    private ZhiYueAdapter adapter;
    private Banner banner;
    private GifImageView music_img;

    private List<RecommendBean.Data.OrdinaryList> ordinaryList = new ArrayList<>();
    private List<RecommendBean.Data.OrdinaryList> roundMapList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_zhiyue, null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        refresh_view.measure(0, 0);
        refresh_view.setRefreshing(true);
        requestData();
    }

    @Override
    public void onStart() {
        super.onStart();
        banner.startAutoPlay();
    }


    @Override
    public void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    private void initData() {
        // TODO Auto-generated method stub
        List<String> imagepaths = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (RecommendBean.Data.OrdinaryList list : roundMapList) {
            imagepaths.add(list.getCoverPath());
            titles.add(list.getContent());
        }

        banner.setImages(imagepaths).setImageLoader(new BannerImageLoader(getActivity())).setBannerTitles(titles).setDelayTime(2000)
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setIndicatorGravity(BannerConfig.CENTER)
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Intent intent = new Intent(getActivity(), ZhiYueDetailActivity.class);
                        intent.putExtra("data", roundMapList.get(position));
                        startActivity(intent);
                    }
                }).start();

        adapter.setNewData(ordinaryList);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        // TODO Auto-generated method stub
        refresh_view = (SuperSwipeRefreshLayout) mRootView.findViewById(R.id.swipe);
        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        adapter = new ZhiYueAdapter(getActivity(), R.layout.item_zhiyue, null);
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_zhiyue, null);
        banner = (Banner) header.findViewById(R.id.banner);
        adapter.addHeaderView(header);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), ZhiYueDetailActivity.class);
                intent.putExtra("data", ordinaryList.get(position));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        music_img = mRootView.findViewById(R.id.music_img);
        music_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MediaPlayerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(SPUtility.getSPString(getContext(), "isPlay")) && SPUtility.getSPString(getContext(), "isPlay").equals("true")) {
            music_img.setImageResource(R.drawable.music_play2);
        } else {
            music_img.setImageResource(R.drawable.music_right);

        }
    }

    private void requestData() {
        RequestParams params = new RequestParams(Preferences.RECOMMEND_LIST);
        params.addQueryStringParameter("userId", UserUtil.getInstance(getActivity()).getUserId());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "ZHIYUE DATA==" + s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<RecommendBean>() {
                }.getType();
                RecommendBean bean = gson.fromJson(s, type);
                if (bean != null) {
                    ordinaryList = bean.getData().getOrdinaryList();
                    roundMapList = bean.getData().getRoundMapList();
                    initData();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d(TAG, "onError" + throwable.getMessage());
                if (isAdded())
                    ActivityUtils.showToast(getActivity(), getString(R.string.load_fail) + "请检查网络");
            }

            @Override
            public void onCancelled(CancelledException e) {
                Log.d(TAG, "onCancelled");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished");
                if (refresh_view.isRefreshing())
                    refresh_view.setRefreshing(false);
            }
        });

    }


    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        refresh_view.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData();
            }
        }, 0);

    }

}
