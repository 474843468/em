package com.psi.easymanager.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 此种方法在4.x系统上好用，能显示滑动也流畅，但是在5.x上虽然显示正常，但是滑动的时候好像被粘住了，没有惯性效果。。。。然后郁闷了一下午。。。。
 最后解决方法是重写最外层的Scrollview

 **
 * 屏蔽 滑动事件
 * Created by fc on 2015/7/16.
 *
public class MyScrollview extends ScrollView {
  private int downX;
  private int downY;
  private int mTouchSlop;

  public MyScrollview(Context context) {
    super(context);
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  public MyScrollview(Context context, AttributeSet attrs) {
    super(context, attrs);
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  public MyScrollview(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent e) {
    int action = e.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        downX = (int) e.getRawX();
        downY = (int) e.getRawY();
        break;
      case MotionEvent.ACTION_MOVE:
        int moveY = (int) e.getRawY();
        if (Math.abs(moveY - downY) > mTouchSlop) {
          return true;
        }
    }
    return super.onInterceptTouchEvent(e);
  }
}
这样就可以了，暴力屏蔽。。。。5以上的事件直接传递给了内层的recyclerview,所以我们把滑动事件拦截就好了。。。
 *
 */
public class FullyLinearLayoutManager extends LinearLayoutManager {
  private static final String TAG = FullyLinearLayoutManager.class.getSimpleName();

  public FullyLinearLayoutManager(Context context) {
    super(context);
  }

  public FullyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  private int[] mMeasuredDimension = new int[2];

  @Override
  public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec,
      int heightSpec) {

    final int widthMode = View.MeasureSpec.getMode(widthSpec);
    final int heightMode = View.MeasureSpec.getMode(heightSpec);
    final int widthSize = View.MeasureSpec.getSize(widthSpec);
    final int heightSize = View.MeasureSpec.getSize(heightSpec);

    Log.i(TAG, "onMeasure called. \nwidthMode " + widthMode + " \nheightMode " + heightSpec
        + " \nwidthSize " + widthSize + " \nheightSize " + heightSize + " \ngetItemCount() "
        + getItemCount());

    int width = 0;
    int height = 0;
    for (int i = 0; i < getItemCount(); i++) {
      measureScrapChild(recycler, i,
          View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
          View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);

      if (getOrientation() == HORIZONTAL) {
        width = width + mMeasuredDimension[0];
        if (i == 0) {
          height = mMeasuredDimension[1];
        }
      } else {
        height = height + mMeasuredDimension[1];
        if (i == 0) {
          width = mMeasuredDimension[0];
        }
      }
    }
    switch (widthMode) {
      case View.MeasureSpec.EXACTLY://adv. 恰好地；正是；精确地；正确地
        width = widthSize;
      case View.MeasureSpec.AT_MOST://至多
      case View.MeasureSpec.UNSPECIFIED://未指明的；未详细说明的
    }

    switch (heightMode) {
      case View.MeasureSpec.EXACTLY:
        height = heightSize;
      case View.MeasureSpec.AT_MOST:
      case View.MeasureSpec.UNSPECIFIED:
    }

    setMeasuredDimension(width, height);
  }

  private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
      int heightSpec, int[] measuredDimension) {
    try {
      View view = recycler.getViewForPosition(0);//fix 动态添加时报IndexOutOfBoundsException

      if (view != null) {
        RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();

        int childWidthSpec =
            ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width);

        int childHeightSpec =
            ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(),
                p.height);

        view.measure(childWidthSpec, childHeightSpec);
        measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
        measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
        recycler.recycleView(view);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }
  }
}
