package com.psi.easymanager.event;

import com.psi.easymanager.module.PxProductCategory;

/**
 * Created by dorado on 2016/6/6.
 * 点击商品，向OverBillSaleContentFragment传递商品和筛选条件
 */
public class SaleContentEvent {
  private PxProductCategory mCategory;

  private int mTimeFilter;

  public PxProductCategory getCategory() {
    return mCategory;
  }

  public SaleContentEvent setCategory(PxProductCategory category) {
    mCategory = category;
    return this;
  }

  public int getTimeFilter() {
    return mTimeFilter;
  }

  public SaleContentEvent setTimeFilter(int timeFilter) {
    mTimeFilter = timeFilter;
    return this;
  }
}
