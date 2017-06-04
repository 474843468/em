package com.psi.easymanager.event;

/**
 * Created by dorado on 2016/10/12.
 */
public class AutoOrderEvent {
  private boolean autoOrder;

  public boolean isAutoOrder() {
    return autoOrder;
  }

  public AutoOrderEvent setAutoOrder(boolean autoOrder) {
    this.autoOrder = autoOrder;
    return this;
  }
}