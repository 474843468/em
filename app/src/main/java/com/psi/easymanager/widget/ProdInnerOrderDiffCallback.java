package com.psi.easymanager.widget;

import android.support.v7.util.DiffUtil;
import com.psi.easymanager.module.ProdInnerOrder;
import java.util.List;

/**
 * 作者：${ylw} on 2017-03-27 12:10
 */
public class ProdInnerOrderDiffCallback extends DiffUtil.Callback {
  private List<ProdInnerOrder> mOldList, mNewList;

  public ProdInnerOrderDiffCallback(List<ProdInnerOrder> oldList, List<ProdInnerOrder> newList) {
    mOldList = oldList;
    mNewList = newList;
  }

  @Override public int getOldListSize() {
    return mOldList == null ? 0 : mOldList.size();
  }

  @Override public int getNewListSize() {
    return mNewList == null ? 0 : mNewList.size();
  }

  @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    String oldId = mOldList.get(oldItemPosition).getProductInfo().getObjectId();
    String newId = mNewList.get(newItemPosition).getProductInfo().getObjectId();
    return oldId.equals(newId);
  }

  @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
    ProdInnerOrder oldProdInnerOrder = mOldList.get(oldItemPosition);
    ProdInnerOrder newProdInnerOrder = mNewList.get(newItemPosition);
    if (!oldProdInnerOrder.toString().equals(newProdInnerOrder.toString())) return false;
    return true;
  }
}
