package com.psi.easymanager.event;

import com.psi.easymanager.module.AppComboGroupInfo;
import com.psi.easymanager.module.PxComboGroup;

/**
 * Created by dorado on 2016/8/19.
 */
public class AddComboProdClickEvent {
  private int mPos;
  private AppComboGroupInfo mComboGroup;

  public int getPos() {
    return mPos;
  }

  public AddComboProdClickEvent setPos(int pos) {
    mPos = pos;
    return this;
  }

  public AppComboGroupInfo getComboGroup() {
    return mComboGroup;
  }

  public AddComboProdClickEvent setComboGroup(AppComboGroupInfo comboGroup) {
    mComboGroup = comboGroup;
    return this;
  }
}
