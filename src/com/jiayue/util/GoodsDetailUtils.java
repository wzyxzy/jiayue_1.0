package com.jiayue.util;

import android.content.Context;

import com.jiayue.dto.base.GoodsDetailBean;

/**
 * Created by BAO on 2016-08-10.
 */
public class GoodsDetailUtils {
    private GoodsDetailBean bean;
    private static GoodsDetailUtils instance;
    private Context context;
    private GoodsDetailUtils(Context context){
        this.context = context;
    }

    public static GoodsDetailUtils getInstance(Context context){
        if(instance == null){
            synchronized (GoodsDetailUtils.class){
                instance = new GoodsDetailUtils(context);
            }
        }
        return instance;
    }

    public synchronized GoodsDetailBean getBean() {
        if(bean == null)
            bean = new GoodsDetailBean();
        return bean;
    }

    public synchronized void setBean(GoodsDetailBean bean) {
        this.bean = bean;
    }
}
