package com.psi.easymanager.pay.query;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.EPayinfoAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.EPaymentInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxPaymentModeDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpNetPayRecordReq;
import com.psi.easymanager.network.resp.HttpNetPayRecordResp;
import com.psi.easymanager.pay.query.module.PxNetPayRecord;
import com.psi.easymanager.utils.ListViewHeightUtil;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;

/**
 * User: ylw
 * Date: 2016-10-25
 * Time: 18:53
 * 查询置定订单的所有在线支付 退款记录
 * 支付宝、微信、
 */
//@formatter:off
public class QueryNetPayRecord {
  public static void queryNetPayRecord(final Activity act, final PxOrderInfo orderInfo) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动!");
      return;
    }
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();

    HttpNetPayRecordReq req = new HttpNetPayRecordReq();
    req.setOrderNo(orderInfo.getOrderNo());
    req.setCompanyCode(companyCode);
    req.setUserId(userId);

    final MaterialDialog checkDialog = DialogUtils.showDialog(act,"核对在线交易详情");

    new RestClient(0, 1000, 10000, 3000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.e(responseString + "---" + throwable.toString());
        DialogUtils.dismissDialog(checkDialog);
        if (throwable instanceof SocketTimeoutException) {
          ToastUtils.showShort(App.getContext(), "服务器响应超时，请稍后查询!");
        } else {
          ToastUtils.showShort(App.getContext(), "网络连接异常，请检查网络配置!");
        }
      }

      @Override protected void success(String responseString) {
        HttpNetPayRecordResp resp = new Gson().fromJson(responseString, HttpNetPayRecordResp.class);
        final List<PxNetPayRecord> recordList = resp.getList();
        DialogUtils.dismissDialog(checkDialog);
        if (recordList != null && recordList.size() > 0) {
          //处理记录
          checkNetPayRecord(recordList, orderInfo);
          //@formatter:on
          //显示电子支付内容
          final MaterialDialog dialogEpay = new MaterialDialog.Builder(act).title("电子支付信息")
              .customView(R.layout.layout_dialog_epay_info, true)
              .positiveText("确定")
              .positiveColor(act.getResources().getColor(R.color.primary_text))
              .build();
          //确认按钮
          MDButton positiveBtn = dialogEpay.getActionButton(DialogAction.POSITIVE);
          positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              dialogEpay.dismiss();
            }
          });
          ListView lvEpayInfo =
              (ListView) dialogEpay.getCustomView().findViewById(R.id.lv_epay_info);
          EPayinfoAdapter ePayinfoAdapter = new EPayinfoAdapter(act, recordList);
          lvEpayInfo.setAdapter(ePayinfoAdapter);
          ListViewHeightUtil.setListViewHeightBasedOnChildren(lvEpayInfo);
          //显示对话框
          dialogEpay.show();
        } else {
          ToastUtils.showShort(act, "无网络支付记录");
        }
      }
    }.postOther(act, URLConstants.NET_PAY_RECORD, req);
  }

  /**
   * 核对网络 支付、退款记录
   */
  //@formatter:off
  private static void checkNetPayRecord(List<PxNetPayRecord> recordList, PxOrderInfo orderInfo) {
    boolean isRefresh = false;
    for (PxNetPayRecord record : recordList) {
      String status = record.getStatus();
      //付款记录处理
      if (PxNetPayRecord.STATUS_PAY.equals(status)) {
        EPaymentInfo unique = DaoServiceUtil.getEPaymentInfoService()
            .queryBuilder()
            .where(EPaymentInfoDao.Properties.TradeNo.eq(record.getSelfTradeNo()))
            .whereOr(EPaymentInfoDao.Properties.Status.eq(EPaymentInfo.STATUS_PAYED), EPaymentInfoDao.Properties.Status.eq(EPaymentInfo.STATUS_PAYED_AND_REFUND))
            .unique();
        //本地没生成
        if (unique == null) {
          isRefresh = true;
          createPayInfo(record, orderInfo);
        }
      }
      //退款记录处理
      else if (PxNetPayRecord.STATUS_REFUND.equals(status)) {
        EPaymentInfo unique = DaoServiceUtil.getEPaymentInfoService()
            .queryBuilder()
            .where(EPaymentInfoDao.Properties.TradeNo.eq(record.getSelfTradeNo()))
            .where(EPaymentInfoDao.Properties.Status.eq(EPaymentInfo.STATUS_REFUND))
            .unique();
        Logger.e(record.getSelfTradeNo() + "----" + (unique == null));
        //本地没删除
        if (unique == null) {
          isRefresh = true;
          deletePayInfoAndCreateEPaymentInfo(record);
        }
      }
    }
    //通知更新
    if (isRefresh) {
      EventBus.getDefault().post(new RefreshCashBillListEvent());
    }
  }

  /**
   * 删除PayInfo 并生成一条退款电子支付信息、修改原有的为已退款
   */
  private static void deletePayInfoAndCreateEPaymentInfo(PxNetPayRecord record) {
    String ePaymentType = null;
    if (PxNetPayRecord.TYPE_ALIPAY.equals(record.getType())) {
      ePaymentType = EPaymentInfo.TYPE_ALI_PAY;
    } else if (PxNetPayRecord.TYPE_WEIXIN.equals(record.getType())) {
      ePaymentType = EPaymentInfo.TYPE_WX_PAY;
    } else if (PxNetPayRecord.TYPE_BESTPAY.equals(record.getType())){
      ePaymentType = EPaymentInfo.TYPE_BEST_PAY;
    }
    if (ePaymentType == null) return;
    PxPayInfo payInfo = DaoServiceUtil.getPayInfoService()
            .queryBuilder()
            .where(PxPayInfoDao.Properties.TradeNo.eq(record.getSelfTradeNo()))
            .unique();
    if (payInfo == null) return;

    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //生成新的 已退款电子支付信息
      PxOrderInfo dbOrder = payInfo.getDbOrder();
      String orderNo = dbOrder.getOrderNo();
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(dbOrder.getId()))
          .unique();
      EPaymentInfo ePaymentInfo = new EPaymentInfo();
      ePaymentInfo.setDbOrder(dbOrder);
      ePaymentInfo.setPrice(payInfo.getReceived());
      ePaymentInfo.setOrderNo("No." + orderNo.substring(orderNo.length() - 4, orderNo.length()));
      ePaymentInfo.setTradeNo(payInfo.getTradeNo());
      ePaymentInfo.setDbPayInfo(payInfo);
      ePaymentInfo.setPayTime(new Date());
      ePaymentInfo.setStatus(EPaymentInfo.STATUS_REFUND);
      ePaymentInfo.setTableName((unique == null) ? "零售单" : unique.getDbTable().getName());
      ePaymentInfo.setIsHandled(EPaymentInfo.HAS_HANDLED);
      ePaymentInfo.setType(ePaymentType);
      DaoServiceUtil.getEPaymentInfoService().saveOrUpdate(ePaymentInfo);
      //修改以前记录为 付款过并已退款
      EPaymentInfo originInfo = DaoServiceUtil.getEPaymentInfoService()
          .queryBuilder()
          .where(EPaymentInfoDao.Properties.TradeNo.eq(payInfo.getTradeNo()))
          .where(EPaymentInfoDao.Properties.Status.eq(EPaymentInfo.STATUS_PAYED))
          .unique();
      originInfo.setStatus(EPaymentInfo.STATUS_PAYED_AND_REFUND);
      DaoServiceUtil.getEPaymentInfoService().saveOrUpdate(originInfo);
      //删除PayInfo
      DaoServiceUtil.getPayInfoService().delete(payInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 补充一条支付记录 并生成一条电子支付信息
   */
  private static void createPayInfo(PxNetPayRecord record, PxOrderInfo orderInfo) {
    String payInfoType = null;
    String ePaymentType = null;
    if (PxNetPayRecord.TYPE_ALIPAY.equals(record.getType())) {
      payInfoType = PxPaymentMode.TYPE_ALIPAY;
      ePaymentType = EPaymentInfo.TYPE_ALI_PAY;
    } else if (PxNetPayRecord.TYPE_WEIXIN.equals(record.getType())) {
      payInfoType = PxPaymentMode.TYPE_WEIXIN;
      ePaymentType = EPaymentInfo.TYPE_WX_PAY;
    } else if (PxNetPayRecord.TYPE_BESTPAY.equals(record.getType())){
      payInfoType = PxPaymentMode.TYPE_WINGPAY;
      ePaymentType = EPaymentInfo.TYPE_BEST_PAY;
    }
    if (payInfoType == null || ePaymentType == null) return;
    //类型 编辑方式 找出默认支付方式
    PxPaymentMode paymentMode = DaoServiceUtil.getPaymentModeService()
        .queryBuilder()
        .where(PxPaymentModeDao.Properties.Type.eq(payInfoType))
        .where(PxPaymentModeDao.Properties.Edit.eq(PxPaymentMode.EDIT_FALSE))
        .unique();
    if (paymentMode == null) return;

    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //payInfo
      PxPayInfo payInfo = new PxPayInfo();
      //实收
      double received = record.getFee();
      payInfo.setReceived(received);
      //找零
      payInfo.setChange(0.0);
      //支付时间
      payInfo.setPayTime(new Date());
      //所属订单
      payInfo.setDbOrder(orderInfo);
      //支付方式 相关
      payInfo.setPaymentId(paymentMode.getObjectId());
      payInfo.setPaymentType(paymentMode.getType());
      payInfo.setPaymentName(paymentMode.getName());
      payInfo.setSalesAmount(paymentMode.getSalesAmount());
      //流水号
      String tradeNo = record.getSelfTradeNo();
      payInfo.setTradeNo(tradeNo);
      //支付类优惠
      payInfo.setPayPrivilege(0.0);
      //储存
      DaoServiceUtil.getPayInfoService().saveOrUpdate(payInfo);
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(orderInfo);

      //EPaymentInfo
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .unique();
      String orderNo = orderInfo.getOrderNo();
      EPaymentInfo ePaymentInfo = new EPaymentInfo();
      ePaymentInfo.setDbOrder(orderInfo);
      ePaymentInfo.setOrderNo("No." + orderNo.substring(orderNo.length() - 4, orderNo.length()));
      ePaymentInfo.setPrice(received);
      ePaymentInfo.setTradeNo(tradeNo);
      ePaymentInfo.setDbPayInfo(payInfo);
      ePaymentInfo.setPayTime(new Date());
      ePaymentInfo.setStatus(EPaymentInfo.STATUS_PAYED);
      ePaymentInfo.setTableName((unique == null) ? "零售单" : unique.getDbTable().getName());
      ePaymentInfo.setIsHandled(EPaymentInfo.HAS_HANDLED);
      ePaymentInfo.setType(ePaymentType);
      DaoServiceUtil.getEPaymentInfoService().saveOrUpdate(ePaymentInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }
}  