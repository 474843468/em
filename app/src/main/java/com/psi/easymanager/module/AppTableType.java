package com.psi.easymanager.module;

/**
 * Created by zjq on 2016/3/31.
 * 暂时仅app用，餐桌分类
 */
public class AppTableType {
  //public static final int HALL = 0;//大厅
  //public static final int PARLOR = 1;//包厢
  public static final String ALL = "2";//全部
  public static final String RETAIL = "3";//零售

  private String type;//类型
  private String name;//类型名称
  private PxTableArea mTableArea;

  public PxTableArea getTableArea() {
    return mTableArea;
  }

  public void setTableArea(PxTableArea tableArea) {
    mTableArea = tableArea;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
