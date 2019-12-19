package com.jiayue.sortlistview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragGridView extends GridView {

    /**
     * dragGridView item长按的响应时间 默认1000毫秒,可以自定义
     */
    private long mLongItemResponeTime = 1000;

    /**
     * 是否可以拖拽,默认不可以
     */
    private boolean isDrag = false;

    /**
     * 正在拖拽的item的position
     */
    private int mDragPosition;

    /**
     * 开始拖动item的view对象
     */
    private View mStartDragItemView = null;

    /**
     * 镜像View
     */
    private ImageView mDragImageView;

    /**
     * 震动器
     */
    private Vibrator mVibrator;

    /**
     * windowManager 通过其添加镜像view到当前窗口
     */
    private WindowManager mWindowManager;

    /**
     * 镜像view的layoutParams
     */
    private WindowManager.LayoutParams mLayoutParams;

    /**
     * 拖动item缓存的bitmap
     */
    private Bitmap mDragBitmap;

    /**
     * 按下点到item的上边距
     */
    private int mPoint2ItemTop;

    /**
     * 按下的点到item的左边距
     */
    private int mPoint2ItemLeft;

    /**
     * gridView距离屏幕的上边距
     */
    private int mOffset2Top;

    /**
     * gridView距离屏幕的左边距
     */
    private int mOffset2Left;

    /**
     * 状态栏的高度
     */
    private int mStatusHeight;

    /**
     * 为外部提供的item拖动位置改变的回调接口
     */
    private OnChangeListener mOnChangeListener;

    private Handler mHandler = new Handler();

    private int mDownX;
    private int mDownY;
    private int mMoveX;
    private int mMoveY;


    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取振动器
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //获取windowManager
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //获取状态栏高度
        mStatusHeight = getStatusHeight(context);
    }

    /**
     * 设置长按item的时间
     *
     * @param time 默认1000毫秒
     */
    public void setLongItemResponeTime(int time) {
        mLongItemResponeTime = time;
    }

    /**
     * 设置item状态改变的监听对象,主要用于处理外部数据的交换
     *
     * @param onChangeListener
     */
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        mOnChangeListener = onChangeListener;
    }

    /**
     * 长按item后的任务
     */
    private Runnable mLongClickRun = new Runnable() {
        @Override
        public void run() {
            //设置可拖动
            isDrag = true;
            if (mOnChangeListener != null) {
                mOnChangeListener.onStartChange();
            }
            //设置震动
            mVibrator.vibrate(50);
            //设置拖拽的item隐藏
            mStartDragItemView.setVisibility(INVISIBLE);

            //根据我们按下的点显示item镜像
            createDragImage(mDragBitmap, mDownX, mDownY);
        }
    };

    /**
     * 创建拖动的镜像
     *
     * @param dragBitmap 按下的item bitmapCache
     * @param downX      按下的点相对父控件的坐标
     * @param downY
     */
    private void createDragImage(Bitmap dragBitmap, int downX, int downY) {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.TRANSLUCENT;            //图片之外其他地方透明
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;   //设置imageView的原点
        mLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top;
        mLayoutParams.alpha = 0.55f;                                //设置透明度
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(dragBitmap);
        mWindowManager.addView(mDragImageView, mLayoutParams);   //添加该iamgeView到window
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                //获取按下时的坐标
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();

                //根据坐标获取所点击的item的position
                mDragPosition = pointToPosition(mDownX, mDownY);

                if (mDragPosition == AdapterView.INVALID_POSITION)
                    return super.dispatchHoverEvent(event);

                //提交延迟任务到handler    在抬起时清空handler如果在延迟时间内抬起,任务还没执行就已经被清除了
                mHandler.postDelayed(mLongClickRun, mLongItemResponeTime);

                //根据position获取该item对应的View  获取viewGroup中的子View
                mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());

                //获取按下的点距离item的边距
                mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                //根据按下的点到屏幕边缘的距离减去该点到控件边缘的距离得出控件的边距
                mOffset2Top = (int) (event.getRawY() - mDownY);
                mOffset2Left = (int) (event.getRawX() - mDownX);

                //开启选中item的绘图缓存
                mStartDragItemView.setDrawingCacheEnabled(true);
                //获取item缓存的bitmap对象
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                //释放绘图缓存,避免出现重复镜像
                mStartDragItemView.destroyDrawingCache();

                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongClickRun);
                break;
            case MotionEvent.ACTION_CANCEL:
                mHandler.removeCallbacks(mLongClickRun);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        //判断是否可以拖动
        if (isDrag && mDragImageView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:


                    mMoveX = (int) ev.getX();
                    mMoveY = (int) ev.getY();

                    //更新镜像view位置
                    onDragItem(mMoveX, mMoveY);
                    break;
                case MotionEvent.ACTION_UP:
                    isDrag = false;
                    onStopDrag();
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 停止拖动
     */
    private void onStopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        if (mOnChangeListener != null) {
            mOnChangeListener.onEndChange();
        }
        removeImage();
    }

    /**
     * 拖动完成时移除掉imageView
     */
    private void removeImage() {
        if (mDragImageView != null && mWindowManager != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     * 拖动item   使用updateViewLayout方法来改变imageView的位置
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {

        if (mLayoutParams == null || mDragImageView == null) {
            return;
        }

        mLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top;
        mWindowManager.updateViewLayout(mDragImageView, mLayoutParams);

        onSwapItem(moveX, moveY);
    }


    /**
     * 交换item
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        //当前移动到的位置
        int tempPosition = pointToPosition(moveX, moveY);

        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION) {

            if (mOnChangeListener != null) {
                mOnChangeListener.onChange(mDragPosition, tempPosition);
            }

            //实际上在外面的onChange实现中做了数据交换并且刷新了gridView 这里做的只是让新的位置看起来没有内容而已!
            getChildAt(tempPosition - getFirstVisiblePosition()).setVisibility(INVISIBLE);
            getChildAt(mDragPosition - getFirstVisiblePosition()).setVisibility(VISIBLE);

            mDragPosition = tempPosition;
        }
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * item状态改变的监听
     */
    public interface OnChangeListener {

        /**
         * 当item交换位置时回调的方法,我们只需要在这个方法中实现数据的交换即可
         *
         * @param from 开始item的position
         * @param to   拖拽到的item的position
         */
        void onChange(int from, int to);

        void onStartChange();

        void onEndChange();

    }
}