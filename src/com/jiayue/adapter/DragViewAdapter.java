package com.jiayue.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.BookVO;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.SPUtility;
import com.jiayue.view.DragAdapter;

import java.io.File;
import java.util.List;

public class DragViewAdapter extends WZYDragAdapter<BookVO> {
    private List<BookVO> data;
    private final Context context;

    public DragViewAdapter(List<BookVO> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
        this.data = data;
        this.context = context;
    }

    @Override
    public void bindData(ViewHolder holder, BookVO bookVO, int indexPostion) {

        // 是否显示云彩
        ImageView iv_cloud1 = (ImageView) holder.getView(R.id.iv_cloud1);
        if (ActivityUtils.isExistAndRead(bookVO.getBookId()) || ActivityUtils.epubIsExistAndRead(bookVO.getBookId(), bookVO.getBookSaveName())) {
            iv_cloud1.setVisibility(View.GONE);
        } else {
            iv_cloud1.setVisibility(View.VISIBLE);
        }
        // 书籍封面获取
        ImageView imageView01 = (ImageView) holder.getView(R.id.iv_book1);
        String image_url = Preferences.IMAGE_HTTP_LOCATION + bookVO.getBookImgPath() + bookVO.getBookImg();

//        Log.i("图片地址", image_url);
        String bookId = bookVO.getBookId();
        // String bookSaveName = books.get(i).getBookSaveName();
        if (ActivityUtils.isNetworkAvailable(context)) {
            SPUtility.putSPString(context, bookId, image_url);
        }

        if (ActivityUtils.isExistByName(bookVO.getBookId(), bookVO.getBookName() + ".jpg")) {
            Glide
                    .with(context)
                    .load("file://" + ActivityUtils.getSDPath(bookVO.getBookId()) + File.separator + bookVO.getBookName() + ".jpg")
                    .centerCrop()
                    .placeholder(R.drawable.cover_normal)
                    .into(imageView01);
        } else {
            Glide
                    .with(context)
                    .load(SPUtility.getSPString(context, bookId))
                    .centerCrop()
                    .placeholder(R.drawable.cover_normal)
                    .into(imageView01);
        }

        ImageView btn_cancel = (ImageView) holder.getView(R.id.btn_cancel);

        // 判断是否可以直播
        if (bookVO.getIsPlay() == 0) {
            btn_cancel.setVisibility(View.INVISIBLE);
        } else {
            btn_cancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDataModelMove(int from, int to) {

        BookVO bookVO = data.remove(from);
        data.add(to, bookVO);

    }
}
