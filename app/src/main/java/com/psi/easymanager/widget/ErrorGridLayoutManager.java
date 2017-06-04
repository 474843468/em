package com.psi.easymanager.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import com.orhanobut.logger.Logger;

/**
 * 作者：${ylw} on 2017-03-28 16:30
 * java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid item position
 * 0(offset:1).state:1
 */
public class ErrorGridLayoutManager extends GridLayoutManager {

  public ErrorGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public ErrorGridLayoutManager(Context context, int spanCount) {
    super(context, spanCount);
  }

  public ErrorGridLayoutManager(Context context, int spanCount, int orientation,
      boolean reverseLayout) {
    super(context, spanCount, orientation, reverseLayout);
  }

  @Override public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    try {
      super.onLayoutChildren(recycler, state);
    } catch (IndexOutOfBoundsException e) {
      Logger.e(e.toString());
      //待解决
    }
  }
}
