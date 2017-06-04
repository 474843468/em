package com.psi.easymanager.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zjq on 2016/3/15.
 */
public class RecyclerViewSpaceItemDecoration extends RecyclerView.ItemDecoration {

  private final int mVerticalSpace;
  private final int mHorizontalSpace;

  public RecyclerViewSpaceItemDecoration(int mHorizontalSpace, int mVerticalSpace) {
    this.mHorizontalSpace = mHorizontalSpace;
    this.mVerticalSpace = mVerticalSpace;
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    outRect.right = mHorizontalSpace;
    outRect.bottom = mVerticalSpace;
    //if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
    //
    //}
  }
}