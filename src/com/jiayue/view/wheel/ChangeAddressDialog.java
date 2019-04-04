package com.jiayue.view.wheel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiayue.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChangeAddressDialog extends Dialog implements View.OnClickListener,OnWheelChangedListener{

	//省市区控件
	private WheelView wvProvince;
	private WheelView wvCitys;
	private WheelView wvArea;
	
	private TextView btnSure;//确定按钮
	private TextView btnCancel;//取消按钮

	private Context context;//上下文对象
	
	private JSONObject mJsonObj;//存放地址信息的json对象
	
	private String[] mProvinceDatas;
	private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();
	private Map<String, String[]> mCodeDatasMap = new HashMap<String, String[]>();//根据城市同步记录区县code值

	private ArrayList<String> arrProvinces = new ArrayList<String>();
	private ArrayList<String> arrCitys = new ArrayList<String>();
	private ArrayList<String> arrAreas = new ArrayList<String>();
	
	private AddressTextAdapter provinceAdapter;
	private AddressTextAdapter cityAdapter;
	private AddressTextAdapter areaAdapter;

	//选中的省市区信息
	private String strProvince;
	private String strCity ;
	private String strArea ;
	
	//回调方法
//	private OnAddressCListener onAddressCListener;
	private OnAddressCodeCListener onAddressCListener;
	//显示文字的字体大小
	private int maxsize = 24;
	private int minsize = 14;

	private int index = 0;

	public ChangeAddressDialog(Context context) {
		super(context, R.style.ShareDialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_myinfo_changeaddress);

		wvProvince = (WheelView) findViewById(R.id.wv_address_province);
		wvCitys = (WheelView) findViewById(R.id.wv_address_city);
		wvArea = (WheelView) findViewById(R.id.wv_address_area);
		btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
		btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);
		
		btnSure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		wvProvince.addChangingListener(this);
		wvCitys.addChangingListener(this);
		wvArea.addChangingListener(this);

		initJsonData();
		initDatas();
		initProvinces();
		provinceAdapter = new AddressTextAdapter(context, arrProvinces, getProvinceItem(strProvince), maxsize, minsize);
		wvProvince.setVisibleItems(5);
		wvProvince.setViewAdapter(provinceAdapter);
//		wvProvince.setCyclic(true);//设置内容循环
		wvProvince.setCurrentItem(getProvinceItem(strProvince));

		initCitys(mCitisDatasMap.get(strProvince));
		cityAdapter = new AddressTextAdapter(context, arrCitys, getCityItem(strCity), maxsize, minsize);
		wvCitys.setVisibleItems(5);
		wvCitys.setViewAdapter(cityAdapter);
		wvCitys.setCurrentItem(getCityItem(strCity));
		
		initAreas(mAreaDatasMap.get(strCity));
		areaAdapter = new AddressTextAdapter(context, arrAreas, getCityItem(strArea), maxsize, minsize);
		wvArea.setVisibleItems(5);
		wvArea.setViewAdapter(areaAdapter);
		wvArea.setCurrentItem(getAreaItem(strArea));
		wvProvince.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, provinceAdapter);
			}
		});
		wvCitys.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, cityAdapter);
			}
		});
		wvArea.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = (String) areaAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, areaAdapter);
			}
		});
	}

	/**
	 * 初始化省会
	 */
	public void initProvinces() {
		int length = mProvinceDatas.length;
		for (int i = 0; i < length; i++) {
			arrProvinces.add(mProvinceDatas[i]);
		}
	}

	/**
	 * 根据省会，生成该省会的所有城市
	 * 
	 * @param citys
	 */
	public void initCitys(String[] citys) {
		if (citys != null) {
			arrCitys.clear();
			int length = citys.length;
			for (int i = 0; i < length; i++) {
				arrCitys.add(citys[i]);
			}
		} else {
			String[] city = mCitisDatasMap.get("广东");
			arrCitys.clear();
			int length = city.length;
			for (int i = 0; i < length; i++) {
				arrCitys.add(city[i]);
			}
		}
		if (arrCitys != null && arrCitys.size() > 0
				&& !arrCitys.contains(strCity)) {
			strCity = arrCitys.get(0);
		}
	}
	
	/**
	 * 根据城市，生成该城市的所有地区
	 * 
	 * @param areas
	 */
	public void initAreas(String[] areas) {
		if (areas != null) {
			arrAreas.clear();
			int length = areas.length;
			for (int i = 0; i < length; i++) {
				arrAreas.add(areas[i]);
			}
		} else {
			String[] city = mCitisDatasMap.get("广东");
			arrCitys.clear();
			int length = city.length;
			for (int i = 0; i < length; i++) {
				arrCitys.add(city[i]);
			}
		}
		if (arrAreas != null && arrAreas.size() > 0
				&& !arrAreas.contains(strArea)) {
			strArea = arrAreas.get(0);
		}
	}


	/**
	 * 初始化地点
	 * 
	 * @param province
	 * @param city
	 */
	public void setAddress(String province, String city,String area) {
		if (province != null && province.length() > 0) {
			this.strProvince = province;
		}
		if (city != null && city.length() > 0) {
			this.strCity = city;
		}
		if (area != null && area.length() > 0) {
			this.strArea = area;
		}
	}

	/**
	 * 返回省会索引
	 */
	public int getProvinceItem(String province) {
		int size = arrProvinces.size();
		int provinceIndex = 0;
		boolean noprovince = true;
		for (int i = 0; i < size; i++) {
			if (province.equals(arrProvinces.get(i))) {
				noprovince = false;
				return provinceIndex;
			} else {
				provinceIndex++;
			}
		}
		if (noprovince) {
			strProvince = "北京市";
			return 0;
		}
		return provinceIndex;
	}

	/**
	 * 得到城市索引
	 */
	public int getCityItem(String city) {
		int size = arrCitys.size();
		int cityIndex = 0;
		boolean nocity = true;
		for (int i = 0; i < size; i++) {
			if (city.equals(arrCitys.get(i))) {
				nocity = false;
				return cityIndex;
			} else {
				cityIndex++;
			}
		}
		if (nocity) {
			strCity = "北京市";
			return 0;
		}
		return cityIndex;
	}
	
	//得到地区
	public int getAreaItem(String area) {
		int size = arrAreas.size();
		int cityIndex = 0;
		boolean nocity1 = true;
		for (int i = 0; i < size; i++) {
			if (area.equals(arrAreas.get(i))) {
				nocity1 = false;
				return cityIndex;
			} else {
				cityIndex++;
			}
		}
		if (nocity1) {
			strArea = "东城区";
			return 0;
		}
		return cityIndex;
	}
	
	//根据省来更新wheel的状态
	private void updateCities()
	{
		String currentText = (String) provinceAdapter.getItemText(wvProvince.getCurrentItem());
		strProvince = currentText;
		setTextviewSize(currentText, provinceAdapter);
		String[] citys = mCitisDatasMap.get(currentText);
		if (citys == null)
		{
			citys = new String[] { "" };
		}
		initCitys(citys);
		cityAdapter = new AddressTextAdapter(context, arrCitys, 0, maxsize, minsize);
		wvCitys.setViewAdapter(cityAdapter);
		wvCitys.setCurrentItem(0);
		updateAreas();
	}
	
	//根据城市来更新wheel的状态
	private void updateAreas()
	{
		String currentText = (String) cityAdapter.getItemText(wvCitys.getCurrentItem());
		strCity = currentText;
		setTextviewSize(currentText, cityAdapter);
		String[] areas = mAreaDatasMap.get(currentText);
		try {
			if (areas == null)
			{
				areas = new String[] { "" };
				strArea = "";
			}else{
				strArea = areas[0];
			}
		}catch (Exception e){

		}
		initAreas(areas);
		areaAdapter = new AddressTextAdapter(context, arrAreas, 0, maxsize, minsize);
		wvArea.setViewAdapter(areaAdapter);
		wvArea.setCurrentItem(0);
	}
	
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == wvProvince)
		{
			//切换省份的操作
			updateCities();
		} else if (wheel == wvCitys)
		{
			updateAreas();
		} else if (wheel == wvArea)
		{
			String currentText = (String) areaAdapter.getItemText(wheel.getCurrentItem());
			strArea = currentText;
			if(mAreaDatasMap.get(strCity)!=null&&mAreaDatasMap.get(strCity)[newValue]!=null){
				strArea = mAreaDatasMap.get(strCity)[newValue];
				index = newValue;
				setTextviewSize(currentText, areaAdapter);
			}
		}
	}
	
////////////////////////////////////////////////////华丽的分界线	
	private void initJsonData() {
		try {
			StringBuffer sb = new StringBuffer();
			InputStream is = context.getAssets().open("JsonString.json");
			int len = -1;
			byte[] buf = new byte[is.available()];
			while ((len = is.read(buf)) != -1) {
				sb.append(new String(buf, 0, len, "utf-8"));
			}
			is.close();
			mJsonObj = new JSONObject(sb.toString());
//			Log.d("json",sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initDatas()
	{
		try
		{
			JSONArray jsonArray = mJsonObj.getJSONArray("province");
			mProvinceDatas = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
				String province = jsonP.getString("name");// 省名字

				mProvinceDatas[i] = province;

				JSONArray jsonCs = null;
				try
				{
					/**
					 * Throws JSONException if the mapping doesn't exist or is
					 * not a JSONArray.
					 */
					jsonCs = jsonP.getJSONArray("cities");
				} catch (Exception e1)
				{
					continue;
				}
				String[] mCitiesDatas = new String[jsonCs.length()];
				for (int j = 0; j < jsonCs.length(); j++)
				{
					JSONObject jsonCity = jsonCs.getJSONObject(j);
					String city = jsonCity.getString("name");// 市名字
					mCitiesDatas[j] = city;
					JSONArray jsonAreas = null;
					try
					{
						/**
						 * Throws JSONException if the mapping doesn't exist or
						 * is not a JSONArray.
						 */
						jsonAreas = jsonCity.getJSONArray("counties");
					} catch (Exception e)
					{
						continue;
					}

					String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
					String[] mCodeDatas = new String[jsonAreas.length()];// 当前市的所有区
					for (int k = 0; k < jsonAreas.length(); k++)
					{
						String area = jsonAreas.getJSONObject(k).getString("name");// 区域的名称
						String code = jsonAreas.getJSONObject(k).getString("code");// 区域的代号
						mAreasDatas[k] = area;
						mCodeDatas[k] = code;
					}
					mAreaDatasMap.put(city, mAreasDatas);
					mCodeDatasMap.put(city,mCodeDatas);
				}

				mCitisDatasMap.put(province, mCitiesDatas);
			}

		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		mJsonObj = null;
	}

	private class AddressTextAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> list;

		protected AddressTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
			super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
			this.list = list;
			setItemTextResource(R.id.tempValue);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			String s = "";
			try {
				s = list.get(index) + "";
			}catch (Exception e){

			}
			return s;
		}
	}

	public void setTextviewSize(String curriteItemText, AddressTextAdapter adapter) {
		ArrayList<View> arrayList = adapter.getTestViews();
		int size = arrayList.size();
		String currentText;
		for (int i = 0; i < size; i++) {
			TextView textvew = (TextView) arrayList.get(i);
			currentText = textvew.getText().toString();
			if (curriteItemText.equals(currentText)) {
				textvew.setTextSize(24);
			} else {
				textvew.setTextSize(14);
			}
		}
	}


	public void setAddresskListener(OnAddressCodeCListener onAddressCListener) {
		this.onAddressCListener = onAddressCListener;
	}

	@Override
	public void onClick(View v) {
		if (v == btnSure) {
			if (onAddressCListener != null) {
				String code = "";
				if(mCodeDatasMap.get(strCity) != null&&mCodeDatasMap.get(strCity).length!=0)
					code = mCodeDatasMap.get(strCity)[index];
//				Log.d("onAddressCListener","-----strCity="+strCity+"-------strArea="+strArea+"---code="+mCodeDatasMap.get(strCity)[index]);
				onAddressCListener.onClick(strProvince, strCity,strArea,code);
				dismiss();
			}
		}else if(v == btnCancel){
			dismiss();
		}
	}
}