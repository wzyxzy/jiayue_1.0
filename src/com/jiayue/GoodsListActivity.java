package com.jiayue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.BuyBean;
import com.jiayue.dto.base.BuyListBean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by BAO on 2016-08-12.
 */
public class GoodsListActivity extends BaseActivity {
    private TextView tv_header_title;
//    private Button btn_header_right;
    private MyAdapter adapter;
    private ListView listview;
    private boolean isCancelStatus = false;
    private final String TAG = getClass().getSimpleName();
    private List<BuyBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_goodlist);
        initView();
    }

    private void initView() {
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        tv_header_title.setText("购买清单");
//        btn_header_right = (Button) findViewById(R.id.btn_header_right);
//        btn_header_right.setVisibility(View.VISIBLE);
        listview = (ListView) findViewById(R.id.listView);
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
//        btn_header_right.setBackgroundColor(getResources().getColor(R.color.transparent));
//        btn_header_right.setText("删除");
//        btn_header_right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isCancelStatus){
//                    btn_header_right.setText("删除");
//                    isCancelStatus = false;
//                }else{
//                    btn_header_right.setText("完成");
//                    isCancelStatus = true;
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
        loadData();
    }

    private void loadData(){
    	
    	RequestParams params = new RequestParams(Preferences.ZHIBO_GOODS_BUYLIST);
		params.addQueryStringParameter("user.userId", UserUtil.getInstance(this).getUserId());

        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<BuyListBean>() {
                }.getType();
                BuyListBean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    list = bean.getData();
                    adapter.notifyDataSetChanged();
                } else {
                    ActivityUtils.showToastForFail(GoodsListActivity.this, "获取信息失败");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(GoodsListActivity.this, "获取信息失败");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void btnBack(View v) {
        this.finish();
    }

    private class MyAdapter extends BaseAdapter{
        private LayoutInflater inflater;

        public MyAdapter() {
            inflater = LayoutInflater.from(GoodsListActivity.this);
        }

        @Override
        public int getCount() {
            return list == null?0:list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold holder = null;
            if(convertView == null){
                holder = new ViewHold();
                convertView = inflater.inflate(R.layout.item_good_list,null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.cancel = (ImageButton) convertView.findViewById(R.id.cancel);
                convertView.setTag(holder);
            }else{
                holder = (ViewHold) convertView.getTag();
            }
            final BuyBean bean = list.get(position);

            holder.title.setText(bean.getGoodsName());
            holder.price.setText("¥"+bean.getPrice());
//            String[] s = bean.getAddTime().split("-");
//            String time = s[0].substring(2)+"/"+s[1]+"/"+s[2];
            holder.time.setText(bean.getAddTime());

            if(isCancelStatus)
                holder.cancel.setVisibility(View.VISIBLE);
            else
                holder.cancel.setVisibility(View.GONE);

            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showMyDialog(GoodsListActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "删除商品信息", "是否确定删除商品清单信息？一经删除无法找回。", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        	
                        	RequestParams params = new RequestParams(Preferences.ZHIBO_GOODS_CANCELBUYLIST);
                    		params.addQueryStringParameter("id", bean.getId()+"");

                    		x.http().post(params, new Callback.CommonCallback<String>(){
                                @Override
                                public void onSuccess(String s) {
                                    Gson gson = new Gson();
                                    java.lang.reflect.Type type = new TypeToken<Bean>() {
                                    }.getType();
                                    Bean bean = gson.fromJson(s, type);

                                    if(bean!=null&&bean.getCode().equals("SUCCESS")){
                                        loadData();
                                    }else{
                                        ActivityUtils.showToastForFail(GoodsListActivity.this, "删除信息失败");
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable, boolean b) {
                                    ActivityUtils.showToastForFail(GoodsListActivity.this, "删除信息失败");
                                }

                                @Override
                                public void onCancelled(CancelledException e) {

                                }

                                @Override
                                public void onFinished() {

                                }
                            });
                            DialogUtils.dismissMyDialog();
                        }
                    });
                }
            });
            return convertView;
        }

        private class ViewHold{
            private TextView title,price,time;
            private ImageButton cancel;
        }
    }
}
