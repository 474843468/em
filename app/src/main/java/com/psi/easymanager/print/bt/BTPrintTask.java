package com.psi.easymanager.print.bt;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CollectionContentEvent;
import com.psi.easymanager.module.AppBillCount;
import com.psi.easymanager.module.AppCashCollect;
import com.psi.easymanager.module.AppSaleContent;
import com.psi.easymanager.module.AppShiftCateInfo;
import com.psi.easymanager.module.PdConfigRel;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxRechargeRecord;
import com.psi.easymanager.module.PxSetInfo;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.print.MergePrintDetails;
import com.psi.easymanager.print.constant.EpsonPosPrinterCommand;
import com.psi.easymanager.print.constant.PrinterConstant;
import com.psi.easymanager.print.module.AppCustomAmount;
import com.psi.easymanager.print.module.AppFinanceAmount;
import com.psi.easymanager.print.module.ShiftWork;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.UserUtils;
import de.greenrobot.dao.query.QueryBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_KITCHEN_DETAILS;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_BILL_DETAIL_CUSTOMER;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_BILL_DETAIL_FINANCE;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_BILL_SUMMARY;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_CATEGORY_COLLECT;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_COLLECT;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_COLLECT_REFUND;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_CUSTOMERS_AL;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_DETAILS_COLLECTION;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_FALSE_DATA;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_FINANCE_INFO;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_SALE_COUNT;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_SHIFT_WORK;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_SHIFT_WORK_ALL_BILLS;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_TABLE;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_VIP_CONSUME_RECORD;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_VIP_RECHARGE_RECORD;
import static com.psi.easymanager.print.constant.BTPrintConstants.byteCommands;
import static com.psi.easymanager.print.constant.BTPrintConstants.office;
import static com.psi.easymanager.print.constant.BTPrintConstants.sdf;

/**
 * User: ylw
 * Date: 2017-01-06
 * Time: 13:56
 * FIXME
 */
public class BTPrintTask {


  private List<PrintDetailsCollect> printDetailsCollectList;//下单生成的collect
  private ShiftWork shiftWork;//交接班
  private boolean isRefundBill;//是否撤单
  private AppFinanceAmount financeAmount;//App财务联打印总价,应收等等
  private AppCustomAmount customAmount;
  private List<AppSaleContent> saleContentList;//销售统计
  private PxProductCategory category;//商品分类
  private AppBillCount appBillCount;//账单汇总
  private CollectionContentEvent contentevent;
  private PxOrderInfo orderInfo; //订单
  private PxTableAlteration mAlteration;
  private List<PxOrderDetails> orderDetailsList;//商品详情 汇总
  private boolean isOncePrint;//一菜一切
  private int printNum;//打印份数
  private List<PxOrderInfo> orderInfoList;//订单
  private String planName;// 配菜方案名称
  private PxRechargeRecord vipRechargeRecord;//会员充值记录
  private PxVipInfo mVipInfo;//会员
  private final int mMode;//打印模式
  private double mConsume;//会员本次消费
  private boolean mIs58 = false;//58mm 80mm
  private String mAddress;//打印机address

  public void setIs58(boolean is58) {
    mIs58 = is58;
  }

  private BTPrintTask(Builder builder) {
    printDetailsCollectList = builder.printDetailsCollectList;
    shiftWork = builder.shiftWork;
    isRefundBill = builder.isRefundBill;
    financeAmount = builder.financeAmount;
    customAmount = builder.customAmount;
    saleContentList = builder.saleContentList;
    category = builder.category;
    appBillCount = builder.appBillCount;
    contentevent = builder.contentevent;
    orderInfo = builder.orderInfo;
    orderDetailsList = builder.collectionList;
    isOncePrint = builder.isOncePrint;
    printNum = builder.printNum;
    orderInfoList = builder.orderInfoList;
    planName = builder.planName;
    vipRechargeRecord = builder.vipRechargeRecord;
    mVipInfo = builder.mVipInfo;
    mMode = builder.mMode;
    mConsume = builder.mConsume;
    mAlteration = builder.mAlteration;
    mAddress = builder.mAddress;
  }

  public int getMode() {
    return mMode;
  }

  //@formatter:on
  public void run(OutputStream outputStream) throws IOException {
    if (mMode == PRINT_MODE_COLLECT) {//打印 Collect 、 DetailsAndConfigRel
      printCollectAndRel(printDetailsCollectList, mIs58, mAddress, outputStream);
    } else if (mMode == PRINT_MODE_COLLECT_REFUND) {//打印 退菜 撤单的

    } else if (mMode == PRINT_KITCHEN_DETAILS) {//KitchenPrintActivity 打印
    } else if (mMode == PRINT_MODE_TABLE) { //移并桌
      printTableAlert(mAlteration, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_DETAILS_COLLECTION) {//打印点菜单
      printDetailsCollection(orderInfo, orderDetailsList, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_BILL_SUMMARY) {//账单汇总
      printBillSummary(contentevent, appBillCount, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_SALE_COUNT) { //销售统计
      printSaleCount(category, saleContentList, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_CUSTOMERS_AL) {//结账单 客户联
      printCustomersAl(orderInfo, orderDetailsList, customAmount, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_FINANCE_INFO) {//结账单 财务联
      printFinaceInfo(orderInfo, orderDetailsList, financeAmount, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_BILL_DETAIL_CUSTOMER) {//账单明细 客户联
      printBillDetailCustomer(orderDetailsList, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_BILL_DETAIL_FINANCE) {//账单明细 财务联
      printBillDetailFinance(orderDetailsList, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_SHIFT_WORK) {//交接班
      printShiftWork(shiftWork, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_SHIFT_WORK_ALL_BILLS) {//交接班所有订单
      //printResult = printShiftWorkAllBills(shiftWork, orderInfoList, isFormat58, outputStream, printWriter);
    } else if (mMode == PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS) {//日结订单
      printDailyStatement(shiftWork, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_CATEGORY_COLLECT) {//打印分类汇总信息
      printCategoryCollect(shiftWork, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_VIP_CONSUME_RECORD) { //打印会员消费记录
      printVipConsumeRecord(mVipInfo, mConsume, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_VIP_RECHARGE_RECORD) { //打印会员充值记录
      printVipRechargeRecord(vipRechargeRecord, mVipInfo, mIs58, outputStream);
    } else if (mMode == PRINT_MODE_FALSE_DATA) {// 啥都不做

    }
    // else if (mMode == PRINT_MODE_COLLECT_REFUND) {//打印 退菜 撤单的
    //  printCollectAndRelRefund(printDetailsCollectList, mPrinterInfo, isFormat58, outputStream,
    //      printWriter, isRefundBill);
    //}
  }

  //移并桌
  private void printTableAlert(PxTableAlteration tableAlteration, boolean is58, OutputStream os)
      throws IOException {
    PxOrderInfo orderInfo = tableAlteration.getDbOrder();
    //移并桌
    boolean typeMerge = tableAlteration.getType().equals(PxTableAlteration.TYPE_MERGE);
    printBoldText(os, typeMerge ? PrinterConstant.TABLE_MERGE : PrinterConstant.TABLE_MOVE);
    dividerLine(os, is58);
    //单号
    printText(os, "单  号:" + orderInfo.getOrderNo().substring(10, 30));
    //移并桌信息
    String msg = tableAlteration.getDbOriginalTable().getName() + (typeMerge ? "并" : "移") + "桌到"
        + tableAlteration.getDbTargetTable().getName();
    printText(os, msg);
    dividerLine(os, is58);
    printTime(os);
  }

  //@formatter:off
  //后厨打印
  private void printCollectAndRel(List<PrintDetailsCollect> collectList, boolean is58,String address, OutputStream os) throws IOException {
    //for (PrintDetailsCollect collect : collectList) {
    //  List<PdConfigRel> relList = collect.getDbPdConfigRelList();
    //  if (relList.isEmpty()) continue;
    //  PxProductConfigPlan dbConfig = collect.getDbConfig();
    //  //不在该打印机下
    //  BTPrinterInfo dbBTPrinter = dbConfig.getDbBTPrinter();
    //  if (dbBTPrinter == null || !dbBTPrinter.getAddress().equals(address)) continue;
    //
    //  boolean isOnce = PxProductConfigPlan.ONCE_PRINT.equals(dbConfig.getFlag());//一菜一切
    //  int count = dbConfig.getCount();//count
    //  String name = dbConfig.getName();//name
    //  for (int i = 0; i < count; i++) {
    //    if (isOnce) { //是一菜一切
    //      List<PdConfigRel> pdRelList = new ArrayList<>();
    //      for (PdConfigRel rel : relList) {
    //        pdRelList.clear();
    //        pdRelList.add(rel);
    //        printCollect(name, is58, pdRelList, os);
    //        savePrintDetailsAndConfigRel(rel);
    //      }
    //    } else {//fou
    //      printCollect(name, is58, relList, os);
    //      savePrintCollect(collect);
    //    }
    //  }
    //}
    //sound
  }

  /**
   * print collect
  // */
  //private void printCollect(String name, boolean is58, List<PdConfigRel> relList, OutputStream os)
  //    throws IOException {
  //  //配菜方案名称
  //  printCenterBoldText(os, (name == null) ? "厨房打印" : name);
  //  PxOrderInfo orderInfo = relList.get(0).getDbPrintDetails().getDbOrder();
  //  //是零售单
  //  boolean retail = PxOrderInfo.ORDER_INFO_TYPE_RETAIL.equals(orderInfo.getOrderInfoType());
  //  //单号桌号
  //  String billAndTable = "单  号:" + Integer.valueOf(orderInfo.getOrderNo().substring(24, 30));
  //  if (!retail) {//不零售单
  //    TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
  //        .queryBuilder()
  //        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
  //        .unique();
  //    PxTableInfo dbTable = unique.getDbTable();
  //    billAndTable = billAndTable + "  桌  号:" + dbTable.getName();
  //  }
  //  //放大单号
  //  printBoldText(os, billAndTable);
  //  if (!retail) {//不零售单
  //    printText(os, "人  数:" + orderInfo.getActualPeopleNumber());
  //  }
  //  //张单号
  //  printText(os, "账单号:" + orderInfo.getOrderNo().substring(10, 30));
  //  //
  //  dividerLine(os, is58);
  //  for (PdConfigRel rel : relList) {
  //    PrintDetails mPrintDetails = rel.getDbPrintDetails();
  //    //商品
  //    PxProductInfo dbProduct = mPrintDetails.getDbProduct();
  //    //规格rel
  //    PxFormatInfo dbFormat = mPrintDetails.getDbFormatInfo();
  //    //做法ref
  //    PxMethodInfo dbMethod = mPrintDetails.getDbMethodInfo();
  //    //有规格或做法就打印
  //    String formatName = "";
  //    if (dbFormat != null && dbFormat.getName() != null) {
  //      formatName = "(" + dbFormat.getName() + ")";
  //    }
  //    String methodName = "";
  //    if (dbMethod != null && dbMethod.getName() != null) {
  //      methodName = "(" + dbMethod.getName() + ")";
  //    }
  //    //判断是否是延迟
  //    String delay = "";
  //    if (mPrintDetails.getStatus().equals(PxOrderDetails.STATUS_DELAY)) {
  //      delay = "(待)";
  //    }
  //    //是否是套餐商品
  //    String isComboDetails = "";
  //    String before = dbProduct.getName() + isComboDetails + formatName + methodName + delay;
  //
  //    if (dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
  //      String unit = NumberFormatUtils.formatFloatNumber(mPrintDetails.getMultipleUnitNumber())
  //          + dbProduct.getUnit() + "/" + mPrintDetails.getNum().intValue()
  //          + dbProduct.getOrderUnit();
  //      //printBoldText(os, replaceKitchen(before, (is58) ? 8 : 15) + unit);
  //      printBoldText(os, before + getSpace(before,is58 ? 8 :15)+ unit);
  //    } else { //非双单位(品名)
  //      printBoldText(os, replaceKitchen(before, (is58) ? 12 : 18) + mPrintDetails.getNum().intValue()
  //          + dbProduct.getUnit());
  //    }
  //    //单个备注
  //    String remarks = mPrintDetails.getRemarks();
  //    if (!"无".equals(remarks)) {
  //      printBoldText(os, "--备注:" + remarks);
  //    }
  //  }
  //  //整单备注
  //  String orderInfoRemarks = orderInfo.getRemarks();
  //  if (!"无".equals(orderInfoRemarks)) {
  //    //分割线
  //    dividerLine(os, is58);
  //    printBoldText(os, "整单备注:" + orderInfoRemarks);
  //  }
  //  //
  //  dividerLine(os, is58);
  //  printTime(os);
  //}

  /**
   * save PdConfigRel
   */
  private void savePrintDetailsAndConfigRel(PdConfigRel rel) {
    //PdConfigRel exist = DaoServiceUtil.getPdConfigRelService()
    //    .queryBuilder()
    //    .where(PdConfigRelDao.Properties.Id.eq(rel.getId()))
    //    .unique();
    //if (exist == null) return;
    //if (!exist.getIsBTPrinted()) {
    //  exist.setIsBTPrinted(true);
    //  DaoServiceUtil.getPdConfigRelService().update(exist);
    //}
  }

  /**
   * save PrintDetailsCollect
   */
  private void savePrintCollect(PrintDetailsCollect collect) {
    //PrintDetailsCollect exist = DaoServiceUtil.getPdCollectService()
    //    .queryBuilder()
    //    .where(PrintDetailsCollectDao.Properties.Id.eq(collect.getId()))
    //    .unique();
    //if (exist == null) return;
    //Boolean isPrint = exist.getIsBTPrint();
    //if (!isPrint) {
    //  SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    //  db.beginTransaction();
    //  try {
    //    exist.setIsBTPrint(true);
    //    DaoServiceUtil.getPdCollectService().update(exist);
    //    db.setTransactionSuccessful();
    //  } catch (Exception e) {
    //    e.printStackTrace();
    //  } finally {
    //    db.endTransaction();
    //  }
    //}
  }

  //结束时间字样
  private void printTime(OutputStream os) throws IOException {
    printText(os, "打印时间:" + sdf.format(new Date()));
    printText(os, " ");
    printText(os, " ");
    printText(os, " ");
    printText(os, " ");
    //切纸
    os.write(byteCommands[18]);
  }

  /**
   * 收银 打印点菜单
   */
  //@formatter:off
  private void printDetailsCollection(PxOrderInfo orderInfo, List<PxOrderDetails> detailsList,boolean is58, OutputStream outputStream) throws IOException{
    if (detailsList == null || detailsList.isEmpty()) return ;
      //打印通用头部
      initHead(outputStream);
      //点菜单字样
      printCenterBoldText(outputStream,PrinterConstant.POINT_MENU);
      //分割线
      dividerLine(outputStream,is58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo, outputStream);
      //账单号
      getBillId(orderInfo,outputStream);
      //服务员和单位
      getWaiterAndUnit(orderInfo,outputStream);
      //分割线
      dividerLine(outputStream, is58);
      //品名(金额)和数量/单位
      getProductProperty(is58,outputStream);
      printText(outputStream," ");
      //合并数据
      List<PxOrderDetails> mergeDetails = MergePrintDetails.mergeDbDetailsList(orderInfo);
      //商品真实实体数据
      //getProductData(mergeDetails,is58,outputStream,true);
      mergeProductData(orderInfo,mergeDetails,is58,outputStream,true);
      //分割线
      dividerLine(outputStream, is58);
      //总价
      getTotalPrice(outputStream, detailsList);
      initFoot(outputStream,is58);
  }

  /**
   * 收银 打印账单汇总
   */
  private void printBillSummary(CollectionContentEvent contentEvent, AppBillCount appBillCount,boolean is58, OutputStream outputStream) throws IOException{
      //打印通用头部
      initHead(outputStream);
      //收银员账单汇总
      printCenterBoldText(outputStream,PrinterConstant.CASHIER_COLLECT);
      //分割线
      dividerLine(outputStream, is58);
      String time = PrinterConstant.DISH_STATISTICS_TIME + sdf.format(new Date());
      printText(outputStream,time);
      //分割线
      dividerLine(outputStream, is58);
      //收银员
      printText(outputStream,PrinterConstant.CASHIER + contentEvent.getUser().getName());
      //分割线
      dividerLine(outputStream, is58);
      //应收
      printText(outputStream,"应收:" + appBillCount.getTotalReceivable());
      //找零
      printText(outputStream,"找零:" + appBillCount.getTotalChange());
      //抹零
      printText(outputStream,"抹零:" + appBillCount.getTotalTail());
      //支付类优惠
      printText(outputStream,"支付类优惠:" + appBillCount.getPayPrivilege());
      //各项实收
      printText(outputStream,"--------实收详情--------");
      List<Pair<String, String>> everyReceived = appBillCount.getEveryReceived();
      for (Pair<String, String> pair : everyReceived) {
        printText(outputStream,pair.first + ":" + pair.second);
      }
      //foot
      initFoot(outputStream,is58);
  }

  /**
   * 收银 打印销售统计
   */
  private void printSaleCount(PxProductCategory mCategory, List<AppSaleContent> saleContentList, boolean is58,OutputStream outputStream) throws IOException{
      //打印通用头部
      initHead(outputStream);
      //菜类点菜统计
      printCenterBoldText(outputStream,PrinterConstant.SALE_ACCOUNT_MENU);
      //分割线
      dividerLine(outputStream, is58);
      //统计时间
      String time = PrinterConstant.DISH_STATISTICS_TIME + sdf.format(new Date());
      printText(outputStream,time);
      //分割线
      dividerLine(outputStream, is58);
      //分类名
      printText(outputStream,PrinterConstant.DISH_CATEGORY + mCategory.getName());
      //分割线
      dividerLine(outputStream, is58);
      //品名(金额)和数量/单位
      //mPrintWriter.println(replaceKitchen("品名",(isFormat58 ? 11 : 16))+"数量");getSpace(before,(isFormat58 ? 26 : 38))
      printText(outputStream,"品名" + getSpace("品名",is58 ? 25 : 38) +"数量");
      //销量实体
      for (AppSaleContent appSaleContent : saleContentList) {
        //mPrintWriter.println(replaceKitchen(appSaleContent.getProdName()+" ",(isFormat58 ? 11 : 16)) +appSaleContent.getSaleNumber());
        String prodName = appSaleContent.getProdName();
        String text = null;
        if (appSaleContent.isMultUnitProd()){
          text =prodName + getSpace(prodName ,is58 ? 26 : 39) + appSaleContent.getSaleMultNumber()+appSaleContent.getUnit();
        }else{
          text = prodName + getSpace(prodName ,is58 ? 26 : 39) + appSaleContent.getSaleNumber();
        }
        printText(outputStream,text);
      }
      //foot
      initFoot(outputStream,is58);
  }

  /**
   * 收银 打印结账单客户联
   */
  private void printCustomersAl(PxOrderInfo orderInfo, List<PxOrderDetails> detailsList, AppCustomAmount customAmount,boolean is58, OutputStream outputStream) throws IOException{
      initHead( outputStream);
      //结账单（客户联）
      printCenterBoldText(outputStream,PrinterConstant.STATE_MENU_CUSTOM);
      //分割线
      dividerLine(outputStream, is58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo,outputStream);
      //账单号
      getBillId(orderInfo,outputStream);
      //服务员和单位
      getWaiterAndUnit(orderInfo,outputStream);
      //分割线
      dividerLine(outputStream, is58);
      //品名(金额)和数量/单位
      getProductProperty(is58,outputStream);
      printText(outputStream," ");
      //商品真实实体数据
    //getProductData(detailsList,is58,outputStream,false);
    List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
    mergeProductData(orderInfo,mergeDetailsList,is58,outputStream,false);
    //分割线
      dividerLine(outputStream, is58);
      //总金额,折后,优惠等等
      getBillWithCustomInfoAmount(customAmount,is58, outputStream,orderInfo);
      initFoot(outputStream,is58);
  }

  /**
   * 收银 结账单财务联
   */
  private void printFinaceInfo(PxOrderInfo orderInfo, List<PxOrderDetails> detailsList, AppFinanceAmount mFinanceAmount,boolean is58, OutputStream outputStream) throws IOException{
    if (detailsList == null || detailsList.isEmpty()) return ;
      //打印通用头部
      initHead( outputStream);
      //结账单（财务联）
      printCenterBoldText(outputStream,PrinterConstant.STATE_MENU_FINANCE);
      //分割线
      dividerLine(outputStream, is58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo,outputStream);
      //账单号
      getBillId(orderInfo,outputStream);
      //服务员和单位
      getWaiterAndUnit(orderInfo, outputStream);
      //分割线
      dividerLine(outputStream, is58);
      //品名(金额)和数量/单位
      //getProductProperty(mPrintWriter,isFormat58);
      getProductProperty(is58,outputStream);
      printText(outputStream," ");
      //商品真实实体数据
      //getProductData(detailsList,is58,outputStream,false);
    List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
    mergeProductData(orderInfo,mergeDetailsList,is58,outputStream,false);
      //--分类统计
      categoryCollect(orderInfo,outputStream,is58);
      //应收,实收,找零
      getBillWithFinanceInfoAmount(orderInfo, mFinanceAmount,is58, outputStream);
      initFoot(outputStream,is58);
      //sound(mOutputStream);
  }


  /**
   * 收银  账单明细 客户联
   */
  private void printBillDetailCustomer(List<PxOrderDetails> detailsList, boolean is58, OutputStream outputStream) throws IOException{
    if (detailsList == null || detailsList.size() == 0) return ;
    PxOrderInfo orderInfo = detailsList.get(0).getDbOrder();
      //打印通用头部
      initHead(outputStream);
      //账单明细（客户联）
      printCenterBoldText(outputStream,PrinterConstant.ACCOUNT_DETAIL_WITH_CUSTOMER);
      //分割线
      dividerLine(outputStream, is58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo,outputStream);
      //账单号
      getBillId(orderInfo, outputStream);
      //人数
      getPeopleNum(orderInfo, outputStream);
      //收银员和单位
      getWaiterAndUnit(orderInfo, outputStream);
      //分割线
      dividerLine(outputStream, is58);
      //品名(金额)和数量/单位
      getProductProperty(is58,outputStream);
      printText(outputStream," ");
      //商品真实实体数据 (账单明细客户联）
      //getProductData(detailsList,is58,outputStream,false);
    List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
    mergeProductData(orderInfo,mergeDetailsList,is58,outputStream,false);
      //分割线
      dividerLine(outputStream, is58);
      //合计项
      getSumNumber(detailsList, outputStream);
      //分割线
      dividerLine(outputStream, is58);
      //仅供账单详情应收,实收,找零,抹零
      getRecMoney(orderInfo,is58,outputStream);
      //init foot
      initFoot(outputStream,is58);
  }

  /**
   * 收银 账单明细 财务联
   */
  private void printBillDetailFinance(List<PxOrderDetails> detailsList, boolean is58, OutputStream outputStream) throws IOException{
    if (detailsList == null || detailsList.size()== 0) return ;
    PxOrderInfo orderInfo = detailsList.get(0).getDbOrder();
    //refresh
    DaoServiceUtil.getOrderInfoService().refresh(orderInfo);
      //打印通用头部
      initHead(outputStream);
      //账单明细（客户联）
      printCenterBoldText(outputStream,PrinterConstant.ACCOUNT_DETAIL_WITH_FINANCING);
      //分割线
      dividerLine(outputStream, is58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo,outputStream);
      //账单号
      getBillId(orderInfo, outputStream);
      //人数
      getPeopleNum(orderInfo, outputStream);
      //收银员和单位
      getWaiterAndUnit(orderInfo, outputStream);
      //分割线
      dividerLine(outputStream, is58);
      //品名(金额)和数量/单位
      getProductProperty(is58,outputStream);
      printText(outputStream," ");
      //商品真实实体数据 (账单明细客户联）
      //getProductData(detailsList,is58,outputStream,false);
    List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
    mergeProductData(orderInfo,mergeDetailsList,is58,outputStream,false);
      //--分类统计
      categoryCollect(orderInfo,outputStream,is58);
      //合计项
      getSumNumber(detailsList, outputStream);
      //分割线
      dividerLine(outputStream, is58);
      //仅供账单详情应收,实收,找零,抹零
      getRecMoney(orderInfo,is58,outputStream);
      //init foot
      initFoot(outputStream,is58);
  }

  /**
   * 打印收银员 交接班
   */
  private void printShiftWork(ShiftWork shiftWork, boolean is58, OutputStream outputStream) throws IOException{
      //收银员交接班
      printCenterBoldText(outputStream,PrinterConstant.SHIFT_WORK_ALL_ORDER);
      //交接班 通用头部
      shiftWorkHead(shiftWork, outputStream);
      // 复用
      shiftWorkBody(shiftWork, is58,outputStream, false);
  }


  /**
   * 日结单据
   */
  private void printDailyStatement(ShiftWork shiftWork, boolean is58, OutputStream outputStream) throws IOException{
      //收银员交接班 所有账单
      String title = shiftWork.getTitle();
      if (title != null) {
        printCenterBoldText(outputStream,title);
      } else {
        printCenterBoldText(outputStream,PrinterConstant.SHIFT_DAY_REPORT_COLLECT);
      }
      printText(outputStream," ");
      //店铺名称
      printText(outputStream,"店铺名称: " + office.getName());
      //营业日期
      printText(outputStream,"营业日期:" + shiftWork.getBusinessData());
      //收银员
      printText(outputStream,"收 银 员:" + shiftWork.getCashierName());
      //交接时间
      printText(outputStream,PrinterConstant.SHIFT_TIME + sdf.format(shiftWork.getShitTime()));
      //账单时间段
      Date startTime = shiftWork.getStartTime();
      Date endTime = shiftWork.getEndTime();
      if (startTime != null && endTime != null) {
        printText(outputStream,PrinterConstant.SHIFT_TIME_ZONE + sdf.format(startTime));
        printText(outputStream,"           ~" + sdf.format(endTime));
      }
      //区域
      printText(outputStream,PrinterConstant.WORK_ZONE + shiftWork.getWorkZone());
      //body
      shiftWorkBodyDaily(shiftWork, is58,outputStream);
  }


  /**
   * 打印分类汇总信息
   */
  //@formatter:off
  private void printCategoryCollect(ShiftWork shiftWork, boolean is58, OutputStream outputStream) throws IOException{
      //收银员交接班
      printCenterBoldText(outputStream,"收银员交接班—所有分类");
      //head
      shiftWorkHead(shiftWork, outputStream);
      //--分类统计
      if (is58) {
         printCenterText(outputStream,"----------- 分类统计 ----------");
      } else {
         printCenterText(outputStream,"------------------ 分类统计 -----------------");
      }
      printText(outputStream,"项目" + replace(is58 ? 2 : 4) + "数量" + replace(is58 ? 2 : 4) + "原  价" + replace(is58 ? 4 : 6) + "优惠后金额");
      printText(outputStream," ");
      for (AppShiftCateInfo collect : shiftWork.getCategoryCollectList()) {
        String receivable = NumberFormatUtils.formatFloatNumber(collect.getReceivableAmount());
        String actualNum = NumberFormatUtils.formatFloatNumber(collect.getActualAmount());
        String text = replaceKitchen(collect.getCateName(),(is58)? 4 : 6) +
            replaceKitchen(String.valueOf(collect.getCateNumber()),(is58) ? 6 : 8) +
            replaceKitchen(receivable , (is58) ? 11 : 13) + actualNum;
        printText(outputStream,text);
      }
      String receivableTotal = NumberFormatUtils.formatFloatNumber(shiftWork.getCategoryCollectReceivableMoneyTotal());
      String actualTotal = NumberFormatUtils.formatFloatNumber(shiftWork.getCategoryCollectRealMoneyTotal());
      String msg = replaceKitchen("总计",is58 ? 4 :6) + replaceKitchen(String.valueOf(shiftWork.getCategoryCollectTotal()), is58 ? 6 :8)
          + replaceKitchen(receivableTotal,is58 ? 11 :13) + actualTotal;
      printText(outputStream,msg);
      dividerLine(outputStream, is58);
      //交接班 尾
      shiftWorkFoot(is58, outputStream);
  }

  /**
   * 会员消费记录
   */
  private void printVipConsumeRecord(PxVipInfo vipInfo, double consume, boolean is58,
      OutputStream outputStream) throws IOException {
      //title
      printCenterBoldText(outputStream,"会员消费记录");
      //
      printText(outputStream,"会员名称:  " + vipInfo.getName());
      printText(outputStream,"会员电话:  " + vipInfo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
      printText(outputStream,"本次消费:  " + NumberFormatUtils.formatFloatNumber(consume));
      printText(outputStream,"余    额:  " + NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance() - consume));
      dividerLine(outputStream, is58);
      printText(outputStream,"消费时间:  " + sdf.format(new Date()));
      printText(outputStream,"消费门店:  " + office.getName());
      User user = UserUtils.getLoginUser();
      printText(outputStream,"收 银 员:  " + ((user == null ) ? "admin" : user.getName()));
      printText(outputStream," ");
      initFoot(outputStream,is58);
  }


  /**
   * 会员充值记录
   */
  private void printVipRechargeRecord(PxRechargeRecord record, PxVipInfo vipInfo, boolean is58,
      OutputStream outputStream) throws IOException{
      //title
      printCenterBoldText(outputStream,"会员充值记录");
      printText(outputStream,"会员名称:  " + vipInfo.getName());
      printText(outputStream,"会员电话:  " + vipInfo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
      printText(outputStream,"充值金额:  " + NumberFormatUtils.formatFloatNumber(record.getMoney()));
      Double giving = record.getGiving();
      printText(outputStream,"赠送金额:  " + ((giving == null) ? 0.0 : giving));
      printText(outputStream,"余    额:  " + NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance()));
      dividerLine(outputStream, is58);
      //
      Date recordDate = record.getRechargeTime();
      printText(outputStream,"充值时间:  " + sdf.format((recordDate == null ) ? new Date() : recordDate));
      printText(outputStream,"充值门店:  " + office.getName());
      User user = UserUtils.getLoginUser();
      printText(outputStream,"收 银 员:  " + ((user == null ) ? "admin" : user.getName()));
      initFoot(outputStream,is58);
  }

  /**
   * 日结订单
   */
  //@formatter:off
  private void shiftWorkBodyDaily(ShiftWork shiftWork,boolean is58, OutputStream outputStream) throws IOException {
    //--- 收银汇总
    if (is58) {
       printCenterText(outputStream,"----------- 收银汇总 ----------");
    } else {
       printCenterText(outputStream,"----------------- 收银汇总 ----------------");
    }
    // 收银员总计  银行卡 翼支付 会员卡 支付宝 现金
    printText(outputStream,"项目" + getSpace("项目",is58 ? 18 : 22) + "笔数" + getSpace("笔数",is58 ? 6 : 8) + "金额");
    List<AppCashCollect> cashCollectList = shiftWork.getCashCollectList();
      for (AppCashCollect collect : cashCollectList) {
        String name = collect.getName();
        int num = collect.getNum();
        String money = NumberFormatUtils.formatFloatNumber(collect.getMoney());
        String numString = String.valueOf(num);
        printText(outputStream,name + getSpace(name,is58 ? 19 : 23) + numString + getSpace(numString,is58 ? 6 : 8) + money);
      }
    //--1.消费统计
    if (is58) {
       printCenterText(outputStream,"----------- 消费统计 ----------");
    } else {
       printCenterText(outputStream,"----------------- 消费统计 ----------------");
    }
    //单数
    printText(outputStream,"单数: " + shiftWork.getBillsCount());
    //人数
    printText(outputStream,"人数: " + shiftWork.getPeopleNum() + replace(9));
    printText(outputStream," ");
    //总价
    printText(outputStream,"总价:"+NumberFormatUtils.formatFloatNumber(shiftWork.getTotalPrice()));
    //应收金额
    printText(outputStream,PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(shiftWork.getAcceptAmount()));
    //优惠金额
    printText(outputStream,PrinterConstant.DISCOUNT_AMOUNT + NumberFormatUtils.formatFloatNumber(shiftWork.getDiscountAmount()));
    //支付类优惠
    printText(outputStream,PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(shiftWork.getPayPrivilege()));
    //损益金额
    printText(outputStream,PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(shiftWork.getGainLoseAmount()));
    //实收收入
    printText(outputStream,"实际收入:" + NumberFormatUtils.formatFloatNumber(shiftWork.getActualAmount()));
    //不计入统计金额
    printText(outputStream,"不计入统计金额:" + NumberFormatUtils.formatFloatNumber(shiftWork.getStaticsExclusive()));
    printText(outputStream," ");
    //--分类统计
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    if (is58) {
       printCenterText(outputStream,"----------- 分类统计 ----------");
    } else {
       printCenterText(outputStream,"----------------- 分类统计 ----------------");
    }
    printText(outputStream,"项目" + replace(is58 ? 2 : 4) + "数量" + replace(is58 ? 2 : 4) + "原  价" + replace(is58 ? 4 : 6) + "优惠后金额");
     for (AppShiftCateInfo collect : shiftWork.getCategoryCollectList()) {
        String receivable = NumberFormatUtils.formatFloatNumber(collect.getReceivableAmount());
        String actualNum = NumberFormatUtils.formatFloatNumber(collect.getActualAmount());
        String result = replaceKitchen(collect.getCateName(),is58 ? 4 : 6) + replaceKitchen(String.valueOf(collect.getCateNumber()),is58 ? 6 : 8)
            + replaceKitchen(receivable , is58 ? 11 : 13) + actualNum;
       printText(outputStream,result);
      }
      String receivableTotal = NumberFormatUtils.formatFloatNumber(shiftWork.getCategoryCollectReceivableMoneyTotal());
      String actualTotal = NumberFormatUtils.formatFloatNumber(shiftWork.getCategoryCollectRealMoneyTotal());
      String result = replaceKitchen("总计",is58 ? 4 : 6) + replaceKitchen(String.valueOf(shiftWork.getCategoryCollectTotal()),is58 ? 6 :8)
          + replaceKitchen(receivableTotal,is58 ? 11 : 13) + actualTotal;
    printText(outputStream,result);

    dividerLine(outputStream, is58);
    //交接班 尾
    shiftWorkFoot(is58,outputStream);
  }


  /**
   * 交接班 通用头部
   */
  private void shiftWorkHead(ShiftWork shiftWork, OutputStream outputStream) throws IOException {
    //店铺名称
    printText(outputStream,"店铺名称: " + office.getName());
    //交班用户
    printText(outputStream,"交接用户: " + shiftWork.getShiftUserName());
    //收银员
    printText(outputStream,"收 银 员: " + shiftWork.getCashierName());
    //交接时间
    printText(outputStream,PrinterConstant.SHIFT_TIME + sdf.format(shiftWork.getShitTime()));
    //账单时间段
    Date startTime = shiftWork.getStartTime();
    Date endTime = shiftWork.getEndTime();
    if (startTime != null && endTime != null) {
      printText(outputStream,PrinterConstant.SHIFT_TIME_ZONE + sdf.format(startTime));
      printText(outputStream,"           ~" + sdf.format(endTime));
    }
    //区域
    printText(outputStream,PrinterConstant.WORK_ZONE + shiftWork.getWorkZone());
  }


  /**
   * 交接班
   */
  private void shiftWorkBody(ShiftWork shiftWork,boolean is58, OutputStream outputStream, boolean isDaily) throws IOException {
    //--.收银汇总
    if (is58) {
      printCenterText(outputStream,"--------- 收银汇总 --------");
    } else {
      printCenterText(outputStream,"----------------- 收银汇总 ----------------");
    }
    // 收银员总计  银行卡 翼支付 会员卡 支付宝 现金
    printText(outputStream,"项目" + getSpace("项目",(is58 ? 18 : 22)) + "笔数" + getSpace("笔数",(is58 ? 6 :8)) + "金额");
    List<AppCashCollect> cashCollectList = shiftWork.getCashCollectList();
    for (AppCashCollect collect : cashCollectList) {
      String name = collect.getName();
      int num = collect.getNum();
      String money = NumberFormatUtils.formatFloatNumber(collect.getMoney());
      String numString = String.valueOf(num);
      printText(outputStream,name + getSpace(name,is58 ? 19 : 23) + numString + getSpace(numString,is58 ? 6 : 8) + money);
    }
    //--1.消费统计

    if (is58) {
      printCenterText(outputStream,"--------- 消费统计 --------");
    } else {
      printCenterText(outputStream,"--------------- 消费统计 --------------");
    }
    //单数
    printText(outputStream,"单数: " + shiftWork.getBillsCount());
    //人数
    printText(outputStream,"人数: " + shiftWork.getPeopleNum() + replace(9));
    //总价
    printText(outputStream,"总价:"+NumberFormatUtils.formatFloatNumber(shiftWork.getTotalPrice()));
    //应收金额
    printText(outputStream,PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(shiftWork.getAcceptAmount()));
    //优惠金额
    printText(outputStream,PrinterConstant.DISCOUNT_AMOUNT + NumberFormatUtils.formatFloatNumber(shiftWork.getDiscountAmount()));
    //支付类优惠
    printText(outputStream,PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(shiftWork.getPayPrivilege()));
    //损益金额
    printText(outputStream,"损益金额:"+  NumberFormatUtils.formatFloatNumber(shiftWork.getGainLoseAmount()));
    //实收金额
    printText(outputStream,"实际收入:" + NumberFormatUtils.formatFloatNumber(shiftWork.getActualAmount()));
    //不计入统计金额
    printText(outputStream,"不计入统计金额:" + NumberFormatUtils.formatFloatNumber(shiftWork.getStaticsExclusive()));
    dividerLine(outputStream, is58);
    //交接班 尾
    shiftWorkFoot(is58, outputStream);
  }

  /**
   * 交接班 尾
   */
  private void shiftWorkFoot(boolean is58,OutputStream outputStream) throws IOException {
    //9.签字
    printText(outputStream,"交班用户签字:");
    printText(outputStream," ");
    printText(outputStream,"当班经理签字:");
    printText(outputStream," ");
    //10.尾
    dividerLine(outputStream, is58);
    flush(outputStream);
  }


  /**
   * 结账单(客户联)总价,优惠,折后等等
   */

  private void getBillWithCustomInfoAmount(AppCustomAmount customAmount,boolean is58, OutputStream outputStream, PxOrderInfo orderInfo) throws IOException {
    //总金额
    double countMoney = customAmount.getCountMoney();
    //应收金额
    double mReceivableAmount = customAmount.getReceivableAmount();
    //实收金额
    double mActualAmount = customAmount.getActualAmount();
    //消费金额
    double mConsumeAmount = customAmount.getConsumeAmount();
    //优惠金额
    double mDiscAmount = customAmount.getDiscAmount();
    //找零金额
    double mChangeAmount = customAmount.getChangeAmount();
    //附加费
    double mSurchargeAmount = customAmount.getSurchargeAmount();
    //抹零
    double mTailMoney = orderInfo.getTailMoney() * -1;
    //支付类优惠
    double payPrivilege = (orderInfo.getPayPrivilege() == null ) ? 0 : orderInfo.getPayPrivilege();
    //总金额
    if (countMoney > 0.0) {
      String totalMoney = PrinterConstant.TOTAL_AMOUNT + NumberFormatUtils.formatFloatNumber(countMoney);
      printText(outputStream,totalMoney);
    }
    if (mReceivableAmount > 0.0) {
      //应收金额
      String receivableAmount = PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount);
      printText(outputStream,receivableAmount);
    }
    if (mActualAmount > 0.0) {
      //实收金额
      String actualAmount = PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mActualAmount);
      printText(outputStream,actualAmount);
    }
    if (mDiscAmount > 0.0) {
      //优惠金额
      String discAmount = PrinterConstant.DISCOUNT_AMOUNT + ":" + NumberFormatUtils.formatFloatNumber(mDiscAmount);
      printText(outputStream,discAmount);
    }
    //支付类优惠
    printText(outputStream,PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
    //找零金额
    if (mChangeAmount > 0.0) {
      String changeAmount = PrinterConstant.CHANGE_CASH + NumberFormatUtils.formatFloatNumber(mChangeAmount);
      printText(outputStream,changeAmount);
    }

    //附加费金额
    if (mSurchargeAmount > 0.0) {
      String surchargeAmount = PrinterConstant.SURCHARGE_CASH + NumberFormatUtils.formatFloatNumber(mSurchargeAmount);
      printText(outputStream,surchargeAmount);
    }
    //损益金额
    if (mTailMoney > 0.0) {
      String tailMoney = PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(mTailMoney);
      printText(outputStream,tailMoney);
    }
    //各项实收
    printText(outputStream,"--------实收详情--------");
    everyReceived(orderInfo,outputStream);
    //免单
    freePayInfo(outputStream,orderInfo,is58);
  }


  /**
   * 仅供账单详情应收,实收,找零,抹零
   */
  private void getRecMoney(PxOrderInfo orderInfo,boolean is58, OutputStream outputStream) throws IOException {
    //应收金额
    double mReceivableAmount = orderInfo.getAccountReceivable();
    //实收金额
    double mActualAmount = orderInfo.getRealPrice();
    //找零金额
    double mChangeAmount = orderInfo.getTotalChange();
    //补足金额
    double mcomplementMoey = orderInfo.getComplementMoney();
    //附加费
    double mSurchargeAmount = orderInfo.getExtraMoney();
    //抹零金额 损益
    double mTailMoney = orderInfo.getTailMoney() * -1;
    //支付列优惠
    double payPrivilege = (orderInfo.getPayPrivilege() == null ) ? 0 : orderInfo.getPayPrivilege();
    //应收金额  //应收 = 点单总额 + 附加费 + 补足金额
    printText(outputStream,PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount));
    //实收金额
    printText(outputStream,PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mActualAmount));
    //补足金额
    if ( mcomplementMoey > 0.0) {
      printText(outputStream,"补足金额:" + NumberFormatUtils.formatFloatNumber(mcomplementMoey));
    }
    //附加费
    if ( mSurchargeAmount > 0.0) {
      printText(outputStream,"附加费金额:" + NumberFormatUtils.formatFloatNumber(mSurchargeAmount));
      printText(outputStream," ");
    }
    //找零金额
    if ( mChangeAmount > 0.0) {
      printText(outputStream,"找零金额:" + NumberFormatUtils.formatFloatNumber(mChangeAmount));
    }
    //支付类优惠
    printText(outputStream,PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
    //损益金额
    if (mTailMoney > 0.0) {
      String tailMoney = PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(mTailMoney);
      printText(outputStream,tailMoney);
    }
    //各项实收
    printText(outputStream,"--------实收详情--------");
    everyReceived(orderInfo,outputStream);
    //免单
    freePayInfo(outputStream,orderInfo,is58);
  }


  /**
   * 商品真实实体数据
   * @param isPointMenu 是否点菜单
   */
  @Deprecated
  private void getProductData(List<PxOrderDetails> list,boolean is58,OutputStream outputStream , boolean isPointMenu) throws IOException {
    //商品实体
    for (PxOrderDetails details : list) {
      PxProductInfo dbProduct = details.getDbProduct();
      PxFormatInfo dbFormatInfo = details.getDbFormatInfo();
      //未下单的不打印
      if (PxOrderDetails.ORDER_STATUS_UNORDER.equals(details.getOrderStatus())) continue;
      String status = "";
      if (PxOrderDetails.ORDER_STATUS_ORDER.equals(details.getOrderStatus())) {
        status = "";
      } else if (PxOrderDetails.ORDER_STATUS_REFUND.equals(details.getOrderStatus())) {
        status = "(退菜)";
      } else {
        status = "(未下)";
      }
      String formatInfo = "";
      boolean hasFormat = (dbFormatInfo != null);
      if (hasFormat) {
        formatInfo = "(" + dbFormatInfo.getName() + ")";
      }
      String methodInfo = "";
      if (details.getDbMethodInfo() != null) {
        methodInfo = "(" + details.getDbMethodInfo().getName() + ")";
      }
      String delay = "";
      if (isPointMenu && (details.getStatus().equals(PxOrderDetails.STATUS_DELAY))) {
        delay = "(待)";
      }
      //小计
      double money = 0;
      //单价
      double unitPrice = 0;
      //规格
      PxOrderInfo orderInfo = details.getDbOrder();
      if (orderInfo.getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
        money = details.getVipPrice() * details.getDiscountRate() / 100;
        if (hasFormat){
          PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
              .queryBuilder()
              .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
              .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(dbProduct.getId()))
              .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(dbFormatInfo.getId()))
              .unique();
          unitPrice = (rel == null ? dbProduct.getVipPrice() : rel.getVipPrice());
        }else{
          unitPrice = dbProduct.getVipPrice();
        }
      } else {
        money = details.getPrice() * details.getDiscountRate() / 100;
        if (hasFormat){
          PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
              .queryBuilder()
              .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
              .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(dbProduct.getId()))
              .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(dbFormatInfo.getId()))
              .unique();
          unitPrice = (rel == null ? dbProduct.getPrice() : rel.getPrice());
        }else{
          unitPrice = dbProduct.getPrice();
        }
      }
      //②
      String realMoney =  NumberFormatUtils.formatFloatNumber(money) ;
      String realUnitMoney = NumberFormatUtils.formatFloatNumber(unitPrice);
      String isGift = "";
      //赠品 不显示价格
      if (PxOrderDetails.GIFT_TRUE.equals(details.getIsGift())) {
        isGift = "(赠)";
        realMoney = "(0)";
      }
      //①
      String before = dbProduct.getName() + formatInfo + methodInfo + delay + status + isGift;
      //③
      String unit = "";
      //双单位
      if (dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        Double realNum = details.getNum();
        unit = NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber())
            + dbProduct.getUnit()  + "/" + realNum.intValue() + dbProduct.getOrderUnit() ;
      } else {
        Double realNumber = details.getNum();
        unit = realNumber.intValue()  + dbProduct.getUnit() ;
      }
      printText(outputStream,before + getSpace(before, (is58 ? 15 : 22)) + unitPrice + getSpace(realUnitMoney,(is58 ? 7 : 10)) + realMoney+getSpace(realMoney ,(is58 ? 7 : 10)) + unit);
      //备注
      if (isPointMenu) {
        String remarks = details.getRemarks();
        if (!TextUtils.isEmpty(remarks)) {
          printText(outputStream,"--备注:" + remarks);
        }
      }
    }
  }

  /**
   *  合并 商品真实实体数据
   */
  private void mergeProductData(PxOrderInfo orderInfo,List<PxOrderDetails> list,boolean is58,OutputStream outputStream , boolean isPointMenu) throws IOException {
    //商品实体
    for (PxOrderDetails details : list) {
      PxProductInfo dbProduct = details.getPrintProd();
      PxFormatInfo dbFormatInfo = details.getPrintFormat();
      PxMethodInfo dbMethodInfo = details.getPrintMethod();
      double refundNum = details.getRefundNum();
      double refundMultNum = details.getRefundMultNum();

      //下单状态 未下单的不打印
      if (PxOrderDetails.ORDER_STATUS_UNORDER.equals(details.getOrderStatus())) continue;
      //String orderStatus = "";
      //if (PxOrderDetails.ORDER_STATUS_REFUND.equals(details.getOrderStatus())) {
      //  orderStatus = "(退菜)";
      //}
      //规格
      String formatInfo = "";
      if (dbFormatInfo != null) {
        formatInfo = "(" + dbFormatInfo.getName() + ")";
      }
      //做法
      String methodInfo = "";
      if (dbMethodInfo != null) {
        methodInfo = "(" + dbMethodInfo.getName() + ")";
      }
      //上菜状态
      String delay = "";
      if (isPointMenu && (details.getStatus().equals(PxOrderDetails.STATUS_DELAY))) {
        delay = "(待)";
      }
      //小计
      double money = details.getPrice() * details.getDiscountRate() / 100;
      //单价
      double unitPrice = details.getUnitPrice();
      if (orderInfo.getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
        money = details.getVipPrice() * details.getDiscountRate() / 100;
        unitPrice = details.getUnitVipPrice();
      }
      //最后价格
      String realMoney =  NumberFormatUtils.formatFloatNumber(money) ;
      String realUnitMoney = NumberFormatUtils.formatFloatNumber(unitPrice);
      String isGift = "";
      //赠品 不显示价格
      if (PxOrderDetails.GIFT_TRUE.equals(details.getIsGift())) {
        isGift = "(赠)";
        realMoney = "(0)";
      }
      //①
      String before = dbProduct.getName() + formatInfo + methodInfo + delay  + isGift;
      //③
      String unit = "";
      //双单位
      boolean isMultiUnit = dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE);
      if (isMultiUnit) {
        Double realNum = details.getNum();
        unit = NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber())
            + dbProduct.getUnit()  + "/" + realNum.intValue() + dbProduct.getOrderUnit() ;
      } else {
        Double realNumber = details.getNum();
        unit = realNumber.intValue()  + dbProduct.getUnit() ;
      }

      printText(outputStream,before + getSpace(before, (is58 ? 15 : 22)) + unitPrice + getSpace(realUnitMoney,(is58 ? 7 : 10)) + realMoney+getSpace(realMoney ,(is58 ? 7 : 10)) + unit);
      //已退数量
      if (refundNum > 0 || refundMultNum > 0){
        String refundMultiNumAndUnit = refundMultNum + dbProduct.getUnit() +"/" + refundNum + dbProduct.getOrderUnit() +")";
        String refunNumAndUnit = refundNum + dbProduct.getUnit() + ")";
        printText(outputStream,"   (已退:" + (isMultiUnit ? refundMultiNumAndUnit : refunNumAndUnit));
      }
      //备注
      if (isPointMenu) {
        String remarks = details.getRemarks();
        if (!TextUtils.isEmpty(remarks)) {
          printText(outputStream,"--备注:" + remarks);
        }
      }
    }
  }



  /**
   * 桌号和单号
   */
  private void getTableIdAndUniqueIdWithLarge(PxOrderInfo orderInfo, OutputStream outputStream) throws IOException {
    boolean retailOrderInfo = orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_RETAIL);//是零售单
    //单号桌号
    String billAndTable = "单  号:" + Integer.valueOf(orderInfo.getOrderNo().substring(24, 30));
    if (!retailOrderInfo) {//零售单
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .unique();
      PxTableInfo dbTable = unique.getDbTable();
      billAndTable = billAndTable + "  桌 号:" + dbTable.getName();
    }
    printBoldText(outputStream,billAndTable);
    if (!retailOrderInfo) {//零售单
      printText(outputStream,"人  数:" + orderInfo.getActualPeopleNumber());
    }
  }
  /**
   * 账单号
   */
  private void getBillId(PxOrderInfo orderInfo,OutputStream outputStream) throws IOException {
    String billId = PrinterConstant.BILL_NO + orderInfo.getOrderNo().substring(10, 30);
    printText(outputStream,billId);
  }

  /**
   * 人数
   */
  private void getPeopleNum(PxOrderInfo orderInfo, OutputStream outputStream) throws IOException{
    boolean table = orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE);//桌位单
    String msg;
    if (table) {
      msg = PrinterConstant.PEOPLE_NUMBER + orderInfo.getActualPeopleNumber();
    } else {
      msg = PrinterConstant.PEOPLE_NUMBER + "无";
    }
    printText(outputStream,msg);
  }


  /**
   * 合计项
   */
  private void getSumNumber(List<PxOrderDetails> detailsList, OutputStream outputStream) throws IOException {
      printText(outputStream,PrinterConstant.COLLECT + ":" + detailsList.size() + "项");
  }

  /**
   * 总价
   */
  private void getTotalPrice(OutputStream outputStream, List<PxOrderDetails> detailsList) throws IOException {
    double countMoney = 0;
    if (detailsList == null || detailsList.size() == 0) return;
    PxOrderInfo dbOrder = detailsList.get(0).getDbOrder();
    //refresh
    DaoServiceUtil.getOrderInfoService().refresh(orderInfo);
    //商品实体
    for (PxOrderDetails details : detailsList) {
      if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
        if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(dbOrder.getUseVipCard())){
          countMoney += (details.getVipPrice() * details.getDiscountRate()) / 100;
        }else {
          countMoney += (details.getPrice() * details.getDiscountRate()) / 100;
        }
      }
    }
    printText(outputStream,PrinterConstant.TOTAL_AMOUNT + NumberFormatUtils.formatFloatNumber(countMoney));
  }
  //@formatter:on
  //分割线
  private void dividerLine(OutputStream outputStream, boolean is58) throws IOException {
    outputStream.write(byteCommands[16]);
    if (is58) {
      printText(outputStream, "--------------------------------");
    } else {
      printText(outputStream, "---------------------------------------------");
    }
    outputStream.write(byteCommands[15]);
  }

  /**
   * 品名(金额)和数量/单位
   */
  private void getProductProperty(boolean is58, OutputStream outputStream) throws IOException {
    printText(outputStream,
        "品名" + getSpace("品名", (is58 ? 15 : 22)) + "单价" + getSpace("单价", (is58 ? 6 : 9)) + "小计"
            + getSpace("小计", (is58 ? 6 : 9)) + "数量");

    //printWriter.println("品名"+getSpace("品名", (isFormat58 ? 15 : 20)) +"单价"+getSpace("单价",(isFormat58 ? 6 : 8) )
    //    +"小计"+getSpace("小计",(isFormat58 ? 6 : 8))+"数量");
  }

  /**
   * 服务员和单位
   */
  private void getWaiterAndUnit(PxOrderInfo orderInfo, OutputStream outputStream)
      throws IOException {
    User dbUser = orderInfo.getDbUser();
    String name = (dbUser == null) ? "admin" : dbUser.getName();
    int lengthWaiter = PrinterConstant.WAITER.getBytes().length + name.getBytes().length;
    int lengthUnit =
        PrinterConstant.HEAD_UNIT.getBytes().length + PrinterConstant.MONEY_UNIT.getBytes().length;
    int lengthWaiterAndUnit = 30 - (lengthWaiter + lengthUnit);
    String waiterAndUnitBlank = "";
    for (int i = 0; i < lengthWaiterAndUnit; i++) {
      waiterAndUnitBlank += " ";
    }
    String waiterAndUnit =
        PrinterConstant.CASHIER + name + waiterAndUnitBlank + PrinterConstant.HEAD_UNIT
            + PrinterConstant.MONEY_UNIT;
    printText(outputStream, waiterAndUnit);
  }

  /**
   * 各项实收 金额
   */
  //@formatter:off
 private void everyReceived(PxOrderInfo orderInfo, OutputStream outputStream) {
   Cursor cursor = null;
   try {
     SQLiteDatabase db = DaoServiceUtil.getPayInfoDao().getDatabase();
     //各项实收
     cursor = db.rawQuery("Select sum(pay.RECEIVED),pay.PAYMENT_NAME,sum(pay.CHANGE),pay.PAYMENT_TYPE"
         + " From PxPayInfo pay"
         + " Where pay.PX_ORDER_INFO_ID = " + orderInfo.getId()
         + " Group by pay.PAYMENT_TYPE"
         ,null);
      //各项实收
     while (cursor.moveToNext()) {
       double received = cursor.getDouble(0);
       String type = cursor.getString(3);
       if (received > 0) {
         if (PxPaymentMode.TYPE_CASH.equals(type)){
            double change = cursor.getDouble(2);
           printText(outputStream,cursor.getString(1) + ":" + received + "   找零:"+NumberFormatUtils.formatFloatNumber(change));
         }else{
           printText(outputStream,cursor.getString(1) + ":" + NumberFormatUtils.formatFloatNumber(received));
         }
       }
     }
   } catch (Exception e) {
      e.printStackTrace();
      Logger.e(e.toString());
   } finally {
    IOUtils.closeCloseables(cursor);
   }
 }

  /**
   * 打印免单原因
   */
  private void freePayInfo(OutputStream outputStream,PxOrderInfo orderInfo,boolean is58) throws IOException{
    QueryBuilder<PxPayInfo> qb = DaoServiceUtil.getPayInfoService().queryBuilder();
    qb.where(PxPayInfoDao.Properties.PxOrderInfoId.eq(orderInfo.getId()));
    qb.where(PxPayInfoDao.Properties.PaymentType.eq(PxPaymentMode.TYPE_FREE));
    List<PxPayInfo> freePayInfoList = qb.list();
    if (freePayInfoList != null && freePayInfoList.size() > 0) {
      //分割线
      dividerLine(outputStream, is58);
      for (PxPayInfo payInfo : freePayInfoList) {
        printText(outputStream,"免单金额:" + payInfo.getReceived() +"  原因:"+payInfo.getRemarks());
      }
    }
  }

  /**
   * 分类统计
   */

  private void categoryCollect(PxOrderInfo orderInfo, OutputStream outputStream,boolean is58) throws IOException {
    PxSetInfo setInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    //打印分类统计
    if (setInfo != null && PxSetInfo.FINANCE_PRINT_CATEGORY_TRUE.equals(setInfo.getIsFinancePrintCategory())) {
      if (is58) {
        printCenterText(outputStream,"----------- 分类统计 ----------");
      } else {
        printCenterText(outputStream,"------------------- 分类统计 ------------------");
      }

      String msg = "项目" + replace(is58 ? 2 : 4) + "数量" + replace(is58 ? 2 : 4) + "原  价" + replace(is58 ? 4 : 6) + "优惠后金额";
      printText(outputStream,msg);

      SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
      Cursor cursor = null;
      try {
      cursor = db.rawQuery(
          "Select sum(d.NUM),sum(d.FINAL_PRICE),sum(d.FINAL_PRICE * d.DISCOUNT_RATE/ 100),c.NAME"
            + " From OrderDetails d"
            + " Join ProductInfo p On p._id = d.PX_PRODUCT_INFO_ID"
            + " Join ProductCategory c On c._id = p.PX_PRODUCT_CATEGORY_ID"
            + " Where d.PX_ORDER_INFO_ID = " + orderInfo.getId()
            + " And d.ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER
            + " Group By c._id" ,null);
      //@formatter:on
        while (cursor.moveToNext()) {
          double originPrice = cursor.getDouble(1);
          double afterDiscount = cursor.getDouble(2);
          String text = replaceKitchen(cursor.getString(3), (is58) ? 4 : 6) + replaceKitchen(
              String.valueOf(cursor.getDouble(0)), (is58) ? 6 : 8) + replaceKitchen(
              NumberFormatUtils.formatFloatNumber(originPrice), (is58) ? 11 : 13)
              + NumberFormatUtils.formatFloatNumber(afterDiscount);
          printText(outputStream, text);
        }
      } catch (Exception e) {
      } finally {
        IOUtils.closeCloseables(cursor);
      }
    }
    //line
    dividerLine(outputStream, is58);
  }

  /**
   * 结账单(财务联)总价,优惠,折后等等
   */
  private void getBillWithFinanceInfoAmount(PxOrderInfo orderInfo, AppFinanceAmount financeAmount,
      boolean is58, OutputStream outputStream) throws IOException {
    //总金额
    double mTotalAmount = financeAmount.getmPayCashTotal();
    //应收金额
    double mReceivableAmount = orderInfo.getAccountReceivable();
    //实收金额
    double mActualAmount = orderInfo.getRealPrice();
    //找零
    double mChangeMoney = orderInfo.getTotalChange();
    //抹零
    double mTailMoney = orderInfo.getTailMoney() * -1;
    //优惠金额
    double mDisMoney = orderInfo.getDiscountPrice();
    //支付类优惠
    double payPrivilege = (orderInfo.getPayPrivilege() == null) ? 0 : orderInfo.getPayPrivilege();
    //总金额
    String totalAmount =
        PrinterConstant.TOTAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mTotalAmount);
    printText(outputStream, totalAmount);
    //优惠金额
    if (mDisMoney > 0.0) {
      String disMoney =
          PrinterConstant.DISCOUNT_AMOUNT + NumberFormatUtils.formatFloatNumber(mDisMoney);
      printText(outputStream, disMoney);
    }
    //支付类优惠
    printText(outputStream,
        PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
    //应收金额
    String receivableAmount =
        PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount);
    printText(outputStream, receivableAmount);
    //实收金额
    String actualAmount =
        PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mActualAmount);
    printText(outputStream, actualAmount);
    //找零金额
    if (mChangeMoney > 0.0) {
      String changeMoney =
          PrinterConstant.CHANGE_CASH + NumberFormatUtils.formatFloatNumber(mChangeMoney);
      printText(outputStream, changeMoney);
    }
    //损益金额
    if (mTailMoney > 0.0) {
      String tailMoney =
          PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(mTailMoney);
      printText(outputStream, tailMoney);
    }
    //各项实收
    printText(outputStream, "--------实收详情--------");
    everyReceived(orderInfo, outputStream);
    //免单
    freePayInfo(outputStream, orderInfo, is58);
  }

  //打印头部分
  private void initHead(OutputStream outputStream) throws IOException {
    //店铺名称
    outputStream.write(byteCommands[16]);
    outputStream.write(byteCommands[8]);
    outputStream.write(byteCommands[6]);
    printText(outputStream, office.getName());
    outputStream.write(byteCommands[7]);
    outputStream.write(byteCommands[3]);
    outputStream.write(byteCommands[15]);
  }

  //打印结尾部分
  private void initFoot(OutputStream outputStream, boolean is58) throws IOException {
    //分割线
    dividerLine(outputStream, is58);
    //打印时间字样和值
    flush(outputStream);
  }

  /**
   * 打印文字
   */
  private void flush(OutputStream outputStream) throws IOException {
    printText(outputStream, "打印时间:" + sdf.format(new Date()));
    //切纸
    outputStream.write(byteCommands[18]);
    printText(outputStream, "  ");
    printText(outputStream, "  ");
    printText(outputStream, "  ");
    //outputStream.flush();
  }

  private void printText(OutputStream outputStream, String msg) throws IOException {
    String text = msg + "\n";
    outputStream.write(text.getBytes("GB2312"));
    outputStream.write(byteCommands[0]);
  }

  private void printBoldText(OutputStream outputStream, String msg) throws IOException {
    outputStream.write(byteCommands[15]);
    outputStream.write(byteCommands[8]);
    printText(outputStream, msg);
    outputStream.write(byteCommands[7]);
  }

  //倍宽高
  private void printBigtext(OutputStream os, String msg) throws IOException {
    os.write(byteCommands[6]);
    printText(os, msg);
    os.write(byteCommands[3]);
  }

  private void printCenterBoldText(OutputStream outputStream, String msg) throws IOException {
    String text = msg + "\n";
    outputStream.write(byteCommands[16]);
    outputStream.write(byteCommands[8]);
    outputStream.write(byteCommands[6]);
    printText(outputStream, text);
    outputStream.write(byteCommands[7]);
    outputStream.write(byteCommands[3]);
    outputStream.write(byteCommands[15]);
  }

  private void printCenterText(OutputStream outputStream, String msg) throws IOException {
    String text = msg + "\n";
    outputStream.write(byteCommands[16]);
    printText(outputStream, text);
    outputStream.write(byteCommands[15]);
  }

  /**
   * 补齐空格
   */
  private String getSpace(String before, int sum) throws UnsupportedEncodingException {
    int length = sum - before.getBytes("GBK").length;
    StringBuilder sb = new StringBuilder();
    while (sb.toString().getBytes().length < length) {
      sb.append(" ");
    }
    if (TextUtils.isEmpty(sb)) {
      sb.append(" ");
    }
    return sb.toString();
  }

  /**
   * 补齐空格
   */
  private String replace(int num) {
    StringBuilder sb = new StringBuilder();
    String s = null;
    try {
      s = new String("  ".getBytes(), "GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      s = " ";
    }
    while (sb.toString().length() < num) {
      sb.append(s);
    }
    if (sb.toString().length() == 0) {
      sb.append(" ");
    }
    return sb.toString();
  }

  /**
   * 补齐后厨打印
   */
  private String replaceKitchen(String before, int num) {
    StringBuilder sb = new StringBuilder();
    sb.append(before);
    String s = null;
    try {
      s = new String(" ".getBytes(), "GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      s = " ";
    }
    while (sb.toString().length() < num) {
      sb.append(s);
    }
    return sb.toString();
  }

  //@formatter:off
  public static final class Builder {
    private List<PrintDetailsCollect> printDetailsCollectList;
    private ShiftWork shiftWork;
    private boolean isRefundBill;
    private AppFinanceAmount financeAmount;
    private AppCustomAmount customAmount;
    private List<AppSaleContent> saleContentList;
    private PxProductCategory category;
    private AppBillCount appBillCount;
    private CollectionContentEvent contentevent;
    private PxOrderInfo orderInfo;
    private List<PxOrderDetails> collectionList;
    private boolean isOncePrint;
    private int printNum;
    private List<PxOrderInfo> orderInfoList;
    private String planName;
    private PxRechargeRecord vipRechargeRecord;
    private PxVipInfo mVipInfo;
    private final int mMode;
    private double mConsume;
    private PxTableAlteration mAlteration;
    private String mAddress;

    public Builder(int mode) {
      mMode = mode;
    }

    public Builder printDetailsCollectList(List<PrintDetailsCollect> val) {
      printDetailsCollectList = val;
      return this;
    }

    public Builder shiftWork(ShiftWork val) {
      shiftWork = val;
      return this;
    }

    public Builder isRefundBill(boolean val) {
      isRefundBill = val;
      return this;
    }

    public Builder financeAmount(AppFinanceAmount val) {
      financeAmount = val;
      return this;
    }

    public Builder customAmount(AppCustomAmount val) {
      customAmount = val;
      return this;
    }

    public Builder saleContentList(List<AppSaleContent> val) {
      saleContentList = val;
      return this;
    }

    public Builder category(PxProductCategory val) {
      category = val;
      return this;
    }

    public Builder appBillCount(AppBillCount val) {
      appBillCount = val;
      return this;
    }

    public Builder contentevent(CollectionContentEvent val) {
      contentevent = val;
      return this;
    }

    public Builder orderInfo(PxOrderInfo val) {
      orderInfo = val;
      return this;
    }

    public Builder orderDetailsList(List<PxOrderDetails> val) {
      collectionList = val;
      return this;
    }

    public Builder isOncePrint(boolean val) {
      isOncePrint = val;
      return this;
    }

    public Builder printNum(int val) {
      printNum = val;
      return this;
    }

    public Builder orderInfoList(List<PxOrderInfo> val) {
      orderInfoList = val;
      return this;
    }

    public Builder planName(String val) {
      planName = val;
      return this;
    }

    public Builder vipRechargeRecord(PxRechargeRecord val) {
      vipRechargeRecord = val;
      return this;
    }

    public Builder vipInfo(PxVipInfo val) {
      mVipInfo = val;
      return this;
    }

    public Builder consume(double val) {
      mConsume = val;
      return this;
    }

    public Builder tableAlert(PxTableAlteration alteration) {
      mAlteration = alteration;
      return this;
    }

    public Builder address(String  address) {
      mAddress = address;
      return this;
    }
    public BTPrintTask build() {
      return new BTPrintTask(this);
    }
  }
}