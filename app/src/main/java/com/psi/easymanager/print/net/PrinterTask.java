package com.psi.easymanager.print.net;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.PdConfigRelDao;
import com.psi.easymanager.dao.PrintDetailsCollectDao;
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
import com.psi.easymanager.module.PrintDetails;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductConfigPlan;
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
import de.greenrobot.dao.query.QueryBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_DETAILS;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_DETAILS_COLLECTION;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_FINANCE_INFO;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_SALE_COUNT;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_SHIFT_WORK;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_SHIFT_WORK_ALL_BILLS;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_TABLE;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_TEST;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_VIP_CONSUME_RECORD;
import static com.psi.easymanager.print.constant.BTPrintConstants.PRINT_MODE_VIP_RECHARGE_RECORD;
import static com.psi.easymanager.print.constant.BTPrintConstants.office;
import static com.psi.easymanager.print.constant.BTPrintConstants.sdf;

/**
 * User: ylw
 * Date: 2016-08-26
 * Time: 18:17
 * 网口打印普通票据
 */
public class PrinterTask implements Cloneable {

  public PrinterTask clone() {
    PrinterTask task = null;
    try {
      task = (PrinterTask) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      task = null;
      Logger.e(e.toString());
    }
    return task;
  }

  //是否一菜一切 1是 0 否
  public static String ONCE_PRINT = "1";
  public static String NO_ONCE_PRINT = "0";

  private List<PrintDetailsCollect> printDetailsCollectList;//下单生成的collect
  private ShiftWork shiftWork;//交接班
  private AppFinanceAmount financeAmount;//App财务联打印总价,应收等等
  private AppCustomAmount customAmount;
  private List<AppSaleContent> saleContentList;//销售统计
  private PxProductCategory category;//商品分类
  private AppBillCount appBillCount;//账单汇总
  private CollectionContentEvent contentevent;
  private PxOrderInfo orderInfo; //订单
  private PxTableAlteration mTableAlert;//移并桌
  private String ip;//打印机IP
  private PxPrinterInfo mPrinterInfo;//打印机
  private List<PrintDetails> detailsList;//要打印的详情list
  private List<PxOrderDetails> collectionList;//商品详情 汇总
  private boolean isOncePrint;//一菜一切
  private int printNum;//打印份数
  private List<PxOrderInfo> orderInfoList;//订单
  private boolean isFormat58;//true 58mm 、false 80mm
  private String planName;// 配菜方案名称
  private PxRechargeRecord vipRechargeRecord;//会员充值记录
  private PxVipInfo mVipInfo;//会员
  private int mMode;//打印模式
  private double mConsume;//会员本次消费

  public int getMode() {
    return mMode;
  }

  //打印纸类型
  public static String PAGER_58 = "0";

  public void setPrintInfo(PxPrinterInfo printerInfo) {
    this.isFormat58 = (printerInfo.getFormat().equals(PAGER_58)) ? true : false;
    this.ip = printerInfo.getIpAddress();
    this.mPrinterInfo = printerInfo;
  }

  //打印收银员交接班  + 日结订但
  public PrinterTask(int mode, ShiftWork shiftWork) {
    this.mMode = mode;
    this.shiftWork = shiftWork;
  }

  //收银员交接班 - 所有账单
  public PrinterTask(int mode, ShiftWork shiftWork, List<PxOrderInfo> list) {
    this.mMode = mode;
    this.shiftWork = shiftWork;
    this.orderInfoList = list;
  }

  //打印PrintDetails 用KitchenPrintActivity厨房打印
  public PrinterTask(int mode, boolean isOncePrint, List<PrintDetails> list, int printNum,
      String planName) {
    this.mMode = mode;
    this.detailsList = list;
    this.isOncePrint = isOncePrint;
    this.printNum = printNum;
    this.planName = planName;
  }

  //打印TableAltera
  public PrinterTask(int mode, PxTableAlteration alteration) {
    this.mMode = mode;
    this.mTableAlert = alteration;
  }

  //打印测试
  public PrinterTask(int mode) {
    this.mMode = mode;
  }

  //打印  结账单（财务联）
  public PrinterTask(int mode, PxOrderInfo orderInfo, List<PxOrderDetails> collectionList,
      AppCustomAmount customAmount, AppFinanceAmount financeAmount) {
    this.mMode = mode;
    this.orderInfo = orderInfo;
    this.collectionList = collectionList;
    this.customAmount = customAmount;
    this.financeAmount = financeAmount;
  }

  //打印  结账单（客户联）
  public PrinterTask(int mode, PxOrderInfo orderInfo, List<PxOrderDetails> collectionList,
      AppCustomAmount customAmount) {
    this.mMode = mode;
    this.orderInfo = orderInfo;
    this.collectionList = collectionList;
    this.customAmount = customAmount;
  }

  //打印账单汇总
  public PrinterTask(int mode, CollectionContentEvent contentEvent, AppBillCount appBillCount) {
    this.mMode = mode;
    this.contentevent = contentEvent;
    this.appBillCount = appBillCount;
  }

  //打印销售统计
  public PrinterTask(int mode, PxProductCategory category, List<AppSaleContent> saleContentList) {
    this.mMode = mode;
    this.category = category;
    this.saleContentList = saleContentList;
  }

  //打印账单明细 （客户联）+ (财务联）
  public PrinterTask(List<PxOrderDetails> detailsCollectionList, int mode) {
    this.mMode = mode;
    this.collectionList = detailsCollectionList;
  }

  // 点菜单
  public PrinterTask(int mode, PxOrderInfo orderInfo, List<PxOrderDetails> collectionList) {
    this.mMode = mode;
    this.orderInfo = orderInfo;
    this.collectionList = collectionList;
  }

  //打印collect 、 detailsAndConfigRel + collect 、 detailsAndConfigRel 退菜和撤单
  public PrinterTask(int mode, List<PrintDetailsCollect> collectList) {
    this.mMode = mode;
    this.printDetailsCollectList = collectList;
  }

  //打印collect 、 detailsAndConfigRel 退菜和撤单
  //public PrinterTask(int mode, List<PrintDetailsCollect> collectList) {
  //  this.mMode = mode;
  //  this.printDetailsCollectList = collectList;
  //}

  //打印会员消费记录
  public PrinterTask(int mode, PxVipInfo vipInfo, double consume) {
    this.mMode = mode;
    this.mVipInfo = vipInfo;
    this.mConsume = consume;
  }

  //打印会员充值记录
  public PrinterTask(int mode, PxRechargeRecord record, PxVipInfo vipInfo) {
    this.mMode = mode;
    this.vipRechargeRecord = record;
    this.mVipInfo = vipInfo;
  }

  //  用来 跳过 开钱箱
  public PrinterTask(String ip) {
    this.ip = ip;
  }

  public String getIp() {
    return ip;
  }

  public long getPrinterId() {
    return mPrinterInfo.getId();
  }

  //@formatter:off
  public void run(OutputStream outputStream,PrintWriter printWriter) throws IOException {
    if (mMode == PRINT_MODE_COLLECT) {//打印 Collect 、 DetailsAndConfigRel
      printCollectAndRel(printDetailsCollectList, mPrinterInfo, isFormat58, outputStream, printWriter);
    } else if (mMode == PRINT_MODE_DETAILS) { //撤单退单 OrderDetails
      //printResult = printOrderDetails(detailsList,isRefundBill, mPrinterInfo, isFormat58, outputStream, printWriter);
    } else if (mMode == PRINT_KITCHEN_DETAILS) {//KitchenPrintActivity 打印
      printKitchenOrderDetails(isOncePrint, detailsList, printNum, planName, isFormat58, outputStream, printWriter);
    } else if (mMode == PRINT_MODE_TABLE) { //移并桌
      printTableAlert1(mTableAlert,isFormat58, outputStream, printWriter);
    } else if (mMode == PRINT_MODE_TEST) { // 测试
      printerTestInfo(isFormat58, outputStream, printWriter);
    } else if (mMode == PRINT_MODE_DETAILS_COLLECTION) {//打印点菜单
      printDetailsCollection(orderInfo, collectionList,isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_BILL_SUMMARY) {//账单汇总
      printBillSummary(contentevent, appBillCount, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_SALE_COUNT) { //销售统计
      printSaleCount(category, saleContentList,  isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_CUSTOMERS_AL) {//结账单 客户联
      printCustomersAl(orderInfo, collectionList, customAmount, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_FINANCE_INFO) {//结账单 财务联
      printFinaceInfo(orderInfo, collectionList, financeAmount, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_BILL_DETAIL_CUSTOMER) {//账单明细 客户联
      printBillDetailCustomer( collectionList, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_BILL_DETAIL_FINANCE) {//账单明细 财务联
      printBillDetailFinance( collectionList, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_SHIFT_WORK) {//交接班
      printShiftWork( shiftWork, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_SHIFT_WORK_ALL_BILLS) {//交接班所有订单
      printShiftWorkAllBills( shiftWork, orderInfoList, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS) {//日结订单
      printDailyStatement(shiftWork, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_CATEGORY_COLLECT) {//打印分类汇总信息
      printCategoryCollect( shiftWork, isFormat58,outputStream,printWriter);
    } else if (mMode == PRINT_MODE_COLLECT_REFUND){//打印 退菜 撤单的
      printCollectAndRelRefund(printDetailsCollectList, mPrinterInfo, isFormat58, outputStream, printWriter);
    }else if (mMode == PRINT_MODE_VIP_CONSUME_RECORD){ //打印会员消费记录
      printVipConsumeRecord(isFormat58,mVipInfo,mConsume,outputStream,printWriter);
    }else if (mMode == PRINT_MODE_VIP_RECHARGE_RECORD) { //打印会员充值记录
      printVipRechargeRecord(isFormat58,vipRechargeRecord ,mVipInfo,outputStream,printWriter);
    }
  }
  /**
   * 会员充值记录
   */
  private void printVipRechargeRecord(boolean isFormat58,PxRechargeRecord record , PxVipInfo vipInfo,
      OutputStream outputStream,PrintWriter printWriter) throws IOException{
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(true, true, false));
      printWriter.println("会员充值记录");
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      printWriter.println("会员名称:  " + vipInfo.getName());
      printWriter.println("会员电话:  " + vipInfo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
      printWriter.println("充值金额:  " + NumberFormatUtils.formatFloatNumber(record.getMoney()));
      Double giving = record.getGiving();
      printWriter.println("赠送金额:  " + ((giving == null) ? 0.0 : giving));
      printWriter.println("余    额:  " + NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance()));
      dividerLine(outputStream,printWriter,isFormat58);
      //
      Date recordDate = record.getRechargeTime();
      printWriter.println("充值时间:  " + sdf.format((recordDate == null ) ? new Date() : recordDate));
      printWriter.println("充值门店:  " + office.getName());
      App app = (App) App.getContext();
      User user = app.getUser();
      printWriter.println("收 银 员:  " + ((user == null ) ? "admin" : user.getName()));
      initFoot(outputStream,printWriter,isFormat58);
  }
  /**
   * 会员消费记录
   */
  private void printVipConsumeRecord(boolean isFormat58,PxVipInfo vipInfo,double consume,
      OutputStream outputStream,PrintWriter printWriter) throws IOException {
     //title
     outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
     outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
     outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(true, true, false));
     printWriter.println("会员消费记录");
      //
     outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
     outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
     printWriter.println("会员名称:  " + vipInfo.getName());
     printWriter.println("会员电话:  " + vipInfo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
     printWriter.println("本次消费:  " + NumberFormatUtils.formatFloatNumber(consume));
     printWriter.println("余    额:  " + NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance() - consume));
     dividerLine(outputStream,printWriter,isFormat58);
     printWriter.println("消费时间:  " + sdf.format(new Date()));
     printWriter.println("消费门店:  " + office.getName());
     App app = (App) App.getContext();
     User user = app.getUser();
     printWriter.println("收 银 员:  " + ((user == null ) ? "admin" : user.getName()));
     initFoot(outputStream,printWriter,isFormat58);
  }
  /**
   * 打印分类汇总信息
   */
  //@formatter:off
  private void printCategoryCollect(ShiftWork shiftWork, boolean isFormat58, OutputStream outputStream,PrintWriter printWriter) throws IOException{
      //收银员交接班
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(true, true, false));
      printWriter.println("收银员交接班—所有分类");
      //head
      shiftWorkHead(shiftWork, outputStream, printWriter, sdf);
      //--分类统计
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      if (isFormat58) {
        printWriter.println("----------- 分类统计 ----------");
      } else {
        printWriter.println("------------------ 分类统计 -----------------");
      }

      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      printWriter.println("项目" + replace(isFormat58 ? 2:4) + "数量" + replace(isFormat58 ? 2:4) + "原  价" + replace(isFormat58 ? 4:6) + "优惠后金额");
      for (AppShiftCateInfo collect : shiftWork.getCategoryCollectList()) {
        String receivable = NumberFormatUtils.formatFloatNumber(collect.getReceivableAmount());
        String actualNum = NumberFormatUtils.formatFloatNumber(collect.getActualAmount());
        String result = replaceKitchen(collect.getCateName(),(isFormat58)? 4 : 6) +
            replaceKitchen(String.valueOf(collect.getCateNumber()),(isFormat58) ? 6 : 8) +
            replaceKitchen(receivable , (isFormat58) ? 11 : 13) + actualNum;
        printWriter.println(result);

      }
      String receivableTotal = NumberFormatUtils.formatFloatNumber(shiftWork.getCategoryCollectReceivableMoneyTotal());
      String actualTotal = NumberFormatUtils.formatFloatNumber(shiftWork.getCategoryCollectRealMoneyTotal());
      String result = replaceKitchen("总计",(isFormat58) ? 4 : 6) + replaceKitchen(String.valueOf(shiftWork.getCategoryCollectTotal()),
          (isFormat58) ? 6 : 8) + replaceKitchen(receivableTotal,isFormat58 ? 11 : 13) + actualTotal;
      printWriter.println(result);
      if (isFormat58) {
        printWriter.println("------------------------------");
      } else {
        printWriter.println("-----------------------------------------------");
      }
      //交接班 尾
      shiftWorkFoot( outputStream, printWriter, sdf, isFormat58);
  }

  /**
   * 打印收银员 交接班
   */
  private void printShiftWork(ShiftWork shiftWork, boolean isFormat58,
      OutputStream outputStream, PrintWriter printWriter) throws IOException {
      //收银员交接班
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(true, true, false));
      printWriter.println(PrinterConstant.SHIFT_WORK_ALL_ORDER);
      printWriter.println(" ");
      //交接班 通用头部
      shiftWorkHead(shiftWork, outputStream, printWriter, sdf);
      // 复用
      shiftWorkBody(shiftWork, outputStream, printWriter, sdf, isFormat58, false);
  }

  /**
   * 日结订单
   */
  //@formatter:off
  private void shiftWorkBodyDaily(ShiftWork shiftWork, OutputStream outputStream,
      PrintWriter printWriter, SimpleDateFormat sdf, boolean isFormat58)
      throws IOException {
    //--- 收银汇总
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    if (isFormat58) {
      printWriter.println("----------- 收银汇总 ----------");
    } else {
      printWriter.println("----------------- 收银汇总 ----------------");
    }
    // 收银员总计  银行卡 翼支付 会员卡 支付宝 现金
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    printWriter.println("项目" + getSpace("项目",(isFormat58 ? 18 : 22)) + "笔数" + getSpace("笔数",(isFormat58 ? 5 : 7)) + "金额");
    List<AppCashCollect> cashCollectList = shiftWork.getCashCollectList();
      for (AppCashCollect collect : cashCollectList) {
        String name = collect.getName();
        int num = collect.getNum();
        String money = NumberFormatUtils.formatFloatNumber(collect.getMoney());
        String numString = String.valueOf(num);
        printWriter.println(name + getSpace(name,(isFormat58 ? 19 : 23)) + numString + getSpace(numString,(isFormat58 ? 6 : 8)) + money);
      }
    //--1.消费统计
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    if (isFormat58) {
      printWriter.println("----------- 消费统计 ----------");
    } else {
      printWriter.println("----------------- 消费统计 ----------------");
    }

    //单数
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    printWriter.println("单数: " + shiftWork.getBillsCount());
    //人数
    printWriter.println("人数: " + shiftWork.getPeopleNum() + replace(isFormat58 ? 6 : 9));
    //总价
    printWriter.println("总价:"+NumberFormatUtils.formatFloatNumber(shiftWork.getTotalPrice()));
    //应收金额
    printWriter.println(PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(shiftWork.getAcceptAmount()));
    //优惠金额
    printWriter.println(PrinterConstant.DISCOUNT_AMOUNT + NumberFormatUtils.formatFloatNumber(shiftWork.getDiscountAmount()));
    //支付类优惠
    printWriter.println(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(shiftWork.getPayPrivilege()));
    //损益金额
    printWriter.println(PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(shiftWork.getGainLoseAmount()));
    //实收收入
    printWriter.println("实际收入:" + NumberFormatUtils.formatFloatNumber(shiftWork.getActualAmount()));
    //不计入统计金额
    printWriter.println("不计入统计金额:" + NumberFormatUtils.formatFloatNumber(shiftWork.getStaticsExclusive()));
    printWriter.println(" ");
    //--分类统计
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    if (isFormat58) {
      printWriter.println("----------- 分类统计 ----------");
    } else {
      printWriter.println("------------------ 分类统计 -----------------");
    }
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
     printWriter.println("项目" + replace(isFormat58 ? 2:4) + "数量" + replace(isFormat58 ? 2:4) + "原  价"
              + replace(isFormat58 ? 4:6) + "优惠后金额");
     for (AppShiftCateInfo collect : shiftWork.getCategoryCollectList()) {
        String receivable = NumberFormatUtils.formatFloatNumber(collect.getReceivableAmount());
        String actualNum = NumberFormatUtils.formatFloatNumber(collect.getActualAmount());
        String result = replaceKitchen(collect.getCateName(),(isFormat58)? 4 : 6) +
            replaceKitchen(String.valueOf(collect.getCateNumber()),(isFormat58) ? 6 : 8) +
            replaceKitchen(receivable , (isFormat58) ? 11 : 13) + actualNum;
        printWriter.println(result);

      }
      String receivableTotal = NumberFormatUtils.formatFloatNumber(shiftWork.getCategoryCollectReceivableMoneyTotal());
      String actualTotal = NumberFormatUtils.formatFloatNumber(shiftWork.getCategoryCollectRealMoneyTotal());
      String result = replaceKitchen("总计",(isFormat58) ? 4 : 6) +
          replaceKitchen(String.valueOf(shiftWork.getCategoryCollectTotal()),(isFormat58) ? 6 : 8) +
          replaceKitchen(receivableTotal,isFormat58 ? 11 : 13) +
          actualTotal;
      printWriter.println(result);

    dividerLine(outputStream,printWriter,isFormat58);
    //交接班 尾
    shiftWorkFoot( outputStream, printWriter, sdf, isFormat58);
  }

  /**
   * 交接班
   */
  private void shiftWorkBody(ShiftWork shiftWork, OutputStream outputStream, PrintWriter printWriter, SimpleDateFormat sdf, boolean isFormat58, boolean isDaily)
      throws IOException {
    //--.收银汇总
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    if (isFormat58) {
      printWriter.println("--------- 收银汇总 --------");
    } else {
      printWriter.println("----------------- 收银汇总 ----------------");
    }
    // 收银员总计  银行卡 翼支付 会员卡 支付宝 现金
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    printWriter.println("项目" + getSpace("项目",(isFormat58 ? 18 : 22)) + "笔数" + getSpace("笔数",(isFormat58 ? 5 : 7)) + "金额");
    List<AppCashCollect> cashCollectList = shiftWork.getCashCollectList();
      for (AppCashCollect collect : cashCollectList) {
        String name = collect.getName();
        int num = collect.getNum();
        String money = NumberFormatUtils.formatFloatNumber(collect.getMoney());
        String numString = String.valueOf(num);
        printWriter.println(name + getSpace(name,(isFormat58 ? 19 : 23)) + numString + getSpace(numString,(isFormat58 ? 6 : 8)) + money);
      }
    //--1.消费统计
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    if (isFormat58) {
      printWriter.println("--------- 消费统计 --------");
    } else {
      printWriter.println("--------------- 消费统计 --------------");
    }
    //单数
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    printWriter.println("单数: " + shiftWork.getBillsCount());
    //人数
    printWriter.println("人数: " + shiftWork.getPeopleNum() + replace(isFormat58 ? 6 : 9));
    //总价
    printWriter.println("总价:"+NumberFormatUtils.formatFloatNumber(shiftWork.getTotalPrice()));
    //应收金额
    printWriter.println(PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(shiftWork.getAcceptAmount()));
    //优惠金额
    printWriter.println(PrinterConstant.DISCOUNT_AMOUNT + NumberFormatUtils.formatFloatNumber(shiftWork.getDiscountAmount()));
    //支付类优惠
    printWriter.println(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(shiftWork.getPayPrivilege()));
    //损益金额
    printWriter.println("损益金额:"+  NumberFormatUtils.formatFloatNumber(shiftWork.getGainLoseAmount()));
    //实收金额
    printWriter.println("实际收入:" + NumberFormatUtils.formatFloatNumber(shiftWork.getActualAmount()));
    //不计入统计金额
    printWriter.println("不计入统计金额:" + NumberFormatUtils.formatFloatNumber(shiftWork.getStaticsExclusive()));
    printWriter.println(" ");
    ////--2.收银统计
    //mOutputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    //if (isFormat58) {
    //  mPrintWriter.println("----------- 消费收银  ----------");
    //} else {
    //  mPrintWriter.println("----------------- 消费收银 ----------------");
    //}
    ////项目  单数   金额
    //mOutputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    //mPrintWriter.println(
    //    "项  目" + replace(isFormat58 ? 3 : 5) + "单数" + replace(isFormat58 ? 3 : 5) + "金额");
    ////消费收银 银行卡 翼支付 会员卡 支付宝 微信 + 现金
    //mPrintWriter.println("现  金" + replace(isFormat58 ? 6 : 8) + replace1(shiftWork.getCashCount(),
    //    isFormat58 ? 6 : 8) + NumberFormatUtils.formatFloatNumber(shiftWork.getCashMoney()));
    //mPrintWriter.println(
    //    "银行卡" + replace(isFormat58 ? 6 : 8) + replace1(shiftWork.getBankCardCount(),
    //        isFormat58 ? 6 : 8) + NumberFormatUtils.formatFloatNumber(shiftWork.getBankCardMoney()));
    //mPrintWriter.println(
    //    "会员卡" + replace(isFormat58 ? 6 : 8) + replace1(shiftWork.getVipCount(), isFormat58 ? 6 : 8)
    //        + NumberFormatUtils.formatFloatNumber(shiftWork.getVipMoney()));
    //mPrintWriter.println("翼支付" + replace(isFormat58 ? 6 : 8) + replace1(shiftWork.getWingPayCount(),
    //    isFormat58 ? 6 : 8) + NumberFormatUtils.formatFloatNumber(shiftWork.getWingPayMoney()));
    //mPrintWriter.println("支付宝" + replace(isFormat58 ? 6 : 8) + replace1(shiftWork.getAliPayCount(),
    //    isFormat58 ? 6 : 8) + NumberFormatUtils.formatFloatNumber(shiftWork.getAliPayMoney()));
    //mPrintWriter.println("微  信" + replace(isFormat58 ? 6 : 8) + replace1(shiftWork.getWeiXinCount(),
    //    isFormat58 ? 6 : 8) + NumberFormatUtils.formatFloatNumber(shiftWork.getWeixinMoney()));
    ////小计
    //mPrintWriter.println(
    //    "小 计:" + replace(isFormat58 ? 6 : 8) + replace1(shiftWork.getSubTotalCount(),
    //        isFormat58 ? 6 : 8) + NumberFormatUtils.formatFloatNumber(shiftWork.getSubTotalMoney()));
    ////3.会员充值情况
    //mOutputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    //if (isFormat58) {
    //  mPrintWriter.println("--------- 会员充值情况 --------");
    //} else {
    //  mPrintWriter.println("--------------- 会员充值情况 --------------");
    //}
    ////充值人数
    //mOutputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    //mPrintWriter.println(PrinterConstant.RECHARGE_PEOPLE_NUM + shiftWork.getRechargePeopleNum());
    ////充值金额
    //mPrintWriter.println(PrinterConstant.RECHARGE_MONEY + NumberFormatUtils.formatFloatNumber(shiftWork.getRechargeMoney()));
    ////赠送金额
    //mPrintWriter.println(PrinterConstant.PRESENT_MONEY + NumberFormatUtils.formatFloatNumber(shiftWork.getPresentMoney()));
    ////实收款数
    //mPrintWriter.println(PrinterConstant.ACTUAL_MONEY + NumberFormatUtils.formatFloatNumber(shiftWork.getActualMoney()));

    if (isFormat58) {
      printWriter.println("------------------------------");
    } else {
      printWriter.println("------------------------------------------");
    }
    //交接班 尾
    shiftWorkFoot( outputStream, printWriter, sdf, isFormat58);
  }

  /**
   * 收银员交接班 所有账单
   */
  private void printShiftWorkAllBills(ShiftWork shiftWork, List<PxOrderInfo> billList, boolean isFormat58,
      OutputStream outputStream,PrintWriter printWriter) throws IOException{
    if (billList == null || billList.size() == 0) return ;
      //收银员交接班 所有账单
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(true, true, false));
      printWriter.println(PrinterConstant.SHIFT_WORK_ALL_BILLS);
      printWriter.println(" ");
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      //head
      shiftWorkHead(shiftWork, outputStream, printWriter, sdf);
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      if (isFormat58) {
        printWriter.println("----------------------------");//14
      } else {
        printWriter.println("----------------------------------");//14
      }
      Double allRealPrice = 0.0;
      //账单详情
      for (PxOrderInfo orderInfo : billList) {
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
        printWriter.println("账单号:" + orderInfo.getOrderNo());
        String tableName = null;
        if (orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)){
          tableName =  DaoServiceUtil.getTableOrderRelService().queryBuilder().where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId())).unique().getDbTable().getName();
        } else {
          tableName = "零售单";
        }
        printWriter.println("桌  台:" + tableName);
        printWriter.println(
            "应收金额: " + orderInfo.getAccountReceivable() + replace(isFormat58 ? 2 : 4) + "优惠金额: "
                + orderInfo.getDiscountPrice());
        printWriter.println(
            "实收金额: " + orderInfo.getRealPrice() + replace(isFormat58 ? 2 : 4) + "抹零金额: " + orderInfo
                .getTailMoney());
        double totalMoney = orderInfo.getAccountReceivable() + orderInfo.getDiscountPrice();
        printWriter.println("总 金 额:" + totalMoney + replace(isFormat58 ? 2 : 4) + "找零金额:"
            + orderInfo.getTotalChange());
        allRealPrice += orderInfo.getRealPrice();
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
        if (isFormat58) {
          printWriter.println("----------------------------");
        } else {
          printWriter.println("----------------------------------");
        }
      }
      //
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      //账单数 实收金额
      printWriter.println("账 单 数:  " + billList.size());
      printWriter.println("实收金额: " + allRealPrice);
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      if (isFormat58) {
        printWriter.println("----------------------------");//14
      } else {
        printWriter.println("----------------------------------");//14
      }
      shiftWorkFoot(outputStream, printWriter, sdf, isFormat58);
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
    if (sb.toString().length() == 0){
      sb.append(" ");
    }
    return sb.toString();
  }
  /**
   *补齐空格
   */
  private String getSpace(String before, int sum) throws UnsupportedEncodingException {
    int length = sum - before.getBytes("GBK").length;
    StringBuilder sb = new StringBuilder();
    while (sb.toString().getBytes().length < length) {
      sb.append(" ");
    }
    if (TextUtils.isEmpty(sb)){
      sb.append(" ");
    }
    return sb.toString();
  }
  /**
   * 补齐后厨打印
   */
  private String replaceKitchen(String before,int num) {
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

  /**
   * 日结单据
   */
  private void printDailyStatement( ShiftWork shiftWork, boolean isFormat58,OutputStream outputStream, PrintWriter printWriter) throws IOException{
      //收银员交接班 所有账单
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(true, true, false));
      String title = shiftWork.getTitle();
      if (title != null) {
        printWriter.println(title);
      } else {
        printWriter.println(PrinterConstant.SHIFT_DAY_REPORT_COLLECT);
      }
      printWriter.println(" ");
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      //店铺名称
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      printWriter.println("店铺名称: " + office.getName());
      //营业日期
      printWriter.println("营业日期:" + shiftWork.getBusinessData());
      //收银员
      printWriter.println("收 银 员:" + shiftWork.getCashierName());
      //交接时间
      printWriter.println(PrinterConstant.SHIFT_TIME + sdf.format(shiftWork.getShitTime()));
      //账单时间段
      Date startTime = shiftWork.getStartTime();
      Date endTime = shiftWork.getEndTime();
      if (startTime != null && endTime != null) {
        printWriter.println(PrinterConstant.SHIFT_TIME_ZONE + sdf.format(startTime));
        printWriter.println("           ~" + sdf.format(endTime));
      }
      //区域
      printWriter.println(PrinterConstant.WORK_ZONE + shiftWork.getWorkZone());
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      //body
      shiftWorkBodyDaily(shiftWork, outputStream, printWriter, sdf, isFormat58);
  }

  /**
   * 交接班 尾
   */
  private void shiftWorkFoot(OutputStream outputStream, PrintWriter printWriter, SimpleDateFormat sdf, boolean isFormat58)
      throws IOException {
    //9.签字
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    outputStream.write(EpsonPosPrinterCommand.setPrintMode(true, false, false, false, false));
    printWriter.println("交班用户签字:");
    printWriter.println(" ");
    printWriter.println("当班经理签字:");
    outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
    //10.尾
    printWriter.println(" ");
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
   dividerLine(outputStream,printWriter,isFormat58);
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    flush(printWriter,outputStream);
  }

  private void flush(PrintWriter printWriter,OutputStream outputStream) throws IOException{
    printWriter.println("打印时间:" + sdf.format(new Date()));
    printWriter.println(" ");
    printWriter.println(" ");
    printWriter.println(" ");
    printWriter.println(" ");
    printWriter.println(" ");
    //切纸
    outputStream.write(EpsonPosPrinterCommand.ESC_CUT_PAPER);
    outputStream.flush();
    printWriter.flush();
  }

  /**
   * 交接班 通用头部
   */
  private void shiftWorkHead(ShiftWork shiftWork, OutputStream outputStream,
      PrintWriter printWriter, SimpleDateFormat sdf) throws IOException {
    //店铺名称
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
    printWriter.println("店铺名称: " + office.getName());
    //交班用户
    printWriter.println("交接用户: " + shiftWork.getShiftUserName());
    //收银员
    printWriter.println("收 银 员: " + shiftWork.getCashierName());
    //交接时间
    printWriter.println(PrinterConstant.SHIFT_TIME + sdf.format(shiftWork.getShitTime()));
    //账单时间段
    Date startTime = shiftWork.getStartTime();
    Date endTime = shiftWork.getEndTime();
    if (startTime != null && endTime != null) {
      printWriter.println(PrinterConstant.SHIFT_TIME_ZONE + sdf.format(startTime));
      printWriter.println("           ~" + sdf.format(endTime));
    }
    //区域
    printWriter.println(PrinterConstant.WORK_ZONE + shiftWork.getWorkZone());
  }
  //@formatter:off
  /**
   * 收银 账单明细 财务联
   */
  private void printBillDetailFinance(List<PxOrderDetails> detailsList,boolean isFormat58,
      OutputStream outputStream,PrintWriter printWriter) throws IOException {
    if (detailsList == null || detailsList.size()== 0) return ;
      PxOrderInfo orderInfo = detailsList.get(0).getDbOrder();
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      //打印通用头部
      initHead(outputStream, printWriter);
      //账单明细（客户联）
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      printWriter.println(PrinterConstant.ACCOUNT_DETAIL_WITH_FINANCING);
      printWriter.println(" ");
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false,false,false));
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo, printWriter,outputStream);
      //账单号
      getBillId(orderInfo, printWriter);
      //人数
      getPeopleNum(orderInfo, printWriter);
      //收银员和单位
      getWaiterAndUnit(orderInfo, printWriter);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //品名(金额)和数量/单位
      getProductProperty(printWriter,isFormat58);
      printWriter.println(" ");
      //商品真实实体数据 (账单明细客户联）
      //getProductData(detailsList,printWriter,isFormat58,false);
      List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
      printMergeDetails(orderInfo,mergeDetailsList,printWriter,isFormat58,false);
      //--分类统计
      categoryCollect(orderInfo,outputStream,printWriter,isFormat58);
      //合计项
      getSumNumber(detailsList, outputStream, printWriter);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //仅供账单详情应收,实收,找零,抹零
      getRecMoney(orderInfo,outputStream, printWriter,isFormat58);
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false,false,false,false,false));
      //init foot
      initFoot(outputStream, printWriter,isFormat58);
  }

  /**
   * 收银  账单明细 客户联
   */
  private void printBillDetailCustomer(List<PxOrderDetails> detailsList,boolean isFormat58,
      OutputStream outputStream,PrintWriter printWriter) throws IOException{
    if (detailsList == null || detailsList.size() == 0) return ;
     PxOrderInfo orderInfo = detailsList.get(0).getDbOrder();
      //打印通用头部
      initHead(outputStream, printWriter);
      //账单明细（客户联）
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      printWriter.println(PrinterConstant.ACCOUNT_DETAIL_WITH_CUSTOMER);
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false,false,false));
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo, printWriter,outputStream);
      //账单号
      getBillId(orderInfo, printWriter);
      //人数
      getPeopleNum(orderInfo, printWriter);
      //收银员和单位
      getWaiterAndUnit(orderInfo, printWriter);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //品名(金额)和数量/单位
      getProductProperty(printWriter,isFormat58);
      printWriter.println(" ");
      //商品真实实体数据 (账单明细客户联）
      //getProductData(detailsList,printWriter,isFormat58,false);
      List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
      printMergeDetails(orderInfo,mergeDetailsList,printWriter,isFormat58,false);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //合计项
      getSumNumber(detailsList, outputStream, printWriter);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //仅供账单详情应收,实收,找零,抹零
      getRecMoney(orderInfo,outputStream, printWriter,isFormat58);
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false,false,false));
      //init foot
      initFoot(outputStream, printWriter,isFormat58);
  }

  /**
   * 收银 结账单财务联
   */
  private void printFinaceInfo(PxOrderInfo orderInfo, List<PxOrderDetails> detailsList, AppFinanceAmount mFinanceAmount,
     boolean isFormat58, OutputStream outputStream, PrintWriter printWriter) throws IOException {
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      //打印通用头部
      initHead( outputStream, printWriter);
      //结账单（财务联）
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      printWriter.println(PrinterConstant.STATE_MENU_FINANCE);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo, printWriter,outputStream);
      //账单号
      getBillId(orderInfo, printWriter);
      //人数
      //getPeopleNum(orderInfo, mPrintWriter);
      //服务员和单位
      getWaiterAndUnit(orderInfo, printWriter);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //品名(金额)和数量/单位
      //getProductProperty(mPrintWriter,isFormat58);
      getProductProperty(printWriter,isFormat58);
      printWriter.println(" ");
      //商品真实实体数据
       outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false,false,false,false,false));
       //getProductData(detailsList, printWriter,isFormat58,false);
      List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
      printMergeDetails(orderInfo,mergeDetailsList,printWriter,isFormat58,false);
       outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false,false,false,false,false));
      //--分类统计
      categoryCollect(orderInfo,outputStream,printWriter,isFormat58);
      //应收,实收,找零
      getBillWithFinanceInfoAmount(orderInfo, mFinanceAmount, outputStream, printWriter,isFormat58);
      initFoot(outputStream, printWriter,isFormat58);
      //sound(mOutputStream);
  }
  /**
   * 收银 打印结账单客户联
   */
  private void printCustomersAl(PxOrderInfo orderInfo, List<PxOrderDetails> detailsList, AppCustomAmount customAmount,
      boolean isFormat58, OutputStream outputStream, PrintWriter printWriter) throws IOException{
      initHead( outputStream, printWriter);
      //结账单（客户联）
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      printWriter.println(PrinterConstant.STATE_MENU_CUSTOM);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo, printWriter,outputStream);
      //账单号
      getBillId(orderInfo, printWriter);
      //人数
      //getPeopleNum(orderInfo, mPrintWriter);
      //服务员和单位
      getWaiterAndUnit(orderInfo, printWriter);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //品名(金额)和数量/单位
      getProductProperty(printWriter,isFormat58);
      printWriter.println(" ");
      //商品真实实体数据
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
      //getProductData(detailsList, printWriter,isFormat58,false);
      List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
      printMergeDetails(orderInfo,mergeDetailsList,printWriter,isFormat58,false);
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //总金额,折后,优惠等等
      getBillWithCustomInfoAmount(customAmount, outputStream, printWriter,orderInfo,isFormat58);
      initFoot(outputStream, printWriter,isFormat58);
  }

  /**
   * 收银 打印销售统计
   */
  private void printSaleCount(PxProductCategory mCategory, List<AppSaleContent> saleContentList,
     boolean isFormat58,OutputStream outputStream,PrintWriter printWriter) throws IOException{
      //打印通用头部
      initHead( outputStream, printWriter);
      //菜类点菜统计
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      printWriter.println(PrinterConstant.SALE_ACCOUNT_MENU);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //统计时间
      String time = PrinterConstant.DISH_STATISTICS_TIME + sdf.format(new Date());
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false,false,false));
      printWriter.println(time);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //分类名
      printWriter.println(PrinterConstant.DISH_CATEGORY + mCategory.getName());
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //品名(金额)和数量/单位
      //mPrintWriter.println(replaceKitchen("品名",(isFormat58 ? 11 : 16))+"数量");getSpace(before,(isFormat58 ? 26 : 38))
      printWriter.println("品名" + getSpace("品名",(isFormat58 ? 25 : 38)) +"数量" );
      //销量实体
      for (AppSaleContent appSaleContent : saleContentList) {
        //mPrintWriter.println(replaceKitchen(appSaleContent.getProdName()+" ",(isFormat58 ? 11 : 16)) +appSaleContent.getSaleNumber());
        String prodName = appSaleContent.getProdName();
        String text = null;
        if (appSaleContent.isMultUnitProd()){
          text =prodName + getSpace(prodName , (isFormat58 ? 26 : 39)) + appSaleContent.getSaleMultNumber()+appSaleContent.getUnit();
        }else{
          text = prodName + getSpace(prodName , (isFormat58 ? 26 : 39)) + appSaleContent.getSaleNumber();
        }
        printWriter.println(text);
      }
      //foot
      initFoot(outputStream, printWriter,isFormat58);
  }

  /**
   * 收银 打印账单汇总
   */
  //@formatter:on
  private void printBillSummary(CollectionContentEvent contentEvent, AppBillCount appBillCount,
      boolean isFormat58, OutputStream outputStream, PrintWriter printWriter) throws IOException {
    //打印通用头部
    initHead(outputStream, printWriter);
    //收银员账单汇总
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
    printWriter.println(PrinterConstant.CASHIER_COLLECT);
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    //分割线
    dividerLine(outputStream, printWriter, isFormat58);
    //统计时间
    String time = PrinterConstant.DISH_STATISTICS_TIME + sdf.format(new Date());
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
    printWriter.println(time);
    //分割线
    dividerLine(outputStream, printWriter, isFormat58);
    //收银员
    printWriter.println(PrinterConstant.CASHIER + contentEvent.getUser().getName());
    //分割线
    dividerLine(outputStream, printWriter, isFormat58);
    //应收
    printWriter.println("应收:" + appBillCount.getTotalReceivable());
    //找零
    printWriter.println("找零:" + appBillCount.getTotalChange());
    //抹零
    printWriter.println("抹零:" + appBillCount.getTotalTail());
    //支付类优惠
    printWriter.println("支付类优惠:" + appBillCount.getPayPrivilege());
    //各项实收
    printWriter.println("--------实收详情--------");
    List<Pair<String, String>> everyReceived = appBillCount.getEveryReceived();
    for (Pair<String, String> pair : everyReceived) {
      printWriter.println(pair.first + ":" + pair.second);
    }
    //foot
    initFoot(outputStream, printWriter, isFormat58);
  }

  /**
   * 收银 打印点菜单
   */
  //@formatter:off
  private void printDetailsCollection(PxOrderInfo orderInfo, List<PxOrderDetails> detailsList, boolean isFormat58,OutputStream outputStream,
      PrintWriter printWriter) throws IOException{
      //打印通用头部
      initHead(outputStream, printWriter);
      //点菜单字样
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      printWriter.println(PrinterConstant.POINT_MENU);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //桌号和单号
      getTableIdAndUniqueIdWithLarge(orderInfo, printWriter,outputStream);
      //账单号
      getBillId(orderInfo, printWriter);
      //人数
      //getPeopleNum(orderInfo, mPrintWriter);
      //服务员和单位
      getWaiterAndUnit(orderInfo, printWriter);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //品名(金额)和数量/单位
      getProductProperty(printWriter,isFormat58);
      printWriter.println(" ");
      //商品真实实体数据
      //getProductData(detailsList, printWriter,isFormat58,true);
      List<PxOrderDetails> mergeDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
      printMergeDetails(orderInfo,mergeDetailsList,printWriter,isFormat58,true);
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //总价
      getTotalPrice(outputStream, printWriter,detailsList);
     initFoot(outputStream,printWriter,isFormat58);
  }

  /**
   * KitchenPrintActivity 打印
   */
  private void printKitchenOrderDetails(boolean isOncePrint, List<PrintDetails> detailsList,
     int printNum, String planName, boolean isFormat58,OutputStream outputStream ,PrintWriter printWriter) throws IOException {
    if (detailsList == null || detailsList.size() == 0) return ;
    //订单状态(0:未结账 1：结账 2:撤单)
    boolean isRefundBill =PxOrderInfo.STATUS_CANCEL.equals(detailsList.get(0).getDbOrder().getStatus());
      if (detailsList == null || detailsList.size() == 0) return ;
      PrintDetails details = detailsList.get(0);
      //退菜、撤单的 默认不一菜一切 打印一份
      if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
        noOncePrintSmall(detailsList,isRefundBill, 1, outputStream, printWriter, planName, isFormat58);
        sound(outputStream);
        return ;
      }
      noOncePrintSmall(detailsList,  isRefundBill, printNum, outputStream, printWriter, planName, isFormat58);
      sound(outputStream);
  }

  //@formatter:off
  private void printCollectAndRel(List<PrintDetailsCollect> collectList, PxPrinterInfo printerInfo,
      boolean isFormat58, OutputStream outputStream, PrintWriter printWriter) throws IOException{
    if (collectList == null || collectList.size() == 0) return ;
      for (PrintDetailsCollect collect : collectList) {
        List<PdConfigRel> relList = collect.getDbPdConfigRelList();
        if (relList == null || relList.size() == 0) {
          continue;
        }
        PxProductConfigPlan dbConfig = collect.getDbConfig();
        if (dbConfig == null) continue;
        PxPrinterInfo dbPrinter = dbConfig.getDbPrinter();
        //打印机不匹配该配菜方案  、未启用 、非厨房
        if (dbPrinter == null || !dbPrinter.getObjectId().equals(printerInfo.getObjectId()) ||
            PxPrinterInfo.DIS_ENABLE.equals(dbPrinter.getStatus())) {
          continue;
        }
        //份数 、 是否一菜一切 、 配菜方案名称
        int printNum = dbConfig.getCount();
        //1是 0 否
        boolean isOncePrint = dbConfig.getFlag().equals(ONCE_PRINT) ? true : false;
        String name = dbConfig.getName();
        for (int i = 0; i < printNum; i++) {
          if (isOncePrint) {
            List<PdConfigRel> list = new ArrayList<>();
            for (PdConfigRel rel : collect.getDbPdConfigRelList()) {
              if (!list.isEmpty()){
                list.clear();
              }
              list.add(rel);
              boolean result = printCollectSmall(outputStream, printWriter, name, list, isFormat58);
              if (result) {
                savePrintDetailsAndConfigRel(rel);
              }
            }
          } else {
            boolean result = printCollectSmall(outputStream, printWriter, name, collect.getDbPdConfigRelList(), isFormat58);
            if (result) {
              savePrintCollect(collect);
            }
          }
        }
      }
      sound(outputStream);
  }

  /**
   * 打印 退菜 或撤单的  默认一份 不一菜一切
   */
  private void printCollectAndRelRefund(List<PrintDetailsCollect> collectList, PxPrinterInfo printerInfo,
      boolean isFormat58, OutputStream outputStream, PrintWriter printWriter) throws IOException{

    if (collectList == null || collectList.size() == 0) return ;
      for (PrintDetailsCollect collect : collectList) {
        List<PdConfigRel> relList = collect.getDbPdConfigRelList();
        if (relList == null || relList.size() == 0) {
          continue;
        }
        PxProductConfigPlan dbConfig = collect.getDbConfig();
        if (dbConfig == null) continue;
        PxPrinterInfo dbPrinter = dbConfig.getDbPrinter();
        //打印机不匹配该配菜方案  、未启用 、非厨房
        if (dbPrinter == null || !dbPrinter.getObjectId().equals(printerInfo.getObjectId()) ||
            dbPrinter.getStatus().equals(PxPrinterInfo.DIS_ENABLE) ) {
          continue;
        }
        String name = dbConfig.getName();
        List<PrintDetails> detailsList = new ArrayList<>();
        for (PdConfigRel rel : relList) {
          detailsList.add(rel.getDbPrintDetails());
        }
        noOncePrintSmall(detailsList, true, 1, outputStream, printWriter, name, isFormat58);
        //保存collect
        savePrintCollect(collect);
        savePrintRel(relList);
      }
      sound(outputStream);
  }

  /**
   * 打印small   collect
   */
  //@formatter:on
  private boolean printCollectSmall(OutputStream outputStream, PrintWriter printWriter,
      String planName, List<PdConfigRel> relList, boolean isFormat58) {
    try {
      //配菜方案名称
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, true, true, false));
      if (isFormat58) {
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
        printWriter.println((planName == null) ? "厨房打印" : planName);
      } else {
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
        printWriter.println("        " + ((planName == null) ? "厨房打印" : planName));
      }
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      PxOrderInfo orderInfo = relList.get(0).getDbPrintDetails().getDbOrder();
      boolean retailOrderInfo =
          orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_RETAIL);//是零售单
      //单号桌号
      String billAndTable =
          PrinterConstant.ODD_NUMBER + Integer.valueOf(orderInfo.getOrderNo().substring(24, 30));
      if (!retailOrderInfo) {//不零售单
        TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
            .unique();
        PxTableInfo dbTable = unique.getDbTable();
        billAndTable = billAndTable + "  桌  号:" + dbTable.getName();
      }
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, true, false));
      //放大单号
      printWriter.println(billAndTable);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
      if (!retailOrderInfo) {//不零售单
        printWriter.println("人  数:" + orderInfo.getActualPeopleNumber());
      }
      //张单号
      printWriter.println("账单号:" + orderInfo.getOrderNo().substring(10, 30));
      //分割线
      dividerLine(outputStream, printWriter, isFormat58);
      for (PdConfigRel rel : relList) {
        PrintDetails printDetails = rel.getDbPrintDetails();
        String formatName = printDetails.getTransformFormatName();
        String methodName = printDetails.getTransformMethodName();
        PxProductInfo dbProduct = printDetails.getDbProduct();
        String prodName = dbProduct.getName();
        boolean twoUnit = PxProductInfo.IS_TWO_UNIT_TURE.equals(dbProduct.getMultipleUnit());
        String unit = dbProduct.getUnit();
        String orderUnit = dbProduct.getOrderUnit();

        //是双单位(品名)
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
        outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
        outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, true, true, false));
        //判断是否是延迟
        String delay = "";
        if (printDetails.getStatus().equals(PxOrderDetails.STATUS_DELAY)) {
          delay = "(待)";
        }
        //是否是套餐商品
        String isComboDetails = "";
        if (PrintDetails.IN_COMBO_TRUE.equals(printDetails.getInCombo())) {
          isComboDetails = "(套餐)";
        }
        String before = prodName + isComboDetails + formatName + methodName + delay;

        if (twoUnit) {
          String unitAndNum =
              NumberFormatUtils.formatFloatNumber(printDetails.getMultipleUnitNumber()) + unit + "/"
                  + printDetails.getNum().intValue() + orderUnit;
          printWriter.println(replaceKitchen(before, (isFormat58) ? 8 : 15) + unitAndNum);
        } else { //非双单位(品名)
          printWriter.println(
              replaceKitchen(before, (isFormat58) ? 12 : 18) + printDetails.getNum().intValue()
                  + unit);
        }
        //备注
        String remarks = printDetails.getRemarks();
        if (remarks != null && !TextUtils.isEmpty(remarks.trim())) {
          printWriter.println("--备注:" + remarks);
        }
      }
      //整单备注
      String orderInfoRemarks = orderInfo.getRemarks();
      if (orderInfoRemarks != null && !TextUtils.isEmpty(orderInfoRemarks.trim())) {
        //分割线
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
        outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
        outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
        dividerLine(outputStream, printWriter, isFormat58);
        outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
        outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, true, true, false));
        printWriter.println("整单备注:" + orderInfoRemarks);
      }
      //分割线
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
      dividerLine(outputStream, printWriter, isFormat58);
      //打印时间字样和值
      flush(printWriter, outputStream);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * 保存 Collect 为已打印
   */
  private void savePrintCollect(PrintDetailsCollect collect) {
    PrintDetailsCollect exist = DaoServiceUtil.getPdCollectService()
        .queryBuilder()
        .where(PrintDetailsCollectDao.Properties.Id.eq(collect.getId()))
        .unique();
    if (exist == null) return;
    Boolean isPrint = exist.getIsPrint();
    if (!isPrint) {
      SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
      db.beginTransaction();
      try {
        exist.setIsPrint(true);
        DaoServiceUtil.getPdCollectService().update(exist);
        db.setTransactionSuccessful();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        db.endTransaction();
      }
    }
  }

  /**
   * 保存 Rel 已打印
   */
  private void savePrintRel(List<PdConfigRel> list) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      for (PdConfigRel rel : list) {
        PdConfigRel exist = DaoServiceUtil.getPdConfigRelService()
            .queryBuilder()
            .where(PdConfigRelDao.Properties.Id.eq(rel.getId()))
            .unique();
        if (exist != null && !exist.getIsPrinted()) {
          exist.setIsPrinted(true);
        }
      }
      DaoServiceUtil.getPdConfigRelService().update(list);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 保存rel 为已打印
   */
  private void savePrintDetailsAndConfigRel(PdConfigRel rel) {
    PdConfigRel exist = DaoServiceUtil.getPdConfigRelService()
        .queryBuilder()
        .where(PdConfigRelDao.Properties.Id.eq(rel.getId()))
        .unique();
    if (exist == null) return;
    if (!exist.getIsPrinted()) {
      exist.setIsPrinted(true);
      DaoServiceUtil.getPdConfigRelService().update(exist);
    }
  }

  /**
   * 后厨打印 OrderDetails  不是一菜一切
   */
  private void noOncePrintSmall(List<PrintDetails> detailsList, boolean isRefundBill, int printNum,
      OutputStream outputStream, PrintWriter printWriter, String planName, boolean isFormat58)
      throws IOException {
    PxOrderInfo orderInfo = detailsList.get(0).getDbOrder();
    //refresh
    DaoServiceUtil.getOrderInfoService().refresh(orderInfo);
    boolean retailOrderInfo =
        orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_RETAIL);//是零售单
    //是否退菜
    boolean isRefund =
        detailsList.get(0).getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND);
    for (int i = 0; i < printNum; i++) {//打印份数
      //配菜方案名称
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, true, true, false));
      String configName = (planName == null) ? "厨房打印" : planName;
      String finalName = (isRefund) ? configName + "(退)" : configName;
      if (isFormat58) {
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
        printWriter.println(finalName);
      } else {
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
        printWriter.println("        " + finalName);
      }

      //单号桌号
      String billAndTable =
          PrinterConstant.ODD_NUMBER + Integer.valueOf(orderInfo.getOrderNo().substring(24, 30));
      if (!retailOrderInfo) {//零售单
        TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
            .unique();
        PxTableInfo dbTable = unique.getDbTable();
        billAndTable = billAndTable + "  桌  号:" + dbTable.getName();
      }
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, true, false));
      //放大单号
      printWriter.println(billAndTable);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
      if (!retailOrderInfo) {//零售单
        printWriter.println("人  数:" + orderInfo.getActualPeopleNumber());
      }
      //单号
      printWriter.println("账单号:" + orderInfo.getOrderNo().substring(10, 30));

      //分割线
      if (isFormat58) {
        printWriter.println("--------------------------------");
      } else {
        printWriter.println("--------------------------------------");
      }
      //变换格式
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, true, true, false));
      for (PrintDetails details : detailsList) {
        printDetails(outputStream, printWriter, details, isFormat58);
      }
      //if (detailsList.size() == 1 && !isRefundBill ) {
      //  String remarks = detailsList.get(0).getRemarks();
      //  if (remarks != null && !TextUtils.isEmpty(remarks)){
      //      printWriter.println("--备注:" + remarks);
      //  }
      //}
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
      //退菜原因
      if (!TextUtils.isEmpty(detailsList.get(0).getTransformReasonName())) {
        //分割线
        if (isFormat58) {
          printWriter.println("--------------------------------");
        } else {
          printWriter.println("--------------------------------------");
        }
        printWriter.println("退菜原因:" + detailsList.get(0).getTransformReasonName());
      }
      //整单备注
      String orderInfoRemarks = orderInfo.getRemarks();
      if (!isRefund && orderInfoRemarks != null && !TextUtils.isEmpty(orderInfoRemarks.trim())) {
        //分割线
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
        outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
        outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
        if (isFormat58) {
          printWriter.println("--------------------------------");
        } else {
          printWriter.println("-----------------------------------------");
        }
        outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
        outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
        outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, true, true, false));
        printWriter.println("整单备注:" + orderInfoRemarks);
      }

      //分割线
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
      if (isFormat58) {
        printWriter.println("--------------------------------");
      } else {
        printWriter.println("--------------------------------------");
      }
      //打印时间字样和值
      flush(printWriter, outputStream);
    }
  }
  //@formatter:on

  /**
   * 打印指定详情
   */
  private void printDetails(OutputStream outputStream, PrintWriter printWriter,
      PrintDetails printDetails, boolean isFormat58) throws IOException {
    String formatName = printDetails.getTransformFormatName();
    String methodName = printDetails.getTransformMethodName();
    PxProductInfo dbProduct = printDetails.getDbProduct();
    String prodName = dbProduct.getName();
    String unit = dbProduct.getUnit();
    String orderUnit = dbProduct.getOrderUnit();
    boolean twoUnit = PxProductInfo.IS_TWO_UNIT_TURE.equals(dbProduct.getMultipleUnit());
    //是双单位(品名)
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
    outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, true, true, false));
    //判断是否是延迟
    String delay = "";
    if (PxOrderDetails.STATUS_DELAY.equals(printDetails.getStatus())) {
      delay = "(待)";
    }
    //是否是套餐商品
    String isComboDetails = "";
    if (PxOrderDetails.IN_COMBO_TRUE.equals(printDetails.getInCombo())) {
      isComboDetails = "(套餐)";
    }
    String before = prodName + isComboDetails + formatName + methodName + delay;
    if (twoUnit) {
      String unitAndNum =
          NumberFormatUtils.formatFloatNumber(printDetails.getMultipleUnitNumber()) + unit + "/"
              + printDetails.getNum().intValue() + orderUnit;
      printWriter.println(replaceKitchen(before, (isFormat58) ? 8 : 15) + unitAndNum);
    } else { //非双单位(品名)
      printWriter.println(
          replaceKitchen(before, (isFormat58) ? 12 : 18) + printDetails.getNum().intValue() + unit);
    }
    //备注
    String remarks = printDetails.getRemarks();
    if (remarks != null && !TextUtils.isEmpty(remarks.trim())) {
      printWriter.println("--备注:" + remarks);
    }
  }

  //@formatter:off
  // 厨房打印 打印移并桌信息
  private void printTableAlert1(PxTableAlteration tableAlteration,boolean isFormat58,OutputStream outputStream , PrintWriter printWriter) throws IOException{
      PxOrderInfo orderInfo = tableAlteration.getDbOrder();
      //厨房打印字样
      outputStream.write(EpsonPosPrinterCommand.setLineSpacing(20));
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      boolean typeMerge = tableAlteration.getType().equals(PxTableAlteration.TYPE_MERGE);
      printWriter.println(typeMerge ? PrinterConstant.TABLE_MERGE : PrinterConstant.TABLE_MOVE);
       outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      dividerLine(outputStream,printWriter,isFormat58);
      //单号
       outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      printWriter.println(PrinterConstant.ODD_NUMBER + orderInfo.getOrderNo().substring(10, 30));
      //分割线
       dividerLine(outputStream,printWriter,isFormat58);
      //移并桌信息
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      String msg = tableAlteration.getDbOriginalTable().getName() + (typeMerge ? "并" : "移") + "桌到"
          + tableAlteration.getDbTargetTable().getName();
      printWriter.println(msg);
      dividerLine(outputStream,printWriter,isFormat58);
       //打印时间字样和值
      flush(printWriter,outputStream);
      sound(outputStream);
      saveTable(tableAlteration);
  }
  /**
  *保存移并桌信息 tableAlteration
   */
  private void saveTable(PxTableAlteration tableAlteration) {
  SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try{
      tableAlteration.setIsPrinted(true);
      DaoServiceUtil.getTableAlterationService().update(tableAlteration);
      db.setTransactionSuccessful();
    }catch (Exception e){
       e.printStackTrace();
    }finally{
      db.endTransaction();
    }}

  /**
   * 检测打印给打印给后厨的测试页(是否成功打印)
   */
  private void printerTestInfo(boolean isFormat58,OutputStream outputStream , PrintWriter printWriter) throws IOException {
      //店铺名称
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      printWriter.println(office.getName() + "测试页");
      printWriter.println(" ");
      //厨房打印字样
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      printWriter.println(office.getName() + PrinterConstant.KITCHEN_MENU);
      printWriter.println(" ");
      //分割线
      dividerLine(outputStream, printWriter,isFormat58);
      //打印时间字样和值
     flush(printWriter,outputStream);
  }

  //打印头部分
  private void initHead(OutputStream outputStream, PrintWriter printWriter)
      throws IOException {
    outputStream.write(0x1B);
    outputStream.write(0x33);
    outputStream.write(0x00);
     //店铺名称
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(true, true, false));
    outputStream.write(EpsonPosPrinterCommand.setPrintMode(false,true,true,true,false));
    printWriter.println(office.getName());
    outputStream.write(EpsonPosPrinterCommand.setPrintMode(false,false,false,false,false));
  }


  //打印结尾部分
  private void initFoot(OutputStream outputStream, PrintWriter printWriter, boolean isFormat58)
      throws IOException {
    //分割线
    dividerLine(outputStream, printWriter, isFormat58);
    //打印时间字样和值
    flush(printWriter,outputStream);
  }

  //分割线
  private void dividerLine(OutputStream outputStream, PrintWriter printWriter, boolean isFormat58)
      throws IOException {
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
    if (isFormat58) {
      printWriter.println("--------------------------------");
    } else {
      printWriter.println("-----------------------------------------------");
    }
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
  }

  /**
   * 桌号和单号(仅供放大)
   */
  private void getTableIdAndUniqueIdWithLarge(PxOrderInfo orderInfo, PrintWriter printWriter,OutputStream outputStream) throws IOException {
     boolean retailOrderInfo =
          orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_RETAIL);//是零售单
    //单号桌号
      String billAndTable =PrinterConstant.ODD_NUMBER+Integer.valueOf(orderInfo.getOrderNo().substring(24,30));
      if (!retailOrderInfo) {//零售单
        TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .unique();
      PxTableInfo dbTable = unique.getDbTable();
        billAndTable = billAndTable + "  桌  号:" + dbTable.getName();
      }
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, true, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, true, false));
      //放大单号
      printWriter.println(billAndTable);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
    if (!retailOrderInfo) {//零售单
         printWriter.println("人  数:" + orderInfo.getActualPeopleNumber());
      }

  }

  /**
   * 账单号
   */
  private void getBillId(PxOrderInfo orderInfo, PrintWriter printWriter) {
    String billId = PrinterConstant.BILL_NO + orderInfo.getOrderNo().substring(10, 30);
    printWriter.println(billId);
  }

  /**
   * 人数
   */
  private void getPeopleNum(PxOrderInfo orderInfo, PrintWriter printWriter) {
    boolean table = orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE);//桌位单
    String msg;
    if (table) {
      msg = PrinterConstant.PEOPLE_NUMBER + orderInfo.getActualPeopleNumber();
    } else {
      msg = PrinterConstant.PEOPLE_NUMBER + "无";
    }
    printWriter.println(msg);
  }

  /**
   * 服务员和单位
   */
  private void getWaiterAndUnit(PxOrderInfo orderInfo, PrintWriter printWriter) {
    User dbUser = orderInfo.getDbUser();
    String name = (dbUser == null ) ?"admin" : dbUser.getName();
    int lengthWaiter = PrinterConstant.WAITER.getBytes().length + name.getBytes().length;
    int lengthUnit = PrinterConstant.HEAD_UNIT.getBytes().length + PrinterConstant.MONEY_UNIT.getBytes().length;
    int lengthWaiterAndUnit = 30 - (lengthWaiter + lengthUnit);
    String waiterAndUnitBlank = "";
    for (int i = 0; i < lengthWaiterAndUnit; i++) {
      waiterAndUnitBlank += " ";
    }
    String waiterAndUnit = PrinterConstant.CASHIER + name + waiterAndUnitBlank
        + PrinterConstant.HEAD_UNIT + PrinterConstant.MONEY_UNIT;
    printWriter.println(waiterAndUnit);
  }

  /**
   * 品名(金额)和数量/单位
   */
  private void getProductProperty(PrintWriter printWriter, boolean isFormat58) throws UnsupportedEncodingException{
   printWriter.println("品名"+getSpace("品名", (isFormat58 ? 15 : 20)) +"单价"+getSpace("单价",(isFormat58 ? 6 : 8) )
       +"小计"+getSpace("小计",(isFormat58 ? 6 : 8))+"数量");
  }

  /**
   * 商品真实实体数据
  * @param isPointMenu 是否点菜单
   */
  private void getProductData(List<PxOrderDetails> list, PrintWriter printWriter, boolean isFormat58 , boolean isPointMenu) throws IOException {
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
      //refresh
    DaoServiceUtil.getOrderInfoService().refresh(orderInfo);
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

     printWriter.println(before + getSpace(before, (isFormat58 ? 14 : 20)) + unitPrice + getSpace(realUnitMoney,(isFormat58 ? 7:9))
           + realMoney+getSpace(realMoney ,(isFormat58 ? 7:9)) + unit);
      //备注
      if (isPointMenu) {
        String remarks = details.getRemarks();
        if (remarks != null && !TextUtils.isEmpty(remarks.trim())) {
          printWriter.println("--备注:" + remarks);
        }
      }
    }
  }


  /**
   * 结账单(客户联)总价,优惠,折后等等
   */

  private void getBillWithCustomInfoAmount(AppCustomAmount customAmount, OutputStream outputStream, PrintWriter printWriter,
      PxOrderInfo orderInfo,boolean isFormat58) throws IOException {
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
        printWriter.println(totalMoney);
      }
      if (mReceivableAmount > 0.0) {
        //应收金额
        String receivableAmount = PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount);
        printWriter.println(receivableAmount);
      }
      if (mActualAmount > 0.0) {
        //实收金额
        String actualAmount = PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mActualAmount);
        printWriter.println(actualAmount);
      }
     if (mDiscAmount > 0.0) {
        //优惠金额
        String discAmount = PrinterConstant.DISCOUNT_AMOUNT + ":" + NumberFormatUtils.formatFloatNumber(mDiscAmount);
        printWriter.println(discAmount);
      }
     //支付类优惠
      printWriter.println(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
      //找零金额
      if (mChangeAmount > 0.0) {
        String changeAmount = PrinterConstant.CHANGE_CASH + NumberFormatUtils.formatFloatNumber(mChangeAmount);
        printWriter.println(changeAmount);
      }

      //附加费金额
      if (mSurchargeAmount > 0.0) {
        String surchargeAmount = PrinterConstant.SURCHARGE_CASH + NumberFormatUtils.formatFloatNumber(mSurchargeAmount);
        printWriter.println(surchargeAmount);
      }
     //损益金额
      if (mTailMoney > 0.0) {
        String tailMoney = PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(mTailMoney);
        printWriter.println(tailMoney);
      }
      //各项实收
      printWriter.println("--------实收详情--------");
      everyReceived(orderInfo,printWriter);
      //免单
      freePayInfo(outputStream,printWriter,orderInfo);
  }
  /**
   * 总价
   */
  private void getTotalPrice(OutputStream outputStream, PrintWriter printWriter,
      List<PxOrderDetails> detailsList) throws IOException {
    double countMoney = 0;
     if (detailsList == null || detailsList.size() == 0) return;
    PxOrderInfo dbOrder = detailsList.get(0).getDbOrder();
    //refresh
    DaoServiceUtil.getOrderInfoService().refresh(dbOrder);
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
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
    outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
    printWriter.println(PrinterConstant.TOTAL_AMOUNT + NumberFormatUtils.formatFloatNumber(countMoney));
    outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
  }

  /**
   * 合计项
   */
  private void getSumNumber(List<PxOrderDetails> detailsCollectionList, OutputStream outputStream, PrintWriter printWriter) throws IOException {
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
    printWriter.println(PrinterConstant.COLLECT + ":" + detailsCollectionList.size() + "项");
  }

  /**
   * 仅供账单详情应收,实收,找零,抹零
   */
  private void getRecMoney(PxOrderInfo orderInfo, OutputStream outputStream, PrintWriter printWriter,boolean isFormat58) throws IOException {
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
    outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
    outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
    outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
    printWriter.println(PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount));
    //实收金额
    printWriter.println(PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mActualAmount));
    //补足金额
    if ( mcomplementMoey > 0.0) {
      printWriter.println("补足金额:" + NumberFormatUtils.formatFloatNumber(mcomplementMoey));
    }
    //附加费
    if ( mSurchargeAmount > 0.0) {
      printWriter.println("附加费金额:" + NumberFormatUtils.formatFloatNumber(mSurchargeAmount));
      printWriter.println(" ");
    }
    //找零金额
    if ( mChangeAmount > 0.0) {
      printWriter.println("找零金额:" + NumberFormatUtils.formatFloatNumber(mChangeAmount));
    }
    //支付类优惠
    printWriter.println(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
   //损益金额
    if (mTailMoney > 0.0) {
      String tailMoney = PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(mTailMoney);
      printWriter.println(tailMoney);
    }
    //各项实收
    printWriter.println("--------实收详情--------");
    everyReceived(orderInfo,printWriter);

    //免单
    freePayInfo(outputStream,printWriter,orderInfo);
  }

  //@formatter:on

  /**
   * 打印合并后的详情
   *
   * @param isPointMenu 是否点菜单
   */
  private void printMergeDetails(PxOrderInfo orderInfo, List<PxOrderDetails> list,
      PrintWriter printWriter, boolean isFormat58, boolean isPointMenu) throws IOException {
    //商品实体
    for (PxOrderDetails details : list) {
      PxProductInfo dbProduct = details.getPrintProd();
      PxFormatInfo dbFormatInfo = details.getPrintFormat();
      PxMethodInfo dbMethodInfo = details.getPrintMethod();
      double refundNum = details.getRefundNum();
      double refundMultNum = details.getRefundMultNum();

      //下单状态 未下单的不打印
      if (PxOrderDetails.ORDER_STATUS_UNORDER.equals(details.getOrderStatus())) continue;
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
      //延迟状态
      String delay = "";
      if (isPointMenu && (details.getStatus().equals(PxOrderDetails.STATUS_DELAY))) {
        delay = "(待)";
      }
      //小计
      double money = details.getPrice() * details.getDiscountRate() / 100;
      //单价
      double unitPrice = details.getUnitPrice();
      if (orderInfo.getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
        unitPrice = details.getUnitVipPrice();
        money = details.getVipPrice() * details.getDiscountRate() / 100;
      }
      //②
      String realMoney = NumberFormatUtils.formatFloatNumber(money);
      String realUnitMoney = NumberFormatUtils.formatFloatNumber(unitPrice);
      String isGift = "";
      //赠品 不显示价格
      if (PxOrderDetails.GIFT_TRUE.equals(details.getIsGift())) {
        isGift = "(赠)";
        realMoney = "(0)";
      }
      //①
      String before = dbProduct.getName() + formatInfo + methodInfo + delay + isGift;
      //③
      String unit = "";
      //双单位
      boolean isMultiUnit = dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE);
      if (isMultiUnit) {
        Double realNum = details.getNum();
        unit = NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber())
            + dbProduct.getUnit() + "/" + realNum.intValue() + dbProduct.getOrderUnit();
      } else {
        Double realNumber = details.getNum();
        unit = realNumber.intValue() + dbProduct.getUnit();
      }

      printWriter.println(
          before + getSpace(before, (isFormat58 ? 14 : 20)) + unitPrice + getSpace(realUnitMoney,
              (isFormat58 ? 7 : 9)) + realMoney + getSpace(realMoney, (isFormat58 ? 7 : 9)) + unit);
      //已退数量
      if (refundNum > 0 || refundMultNum > 0) {
        String refundMultiNumAndUnit =
            refundMultNum + dbProduct.getUnit() + "/" + refundNum + dbProduct.getOrderUnit() + ")";
        String refunNumAndUnit = refundNum + dbProduct.getUnit() + ")";
        printWriter.println("   (已退:" + (isMultiUnit ? refundMultiNumAndUnit : refunNumAndUnit));
      }
      //备注
      if (isPointMenu) {
        String remarks = details.getRemarks();
        if (!TextUtils.isEmpty(remarks)) {
          printWriter.println("--备注:" + remarks);
        }
      }
    }
  }
  //@formatter:off
  /**
   * 分类统计
   */
  //@formatter:on
  private void categoryCollect(PxOrderInfo orderInfo, OutputStream outputStream,
      PrintWriter printWriter, boolean isFormat58) throws IOException {
    PxSetInfo setInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();

    if (setInfo != null && PxSetInfo.FINANCE_PRINT_CATEGORY_TRUE.equals(
        setInfo.getIsFinancePrintCategory())) {
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_CENTER);
      if (isFormat58) {
        printWriter.println("----------- 分类统计 ----------");
      } else {
        printWriter.println("------------------- 分类统计 ------------------");
      }
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      printWriter.println(
          "项目" + replace(isFormat58 ? 2 : 4) + "数量" + replace(isFormat58 ? 2 : 4) + "原  价"
              + replace(isFormat58 ? 4 : 6) + "优惠后金额");
      SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
      Cursor cursor = null;
      try {
        //@formatter:off
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
          String text = replaceKitchen(cursor.getString(3), (isFormat58) ? 4 : 6) + replaceKitchen(
              String.valueOf(cursor.getDouble(0)), (isFormat58) ? 6 : 8) + replaceKitchen(
              NumberFormatUtils.formatFloatNumber(originPrice), (isFormat58) ? 11 : 13)
              + NumberFormatUtils.formatFloatNumber(afterDiscount);
          printWriter.println(text);
        }
      } catch (Exception e) {
        e.printStackTrace();
        Logger.e(e.toString());
      } finally {
        IOUtils.closeCloseables(cursor);
      }
    }
    //line
    dividerLine(outputStream, printWriter, isFormat58);
  }

  /**
   * 结账单(财务联)总价,优惠,折后等等
   */
  // @formatter:off
  private void getBillWithFinanceInfoAmount(PxOrderInfo orderInfo, AppFinanceAmount financeAmount,
      OutputStream outputStream, PrintWriter printWriter,boolean isFormat58) throws IOException{
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
      double payPrivilege = (orderInfo.getPayPrivilege() == null ) ? 0 : orderInfo.getPayPrivilege();
      //总金额
      String totalAmount = PrinterConstant.TOTAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mTotalAmount);
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      outputStream.write(EpsonPosPrinterCommand.setMultiByteCharMode(false, false, false));
      outputStream.write(EpsonPosPrinterCommand.setPrintMode(false, false, false, false, false));
      printWriter.println(totalAmount);
     //优惠金额
      if (mDisMoney > 0.0) {
        String disMoney = PrinterConstant.DISCOUNT_AMOUNT + NumberFormatUtils.formatFloatNumber(mDisMoney);
        printWriter.println(disMoney);
      }
    //支付类优惠
      printWriter.println(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
      //应收金额
      String receivableAmount = PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount);
      outputStream.write(EpsonPosPrinterCommand.ESC_ALIGN_LEFT);
      printWriter.println(receivableAmount);
      //实收金额
      String actualAmount = PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mActualAmount);
      printWriter.println(actualAmount);
      //找零金额
      if (mChangeMoney > 0.0) {
        String changeMoney = PrinterConstant.CHANGE_CASH + NumberFormatUtils.formatFloatNumber(mChangeMoney);
        printWriter.println(changeMoney);
      }
     //损益金额
      if (mTailMoney > 0.0) {
        String tailMoney = PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(mTailMoney);
        printWriter.println(tailMoney);
      }
      //各项实收
       printWriter.println("--------实收详情--------");
      everyReceived(orderInfo, printWriter);
     //免单
      freePayInfo(outputStream,printWriter,orderInfo);
    }

 /**
 * 打印免单原因
  */
  private void freePayInfo(OutputStream outputStream,PrintWriter printWriter,PxOrderInfo orderInfo) throws IOException{
    QueryBuilder<PxPayInfo> qb = DaoServiceUtil.getPayInfoService().queryBuilder();
    qb.where(PxPayInfoDao.Properties.PxOrderInfoId.eq(orderInfo.getId()));
    qb.where(PxPayInfoDao.Properties.PaymentType.eq(PxPaymentMode.TYPE_FREE));
    List<PxPayInfo> freePayInfoList = qb.list();
    if (freePayInfoList != null && freePayInfoList.size() > 0) {
      //分割线
       dividerLine(outputStream,printWriter,isFormat58);
      for (PxPayInfo payInfo : freePayInfoList) {
        printWriter.println("免单金额:" + payInfo.getReceived() +"  原因:"+payInfo.getRemarks());
      }
    }
  }
  /**
 * 各项实收 金额
  */
 //@formatter:off
 private void everyReceived(PxOrderInfo orderInfo, PrintWriter printWriter) {
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
            printWriter.println(cursor.getString(1) + ":" + received + "   找零:"+NumberFormatUtils.formatFloatNumber(change));
         }else{
            printWriter.println(cursor.getString(1) + ":" + NumberFormatUtils.formatFloatNumber(received));
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

  //@formatter:on
  private void sound(OutputStream outputStream) throws IOException {
    byte US = 0x05;
    byte US1 = 0x02;
    byte[] sound = addSound(US1, US);
    outputStream.write(sound);
    outputStream.flush();
  }

  /**
   * 蜂鸣
   *
   * @param n 次数
   * @param t 音量
   */
  private byte[] addSound(byte n, byte t) {
    byte[] sounds = EpsonPosPrinterCommand.SOUND;
    sounds[2] = n;
    sounds[3] = t;
    return sounds;
  }
  //@formatter:off
}