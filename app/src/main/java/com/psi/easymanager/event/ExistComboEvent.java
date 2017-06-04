package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderDetails;

/**
 * Created by dorado on 2016/9/26.
 */
public class ExistComboEvent {
  private PxOrderDetails mDetails;
  private String mType;

  public PxOrderDetails getDetails() {
    return mDetails;
  }

  public ExistComboEvent setDetails(PxOrderDetails details) {
    mDetails = details;
    return this;
  }

  public String getType() {
    return mType;
  }

  public ExistComboEvent setType(String type) {
    mType = type;
    return this;
  }
}
