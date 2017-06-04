package com.psi.easymanager.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;

/**
 * 作者：${ylw} on 2017-04-13 18:25
 *
 */
public class NoAnimationRcv extends RecyclerView {
  public NoAnimationRcv(Context context) {
    this(context, null);
  }

  public NoAnimationRcv(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NoAnimationRcv(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    closeAnimation();
  }

  /**
   * 取消默认动画
   */
  private void closeAnimation() {
    this.getItemAnimator().setAddDuration(0);
    this.getItemAnimator().setChangeDuration(0);
    this.getItemAnimator().setMoveDuration(0);
    this.getItemAnimator().setRemoveDuration(0);
    ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(false);
  }
}
