package com.psi.easymanager.event;

/**
 * Created by dorado on 2016/8/24.
 */
public class ConfirmComboEvent {
  public static final String TYPE_ADD = "Add";
  public static final String TYPE_EDIT = "Edit";
  public static final String TYPE_ORDERED = "Ordered";

  private String mType;
  private int mComboNum;
  private boolean isDelay;

  public String getType() {
    return mType;
  }

  public ConfirmComboEvent setType(String type) {
    mType = type;
    return this;
  }

  public int getComboNum() {
    return mComboNum;
  }

  public ConfirmComboEvent setComboNum(int comboNum) {
    mComboNum = comboNum;
    return this;
  }

  public boolean isDelay() {
    return isDelay;
  }

  public ConfirmComboEvent setDelay(boolean delay) {
    isDelay = delay;
    return this;
  }
}
