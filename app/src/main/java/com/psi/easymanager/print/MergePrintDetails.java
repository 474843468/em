package com.psi.easymanager.print;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxOptReasonDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOptReason;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.print.module.PackagePrintDetails;
import com.psi.easymanager.utils.IOUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dorado on 2017/3/8.
 * 合并打印内容
 */

public class MergePrintDetails {
  /**
   * 下单合并
   */
  //@formatter:off
  public static void mergeByOrder(PxOrderInfo mOrderInfo,Date orderTime,SparseArray<PrintDetailsCollect> collectArray,
      List<Long> netIpList,List<PxOrderDetails> labelDetailsList) {
    List<PxOrderDetails> mergeDetailsList = merge(mOrderInfo,PxOrderDetails.ORDER_STATUS_UNORDER);
    for (PxOrderDetails mergeDetails:mergeDetailsList){
      MakePrintDetails.makePrintDetailsAndLabel(mergeDetails, orderTime, collectArray, mergeDetails.getNum(), mergeDetails.getMultipleUnitNumber(), netIpList, labelDetailsList);
    }
  }

  /**
   * 撤单合并
   */
  //@formatter:off
  public static void mergeByRevoke(PxOrderInfo mOrderInfo,Date refundTime,SparseArray<PrintDetailsCollect> collectArray,List<Long> printerIdList) {
    List<PxOrderDetails> mergeDetailsList = merge(mOrderInfo,PxOrderDetails.ORDER_STATUS_ORDER);
    for (PxOrderDetails mergeDetails:mergeDetailsList){
      MakePrintDetails.makePrintDetails(mergeDetails, refundTime, collectArray, mergeDetails.getNum(), mergeDetails.getMultipleUnitNumber(), printerIdList);
    }
  }

  private static List<PxOrderDetails> merge(PxOrderInfo mOrderInfo, String orderStatus) {
    SQLiteDatabase database = DaoServiceUtil.getOrderDetailsDao().getDatabase();
    Cursor cursor = database.rawQuery(
        "Select d.PX_PRODUCT_INFO_ID,f.NAME,m.NAME,r.NAME,sum(d.NUM),SUM(d.MULTIPLE_UNIT_NUMBER),d.STATUS AS STATUS,d.REMARKS,d.IN_COMBO"
            + " From OrderDetails d"
            + " Left Join FormatInfo f On f._id = d.PX_FORMAT_INFO_ID"
            + " Left Join MethodInfo m On m._id = d.PX_METHOD_INFO_ID"
            + " Left Join OptReason r On r._id = d.PX_OPT_REASON_ID"
            + " Where PX_ORDER_INFO_ID = " + mOrderInfo.getId()
            + " And ORDER_STATUS = " + orderStatus
            + " And IS_COMBO_DETAILS = " + PxOrderDetails.IS_COMBO_FALSE
            + " Group by PX_PRODUCT_INFO_ID,PX_FORMAT_INFO_ID,PX_METHOD_INFO_ID,REMARKS,STATUS,IN_COMBO",
        null);
    List<PxOrderDetails> mergeDetailsList = new ArrayList<>();
    while (cursor.moveToNext()) {
      int prodId = cursor.getInt(0);
      String formatName = cursor.getString(1);
      String methodName = cursor.getString(2);
      String reasonName = cursor.getString(3);
      double prodNum = cursor.getDouble(4);
      double prodMultNum = cursor.getDouble(5);
      String status = cursor.getString(6);
      String remarks = cursor.getString(7);
      String inCombo = cursor.getString(8);

      //查询
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.Id.eq(prodId))
          .unique();

      PackagePrintDetails packagePrintDetails = new PackagePrintDetails();
      packagePrintDetails.setFormatName(formatName);
      packagePrintDetails.setMethodName(methodName);
      packagePrintDetails.setReasonName(reasonName);

      //临时合并的Details,用于打印
      PxOrderDetails temporaryMergeDetails = new PxOrderDetails();
      //
      temporaryMergeDetails.setPackagePrintDetails(packagePrintDetails);

      temporaryMergeDetails.setPrintOrder(mOrderInfo);
      temporaryMergeDetails.setNum(prodNum);
      temporaryMergeDetails.setMultipleUnitNumber(prodMultNum);
      temporaryMergeDetails.setStatus(status);
      temporaryMergeDetails.setRemarks(remarks);
      temporaryMergeDetails.setInCombo(inCombo);
      temporaryMergeDetails.setPrintProd(productInfo);
      //add
      mergeDetailsList.add(temporaryMergeDetails);
    }
    if (cursor != null){
      IOUtils.closeCloseables(cursor);
    }
    return mergeDetailsList;
  }

  /**
   * 合并数据库details
   */
  //@formatter:off
  public static List<PxOrderDetails> mergeDbDetailsList(PxOrderInfo orderInfo) {
    //分组条件 ：商品、规格、做法、退菜原因、延迟状态、详情状态、备注、不在套餐、价格、会员价、折扣、赠品
    SQLiteDatabase database = DaoServiceUtil.getOrderDetailsDao().getDatabase();
    Cursor cursor = database.rawQuery(
        "Select PX_PRODUCT_INFO_ID,PX_FORMAT_INFO_ID,PX_METHOD_INFO_ID,PX_OPT_REASON_ID,"
            + "sum(NUM),SUM(MULTIPLE_UNIT_NUMBER),STATUS,ORDER_STATUS,REMARKS,IN_COMBO,UNIT_PRICE,UNIT_VIP_PRICE,"
            + "DISCOUNT_RATE,IS_GIFT,sum(PRICE),sum(VIP_PRICE),sum(REFUND_NUM),sum(REFUND_MULT_NUM)"
            + " From OrderDetails"
            + " Where PX_ORDER_INFO_ID = " + orderInfo.getId()
            + " And IN_COMBO = " + PxOrderDetails.IN_COMBO_FALSE
            + " Group by PX_PRODUCT_INFO_ID,PX_FORMAT_INFO_ID,PX_METHOD_INFO_ID,PX_OPT_REASON_ID,"
            + "STATUS,ORDER_STATUS,REMARKS,UNIT_PRICE,UNIT_VIP_PRICE,DISCOUNT_RATE,IS_GIFT", null);
    List<PxOrderDetails> mergeDetailsList = new ArrayList<>();
    while (cursor.moveToNext()) {
      int prodId = cursor.getInt(0);
      int formatId = cursor.getInt(1);
      int methodId = cursor.getInt(2);
      int reasonId = cursor.getInt(3);
      double prodNum = cursor.getDouble(4);
      double prodMultNum = cursor.getDouble(5);
      String status = cursor.getString(6);
      String orderStatus = cursor.getString(7);
      String remarks = cursor.getString(8);
      String inCombo = cursor.getString(9);
      double unitPrice = cursor.getDouble(10);
      double unitVipPrice = cursor.getDouble(11);
      int discountRate = cursor.getInt(12);
      String isGift = cursor.getString(13);
      double price = cursor.getDouble(14);
      double vipPrice = cursor.getDouble(15);
      double refundNum = cursor.getDouble(16);
      double refundMultNum = cursor.getDouble(17);

      //查询
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.Id.eq(prodId))
          .unique();
      PxFormatInfo formatInfo = DaoServiceUtil.getFormatInfoService()
          .queryBuilder()
          .where(PxFormatInfoDao.Properties.Id.eq(formatId))
          .unique();
      PxMethodInfo methodInfo = DaoServiceUtil.getMethodInfoService()
          .queryBuilder()
          .where(PxMethodInfoDao.Properties.Id.eq(methodId))
          .unique();
      PxOptReason reason = DaoServiceUtil.getOptReasonService()
          .queryBuilder()
          .where(PxOptReasonDao.Properties.Id.eq(reasonId))
          .unique();
      //临时合并的Details,用于打印
      PxOrderDetails temporaryMergeDetails = new PxOrderDetails();
      temporaryMergeDetails.setPrintProd(productInfo);
      temporaryMergeDetails.setPrintOrder(orderInfo);
      temporaryMergeDetails.setPrintFormat(formatInfo);
      temporaryMergeDetails.setPrintMethod(methodInfo);
      temporaryMergeDetails.setPrintReason(reason);
      temporaryMergeDetails.setNum(prodNum);
      temporaryMergeDetails.setMultipleUnitNumber(prodMultNum);
      temporaryMergeDetails.setStatus(status);
      temporaryMergeDetails.setRemarks(remarks);
      temporaryMergeDetails.setInCombo(inCombo);

      temporaryMergeDetails.setOrderStatus(orderStatus);
      temporaryMergeDetails.setUnitPrice(unitPrice);
      temporaryMergeDetails.setUnitVipPrice(unitVipPrice);
      temporaryMergeDetails.setDiscountRate(discountRate);
      temporaryMergeDetails.setIsGift(isGift);
      temporaryMergeDetails.setPrice(price);
      temporaryMergeDetails.setVipPrice(vipPrice);

      temporaryMergeDetails.setRefundNum(refundNum);
      temporaryMergeDetails.setRefundMultNum(refundMultNum);
      //add
      mergeDetailsList.add(temporaryMergeDetails);
    }
    IOUtils.closeCloseables(cursor);
    return mergeDetailsList;
  }

}
