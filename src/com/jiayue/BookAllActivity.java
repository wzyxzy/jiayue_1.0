package com.jiayue;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.BookAllAdapter;
import com.jiayue.adapter.BookAllAdapter.onListRefreshListener;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.BookVO;
import com.jiayue.dto.base.BookVOBean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jiguang.net.HttpUtils;

public class BookAllActivity extends Activity implements OnRefreshListener {
	private SwipeRefreshLayout refresh_view;
	private List<BookVO> books = new ArrayList<BookVO>();// 书籍列表数据
	String pageNo = "1";// 页码
	String pageSize = "10";// 每页
	private ListView listview;
	private TextView tv_header_title;
	private BookAllAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bookall);
		initView();
		loadCancelBook();
	}

	private void initView() {
		// TODO Auto-generated method stub
		refresh_view = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
		refresh_view.setOnRefreshListener(this);
		refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("全部书籍");

		listview = (ListView) findViewById(R.id.listView);
		adapter = new BookAllAdapter(this, books, 0);
		adapter.setonListRefreshListener(new onListRefreshListener() {

			@Override
			public void onListRefresh() {
				// TODO Auto-generated method stub
				loadCancelBook();
			}
		});
		listview.setAdapter(adapter);
	}

	public void btnBack(View v) {
		this.finish();
	}

	private void loadCancelBook() {
		if (!ActivityUtils.isNetworkAvailable(this)) {
			ActivityUtils.showToastForFail(this, "信息获取失败,请连接网络");
			refresh_view.setRefreshing(false);
			return;
		}
		RequestParams params = new RequestParams(Preferences.BOOKLIST_CANCEL);
		params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());

		x.http().post(params, new Callback.CommonCallback<String>(){
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<BookVOBean>() {
				}.getType();
				BookVOBean bean = gson.fromJson(s ,type);

				if (bean != null && bean.getCode().equals("SUCCESS")) {
					books.clear();
					books.addAll(bean.getData());
					Collections.reverse(books);// 反向

					int size = books.size();
					initBook(size);
				} else {
					ActivityUtils.showToastForFail(BookAllActivity.this, "加载失败," + bean.getCodeInfo());
				}
				refresh_view.setRefreshing(false);
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				ActivityUtils.showToastForFail(BookAllActivity.this, "信息获取失败");
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

	/**
	 * 初始化图书列表数据
	 */
	List<BookVO> list = new ArrayList<BookVO>();
	private List<BookVO> initBook(final int size) {
		// String str = SPUtility.getSPString(this, "booksData");
		// // FinalDb db=FinalDb.create(this, "bookvo.db");
		// if (!str.equals("")) {
		// try {
		// String[] strBooks = str.split(";");
		// for (int i = 0; i < strBooks.length; i++) {
		// String[] strBook = strBooks[i].split(":");
		// BookVO bookVO = new BookVO();
		// bookVO.setBookId(strBook[0]);
		// bookVO.setBookName(strBook[1]);
		// bookVO.setBookImg(strBook[2]);
		// String strSaveName = strBook[2].substring(0,
		// strBook[2].lastIndexOf("."));
		// bookVO.setBookSaveName(strSaveName);
		// bookVO.setBookImgPath(strBook[3]);
		// bookVO.setIsPlay(Integer.parseInt(strBook[4]));
		// list.add(bookVO);
		//
		// // bookVO.setIspackage(Integer.parseInt(strBook[0]));
		// // db.save(bookVO);
		// }
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// }
		// }else {
		RequestParams params = new RequestParams(Preferences.BOOKLIST_URL);
		params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
		params.addQueryStringParameter("pageNo", pageNo);
		params.addQueryStringParameter("pageSize", pageSize);

		HttpUtils http = new HttpUtils();
		x.http().post(params, new Callback.CommonCallback<String>(){
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<BookVOBean>() {
				}.getType();
				BookVOBean bean = gson.fromJson(s, type);

				if (bean != null && bean.getCode().equals("SUCCESS")) {
					list.clear();
					list.addAll(bean.getData());
					Collections.reverse(list);// 反向

					books.addAll(list);

					adapter.setCancel_position(size);
					adapter.setList(books);
					adapter.notifyDataSetChanged();
				} else {
					ActivityUtils.showToastForFail(BookAllActivity.this, "加载失败," + bean.getCodeInfo());
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				ActivityUtils.showToastForFail(BookAllActivity.this, "信息获取失败");
			}

			@Override
			public void onCancelled(CancelledException e) {

			}

			@Override
			public void onFinished() {

			}
		});
		return list;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadCancelBook();
	}
}
