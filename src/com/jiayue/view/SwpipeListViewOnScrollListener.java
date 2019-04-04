package com.jiayue.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.widget.AbsListView;

/** 由于Listview与下拉刷新的Scroll事件冲突, 使用这个ScrollListener可以避免Listview滑动异常 */
public class SwpipeListViewOnScrollListener implements AbsListView.OnScrollListener {

    private SwipeRefreshLayout mSwipeView;
    private AbsListView.OnScrollListener mOnScrollListener;

    public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView) {
        mSwipeView = swipeView;
    }

    public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView,
            OnScrollListener onScrollListener) {
        mSwipeView = swipeView;
        mOnScrollListener = (AbsListView.OnScrollListener) onScrollListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        View firstView = absListView.getChildAt(firstVisibleItem);

//         当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
//        Log.d("swip","firstVisibleItem="+firstVisibleItem+"-------firstView="+firstView+"---firstView.getTop()="+(firstView == null?null:firstView.getTop()));
        if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0||firstView.getTop() == 17)) {
            mSwipeView.setEnabled(true);
        } else {
            mSwipeView.setEnabled(false);
        }
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(absListView, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
//        if (totalItemCount > 0) {
//            boolean enable = false;
//            if (absListView != null && absListView.getChildCount() > 0) {
//                // check if the first item of the list is visible
//                boolean firstItemVisible = absListView.getFirstVisiblePosition() == 0;
//                // check if the top of the first item is visible
//                boolean topOfFirstItemVisible = absListView.getChildAt(0).getTop() == 0;
//                // enabling or disabling the refresh layout
//                enable = firstItemVisible && topOfFirstItemVisible;
//            }
//            mSwipeView.setEnabled(enable);
//        }
    }
}
