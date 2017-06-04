package com.psi.easymanager.module;

/**
 * Created by dorado on 2016/6/3.
 */
public class AppPrinterDetails<T> {
  public static final int TYPE_DETAILS = 0;
  public static final int TYPE_TABALTER = 1;

  //打印Detasils
  private T mDetails;
  //打印桌台变更信息
  private PxTableAlteration mAlteration;
  //类型 (0:Details,1:TableAlteration)
  private PrintDetailsCollect mCollect;
  //一菜一切 专用
  private PdConfigRel rel;

  public PdConfigRel getRel() {
    return rel;
  }

  public void setRel(PdConfigRel rel) {
    this.rel = rel;
  }

  public PrintDetailsCollect getCollect() {
    return mCollect;
  }

  public void setCollect(PrintDetailsCollect collect) {
    mCollect = collect;
  }

  private int type;

  public T getDetails() {
    return mDetails;
  }

  public void setDetails(T details) {
    mDetails = details;
  }

  public PxTableAlteration getAlteration() {
    return mAlteration;
  }

  public void setAlteration(PxTableAlteration alteration) {
    mAlteration = alteration;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
