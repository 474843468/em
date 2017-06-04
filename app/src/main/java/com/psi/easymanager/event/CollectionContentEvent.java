package com.psi.easymanager.event;

import com.psi.easymanager.module.User;

/**
 * Created by dorado on 2016/6/6.
 */
public class CollectionContentEvent {
  private User mUser;
  private int mTimeFilter;
  private String mTableFilter;

  public User getUser() {
    return mUser;
  }

  public CollectionContentEvent setUser(User user) {
    mUser = user;
    return this;
  }

  public int getTimeFilter() {
    return mTimeFilter;
  }

  public CollectionContentEvent setTimeFilter(int timeFilter) {
    mTimeFilter = timeFilter;
    return this;
  }

  public String getTableFilter() {
    return mTableFilter;
  }

  public CollectionContentEvent setTableFilter(String tableFilter) {
    mTableFilter = tableFilter;
    return this;
  }
}
