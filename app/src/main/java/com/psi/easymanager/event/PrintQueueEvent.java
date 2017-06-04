package com.psi.easymanager.event;

import com.psi.easymanager.print.net.PrinterTask;

/**
 * User: ylw
 * Date: 2016-08-26
 * Time: 19:26
 * 打印 任务队列
 */
public class PrintQueueEvent {
  private PrinterTask mTask;
  private long mPrintInfoId;

  public PrintQueueEvent(PrinterTask task, long printInfoId) {
    mTask = task;
    mPrintInfoId = printInfoId;
  }

  public PrinterTask getTask() {
    return mTask;
  }

  public long getPrintInfoId() {
    return mPrintInfoId;
  }
}