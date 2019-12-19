package com.jiayue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayue.adapter.ZhiYueDetailAdapter;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.RecommendBean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.DecimalFormat;

public class ZhiYueDetailActivity extends BaseActivity {

    private ImageView iv_teacher_bg;
    private TextView tv_title, tv_price, tv_introdution;
    private Button btn;
    private RecyclerView recyclerview;
    private ZhiYueDetailAdapter adapter;
    private RecommendBean.Data.OrdinaryList data;
    private DisplayImageOptions options;
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_zhiyue_detail);

        initView();
    }



    private void initView() {
        data = (RecommendBean.Data.OrdinaryList) getIntent().getSerializableExtra("data");

        iv_teacher_bg = (ImageView) findViewById(R.id.teacher_bg);
        tv_title = (TextView) findViewById(R.id.text1);
        tv_price = (TextView) findViewById(R.id.text2);
        btn = (Button) findViewById(R.id.button5);
        tv_introdution = (TextView) findViewById(R.id.introdution);

        int default_img = getResources().getIdentifier("zhibo_default", "drawable", getPackageName());
        options = new DisplayImageOptions.Builder().showStubImage(default_img)
                // 设置图片下载期间显示的图片
                .showImageForEmptyUri(default_img)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(default_img)
                // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)
                // 设置下载的图片是否缓存在内存中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
        String image_url = Preferences.IMAGE_HTTP_LOCATION + data.getIntroductionPath();
        ImageLoader.getInstance().displayImage(image_url,iv_teacher_bg,options);

        tv_title.setText(data.getAttachOneName());
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        String geff = df.format(data.getAttachOnePrice());//返回的是String类型的
        tv_price.setText("¥"+geff);
        tv_introdution.setText(data.getBookIntroduction());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinBook();
            }
        });
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        adapter = new ZhiYueDetailAdapter(this,R.layout.item_zhiyue_catalog,data.getAttachTwoList());
        recyclerview.setAdapter(adapter);

    }

    private void joinBook() {
        RequestParams params = new RequestParams(Preferences.ADDBOOK_URL);
        params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
        params.addQueryStringParameter("code", data.getEncodeName());

        DialogUtils.showMyDialog(this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG,"data=="+data.getBookId()+"----------"+data.getAttachOnePrice()+"------"+data.getBookName()+"-----"+data.getAttachOneId());
                Intent intent = new Intent(ZhiYueDetailActivity.this, BookSynActivity.class);
                intent.putExtra("bookId", data.getBookId()+"");
                intent.putExtra("bookName", data.getBookName());
                intent.putExtra("image_url", Preferences.IMAGE_HTTP_LOCATION+data.getBookImgPath());
                intent.putExtra("attachOneId", data.getAttachOneId());
                intent.putExtra("attachName", data.getAttachOneName());
                intent.putExtra("price",data.getAttachOnePrice());
                intent.putExtra("isZhiYurJoin",true);
                startActivity(intent);

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(ZhiYueDetailActivity.this, "加载失败,请检查网络。");
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
        finish();
    }

}
