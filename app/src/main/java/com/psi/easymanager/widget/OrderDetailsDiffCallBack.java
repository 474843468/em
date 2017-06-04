package com.psi.easymanager.widget;

import android.support.v7.util.DiffUtil;
import com.psi.easymanager.module.PxOrderDetails;
import java.util.List;

/**
 * 作者：${ylw} on 2017-03-28 11:34
 */
public class OrderDetailsDiffCallBack extends DiffUtil.Callback {
  private List<PxOrderDetails> mOldDetails;
  private List<PxOrderDetails> mNewDetails;

  public OrderDetailsDiffCallBack(List<PxOrderDetails> oldDetails, List<PxOrderDetails> newDeails) {
    mOldDetails = oldDetails;
    mNewDetails = newDeails;
  }

  @Override public int getOldListSize() {
    return mOldDetails == null ? 0 : mOldDetails.size();
  }

  @Override public int getNewListSize() {
    return mNewDetails == null ? 0 : mNewDetails.size();
  }

  @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    return mOldDetails.get(oldItemPosition)
        .getObjectId()
        .equals(mNewDetails.get(newItemPosition).getObjectId());
  }

  @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
    PxOrderDetails oldDetails = mOldDetails.get(oldItemPosition);
    PxOrderDetails newDetails = mNewDetails.get(newItemPosition);
    if (!oldDetails.toString().equals(newDetails.toString())) return false;
    return true;
  }
}
