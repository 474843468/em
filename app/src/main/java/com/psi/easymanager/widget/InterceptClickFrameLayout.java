package com.psi.easymanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * User: ylw
 * Date: 2016-12-23
 * Time: 16:30
 * FIXME
 */
public class InterceptClickFrameLayout extends FrameLayout {
  //是否拦截
  private boolean intercept;

  public InterceptClickFrameLayout(Context context) {
    super(context);
  }

  public InterceptClickFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public InterceptClickFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (intercept) {
      return true;
    } else {
      return super.onInterceptTouchEvent(ev);
    }
  }

  public void setIntercept(boolean intercept) {
    this.intercept = intercept;
  }

  public boolean isIntercept() {
    return intercept;
  }
}