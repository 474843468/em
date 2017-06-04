package com.psi.easymanager.pay.help;

import android.database.sqlite.SQLiteDatabase;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxOrderInfo;

/**
 * User: ylw
 * Date: 2016-11-19
 * Time: 16:02
 * 订单锁定与释放
 */
public class OptOrderLock {
  /**
   * 更换订单为不锁定状态
   */
  public static void optOrderLock(PxOrderInfo orderInfo, boolean isLock) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      orderInfo.setIsLock(isLock);
      DaoServiceUtil.getOrderInfoService().update(orderInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }
}