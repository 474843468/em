package com.psi.easymanager.print.usb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Base64;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CollectionContentEvent;
import com.psi.easymanager.event.DetailsContentEvent;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.module.AppBillCount;
import com.psi.easymanager.module.AppCashCollect;
import com.psi.easymanager.module.AppSaleContent;
import com.psi.easymanager.module.AppShiftCateInfo;
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
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.print.MergePrintDetails;
import com.psi.easymanager.print.constant.PrinterConstant;
import com.psi.easymanager.print.module.AppCustomAmount;
import com.psi.easymanager.print.module.AppFinanceAmount;
import com.psi.easymanager.print.module.ShiftWork;
import com.psi.easymanager.ui.activity.DayReportActivity;
import com.psi.easymanager.ui.activity.ShiftChangeFunctionsActivity;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.NumberFormatUtils;
import de.greenrobot.dao.query.QueryBuilder;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by psi on 2016/5/28.
 * USB打印数据
 */
public class PrinterUsbData {
  /**
   * 餐厅名称
   */
  //@formatter:off
  private static void getOfficeName(EscCommand esc) {
    String officeName = DaoServiceUtil.getOfficeDao().queryBuilder().unique().getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(officeName);
    esc.addPrintAndLineFeed();
  }

  /**
   * 单子类型(点菜单,客户联,财务联等等)
   */
  private static void getTypeName(EscCommand esc, String typeName) {
    esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高
    esc.addText(typeName);
    esc.addPrintAndLineFeed();
  }

  /**
   * 分割线(设置打印左对齐)
   */
  private static void getDividerLineWithLeft(EscCommand esc) {
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
    esc.addText("--------------------------------");
    esc.addPrintAndLineFeed();
  }

  /**
   * 桌号和单号
   */
  private static void getTableIdAndUniqueId(EscCommand esc, PxOrderInfo mOrderInfo) {
    try {
      //桌位单
      if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
        TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .unique();
        PxTableInfo dbTable = unique.getDbTable();
        int lengthTableId = PrinterConstant.TABLE_NUMBER.getBytes("GBK").length + dbTable.getName()
            .getBytes("GBK").length;
        int lengthUniqueId = PrinterConstant.ODD_NUMBER.getBytes("GBK").length + String.valueOf(
            Integer.valueOf(mOrderInfo.getOrderNo().substring(24, 30))).getBytes("GBK").length;
        int lengthTableAndUnique = 32 - (lengthTableId + lengthUniqueId);
        String tableAndUniqueBlank = "";
        for (int i = 0; i < lengthTableAndUnique; i++) {
          tableAndUniqueBlank += " ";
        }
        String tableAndId = PrinterConstant.TABLE_NUMBER + dbTable.getName() + tableAndUniqueBlank
            + PrinterConstant.ODD_NUMBER + Integer.valueOf(
            mOrderInfo.getOrderNo().substring(24, 30)) + "";
        esc.addText(tableAndId);
        esc.addPrintAndLineFeed();
      }
      //零售单
      else {
        int lengthTableId = PrinterConstant.TABLE_NUMBER.getBytes("GBK").length
            + PrinterConstant.UNIQUE_BILL.getBytes("GBK").length;
        int lengthUniqueId = PrinterConstant.ODD_NUMBER.getBytes("GBK").length + String.valueOf(
            Integer.valueOf(mOrderInfo.getOrderNo().substring(24, 30))).getBytes("GBK").length;
        int lengthTableAndUnique = 32 - (lengthTableId + lengthUniqueId);
        String tableAndUniqueBlank = "";
        for (int i = 0; i < lengthTableAndUnique; i++) {
          tableAndUniqueBlank += " ";
        }
        String tableAndId =
            PrinterConstant.TABLE_NUMBER + PrinterConstant.UNIQUE_BILL + tableAndUniqueBlank
                + PrinterConstant.ODD_NUMBER + Integer.valueOf(
                mOrderInfo.getOrderNo().substring(24, 30)) + "";
        esc.addText(tableAndId);
        esc.addPrintAndLineFeed();
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * 桌号和单号(仅供单号放大用在点菜单,结账页面的客户联和财务联)
   */
  private static void getTableIdAndUniqueIdWithLarge(EscCommand esc, PxOrderInfo mOrderInfo) {
    try {
      if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {//桌位单
        //单号
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);//设置倍高
        esc.addText(PrinterConstant.ODD_NUMBER + Integer.valueOf(
            mOrderInfo.getOrderNo().substring(24, 30)));
        //桌号
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);//设置倍高
        TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .unique();
        PxTableInfo dbTable = unique.getDbTable();
        esc.addText("   " + PrinterConstant.TABLE_NUMBER + dbTable.getName());
        esc.addPrintAndLineFeed();
      } else {//零售单
        //单号
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);//设置倍高
        esc.addText(PrinterConstant.ODD_NUMBER + Integer.valueOf(
            mOrderInfo.getOrderNo().substring(24, 30)));
        esc.addPrintAndLineFeed();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 账单号
   */
  private static void getBillId(EscCommand esc, PxOrderInfo mOrderInfo) {
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    String billId = PrinterConstant.BILL_NO + mOrderInfo.getOrderNo().substring(10, 30);
    esc.addText(billId);
    esc.addPrintAndLineFeed();
  }

  /**
   * 人数
   */
  private static void getPeopleNumber(EscCommand esc, PxOrderInfo mOrderInfo) {
    //桌位单
    if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
      String PeopleNumber = PrinterConstant.PEOPLE_NUMBER + mOrderInfo.getActualPeopleNumber();
      esc.addText(PeopleNumber);
      esc.addPrintAndLineFeed();
    }
    //零售单
    else {
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
      String PeopleNumber = PrinterConstant.PEOPLE_NUMBER + PrinterConstant.NOTHING;
      esc.addText(PeopleNumber);
      esc.addPrintAndLineFeed();
    }
  }

  /**
   * 服务员和单位
   */
  private static void getWaiterAndUnit(EscCommand esc, PxOrderInfo mOrderInfo) {
    try {
      int lengthWaiter = PrinterConstant.WAITER.getBytes("GBK").length + mOrderInfo.getDbUser()
          .getName()
          .getBytes("GBK").length;
      int lengthUnit =
          PrinterConstant.HEAD_UNIT.getBytes("GBK").length + PrinterConstant.MONEY_UNIT.getBytes(
              "GBK").length;
      int lengthWaiterAndUnit = 32 - (lengthWaiter + lengthUnit);
      String waiterAndUnitBlank = "";
      for (int i = 0; i < lengthWaiterAndUnit; i++) {
        waiterAndUnitBlank += " ";
      }
      String waiterAndUnit =
          PrinterConstant.CASHIER + mOrderInfo.getDbUser().getName() + waiterAndUnitBlank
              + PrinterConstant.HEAD_UNIT + PrinterConstant.MONEY_UNIT;
      esc.addText(waiterAndUnit);
      esc.addPrintAndLineFeed();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * 分割线
   */
  private static void getDividerLine(EscCommand esc) {
    esc.addText("--------------------------------");
    esc.addPrintAndLineFeed();
  }

  /**
   * 品名(金额)和数量/单位
   */
  private static void getProductProperty(EscCommand esc) {
    try {
      int lengthName =
          (PrinterConstant.COMMODITY_NAME + "(" + PrinterConstant.MONEY + ")").getBytes(
              "GBK").length;
      int lengthNumber =
          (PrinterConstant.NUMBER + "/" + PrinterConstant.UNIT).getBytes("GBK").length;
      int lengthNameAndNumber = 32 - (lengthName + lengthNumber);
      String nameAndNumberBlank = "";
      for (int i = 0; i < lengthNameAndNumber; i++) {
        nameAndNumberBlank += " ";
      }
      String nameAndNumber =
          (PrinterConstant.COMMODITY_NAME + "(" + PrinterConstant.MONEY + ")") + nameAndNumberBlank
              + (PrinterConstant.NUMBER + "/" + PrinterConstant.UNIT);
      esc.addText(nameAndNumber);
      esc.addPrintAndLineFeed();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * 品名(金额)和数量/单位(修改版)
   */
  private static void getProductPropertyModify(EscCommand esc) {
    String productPropertyBlank = "品名" + "        "+"单价"+"    "+"小计"+"    "+"数量";
    esc.addText(productPropertyBlank);
    esc.addPrintAndLineFeed();
  }

  /**
   * 分割线(取消倍高倍宽)
   */
  private static void getCancelWeightAndHeight(EscCommand esc) {
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消为倍高倍宽
    esc.addText("--------------------------------");
    esc.addPrintAndLineFeed();
  }

  /**
   * 商品真实实体数据
   */
  //@formatter:off
  private static void getProductData(EscCommand esc, List<PxOrderDetails> detailsList,
      String type) {
    try {
      //商品实体
      for (PxOrderDetails details : detailsList) {
        //未下单的不打印
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_UNORDER)) continue;
        //品名(金额)长度
        int lengthNameValue;
        //数量/单位长度
        int lengthNumberValue;
        //中间空格字符数
        int lengthNameValueAndNumberValue;
        //中间空格字符
        String nameValueAndNumberValueBlank = "";
        //一条数据字符串
        String nameValueAndNumberValue = "";
        //赋值一种商品金额
        double moneyFormat = 0;
        //赋值做法
        String method = "";
        //赋值规格
        String standard = "";
        //赋值订货单位
        String number;
        //赋值结账单位
        String numberMultiple;
        //赋值商品状态
        String status = "";
        //是否为赠品
        String isGift = "";
        //商品状态(未下单,已下单,退货)
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
          status = "";
        } else if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
          status = "(退菜)";
        } else {
          status = "(未下)";
        }
        //价格
        moneyFormat = details.getPrintPrice();
        //规格
        if (details.getDbFormatInfo() == null) {
          standard = "";
        } else {
          standard = details.getDbFormatInfo().getName();
        }
        //做法
        if (details.getDbMethodInfo() == null) {
          method = "";
        } else {
          method = details.getDbMethodInfo().getName();
        }
        //双单位
        if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + details.getDbProduct().getOrderUnit() + ")";
          numberMultiple =
              NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber()) + "("
                  + details.getDbProduct().getUnit() + ")";
        } else {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + details.getDbProduct().getUnit() + ")";
          numberMultiple = "";
        }
        //是否为赠品
        if (details.getIsGift().equals("0")) {//不是赠品
          isGift = "";
        } else {//是赠品
          isGift = "(赠)";
        }
        //双单位计算空格数并显示
        if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          lengthNameValue =
              (details.getDbProduct().getName() + "(" + NumberFormatUtils.formatFloatNumber(
                  moneyFormat) + ")" + isGift).getBytes("GBK").length;
          lengthNumberValue = (numberMultiple + "/" + number).getBytes("GBK").length;
          lengthNameValueAndNumberValue = 32 - (lengthNameValue + lengthNumberValue);
          for (int i = 0; i < lengthNameValueAndNumberValue; i++) {
            nameValueAndNumberValueBlank += " ";
          }
          nameValueAndNumberValue =
              (details.getDbProduct().getName() + "(" + NumberFormatUtils.formatFloatNumber(
                  moneyFormat) + ")" + isGift) + nameValueAndNumberValueBlank + (numberMultiple
                  + "/" + number);
        } else {
          lengthNameValue =
              (details.getDbProduct().getName() + "(" + NumberFormatUtils.formatFloatNumber(
                  moneyFormat) + ")" + isGift).getBytes("GBK").length;
          lengthNumberValue = (number).getBytes("GBK").length;
          lengthNameValueAndNumberValue = 32 - (lengthNameValue + lengthNumberValue);
          for (int i = 0; i < lengthNameValueAndNumberValue; i++) {
            nameValueAndNumberValueBlank += " ";
          }
          nameValueAndNumberValue =
              (details.getDbProduct().getName() + "(" + NumberFormatUtils.formatFloatNumber(
                  moneyFormat) + ")" + isGift) + nameValueAndNumberValueBlank + (number);
        }
        esc.addText(nameValueAndNumberValue);
        esc.addPrintAndLineFeed();
        //折扣率 不等于100时显示折扣率  商品状态
        if ((details.getDiscountRate() != 100) || !status.equals("")) {
          String content = (details.getDiscountRate() == 100) ? status
              : details.getDiscountRate() + "%    " + status;
          esc.addText(content);
          esc.addPrintAndLineFeed();
        }
        //规格做法显示
        if (details.getDbMethodInfo() == null && details.getDbFormatInfo() == null) {
          //esc.addPrintAndLineFeed();
        } else {
          StringBuilder sb = new StringBuilder();
          if (details.getDbFormatInfo() != null) {
            sb.append(standard);
          }
          if (details.getDbMethodInfo() != null) {
            sb.append((details.getDbFormatInfo() == null) ? method : "   " + method);
          }
          esc.addText(sb.toString());
          esc.addPrintAndLineFeed();
        }
        if (type.equals("0")) {//仅供点菜单使用
          //备注
          String remarks = details.getRemarks();
          if (!TextUtils.isEmpty(remarks)){
            esc.addText("(备注:" + remarks + ")");
            esc.addPrintAndLineFeed();
          }
        } else if (type.equals("1")) {//仅供结账页面财务联和客户联
          //不显示备注信息
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * 商品真实实体数据(修改版)
   */
  //@formatter:off
  @Deprecated
  private static void getProductDataModify(EscCommand esc, List<PxOrderDetails> detailsList,
      String type) {
    try {
      //商品实体
      for (PxOrderDetails details : detailsList) {
        //未下单的不打印
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_UNORDER)) continue;
        //品名长度
        int lengthNameValue;
        //单价长度
        int lengthPriceValue;
        //小计长度
        int lengthTotalValue;
        //数量长度
        int lengthNumberValue;
        //品名和单价中间空格字符数
        int lengthNameValueAndPriceValue;
        //单价和小计中间空格字符数
        int lengthPriceValueAndTotalValue;
        //小计和数量中间空格字符数
        int lengthTotalValueAndNumberValue;
        //品名和单价中间空格字符
        String nameValueAndPriceValueBlank = "";
        //单价和小计中间空格字符
        String namePriceAndTotalValueBlank = "";
        //小计和数量中间空格字符
        String nameTotalAndNumberValueBlank = "";
        //一条数据字符串
        String oneValue = "";
        //赋值一种商品金额
        double moneyFormat = 0;
        //赋值做法
        String method = "";
        //赋值规格
        String standard = "";
        //赋值订货单位
        String number;
        //赋值结账单位
        String numberMultiple;
        //赋值商品状态
        String status = "";
        //是否为赠品
        String isGift = "";
        //商品状态(未下单,已下单,退货)
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
          status = "";
        } else if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
          status = "(退菜)";
        } else {
          status = "(未下)";
        }
        //价格
        moneyFormat = details.getPrintPrice();
        //规格
        if (details.getDbFormatInfo() == null) {
          standard = "";
        } else {
          standard = details.getDbFormatInfo().getName();
        }
        //做法
        if (details.getDbMethodInfo() == null) {
          method = "";
        } else {
          method = details.getDbMethodInfo().getName();
        }
        //双单位
        if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + details.getDbProduct().getOrderUnit() + ")";
          numberMultiple =
              NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber()) + "("
                  + details.getDbProduct().getUnit() + ")";
        } else {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + details.getDbProduct().getUnit() + ")";
          numberMultiple = "";
        }
        //是否为赠品
        if (details.getIsGift().equals("0")) {//不是赠品
          isGift = "";
        } else {//是赠品
          isGift = "(赠)";
        }
        //单价
        double unitPrice = 0;
        //规格单价计算
        PxOrderInfo orderInfo = details.getDbOrder();
        if (orderInfo.getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
          if (details.getDbFormatInfo() != null){
            PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
                .queryBuilder()
                .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
                .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(details.getDbProduct().getId()))
                .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(details.getDbFormatInfo().getId()))
                .unique();
            unitPrice = (rel == null ? details.getDbProduct().getVipPrice() : rel.getVipPrice());
          }else{
            unitPrice = details.getDbProduct().getVipPrice();
          }
        } else {
          if (details.getDbFormatInfo() != null){
            PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
                .queryBuilder()
                .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
                .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(details.getDbProduct().getId()))
                .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(details.getDbFormatInfo().getId()))
                .unique();
            unitPrice = (rel == null ? details.getDbProduct().getPrice() : rel.getPrice());
          }else{
            unitPrice = details.getDbProduct().getPrice();
          }
        }
        //双单位计算空格数并显示
        if(details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)){
          lengthNameValue = (details.getDbProduct().getName()+isGift).getBytes("GBK").length;
          //lengthNameValueAndPriceValue这个值可能会出现负数
          lengthNameValueAndPriceValue = 12-lengthNameValue;
          for (int i = 0; i < lengthNameValueAndPriceValue; i++) {
            nameValueAndPriceValueBlank += " ";
          }
          lengthPriceValue = NumberFormatUtils.formatFloatNumber(unitPrice).getBytes("GBK").length;
          //lengthPriceValueAndTotalValue这个值可能会出现负数
          lengthPriceValueAndTotalValue = 8-lengthPriceValue;
          for (int i = 0; i < lengthPriceValueAndTotalValue; i++) {
            namePriceAndTotalValueBlank += " ";
          }
          //lengthTotalValueAndNumberValue这个值可能会出现负数
          lengthTotalValue = NumberFormatUtils.formatFloatNumber(moneyFormat).getBytes("GBK").length;
          lengthTotalValueAndNumberValue = 7-lengthTotalValue;
          for (int i = 0; i < lengthTotalValueAndNumberValue; i++) {
            nameTotalAndNumberValueBlank += " ";
          }
          oneValue = details.getDbProduct().getName()
              +isGift+nameValueAndPriceValueBlank
              +NumberFormatUtils.formatFloatNumber(unitPrice)
              +namePriceAndTotalValueBlank
              +NumberFormatUtils.formatFloatNumber(moneyFormat)
              +nameTotalAndNumberValueBlank
              +numberMultiple + "/" + number;
        }else {
          lengthNameValue = (details.getDbProduct().getName()+isGift).getBytes("GBK").length;
          //lengthNameValueAndPriceValue这个值可能会出现负数
          lengthNameValueAndPriceValue = 12-lengthNameValue;
          for (int i = 0; i < lengthNameValueAndPriceValue; i++) {
            nameValueAndPriceValueBlank += " ";
          }
          lengthPriceValue = NumberFormatUtils.formatFloatNumber(unitPrice).getBytes("GBK").length;
          //lengthPriceValueAndTotalValue这个值可能会出现负数
          lengthPriceValueAndTotalValue = 8-lengthPriceValue;
          for (int i = 0; i < lengthPriceValueAndTotalValue; i++) {
            namePriceAndTotalValueBlank += " ";
          }
          //lengthTotalValueAndNumberValue这个值可能会出现负数
          lengthTotalValue = NumberFormatUtils.formatFloatNumber(moneyFormat).getBytes("GBK").length;
          lengthTotalValueAndNumberValue = 7-lengthTotalValue;
          for (int i = 0; i < lengthTotalValueAndNumberValue; i++) {
            nameTotalAndNumberValueBlank += " ";
          }
          oneValue = details.getDbProduct().getName()
              +isGift+nameValueAndPriceValueBlank
              +NumberFormatUtils.formatFloatNumber(unitPrice)
              +namePriceAndTotalValueBlank
              +NumberFormatUtils.formatFloatNumber(moneyFormat)
              +nameTotalAndNumberValueBlank
              +number;
        }
        esc.addText(oneValue);
        esc.addPrintAndLineFeed();
        //折扣率 不等于100时显示折扣率  商品状态
        if ((details.getDiscountRate() != 100) || !status.equals("")) {
          String content = (details.getDiscountRate() == 100) ? status
              : details.getDiscountRate() + "%    " + status;
          esc.addText(content);
          esc.addPrintAndLineFeed();
        }
        //规格做法显示
        if (details.getDbMethodInfo() == null && details.getDbFormatInfo() == null) {
          //esc.addPrintAndLineFeed();
        } else {
          StringBuilder sb = new StringBuilder();
          if (details.getDbFormatInfo() != null) {
            sb.append(standard);
          }
          if (details.getDbMethodInfo() != null) {
            sb.append((details.getDbFormatInfo() == null) ? method : "   " + method);
          }
          esc.addText(sb.toString());
          esc.addPrintAndLineFeed();
        }
        if (type.equals("0")) {//仅供点菜单使用
          //备注
          String remarks = details.getRemarks();
          if (!TextUtils.isEmpty(remarks)){
            esc.addText("(备注:" +remarks  + ")");
            esc.addPrintAndLineFeed();
          }
        } else if (type.equals("1")) {//仅供结账页面财务联和客户联
          //不显示备注信息
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }



  /**
   * 合并后的  商品真实实体数据(修改版)
   */
  private static void getProductDataModifyMerge(EscCommand esc, List<PxOrderDetails> detailsList,
      String type) {
    try {
      //商品实体
      for (PxOrderDetails details : detailsList) {
        //临时打印用的数据
        PxFormatInfo dbFormatInfo = details.getPrintFormat();
        PxMethodInfo dbMethodInfo = details.getPrintMethod();
        PxProductInfo dbProduct = details.getPrintProd();
        int discountRate = details.getDiscountRate();
        PxOrderInfo dbOrder = details.getPrintOrder();
        double num = details.getNum();
        double multipleUnitNumber = details.getMultipleUnitNumber();
        double refundNum = details.getRefundNum();
        double refundMultNum = details.getRefundMultNum();

        //未下单的不打印
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_UNORDER)) continue;
        ////商品状态(未下单,已下单,退货)
        //String orderStatus = "";
        //if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
        //  orderStatus = "(退菜)";
        //}
        //品名长度
        int lengthNameValue;
        //单价长度
        int lengthPriceValue;
        //小计长度
        int lengthTotalValue;
        //数量长度
        int lengthNumberValue;
        //品名和单价中间空格字符数
        int lengthNameValueAndPriceValue;
        //单价和小计中间空格字符数
        int lengthPriceValueAndTotalValue;
        //小计和数量中间空格字符数
        int lengthTotalValueAndNumberValue;
        //品名和单价中间空格字符
        String nameValueAndPriceValueBlank = "";
        //单价和小计中间空格字符
        String namePriceAndTotalValueBlank = "";
        //小计和数量中间空格字符
        String nameTotalAndNumberValueBlank = "";
        //一条数据字符串
        String oneValue = "";
        //赋值做法
        String method = "";
        //赋值规格
        String standard = "";
        //赋值订货单位
        String number;
        //赋值结账单位
        String numberMultiple;
        //是否为赠品
        String isGift = "";

        //规格
        if (dbFormatInfo == null) {
          standard = "";
        } else {
          standard = dbFormatInfo.getName();
        }
        //做法
        if (dbMethodInfo == null) {
          method = "";
        } else {
          method = dbMethodInfo.getName();
        }
        //双单位
        boolean isMultiUnit = dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE);
        if (isMultiUnit) {
          number = (num + "").substring(0, (num + "").indexOf(".")) + dbProduct.getOrderUnit();
          numberMultiple =
              NumberFormatUtils.formatFloatNumber(multipleUnitNumber) + dbProduct.getUnit();
        } else {
          number = (num + "").substring(0, (num + "").indexOf("."))  + dbProduct.getUnit() ;
          numberMultiple = "";
        }
        //是否为赠品
        if (details.getIsGift().equals("0")) {//不是赠品
          isGift = "";
        } else {//是赠品
          isGift = "(赠)";
        }
        //单价
        double unitPrice = details.getUnitPrice();
        //小计
        double moneyFormat = details.getPrice() * details.getDiscountRate() / 100;
        //规格单价计算
        if (dbOrder.getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
          unitPrice = details.getUnitVipPrice();
          moneyFormat = details.getVipPrice() * details.getDiscountRate() / 100;
        }

        //双单位计算空格数并显示
        if (isMultiUnit) {
          lengthNameValue = (dbProduct.getName() + isGift).getBytes("GBK").length;
          //lengthNameValueAndPriceValue这个值可能会出现负数
          lengthNameValueAndPriceValue = 12 - lengthNameValue;
          for (int i = 0; i < lengthNameValueAndPriceValue; i++) {
            nameValueAndPriceValueBlank += " ";
          }
          lengthPriceValue = NumberFormatUtils.formatFloatNumber(unitPrice).getBytes("GBK").length;
          //lengthPriceValueAndTotalValue这个值可能会出现负数
          lengthPriceValueAndTotalValue = 8 - lengthPriceValue;
          for (int i = 0; i < lengthPriceValueAndTotalValue; i++) {
            namePriceAndTotalValueBlank += " ";
          }
          //lengthTotalValueAndNumberValue这个值可能会出现负数
          lengthTotalValue =
              NumberFormatUtils.formatFloatNumber(moneyFormat).getBytes("GBK").length;
          lengthTotalValueAndNumberValue = 7 - lengthTotalValue;
          for (int i = 0; i < lengthTotalValueAndNumberValue; i++) {
            nameTotalAndNumberValueBlank += " ";
          }
          oneValue = dbProduct.getName() + isGift + nameValueAndPriceValueBlank
              + NumberFormatUtils.formatFloatNumber(unitPrice) + namePriceAndTotalValueBlank
              + NumberFormatUtils.formatFloatNumber(moneyFormat) + nameTotalAndNumberValueBlank
              + numberMultiple + "/" + number;
        } else {
          lengthNameValue = (dbProduct.getName() + isGift).getBytes("GBK").length;
          //lengthNameValueAndPriceValue这个值可能会出现负数
          lengthNameValueAndPriceValue = 12 - lengthNameValue;
          for (int i = 0; i < lengthNameValueAndPriceValue; i++) {
            nameValueAndPriceValueBlank += " ";
          }
          lengthPriceValue = NumberFormatUtils.formatFloatNumber(unitPrice).getBytes("GBK").length;
          //lengthPriceValueAndTotalValue这个值可能会出现负数
          lengthPriceValueAndTotalValue = 8 - lengthPriceValue;
          for (int i = 0; i < lengthPriceValueAndTotalValue; i++) {
            namePriceAndTotalValueBlank += " ";
          }
          //lengthTotalValueAndNumberValue这个值可能会出现负数
          lengthTotalValue =
              NumberFormatUtils.formatFloatNumber(moneyFormat).getBytes("GBK").length;
          lengthTotalValueAndNumberValue = 7 - lengthTotalValue;
          for (int i = 0; i < lengthTotalValueAndNumberValue; i++) {
            nameTotalAndNumberValueBlank += " ";
          }
          oneValue = dbProduct.getName() + isGift + nameValueAndPriceValueBlank
              + NumberFormatUtils.formatFloatNumber(unitPrice) + namePriceAndTotalValueBlank
              + NumberFormatUtils.formatFloatNumber(moneyFormat) + nameTotalAndNumberValueBlank
              + number;
        }
        esc.addText(oneValue);
        esc.addPrintAndLineFeed();
        //折扣率 不等于100时显示折扣率  商品状态
        if ((discountRate != 100)) {
          String content = discountRate + "%    " ;
          esc.addText(content);
          esc.addPrintAndLineFeed();
        }
        //规格做法显示
        if (dbMethodInfo == null && dbFormatInfo == null) {
          //esc.addPrintAndLineFeed();
        } else {
          StringBuilder sb = new StringBuilder();
          if (dbFormatInfo != null) {
            sb.append(standard);
          }
          if (dbMethodInfo != null) {
            sb.append((dbFormatInfo == null) ? method : "   " + method);
          }
          esc.addText(sb.toString());
          esc.addPrintAndLineFeed();
        }
        //已退数量
        //已退数量
        if (refundNum > 0 || refundMultNum > 0){
          String refundMultiNumAndUnit = refundMultNum + dbProduct.getUnit() +"/" + refundNum + dbProduct.getOrderUnit() +")";
          String refunNumAndUnit = refundNum + dbProduct.getUnit() + ")";
          esc.addText("   (已退:" + (isMultiUnit ? refundMultiNumAndUnit : refunNumAndUnit));
          esc.addPrintAndLineFeed();
        }

        if (type.equals("0")) {//仅供点菜单使用
          //备注
          String remarks = details.getRemarks();
          if (!TextUtils.isEmpty(remarks)){
            esc.addText("(备注:" +  remarks + ")");
            esc.addPrintAndLineFeed();
          }
        } else if (type.equals("1")) {//仅供结账页面财务联和客户联
          //不显示备注信息
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }
  /**
   * 商品真实退菜实体数据(相对于单个菜)
   */
  private static void getRefundProductData(EscCommand esc, PxOrderDetails orderDetails) {
    try {
      //品名(金额)长度
      int lengthNameValue;
      //数量/单位长度
      int lengthNumberValue;
      //中间空格字符数
      int lengthNameValueAndNumberValue;
      //中间空格字符
      String nameValueAndNumberValueBlank = "";
      //一条数据字符串
      String nameValueAndNumberValue = "";
      //赋值一种商品金额
      double moneyFormat = 0;
      //赋值做法
      String method = "";
      //赋值规格
      String standard = "";
      //赋值订货单位
      String number;
      //赋值结账单位
      String numberMultiple;
      //赋值商品状态
      String status = "";
      lengthNameValue = orderDetails.getDbProduct().getName().getBytes("GBK").length;
      esc.addText(orderDetails.getDbProduct().getName());
      esc.addPrintAndLineFeed();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 商品真实实体数据(仅用于已结账单的财务联和客户联)
   */
  private static void getProductDataWith(EscCommand esc, List<PxOrderDetails> detailsList) {
    try {
      //商品实体
      for (PxOrderDetails details : detailsList) {
        //未下单的不打印
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_UNORDER)) {
          continue;
        }
        //品名(金额)长度
        int lengthNameValue;
        //数量/单位长度
        int lengthNumberValue;
        //中间空格字符数
        int lengthNameValueAndNumberValue;
        //中间空格字符
        String nameValueAndNumberValueBlank = "";
        //一条数据字符串
        String nameValueAndNumberValue = "";
        //赋值一种商品金额
        double moneyFormat = 0;
        //赋值做法
        String method = "";
        //赋值规格
        String standard = "";
        //赋值订货单位
        String number;
        //赋值结账单位
        String numberMultiple;
        //赋值商品状态
        String status = "";
        //是否为赠品
        String isGift = "";
        //商品状态(未下单,已下单,退货)
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
          status = "";
        } else if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
          status = "(退菜)";
        } else {
          status = "(未下)";
        }
        //价格
        moneyFormat = details.getPrintPrice();
        //规格
        if (details.getDbFormatInfo() == null) {
          standard = "";
        } else {
          standard = details.getDbFormatInfo().getName();
        }
        //做法
        if (details.getDbMethodInfo() == null) {
          method = "";
        } else {
          method = details.getDbMethodInfo().getName();
        }
        //双单位
        if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + details.getDbProduct().getOrderUnit() + ")";
          numberMultiple =
              NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber()) + "("
                  + details.getDbProduct().getUnit() + ")";
        } else {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + details.getDbProduct().getUnit() + ")";
          numberMultiple = "";
        }
        //是否为赠品
        if (details.getIsGift().equals("0")) {//不是赠品
          isGift = "";
        } else {//是赠品
          isGift = "(赠)";
        }
        //双单位计算空格数并显示
        if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          lengthNameValue =
              (details.getDbProduct().getName() + "(" + NumberFormatUtils.formatFloatNumber(
                  moneyFormat) + ")" + isGift).getBytes("GBK").length;
          lengthNumberValue = (numberMultiple + "/" + number).getBytes("GBK").length;
          lengthNameValueAndNumberValue = 32 - (lengthNameValue + lengthNumberValue);
          for (int i = 0; i < lengthNameValueAndNumberValue; i++) {
            nameValueAndNumberValueBlank += " ";
          }
          nameValueAndNumberValue =
              (details.getDbProduct().getName() + "(" + NumberFormatUtils.formatFloatNumber(
                  moneyFormat) + ")" + isGift) + nameValueAndNumberValueBlank + (numberMultiple
                  + "/" + number);
        } else {
          lengthNameValue =
              (details.getDbProduct().getName() + "(" + NumberFormatUtils.formatFloatNumber(
                  moneyFormat) + ")" + isGift).getBytes("GBK").length;
          lengthNumberValue = (number).getBytes("GBK").length;
          lengthNameValueAndNumberValue = 32 - (lengthNameValue + lengthNumberValue);
          for (int i = 0; i < lengthNameValueAndNumberValue; i++) {
            nameValueAndNumberValueBlank += " ";
          }
          nameValueAndNumberValue =
              (details.getDbProduct().getName() + "(" + NumberFormatUtils.formatFloatNumber(
                  moneyFormat) + ")" + isGift) + nameValueAndNumberValueBlank + (number);
        }
        esc.addText(nameValueAndNumberValue);
        esc.addPrintAndLineFeed();
        //折扣率 不等于100时显示折扣率  商品状态
        if ((details.getDiscountRate() != 100) || !status.equals("")) {
          String content = (details.getDiscountRate() == 100) ? status
              : details.getDiscountRate() + "%    " + status;
          esc.addText(content);
          esc.addPrintAndLineFeed();
        }
        //规格做法显示
        if (details.getDbMethodInfo() == null && details.getDbFormatInfo() == null) {
          //esc.addPrintAndLineFeed();
        } else {
          StringBuilder sb = new StringBuilder();
          if (details.getDbFormatInfo() != null) {
            sb.append(standard);
          }
          if (details.getDbMethodInfo() != null) {
            sb.append((details.getDbFormatInfo() == null) ? method : "   " + method);
          }
          esc.addText(sb.toString());
          esc.addPrintAndLineFeed();
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * 商品真实实体数据(仅用于已结账单的财务联和客户联)(修改版)
   */
  @Deprecated
  private static void getProductDataWithModify(EscCommand esc, List<PxOrderDetails> detailsList) {
    try {
      //商品实体
      for (PxOrderDetails details : detailsList) {
        //未下单的不打印
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_UNORDER)) {
          continue;
        }
        //品名长度
        int lengthNameValue;
        //单价长度
        int lengthPriceValue;
        //小计长度
        int lengthTotalValue;
        //数量长度
        int lengthNumberValue;
        //品名和单价中间空格字符数
        int lengthNameValueAndPriceValue;
        //单价和小计中间空格字符数
        int lengthPriceValueAndTotalValue;
        //小计和数量中间空格字符数
        int lengthTotalValueAndNumberValue;
        //品名和单价中间空格字符
        String nameValueAndPriceValueBlank = "";
        //单价和小计中间空格字符
        String namePriceAndTotalValueBlank = "";
        //小计和数量中间空格字符
        String nameTotalAndNumberValueBlank = "";
        //一条数据字符串
        String oneValue = "";
        //赋值一种商品金额
        double moneyFormat = 0;
        //赋值做法
        String method = "";
        //赋值规格
        String standard = "";
        //赋值订货单位
        String number;
        //赋值结账单位
        String numberMultiple;
        //赋值商品状态
        String status = "";
        //是否为赠品
        String isGift = "";
        //商品状态(未下单,已下单,退货)
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
          status = "";
        } else if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
          status = "(退菜)";
        } else {
          status = "(未下)";
        }
        //价格
        moneyFormat = details.getPrintPrice();
        //规格
        if (details.getDbFormatInfo() == null) {
          standard = "";
        } else {
          standard = details.getDbFormatInfo().getName();
        }
        //做法
        if (details.getDbMethodInfo() == null) {
          method = "";
        } else {
          method = details.getDbMethodInfo().getName();
        }
        //双单位
        if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + details.getDbProduct().getOrderUnit() + ")";
          numberMultiple =
              NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber()) + "("
                  + details.getDbProduct().getUnit() + ")";
        } else {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + details.getDbProduct().getUnit() + ")";
          numberMultiple = "";
        }
        //是否为赠品
        if (details.getIsGift().equals("0")) {//不是赠品
          isGift = "";
        } else {//是赠品
          isGift = "(赠)";
        }
        //单价
        double unitPrice = 0;
        //规格单价计算
        PxOrderInfo orderInfo = details.getDbOrder();
        if (orderInfo.getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
          if (details.getDbFormatInfo() != null){
            PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
                .queryBuilder()
                .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
                .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(details.getDbProduct().getId()))
                .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(details.getDbFormatInfo().getId()))
                .unique();
            unitPrice = (rel == null ? details.getDbProduct().getVipPrice() : rel.getVipPrice());
          }else{
            unitPrice = details.getDbProduct().getVipPrice();
          }
        } else {
          if (details.getDbFormatInfo() != null){
            PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
                .queryBuilder()
                .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
                .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(details.getDbProduct().getId()))
                .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(details.getDbFormatInfo().getId()))
                .unique();
            unitPrice = (rel == null ? details.getDbProduct().getPrice() : rel.getPrice());
          }else{
            unitPrice = details.getDbProduct().getPrice();
          }
        }
        //双单位计算空格数并显示
        if(details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)){
          lengthNameValue = (details.getDbProduct().getName()+isGift).getBytes("GBK").length;
          //lengthNameValueAndPriceValue这个值可能会出现负数
          lengthNameValueAndPriceValue = 12-lengthNameValue;
          for (int i = 0; i < lengthNameValueAndPriceValue; i++) {
            nameValueAndPriceValueBlank += " ";
          }
          lengthPriceValue = NumberFormatUtils.formatFloatNumber(unitPrice).getBytes("GBK").length;
          //lengthPriceValueAndTotalValue这个值可能会出现负数
          lengthPriceValueAndTotalValue = 8-lengthPriceValue;
          for (int i = 0; i < lengthPriceValueAndTotalValue; i++) {
            namePriceAndTotalValueBlank += " ";
          }
          //lengthTotalValueAndNumberValue这个值可能会出现负数
          lengthTotalValue = NumberFormatUtils.formatFloatNumber(moneyFormat).getBytes("GBK").length;
          lengthTotalValueAndNumberValue = 7-lengthTotalValue;
          for (int i = 0; i < lengthTotalValueAndNumberValue; i++) {
            nameTotalAndNumberValueBlank += " ";
          }
          oneValue = details.getDbProduct().getName()
              +isGift+nameValueAndPriceValueBlank
              +NumberFormatUtils.formatFloatNumber(unitPrice)
              +namePriceAndTotalValueBlank
              +NumberFormatUtils.formatFloatNumber(moneyFormat)
              +nameTotalAndNumberValueBlank
              +numberMultiple + "/" + number;
        }else {
          lengthNameValue = (details.getDbProduct().getName()+isGift).getBytes("GBK").length;
          //lengthNameValueAndPriceValue这个值可能会出现负数
          lengthNameValueAndPriceValue = 12-lengthNameValue;
          for (int i = 0; i < lengthNameValueAndPriceValue; i++) {
            nameValueAndPriceValueBlank += " ";
          }
          lengthPriceValue = NumberFormatUtils.formatFloatNumber(unitPrice).getBytes("GBK").length;
          //lengthPriceValueAndTotalValue这个值可能会出现负数
          lengthPriceValueAndTotalValue = 8-lengthPriceValue;
          for (int i = 0; i < lengthPriceValueAndTotalValue; i++) {
            namePriceAndTotalValueBlank += " ";
          }
          //lengthTotalValueAndNumberValue这个值可能会出现负数
          lengthTotalValue = NumberFormatUtils.formatFloatNumber(moneyFormat).getBytes("GBK").length;
          lengthTotalValueAndNumberValue = 7-lengthTotalValue;
          for (int i = 0; i < lengthTotalValueAndNumberValue; i++) {
            nameTotalAndNumberValueBlank += " ";
          }
          oneValue = details.getDbProduct().getName()
              +isGift+nameValueAndPriceValueBlank
              +NumberFormatUtils.formatFloatNumber(unitPrice)
              +namePriceAndTotalValueBlank
              +NumberFormatUtils.formatFloatNumber(moneyFormat)
              +nameTotalAndNumberValueBlank
              +number;
        }
        esc.addText(oneValue);
        esc.addPrintAndLineFeed();
        //折扣率 不等于100时显示折扣率  商品状态
        if ((details.getDiscountRate() != 100) || !status.equals("")) {
          String content = (details.getDiscountRate() == 100) ? status
              : details.getDiscountRate() + "%    " + status;
          esc.addText(content);
          esc.addPrintAndLineFeed();
        }
        //规格做法显示
        if (details.getDbMethodInfo() == null && details.getDbFormatInfo() == null) {
          //esc.addPrintAndLineFeed();
        } else {
          StringBuilder sb = new StringBuilder();
          if (details.getDbFormatInfo() != null) {
            sb.append(standard);
          }
          if (details.getDbMethodInfo() != null) {
            sb.append((details.getDbFormatInfo() == null) ? method : "   " + method);
          }
          esc.addText(sb.toString());
          esc.addPrintAndLineFeed();
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }



  /**
   * 合并后的 商品真实实体数据(仅用于已结账单的财务联和客户联)(修改版)
   */
  private static void getProductDataWithModifyMerge(EscCommand esc, List<PxOrderDetails> detailsList) {
    try {
      //商品实体
      for (PxOrderDetails details : detailsList) {
        PxFormatInfo dbFormatInfo = details.getPrintFormat();
        PxMethodInfo dbMethodInfo = details.getPrintMethod();
        PxProductInfo dbProduct = details.getPrintProd();
        PxOrderInfo orderInfo = details.getPrintOrder();
        double refundNum = details.getRefundNum();
        double refundMultNum = details.getRefundMultNum();

        //未下单的不打印
        if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_UNORDER)) continue;
        ////商品状态(未下单,已下单,退货)
        //String orderStatus = "";
        //if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
        //  orderStatus = "(退菜)";
        //}
        //品名长度
        int lengthNameValue;
        //单价长度
        int lengthPriceValue;
        //小计长度
        int lengthTotalValue;
        //数量长度
        int lengthNumberValue;
        //品名和单价中间空格字符数
        int lengthNameValueAndPriceValue;
        //单价和小计中间空格字符数
        int lengthPriceValueAndTotalValue;
        //小计和数量中间空格字符数
        int lengthTotalValueAndNumberValue;
        //品名和单价中间空格字符
        String nameValueAndPriceValueBlank = "";
        //单价和小计中间空格字符
        String namePriceAndTotalValueBlank = "";
        //小计和数量中间空格字符
        String nameTotalAndNumberValueBlank = "";
        //一条数据字符串
        String oneValue = "";
        //赋值做法
        String method = "";
        //赋值规格
        String standard = "";
        //赋值订货单位
        String number;
        //赋值结账单位
        String numberMultiple;
        //是否为赠品
        String isGift = "";

        //规格
        if (dbFormatInfo == null) {
          standard = "";
        } else {
          standard = dbFormatInfo.getName();
        }
        //做法
        if (dbMethodInfo == null) {
          method = "";
        } else {
          method = dbMethodInfo.getName();
        }
        //双单位

        boolean isMultiUnit = dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE);
        if (isMultiUnit) {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + dbProduct.getOrderUnit() + ")";
          numberMultiple =
              NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber()) + "("
                  + dbProduct.getUnit() + ")";
        } else {
          number = (details.getNum() + "").substring(0, (details.getNum() + "").indexOf(".")) + "("
              + dbProduct.getUnit() + ")";
          numberMultiple = "";
        }
        //是否为赠品
        if (details.getIsGift().equals("0")) {//不是赠品
          isGift = "";
        } else {//是赠品
          isGift = "(赠)";
        }
        //单价
        double unitPrice = details.getUnitPrice();
        double moneyFormat = details.getPrice() * details.getDiscountRate() /100;
        //规格单价计算

        if (orderInfo.getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
          unitPrice = details.getUnitVipPrice();
          moneyFormat = details.getVipPrice() * details.getDiscountRate() / 100;
        }

        //双单位计算空格数并显示
        if(isMultiUnit){
          lengthNameValue = (dbProduct.getName()+isGift).getBytes("GBK").length;
          //lengthNameValueAndPriceValue这个值可能会出现负数
          lengthNameValueAndPriceValue = 12-lengthNameValue;
          for (int i = 0; i < lengthNameValueAndPriceValue; i++) {
            nameValueAndPriceValueBlank += " ";
          }
          lengthPriceValue = NumberFormatUtils.formatFloatNumber(unitPrice).getBytes("GBK").length;
          //lengthPriceValueAndTotalValue这个值可能会出现负数
          lengthPriceValueAndTotalValue = 8-lengthPriceValue;
          for (int i = 0; i < lengthPriceValueAndTotalValue; i++) {
            namePriceAndTotalValueBlank += " ";
          }
          //lengthTotalValueAndNumberValue这个值可能会出现负数
          lengthTotalValue = NumberFormatUtils.formatFloatNumber(moneyFormat).getBytes("GBK").length;
          lengthTotalValueAndNumberValue = 7-lengthTotalValue;
          for (int i = 0; i < lengthTotalValueAndNumberValue; i++) {
            nameTotalAndNumberValueBlank += " ";
          }
          oneValue = dbProduct.getName()
              +isGift+nameValueAndPriceValueBlank
              +NumberFormatUtils.formatFloatNumber(unitPrice)
              +namePriceAndTotalValueBlank
              +NumberFormatUtils.formatFloatNumber(moneyFormat)
              +nameTotalAndNumberValueBlank
              +numberMultiple + "/" + number;
        }else {
          lengthNameValue = (dbProduct.getName()+isGift).getBytes("GBK").length;
          //lengthNameValueAndPriceValue这个值可能会出现负数
          lengthNameValueAndPriceValue = 12-lengthNameValue;
          for (int i = 0; i < lengthNameValueAndPriceValue; i++) {
            nameValueAndPriceValueBlank += " ";
          }
          lengthPriceValue = NumberFormatUtils.formatFloatNumber(unitPrice).getBytes("GBK").length;
          //lengthPriceValueAndTotalValue这个值可能会出现负数
          lengthPriceValueAndTotalValue = 8-lengthPriceValue;
          for (int i = 0; i < lengthPriceValueAndTotalValue; i++) {
            namePriceAndTotalValueBlank += " ";
          }
          //lengthTotalValueAndNumberValue这个值可能会出现负数
          lengthTotalValue = NumberFormatUtils.formatFloatNumber(moneyFormat).getBytes("GBK").length;
          lengthTotalValueAndNumberValue = 7-lengthTotalValue;
          for (int i = 0; i < lengthTotalValueAndNumberValue; i++) {
            nameTotalAndNumberValueBlank += " ";
          }
          oneValue = dbProduct.getName()
              +isGift+nameValueAndPriceValueBlank
              +NumberFormatUtils.formatFloatNumber(unitPrice)
              +namePriceAndTotalValueBlank
              +NumberFormatUtils.formatFloatNumber(moneyFormat)
              +nameTotalAndNumberValueBlank
              +number;
        }
        esc.addText(oneValue);
        esc.addPrintAndLineFeed();
        //折扣率 不等于100时显示折扣率  商品状态
        if (details.getDiscountRate() != 100) {
          String content = details.getDiscountRate() + "%    ";
          esc.addText(content);
          esc.addPrintAndLineFeed();
        }
        //规格做法显示
        if (dbMethodInfo == null && dbFormatInfo == null) {
          //esc.addPrintAndLineFeed();
        } else {
          StringBuilder sb = new StringBuilder();
          if (dbFormatInfo != null) {
            sb.append(standard);
          }
          if (dbMethodInfo != null) {
            sb.append((dbFormatInfo == null) ? method : "   " + method);
          }
          esc.addText(sb.toString());
          esc.addPrintAndLineFeed();
        }
        //已退数量
        //已退数量
        if (refundNum > 0 || refundMultNum > 0){
          String refundMultiNumAndUnit = refundMultNum + dbProduct.getUnit() +"/" + refundNum + dbProduct.getOrderUnit() +")";
          String refunNumAndUnit = refundNum + dbProduct.getUnit() + ")";
          esc.addText("   (已退:" + (isMultiUnit ? refundMultiNumAndUnit : refunNumAndUnit));
          esc.addPrintAndLineFeed();
        }

      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }





  /**
   * 点菜单总价
   */
  private static void getOrderInfoAmount(EscCommand esc, List<PxOrderDetails> detailsList) {
    double countMoney = 0;
    if (detailsList == null || detailsList.size() == 0) return;
    PxOrderInfo dbOrder = detailsList.get(0).getDbOrder();
    //商品实体
    for (PxOrderDetails details : detailsList) {
      if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
        if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(dbOrder.getUseVipCard())) {
          countMoney += (details.getVipPrice() * details.getDiscountRate()) / 100;
        } else {
          countMoney += (details.getPrice() * details.getDiscountRate()) / 100;
        }
      }
    }
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(PrinterConstant.TOTAL_AMOUNT + NumberFormatUtils.formatFloatNumber(countMoney));
    esc.addPrintAndLineFeed();
  }

  /**
   * 结账单(客户联)总价,优惠,折后等等
   */
  private static void getBillWithCustomInfoAmount(EscCommand esc, AppCustomAmount mCustomAmount,
      PxOrderInfo mOrderInfo) {
    //总金额
    double countMoney = mCustomAmount.getCountMoney();
    //应收金额
    double mReceivableAmount = mCustomAmount.getReceivableAmount();
    //实收金额
    double mActualAmount = mCustomAmount.getActualAmount();
    //消费金额
    double mConsumeAmount = mCustomAmount.getConsumeAmount();
    //优惠金额
    double mDiscAmount = mCustomAmount.getDiscAmount();
    //找零金额
    double mChangeAmount = mCustomAmount.getChangeAmount();
    //附加费
    double mSurchargeAmount = mCustomAmount.getSurchargeAmount();
    //抹零
    double mTailMoney = mOrderInfo.getTailMoney() * -1;
    //支付类优惠
    double payPrivilege = mOrderInfo.getPayPrivilege();
    if (countMoney > 0.0) {
      //总金额
      String moneyCount =
          PrinterConstant.TOTAL_AMOUNT + NumberFormatUtils.formatFloatNumber(countMoney);
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(moneyCount);
      esc.addPrintAndLineFeed();
    }
    if (mReceivableAmount > 0.0) {
      //应收金额
      String amountReceivable =
          PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount);
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(amountReceivable);
      esc.addPrintAndLineFeed();
    }
    //实收金额
    if (mActualAmount > 0.0) {
      String amountActual =
          PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mActualAmount);
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(amountActual);
      esc.addPrintAndLineFeed();
    }
    if (mDiscAmount > 0.0) {
      //优惠金额
      String amountDisc =
          PrinterConstant.DISCOUNT_AMOUNT + NumberFormatUtils.formatFloatNumber(mDiscAmount);
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(amountDisc);
      esc.addPrintAndLineFeed();
    }
    //支付类优惠金额
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
    esc.addPrintAndLineFeed();
    if (mChangeAmount > 0.0) {
      //找零金额
      String amountChange =
          PrinterConstant.CHANGE_CASH + NumberFormatUtils.formatFloatNumber(mChangeAmount);
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(amountChange);
      esc.addPrintAndLineFeed();
    }
    if (mTailMoney > 0.0) {
      //损益金额
      String tailMoney = PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(mTailMoney);
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(tailMoney);
      esc.addPrintAndLineFeed();
    }
    if (mSurchargeAmount > 0.0) {
      //附加费金额
      String amountSurcharge =
          PrinterConstant.SURCHARGE_CASH + NumberFormatUtils.formatFloatNumber(mSurchargeAmount);
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(amountSurcharge);
      esc.addPrintAndLineFeed();
    }
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText("----实收详情----");
    esc.addPrintAndLineFeed();
    //各项实收
    everyReceived(mOrderInfo, esc);
    //免单
    freePayInfo(esc, mOrderInfo);
  }

  /**
   * 结账单(财务联)总价,优惠,折后等等
   */
  private static void getBillWithFinanceInfoAmount(EscCommand esc, PxOrderInfo mOrderInfo,
      AppFinanceAmount mFinanceAmount) {
    //总金额
    double mTotalAmount = mFinanceAmount.getmPayCashTotal();
    //应收金额
    double mReceivableAmount = mOrderInfo.getAccountReceivable();
    //实收金额
    double mActualAmount = mOrderInfo.getRealPrice();
    //找零
    double mChangeMoney = mOrderInfo.getTotalChange();
    //抹零
    double mTailMoney = mOrderInfo.getTailMoney();
    //优惠金额
    double mDisMoney = mOrderInfo.getDiscountPrice();
    //支付类优惠
    double payPrivilege = mOrderInfo.getPayPrivilege();
    //总金额
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(PrinterConstant.TOTAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mTotalAmount));
    esc.addPrintAndLineFeed();
    //应收金额
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(
        PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount));
    esc.addPrintAndLineFeed();
    //实收金额
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(mActualAmount));
    esc.addPrintAndLineFeed();
    if (mChangeMoney > 0.0) {
      //找零金额
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(PrinterConstant.CHANGE_CASH + NumberFormatUtils.formatFloatNumber(mChangeMoney));
      esc.addPrintAndLineFeed();
    }
    if (mTailMoney > 0.0) {
      //抹零金额
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(PrinterConstant.TAIL_ZERO_CASH + NumberFormatUtils.formatFloatNumber(mTailMoney));
      esc.addPrintAndLineFeed();
    }
    if (mDisMoney > 0.0) {
      //优惠金额
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(PrinterConstant.DISCOUNT_AMOUNT + NumberFormatUtils.formatFloatNumber(mDisMoney));
      esc.addPrintAndLineFeed();
    }
    //支付类优惠金额
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
    esc.addPrintAndLineFeed();

    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText("----实收详情----");
    esc.addPrintAndLineFeed();
    //各项实收
    everyReceived(mOrderInfo, esc);
    //免单
    freePayInfo(esc, mOrderInfo);
  }

  /**
   * 仅供账单详情应收,实收,找零,抹零
   */
  private static void getRecMoney(EscCommand esc, PxOrderInfo mOrderInfo) {
    //应收金额
    double mReceivableAmount = mOrderInfo.getAccountReceivable();
    //抹零金额
    double mTailAmount = mOrderInfo.getTailMoney();
    //找零金额
    double mChangeAmount = mOrderInfo.getTotalChange();
    //附加费
    double mSurchargeAmount = mOrderInfo.getExtraMoney();
    //支付类优惠
    double payPrivilege = mOrderInfo.getPayPrivilege();
    //应收金额
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(
        PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(mReceivableAmount));
    esc.addPrintAndLineFeed();
    if (mChangeAmount > 0.0) {
      //找零金额
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(PrinterConstant.CHANGE_CASH + NumberFormatUtils.formatFloatNumber(mChangeAmount));
      esc.addPrintAndLineFeed();
    }
    if (mTailAmount > 0.0) {
      //抹零金额
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(PrinterConstant.TAIL_CASH + NumberFormatUtils.formatFloatNumber(mTailAmount));
      esc.addPrintAndLineFeed();
    }
    //支付类优惠金额
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(payPrivilege));
    esc.addPrintAndLineFeed();

    if (mSurchargeAmount > 0.0) {
      //附加费
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
      esc.addText(
          PrinterConstant.SURCHARGE_CASH + NumberFormatUtils.formatFloatNumber(mSurchargeAmount));
      esc.addPrintAndLineFeed();
    }
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText("----实收详情----");
    esc.addPrintAndLineFeed();
    //各项实收
    everyReceived(mOrderInfo, esc);
    //免单
    freePayInfo(esc, mOrderInfo);
  }

  /**
   * 合计项
   */
  private static void getSumNumber(EscCommand esc, List<PxOrderDetails> detailsList) {
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高
    esc.addText(PrinterConstant.COLLECT);
    esc.addPrintAndLineFeed();
    esc.addText(detailsList.size() + "项");
    esc.addPrintAndLineFeed();
  }

  /**
   * 打印时间字样和值
   */
  private static void getTimeAndValue(EscCommand esc) {
    try {
      int lengthTime = PrinterConstant.PRINT_TIME.getBytes("GBK").length;
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String printTimeValue = simpleDateFormat.format(new Date());
      int lengthTimeValue = printTimeValue.getBytes("GBK").length;
      int lengthTimeAndValue = 32 - (lengthTime + lengthTimeValue);
      String timeAndValueBlank = "";
      for (int i = 0; i < lengthTimeAndValue; i++) {
        timeAndValueBlank += " ";
      }
      String timeAndValue = PrinterConstant.PRINT_TIME + timeAndValueBlank + printTimeValue;
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高
      esc.addText(timeAndValue);
      esc.addPrintAndLineFeed();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  ///**
  // * 切纸
  // */
  //public static void cutPaper(ReceiptCommand esc) {
  //  esc.addCutPaper();
  //}

  /**
   * USB打印点菜单
   */
  public static void printOrderInfo(GpService mGpService, PxOrderInfo mOrderInfo,
      List<PxOrderDetails> detailsList) {
    EscCommand esc = new EscCommand();
    esc.addInitializePrinter();
    esc.addPrintAndFeedLines((byte) 3);
    //餐厅名字
    getOfficeName(esc);
    //点菜单
    getTypeName(esc, PrinterConstant.POINT_MENU);
    //分割线
    getDividerLineWithLeft(esc);
    //桌号和单号
    getTableIdAndUniqueIdWithLarge(esc, mOrderInfo);
    //账单号
    getBillId(esc, mOrderInfo);
    //人数
    getPeopleNumber(esc, mOrderInfo);
    //服务员和单位
    getWaiterAndUnit(esc, mOrderInfo);
    //分割线
    getDividerLine(esc);
    //品名(金额)和数量/单位
    //getProductProperty(esc);
    getProductPropertyModify(esc);
    //分割线
    getDividerLine(esc);
    //商品真实实体数据
    //getProductData(esc, detailsList, "0");
    //getProductDataModify(esc, detailsList, "0");
    List<PxOrderDetails> mergeDbDetailsList = MergePrintDetails.mergeDbDetailsList(mOrderInfo);
    getProductDataModifyMerge(esc,mergeDbDetailsList,"0");
    //分割线
    getDividerLine(esc);
    //总价
    getOrderInfoAmount(esc, detailsList);
    //分割线
    getCancelWeightAndHeight(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.CASH_BILL_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * USB打印结账单(客户联)
   */
  public static void printBillWithCustomInfo(GpService mGpService, PxOrderInfo mOrderInfo, List<PxOrderDetails> detailsList, AppCustomAmount mCustomAmount) {
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //餐厅名字
    getOfficeName(esc);
    //餐厅结账单(客户联)
    getTypeName(esc, PrinterConstant.STATE_MENU_CUSTOM);
    //分割线
    getDividerLineWithLeft(esc);
    //桌号和单号
    getTableIdAndUniqueIdWithLarge(esc, mOrderInfo);
    //账单号
    getBillId(esc, mOrderInfo);
    //人数
    getPeopleNumber(esc, mOrderInfo);
    //服务员和单位
    getWaiterAndUnit(esc, mOrderInfo);
    //分割线
    getDividerLine(esc);
    ////点单明细
    //esc.addText("----" + PrinterConstant.BILL_DETAIL + "----");
    //esc.addPrintAndLineFeed();
    //品名(金额)和数量/单位
    //getProductProperty(esc);
    getProductPropertyModify(esc);
    //分割线
    getDividerLine(esc);
    //商品真实实体数据
    //getProductData(esc, detailsList, "1");
    //getProductDataModify(esc, detailsList, "1");
    List<PxOrderDetails> mergeDbDetailsList = MergePrintDetails.mergeDbDetailsList(mOrderInfo);
    getProductDataModifyMerge(esc,mergeDbDetailsList,"1");
    //分割线
    getDividerLine(esc);
    //分类统计
    esc.addText("----" + PrinterConstant.CLASS_STATISTICS + "----");
    esc.addPrintAndLineFeed();
    //总金额,折后,优惠等等
    getBillWithCustomInfoAmount(esc, mCustomAmount, mOrderInfo);
    //分割线
    getCancelWeightAndHeight(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.CUSTOM_BILL_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
  //
  ///**
  // * USB打印结账单(客户联)仅供配置后快速打印客户联
  // */
  //public static void autoPrintBillWithCustomInfo(JBService mJbService, int mPrinterId,
  //    PxOrderInfo mOrderInfo, List<PxOrderDetails> detailsList, Office mOffice) {
  //  ReceiptCommand esc = new ReceiptCommand();
  //  //开始间距
  //  esc.addPrintAndFeedLines((byte) 3);
  //  //餐厅名字
  //  getOfficeName(esc, mOffice);
  //  //餐厅结账单(客户联)
  //  getTypeName(esc, PrinterConstant.STATE_MENU_CUSTOM);
  //  //分割线
  //  getDividerLineWithLeft(esc);
  //  //桌号和单号
  //  getTableIdAndUniqueIdWithLarge(esc, mOrderInfo);
  //  //账单号
  //  getBillId(esc, mOrderInfo);
  //  //人数
  //  getPeopleNumber(esc, mOrderInfo);
  //  //服务员和单位
  //  getWaiterAndUnit(esc, mOrderInfo);
  //  //分割线
  //  getDividerLine(esc);
  //  //点单明细
  //  esc.addText("----" + PrinterConstant.BILL_DETAIL + "----");
  //  esc.addPrintAndLineFeed();
  //  //品名(金额)和数量/单位
  //  //getProductProperty(esc);
  //  getProductPropertyModify(esc);
  //  //分割线
  //  getDividerLine(esc);
  //  //商品真实实体数据
  //  //getProductData(esc, detailsList, "1");
  //  getProductDataModify(esc, detailsList, "1");
  //  //分割线
  //  getDividerLine(esc);
  //  //分类统计
  //  esc.addText("----" + PrinterConstant.CLASS_STATISTICS + "----");
  //  esc.addPrintAndLineFeed();
  //  //总金额
  //  getOrderInfoAmount(esc, detailsList);
  //  //分割线
  //  getCancelWeightAndHeight(esc);
  //  //打印时间字样和值
  //  getTimeAndValue(esc);
  //  //开始间距
  //  esc.addPrintAndFeedLines((byte) 3);
  //
  //  Vector<Byte> vector = esc.getCommand();
  //  final byte[] data = getData(vector);
  //  try {
  //    mJbService.sendReceiptCommand(mPrinterId, data);
  //  } catch (RemoteException e) {
  //    e.printStackTrace();
  //  }
  //}
  //
  ///**
  // * 配置后打印退菜单(相对于单个菜而言)
  // */
  //public static void autoPrintRefundDish(JBService mJbService, int mPrinterId,
  //    PxOrderInfo mOrderInfo, PxOrderDetails orderDetails, Office mOffice) {
  //  ReceiptCommand esc = new ReceiptCommand();
  //  //开始间距
  //  esc.addPrintAndFeedLines((byte) 3);
  //  //餐厅名字
  //  getOfficeName(esc, mOffice);
  //  //餐厅退菜单
  //  getTypeName(esc, PrinterConstant.REFUND_DETAILS);
  //  //分割线
  //  getDividerLineWithLeft(esc);
  //  //桌号和单号
  //  getTableIdAndUniqueId(esc, mOrderInfo);
  //  //账单号
  //  getBillId(esc, mOrderInfo);
  //  //人数
  //  getPeopleNumber(esc, mOrderInfo);
  //  //服务员和单位
  //  getWaiterAndUnit(esc, mOrderInfo);
  //  //分割线
  //  getDividerLine(esc);
  //  //品名(金额)和数量/单位
  //  getProductProperty(esc);
  //  //分割线
  //  getDividerLine(esc);
  //  //分割线
  //  getDividerLine(esc);
  //  //商品真实实体数据
  //  getRefundProductData(esc, orderDetails);
  //  //分割线
  //  getCancelWeightAndHeight(esc);
  //  //打印时间字样和值
  //  getTimeAndValue(esc);
  //  //开始间距
  //  esc.addPrintAndFeedLines((byte) 3);
  //
  //  Vector<Byte> vector = esc.getCommand();
  //  final byte[] data = getData(vector);
  //  try {
  //    mJbService.sendReceiptCommand(mPrinterId, data);
  //  } catch (RemoteException e) {
  //    e.printStackTrace();
  //  }
  //}

  /**
   * 开启钱箱
   */
  public static void openDrawer(GpService mGpService) {
    EscCommand esc = new EscCommand();
    //开启钱箱
    esc.addGeneratePluseAtRealtime(LabelCommand.FOOT.F2, (byte) 8); //立即开钱箱
    Vector<Byte> vector = esc.getCommand();
    byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.OPEN_DRAWER_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * USB打印结账单(财务联)
   */
  public static void printBillWithFinanceInfo(GpService mGpService, PxOrderInfo mOrderInfo,
      List<PxOrderDetails> detailsList, AppFinanceAmount mFinanceAmount) {
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //餐厅名字
    getOfficeName(esc);
    //餐厅结账单(财务联)
    getTypeName(esc, PrinterConstant.STATE_MENU_FINANCE);
    //分割线
    getDividerLineWithLeft(esc);
    //桌号和单号
    getTableIdAndUniqueIdWithLarge(esc, mOrderInfo);
    //账单号
    getBillId(esc, mOrderInfo);
    //人数
    getPeopleNumber(esc, mOrderInfo);
    //服务员和单位
    getWaiterAndUnit(esc, mOrderInfo);
    //分割线
    getDividerLine(esc);
    //点单明细
    //esc.addText("----" + PrinterConstant.BILL_DETAIL + "----");
    //esc.addPrintAndLineFeed();
    //品名(金额)和数量/单位
    //getProductProperty(esc);
    getProductPropertyModify(esc);
    //分割线
    getDividerLine(esc);
    //商品真实实体数据
    //getProductData(esc, detailsList, "1");
    //getProductDataModify(esc, detailsList, "1");
    List<PxOrderDetails> mergeDbDetailsList = MergePrintDetails.mergeDbDetailsList(mOrderInfo);
    getProductDataModifyMerge(esc,mergeDbDetailsList,"1");
    //--分类统计
    categoryCollect(esc,mOrderInfo);
    //应收,实收,找零
    getBillWithFinanceInfoAmount(esc, mOrderInfo, mFinanceAmount);
    //分割线
    getCancelWeightAndHeight(esc);
    ////操作员
    //esc.addText(PrinterConstant.DISH_PRINTER + PrinterConstant.WAITER_VALUE);
    //esc.addPrintAndLineFeed();
    //分割线
    getCancelWeightAndHeight(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.FINANCE_BILL_PORT));
      }
    } catch (RemoteException e) {
      Logger.e(e.toString());
      e.printStackTrace();
    }
  }

  /**
   * USB打印销售统计
   */
  public static void printOverBillSaleListInfo(GpService mGpService, PxProductCategory mCategory,
      List<AppSaleContent> mSaleContentList) {
    try {
      EscCommand esc = new EscCommand();
      //开始间距
      esc.addPrintAndFeedLines((byte) 3);
      //餐厅名字
      getOfficeName(esc);
      //菜类点菜统计
      getTypeName(esc, PrinterConstant.SALE_ACCOUNT_MENU);
      //分割线
      getDividerLineWithLeft(esc);
      //统计时间时间
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String time = PrinterConstant.DISH_STATISTICS_TIME + simpleDateFormat.format(new Date());
      esc.addText(time);
      esc.addPrintAndLineFeed();
      //分割线
      getDividerLine(esc);
      //分类名
      esc.addText(PrinterConstant.DISH_CATEGORY + mCategory.getName());
      esc.addPrintAndLineFeed();
      //分割线
      getDividerLine(esc);
      //品名(金额)和数量/单位
      int lengthName = PrinterConstant.COMMODITY_NAME.getBytes("GBK").length;
      int lengthNumber =
          (PrinterConstant.NUMBER + "/" + PrinterConstant.UNIT).getBytes("GBK").length;
      int lengthNameAndNumber = 32 - (lengthName + lengthNumber);
      String nameAndNumberBlank = "";
      for (int i = 0; i < lengthNameAndNumber; i++) {
        nameAndNumberBlank += " ";
      }
      String nameAndNumber =
          PrinterConstant.COMMODITY_NAME + nameAndNumberBlank + (PrinterConstant.NUMBER + "/"
              + PrinterConstant.UNIT);
      esc.addText(nameAndNumber);
      esc.addPrintAndLineFeed();

      //销量实体
      for (AppSaleContent appSaleContent : mSaleContentList) {
        //品名(金额)长度
        int lengthNameValue;
        //数量/单位长度
        int lengthNumberValue;
        //中间空格字符数
        int lengthNameValueAndNumberValue;
        //中间空格字符
        String nameValueAndNumberValueBlank = "";
        //一条数据字符串
        String nameValueAndNumberValue = "";
        lengthNameValue = (appSaleContent.getProdName()).getBytes("GBK").length;

        String numAndUnit = appSaleContent.isMultUnitProd() ? (appSaleContent.getSaleMultNumber()+appSaleContent.getUnit())
            : (appSaleContent.getSaleNumber() +"");
        lengthNumberValue = (numAndUnit).getBytes("GBK").length;
        lengthNameValueAndNumberValue = 32 - (lengthNameValue + lengthNumberValue);
        for (int i = 0; i < lengthNameValueAndNumberValue; i++) {
          nameValueAndNumberValueBlank += " ";
        }
        nameValueAndNumberValue = appSaleContent.getProdName() + nameValueAndNumberValueBlank + numAndUnit;
        esc.addText(nameValueAndNumberValue);
        esc.addPrintAndLineFeed();
      }
      //分割线
      getDividerLine(esc);
      ////打印员
      //esc.addText(PrinterConstant.DISH_PRINTER + PrinterConstant.WAITER_VALUE);
      //esc.addPrintAndLineFeed();
      //分割线
      getDividerLine(esc);
      //打印时间字样和值
      getTimeAndValue(esc);
      //开始间距
      esc.addPrintAndFeedLines((byte) 3);

      Vector<Byte> vector = esc.getCommand();
      final byte[] data = GpUtils.ByteTo_byte(vector);
      String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
      int sendInstruction;
      try {
        sendInstruction = mGpService.sendEscCommand(1,encodeString);
        GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
        if (r != GpCom.ERROR_CODE.SUCCESS) {
          EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.SALE_CONNECT_PORT));
        }
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * USB打印收银员账单汇总
   */
  //@formatter:off
  //@formatter:on
  public static void printBillCollectInfo(GpService mGpService,
      CollectionContentEvent mContentEvent, AppBillCount mAppBillCountOffice) {
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //餐厅名字
    getOfficeName(esc);
    //收银员账单汇总
    getTypeName(esc, PrinterConstant.CASHIER_COLLECT);
    //分割线
    getDividerLineWithLeft(esc);
    //统计时间
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time = PrinterConstant.DISH_STATISTICS_TIME + simpleDateFormat.format(new Date());
    esc.addText(time);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //收银员字样
    esc.addText(PrinterConstant.CASHIER + mContentEvent.getUser().getName());
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //应收字样
    esc.addText(PrinterConstant.RECEIVABLE + mAppBillCountOffice.getTotalReceivable());
    esc.addPrintAndLineFeed();
    //找零
    esc.addText("找零:" + mAppBillCountOffice.getTotalChange());
    esc.addPrintAndLineFeed();
    //抹零
    esc.addText("抹零:" + mAppBillCountOffice.getTotalTail());
    esc.addPrintAndLineFeed();
    //支付类优惠
    esc.addText("支付类优惠:" + mAppBillCountOffice.getPayPrivilege());
    esc.addPrintAndLineFeed();
    //各项实收
    esc.addText("----------实收详情---------");
    esc.addPrintAndLineFeed();
    List<Pair<String, String>> everyReceived = mAppBillCountOffice.getEveryReceived();
    for (Pair<String, String> pair : everyReceived) {
      esc.addText(pair.first + ":" + pair.second);
      esc.addPrintAndLineFeed();
    }
    //分割线
    getDividerLine(esc);
    ////打印员
    //esc.addText(PrinterConstant.DISH_PRINTER + PrinterConstant.WAITER_VALUE);
    //esc.addPrintAndLineFeed();
    //打印时间字样和值
    getTimeAndValue(esc);
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data, Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1, encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.BILL_CONNECT_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
  //@formatter:off
  /**
   * USB打印收银员账单明细(客户联)
   */
  public static void printBillDetailInfoWithCustomer(GpService mGpService, DetailsContentEvent mDetailsContent,
      List<PxOrderDetails> detailsList) {
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    PxOrderInfo orderInfo = mDetailsContent.getOrderInfo();
    //餐厅名字
    getOfficeName(esc);
    //餐厅账单明细
    getTypeName(esc, PrinterConstant.ACCOUNT_DETAIL_WITH_CUSTOMER);
    //分割线
    getDividerLineWithLeft(esc);
    //桌号和单号
    getTableIdAndUniqueIdWithLarge(esc, orderInfo);
    //账单号
    getBillId(esc, orderInfo);
    //人数
    getPeopleNumber(esc, orderInfo);
    //收银员和单位
    getWaiterAndUnit(esc, orderInfo);
    //分割线
    getDividerLine(esc);
    //商品真实实体数据(仅用于已结账单)
    //getProductDataWith(esc, detailsList);
    //getProductDataWithModify(esc, detailsList);
    List<PxOrderDetails> mergeDbDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
    getProductDataWithModifyMerge(esc,mergeDbDetailsList);
    //分割线
    getDividerLine(esc);
    //合计项
    getSumNumber(esc, detailsList);
    //分割线
    getDividerLine(esc);
    //仅供账单详情应收,实收,找零,抹零
    getRecMoney(esc, orderInfo);
    //分割线
    getDividerLine(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);
    esc.addCutPaper();

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.BILL_DETAIL_WITH_CUSTOM_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * USB打印收银员账单明细(财务联)
   */
  public static void printBillDetailInfoWithFinancing(GpService mGpService, DetailsContentEvent mDetailsContent,
      List<PxOrderDetails> detailsList) {
    if (detailsList == null || detailsList.isEmpty()) return;
    PxOrderInfo dbOrder = detailsList.get(0).getDbOrder();

    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    PxOrderInfo orderInfo = mDetailsContent.getOrderInfo();
    //餐厅名字
    getOfficeName(esc);
    //餐厅账单明细
    getTypeName(esc, PrinterConstant.ACCOUNT_DETAIL_WITH_FINANCING);
    //分割线
    getDividerLineWithLeft(esc);
    //桌号和单号
    getTableIdAndUniqueIdWithLarge(esc, orderInfo);
    //账单号
    getBillId(esc, orderInfo);
    //人数
    getPeopleNumber(esc, orderInfo);
    //收银员和单位
    getWaiterAndUnit(esc, orderInfo);
    //分割线
    getDividerLine(esc);
    //商品真实实体数据(仅用于已结账单)
    //getProductDataWith(esc, detailsList);
    //getProductDataWithModify(esc, detailsList);
     List<PxOrderDetails> mergeDbDetailsList = MergePrintDetails.mergeDbDetailsList(orderInfo);
    getProductDataWithModifyMerge(esc,mergeDbDetailsList);
    //--分类统计
    categoryCollect(esc,dbOrder);
    //合计项
    getSumNumber(esc, detailsList);
    //分割线
    getDividerLine(esc);
    //仅供账单详情应收,实收,找零,抹零
    getRecMoney(esc, orderInfo);
    //分割线
    getDividerLine(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.BILL_DETAIL_WITH_FINANCE_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  ///**
  // * 交接班（所有账单）
  // */
  //public static void printAllBillCollect(JBService mJbService, int mPrinterId,
  //    List<PxOrderInfo> mOrderInfoList, ShiftWork shiftWork, Office mOffice) {
  //  ReceiptCommand esc = new ReceiptCommand();
  //  //开始间距
  //  esc.addPrintAndFeedLines((byte) 3);
  //  //收银员交接班—所有账单
  //  String shiftName = PrinterConstant.SHIFT_WORK_ALL_BILLS;
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.CENTER);// 设置打印居中
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.ON, ReceiptCommand.ENABLE.ON, ReceiptCommand.ENABLE.OFF);// 设置为倍高倍宽
  //  esc.addText(shiftName);
  //  esc.addPrintAndLineFeed();
  //  esc.addPrintAndLineFeed();
  //  esc.addPrintAndLineFeed();
  //  //店铺名称
  //  String officeName = PrinterConstant.OFFICE_NAME + mOffice.getName();
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(officeName);
  //  esc.addPrintAndLineFeed();
  //  //收银员
  //  String shiftCashier = PrinterConstant.CASHIER + shiftWork.getCashierName();
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(shiftCashier);
  //  esc.addPrintAndLineFeed();
  //  //交接时间
  //  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  //  String shiftTimeValue = simpleDateFormat.format(new Date());
  //  String shiftTime = PrinterConstant.SHIFT_TIME + shiftTimeValue;
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(shiftTime);
  //  esc.addPrintAndLineFeed();
  //  //账单时间段
  //  String shiftOrderTime =
  //      PrinterConstant.SHIFT_TIME_ZONE + simpleDateFormat.format(shiftWork.getStartTime()) + "~"
  //          + simpleDateFormat.format(shiftWork.getEndTime());
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(shiftOrderTime);
  //  esc.addPrintAndLineFeed();
  //  //区域
  //  String shiftArea = PrinterConstant.WORK_ZONE + shiftWork.getWorkZone();
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(shiftArea);
  //  esc.addPrintAndLineFeed();
  //  //分割线
  //  getDividerLine(esc);
  //  //账单实体(PxOrderInfo)
  //  try {
  //    for (PxOrderInfo mPxOrderInfo : mOrderInfoList) {
  //      //账单号
  //      String shiftOrderNum = PrinterConstant.BILL_NO + mPxOrderInfo.getOrderNo();
  //      esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //      esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //      esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //          ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF,
  //          ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //      esc.addText(shiftOrderNum);
  //      esc.addPrintAndLineFeed();
  //      //桌台
  //      if (mPxOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
  //        TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
  //            .queryBuilder()
  //            .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mPxOrderInfo.getId()))
  //            .unique();
  //        PxTableInfo dbTable = unique.getDbTable();
  //        String shiftTableNum = PrinterConstant.TABLE_NUMBER + dbTable.getName();
  //        esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //        esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //        esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //            ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF,
  //            ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //        esc.addText(shiftTableNum);
  //        esc.addPrintAndLineFeed();
  //      } else {
  //        String shiftTableNum = PrinterConstant.TABLE_NUMBER + "零售单";
  //        esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //        esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //        esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //            ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF,
  //            ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //        esc.addText(shiftTableNum);
  //        esc.addPrintAndLineFeed();
  //      }
  //      //应收金额和优惠金额
  //      String shiftReceivableAndDiscountMoney =
  //          PrinterConstant.ACCEPT_AMOUNT + NumberFormatUtils.formatFloatNumber(
  //              mPxOrderInfo.getAccountReceivable()) + "     " + PrinterConstant.DISCOUNT_AMOUNT
  //              + NumberFormatUtils.formatFloatNumber(mPxOrderInfo.getDiscountPrice());
  //      esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //      esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //      esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //          ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF,
  //          ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //      esc.addText(shiftReceivableAndDiscountMoney);
  //      esc.addPrintAndLineFeed();
  //      //实收金额和抹零金额
  //      String shiftRealAndTailMoney =
  //          PrinterConstant.ACTUAL_AMOUNT + NumberFormatUtils.formatFloatNumber(
  //              mPxOrderInfo.getRealPrice()) + "     " + PrinterConstant.TAIL_CASH
  //              + NumberFormatUtils.formatFloatNumber(mPxOrderInfo.getTailMoney());
  //      esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //      esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //      esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //          ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF,
  //          ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //      esc.addText(shiftRealAndTailMoney);
  //      esc.addPrintAndLineFeed();
  //      //找零金额和总金额
  //      String shiftChangeAndTotalMoney =
  //          PrinterConstant.CHANGE_CASH + NumberFormatUtils.formatFloatNumber(
  //              mPxOrderInfo.getTotalChange()) + "     " + PrinterConstant.TOTAL_AMOUNT
  //              + NumberFormatUtils.formatFloatNumber(
  //              mPxOrderInfo.getAccountReceivable() + mPxOrderInfo.getDiscountPrice());
  //      esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //      esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //      esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //          ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF,
  //          ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //      esc.addText(shiftChangeAndTotalMoney);
  //      esc.addPrintAndLineFeed();
  //      //分割线
  //      getDividerLine(esc);
  //    }
  //  } catch (Exception e) {
  //    e.printStackTrace();
  //  }
  //  //账单数
  //  String shiftOrderSum = PrinterConstant.SHIFT_ORDER_SUM + (mOrderInfoList.size());
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(shiftOrderSum);
  //  esc.addPrintAndLineFeed();
  //  //实收金额（总）
  //  double realMoneySum = 0;
  //  for (PxOrderInfo orderInfo : mOrderInfoList) {
  //    if (orderInfo.getStatus().equals(PxOrderInfo.STATUS_FINISH)) {
  //      realMoneySum += orderInfo.getRealPrice();
  //    }
  //  }
  //  String shiftRealOrderSum =
  //      PrinterConstant.REAL_RECEIVE + NumberFormatUtils.formatFloatNumber(realMoneySum);
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(shiftRealOrderSum);
  //  esc.addPrintAndLineFeed();
  //  //分割线
  //  getDividerLine(esc);
  //  //交班收银员签字
  //  String shiftCashierSign = PrinterConstant.SHIFT_CASHIER_SIGN;
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.ON, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(shiftCashierSign);
  //  esc.addPrintAndLineFeed();
  //  //值班经理签字
  //  String shiftManagerSign = PrinterConstant.SHIFT_MANAGER_SIGN;
  //  esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
  //  esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
  //  esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
  //      ReceiptCommand.ENABLE.ON, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
  //  esc.addText(shiftManagerSign);
  //  esc.addPrintAndLineFeed();
  //  //分割线
  //  getDividerLine(esc);
  //  //打印时间字样和值
  //  getTimeAndValue(esc);
  //  //结束间距
  //  esc.addPrintAndFeedLines((byte) 3);
  //
  //  Vector<Byte> vector = esc.getCommand();
  //  final byte[] data = getData(vector);
  //  try {
  //    mJbService.sendReceiptCommand(mPrinterId, data);
  //  } catch (RemoteException e) {
  //    e.printStackTrace();
  //  }
  //}
  //
  /**
   * 交接班（所有分类）
   */
  public static void printAllCateCollect(GpService mGpService, List<AppShiftCateInfo> shiftCateInfoList,
      ShiftWork shiftWork) {
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //收银员交接班—分类汇总
    String shiftName = PrinterConstant.SHIFT_WORK_ALL_CATE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(shiftName);
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    //店铺名称
    String officeName = PrinterConstant.OFFICE_NAME + DaoServiceUtil.getOfficeDao().queryBuilder().unique().getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(officeName);
    esc.addPrintAndLineFeed();
    //收银员
    String shiftCashier = PrinterConstant.CASHIER + shiftWork.getCashierName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftCashier);
    esc.addPrintAndLineFeed();
    //交接时间
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String shiftTimeValue = simpleDateFormat.format(new Date());
    String shiftTime = PrinterConstant.SHIFT_TIME + shiftTimeValue;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftTime);
    esc.addPrintAndLineFeed();
    //区域
    String shiftArea = PrinterConstant.WORK_ZONE + shiftWork.getWorkZone();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftArea);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //名称（项目,数量,原价,实收）
    String name = PrinterConstant.SHIFT_PROJECT + "   " + PrinterConstant.NUMBER + "   "
        + PrinterConstant.SHIFT_RECEIVABLE_MONEY + "   " + PrinterConstant.SHIFT_REAL_MONEY;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(name);
    esc.addPrintAndLineFeed();
    //分类汇总实体(AppShiftCateInfo)
    //实收金额（总）
    int cateNumSum = 0;
    double cateReceivableMoneySum = 0;
    double cateActualMoneySum = 0;
    try {
      for (AppShiftCateInfo shiftCateInfo : shiftCateInfoList) {
        //计算分类名和数量之间的空格数
        String lengthNumber = "";
        int lengthCateAndNum = shiftCateInfo.getCateName().getBytes("GBK").length;
        int lengthCateAndNumValue = 8 - lengthCateAndNum;
        for (int i = 0; i < lengthCateAndNumValue; i++) {
          lengthNumber += " ";
        }
        //计算数量和原价之间的空格数
        String lengthReceivable = "";
        int lengthNumAndRecCate =
            String.valueOf(shiftCateInfo.getCateNumber()).getBytes("GBK").length;
        int lengthNumAndRecCateValue = 6 - lengthNumAndRecCate;
        for (int i = 0; i < lengthNumAndRecCateValue; i++) {
          lengthReceivable += " ";
        }
        //计算原价和实收之间的空格数
        String lengthReal = "";
        int lengthRecAndAct =
            NumberFormatUtils.formatFloatNumber(shiftCateInfo.getReceivableAmount())
                .getBytes("GBK").length;
        int lengthRecAndActValue = 8 - lengthRecAndAct;
        for (int i = 0; i < lengthRecAndActValue; i++) {
          lengthReal += " ";
        }

        //分类名,数量,原价,实收
        String nameValue =
            shiftCateInfo.getCateName() + lengthNumber + shiftCateInfo.getCateNumber()
                + lengthReceivable + NumberFormatUtils.formatFloatNumber(
                shiftCateInfo.getReceivableAmount()) + lengthReal
                + NumberFormatUtils.formatFloatNumber(shiftCateInfo.getActualAmount());
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addText(nameValue);
        esc.addPrintAndLineFeed();
        cateNumSum += shiftCateInfo.getCateNumber();
        cateReceivableMoneySum += shiftCateInfo.getReceivableAmount();
        cateActualMoneySum += shiftCateInfo.getActualAmount();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    //总计名称（总计项目,总计数量,总计原价金额,总计实收金额）
    String nameValueSum = PrinterConstant.SHIFT_SUM + "    " + cateNumSum + "    "
        + NumberFormatUtils.formatFloatNumber(cateReceivableMoneySum) + "    "
        + NumberFormatUtils.formatFloatNumber(cateActualMoneySum);
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(nameValueSum);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //交班收银员签字
    String shiftCashierSign = PrinterConstant.SHIFT_CASHIER_SIGN;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftCashierSign);
    esc.addPrintAndLineFeed();
    //值班经理签字
    String shiftManagerSign = PrinterConstant.SHIFT_MANAGER_SIGN;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftManagerSign);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.SHIFT_BILL_CATE_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 交接班（账单汇总）
   */
  public static void printAllOrderCollect(GpService mGpService, ShiftWork shiftWork) {
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //收银员交接班—账单汇总
    String shiftName = PrinterConstant.SHIFT_WORK_ALL_ORDER;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(shiftName);
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    //店铺名称
    String officeName = PrinterConstant.OFFICE_NAME + DaoServiceUtil.getOfficeDao().queryBuilder().unique().getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(officeName);
    esc.addPrintAndLineFeed();
    //收银员
    String shiftCashier = PrinterConstant.CASHIER + shiftWork.getCashierName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftCashier);
    esc.addPrintAndLineFeed();
    //交接时间
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String shiftTimeValue = simpleDateFormat.format(new Date());
    String shiftTime = PrinterConstant.SHIFT_TIME + shiftTimeValue;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftTime);
    esc.addPrintAndLineFeed();
    //区域
    String shiftArea = PrinterConstant.WORK_ZONE + shiftWork.getWorkZone();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftArea);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //收银汇总(标题)
    String cashCollectTitle = PrinterConstant.SHIFT_CASH_COLLECT_TITLE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cashCollectTitle);
    esc.addPrintAndLineFeed();
    //收银汇总(项目,单数,金额)
    String cashCollectProject =
        PrinterConstant.SHIFT_CASH_COLLECT_PROJECT + "        " + PrinterConstant.SHIFT_CASH_COLLECT_NUM
            + "        " + PrinterConstant.SHIFT_CASH_COLLECT_AMOUNT;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cashCollectProject);
    esc.addPrintAndLineFeed();
    //收银汇总(项目,单数值,金额值)
    List<AppCashCollect> cashCollectList = shiftWork.getCashCollectList();
    for (AppCashCollect collect : cashCollectList) {
      String name = collect.getName();
      int num = collect.getNum();
      String money = NumberFormatUtils.formatFloatNumber(collect.getMoney());
      int nameAndNum = 0;
      int numAndMoney = 0;
      String nameAndNumValue = "";
      String numAndMoneyValue = "";
      try {
        nameAndNum = 15-(name.getBytes("GBK").length+String.valueOf(num).getBytes("GBK").length);
        numAndMoney = 15-(String.valueOf(num).getBytes("GBK").length+money.getBytes("GBK").length);
        for (int i = 0; i < nameAndNum; i++) {
          nameAndNumValue += " ";
        }
        for (int i = 0; i < numAndMoney; i++) {
          numAndMoneyValue += " ";
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      String valueCollect = name + nameAndNumValue + num + numAndMoneyValue + money;
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
      esc.addText(valueCollect);
      esc.addPrintAndLineFeed();
    }
    //消费统计（标题）
    String consumeStaticsTitle = PrinterConstant.SHIFT_CONSUME_STATICS_TITLE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTitle);
    esc.addPrintAndLineFeed();
    //消费统计（单数）
    String consumeStaticsNum = "单数: " + shiftWork.getBillsCount();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsNum);
    esc.addPrintAndLineFeed();
    //消费统计（人数）
    String consumeStaticsPeopleNum = "人数: " + shiftWork.getPeopleNum();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsPeopleNum);
    esc.addPrintAndLineFeed();
    //消费统计 （总价）
    String consumeStaticsTotal =
        "总价: " + NumberFormatUtils.formatFloatNumber(shiftWork.getTotalPrice());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTotal);
    esc.addPrintAndLineFeed();
    //消费统计（应收金额）
    String consumeStaticsReceivable =
        "应收金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getAcceptAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsReceivable);
    esc.addPrintAndLineFeed();
    //消费统计（优惠金额）
    String consumeStaticsPrivilege =
        "优惠金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getDiscountAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsPrivilege);
    esc.addPrintAndLineFeed();

    //支付类优惠
     esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(shiftWork.getPayPrivilege()));
    esc.addPrintAndLineFeed();

    //消费统计（损益金额）
    String consumeStaticsTail =
        "损益金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getGainLoseAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTail);
    esc.addPrintAndLineFeed();
    //消费统计（实际收入）
    String consumeStaticsReceived =
        "实际收入: " + NumberFormatUtils.formatFloatNumber(shiftWork.getActualAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsReceived);
    esc.addPrintAndLineFeed();
    //消费统计（不计入统计金额）
    String consumeStaticsExclusive =
        "不计入统计金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getStaticsExclusive());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsExclusive);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    ////会员充值（标题）
    //String vipRechargeTitle = PrinterConstant.SHIFT_VIP_RECHARGE_TITLE;
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeTitle);
    //esc.addPrintAndLineFeed();
    ////会员充值（充值笔数）
    //String vipRechargeNumber = "充值笔数: " + shiftWork.getRechargePeopleNum();
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeNumber);
    //esc.addPrintAndLineFeed();
    ////会员充值（实收金额）
    //String vipRechargeReceived =
    //    "实收金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getActualMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeReceived);
    //esc.addPrintAndLineFeed();
    ////会员充值（赠送金额）
    //String vipRechargeGift =
    //    "赠送金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getPresentMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeGift);
    //esc.addPrintAndLineFeed();
    ////会员充值（充值金额）
    //String vipRechargeRecharge =
    //    "充值金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getRechargeMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeRecharge);
    //esc.addPrintAndLineFeed();
    ////分割线
    //getDividerLine(esc);
    //交班收银员签字
    String shiftCashierSign = PrinterConstant.SHIFT_CASHIER_SIGN;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftCashierSign);
    esc.addPrintAndLineFeed();
    //值班经理签字
    String shiftManagerSign = PrinterConstant.SHIFT_MANAGER_SIGN;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftManagerSign);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.SHIFT_BILL_CONNECT_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 交接班（日结汇总）
   */
  public static void printDayReportCollect(GpService mGpService, ShiftWork shiftWork, DayReportActivity activity) {
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //收银员交接班—日结汇总
    String shiftName = PrinterConstant.SHIFT_DAY_REPORT_COLLECT;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(shiftName);
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    //店铺名称
    String officeName = PrinterConstant.OFFICE_NAME + DaoServiceUtil.getOfficeDao().queryBuilder().unique().getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(officeName);
    esc.addPrintAndLineFeed();
    //收银员
    String shiftCashier = PrinterConstant.CASHIER + shiftWork.getCashierName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftCashier);
    esc.addPrintAndLineFeed();
    //交接时间
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String shiftTimeValue = simpleDateFormat.format(new Date());
    String shiftTime = PrinterConstant.SHIFT_TIME + shiftTimeValue;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftTime);
    esc.addPrintAndLineFeed();
    //账单时间段
    String shiftOrderTime =
        PrinterConstant.SHIFT_TIME_ZONE + simpleDateFormat.format(shiftWork.getStartTime()) + "~"
            + simpleDateFormat.format(shiftWork.getEndTime());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftOrderTime);
    esc.addPrintAndLineFeed();
    //区域
    String shiftArea = PrinterConstant.WORK_ZONE + shiftWork.getWorkZone();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftArea);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //收银汇总(标题)
    String cashCollectTitle = PrinterConstant.SHIFT_CASH_COLLECT_TITLE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cashCollectTitle);
    esc.addPrintAndLineFeed();
    //收银汇总(项目,单数,金额)
    String cashCollectProject =
        PrinterConstant.SHIFT_CASH_COLLECT_PROJECT + "        " + PrinterConstant.SHIFT_CASH_COLLECT_NUM
            + "        " + PrinterConstant.SHIFT_CASH_COLLECT_AMOUNT;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cashCollectProject);
    esc.addPrintAndLineFeed();
    //收银汇总(项目,单数值,金额值)
    List<AppCashCollect> cashCollectList = shiftWork.getCashCollectList();
    for (AppCashCollect collect : cashCollectList) {
      String name = collect.getName();
      int num = collect.getNum();
      String money = NumberFormatUtils.formatFloatNumber(collect.getMoney());
      int nameAndNum = 0;
      int numAndMoney = 0;
      String nameAndNumValue = "";
      String numAndMoneyValue = "";
      try {
        nameAndNum = 15-(name.getBytes("GBK").length+String.valueOf(num).getBytes("GBK").length);
        numAndMoney = 15-(String.valueOf(num).getBytes("GBK").length+money.getBytes("GBK").length);
        for (int i = 0; i < nameAndNum; i++) {
          nameAndNumValue += " ";
        }
        for (int i = 0; i < numAndMoney; i++) {
          numAndMoneyValue += " ";
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      String valueCollect = name + nameAndNumValue + num + numAndMoneyValue + money;
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
      esc.addText(valueCollect);
      esc.addPrintAndLineFeed();
    }
    //消费统计（标题）
    String consumeStaticsTitle = PrinterConstant.SHIFT_CONSUME_STATICS_TITLE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTitle);
    esc.addPrintAndLineFeed();
    //消费统计（单数）
    String consumeStaticsNum = "单数: " + shiftWork.getBillsCount();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsNum);
    esc.addPrintAndLineFeed();
    //消费统计（人数）
    String consumeStaticsPeopleNum = "人数: " + shiftWork.getPeopleNum();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsPeopleNum);
    esc.addPrintAndLineFeed();
    //消费统计 （总价）
    String consumeStaticsTotal =
        "总价: " + NumberFormatUtils.formatFloatNumber(shiftWork.getTotalPrice());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTotal);
    esc.addPrintAndLineFeed();
    //消费统计（应收金额）
    String consumeStaticsReceivable =
        "应收金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getAcceptAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsReceivable);
    esc.addPrintAndLineFeed();
    //消费统计（优惠金额）
    String consumeStaticsPrivilege =
        "优惠金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getDiscountAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsPrivilege);
    esc.addPrintAndLineFeed();

     //支付类优惠
     esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(shiftWork.getPayPrivilege()));
    esc.addPrintAndLineFeed();

    //消费统计（损益金额）
    String consumeStaticsTail =
        "损益金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getGainLoseAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTail);
    esc.addPrintAndLineFeed();
    //消费统计（实收金额）净收入
    String consumeStaticsReceived =
        "实际收入: " + NumberFormatUtils.formatFloatNumber(shiftWork.getActualAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsReceived);
    esc.addPrintAndLineFeed();
    //消费统计（不计入统计金额）
    String consumeStaticsExclusive =
        "不计入统计金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getStaticsExclusive());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsExclusive);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    ////会员充值（标题）
    //String vipRechargeTitle = PrinterConstant.SHIFT_VIP_RECHARGE_TITLE;
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeTitle);
    //esc.addPrintAndLineFeed();
    ////会员充值（充值笔数）
    //String vipRechargeNumber = "充值笔数: " + shiftWork.getRechargePeopleNum();
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeNumber);
    //esc.addPrintAndLineFeed();
    ////会员充值（实收金额）
    //String vipRechargeReceived =
    //    "实收金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getActualMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeReceived);
    //esc.addPrintAndLineFeed();
    ////会员充值（赠送金额）
    //String vipRechargeGift =
    //    "赠送金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getPresentMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeGift);
    //esc.addPrintAndLineFeed();
    ////会员充值（充值金额）
    //String vipRechargeRecharge =
    //    "充值金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getRechargeMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeRecharge);
    //esc.addPrintAndLineFeed();
    ////分割线
    //getDividerLine(esc);
    //分类统计(标题)
    String cateCollectTitle = PrinterConstant.SHIFT_CATE_COLLECT_TITLE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cateCollectTitle);
    esc.addPrintAndLineFeed();
    //分类统计(项目,数量,原价,实收)
    String cateCollectProject =
        PrinterConstant.SHIFT_CASH_COLLECT_PROJECT + "   " + PrinterConstant.NUMBER + "   "
            + PrinterConstant.SHIFT_CONSUME_STATICS_RECEIVABLE + "   "
            + PrinterConstant.SHIFT_CONSUME_STATICS_RECEIVED;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cateCollectProject);
    esc.addPrintAndLineFeed();
    //分类汇总实体(AppShiftCateInfo)
    //实收金额（总）
    int cateNumSum = 0;
    double cateReceivableMoneySum = 0;
    double cateActualMoneySum = 0;
    try {
      for (AppShiftCateInfo shiftCateInfo : shiftWork.getCategoryCollectList()) {
        //计算分类名和数量之间的空格数
        String lengthNumber = "";
        int lengthCateAndNum = shiftCateInfo.getCateName().getBytes("GBK").length;
        int lengthCateAndNumValue = 8 - lengthCateAndNum;
        for (int i = 0; i < lengthCateAndNumValue; i++) {
          lengthNumber += " ";
        }
        //计算数量和原价之间的空格数
        String lengthReceivable = "";
        int lengthNumAndRecCate =
            String.valueOf(shiftCateInfo.getCateNumber()).getBytes("GBK").length;
        int lengthNumAndRecCateValue = 6 - lengthNumAndRecCate;
        for (int i = 0; i < lengthNumAndRecCateValue; i++) {
          lengthReceivable += " ";
        }
        //计算原价和实收之间的空格数
        String lengthReal = "";
        int lengthRecAndAct =
            NumberFormatUtils.formatFloatNumber(shiftCateInfo.getReceivableAmount())
                .getBytes("GBK").length;
        int lengthRecAndActValue = 8 - lengthRecAndAct;
        for (int i = 0; i < lengthRecAndActValue; i++) {
          lengthReal += " ";
        }

        //分类名,数量,原价,实收
        String nameValue =
            shiftCateInfo.getCateName() + lengthNumber + shiftCateInfo.getCateNumber()
                + lengthReceivable + NumberFormatUtils.formatFloatNumber(
                shiftCateInfo.getReceivableAmount()) + lengthReal
                + NumberFormatUtils.formatFloatNumber(shiftCateInfo.getActualAmount());
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addText(nameValue);
        esc.addPrintAndLineFeed();
        cateNumSum += shiftCateInfo.getCateNumber();
        cateReceivableMoneySum += shiftCateInfo.getReceivableAmount();
        cateActualMoneySum += shiftCateInfo.getActualAmount();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    //总计名称（总计项目,总计数量,总计原价金额,总计实收金额）
    String nameValueSum = PrinterConstant.SHIFT_SUM + "   " + cateNumSum + "    "
        + NumberFormatUtils.formatFloatNumber(cateReceivableMoneySum) + "    "
        + NumberFormatUtils.formatFloatNumber(cateActualMoneySum);
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(nameValueSum);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //交班收银员签字
    String shiftCashierSign = PrinterConstant.SHIFT_CASHIER_SIGN;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftCashierSign);
    esc.addPrintAndLineFeed();
    //值班经理签字
    String shiftManagerSign = PrinterConstant.SHIFT_MANAGER_SIGN;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftManagerSign);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.DAY_REPORT_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 交接班（交接后打印汇总信息）
   */
  public static void printShiftCollect(GpService mGpService, ShiftWork shiftWork,ShiftChangeFunctionsActivity activity) {
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //收银员交接班—汇总信息
    String shiftName = PrinterConstant.SHIFT_SHIFT_COLLECT;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(shiftName);
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    //店铺名称
    String officeName = PrinterConstant.OFFICE_NAME + DaoServiceUtil.getOfficeDao().queryBuilder().unique().getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(officeName);
    esc.addPrintAndLineFeed();
    //收银员
    String shiftCashier = PrinterConstant.CASHIER + shiftWork.getCashierName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftCashier);
    esc.addPrintAndLineFeed();
    //交接时间
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String shiftTimeValue = simpleDateFormat.format(new Date());
    String shiftTime = PrinterConstant.SHIFT_TIME + shiftTimeValue;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftTime);
    esc.addPrintAndLineFeed();
    //区域
    String shiftArea = PrinterConstant.WORK_ZONE + shiftWork.getWorkZone();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftArea);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //收银汇总(标题)
    String cashCollectTitle = PrinterConstant.SHIFT_CASH_COLLECT_TITLE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cashCollectTitle);
    esc.addPrintAndLineFeed();
    //收银汇总(项目,单数,金额)
    String cashCollectProject =
        PrinterConstant.SHIFT_CASH_COLLECT_PROJECT + "        " + PrinterConstant.SHIFT_CASH_COLLECT_NUM
            + "        " + PrinterConstant.SHIFT_CASH_COLLECT_AMOUNT;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cashCollectProject);
    esc.addPrintAndLineFeed();
    //收银汇总(项目,单数值,金额值)
    List<AppCashCollect> cashCollectList = shiftWork.getCashCollectList();
    for (AppCashCollect collect : cashCollectList) {
      String name = collect.getName();
      int num = collect.getNum();
      String money = NumberFormatUtils.formatFloatNumber(collect.getMoney());
      int nameAndNum = 0;
      int numAndMoney = 0;
      String nameAndNumValue = "";
      String numAndMoneyValue = "";
      try {
        nameAndNum = 15-(name.getBytes("GBK").length+String.valueOf(num).getBytes("GBK").length);
        numAndMoney = 15-(String.valueOf(num).getBytes("GBK").length+money.getBytes("GBK").length);
        for (int i = 0; i < nameAndNum; i++) {
          nameAndNumValue += " ";
        }
        for (int i = 0; i < numAndMoney; i++) {
          numAndMoneyValue += " ";
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      String valueCollect = name + nameAndNumValue + num + numAndMoneyValue + money;
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
          EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
      esc.addText(valueCollect);
      esc.addPrintAndLineFeed();
    }
    //消费统计（标题）
    String consumeStaticsTitle = PrinterConstant.SHIFT_CONSUME_STATICS_TITLE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTitle);
    esc.addPrintAndLineFeed();
    //消费统计（单数）
    String consumeStaticsNum = "单数: " + shiftWork.getBillsCount();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsNum);
    esc.addPrintAndLineFeed();
    //消费统计（人数）
    String consumeStaticsPeopleNum = "人数: " + shiftWork.getPeopleNum();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsPeopleNum);
    esc.addPrintAndLineFeed();
    //消费统计 （总价）
    String consumeStaticsTotal =
        "总价  " + NumberFormatUtils.formatFloatNumber(shiftWork.getTotalPrice());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTotal);
    esc.addPrintAndLineFeed();
    //消费统计（应收金额）
    String consumeStaticsReceivable =
        "应收金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getAcceptAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsReceivable);
    esc.addPrintAndLineFeed();
    //消费统计（优惠金额）
    String consumeStaticsPrivilege =
        "优惠金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getDiscountAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsPrivilege);
    esc.addPrintAndLineFeed();

     //支付类优惠
     esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(PrinterConstant.PAY_PRIVILEGE + NumberFormatUtils.formatFloatNumber(shiftWork.getPayPrivilege()));
    esc.addPrintAndLineFeed();

    //消费统计（损益金额）
    String consumeStaticsTail =
        "损益金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getGainLoseAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsTail);
    esc.addPrintAndLineFeed();
    //消费统计（实收金额）
    String consumeStaticsReceived =
        "实际收入: " + NumberFormatUtils.formatFloatNumber(shiftWork.getActualAmount());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsReceived);
    esc.addPrintAndLineFeed();
    //消费统计（不计入统计金额）
    String consumeStaticsExclusive =
        "不计入统计金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getStaticsExclusive());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(consumeStaticsExclusive);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    ////会员充值（标题）
    //String vipRechargeTitle = PrinterConstant.SHIFT_VIP_RECHARGE_TITLE;
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeTitle);
    //esc.addPrintAndLineFeed();
    ////会员充值（充值笔数）
    //String vipRechargeNumber = "充值笔数: " + shiftWork.getRechargePeopleNum();
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeNumber);
    //esc.addPrintAndLineFeed();
    ////会员充值（实收金额）
    //String vipRechargeReceived =
    //    "实收金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getActualMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeReceived);
    //esc.addPrintAndLineFeed();
    ////会员充值（赠送金额）
    //String vipRechargeGift =
    //    "赠送金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getPresentMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeGift);
    //esc.addPrintAndLineFeed();
    ////会员充值（充值金额）
    //String vipRechargeRecharge =
    //    "充值金额: " + NumberFormatUtils.formatFloatNumber(shiftWork.getRechargeMoney());
    //esc.addSelectCodePage(ReceiptCommand.CODEPAGE.UYGUR);
    //esc.addSelectJustification(ReceiptCommand.JUSTIFICATION.LEFT);// 设置打印居左
    //esc.addSelectPrintModes(ReceiptCommand.FONT.FONTA, ReceiptCommand.ENABLE.OFF,
    //    ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF, ReceiptCommand.ENABLE.OFF);// 取消倍高倍宽
    //esc.addText(vipRechargeRecharge);
    //esc.addPrintAndLineFeed();
    ////分割线
    //getDividerLine(esc);
    //分类统计(标题)
    String cateCollectTitle = PrinterConstant.SHIFT_CATE_COLLECT_TITLE;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cateCollectTitle);
    esc.addPrintAndLineFeed();
    //分类统计(项目,数量,原价,实收)
    String cateCollectProject =
        PrinterConstant.SHIFT_CASH_COLLECT_PROJECT + "   " + PrinterConstant.NUMBER + "   "
            + PrinterConstant.SHIFT_CONSUME_STATICS_RECEIVABLE + "   "
            + PrinterConstant.SHIFT_CONSUME_STATICS_RECEIVED;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(cateCollectProject);
    esc.addPrintAndLineFeed();
    //分类汇总实体(AppShiftCateInfo)
    //实收金额（总）
    int cateNumSum = 0;
    double cateReceivableMoneySum = 0;
    double cateActualMoneySum = 0;
    try {
      for (AppShiftCateInfo shiftCateInfo : shiftWork.getCategoryCollectList()) {
        //计算分类名和数量之间的空格数
        String lengthNumber = "";
        int lengthCateAndNum = shiftCateInfo.getCateName().getBytes("GBK").length;
        int lengthCateAndNumValue = 8 - lengthCateAndNum;
        for (int i = 0; i < lengthCateAndNumValue; i++) {
          lengthNumber += " ";
        }
        //计算数量和原价之间的空格数
        String lengthReceivable = "";
        int lengthNumAndRecCate =
            String.valueOf(shiftCateInfo.getCateNumber()).getBytes("GBK").length;
        int lengthNumAndRecCateValue = 6 - lengthNumAndRecCate;
        for (int i = 0; i < lengthNumAndRecCateValue; i++) {
          lengthReceivable += " ";
        }
        //计算原价和实收之间的空格数
        String lengthReal = "";
        int lengthRecAndAct =
            NumberFormatUtils.formatFloatNumber(shiftCateInfo.getReceivableAmount())
                .getBytes("GBK").length;
        int lengthRecAndActValue = 8 - lengthRecAndAct;
        for (int i = 0; i < lengthRecAndActValue; i++) {
          lengthReal += " ";
        }

        //分类名,数量,原价,实收
        String nameValue =
            shiftCateInfo.getCateName() + lengthNumber + shiftCateInfo.getCateNumber()
                + lengthReceivable + NumberFormatUtils.formatFloatNumber(
                shiftCateInfo.getReceivableAmount()) + lengthReal
                + NumberFormatUtils.formatFloatNumber(shiftCateInfo.getActualAmount());
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addText(nameValue);
        esc.addPrintAndLineFeed();
        cateNumSum += shiftCateInfo.getCateNumber();
        cateReceivableMoneySum += shiftCateInfo.getReceivableAmount();
        cateActualMoneySum += shiftCateInfo.getActualAmount();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    //总计名称（总计项目,总计数量,总计原价金额,总计实收金额）
    String nameValueSum = PrinterConstant.SHIFT_SUM + "    " + cateNumSum + "    "
        + NumberFormatUtils.formatFloatNumber(cateReceivableMoneySum) + "    "
        + NumberFormatUtils.formatFloatNumber(cateActualMoneySum);
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(nameValueSum);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //交班收银员签字
    String shiftCashierSign = PrinterConstant.SHIFT_CASHIER_SIGN;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftCashierSign);
    esc.addPrintAndLineFeed();
    //值班经理签字
    String shiftManagerSign = PrinterConstant.SHIFT_MANAGER_SIGN;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(shiftManagerSign);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //打印时间字样和值
    getTimeAndValue(esc);
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.SHIFT_CONNECT_DATA_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 会员充值记录
   */
  public static void printVipRechargeRecord(GpService mGpService, PxVipInfo vipInfo,PxRechargeRecord record ) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //会员充值记录
    String vipRechargeRecord = "会员充值记录";
    esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(vipRechargeRecord);
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    //会员名称
    String vipName = "会员名称: " + vipInfo.getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipName);
    esc.addPrintAndLineFeed();
    //会员电话
    String vipMobile = "会员电话: " + vipInfo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipMobile);
    esc.addPrintAndLineFeed();
    //会员充值金额
    String vipRechargeMoney = "充值金额: "+record.getMoney();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipRechargeMoney);
    esc.addPrintAndLineFeed();
    //会员赠送金额
    String vipGivingMoney ="赠送金额: " +record.getGiving();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipGivingMoney);
    esc.addPrintAndLineFeed();
    //会员余额
    String vipAccountBalance ="余    额: " +vipInfo.getAccountBalance();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipAccountBalance);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);
    //消费时间
    String rechargeTime ="消费时间: " +sdf.format(new Date());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(rechargeTime);
    esc.addPrintAndLineFeed();
    //消费门店
    String rechargeOffice ="消费门店: " +DaoServiceUtil.getOfficeDao().queryBuilder().unique().getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(rechargeOffice);
    esc.addPrintAndLineFeed();
    //打印时间字样和值
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String printTimeValue = simpleDateFormat.format(new Date());
    String timeAndValue = PrinterConstant.PRINT_TIME+ printTimeValue;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高
    esc.addText(timeAndValue);
    esc.addPrintAndLineFeed();
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.VIP_RECHARGE_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
  /**
   * 会员消费记录
   */
  public static void printVipConsumeRecord(GpService mGpService, double consume ,PxVipInfo vipInfo) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    EscCommand esc = new EscCommand();
    //开始间距
    esc.addPrintAndFeedLines((byte) 3);
    //会员充值记录
    String vipRechargeRecord = "会员消费记录";
    esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
    esc.addText(vipRechargeRecord);
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    esc.addPrintAndLineFeed();
    //会员名称
    String vipName = "会员名称: " + vipInfo.getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipName);
    esc.addPrintAndLineFeed();
    //会员电话
    String vipMobile = "会员电话: " + vipInfo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipMobile);
    esc.addPrintAndLineFeed();
    //本次消费
    String vipRechargeMoney = "本次消费: "+NumberFormatUtils.formatFloatNumber(consume);
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipRechargeMoney);
    esc.addPrintAndLineFeed();
    //余额
    String vipGivingMoney ="余    额: " +NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance() - consume);
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(vipGivingMoney);
    esc.addPrintAndLineFeed();
    //分割线
    getDividerLine(esc);

    //消费时间
    String rechargeTime ="消费时间: " +sdf.format(new Date());
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(rechargeTime);
    esc.addPrintAndLineFeed();
    //消费门店
    String rechargeOffice ="消费门店: " +DaoServiceUtil.getOfficeDao().queryBuilder().unique().getName();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText(rechargeOffice);
    esc.addPrintAndLineFeed();
    //收银员
     App app = (App) App.getContext();
     User user = app.getUser();
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
    esc.addText("收 银 员:" + (user == null ? "admin" : user.getName()));
    esc.addPrintAndLineFeed();
    //打印时间字样和值
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String printTimeValue = simpleDateFormat.format(new Date());
    String timeAndValue = PrinterConstant.PRINT_TIME+ printTimeValue;
    esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
        EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 设置为倍高
    esc.addText(timeAndValue);
    esc.addPrintAndLineFeed();
    //结束间距
    esc.addPrintAndFeedLines((byte) 3);

    Vector<Byte> vector = esc.getCommand();
    final byte[] data = GpUtils.ByteTo_byte(vector);
    String encodeString = Base64.encodeToString(data,Base64.DEFAULT);
    int sendInstruction;
    try {
      sendInstruction = mGpService.sendEscCommand(1,encodeString);
      GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[sendInstruction];
      if (r != GpCom.ERROR_CODE.SUCCESS) {
        EventBus.getDefault().postSticky(new OpenPortEvent(OpenPortEvent.VIP_CONSUME_PORT));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
  ///**
  // * 得到所填充的数据
  // */
  //private static byte[] getData(Vector<Byte> vector) {
  //  int length = vector.size();
  //  byte[] bytes = new byte[length];
  //  for (int i = 0; i < length; i++) {
  //    bytes[i] = vector.get(i);
  //  }
  //  return bytes;
  //}
  //
  /**
   * 各项实收 金额
   */
  //@formatter:off
  private static void everyReceived(PxOrderInfo mOrderInfo, EscCommand esc) {
    Cursor cursor = null;
    try {
      SQLiteDatabase db = DaoServiceUtil.getPayInfoDao().getDatabase();
      //各项实收
      cursor = db.rawQuery("Select sum(pay.RECEIVED),pay.PAYMENT_NAME,sum(pay.CHANGE),pay.PAYMENT_TYPE"
              + " From PxPayInfo pay"
              + " Where pay.PX_ORDER_INFO_ID = " + mOrderInfo.getId()
              + " Group by pay.PAYMENT_TYPE"
          ,null);
      //各项实收
      while (cursor.moveToNext()) {
        double received = cursor.getDouble(0);
        String type = cursor.getString(3);
        if (received > 0) {
          esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
          esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
              EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
              EscCommand.ENABLE.OFF);// 设置为倍高倍宽
          String text = null;
          if (PxPaymentMode.TYPE_CASH.equals(type)){
            double change = cursor.getDouble(2);
            text = cursor.getString(1) + ":" + NumberFormatUtils.formatFloatNumber(received) + "   找零:"+NumberFormatUtils.formatFloatNumber(change);
          }else{
            text = cursor.getString(1) + ":" + NumberFormatUtils.formatFloatNumber(received);
          }
          esc.addText(text);
          esc.addPrintAndLineFeed();
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
  private static void freePayInfo(EscCommand esc, PxOrderInfo orderInfo) {
    QueryBuilder<PxPayInfo> qb = DaoServiceUtil.getPayInfoService().queryBuilder();
    qb.where(PxPayInfoDao.Properties.PxOrderInfoId.eq(orderInfo.getId()));
    qb.where(PxPayInfoDao.Properties.PaymentType.eq(PxPaymentMode.TYPE_FREE));
    List<PxPayInfo> freePayInfoList = qb.list();
    if (freePayInfoList != null && freePayInfoList.size() > 0) {
      getDividerLine(esc);
      for (PxPayInfo payInfo : freePayInfoList) {
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("免单金额:" + payInfo.getReceived() + "  原因:" + payInfo.getRemarks());
        esc.addPrintAndLineFeed();
      }
    }
  }

  /**
   * 分类汇总
   */
  private static void categoryCollect(EscCommand esc, PxOrderInfo orderInfo) {
    PxSetInfo setInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    if (setInfo != null && PxSetInfo.FINANCE_PRINT_CATEGORY_TRUE.equals(setInfo.getIsFinancePrintCategory())) {
      String cateCollectTitle = PrinterConstant.SHIFT_CATE_COLLECT_TITLE;
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
      esc.addText(cateCollectTitle);
      esc.addPrintAndLineFeed();
      //分类统计(项目,数量,原价,实收)
      String cateCollectProject = PrinterConstant.SHIFT_CASH_COLLECT_PROJECT + "   " + PrinterConstant.NUMBER + "   "
              + PrinterConstant.SHIFT_CONSUME_STATICS_RECEIVABLE + "   "
              + PrinterConstant.SHIFT_CONSUME_STATICS_RECEIVED;
      esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
      esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
      esc.addText(cateCollectProject);
      esc.addPrintAndLineFeed();

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
        while (cursor.moveToNext()) {
          double originPrice = cursor.getDouble(1);
          double afterDiscount = cursor.getDouble(2);
          String name = cursor.getString(3);
          double num = cursor.getDouble(0);

          //计算分类名和数量之间的空格数
          String lengthNumber = "";
          int lengthCateAndNum = name.getBytes("GBK").length;
          int lengthCateAndNumValue = 8 - lengthCateAndNum;
          for (int i = 0; i < lengthCateAndNumValue; i++) {
            lengthNumber += " ";
          }
          //计算数量和原价之间的空格数
          String lengthReceivable = "";
          int lengthNumAndRecCate = String.valueOf(num).getBytes("GBK").length;
          int lengthNumAndRecCateValue = 6 - lengthNumAndRecCate;
          for (int i = 0; i < lengthNumAndRecCateValue; i++) {
            lengthReceivable += " ";
          }
          //计算原价和实收之间的空格数
          String lengthReal = "";
          int lengthRecAndAct =
              NumberFormatUtils.formatFloatNumber(originPrice).getBytes("GBK").length;
          int lengthRecAndActValue = 8 - lengthRecAndAct;
          for (int i = 0; i < lengthRecAndActValue; i++) {
            lengthReal += " ";
          }
          //分类名,数量,原价,实收
          String nameValue =
              name + lengthNumber + num + lengthReceivable + NumberFormatUtils.formatFloatNumber(
                  originPrice) + lengthReal + NumberFormatUtils.formatFloatNumber(afterDiscount);
          esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印居左
          esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF,
              EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
          esc.addText(nameValue);
          esc.addPrintAndLineFeed();
        }
      } catch (Exception e) {
         e.printStackTrace();
        Logger.e(e.toString());
      } finally {
        IOUtils.closeCloseables(cursor);
      }
    }
    //line
    getDividerLineWithLeft(esc);
  }
}
