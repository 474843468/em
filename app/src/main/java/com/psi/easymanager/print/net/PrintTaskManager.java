package com.psi.easymanager.print.net;

import android.util.SparseArray;
import com.psi.easymanager.dao.PxPrinterInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.PrintQueueEvent;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.constant.BTPrintConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ylw
 * Date: 2016-10-28
 * Time: 14:25
 * 网口普通票据打印管理
 */
//@formatter:off
public class PrintTaskManager {
  private static List<PxPrinterInfo> mCashPrinterList;//收银打印机
  private static List<PxPrinterInfo> mKitchenPrinterList;//后厨打印及

  /**
   * 初始化打印机  数据更新后
   */
  public static void initPrinterList() {
    mCashPrinterList = DaoServiceUtil.getPrinterInfoService()
        .queryBuilder()
        .where(PxPrinterInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPrinterInfoDao.Properties.Type.eq(PxPrinterInfo.TYPE_CASH))
        .where(PxPrinterInfoDao.Properties.Status.eq(PxPrinterInfo.ENABLE))
        .list();
    mKitchenPrinterList = DaoServiceUtil.getPrinterInfoService()
        .queryBuilder()
        .where(PxPrinterInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPrinterInfoDao.Properties.Status.eq(PxPrinterInfo.ENABLE))
        .list();
  }

  /**
   * 打印收银任务
   */
  public static void printCashTask(PrinterTask task) {
    //没有打印机
    if (mCashPrinterList != null && !mCashPrinterList.isEmpty()) {
      for (PxPrinterInfo printerInfo : mCashPrinterList) {
        PrinterTask clone = task.clone();
        if (clone != null) {
          clone.setPrintInfo(printerInfo);
          long id = printerInfo.getId();
          PrintEventManager.getManager().postPrintEvent(PrintEventManager.PRINT_TASK, new PrintQueueEvent(clone, id));
        }
      }
    }
  }

  /**
   * 打印移并桌信息
   */
   public static void printTableAlteration(PxTableAlteration pxTableAlteration) {
    //没有打印机
    if (mKitchenPrinterList != null && !mKitchenPrinterList.isEmpty()) {
      PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_TABLE, pxTableAlteration);
      for (PxPrinterInfo printerInfo : mKitchenPrinterList) {
        long id = printerInfo.getId();
        PrinterTask clone = task.clone();
        if (clone != null) {
          clone.setPrintInfo(printerInfo);
          PrintEventManager.getManager().postPrintEvent(PrintEventManager.PRINT_TASK, new PrintQueueEvent(clone, id));
        }
      }
    }
   }

  /**
   * 打印后厨任务
   *
   * @param collectArray collect
   * @param ipList 打印机IPList
   * @param isRefund 是否是退菜或退单的
   */
  public static void printKitchenTask(SparseArray<PrintDetailsCollect> collectArray, List<Long> ipList, boolean isRefund) {
    if (collectArray.size() == 0 || ipList.isEmpty()) return;
    //没有打印机
    if (mKitchenPrinterList != null && !mKitchenPrinterList.isEmpty()) {
      final List<PrintDetailsCollect> collectList = new ArrayList<>();
      for (int i = 0; i < collectArray.size(); i++) {
        PrintDetailsCollect collect = collectArray.get(collectArray.keyAt(i));
        collectList.add(collect);
      }
      int printMode = isRefund ? BTPrintConstants.PRINT_MODE_COLLECT_REFUND : BTPrintConstants.PRINT_MODE_COLLECT;
      //后厨打印
      PrinterTask task = new PrinterTask(printMode, collectList);
      for (PxPrinterInfo printerInfo : mKitchenPrinterList) {
        long id = printerInfo.getId();
        //包含此打印机
        if (ipList == null || ipList.contains(id)) {
          PrinterTask clone = task.clone();
          if (clone != null) {
            clone.setPrintInfo(printerInfo);
            PrintEventManager.getManager().postPrintEvent(PrintEventManager.PRINT_TASK, new PrintQueueEvent(clone, id));
          }
        }
      }
    }
  }
}