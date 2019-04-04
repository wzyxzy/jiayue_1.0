package com.jiayue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.jiayue.R;
import com.jiayue.dto.base.BookPage;
import com.jiayue.dto.base.TextPart;
import com.jiayue.service.BookReadService;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;
import com.jiayue.util.StringUtil;
import com.jiayue.view.XListViewOfRead;
import com.jiayue.view.XListViewOfRead.IXListViewListener;

@SuppressLint("NewApi")
public class BookReadActivity extends AbsListViewBaseActivity implements IXListViewListener {
	String imagePath;
	String xmlPath;
	BookReadService service;
	MyAdapter adapter;

	DisplayImageOptions options;
	List<BookPage> bookPages = new ArrayList<BookPage>();
	int idParent = 1000;
	int pageNO = 1;
	int gotoPageNO = -1;
	int search_count = 0;
	BookPage bookPage = null;
	String bookName = "";
	TextView tv_pageNO;
	String search_content;

	Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					onLoad();

					break;
				case 2:
					bookPages.clear();
					bookPages.add(bookPage);
					bookPage = null;
					pageNO++;
					onLoad();
					SPUtility.putSPInteger(BookReadActivity.this, bookName, pageNO);
					break;
				case 3:
					bookPages.clear();
					bookPages.add(bookPage);
					bookPage = null;
					pageNO--;
					onLoad();
					SPUtility.putSPInteger(BookReadActivity.this, bookName, pageNO);
					break;
				case 4:
					bookPages.clear();
					bookPages.add(bookPage);
					bookPage = null;
					pageNO = gotoPageNO;
					onLoad();
					SPUtility.putSPInteger(BookReadActivity.this, bookName, pageNO);
					break;
				case 5:
					ActivityUtils.showToast(BookReadActivity.this, "未搜到相关内容");
					break;
				case 6:
					bookPages.clear();
					bookPages.add(bookPage);
					bookPage = null;
					ActivityUtils.showToast(BookReadActivity.this, "搜到" + search_count + "处相关内容");
					onLoad();
					SPUtility.putSPInteger(BookReadActivity.this, bookName, pageNO);
					break;
				case 7:
					bookPages.clear();
					bookPages.add(bookPage);
					bookPage = null;
					pageNO = gotoPageNO;
					searchContent(search_content);
					SPUtility.putSPInteger(BookReadActivity.this, bookName, pageNO);
					break;
				default:
					break;
			}
			return false;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DialogUtils.dismissMyDialog();
		getApplicationContext().addActivity(this);

		setContentView(R.layout.book_read);
		int cover_normal = getResources().getIdentifier("cover_normal", "drawable", getPackageName());
		int jpg = getResources().getIdentifier("jpg", "drawable", getPackageName());
		options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY_STRETCHED).showStubImage(cover_normal) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(jpg) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(jpg) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		xmlPath = getIntent().getStringExtra("bookPath");
		imagePath = getIntent().getStringExtra("imagePath");
		if (!TextUtils.isEmpty(getIntent().getStringExtra("bookName")))
			bookName = getIntent().getStringExtra("bookName");
		Log.i("BookReadActivity", "xmlPath =" + xmlPath + "imagePath =" + imagePath + "bookName =" + bookName);
		service = new BookReadService();
		initView();
	}

	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

	private void initView() {

		BookPage bookPage = null;
		tv_pageNO = (TextView) findViewById(R.id.tv_pageNO);
		TextView tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText(StringUtil.subString(bookName, 8, "..."));
		try {
			int bookNumber = SPUtility.getSPInteger(BookReadActivity.this, bookName);
			if (bookNumber >= 1) {
				bookPage = service.getBookPageInfo(xmlPath, bookNumber, imagePath);
			} else {
				bookPage = service.getBookPageInfo(xmlPath, pageNO, imagePath);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != bookPage) {
			bookPages.clear();
			bookPages.add(bookPage);
			tv_pageNO.setText(bookPage.getPageNo() + "");
		}
		listView = (XListViewOfRead) findViewById(R.id.lv);
		((XListViewOfRead) listView).setPullLoadEnable(true);
		adapter = new MyAdapter(this);
		listView.setAdapter(adapter);
		((XListViewOfRead) listView).setXListViewListener(this);

		/*
		 * for(TextPart part:bookPage.getParts()){ Log.i("other",
		 * part.getType()+""); Log.i("other", part.getContent()); }
		 */

	}

	public void btnBack(View v) {
		BookReadActivity.this.finish();
	}

	@Override
	protected void onDestroy() {
		getApplicationContext().removeActivity(this);

		ActivityUtils.deleteBookFormSD(xmlPath);
		super.onDestroy();
	}

	@Override
	public void onRefresh() {
		pageNO = SPUtility.getSPInteger(BookReadActivity.this, bookName);
		if (pageNO > 1) {
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						bookPage = service.getBookPageInfo(xmlPath, pageNO - 1, imagePath);
						Message msg = new Message();
						if (null != bookPage) {
							msg.what = 3;
						} else {
							msg.what = 1;
						}
						mHandler.sendMessage(msg);

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}

				}
			}).start();
		} else {
			ActivityUtils.showToast(BookReadActivity.this, "已到第一页");
			((XListViewOfRead) listView).stopRefresh();
		}
	}

	private void onLoad() {
		adapter.notifyDataSetChanged();
		((XListViewOfRead) listView).invalidate();
		((XListViewOfRead) listView).stopRefresh();
		((XListViewOfRead) listView).stopLoadMore(false);

		tv_pageNO.setText(pageNO + "");

		// lv_books.setRefreshTime("刚刚");
	}

	@Override
	public void onLoadMore() {

		if (pageNO < service.totalPage) {
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						bookPage = service.getBookPageInfo(xmlPath, pageNO + 1, imagePath);
						Message msg = new Message();
						if (null != bookPage) {
							msg.what = 2;
						} else {
							msg.what = 1;
						}
						mHandler.sendMessage(msg);

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}

				}
			}).start();
		} else {
			ActivityUtils.showToast(BookReadActivity.this, "已到最后一页");
			((XListViewOfRead) listView).stopLoadMore(true);
			((XListViewOfRead) listView).setPullLoadEnable(false);
		}
	}

	class MyAdapter extends BaseAdapter {
		View view;
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		public MyAdapter(Context context) {
		}

		@Override
		public int getCount() {
			return bookPages.size();
		}

		@Override
		public Object getItem(int position) {
			return bookPages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			/*
			 * RelativeLayout rl = new RelativeLayout(BookReadActivity.this);
			 * RelativeLayout.LayoutParams lp=new
			 * RelativeLayout.LayoutParams(LayoutParams
			 * .FILL_PARENT,LayoutParams.WRAP_CONTENT); for(int
			 * i=0;i<bookPages.get(position).getParts().size();i++){ if(i==0){
			 * TextView tv_text = new TextView(BookReadActivity.this);
			 * lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
			 * RelativeLayout.TRUE); lp.addRule(RelativeLayout.ALIGN_PARENT_TOP,
			 * RelativeLayout.TRUE); tv_text.setId(idParent);
			 * tv_text.setText(bookPages
			 * .get(position).getParts().get(i).getContent());
			 * rl.addView(tv_text,lp); }else{ TextView tv_text = new
			 * TextView(BookReadActivity.this); lp.addRule(RelativeLayout.BELOW,
			 * idParent+i-1); lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
			 * RelativeLayout.TRUE); tv_text.setId(idParent+i);
			 * tv_text.setText(bookPages
			 * .get(position).getParts().get(i).getContent());
			 * rl.addView(tv_text,lp); } }
			 */
			// LinearLayout view_ll = new LinearLayout(BookReadActivity.this);
			// view_ll.setOrientation(LinearLayout.VERTICAL);

			LinearLayout ll = new LinearLayout(BookReadActivity.this);
			ll.setClickable(true);
			ll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					BookReadActivity.this.openOptionsMenu();
				}
			});
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.setScrollbarFadingEnabled(true);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams lp_ll_image = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams lp_image = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

			lp_image.setMargins(0, 0, 0, 5);
			for (int i = 0; i < bookPages.get(position).getParts().size(); i++) {
				if (bookPages.get(position).getParts().get(i).getType() == MyPreferences.P_TYPE) {
					TextView tv_text = new TextView(BookReadActivity.this);
					tv_text.setTextColor(getResources().getColor(R.color.read_text_color));
					tv_text.setTextSize(18);
					tv_text.setLineSpacing(2, 1.2f);
					String content = bookPages.get(position).getParts().get(i).getContent();
					content = ActivityUtils.ToDBC(content);
					tv_text.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;" + content));
					lp.setMargins(0, 1, 0, 2);
					ll.addView(tv_text, lp);
				} else if (bookPages.get(position).getParts().get(i).getType() == MyPreferences.IMG_TYPE) {
					LinearLayout ll_image = new LinearLayout(BookReadActivity.this);
					int bg_image = getResources().getIdentifier("bg_image", "drawable", getPackageName());
					ll_image.setBackgroundResource(bg_image);
					ll_image.setGravity(Gravity.CENTER);
					ImageView iv = new ImageView(BookReadActivity.this);
					iv.setScaleType(ImageView.ScaleType.FIT_XY);
					Log.i("BookReadActivity", "file://" + bookPages.get(position).getParts().get(i).getContent());
					imageLoader.displayImage("file://" + bookPages.get(position).getParts().get(i).getContent(), iv, options, animateFirstListener);
					lp_image.setMargins(0, 10, 0, 0);
					ll_image.addView(iv, lp_image);

					lp_ll_image.setMargins(0, 10, 0, 0);
					ll.addView(ll_image, lp_ll_image);

				}
			}
			TextView tv_text = new TextView(BookReadActivity.this);

			// tv_text.setText("第"+bookPages.get(position).getPageNo()+"页");
			// tv_text.setGravity(Gravity.CENTER);
			lp.setMargins(0, 2, 0, 2);
			ll.addView(tv_text, lp);
			// LinearLayout.LayoutParams view_lp=new
			// LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			// view_lp.setMargins(0, 0, 0, 5);
			// view_ll.addView(ll,view_lp);
			return ll;
		}

	}

	/**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 在这里做你想做的事情
			super.openOptionsMenu(); // 调用这个，就可以弹出菜单
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// 在这里做你想做的事情
		super.onOptionsMenuClosed(menu);
	}

	@Override
	public void openOptionsMenu() {
		super.openOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 1: // do something here
				showGotoPageDialog();
				break;
			case 2: // do something here
				showSearchDialog();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void gotoPage() {
		if (gotoPageNO >= 1 && gotoPageNO <= service.totalPage) {
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						bookPage = service.getBookPageInfo(xmlPath, gotoPageNO, imagePath);
						Message msg = new Message();
						if (null != bookPage) {
							msg.what = 4;
						} else {
							msg.what = 1;
						}
						mHandler.sendMessage(msg);

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}

				}
			}).start();
		} else {
			ActivityUtils.showToastForFail(BookReadActivity.this, "无此页面");
		}
	}

	public void gotoPageForSeach() {
		if (gotoPageNO >= 1 && gotoPageNO <= service.totalPage) {
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						bookPage = service.getBookPageInfo(xmlPath, gotoPageNO, imagePath);
						Message msg = new Message();
						if (null != bookPage) {
							msg.what = 7;
						} else {
							msg.what = 1;
						}
						mHandler.sendMessage(msg);

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}

				}
			}).start();
		} else {
			ActivityUtils.showToastForFail(BookReadActivity.this, "无此页面");
		}
	}

	public void searchContent(final String search_content) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					search_count = 0;
					bookPage = service.getBookPageInfo(xmlPath, pageNO, imagePath);
					List<TextPart> textParts = bookPage.getParts();
					for (TextPart textPart : textParts) {
						if (textPart.getType() == MyPreferences.P_TYPE && textPart.getContent().contains(search_content)) {
							getSearchCount(textPart.getContent(), search_content);

							String result = textPart.getContent().replace(search_content, "<font color=\"#ff6600\">" + search_content + "</font>");
							textPart.setContent(result);
						}
					}
					Message msg = new Message();
					if (search_count > 0) {
						bookPage.setParts(textParts);
						msg.what = 6;
						mHandler.sendMessage(msg);
					} else {
						msg.what = 5;
						mHandler.sendMessage(msg);
					}

				} catch (Exception e) {
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}

			}
		}).start();
	}

	public void getSearchCount(String content, String search_content) {
		if (content.indexOf(search_content) == -1) {
			return;
		} else if (content.indexOf(search_content) != -1) {

			search_count++;
			getSearchCount(content.substring(content.indexOf(search_content) + search_content.length()), search_content);
			return;
		}
		return;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		int group1 = 1;
		// int group2 = 2;
		menu.add(group1, 1, 1, "跳转");
		int i = SPUtility.getSPInteger(BookReadActivity.this, bookName);
		if (i == -1) {
			i = 1;
		}
		menu.add(group1, 0, 2, +i + "/" + service.totalPage);
		menu.add(group1, 2, 2, "搜索");
		return true;
	}

	public void showGotoPageDialog() {
		final Dialog dialog = new Dialog(this, R.style.my_dialog);
		dialog.setContentView(R.layout.goto_dialog);
		dialog.setCancelable(true);
		final EditText et_pageNO = (EditText) dialog.findViewById(R.id.et_pageNO);
		TextView tv_pd_title = (TextView) dialog.findViewById(R.id.tv_pd_title);
		tv_pd_title.setText("请输入跳转页数(" + pageNO + "/" + service.totalPage + "):");
		Button button_goto01 = (Button) dialog.findViewById(R.id.btn_right);
		Button button_goto02 = (Button) dialog.findViewById(R.id.btn_left);
		button_goto01.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String number = et_pageNO.getText().toString().trim();
				if (number == null || number.equals("")) {
					ActivityUtils.showToast(BookReadActivity.this, "请输入页码~");
					return;
				} else {

					int pageNumber;
					try {
						pageNumber = Integer.parseInt(number);
						gotoPageNO = pageNumber;
						gotoPage();
					} catch (NumberFormatException e) {
						e.printStackTrace();
						ActivityUtils.showToastForFail(BookReadActivity.this, "提示：您输入的范围超限！");
					}

				}
				dialog.cancel();
			}
		});
		button_goto02.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}

	public void showSearchDialog() {
		final Dialog dialog = new Dialog(this, R.style.my_dialog);
		dialog.setContentView(R.layout.search_dialog);
		dialog.setCancelable(true);
		final EditText et_search_content = (EditText) dialog.findViewById(R.id.et_search_content);
		Button button_goto01 = (Button) dialog.findViewById(R.id.btn_pre);
		Button button_goto02 = (Button) dialog.findViewById(R.id.btn_current);
		Button button_goto03 = (Button) dialog.findViewById(R.id.btn_next);
		button_goto01.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				search_content = et_search_content.getText().toString().trim();
				if (search_content == null || search_content.equals("")) {
					ActivityUtils.showToast(BookReadActivity.this, "请输入搜索内容~");
					return;
				} else {
					gotoPageNO = pageNO - 1;
					gotoPageForSeach();
					// searchContent(search_content);
				}
				// dialog.cancel();
			}
		});
		button_goto03.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				search_content = et_search_content.getText().toString().trim();
				if (search_content == null || search_content.equals("")) {
					ActivityUtils.showToast(BookReadActivity.this, "请输入搜索内容~");
					return;
				} else {
					gotoPageNO = pageNO + 1;
					gotoPageForSeach();
					// searchContent(search_content);
				}
				// dialog.cancel();
			}
		});
		button_goto02.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String search_content = et_search_content.getText().toString().trim();
				if (search_content == null || search_content.equals("")) {
					ActivityUtils.showToast(BookReadActivity.this, "请输入搜索内容~");
					return;
				} else {
					searchContent(search_content);
				}
				// dialog.cancel();
			}
		});
		/*
		 * button_goto01.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { dialog.cancel(); } });
		 */
		dialog.setCancelable(true);
		dialog.show();
	}
}
