package com.psi.easymanager.utils;

import android.os.Handler;
import android.os.Message;
import com.orhanobut.logger.Logger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: ylw
 * Date: 2016-12-06
 * Time: 15:20
 * 扫描 9100 端口 可用IP （扫描打印机)
 */
public class ScanIpUtils {
  public static final int SCAN_IP_RESULT = 1;

  public static void getValidIpList(String host, Handler handler) {
    ExecutorService threadPool = Executors.newFixedThreadPool(5);
    LinkedList<String> ipList = new LinkedList<>();
    CyclicBarrier barrier = new CyclicBarrier(5, new ResultThread(ipList, handler));
    for (int i = 0; i < 5; i++) {
      int start = i * 51 + 1;
      threadPool.execute(new CheckThread(barrier, ipList, host, start));
    }
  }

  /**
   * 检查线程
   */
  static class CheckThread implements Runnable {
    private CyclicBarrier mBarrier;
    private int mStart;
    private String mHost;
    private LinkedList<String> mIpList;

    public CheckThread(CyclicBarrier barrier, LinkedList<String> ipList, String host, int start) {
      this.mBarrier = barrier;
      this.mStart = start;
      this.mHost = host;
      this.mIpList = ipList;
    }

    @Override public void run() {
      for (int i = mStart; i < mStart + 51; i++) {
        if (isValid(mHost + "." + i)) {
          mIpList.add(mHost + "." + i);
        }
      }
      try {
        mBarrier.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (BrokenBarrierException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 处理结果线程
   */
  static class ResultThread implements Runnable {
    private LinkedList<String> mIpList;
    private Handler mHandler;

    public ResultThread(LinkedList<String> ipList, Handler handler) {
      this.mIpList = ipList;
      this.mHandler = handler;
    }

    @Override public void run() {
      for (String ip : mIpList) {
        Logger.e(ip);
      }
      Message msg = Message.obtain();
      msg.what = SCAN_IP_RESULT;
      msg.obj = mIpList;
      mHandler.sendMessage(msg);
    }
  }

  /**
   * 判断ip有效性
   */
  private static boolean isValid(String ip) {
    boolean isValid = false;
    Socket socket = null;
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress(ip, 9100), 1000);
      isValid = true;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return isValid;
  }
}  