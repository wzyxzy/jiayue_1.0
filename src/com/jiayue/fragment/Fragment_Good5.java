package com.jiayue.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jiayue.GoodsListActivity;
import com.jiayue.R;
import com.jiayue.rest.LastOrNextListener;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Good5 extends Fragment implements View.OnClickListener {

    private View mRootView;
    private LastOrNextListener listener;
    private Button btn_last, btn_pay;

    public void setLastOrNextListener(LastOrNextListener listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fagment_goods5, null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        btn_last = (Button) mRootView.findViewById(R.id.button7);
        btn_pay = (Button) mRootView.findViewById(R.id.button8);
        btn_last.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button7:
                listener.back();
                break;
            case R.id.button8:
                startActivity(new Intent(getActivity(), GoodsListActivity.class));
                break;
        }
    }
}
