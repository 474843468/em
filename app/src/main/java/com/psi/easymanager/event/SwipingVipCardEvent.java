package com.psi.easymanager.event;

/**
 * Created by Administrator on 2017-02-16.
 */
public class SwipingVipCardEvent {
  private boolean canSwiping;

  public boolean isCanSwiping() {
    return canSwiping;
  }

  public SwipingVipCardEvent setCanSwiping(boolean canSwiping) {
    this.canSwiping = canSwiping;
    return this;
  }
}
