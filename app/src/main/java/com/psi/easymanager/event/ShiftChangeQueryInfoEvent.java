package com.psi.easymanager.event;

import com.psi.easymanager.print.module.ShiftWork;

/**
 * Created by dorado on 2016/10/15.
 * 囧啊接班
 */
public class ShiftChangeQueryInfoEvent {
  public static final int AREA_ALL = 0;//全部
  public static final int AREA_TABLE = 1;//桌位单
  public static final int AREA_RETAIL = 2;//零售单
  public static final int AREA_HALL = 3;//大厅
  public static final int AREA_PARLOR = 4;//包厢

  public static final int TYPE_CATEGORY_COLLECT = 1;//分类统计
  public static final int TYPE_ORDER_COLLECT = 2;//账单汇总
  public static final int TYPE_ALL_ORDER = 3;//所有订单

  private long mUserId;
  private int mArea;
  private long mEndTime;
  private int mType;

  private ShiftWork mShiftWork;

  public ShiftWork getShiftWork() {
    return mShiftWork;
  }

  public ShiftChangeQueryInfoEvent setShiftWork(ShiftWork shiftWork) {
    mShiftWork = shiftWork;
    return this;
  }

  public long getUserId() {
    return mUserId;
  }

  public ShiftChangeQueryInfoEvent setUserId(long userId) {
    mUserId = userId;
    return this;
  }

  public int getArea() {
    return mArea;
  }

  public ShiftChangeQueryInfoEvent setArea(int area) {
    mArea = area;
    return this;

  }

  public long getEndTime() {
    return mEndTime;
  }

  public ShiftChangeQueryInfoEvent setEndTime(long endTime) {
    mEndTime = endTime;
    return this;
  }

  public int getType() {
    return mType;
  }

  public ShiftChangeQueryInfoEvent setType(int type) {
    mType = type;
    return this;
  }
}
