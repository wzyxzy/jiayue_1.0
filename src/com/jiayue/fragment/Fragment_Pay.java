package com.jiayue.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiayue.R;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Pay extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	private View mRootView;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.book, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}
}
