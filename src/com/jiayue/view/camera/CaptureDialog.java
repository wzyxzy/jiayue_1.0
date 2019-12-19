package com.jiayue.view.camera;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiayue.R;

/**
 * Created by hezhisu on 2017/5/19.
 */

public class CaptureDialog extends Dialog{
    private Context mContext;
    private ImageView mIvCapture;
    public CaptureDialog(@NonNull Context context) {
        super(context, R.style.Theme_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_capture, null);
        mIvCapture = (ImageView) contentView.findViewById(R.id.iv_capture);

        setContentView(contentView);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public void show(Bitmap bitmap){
        this.show();
        mIvCapture.setImageBitmap(bitmap);
    }

}
