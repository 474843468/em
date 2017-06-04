package com.psi.easymanager.event;

import com.psi.easymanager.module.AppEditComboProdInfo;
import com.psi.easymanager.module.PxOrderDetails;

/**
 * Created by dorado on 2016/8/19.
 */
public class EditComboProdEvent {

  public static final String TYPE_ADD = "Add";
  public static final String TYPE_EDIT = "Edit";
  public static final String TYPE_ORDERED = "Ordered";

  private PxOrderDetails comboDetails;
  private String mType;

  public PxOrderDetails getComboDetails() {
    return comboDetails;
  }

  public EditComboProdEvent setComboDetails(PxOrderDetails comboDetails) {
    this.comboDetails = comboDetails;
    return this;
  }

  public String getType() {
    return mType;
  }

  public EditComboProdEvent setType(String type) {
    mType = type;
    return this;
  }
}
