package com.jiayue;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.jiayue.adapter.CommonAdapter;
import com.jiayue.adapter.ViewHolder;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-10-26 上午11:43:27 项目名称：wyst
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：TeacherTestResult.java 类说明：
 *        ------------------------------------------------------------------
 */
public class TeacherTestResultActivity extends Activity {
	private ListView lv1;
	private ListView lv2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.acticity_teacher_result);
		initViews();
	}
	private void initViews() {

		lv1 = (ListView) findViewById(R.id.lv1);
		lv2 = (ListView) findViewById(R.id.lv2);
		List<String>list=new ArrayList<String>();
		List<String>list1=new ArrayList<String>();
		list.add("1题：10人正确，10人错误");
		list.add("2题：9人正确，8人错误");
		list1.add("力学：10人正确，10人错误");
		list1.add("光学：9人正确，8人错误");
		loadListViewAdapter(lv1, list);
		loadListViewAdapter(lv2, list1);
	}
	private void loadListViewAdapter(ListView lv,final List<String> list) {
		CommonAdapter<String>adapter=new CommonAdapter<String>(this,list,R.layout.teacher_result_item) {
			
			@Override
			public void convert(ViewHolder helper, String item, int postion) {
				helper.setText(R.id.tv_content, list.get(postion));
			}
		};
		lv.setAdapter(adapter);
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
}
