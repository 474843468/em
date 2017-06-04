package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderDetails;
import java.util.List;

/**
 * User: ylw
 * Date: 2016-11-17
 * Time: 11:59
 * 标签打印任务
 */
public class PrintLabelQueueEvent {
  private List<PxOrderDetails> mDetailsList;

  public PrintLabelQueueEvent(List<PxOrderDetails> detailsList) {
    mDetailsList = detailsList;
  }

  public List<PxOrderDetails> getDetailsList() {
    return mDetailsList;
  }
}