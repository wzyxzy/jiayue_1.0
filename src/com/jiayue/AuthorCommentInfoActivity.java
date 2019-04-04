package com.jiayue;

import com.jiayue.R;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-9-16 上午9:02:49 项目名称：wyst
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：AuthorCommentInfoActivity.java 类说明：
 *        ------------------------------------------------------------------
 */
public class AuthorCommentInfoActivity extends BaseActivity {
	private TextView tv_header_title;
	private TextView tv_content;
	private String title;
	private String content;
	private String time;
	private TextView tv_addTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.author_comment_info);
		title = getIntent().getStringExtra("title");
		content = getIntent().getStringExtra("content");
		time = getIntent().getStringExtra("time");
		initView();
	}
	private void initView() {
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_addTime = (TextView) findViewById(R.id.time);
		tv_header_title.setText(title);
		tv_content.setText(content);
		tv_addTime.setText(time);
	}
	public void btnBack(View v) {
		this.finish();
	}

}
