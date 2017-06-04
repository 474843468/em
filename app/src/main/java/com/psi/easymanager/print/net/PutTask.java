package com.psi.easymanager.print.net;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: ylw
 * Date: 2016-11-17
 * Time: 12:03
 * 存放 打印任务
 */
public class PutTask implements Runnable {
  private Object mTask;
  private LinkedBlockingQueue mQueue;

  public PutTask(Object task, LinkedBlockingQueue queue) {
    this.mQueue = queue;
    this.mTask = task;
  }

  @Override public void run() {
    try {
      mQueue.put(mTask);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}  