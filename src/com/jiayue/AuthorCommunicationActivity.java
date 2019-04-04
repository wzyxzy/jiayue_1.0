package com.jiayue;

import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.CommonAdapter;
import com.jiayue.adapter.ViewHolder;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.CommentBean;
import com.jiayue.dto.base.CommentBean.Data.MainContents;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.view.MyXListView;
import com.jiayue.view.MyXListView.IXListViewListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-9-8 下午4:07:37 项目名称：jiayue
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：ReaderCommunication.java 类说明：
 *        ------------------------------------------------------------------
 */
public class AuthorCommunicationActivity extends BaseActivity
		implements
			IXListViewListener {
	DisplayImageOptions options;
	private int cover_normal;
	private String bookName;
	private String image_url;
//	private BookVO bookVO;
	private String bookId;
	private ImageView iv_splash;
	private TextView tv_bookname;
	private TextView tv_desc;
	private MyXListView listview;
	private TextView tv_header_title;
	private Button btn_post;
	private TextView tv_author;
	private CommonAdapter<MainContents> adapter;
	private BroadcastReceiver broadcastReciver;
	public static final int REQUESTCODE1 = 1001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader_communication);
		cover_normal = getResources().getIdentifier("cover_normal", "drawable",
				getPackageName());
		options = new DisplayImageOptions.Builder().showStubImage(cover_normal)
				// 设置图片下载期间显示的图片
				.showImageForEmptyUri(cover_normal)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(cover_normal)
				// 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
		image_url = getIntent().getStringExtra("image_url");
		bookId = getIntent().getStringExtra("bookId");
		bookName = getIntent().getStringExtra("bookname");
//		bookVO = (BookVO) (getIntent().getBundleExtra("bundle")
//				.getSerializable("bookVO"));
		loadData();
		initViews();

	}
	private void loadData() {
		RequestParams params = new RequestParams(Preferences.AUTHOR_COMMENT);
		params.addQueryStringParameter("bookId", bookId);
		params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
		params.addQueryStringParameter("isAuthorReader", "1");
		
		x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<CommentBean>() {
                }.getType();
                CommentBean bean = gson.fromJson(s, type);
                if (bean != null && bean.getCode().equals("SUCCESS")) {
					updata(bean.getData());
                } else {
                	DialogUtils.showMyDialog(
							AuthorCommunicationActivity.this,
							MyPreferences.SHOW_ERROR_DIALOG, "获取失败",
							bean.getCodeInfo(), null);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(AuthorCommunicationActivity.this, "信息获取失败，请检查网络");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
	}

	public void btn_send(View v) {
		Intent intent = new Intent(this, AuthorSendActivity.class);
		intent.putExtra("title", "发表问题");
		intent.putExtra("bookId", bookId);
		startActivity(intent);
	}
	private void initViews() {
		btn_post = (Button) findViewById(R.id.btn_post);
		btn_post.setText("发表问题");
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("作者沟通");
		tv_author = (TextView) findViewById(R.id.tv_author);
		iv_splash = (ImageView) findViewById(R.id.iv_splash);
		imageLoader.displayImage(image_url, iv_splash, options);
		tv_bookname = (TextView) findViewById(R.id.tv_bookname);
		tv_bookname.setText(bookName);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		initBroadcast();
	}
	private void initBroadcast() {
		broadcastReciver = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals("android.intent.action.author")||action.equals("android.intent.action.author.desc")) {
					loadData();
				}
			};
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.author");
		filter.addAction("android.intent.action.author.desc");
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(broadcastReciver, filter);

	}
	private void updata(CommentBean.Data comment) {
		if (comment != null) {
			tv_author.setText(comment.getAuthor());
			tv_desc.setText(comment.getAuthorInfo());
			final List<MainContents> mainContents = comment.getMainContents();

			listview = (MyXListView) findViewById(R.id.listview);
			listview.setPullLoadEnable(false);
			listview.setPullRefreshEnable(false);
			adapter = new CommonAdapter<MainContents>(this, mainContents,
					R.layout.communication_item) {

				@Override
				public void convert(ViewHolder helper, MainContents item,
						int position) {
					helper.setText(R.id.tv_question, item.getTitle());
					helper.setText(R.id.tv_time, item.getAddTime());
					helper.setText(R.id.tv_content, item.getContent());
					helper.setText(R.id.tv_comment_count, item.getQACount());
					if (Integer.valueOf(item.getHasNewReply()) == 0) {
						helper.setInVisibility(R.id.iv_new);
					}
				}
			};
			listview.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(
							AuthorCommunicationActivity.this,
							AuthorDescriptionItemActivity.class);
					String content = mainContents.get(position - 1)
							.getContent();
					intent.putExtra("content", content);
					intent.putExtra("id", mainContents.get(position - 1)
							.getId());
					intent.putExtra("addTime", mainContents.get(position - 1)
							.getAddTime());
					intent.putExtra("title", mainContents.get(position - 1)
							.getTitle());
					startActivity(intent);
				}
			});
		}
	}

	public void btnBack(View v) {
		this.finish();
	}

	@Override
	public void onLoadMore() {
	}

	@Override
	protected void onResume() {
		loadData();
		super.onResume();
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(broadcastReciver);
		super.onDestroy();
	}
}
