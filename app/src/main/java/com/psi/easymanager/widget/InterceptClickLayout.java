package com.psi.easymanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by dorado on 2016/6/20.
 */
public class InterceptClickLayout extends RelativeLayout {
  //是否拦截
  private boolean intercept;

  public InterceptClickLayout(Context context) {
    super(context);
  }

  public InterceptClickLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public InterceptClickLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
