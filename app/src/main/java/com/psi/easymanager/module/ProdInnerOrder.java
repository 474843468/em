package com.psi.easymanager.module;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.PromotioDetailsHelp;

/**
 * 作者：${ylw} on 2017-03-27 11:55
 * 商品在订单下的详细信息 用于CashMenuProductAdapter
 * 数量、促销计划详情
 */
//@formatter:off
public class ProdInnerOrder {
  private PxProductInfo mProductInfo;
  private int mNum;
  private PxPromotioDetails mPromotioDetails;

  public ProdInnerOrder(PxProductInfo productInfo, PxOrderInfo orderInfo) {
    mProductInfo = productInfo;
    mNum = calcNum(productInfo, orderInfo);
    PxPromotioInfo dbPromotioInfo = orderInfo == null ? null : orderInfo.getDbPromotioById();
    mPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(dbPromotioInfo, null, productInfo);
    //用于equals
    promotioDetailsId = mPromotioDetails == null ? "" : mPromotioDetails.getObjectId();
  }

  private int calcNum(PxProductInfo productInfo, PxOrderInfo orderInfo) {
    if (orderInfo == null) return 0;
    int prodNum = 0;
    SQLiteDatabase db = DaoServiceUtil.getProductInfoDao().getDatabase();
    Cursor cursor = db.rawQuery(
        "Select sum(NUM) From OrderDetails"
            + " Where PX_ORDER_INFO_ID = " + orderInfo.getId()
            + " And PX_PRODUCT_INFO_ID = " + productInfo.getId()
            + " And ORDER_STATUS != " + PxOrderDetails.ORDER_STATUS_REFUND
            + " And IN_COMBO = " + PxOrderDetails.IN_COMBO_FALSE
            + " And (IS_COMBO_TEMPORARY_DETAILS = 0 or IS_COMBO_TEMPORARY_DETAILS IS NULL)", null);
    while (cursor.moveToNext()) {
      prodNum = cursor.getInt(0);
    }
    IOUtils.closeCloseables(cursor);
    return prodNum;
  }

  public PxProductInfo getProductInfo() {
    return mProductInfo;
  }

  public int getNum() {
    return mNum;
  }

  public PxPromotioDetails getPromotioDetails() {
    return mPromotioDetails;
  }

  //@formatter:on
  private String promotioDetailsId;
  //private String prodObjId;

  @Override public String toString() {
    return "ProdInnerOrder{" + "mNum=" + mNum + ", promotioDetailsId='" + promotioDetailsId + '\''
        + '}';
  }
}
