package com.psi.easymanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxPrinterInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.PrintLabelQueueEvent;
import com.psi.easymanager.event.PrintQueueEvent;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.constant.EpsonPosPrinterCommand;
import com.psi.easymanager.print.net.PrintLabelTask;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.net.PutTask;
import com.psi.easymanager.print.net.event.IPrintEvent;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.SPUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class PrintQueueService extends Service implements IPrintEvent {
  private static final long LABEL_PRINTER_ID = -1;//默认标签打印机id -1
  private static final int CONNECT_TIME_OUT = 15000;//连接超时时间

  private ConcurrentHashMap<Long, LinkedBlockingQueue> queueArray = null;//Map 打印机id对应的任务队列
  private ConcurrentHashMap<Long, ExecutorService> putThreadPool = null;
  //Map 打印机id 对应的put任务线程（包含标签打印）

  private Vector<String> mOpenCashBoxVector = null;//开钱箱任务队列
  private List<PxPrinterInfo> mCashInfoList = null;//收银打印机 用于开钱箱

  private List<PrintQueue> mPrintQueueList = null; //容器 存放具体的打印Runnable 用于释放
  private PrintLabelQueue mLabelTask;//具体的标签打印Runnable 用于释放

  private ExecutorService mPutOpenCashThread; //存放开钱箱 任务线程
  private ExecutorService mPrintLabelThread;//标签打印任务
  private ExecutorService printThreadPool = null;//具体的打印机id 对应的打印任务线程池（不包含标签打印机）

  private boolean isDestroy = false;
  private boolean isStopLabel = true;//标签打印停止

  public PrintQueueService() {
  }

  @Override public void onCreate() {
    super.onCreate();
    //EventBus.getDefault().register(this);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  //@formatter:off
  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    isDestroy = false;
    //PrintEventManager.getManager().regist(this);
    PrintEventManager.getManager().registPrintEvent(this);
    //1.普通打印机
    List<PxPrinterInfo> mInfoList = DaoServiceUtil.getPrinterInfoService()
        .queryBuilder()
        .where(PxPrinterInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPrinterInfoDao.Properties.Status.eq(PxPrinterInfo.ENABLE))
        .list();
    //获取 所有收银打印机  用于开钱箱
    mCashInfoList = DaoServiceUtil.getPrinterInfoService()
        .queryBuilder()
        .where(PxPrinterInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPrinterInfoDao.Properties.Status.eq(PxPrinterInfo.ENABLE))
        .where(PxPrinterInfoDao.Properties.Type.eq(PxPrinterInfo.TYPE_CASH))
        .list();

    if (mCashInfoList != null && mCashInfoList.size() > 0) {
      mOpenCashBoxVector = new Vector<>(mCashInfoList.size());
    }
    if (mInfoList.size() > 0) {
      printThreadPool = Executors.newFixedThreadPool(mInfoList.size());
      mPrintQueueList = new ArrayList<>();
      for (PxPrinterInfo info : mInfoList) {
        LinkedBlockingQueue<PrinterTask> queue = getQueue(info.getId());
        PrintQueue task = new PrintQueue(info, queue);
        mPrintQueueList.add(task);
        printThreadPool.execute(task);
      }
    }

    //2.标签打印机
    boolean isOpen = (boolean) SPUtils.get(PrintQueueService.this, Constants.SWITCH_LABEL_PRINT, false);
    if (isOpen && mPrintLabelThread == null) {
      openLabelPrint();
    }

    return START_STICKY;
  }

  //@formatter:off
  //标签打印任务
  class PrintLabelQueue implements Runnable {
    private String ip;
    private int width;
    private int height;
    private int gap;
    private LinkedBlockingQueue<PxOrderDetails> queue;

    public PrintLabelQueue(LinkedBlockingQueue<PxOrderDetails> queue, String ip, int width, int height, int gap) {
      this.queue = queue;
      this.ip = ip;
      this.width = width;
      this.height = height;
      this.gap = gap;
    }

    Socket socket = null;
    OutputStream outputStream = null;

    @Override public void run() {
      SocketAddress socketAddress = new InetSocketAddress(ip, 9100);
      PrintLabelTask printLabelTask = new PrintLabelTask();
      ExecutorService putThread = getPutThread(LABEL_PRINTER_ID);
      String companyCode = (String) SPUtils.get(PrintQueueService.this, Constants.SAVE_STORE_NUM, "100000");
      PxOrderDetails details = null;
      while (!isDestroy && !isStopLabel) {
        try {
          details = queue.take();
          socket = new Socket();
          socket.connect(socketAddress, CONNECT_TIME_OUT);
          outputStream = socket.getOutputStream();
          printLabelTask.printLabelOrderDetails(width, height, gap, companyCode, outputStream, details);
          //// 绘制图片
          // Bitmap bitmap = BitmapFactory.decodeResource(App.getContext().getResources(), R.mipmap.bitmap_label);
          //printLabelTask.printLabelBitmap(width,height,outputStream,bitmap);
        } catch (IOException e) {
          Log.e("Print",e.toString());
          //SocketException SocketTimeoutException 再次存放任务
          if (!isDestroy && !isStopLabel && details != null) {
            putThread.execute(new PutTask(details, queue));
            details = null;
          }
        }catch (Exception e){
          Log.e("Print",e.toString());
        }finally {
          IOUtils.closeCloseables(socket, outputStream, null);
          try {
            Thread.sleep(300);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
     /**
     * 强制关闭
     */
    private void forceClose() {
      IOUtils.closeCloseables(socket, outputStream, null);
    }
  }
  //@formatter:on

  /**
   * 取出  开钱箱、打印任务
   */
  class PrintQueue implements Runnable {
    private PxPrinterInfo printerInfo;
    private LinkedBlockingQueue<PrinterTask> queue;

    public PrintQueue(PxPrinterInfo printerInfo, LinkedBlockingQueue<PrinterTask> queue) {
      this.printerInfo = printerInfo;
      this.queue = queue;
    }

    PrinterTask task = null;
    Socket socket = null;
    OutputStream outputStream = null;
    PrintWriter printWriter = null;

    @Override public void run() {
      String ip = printerInfo.getIpAddress();
      long printerInfoId = printerInfo.getId();
      SocketAddress socketAddress = new InetSocketAddress(ip, 9100);
      boolean isCashPrinter = PxPrinterInfo.TYPE_CASH.equals(printerInfo.getType());

      ExecutorService putThread = getPutThread(printerInfoId);
      while (!isDestroy) {
        try {
          //开钱箱  条件:收银打印机、有属于该打印机的开钱箱任务
          if (isCashPrinter && mOpenCashBoxVector != null && mOpenCashBoxVector.contains(ip)) {
            connectSocket(socketAddress);
            printWriter.write(EpsonPosPrinterCommand.OPEN_BOX);
            printWriter.flush();
            outputStream.flush();
            //开成功后 移除
            mOpenCashBoxVector.remove(ip);
            Log.w("Print", ip + "开钱箱成功!");
          } else { // 打印
            task = queue.take();
            if (task.getIp() != null) { //假数据 不打印
              connectSocket(socketAddress);
              task.run(outputStream, printWriter);
              task = null;//置空
              Log.w("Print", ip + "打印任务完了");
            }
          }
        } catch (IOException e) {
          Log.e("Print", e.toString());
          if (!isDestroy && task != null && task.getIp() != null) {
            putThread.execute(new PutTask(task, queue));
            task = null;//置空
          }
        } catch (Exception e) {
          Log.e("Print", e.toString());
        } finally {
          IOUtils.closeCloseables(socket, outputStream, printWriter);
          try {
            Thread.sleep(300);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }

    /**
     * connect Socket
     *
     * @throws IOException
     */
    private void connectSocket(SocketAddress socketAddress) throws IOException {
      socket = new Socket();
      socket.connect(socketAddress, CONNECT_TIME_OUT);
      outputStream = socket.getOutputStream();
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "GBK");
      printWriter = new PrintWriter(outputStreamWriter, true);
    }

    /**
     * 强制关闭
     */
    private void forceClose() {
      IOUtils.closeCloseables(socket, outputStream, printWriter);
    }
  }
  //@formatter:off
  /**
   * 一个打印机ID 对应一个队列
   */
  private synchronized LinkedBlockingQueue getQueue(long printerId) {
    if (queueArray == null) {
      queueArray = new ConcurrentHashMap<>();
    }
    LinkedBlockingQueue queue = queueArray.get(printerId);
    if (queue == null) {
      queue = new LinkedBlockingQueue<>(10);
      queueArray.put(printerId, queue);
    }
    return queue;
  }

  /**
   * 一个打印机ID 对应一个放的线程
   */
  private ExecutorService getPutThread(long id) {
    if (putThreadPool == null) {
      putThreadPool = new ConcurrentHashMap<>();
    }
    ExecutorService service = putThreadPool.get(id);
    if (service == null) {
      service = Executors.newSingleThreadExecutor();
      putThreadPool.put(id, service);
    }
    return service;
  }

  //@formatter:on

  /**
   * 普通任务
   */
  @Override public void receivePrintTask(PrintQueueEvent event) {
    PrinterTask task = event.getTask();
    if (isDestroy || task == null) return;
    ExecutorService putThread = getPutThread(task.getPrinterId());
    LinkedBlockingQueue<PrinterTask> queue = getQueue(task.getPrinterId());
    putThread.execute(new PutTask(task, queue));
  }

  /**
   * 标签打印任务
   */
  @Override public void receiveLabelPrintTask(PrintLabelQueueEvent event) {
    List<PxOrderDetails> detailsList = event.getDetailsList();
    if (isDestroy || isStopLabel || detailsList == null) return;
    ExecutorService putThread = getPutThread(LABEL_PRINTER_ID);
    LinkedBlockingQueue<PrinterTask> queue = getQueue(LABEL_PRINTER_ID);
    for (PxOrderDetails details : detailsList) {
      putThread.execute(new PutTask(details, queue));
    }
    Log.w("Print", "标签打印任务:" + queue.size());
  }

  /**
   * 开钱箱
   */
  @Override public void receiveOpenCashBoxEvent() {
    if (isDestroy || mOpenCashBoxVector == null || mCashInfoList == null) return;
    getPutOpenCashBoxThread();
    mPutOpenCashThread.execute(new Runnable() {
      @Override public void run() {
        for (PxPrinterInfo printerInfo : mCashInfoList) {
          //没有再加
          if (!mOpenCashBoxVector.contains(printerInfo.getIpAddress())) {
            mOpenCashBoxVector.add(printerInfo.getIpAddress());
            //当前 可能阻塞在取打印任务 ，加空任务 跳一下
            LinkedBlockingQueue<PrinterTask> queue = getQueue(printerInfo.getId());
            if (queue != null && queue.size() == 0) {
              try {
                queue.put(new PrinterTask(null));
              } catch (InterruptedException e) {
                Log.e("Print",e.toString());
              }
            }
          }
        }
      }
    });
  }

  /**
   * 标签打印机开关
   */
  @Override public void switchLabelPrint(boolean isOpen) {
    if (isOpen) {
      if (mPrintLabelThread == null) openLabelPrint();
    } else {
      if (printThreadPool == null) {
        stopSelf();
      } else if (mPrintLabelThread != null) closeLabelPrint();
    }
  }

  /**
   * 存放开钱箱任务 线程
   */
  private void getPutOpenCashBoxThread() {
    if (mPutOpenCashThread == null) {
      mPutOpenCashThread = Executors.newSingleThreadExecutor();
    }
  }
  //@formatter:off

   /**
   * 开启标签打印
   */
  private void openLabelPrint() {
    int width = (int) SPUtils.get(PrintQueueService.this, Constants.LABEL_PRINTER_PAPER_WIDTH, 0);
    int height = (int) SPUtils.get(PrintQueueService.this, Constants.LABEL_PRINTER_PAPER_HEIGHT, 0);
    int gap = (int) SPUtils.get(PrintQueueService.this, Constants.LABEL_PRINTER_PAPER_GAP, 2);
    String labelPrintIp = (String) SPUtils.get(PrintQueueService.this, Constants.LABEL_PRINTER_IP, "");
    if (!TextUtils.isEmpty(labelPrintIp) && width > 0 && height > 0) {
      mPrintLabelThread = Executors.newSingleThreadExecutor();
      //标签打印机 ID = LABEL_PRINTER_ID
      LinkedBlockingQueue queue = getQueue(LABEL_PRINTER_ID);
      mLabelTask = new PrintLabelQueue(queue, labelPrintIp, width, height,gap);
      mPrintLabelThread.execute(mLabelTask);
      isStopLabel = false;
      Log.w("Print","标签打印机开启");
    }
  }
   /**
   * 关闭标签打印机
   */
  private void closeLabelPrint() {
    isStopLabel = true;
    if (mLabelTask!= null) {
      mLabelTask.forceClose();
       mLabelTask = null;
    }
    //关闭标签打印打印任务线程
    closeExecutor(mPrintLabelThread);
    //关闭标签打印机存放任务的线程
    if (putThreadPool != null && putThreadPool.contains(LABEL_PRINTER_ID)) {
      putThreadPool.remove(LABEL_PRINTER_ID);
      putThreadPool = null;
    }
    //清空标签任务队列
    if (queueArray != null) {
      LinkedBlockingQueue queue = queueArray.get(LABEL_PRINTER_ID);
      if (queue != null) {
        queue.clear();
        queueArray.remove(queue);
      }
    }
     Log.w("Print","标签打印机关闭");
  }
  //@formatter:on
  @Override public void onDestroy() {
    super.onDestroy();
    isDestroy = true;
    isStopLabel = true;
    //EventBus.getDefault().unregister(this);
    PrintEventManager.getManager().unRegistPrintEvent(this);

    if (putThreadPool != null) {
      for (long key : putThreadPool.keySet()) {
        closeExecutor(putThreadPool.get(key));
      }
      clearConcurrentHashMap(putThreadPool);
    }
    //清空 容器
    clearConcurrentHashMap(queueArray);
    //关闭普通打印
    closeExecutor(printThreadPool);
    //关闭标签打印
    closeExecutor(mPrintLabelThread);
    //关闭存放开钱箱的线程
    closeExecutor(mPutOpenCashThread);
    //label
    closeLabelPrint();
    if (mPrintQueueList != null) {
      for (PrintQueue printQueue : mPrintQueueList) {
        if (printQueue == null) continue;
        printQueue.forceClose();
        printQueue = null;
      }
    }
    if (mOpenCashBoxVector != null) {
      mOpenCashBoxVector.clear();
      mOpenCashBoxVector = null;
    }
  }

  //close ExecutorService
  private void closeExecutor(ExecutorService executor) {
    if (executor != null) {
      executor.shutdownNow();
      executor = null;
    }
  }

  //close
  private void clearConcurrentHashMap(ConcurrentHashMap map) {
    if (map != null) {
      map.clear();
      map = null;
    }
  }
}
