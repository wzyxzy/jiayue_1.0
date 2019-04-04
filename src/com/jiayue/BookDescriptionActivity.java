package com.jiayue;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayue.dto.base.BookVO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class BookDescriptionActivity extends BaseActivity {
	BookVO bookVO;
	DisplayImageOptions options;
	private String image_url;
	private String bookName;
	private ImageView iv_splash;
    private TextView tv_bookname;
    private TextView tv_author;
    private TextView tv_publish;
    private TextView tv_intro;
	private TextView tv_header_title;
	/*
	 * Title: onCreate Description:
	 * 
	 * @param savedInstanceState
	 * 
	 * @see com.jiayue.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApplicationContext().addActivity(this);
		int cover_normal = getResources().getIdentifier("cover_normal",
				"drawable", getPackageName());
		options = new DisplayImageOptions.Builder()
				.showStubImage(cover_normal) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(cover_normal) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(cover_normal) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true).build();
		setContentView(R.layout.book_descrip);
		image_url = getIntent().getStringExtra("image_url");
		bookName = getIntent().getStringExtra("bookname");
		bookVO = (BookVO) (getIntent().getBundleExtra("bundle")
				.getSerializable("bookVO"));
		initView();
		loadData();
	}

	public void initView() {
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("图书简介");
		iv_splash = (ImageView) findViewById(R.id.iv_splash);
        tv_bookname = (TextView) findViewById(R.id.tv_bookname);
        tv_author = (TextView) findViewById(R.id.tv_author);
        tv_publish = (TextView) findViewById(R.id.tv_publish);
        tv_intro = (TextView) findViewById(R.id.tv_intro);
	}
	private void loadData() {
		imageLoader.displayImage(image_url, iv_splash,options);
		tv_bookname.setText(bookName);
		tv_author.setText(bookVO.getBookAuthor());
		tv_publish.setText(bookVO.getBookPublish());
		tv_intro.setText("　"+bookVO.getBookIntro());
	}
	public void btnBack(View v) {
		this.finish();
	}
}
