package com.jiayue;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.jiayue.adapter.PaySubmitAdapter;
import com.jiayue.dto.base.BookVO;

public class PaySubmitActivity extends BaseActivity{
	private Button mBtn_Submit;
	private List<BookVO> list;
	private ListView mListView;
	private PaySubmitAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_submit);
	
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		list = (ArrayList<BookVO>) getIntent().getSerializableExtra(PayTotalActivity.PAY_LIST);
		mBtn_Submit = (Button) findViewById(R.id.btn_submit);
		mBtn_Submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(PaySubmitActivity.this, PayChooseActivity.class));
			}
		});
		mListView = (ListView) findViewById(R.id.listView1);
		mAdapter = new PaySubmitAdapter(PaySubmitActivity.this, list);
		mListView.setAdapter(mAdapter);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		
	}
	
	public void btnBack(View v){
		finish();
	}
}
