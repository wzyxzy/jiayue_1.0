package com.jiayue;

import java.io.File;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayue.constants.Preferences;
import com.jiayue.download.TestService;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.dto.base.AttachOne;
import com.jiayue.dto.base.AttachTwo;
import com.jiayue.dto.base.BookVO;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyDbUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.view.camera.AutoFocusActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class SearchActivity extends BaseActivity implements OnClickListener {
	DisplayImageOptions options;
	private ImageView iv_splash;
	private EditText et_input;
	private Button btn_english;
	private Button btn_one;
	private Button btn_two;
	private Button btn_three;
	private Button btn_four;
	private Button btn_seven;
	private Button btn_five;
	private Button btn_eight;
	private Button btn_nine;
	private Button btn_zero;
	private Button btn_dian;
	private Button btn_six;
	boolean flag = false;
	private Button btn_delete;
	private Button btn_confirm;
	private TextView tv_bookname;
	private TextView tv_desc;
	private TextView tv_publish;
	private TextView tv_photo;
	// private ImageView iv_space;
	private String bookName;
	private String image_url;
	private BookVO bookVO;
	private String bookId;

	private int cover_normal;
	private int search_btn_confirm;
	private int search_btn_cancel;

	private Button btn_header_right;
	String TAG = getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search);
		cover_normal = getResources().getIdentifier("cover_normal", "drawable", getPackageName());
		search_btn_confirm = getResources().getIdentifier("search_btn_confirm", "drawable", getPackageName());
		search_btn_cancel = getResources().getIdentifier("search_btn_cancel", "drawable", getPackageName());
		options = new DisplayImageOptions.Builder().showStubImage(cover_normal)
		// 设置图片下载期间显示的图片
				.showImageForEmptyUri(cover_normal)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(cover_normal)
				// 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
		image_url = getIntent().getStringExtra("image_url");
		bookId = getIntent().getStringExtra("bookId");
		bookName = getIntent().getStringExtra("bookname");
		bookVO = (BookVO) (getIntent().getBundleExtra("bundle").getSerializable("bookVO"));
		initView();
	}

	Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					DialogUtils.dismissMyDialog();

					ActivityUtils.readFile(SearchActivity.this, bookId, msg.getData().getString("fileName"), msg.getData().getString("fileType"), msg.getData().getString("bookName"));
					break;
				default:
					break;
			}
			return false;
		}
	});
	private TextView tv_header_title;

	public void Search(String id) {
		DbManager db = MyDbUtils.getOneAttachDb(this);
		try {
			final AttachOne attachOne = db.selector(AttachOne.class).where("attach_flag", "=", id).and("bookId", "=", bookId).findFirst();
			if (attachOne != null && attachOne.getAttachOneType() != null && attachOne.getAttachOneSaveName() != null) {
				String attachOneSaveName = attachOne.getAttachOneSaveName();
				String attachOneName = attachOne.getAttachOneName();
				String attachOneType = attachOne.getAttachOneType();
				String path = attachOne.getAttachOnePath();
				String new_name = attachOne.getAttachOneName();
				if (new_name.startsWith("图")) {
					new_name = new_name.replace("图", "pic");
				}
				Log.i("SearchActivity", "attach=" + attachOneName + "." + attachOneType + "|attachSaveName=" + attachOneSaveName + "------path=" + path);
				if (attachOneType.equalsIgnoreCase("zip") && ActivityUtils.isExistByName(bookId, attachOne.getAttachOneName())) {// flash
					String fileName = attachOneName + File.separator + "Index.html";
					ActivityUtils.readFile(this, bookId, fileName, "html", bookName);
				} else if (ActivityUtils.isExistByName(bookId, new_name)) {// 3d
					String filepath = "";
					if (attachOneType.equals("frame")) {
						filepath = "file:///android_asset/keyFrame_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
					} else if (attachOneType.equals("skeleton")) {
						filepath = "file:///android_asset/animation_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
					} else if (attachOneType.equals("blender")) {
						filepath = "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".html?no_social";
					}
					ActivityUtils.readURL(this, bookId, filepath, true);
				} else {
					if (!ActivityUtils.isExistByName(bookId, attachOneSaveName)) {

						DialogUtils.showMyDialog(SearchActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "文件同步", attachOneName + ",该文件未同步，是否开始同步？", new OnClickListener() {
							@Override
							public void onClick(View v) {
								ActivityUtils.showToast(SearchActivity.this, "加入下载列表");
								if (attachOne.getAttachOneType().endsWith("lrc")) {
									downLoadFile(attachOne.getAttachOnePath() + attachOne.getAttachOneSaveName(), attachOne.getAttachOneName() + "." + attachOne.getAttachOneType(), attachOne
											.getAttachOneName() + "." + attachOne.getAttachOneType());
								} else {
									downLoadFile(attachOne.getAttachOnePath() + attachOne.getAttachOneSaveName(), attachOne.getAttachOneSaveName(), attachOne.getAttachOneName() + "." + attachOne
											.getAttachOneType());
								}
								DialogUtils.dismissMyDialog();
							}
						});

						// ActivityUtils.showToastForFail(this,
						// attachOneName+",下载之后您就可以观看了呦!");
						// btn_confirm.setBackgroundColor(getResources().getColor(R.color.background));
					} else {
						unLockFile(attachOneSaveName, "copy_" + attachOneSaveName, attachOneType, attachOneName);
					}
				}
			} else {
				Search2(id);
			}
		} catch (DbException e) {
			Search2(id);
			e.printStackTrace();
		}

	}

	private void Search2(String id) {
		DbManager db2 = MyDbUtils.getTwoAttachDb(this);
		try {
			final AttachTwo attachTwo = db2.selector(AttachTwo.class).where("attchtwo_flag", "=", id).and("bookId", "=", bookId).findFirst();
			if (attachTwo != null) {
				String attachTwoSaveName = attachTwo.getAttachTwoSaveName();
				String attachTwoName = attachTwo.getAttachTwoName();
				String attachTwoType = attachTwo.getAttachTwoType();
				String new_name = attachTwo.getAttachTwoName();
				if (new_name.startsWith("图")) {
					new_name = new_name.replace("图", "pic");
				}
				Log.i("SearchActivity", "attach=" + attachTwoName + "." + attachTwoType + "|attachSaveName=" + attachTwoSaveName);
				if (attachTwoType.equalsIgnoreCase("zip") && ActivityUtils.isExistByName(bookId, attachTwo.getAttachTwoName())) {
					String fileName = attachTwoName + File.separator + "Index.html";
					ActivityUtils.readFile(this, bookId, fileName, "html", bookName);
				} else if (ActivityUtils.isExistByName(bookId, new_name)) {
					String filepath = "";
					if (attachTwoType.equals("frame")) {
						filepath = "file:///android_asset/keyFrame_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
					} else if (attachTwoType.equals("skeleton")) {
						filepath = "file:///android_asset/animation_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
					} else if (attachTwoType.equals("blender")) {
						filepath = "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".html?no_social";
					}
					ActivityUtils.readURL(this, bookId, filepath, true);
				} else {
					if (!ActivityUtils.isExistByName(bookId, attachTwoSaveName)) {
						DialogUtils.showMyDialog(SearchActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "文件同步", attachTwoName + ",该文件未同步，是否开始同步？", new OnClickListener() {
							@Override
							public void onClick(View v) {
								ActivityUtils.showToast(SearchActivity.this, "加入下载列表");
								if (attachTwo.getAttachTwoType().endsWith("lrc")) {
									downLoadFile(attachTwo.getAttachTwoPath() + attachTwo.getAttachTwoSaveName(), attachTwo.getAttachTwoName() + "." + attachTwo.getAttachTwoType(), attachTwo
											.getAttachTwoName() + "." + attachTwo.getAttachTwoType());
								} else {
									downLoadFile(attachTwo.getAttachTwoPath() + attachTwo.getAttachTwoSaveName(), attachTwo.getAttachTwoSaveName(), attachTwo.getAttachTwoName() + "." + attachTwo
											.getAttachTwoType());
								}
								DialogUtils.dismissMyDialog();
							}
						});
						// ActivityUtils.showToastForFail(this,
						// attachTwoName+"下载之后您就可以观看了呦!");
						// btn_confirm.setBackgroundResource(search_btn_confirm);
					} else {
						unLockFile(attachTwoSaveName, "copy_" + attachTwoSaveName, attachTwoType, attachTwoName);
					}
				}
			} else {
				ActivityUtils.showToastForFail(this, "很遗憾，您搜索的资源不存在哦!");
				btn_confirm.setBackgroundResource(search_btn_confirm);
			}

		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public void unLockFile(final String soureFileName, final String saveFileName, final String fileType, final String bookName) {
		DialogUtils.showMyDialog(SearchActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载资源", null);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ActivityUtils.unLock(bookId, soureFileName, saveFileName);
				Message msg = new Message();
				msg.what = 1;
				Bundle bundle = new Bundle();
				bundle.putString("fileName", saveFileName);
				bundle.putString("fileType", fileType);
				bundle.putString("bookName", bookName);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 下载附件
	 * 
	 * @param url
	 *            附件的服务器地址
	 * @param saveName
	 *            附件要保存的名字
	 * @param fileName
	 *            附件要现实的名字
	 */
	public void downLoadFile(String url, String saveName, String fileName) {

		String path = getPath(Preferences.FILE_DOWNLOAD_URL + url);
		Log.i("BookSynActivity", "url=" + path);
		Log.i("BookSynActivity", "saveName=" + saveName);
		Log.i("BookSynActivity", "fileName=" + fileName);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Intent intent = new Intent(this, TestService.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			DocInfo d = new DocInfo();
			d.setUrl(path);
			d.setDirectoty(false);
			d.setName(saveName);
			d.setBookId(bookId);
			d.setBookName(fileName);
			bundle.putSerializable("info", d);
			intent.putExtra("bundle", bundle);
			startService(intent);
		} else {
			ActivityUtils.showToastForFail(this, "未检测到SD卡");
		}
	}

	private String getPath(String url) {
		String path = url + "&userId=" + UserUtil.getInstance(this).getUserId() + "&bookId=" + bookId;
		Log.d(TAG, "download path===========" + path);
		return path;
	}

	String imageFilePath;

	public final int CMD_PHOTO = 888;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		// case 103:
		// if (data == null)
		// return;
		// Bitmap bm = null;
		// // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
		// ContentResolver resolver = getContentResolver();
		//
		// try {
		// Uri originalUri = data.getData(); // 获得图片的uri
		// bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); //
		// 显得到bitmap图片
		//
		// // 这里开始的第二部分，获取图片的路径：
		//
		// String[] proj = { MediaStore.Images.Media.DATA };
		//
		// // 好像是android多媒体数据库的封装接口，具体的看Android文档
		// @SuppressWarnings("deprecation")
		// Cursor cursor = managedQuery(originalUri, proj, null, null, null);
		// // 按我个人理解 这个是获得用户选择的图片的索引值
		// int column_index =
		// cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// // 将光标移至开头 ，这个很重要，不小心很容易引起越界
		// cursor.moveToFirst();
		// // 最后根据索引值获取图片路径
		// String path = cursor.getString(column_index);
		// try {
		// searchPhoto(path);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// // iv_photo.setImageURI(originalUri);
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void initView() {
		iv_splash = (ImageView) findViewById(R.id.iv_splash);
		tv_bookname = (TextView) findViewById(R.id.tv_bookname);
		tv_bookname.setText(bookName);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_desc.setText(bookVO.getBookIntro());
		et_input = (EditText) findViewById(R.id.et_input);
		ActivityUtils.hideSoftInputMethod(et_input, SearchActivity.this);
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("搜索");
		btn_header_right = (Button) findViewById(R.id.btn_header_right);
		btn_header_right.setVisibility(View.INVISIBLE);

		btn_header_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jiayue/filename.jpg";
				Intent it = new Intent(SearchActivity.this, AutoFocusActivity.class);// 跳转到相机Activity
				it.putExtra(AutoFocusActivity.BOOKID, bookId);
				startActivityForResult(it, 102);
			}
		});

		tv_publish = (TextView) findViewById(R.id.tv_publish);
		tv_publish.setText(bookVO.getBookPublish());
		imageLoader.displayImage(image_url, iv_splash, options);
		btn_one = (Button) findViewById(R.id.one);
		btn_two = (Button) findViewById(R.id.two);
		btn_three = (Button) findViewById(R.id.three);
		btn_four = (Button) findViewById(R.id.four);
		btn_five = (Button) findViewById(R.id.five);
		btn_six = (Button) findViewById(R.id.six);
		btn_seven = (Button) findViewById(R.id.seven);
		btn_eight = (Button) findViewById(R.id.eight);
		btn_nine = (Button) findViewById(R.id.nine);
		btn_zero = (Button) findViewById(R.id.zero);
		btn_dian = (Button) findViewById(R.id.dian);
		btn_english = (Button) findViewById(R.id.english);
		btn_delete = (Button) findViewById(R.id.delete);
		btn_confirm = (Button) findViewById(R.id.confirm);
		tv_photo = (TextView) findViewById(R.id.tv_photo);
		// iv_space = (ImageView) findViewById(R.id.iv_space);
		et_input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				btn_confirm.setClickable(true);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!TextUtils.isEmpty(et_input.getText().toString().trim())) {
					btn_delete.setBackgroundResource(search_btn_confirm);
				} else {
					btn_delete.setBackgroundResource(search_btn_cancel);
				}
			}
		});
		btn_dian.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				et_input.setText(et_input.getText() + ".");
				et_input.setSelection((et_input.getText() + "1").length() - 1);
				flag = !flag;
				return true;
			}
		});

		setOnclick(btn_zero, btn_one, btn_two, btn_three, btn_four, btn_five, btn_six, btn_seven, btn_eight, btn_nine, btn_dian, btn_english, btn_delete, btn_confirm);
	}

	/**
	 * Title: setOnclick Description: 设置OnClickListener
	 * 
	 * @param v
	 */
	public void setOnclick(View... v) {
		for (int i = 0; i < v.length; i++) {
			v[i].setOnClickListener(this);
		}
	}

	public void space(View v) {
		if (flag) {
			tv_photo.setText("图");
			flag = !flag;
			btn_confirm.setClickable(true);
		} else {
			tv_photo.setText("表");
			flag = !flag;
			btn_confirm.setClickable(true);
		}
	}

	public void btnBack(View v) {

		this.finish();
	}

	private void keyboard(String key) {
		et_input.setText(et_input.getText() + key);
		et_input.setSelection((et_input.getText() + "1").length() - 1);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.one:
				keyboard("1");
				break;
			case R.id.two:
				keyboard("2");
				break;
			case R.id.three:
				keyboard("3");
				break;
			case R.id.four:
				keyboard("4");
				break;
			case R.id.five:
				keyboard("5");
				break;
			case R.id.six:
				keyboard("6");
				break;
			case R.id.seven:
				keyboard("7");
				break;
			case R.id.eight:
				keyboard("8");
				break;
			case R.id.nine:
				keyboard("9");
				break;
			case R.id.zero:
				keyboard("0");
				break;
			case R.id.dian:
				keyboard("-");
				break;
			case R.id.english:
				// if (flag) {
				// english.setBackground(R.drawable.);
				// flag=!flag;
				// }else {
				// english.setBackground(R.drawable.);
				// flag=!flag;
				// }
				break;
			case R.id.delete:
				if ((et_input.getText() + "1").length() - 2 != -1) {
					et_input.setText(et_input.getText().delete((et_input.getText() + "1").length() - 2, (et_input.getText() + "1").length() - 1));
				}
				btn_confirm.setClickable(true);
				// btn_confirm.setBackgroundColor(getResources().getColor(
				// R.color.background));
				btn_confirm.setBackgroundResource(search_btn_confirm);
				et_input.setSelection(et_input.getText().length());
				break;
			case R.id.confirm:
				String type = tv_photo.getText().toString().trim();
				String id = et_input.getText().toString().trim();
				btn_confirm.setClickable(false);
				Log.i("type", type + id);
				Search(type + id);
			default:
				break;
		}
	}

	@Override
	protected void onResume() {
		btn_confirm.setClickable(true);
		btn_confirm.setBackgroundResource(search_btn_confirm);
		super.onResume();
	}

}
