package com.jiayue;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiayue.adapter.MyViewPagerAdapter;
import com.jiayue.fragment.Fragment_Book;
import com.jiayue.fragment.Fragment_Browse;
import com.jiayue.fragment.Fragment_Pay;
import com.jiayue.fragment.Fragment_Setting;
import com.jiayue.fragment.Fragment_ZhiYue;
import com.jiayue.rest.MainListener;
import com.jiayue.view.CustomViewpager;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity {

	private CustomViewpager viewpager;
	private MyViewPagerAdapter adapter;
	private List<Fragment> list = new ArrayList<Fragment>();
	private Fragment f_book, f_zhiyue, f_browse, f_setting, f_pay;
	private int current_activity = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initView();
	}

	// 初始化界面
	public void initView() {
		f_book = new Fragment_Book();
		f_zhiyue = new Fragment_ZhiYue();
		f_browse = new Fragment_Browse();
		f_setting = new Fragment_Setting();
		((Fragment_Setting)f_setting).setListener(new MainListener() {
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		f_pay = new Fragment_Pay();
		list.add(f_zhiyue);
		list.add(f_setting);
		list.add(f_pay);
		list.add(f_browse);
		list.add(f_book);
		viewpager = (CustomViewpager) findViewById(R.id.viewpager);
		viewpager.setOffscreenPageLimit(4);
		adapter = new MyViewPagerAdapter(getSupportFragmentManager(), list);
		viewpager.setAdapter(adapter);
	}

	// 点击状态变成正常状态
	private void changeClickToNormal(int current_activity) {
		LinearLayout ll = (LinearLayout) findViewById(getResources().getIdentifier("nav0" + current_activity, "id", "com.jia.yue"));
		// ll.setBackgroundResource(R.drawable.bg_tab_normal);
		ImageView iv = (ImageView) ll.getChildAt(0);
		iv.setImageResource(getResources().getIdentifier("tab_normal_image" + current_activity, "drawable", "com.jia.yue"));
		TextView tv = (TextView) ll.getChildAt(1);
		tv.setTextColor(this.getResources().getColor(R.color.tab_text_normal));
	}

	// 点击状态变成正常状态
	private void changeNormalToClick(int current_activity) {
		LinearLayout ll = (LinearLayout) findViewById(getResources().getIdentifier("nav0" + current_activity, "id", "com.jia.yue"));
		// ll.setBackgroundResource(R.drawable.bg_tab_click);
		ImageView iv = (ImageView) ll.getChildAt(0);
		iv.setImageResource(getResources().getIdentifier("tab_click_image" + current_activity, "drawable", "com.jia.yue"));
		TextView tv = (TextView) ll.getChildAt(1);
		tv.setTextColor(this.getResources().getColor(R.color.tab_text_click));
	}

	// 导航栏按键控制
	public void btnNavOnclick(View v) {

		int id = v.getId();
		if (id == R.id.nav01) {
			changeClickToNormal(current_activity);
			setContent(1);
			// } else if (id == R.id.nav02) {
			// changeClickToNormal(current_activity);
			// } else if (id == R.id.nav03) {
			// changeClickToNormal(current_activity);
			// setContent(3);
		} else if (id == R.id.nav02) {
			changeClickToNormal(current_activity);
			setContent(2);
		} else if (id == R.id.nav03) {
			changeClickToNormal(current_activity);
			setContent(3);
		} else if (id == R.id.nav04) {
			changeClickToNormal(current_activity);
			setContent(4);
		} else if (id == R.id.nav05) {
			changeClickToNormal(current_activity);
			setContent(5);
		}
	}

	public void setContent(int current_activity) {
		viewpager.setCurrentItem(current_activity-1, false);
		changeNormalToClick(current_activity);
		this.current_activity = current_activity;
		// Intent intent=new Intent(getBaseContext(),BookActivity00.class);
		// startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	long exitTime = 0;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		System.out.println(KeyEvent.KEYCODE_BACK + "--------------------" + event.getKeyCode() + "---------------------" + event.getAction());

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (current_activity == 4 && ((Fragment_Browse) f_browse).canGoBack()) {
				 ((Fragment_Browse) f_browse).goBack();
				 return true;
			}
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// SPUtility.putSPString(getApplicationContext(), "isPlay",
				// "false");
				// this.stopService(new Intent(this, MusicPlayerService.class));
				finish();
				// System.exit(0);
			}
			return true;
		}

		return super.dispatchKeyEvent(event);
	}
}
