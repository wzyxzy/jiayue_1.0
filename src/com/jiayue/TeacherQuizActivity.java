package com.jiayue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.CommonAdapter;
import com.jiayue.adapter.ViewHolder;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.BookSeletctBean;
import com.jiayue.dto.base.PaperInfoBean;
import com.jiayue.dto.base.TestPaperBean;
import com.jiayue.dto.base.TestQuestionsBean;
import com.jiayue.dto.base.TextOneBookDirsBean;
import com.jiayue.dto.base.TextTwoBookDirsBean;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-10-16 下午2:10:41 项目名称：jiayue
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：TeacherBindLogin.java 类说明：
 *        ------------------------------------------------------------------
 */
/**
 * Title: TeacherQuizActivity Description: Company: btpd
 * 
 * @author Ping Wang
 * @date 2015-10-27
 */
@SuppressWarnings("static-access")
@SuppressLint("InflateParams")
public class TeacherQuizActivity extends BaseActivity implements OnClickListener {
	private LinearLayout btn_quiz;
	private LinearLayout quiz_list;
	private TextView tv_header_title;
	private View left_line;
	private View right_line;
	private ViewPager viewPager;
	private ArrayList<View> pageview;

	private Button book_select;
	private Button exam_select;
	private Button one_level;
	private Button two_level;

	// private Button exam_code;
	private EditText exam_name;
	private ListView listview;
	private Button send_exam;

	private TextView exam_number;
	private TextView exam_designation;
	private Button curriculum_name;
	private CheckBox checkbox_slelect;
	private EditText et_time;
	private TextView tv_suggest;
	private CommonAdapter<TestQuestionsBean.Data.SelectQuesitons> adapter;

	private static final int STEP1 = 123;
	private static final int STEP2 = 124;
	private static final int STEP3 = 125;
	private static final int STEP4 = 126;
	private static final int STEP5 = 127;
	private View view1;
	private View view2;
	private View view3;
	private LayoutInflater inflater;
	private ListView listview1;

	private List<BookSeletctBean.Data> bookSeletcts;
	private List<TextOneBookDirsBean.Data> oneBookDirs;
	private List<TextTwoBookDirsBean.Data> twoBookDirs;
	private List<TestQuestionsBean.Data.SelectQuesitons> selectQuesitons;
	private List<TestPaperBean.Data> papers;
	private TestQuestionsBean.Data questions;
	private String bookId;
	private String dirId1;
	private String dirId2;
	private String paperId;
	private String testName;
	private String claTesPapId;
	private String paperCode;
	private String courseId;
	private int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_quiz);
		initViews();
	}

	/**
	 * <p>
	 * Title
	 * </p>
	 * : initViews Description: 初始化view对象
	 */
	private void initViews() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		left_line = (View) findViewById(R.id.left_line);
		right_line = (View) findViewById(R.id.right_line);
		btn_quiz = (LinearLayout) findViewById(R.id.btn_quiz);
		quiz_list = (LinearLayout) findViewById(R.id.quiz_list);
		listview1 = (ListView) findViewById(R.id.listview);
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText("随堂测试");
		loadViewPager();
		setOnclick(btn_quiz, quiz_list, book_select, exam_select, one_level, two_level, /*
																						 * save_exam
																						 * ,
																						 */send_exam, curriculum_name);
	}

	private void loadViewPager() {

		inflater = getLayoutInflater();

		view1 = inflater.inflate(R.layout.teacher_quiz_item01, null);
		book_select = (Button) view1.findViewById(R.id.book_select);
		exam_select = (Button) view1.findViewById(R.id.exam_select);
		one_level = (Button) view1.findViewById(R.id.one_level);
		two_level = (Button) view1.findViewById(R.id.two_level);

		view2 = inflater.inflate(R.layout.teacher_quiz_item02, null);
		// exam_code = (Button) view2.findViewById(R.id.exam_code);
		exam_name = (EditText) view2.findViewById(R.id.exam_name);
		listview = (ListView) view2.findViewById(R.id.listview);

		// save_exam = (Button) view2.findViewById(R.id.save_exam);
		send_exam = (Button) view2.findViewById(R.id.send_exam);

		view3 = inflater.inflate(R.layout.teacher_quiz_item03, null);
		exam_number = (TextView) view3.findViewById(R.id.exam_number);
		exam_designation = (TextView) view3.findViewById(R.id.exam_designation);
		curriculum_name = (Button) view3.findViewById(R.id.curriculum_name);
		checkbox_slelect = (CheckBox) view3.findViewById(R.id.checkbox_slelect);
		et_time = (EditText) view3.findViewById(R.id.et_time);
		tv_suggest = (TextView) view3.findViewById(R.id.tv_suggest);
		pageview = new ArrayList<View>();
		pageview.add(view1);
		pageview.add(view2);
		pageview.add(view3);
		loadViewPagerAdapter();

	}

	private void loadViewPagerAdapter() {
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			// 获取当前窗体界面数
			public int getCount() {
				return pageview.size();
			}

			@Override
			// 断是否由对象生成界面
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			// 是从ViewGroup中移出当前View
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager) arg0).removeView(pageview.get(arg1));
			}

			// 返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
			public Object instantiateItem(View arg0, int arg1) {
				((ViewPager) arg0).addView(pageview.get(arg1));

				return pageview.get(arg1);
			}

		};
		viewPager.setAdapter(mPagerAdapter);
	}

	private void loadListViewAdapter() {
		selectQuesitons = questions.getSelectQuesitons();
		adapter = new CommonAdapter<TestQuestionsBean.Data.SelectQuesitons>(this, selectQuesitons, R.layout.teacher_exam_item) {

			@Override
			public void convert(ViewHolder helper, TestQuestionsBean.Data.SelectQuesitons item, final int postion) {

				helper.setText(R.id.tv_exam_question, item.getName());
				List<TestQuestionsBean.Data.SelectQuesitons.SelectOptions> selectOptions = item.getSelectOptions();
				StringBuffer bf = new StringBuffer();
				for (int i = 0; i < selectOptions.size(); i++) {
					bf.append(selectOptions.get(i).getOptionCode() + "." + selectOptions.get(i).getName() + "\n");
				}
				helper.setText(R.id.tv_exam_select, bf.toString());

				helper.setOnClickListener(R.id.checkbox, new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (isSelected.get(postion)) {
							isSelected.put(postion, false);
							setIsSelected(isSelected);
						} else {
							isSelected.put(postion, true);
							setIsSelected(isSelected);
						}
						// ActivityUtils.showToast(TeacherQuizActivity.this,
						// selectQuesitons.get(postion).getName() + postion);

					}

				});

				helper.setIsChecked(R.id.checkbox, getIsSelected().get(postion));
			}
		};
		listview.setAdapter(adapter);
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

	/**
	 * <p>
	 * Title
	 * </p>
	 * : btnLogin Description:确认绑定按钮
	 * 
	 * @param v
	 */
	public void btnLogin(View v) {
		String book = book_select.getText().toString().trim();
		String exam = exam_select.getText().toString().trim();
		String oneLevel = one_level.getText().toString().trim();
		String twoLevel = two_level.getText().toString().trim();
		if (TextUtils.isEmpty(book)) {
			ActivityUtils.showToastForFail(this, "书籍选择不能为空");
			return;
		}
		if (TextUtils.isEmpty(exam)) {
			ActivityUtils.showToastForFail(this, "试卷选择不能为空");
			return;
		}
		if (TextUtils.isEmpty(oneLevel)) {
			ActivityUtils.showToastForFail(this, "一级列表不能为空");
			return;
		}
		if (TextUtils.isEmpty(twoLevel)) {
			ActivityUtils.showToastForFail(this, "二级列表不能为空");
			return;
		}
		if (paperId != null) {
			ActivityUtils.showToast(this, paperId);
			getPaperInfo(paperId);
		}

		viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
	}

	public void btnSend(View v) {
		if (checkbox_slelect.isChecked()) {
			// String time = et_time.getText().toString().trim();
		}
		confirmSend(claTesPapId, courseId);
	}

	/**
	 * <p>
	 * Title
	 * </p>
	 * : btnBack Description: 返回按钮
	 * 
	 * @param v
	 */
	public void btnBack(View v) {
		this.finish();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.btn_quiz:
				left_line.setBackgroundColor(getResources().getColor(R.color.background));
				right_line.setBackgroundColor(getResources().getColor(R.color.login_hint_color));
				viewPager.setVisibility(View.VISIBLE);

				break;
			case R.id.quiz_list:
				viewPager.setVisibility(View.GONE);
				left_line.setBackgroundColor(getResources().getColor(R.color.login_hint_color));
				right_line.setBackgroundColor(getResources().getColor(R.color.background));
				ArrayList<String> list = new ArrayList<>();
				list.add("hh");
				list.add("hh");
				list.add("hh");
				listview1.setAdapter(new CommonAdapter<String>(this, list, R.layout.quiz_list_item) {

					@Override
					public void convert(ViewHolder helper, String item, int postion) {

					}
				});
				listview1.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Intent intent = new Intent(TeacherQuizActivity.this, TeacherTestResultActivity.class);
						startActivity(intent);
					}
				});

				break;
			case R.id.book_select:
				getTeaClaTesTextbook();
				reset(one_level, two_level, exam_select);
				break;
			case R.id.one_level:
				getTextBookOneDirs(bookId);
				reset(two_level, exam_select);
				break;
			case R.id.two_level:
				getTextBookTwoDirs(dirId1);
				reset(exam_select);
				break;
			case R.id.exam_select:
				getTwoDirPapers(dirId2);

				break;
			// case R.id.save_exam :
			// ActivityUtils.showToast(this, "1");
			// break;
			case R.id.send_exam:

				HashMap<Integer, Boolean> hashMap = adapter.getIsSelected();
				StringBuffer sb = new StringBuffer();

				for (int i = 0; i < hashMap.size(); i++) {
					if (hashMap.get(i)) {
						String test_id = selectQuesitons.get(i).getId();
						sb.append(test_id + ",");
						count++;
					}
				}
				testName = exam_name.getText().toString().trim();
				if (!TextUtils.isEmpty(testName)) {
					exam_name.setText(testName);
					exam_designation.setText("试卷名称：" + testName);
				} else {
					ActivityUtils.showToast(TeacherQuizActivity.this, "请输入试题名称");
					return;
				}
				if (sb.length() == 0) {
					ActivityUtils.showToast(TeacherQuizActivity.this, "您还没有选择试题");
				} else {
					String queIds = sb.deleteCharAt(sb.length() - 1).toString();
					tv_suggest.setText("(共" + count + "道题，建议答题时间为" + questions.getUsedTime() + "分钟)");
					ActivityUtils.showToast(TeacherQuizActivity.this, queIds);
					insertPaperInfo(paperId, "1", queIds, testName);

				}

				break;

			case R.id.curriculum_name:
				intent = new Intent(this, TeacherCurriculumActivity.class);

				startActivityForResult(intent, STEP1);

				break;

			default:
				break;
		}

	}

	private void reset(TextView... v) {
		for (int i = 0; i < v.length; i++) {
			v[i].setText(null);
		}

	}

	/**
	 * <p>
	 * Title
	 * </p>
	 * : getTeaClaTesTextbook Description: 获取书籍列表
	 */
	protected void getTeaClaTesTextbook() {
		RequestParams params = new RequestParams(Preferences.GET_TEA_CLA_TES_TEXTBOOK);
		params.addQueryStringParameter("teacherId", "1");

		DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<BookSeletctBean>() {
				}.getType();
				BookSeletctBean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					bookSeletcts = bean.getData();
					Log.i("msg", bookSeletcts.toString());
					if (bookSeletcts != null) {
						Intent intent = new Intent(TeacherQuizActivity.this, TeacherSelectActivity.class);
						intent.putExtra("bookSelect", (Serializable) bookSeletcts);
						intent.putExtra("select", "1");
						startActivityForResult(intent, STEP2);
					}
				} else {
					DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "信息获取失败", bean.getCodeInfo(), null);
				}

			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherQuizActivity.this, "操作失败，请检查网络");
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
	 * <p>
	 * Title
	 * </p>
	 * : getTextBookOneDirs Description: 获取一级目录
	 * 
	 * @param bookId
	 */
	protected void getTextBookOneDirs(String bookId) {
		if (bookId == null) {
			ActivityUtils.showToast(this, "请选择书籍");
			return;
		}

		RequestParams params = new RequestParams(Preferences.GET_TEXT_ONE_DIRS);
		params.addQueryStringParameter("id", bookId);

		DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<TextOneBookDirsBean>() {
				}.getType();
				TextOneBookDirsBean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					oneBookDirs = bean.getData();
					Log.i("msg", oneBookDirs.toString());
					if (oneBookDirs != null) {
						Intent intent = new Intent(TeacherQuizActivity.this, TeacherSelectActivity.class);
						intent.putExtra("oneBookDirs", (Serializable) oneBookDirs);
						intent.putExtra("select", "2");
						startActivityForResult(intent, STEP3);
					}

				} else {
					DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "信息获取失败", bean.getCodeInfo(), null);
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherQuizActivity.this, "操作失败，请检查网络");
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
	 * <p>
	 * Title
	 * </p>
	 * : getTextBookTwoDirs Description: 获取二级目录
	 * 
	 * @param dirId
	 */
	protected void getTextBookTwoDirs(String dirId) {
		if (dirId == null) {
			ActivityUtils.showToast(this, "请选择一级目录");
			return;
		}
		RequestParams params = new RequestParams(Preferences.GET_TEXT_TWO_DIRS);
		params.addQueryStringParameter("dirId", dirId);

		DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<TextTwoBookDirsBean>() {
				}.getType();
				TextTwoBookDirsBean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					twoBookDirs = bean.getData();
					Log.i("msg", twoBookDirs.toString());
					if (twoBookDirs != null) {
						Intent intent = new Intent(TeacherQuizActivity.this, TeacherSelectActivity.class);
						intent.putExtra("twoBookDirs", (Serializable) twoBookDirs);
						intent.putExtra("select", "3");
						startActivityForResult(intent, STEP4);
					}
				} else {
					DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "信息获取失败", bean.getCodeInfo(), null);
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherQuizActivity.this, "操作失败，请检查网络");
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
	 * <p>
	 * Title
	 * </p>
	 * : getTwoDirPapers Description: 获取试卷列表
	 * 
	 * @param dirId
	 */
	protected void getTwoDirPapers(String dirId) {
		if (dirId == null) {
			ActivityUtils.showToast(this, "请选择二级目录");
			return;
		}
		RequestParams params = new RequestParams(Preferences.GET_TWO_DIR_PAPERS);
		params.addQueryStringParameter("dirId", dirId);

		DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<TestPaperBean>() {
				}.getType();
				TestPaperBean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					papers = bean.getData();
					Log.i("msg", papers.toString());
					if (papers != null) {
						Intent intent = new Intent(TeacherQuizActivity.this, TeacherSelectActivity.class);
						intent.putExtra("papers", (Serializable) papers);
						intent.putExtra("select", "4");
						startActivityForResult(intent, STEP5);
					}
				} else {
					DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "信息获取失败", bean.getCodeInfo(), null);
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherQuizActivity.this, "操作失败，请检查网络");
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
	 * <p>
	 * Title
	 * </p>
	 * : getPaperInfo Description: 获取试卷内容
	 * 
	 * @param dirId
	 */
	protected void getPaperInfo(String paperId) {
		if (paperId == null) {
			ActivityUtils.showToast(this, "请选择试卷");
			return;
		}
		RequestParams params = new RequestParams(Preferences.GET_PAPER_INFO);
		params.addQueryStringParameter("paperId", paperId);

		DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<TestQuestionsBean>() {
				}.getType();
				TestQuestionsBean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					questions = bean.getData();
					if (questions != null) {
						loadListViewAdapter();
						et_time.setText(questions.getUsedTime());
						et_time.setSelection(et_time.getSelectionStart() + 1);

					}

				} else {
					DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "信息获取失败", bean.getCodeInfo(), null);
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherQuizActivity.this, "操作失败，请检查网络");
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
	 * <p>
	 * Title
	 * </p>
	 * : getPaperInfo Description: 添加试卷
	 * 
	 * @param dirId
	 */
	protected void insertPaperInfo(String paperId, String teacherId, String queIds, String paperName) {
		RequestParams params = new RequestParams(Preferences.INSERT_PAPER_INFO);
		params.addQueryStringParameter("paperId", paperId);
		params.addQueryStringParameter("teacherId", teacherId);
		params.addQueryStringParameter("queIds", queIds);
		params.addQueryStringParameter("paperName", paperName);
		DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<PaperInfoBean>() {
				}.getType();
				PaperInfoBean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					PaperInfoBean.Data paperInfo = bean.getData();
					Log.i("msg", paperInfo.toString());
					if (paperInfo != null) {
						claTesPapId = paperInfo.getClaTesPapId();
						paperCode = paperInfo.getPaperCode();
						viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
					}
				} else {
					DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "信息获取失败", bean.getCodeInfo(), null);
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherQuizActivity.this, "操作失败，请检查网络");
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
	 * <p>
	 * Title
	 * </p>
	 * : getPaperInfo Description: 发送试卷
	 * 
	 * @param dirId
	 */
	protected void confirmSend(String claTesPapId, String courseIds) {
		RequestParams params = new RequestParams(Preferences.CONFIRM_SEND);
		params.addQueryStringParameter("claTesPapId", claTesPapId);
		params.addQueryStringParameter("courseIds", courseIds);
		DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<Bean>() {
				}.getType();
				Bean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					ActivityUtils.showToastForSuccess(TeacherQuizActivity.this, "发送成功");
				} else {
					DialogUtils.showMyDialog(TeacherQuizActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "信息获取失败", bean.getCodeInfo(), null);
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherQuizActivity.this, "操作失败，请检查网络");
			}

			@Override
			public void onCancelled(CancelledException e) {

			}

			@Override
			public void onFinished() {

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		switch (requestCode) {
			case STEP1:
				curriculum_name.setText(data.getStringExtra("name"));
				courseId = data.getStringExtra("id");
				String courseCode = data.getStringExtra("code");
				break;
			case STEP2:
				book_select.setText(data.getStringExtra("book_select"));
				bookId = data.getStringExtra("book_id");

				break;
			case STEP3:
				one_level.setText(data.getStringExtra("book_select"));
				dirId1 = data.getStringExtra("dirId1");

				break;
			case STEP4:
				two_level.setText(data.getStringExtra("book_select"));
				dirId2 = data.getStringExtra("dirId2");

				break;
			case STEP5:
				paperId = data.getStringExtra("id");
				String name = data.getStringExtra("book_select");
				String code = data.getStringExtra("code");
				exam_select.setText(name);
				// exam_code.setText(code);
				// exam_name.setText(name);
				exam_number.setText("试卷编号：" + code);

				break;

			default:
				break;
		}

	}
}
