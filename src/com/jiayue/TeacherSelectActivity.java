package com.jiayue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.jiayue.dto.base.BookSeletctBean;
import com.jiayue.dto.base.TestPaperBean;
import com.jiayue.dto.base.TextOneBookDirsBean;
import com.jiayue.dto.base.TextTwoBookDirsBean;
import com.jiayue.sortlistview.CharacterParser;
import com.jiayue.sortlistview.ClearEditText;
import com.jiayue.sortlistview.PinyinComparator;
import com.jiayue.sortlistview.SideBar;
import com.jiayue.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.jiayue.sortlistview.SortAdapter;
import com.jiayue.sortlistview.SortModel;

@SuppressLint("DefaultLocale")
public class TeacherSelectActivity extends BaseActivity {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private List<BookSeletctBean.Data> bookSeletcts;
	private List<TextOneBookDirsBean.Data> oneBookDirs;
	private List<TextTwoBookDirsBean.Data> twoBookDirs;
	private List<TestPaperBean.Data> papers;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_select);
		bookSeletcts = (List<BookSeletctBean.Data>) getIntent().getSerializableExtra(
				"bookSelect");
		oneBookDirs = (List<TextOneBookDirsBean.Data>) getIntent().getSerializableExtra(
				"oneBookDirs");
		twoBookDirs = (List<TextTwoBookDirsBean.Data>) getIntent().getSerializableExtra(
				"twoBookDirs");
		papers = (List<TestPaperBean.Data>) getIntent().getSerializableExtra("papers");
		initViews();
	}

	private void initViews() {
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		loadData();
	}

	private void loadData() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				
				Intent intent = new Intent();
				switch (getIntent().getStringExtra("select")) {
					case "1" :
						intent.putExtra("book_select", ((SortModel) adapter
								.getItem(position)).getName());
						intent.putExtra("book_id", bookSeletcts.get(position)
								.getBookId());
						break;
					case "2" :
						intent.putExtra("book_select", ((SortModel) adapter
								.getItem(position)).getName());
						intent.putExtra("dirId1", oneBookDirs.get(position)
								.getId());
						
						break;
					case "3" :
						intent.putExtra("book_select", ((SortModel) adapter
								.getItem(position)).getName());
						intent.putExtra("dirId2", twoBookDirs.get(position)
								.getId());
						break;
					case "4" :
						intent.putExtra("book_select", ((SortModel) adapter
								.getItem(position)).getName());
						intent.putExtra("id", papers.get(position).getId());
						intent.putExtra("code", papers.get(position).getPaperCode());
						break;

					default :
						break;
				}
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		switch (getIntent().getStringExtra("select")) {
			case "1" :
				SourceDateList = filledData(bookSeletcts);
				break;
			case "2" :
				SourceDateList = filledData1(oneBookDirs);
				break;
			case "3" :
				SourceDateList = filledData2(twoBookDirs);
				break;
			case "4" :
				SourceDateList = filledData3(papers);

				break;

			default :
				break;
		}

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(List<BookSeletctBean.Data> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date.get(i).getBookName());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date.get(i)
					.getBookName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}
	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData1(List<TextOneBookDirsBean.Data> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date.get(i).getName());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}
	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData2(List<TextTwoBookDirsBean.Data> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date.get(i).getName());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}
	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData3(List<TestPaperBean.Data> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date.get(i).getName());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}


	public void btnBack(View v) {
		this.finish();
	}
}
