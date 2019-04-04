package com.jiayue;

import java.util.Iterator;
import java.util.List;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.CommonAdapter;
import com.jiayue.adapter.ViewHolder;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.CoursesBean;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-10-16 下午2:10:41 项目名称：wyst
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：TeacherBindLogin.java 类说明：
 *        ------------------------------------------------------------------
 */
@SuppressWarnings("static-access")
public class TeacherCurriculumActivity extends BaseActivity implements OnClickListener {

	private TextView tv_header_title;
	private ListView listview;

	private CommonAdapter<CoursesBean.Data> adapter;
	private ImageButton btn_header_right;
	private Button btn_new_class;
	private Button btn_delete_class;
	private Button btn_confirm_delete;
	private LinearLayout ll_add;
	private EditText et_text;
	private ImageButton btn_add;
	private int teacher_cancel;
	boolean flag = true;

	private List<CoursesBean.Data> courses;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_curriculum);

		initViews();
	}

	/**
	 * <p>
	 * Title
	 * </p>
	 * : initViews Description: 初始化view对象
	 */
	private void initViews() {
		listview = (ListView) findViewById(R.id.listview);
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		btn_header_right = (ImageButton) findViewById(R.id.btn_header_right);

		btn_new_class = (Button) findViewById(R.id.btn_new_class);
		btn_delete_class = (Button) findViewById(R.id.btn_delete_class);
		btn_confirm_delete = (Button) findViewById(R.id.btn_confirm_delete);
		ll_add = (LinearLayout) findViewById(R.id.ll_add);
		et_text = (EditText) findViewById(R.id.et_text);
		btn_add = (ImageButton) findViewById(R.id.btn_add);
		teacher_cancel = getResources().getIdentifier("teacher_cancel", "drawable", getPackageName());
		setOnclick(btn_header_right, btn_new_class, btn_delete_class, btn_confirm_delete, btn_add, ll_add);

		tv_header_title.setText("课程名称");
		getCourses("1");
		// loadListViewAdapter();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("name", courses.get(position).getGroupName());
				intent.putExtra("id", courses.get(position).getId());
				intent.putExtra("code", courses.get(position).getGroupCode());
				setResult(Activity.RESULT_OK, intent);
				finish();

			}
		});
	}

	private void loadListViewAdapter() {
		adapter = new CommonAdapter<CoursesBean.Data>(this, courses, R.layout.teacher_curriculum_item) {

			@Override
			public void convert(ViewHolder helper, CoursesBean.Data item, final int postion) {

				helper.setText(R.id.tv_exam, item.getGroupName());
				if (flag) {
					helper.setHiddle(R.id.checkbox);
				} else {
					helper.setDisplay(R.id.checkbox);
					helper.setOnClickListener(R.id.checkbox, new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (isSelected.get(postion)) {
								isSelected.put(postion, false);
								setIsSelected(isSelected);
							} else {
								isSelected.put(postion, true);
								setIsSelected(isSelected);
								ActivityUtils.showToast(TeacherCurriculumActivity.this, courses.get(postion).getId() + courses.get(postion).getGroupCode());
							}

						}

					});

					helper.setIsChecked(R.id.checkbox, getIsSelected().get(postion));
				}
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
	 * : btnBack Description: 返回按钮
	 * 
	 * @param v
	 */
	public void btnBack(View v) {
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 取消按钮
			case R.id.btn_header_right:
				btn_header_right.setVisibility(View.GONE);
				btn_new_class.setVisibility(View.VISIBLE);
				btn_delete_class.setVisibility(View.VISIBLE);
				ll_add.setVisibility(View.GONE);
				btn_confirm_delete.setVisibility(View.GONE);
				flag = true;
				adapter.notifyDataSetChanged();
				listview.setEnabled(true);
				break;
			// 新建课程
			case R.id.btn_new_class:
				btn_new_class.setVisibility(View.GONE);
				btn_delete_class.setVisibility(View.GONE);
				btn_header_right.setBackgroundResource(teacher_cancel);
				btn_header_right.setVisibility(View.VISIBLE);
				ll_add.setVisibility(View.VISIBLE);
				listview.setEnabled(false);
				break;
			// 删除课程
			case R.id.btn_delete_class:
				btn_new_class.setVisibility(View.GONE);
				btn_delete_class.setVisibility(View.GONE);
				btn_header_right.setBackgroundResource(teacher_cancel);
				btn_header_right.setVisibility(View.VISIBLE);
				btn_confirm_delete.setVisibility(View.VISIBLE);
				ll_add.setVisibility(View.GONE);
				flag = false;
				adapter.notifyDataSetInvalidated();
				break;
			// 确认删除
			case R.id.btn_confirm_delete:
				StringBuffer bf = new StringBuffer();
				for (int j = 0; j < courses.size(); j++) {
					if (adapter.getIsSelected().get(j)) {
						bf.append(courses.get(j).getId() + ",");
						courses.get(j).setGroupName("*");
					}
				}
				deleteCourses(bf.deleteCharAt(bf.length() - 1).toString());
				btn_header_right.setVisibility(View.GONE);
				btn_new_class.setVisibility(View.VISIBLE);
				btn_delete_class.setVisibility(View.VISIBLE);
				btn_confirm_delete.setVisibility(View.GONE);
				flag = true;

				// ActivityUtils.showToast(TeacherCurriculumActivity.this,
				// list.size()+"");
				break;
			// 确定添加
			case R.id.btn_add:
				String text = et_text.getText().toString().trim();
				if (TextUtils.isEmpty(text)) {
					ActivityUtils.showToast(this, "请输入课程名称");
					return;
				}

				addCourse(text, "1");

				et_text.setText("");
				ll_add.setVisibility(View.GONE);
				btn_header_right.setVisibility(View.GONE);
				btn_new_class.setVisibility(View.VISIBLE);
				btn_delete_class.setVisibility(View.VISIBLE);
				listview.setEnabled(true);

				break;
			default:
				break;
		}
	}

	/**
	 * <p>
	 * Title
	 * </p>
	 * : getCourses Description:获取课程列表
	 * 
	 * @param teacherId
	 */
	protected void getCourses(String teacherId) {
		if (teacherId == null) {
			ActivityUtils.showToast(this, "请选择试卷");
			return;
		}

		RequestParams params = new RequestParams(Preferences.GET_COURSES);
		params.addQueryStringParameter("teacherId", teacherId);

		DialogUtils.showMyDialog(TeacherCurriculumActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<CoursesBean>() {
				}.getType();
				CoursesBean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					courses = bean.getData();
					Log.i("msg", courses.toString());
					if (courses != null) {
						loadListViewAdapter();
					}
				} else {
					DialogUtils.showMyDialog(TeacherCurriculumActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "加载失败", bean.getCodeInfo(), null);
				}

			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherCurriculumActivity.this, "获取信息失败，请检查网络");
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
	 * : deleteCourses Description:删除课程
	 * 
	 * @param teacherId
	 */
	protected void deleteCourses(String courseIds) {
		if (courseIds == null) {
			ActivityUtils.showToast(this, "请选择试卷");
			return;
		}
		
		RequestParams params = new RequestParams(Preferences.DELETE_COURSES);
		params.addQueryStringParameter("courseIds", courseIds);

		DialogUtils.showMyDialog(TeacherCurriculumActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<Bean>() {
				}.getType();
				Bean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					Iterator<CoursesBean.Data> it = courses.iterator();
					while (it.hasNext()) {
						CoursesBean.Data value = (CoursesBean.Data) it.next();
						if (value.getGroupName().equals("*")) {
							it.remove();
						}
					}
					adapter.dataChange(courses);
					adapter.notifyDataSetChanged();
				} else {
					DialogUtils.showMyDialog(TeacherCurriculumActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "删除失败", bean.getCodeInfo(), null);
				}

			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherCurriculumActivity.this, "操作失败，请检查网络");
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
	 * : addCourse Description:添加课程
	 * 
	 * @param teacherId
	 */
	protected void addCourse(String courseName, String teacherId) {
		if (courseName == null && courseName == null) {
			ActivityUtils.showToast(this, "请选择试卷");
			return;
		}
		
		RequestParams params = new RequestParams(Preferences.ADD_COURSE);
		params.addQueryStringParameter("courseName", courseName);
		params.addQueryStringParameter("teacherId", teacherId);
		DialogUtils.showMyDialog(TeacherCurriculumActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中...", null);
		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<CoursesBean>() {
				}.getType();
				CoursesBean bean = gson.fromJson(s, type);
				DialogUtils.dismissMyDialog();
				if (bean != null && bean.getCode().equals("SUCCESS")) {
					courses = bean.getData();
					adapter.dataChange(courses);
					loadListViewAdapter();
				} else {
					DialogUtils.showMyDialog(TeacherCurriculumActivity.this, MyPreferences.SHOW_ERROR_DIALOG, "添加失败", bean.getCodeInfo(), null);
				}

			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				DialogUtils.dismissMyDialog();
				ActivityUtils.showToast(TeacherCurriculumActivity.this, "操作失败，请检查网络");
			}

			@Override
			public void onCancelled(CancelledException e) {

			}

			@Override
			public void onFinished() {

			}
		});
	}
}
