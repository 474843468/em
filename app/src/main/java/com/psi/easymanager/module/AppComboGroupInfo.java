package com.psi.easymanager.module;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dorado on 2016/8/18.
 */
public class AppComboGroupInfo implements Serializable {
  //分组
  private PxComboGroup mComboGroup;
  //已选数量
  private int mNumSelected;
  //允许选择的数量
  private int mNumAllow;
  //分组包含的商品
  private List<AppComboGroupProdInfo> mAppComboGroupProdInfoList;

  public PxComboGroup getComboGroup() {
    return mComboGroup;
  }

  public void setComboGroup(PxComboGroup comboGroup) {
    mComboGroup = comboGroup;
  }

  public int getNumSelected() {
    return mNumSelected;
  }

  public void setNumSelected(int numSelected) {
    mNumSelected = numSelected;
  }

  public List<AppComboGroupProdInfo> getAppComboGroupProdInfoList() {
    return mAppComboGroupProdInfoList;
  }

  public void setAppComboGroupProdInfoList(List<AppComboGroupProdInfo> appComboGroupProdInfoList) {
    mAppComboGroupProdInfoList = appComboGroupProdInfoList;
  }

  public int getNumAllow() {
    return mNumAllow;
  }

  public void setNumAllow(int numAllow) {
    mNumAllow = numAllow;
  }
}
