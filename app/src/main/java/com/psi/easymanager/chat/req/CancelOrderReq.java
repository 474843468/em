package com.psi.easymanager.chat.req;

import java.io.Serializable;

/**
 * Created by zjq on 2016/5/7.
 * 撤单申请
 */
public class CancelOrderReq  implements Serializable {
  private long mOrderId;
  private String mWaiterId;//服务生id

  public long getOrderId() {
    return mOrderId;
  }

  public void setOrderId(long orderId) {
    mOrderId = orderId;
  }

  public String getWaiterId() {
    return mWaiterId;
  }

  public void setWaiterId(String waiterId) {
    mWaiterId = waiterId;
  }
}
