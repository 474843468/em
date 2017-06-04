package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderDetails;

/**
 * Created by dorado on 2016/8/22.
 */
public class ComboRefundProdEvent {
  private PxOrderDetails mDetails;
  private int mNum;

  public PxOrderDetails getDetails() {
    return mDetails;
  }

  public ComboRefundProdEvent setDetails(PxOrderDetails details) {
    mDetails = details;
    return this;
  }

  public int getNum() {
    return mNum;
  }

  public ComboRefundProdEvent setNum(int num) {
    mNum = num;
    return this;

  }
}
