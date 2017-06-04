package com.psi.easymanager.chat;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 * Created by dorado on 2016/6/10.
 */
public class AppTableStatus implements Serializable {
  private String mTableId;

  private String mStatus;

  private long mDuration;

  private int mActualPeopleNumber;

  public String getTableId() {
    return mTableId;
  }

  public void setTableId(String tableId) {
    mTableId = tableId;
  }

  public String getStatus() {
    return mStatus;
  }

  public void setStatus(String status) {
    mStatus = status;
  }

  public long getDuration() {
    return mDuration;
  }

  public void setDuration(long duration) {
    mDuration = duration;
  }

  public int getActualPeopleNumber() {
    return mActualPeopleNumber;
  }

  public void setActualPeopleNumber(int actualPeopleNumber) {
    mActualPeopleNumber = actualPeopleNumber;
  }
}
