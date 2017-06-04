package com.psi.easymanager.print;

import android.util.SparseArray;
import com.psi.easymanager.dao.PxProductConfigPlanRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PdConfigRel;
import com.psi.easymanager.module.PrintDetails;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOptReason;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxProductConfigPlan;
import com.psi.easymanager.module.PxProductConfigPlanRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.print.module.PackagePrintDetails;
import java.util.Date;
import java.util.List;

import static com.psi.easymanager.ui.fragment.CashBillFragment.mOrderInfo;

/**
 * User: ylw
 * Date: 2016-12-29
 * Time: 15:55
 * FIXME
 */
//@formatter:off
public class MakePrintDetails {
  /**
   * 生成打印详情  不带标签打印
   */
  public static void makePrintDetails(PxOrderDetails details, Date refundTime, SparseArray<PrintDetailsCollect> collectArray, Double num, Double multNum, List<Long> ipList) {
    //商品
    PxProductInfo prodInfo = details.getPrintProd();
    PxOrderInfo dbOrder = details.getPrintOrder();
    PackagePrintDetails packagePrintDetails = details.getPackagePrintDetails();
    //后厨出单
    if (PxProductInfo.IS_PRINT.equals(prodInfo.getIsPrint())) {
      //生成对应的PrintDetails
      PrintDetails printDetails = new PrintDetails();
      printDetails.setStatus(details.getStatus());
      printDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
      printDetails.setNum(num.doubleValue());
      printDetails.setMultipleUnitNumber(multNum);
      printDetails.setRemarks(details.getRemarks());
      printDetails.setInCombo(details.getInCombo());
      printDetails.setDbOrder(details.getPrintOrder());
      printDetails.setDbProduct(prodInfo);

      printDetails.setFormatName(packagePrintDetails.getFormatName());
      printDetails.setMethodName(packagePrintDetails.getMethodName());
      printDetails.setReasonName(packagePrintDetails.getReasonName());

      DaoServiceUtil.getPrintDetailsService().saveOrUpdate(printDetails);

      //建立详情和配菜方案的rel
      List<PxProductConfigPlanRel> prodConfigRelList =
          DaoServiceUtil.getProductConfigPlanRelService()
              .queryBuilder()
              .where(PxProductConfigPlanRelDao.Properties.PxProductInfoId.eq(prodInfo.getId()))
              .where(PxProductConfigPlanRelDao.Properties.DelFlag.eq(0))
              .list();
      for (PxProductConfigPlanRel rel : prodConfigRelList) {
        //Rel无效
        //if (rel == null || (!rel.getDelFlag().equals("0"))) continue;
        //获取配菜方案
        PxProductConfigPlan dbProductConfigPlan = rel.getDbProductConfigPlan();
        //配菜方案无效
        if (dbProductConfigPlan == null || (!dbProductConfigPlan.getDelFlag().equals("0"))) {
          continue;
        }
        PxPrinterInfo dbPrinter = dbProductConfigPlan.getDbPrinter();
        // 打印机无效 没启用
        if (dbPrinter == null || !("0".equals(dbPrinter.getDelFlag())) || PxPrinterInfo.DIS_ENABLE.equals(dbPrinter.getStatus())) {
          continue;
        }
        long printId = dbPrinter.getId();
        if (!ipList.contains(printId)) {
          ipList.add(printId);
        }
        //新建rel
        PdConfigRel pdConfigRel = new PdConfigRel();
        //时间
        pdConfigRel.setOperateTime(refundTime);
        //类型
        pdConfigRel.setType(PdConfigRel.TYPE_REFUND);
        //是否打印
        pdConfigRel.setIsPrinted(false);
        //Details
        pdConfigRel.setDbPrintDetails(printDetails);
        //配菜方案
        pdConfigRel.setDbConfig(dbProductConfigPlan);
        //订单
        pdConfigRel.setDbOrder(dbOrder);
        //储存rel
        DaoServiceUtil.getPdConfigRelService().saveOrUpdate(pdConfigRel);

        //map里没有该配菜方案
        if (collectArray.get(dbProductConfigPlan.getId().intValue()) == null) {
          PrintDetailsCollect pdCollect = new PrintDetailsCollect();
          pdCollect.setIsPrint(false);
          pdCollect.setDbConfig(dbProductConfigPlan);
          pdCollect.setType(PrintDetailsCollect.TYPE_REFUND);
          pdCollect.setOperateTime(refundTime);
          pdCollect.setDbOrder(dbOrder);
          collectArray.put(dbProductConfigPlan.getId().intValue(), pdCollect);
          //储存collect
          DaoServiceUtil.getPdCollectService().saveOrUpdate(pdCollect);
          //rel关联collect
          pdConfigRel.setDbPdCollect(pdCollect);
        } else {
          PrintDetailsCollect collect = collectArray.get(dbProductConfigPlan.getId().intValue());
          pdConfigRel.setDbPdCollect(collect);
        }
        //储存rel
        DaoServiceUtil.getPdConfigRelService().saveOrUpdate(pdConfigRel);
      }
    }
  }
  //@formatter:on

  /**
   * 退菜 套餐
   */
  public static void makeNotMergePrintDetails(PxOrderDetails details, Date refundTime,
      SparseArray<PrintDetailsCollect> collectArray, Double num, Double multNum,
      List<Long> ipList) {
    //商品
    PxProductInfo prodInfo = details.getDbProduct();
    PxOrderInfo dbOrder = details.getDbOrder();
    PxFormatInfo dbFormatInfo = details.getDbFormatInfo();
    PxMethodInfo dbMethodInfo = details.getDbMethodInfo();
    PxOptReason dbReason = details.getDbReason();

    //后厨出单
    if (prodInfo.getIsPrint().equals(PxProductInfo.IS_PRINT)) {
      //生成对应的PrintDetails
      PrintDetails printDetails = new PrintDetails();
      printDetails.setStatus(details.getStatus());
      printDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
      printDetails.setNum(num.doubleValue());
      printDetails.setMultipleUnitNumber(multNum);
      printDetails.setRemarks(details.getRemarks());
      printDetails.setInCombo(details.getInCombo());
      printDetails.setDbOrder(details.getDbOrder());
      printDetails.setDbProduct(prodInfo);
      if (dbFormatInfo != null) {
        printDetails.setFormatName(dbFormatInfo.getName());
      }
      if (dbMethodInfo != null) {
        printDetails.setMethodName(dbMethodInfo.getName());
      }
      if (dbReason != null) {
        printDetails.setReasonName(dbReason.getName());
      }
      DaoServiceUtil.getPrintDetailsService().saveOrUpdate(printDetails);

      //建立详情和配菜方案的rel
      List<PxProductConfigPlanRel> prodConfigRelList =
          DaoServiceUtil.getProductConfigPlanRelService()
              .queryBuilder()
              .where(PxProductConfigPlanRelDao.Properties.PxProductInfoId.eq(prodInfo.getId()))
              .where(PxProductConfigPlanRelDao.Properties.DelFlag.eq(0))
              .list();
      for (PxProductConfigPlanRel rel : prodConfigRelList) {
        //Rel无效
        if (rel == null || (!rel.getDelFlag().equals("0"))) continue;
        //获取配菜方案
        PxProductConfigPlan dbProductConfigPlan = rel.getDbProductConfigPlan();
        //配菜方案无效
        if (dbProductConfigPlan == null || (!dbProductConfigPlan.getDelFlag().equals("0"))) {
          continue;
        }
        PxPrinterInfo dbPrinter = dbProductConfigPlan.getDbPrinter();
        // 打印机无效 没启用
        if (dbPrinter == null || !("0".equals(dbPrinter.getDelFlag()))
            || PxPrinterInfo.DIS_ENABLE.equals(dbPrinter.getStatus())) {
          continue;
        }
        long printId = dbPrinter.getId();
        if (!ipList.contains(printId)) {
          ipList.add(printId);
        }
        //新建rel
        PdConfigRel pdConfigRel = new PdConfigRel();
        //时间
        pdConfigRel.setOperateTime(refundTime);
        //类型
        pdConfigRel.setType(PdConfigRel.TYPE_REFUND);
        //是否打印
        pdConfigRel.setIsPrinted(false);
        //Details
        pdConfigRel.setDbPrintDetails(printDetails);
        //配菜方案
        pdConfigRel.setDbConfig(dbProductConfigPlan);
        //订单
        pdConfigRel.setDbOrder(dbOrder);
        //储存rel
        DaoServiceUtil.getPdConfigRelService().saveOrUpdate(pdConfigRel);

        //map里没有该配菜方案
        if (collectArray.get(dbProductConfigPlan.getId().intValue()) == null) {
          //if (!configPlanCollectMap.containsKey(dbProductConfigPlan.getObjectId())) {
          PrintDetailsCollect pdCollect = new PrintDetailsCollect();
          pdCollect.setIsPrint(false);
          pdCollect.setDbConfig(dbProductConfigPlan);
          pdCollect.setType(PrintDetailsCollect.TYPE_REFUND);
          pdCollect.setOperateTime(refundTime);
          pdCollect.setDbOrder(dbOrder);
          collectArray.put(dbProductConfigPlan.getId().intValue(), pdCollect);
          //储存collect
          DaoServiceUtil.getPdCollectService().saveOrUpdate(pdCollect);
          //rel关联collect
          pdConfigRel.setDbPdCollect(pdCollect);
        } else {
          PrintDetailsCollect collect = collectArray.get(dbProductConfigPlan.getId().intValue());
          //PrintDetailsCollect collect= configPlanCollectMap.get(dbProductConfigPlan.getObjectId());
          pdConfigRel.setDbPdCollect(collect);
        }
        //储存rel
        DaoServiceUtil.getPdConfigRelService().saveOrUpdate(pdConfigRel);
      }
    }
  }

  //@formatter:off
  /**
   * 生成打印详情  带标签打印
   */
  public static void makePrintDetailsAndLabel(PxOrderDetails details, Date orderTime, SparseArray<PrintDetailsCollect> collectArray, Double num, Double multNum, List<Long> ipList, List<PxOrderDetails> labelDetailsList) {
    //商品
    PxProductInfo dbProduct = details.getPrintProd();
    PxOrderInfo dbOrder = details.getPrintOrder();
    PackagePrintDetails packagePrintDetails = details.getPackagePrintDetails();
    //标签打印
    if (!PxProductInfo.PRINT_LABEL_FALSE.equals(dbProduct.getIsLabel())) {
      labelDetailsList.add(details);
    }
    //后厨出单
    if (dbProduct.getIsPrint().equals(PxProductInfo.IS_PRINT)) {
      //生成对应的PrintDetails
      PrintDetails printDetails = new PrintDetails();
      printDetails.setStatus(details.getStatus());
      printDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_ORDER);
      printDetails.setNum(num);
      printDetails.setMultipleUnitNumber(multNum);
      printDetails.setRemarks(details.getRemarks());
      printDetails.setInCombo(details.getInCombo());
      printDetails.setDbOrder(dbOrder);
      printDetails.setDbProduct(dbProduct);

      printDetails.setFormatName(packagePrintDetails.getFormatName());
      printDetails.setMethodName(packagePrintDetails.getMethodName());
      printDetails.setReasonName(packagePrintDetails.getReasonName());

      DaoServiceUtil.getPrintDetailsService().saveOrUpdate(printDetails);
      //建立详情和配菜方案的rel
      List<PxProductConfigPlanRel> prodConfigRelList = DaoServiceUtil.getProductConfigPlanRelService()
          .queryBuilder()
          .where(PxProductConfigPlanRelDao.Properties.PxProductInfoId.eq(dbProduct.getId()))
          .where(PxProductConfigPlanRelDao.Properties.DelFlag.eq(0))
          .list();
      for (PxProductConfigPlanRel rel : prodConfigRelList) {
        //获取配菜方案
        PxProductConfigPlan dbProductConfigPlan = rel.getDbProductConfigPlan();
        //配菜方案无效
        if (dbProductConfigPlan == null || (!dbProductConfigPlan.getDelFlag().equals("0"))) {
          continue;
        }
        PxPrinterInfo dbPrinter = dbProductConfigPlan.getDbPrinter();
        // 打印机无效 没启用
        if (dbPrinter == null || !("0".equals(dbPrinter.getDelFlag())) || PxPrinterInfo.DIS_ENABLE.equals(dbPrinter.getStatus())) {
          continue;
        }
        long printId = dbPrinter.getId();
        if (!ipList.contains(printId)) {
          ipList.add(printId);
        }
        //新建rel
        PdConfigRel pdConfigRel = new PdConfigRel();
        //时间
        pdConfigRel.setOperateTime(orderTime);
        //类型
        pdConfigRel.setType(PdConfigRel.TYPE_ORDER);
        //是否打印
        pdConfigRel.setIsPrinted(false);
        //Details
        pdConfigRel.setDbPrintDetails(printDetails);
        //配菜方案
        pdConfigRel.setDbConfig(dbProductConfigPlan);
        //订单
        pdConfigRel.setDbOrder(mOrderInfo);
        //储存rel
        DaoServiceUtil.getPdConfigRelService().saveOrUpdate(pdConfigRel);

        //map里没有该配菜方案
        if (collectArray.get(dbProductConfigPlan.getId().intValue()) == null) {
          PrintDetailsCollect pdCollect = new PrintDetailsCollect();
          pdCollect.setIsPrint(false);
          pdCollect.setDbConfig(dbProductConfigPlan);
          pdCollect.setType(PrintDetailsCollect.TYPE_ORDER);
          pdCollect.setOperateTime(orderTime);
          pdCollect.setDbOrder(dbOrder);
          collectArray.put(dbProductConfigPlan.getId().intValue(), pdCollect);
          //储存collect
          DaoServiceUtil.getPdCollectService().saveOrUpdate(pdCollect);
          //rel关联collect
          pdConfigRel.setDbPdCollect(pdCollect);
        } else {
          PrintDetailsCollect collect = collectArray.get(dbProductConfigPlan.getId().intValue());
          pdConfigRel.setDbPdCollect(collect);
        }
        //储存rel
        DaoServiceUtil.getPdConfigRelService().saveOrUpdate(pdConfigRel);
      }
    }
  }

  //@formatter:on

  /**
   * SmackService用 生成打印详情  不带标签打印
   */
  public static void makePrintDetails(PxOrderDetails details, Date orderTime,
      SparseArray<PrintDetailsCollect> collectArray, Double num, Double multNum,
      PxOrderInfo orderInfo, boolean isRefund, List<Long> ipList) {
    //商品
    PxProductInfo dbProduct = details.getDbProduct();
    PxFormatInfo dbFormatInfo = details.getDbFormatInfo();
    PxMethodInfo dbMethodInfo = details.getDbMethodInfo();
    PxOptReason dbReason = details.getDbReason();

    //后厨出单
    if (dbProduct.getIsPrint().equals(PxProductInfo.IS_PRINT)) {
      //生成对应的PrintDetails
      PrintDetails printDetails = new PrintDetails();
      printDetails.setStatus(details.getStatus());
      if (isRefund) {
        printDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
      } else {
        printDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_ORDER);
      }
      printDetails.setNum(num);
      printDetails.setMultipleUnitNumber(multNum);
      printDetails.setRemarks(details.getRemarks());
      printDetails.setInCombo(details.getInCombo());
      printDetails.setDbOrder(orderInfo);
      printDetails.setDbProduct(dbProduct);

      if (dbFormatInfo != null) {
        printDetails.setFormatName(dbFormatInfo.getName());
      }
      if (dbMethodInfo != null) {
        printDetails.setMethodName(dbMethodInfo.getName());
      }
      if (dbReason != null) {
        printDetails.setReasonName(dbReason.getName());
      }

      DaoServiceUtil.getPrintDetailsService().saveOrUpdate(printDetails);

      //建立详情和配菜方案的rel
      List<PxProductConfigPlanRel> prodConfigRelList =
          DaoServiceUtil.getProductConfigPlanRelService()
              .queryBuilder()
              .where(PxProductConfigPlanRelDao.Properties.PxProductInfoId.eq(dbProduct.getId()))
              .where(PxProductConfigPlanRelDao.Properties.DelFlag.eq(0))
              .list();
      for (PxProductConfigPlanRel rel : prodConfigRelList) {
        //Rel无效
        //if (rel == null || (!rel.getDelFlag().equals("0"))) continue;
        //获取配菜方案
        PxProductConfigPlan dbProductConfigPlan = rel.getDbProductConfigPlan();
        //配菜方案无效
        if (dbProductConfigPlan == null || (!dbProductConfigPlan.getDelFlag().equals("0"))) {
          continue;
        }
        PxPrinterInfo dbPrinter = dbProductConfigPlan.getDbPrinter();
        // 打印机无效 没启用
        if (dbPrinter == null || !("0".equals(dbPrinter.getDelFlag()))
            || PxPrinterInfo.DIS_ENABLE.equals(dbPrinter.getStatus())) {
          continue;
        }
        long printId = dbPrinter.getId();
        if (!ipList.contains(printId)) {
          ipList.add(printId);
        }
        //新建rel
        PdConfigRel pdConfigRel = new PdConfigRel();
        //时间
        pdConfigRel.setOperateTime(orderTime);
        //类型
        if (isRefund) {
          pdConfigRel.setType(PdConfigRel.TYPE_REFUND);
        } else {
          pdConfigRel.setType(PdConfigRel.TYPE_ORDER);
        }
        //是否打印
        pdConfigRel.setIsPrinted(false);
        //Details
        pdConfigRel.setDbPrintDetails(printDetails);
        //配菜方案
        pdConfigRel.setDbConfig(dbProductConfigPlan);
        //订单
        pdConfigRel.setDbOrder(orderInfo);
        //储存rel
        DaoServiceUtil.getPdConfigRelService().saveOrUpdate(pdConfigRel);

        //map里没有该配菜方案
        if (collectArray.get(dbProductConfigPlan.getId().intValue()) == null) {
          PrintDetailsCollect pdCollect = new PrintDetailsCollect();
          pdCollect.setIsPrint(false);
          pdCollect.setDbConfig(dbProductConfigPlan);
          if (isRefund) {
            pdCollect.setType(PrintDetailsCollect.TYPE_REFUND);
          } else {
            pdCollect.setType(PrintDetailsCollect.TYPE_ORDER);
          }
          pdCollect.setOperateTime(orderTime);
          pdCollect.setDbOrder(orderInfo);
          collectArray.put(dbProductConfigPlan.getId().intValue(), pdCollect);
          //储存collect
          DaoServiceUtil.getPdCollectService().saveOrUpdate(pdCollect);
          //rel关联collect
          pdConfigRel.setDbPdCollect(pdCollect);
        } else {
          PrintDetailsCollect collect = collectArray.get(dbProductConfigPlan.getId().intValue());
          pdConfigRel.setDbPdCollect(collect);
        }
        //储存rel
        DaoServiceUtil.getPdConfigRelService().saveOrUpdate(pdConfigRel);
      }
    }
  }
}