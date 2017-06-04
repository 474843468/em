package com.psi.easymanager.pay.alipay;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.adapter.PayInfoAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.EPaymentInfoDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.RefundAlipayEvent;
import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.AlipayRefundReq;
import com.psi.easymanager.network.resp.HttpAlipayResp;
import com.psi.easymanager.operatedialog.ClearDiscAndPayInfoDialog;
import com.psi.easymanager.pay.help.OptOrderLock;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

/**
 * User: ylw
 * Date: 2016-10-20
 * Time: 12:40
 * 支付宝退款
 */
//@formatter:off
public class RefundAlipay {
  public static final int FROM_CHECKOUT_FRAGMENT = 1;
  public static final int FROM_MESSAGES = 2;
  /**
   * 支付宝发起退款
   */
  public static void refundAlipay(final int from ,final Activity act ,final PxPayInfo payInfo, final PayInfoAdapter payInfoAdapter, final int pos) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动");
      return;
    }
    final PxOrderInfo dbOrder = payInfo.getDbOrder();
    final String tradeNo = payInfo.getTradeNo();
    double money = payInfo.getReceived();

    String userId = user.getObjectId();
    String companyCode = user.getCompanyCode();

    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    AlipayRefundReq refundReq = new AlipayRefundReq();
    refundReq.setUserId(userId);
    refundReq.setCompanyCode(companyCode);
    refundReq.setOutTradeNo(tradeNo);
    refundReq.setTotalAmount(""+money);
    refundReq.setRefundReason(""+office.getName());
    final String outRequestNo = Math.abs(companyCode.hashCode()) + UUID.randomUUID().toString().replaceAll("\\-", "");
    refundReq.setOutRequestNo(outRequestNo);
    refundReq.setOrderNo(dbOrder.getOrderNo());

    refundReq.setSelfTradeNo(tradeNo);

    final MaterialDialog refundDialog = DialogUtils.showDialog(act, "支付宝退款", "退款中,请耐心等待!");
    //枷锁
    OptOrderLock.optOrderLock(dbOrder,true);
    new RestClient(0, 1000, 20000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.e(responseString + "--throwable:" + throwable.toString());
        DialogUtils.dismissDialog(refundDialog);
        if (throwable instanceof SocketTimeoutException){//响应超时
          // 继续查询 还是 稍后查询
          showCheckResultOrManualConfirm(from,payInfo,payInfoAdapter,pos,act,"响应超时","请继续查询或稍后查询?",outRequestNo,dbOrder);
        }else{
          ToastUtils.showShort(App.getContext(), "连接超时,请检查网络连接");
          //释放
          OptOrderLock.optOrderLock(dbOrder,false);
        }
      }

      @Override protected void success(String responseString) {
        DialogUtils.dismissDialog(refundDialog);
        HttpAlipayResp resp = new Gson().fromJson(responseString, HttpAlipayResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        Logger.json(responseString);
        if (resp.getStatusCode() == 1 && resp.getStatus().equals(HttpAlipayResp.SUCCESS)) { // 退款成功
          //生成一条退款 EPaymentInfo 并且更新付款的记录为 付款过并已退款
          createRefundInfoAndUpdateOriginInfo(payInfo);
          if (from == FROM_CHECKOUT_FRAGMENT) {
            //删除PayInfo
            ClearDiscAndPayInfoDialog.deletePayInfoAndRefreshUI(payInfo, payInfoAdapter, pos);
          }else{
            deletePayInfo(payInfo);
            EventBus.getDefault().post(new RefundAlipayEvent());
          }
          //更新页面
          EventBus.getDefault().post(new RefreshCashBillListEvent());
        }else{

        }
        //释放
        OptOrderLock.optOrderLock(dbOrder,false);
      }
    }.postOther(act, URLConstants.ALI_REFUND, refundReq);
  }

  /**
   * 删除PayInfo
   */
  private static void deletePayInfo(PxPayInfo payInfo) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //删除支付信息
      DaoServiceUtil.getPayInfoService().delete(payInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   *继续查询 或 手动确认结果
   */
  private static void showCheckResultOrManualConfirm(final int from ,final PxPayInfo payInfo, final PayInfoAdapter payInfoAdapter, final int pos,
      final Activity act, String title, String content, final String outRequestNo,final PxOrderInfo orderInfo) {
    final MaterialDialog dialog = DialogUtils.showCheckResultOrManualConfirm(act, title, content);
    MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(dialog);
        queryRefundResult(from,act,payInfo,payInfoAdapter,pos,outRequestNo,orderInfo);
      }
    });
    MDButton negBtn = dialog.getActionButton(DialogAction.NEGATIVE);
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
         DialogUtils.dismissDialog(dialog);
        //释放
        OptOrderLock.optOrderLock(orderInfo,false);
  }});
  }
  /**
   * 查询退款结果
   */
  private static void queryRefundResult(final int from ,final Activity act,final PxPayInfo payInfo, final PayInfoAdapter payInfoAdapter,
      final int pos, final String outRequestNo,final PxOrderInfo orderInfo) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动");
      return;
    }
    //检查网络
    if (!NetUtils.isConnected(act)) {
      ToastUtils.showShort(App.getContext(), "请检查网络配置!");
      return;
    }
    String userId = user.getObjectId();
    String companyCode = user.getCompanyCode();

    String tradeNo = payInfo.getTradeNo();
    String money = String.valueOf(payInfo.getReceived());

    AlipayRefundReq refundReq = new AlipayRefundReq();
    refundReq.setUserId(userId);
    refundReq.setCompanyCode(companyCode);
    refundReq.setOutTradeNo(tradeNo);
    refundReq.setTotalAmount(money);
    refundReq.setRefundReason("查询退款结果");
    refundReq.setOutRequestNo(outRequestNo);
    refundReq.setSelfTradeNo(tradeNo);

    final MaterialDialog queryRefundDialog = DialogUtils.showDialog(act, "查询退款结果", "查询中,请耐心等待!");

    new RestClient(0, 1000, 5000, 3000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.e(responseString + "--throwable:" + throwable.toString());
        DialogUtils.dismissDialog(queryRefundDialog);
        if (throwable instanceof SocketTimeoutException){//响应超时
          // 继续查询 还是 手动确认
          showCheckResultOrManualConfirm(from,payInfo,payInfoAdapter,pos,act,"响应超时","请继续查询或稍后查询?",outRequestNo,orderInfo);
        }else{
          ToastUtils.showShort(App.getContext(), "连接超时,请检查网络连接");
          //释放
         OptOrderLock.optOrderLock(orderInfo,false);
        }
      }

      @Override protected void success(String responseString) {
        DialogUtils.dismissDialog(queryRefundDialog);
        HttpAlipayResp resp = new Gson().fromJson(responseString, HttpAlipayResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        Logger.json(responseString);
        if (resp.getStatusCode() == 1 && resp.getStatus().equals(HttpAlipayResp.SUCCESS)) { // 查询退款成功
          createRefundInfoAndUpdateOriginInfo(payInfo);
          if (from == FROM_CHECKOUT_FRAGMENT) {
            ClearDiscAndPayInfoDialog.deletePayInfoAndRefreshUI(payInfo, payInfoAdapter, pos);
          }else{
            deletePayInfo(payInfo);
            //更新页面
            EventBus.getDefault().post(new RefreshCashBillListEvent());
            EventBus.getDefault().post(new RefundAlipayEvent());
          }
        } else {

        }
        //释放
        OptOrderLock.optOrderLock(orderInfo,false);
      }
    }.postOther(act, URLConstants.ALI_REFUND_QUERY, refundReq);
  }

  /**
   * 声称一条退款的电子支付信息 并更新原信息为已 付款过并已退款
   */
  private static void createRefundInfoAndUpdateOriginInfo(PxPayInfo payInfo){
    //退款记录
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
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
      ePaymentInfo.setType(EPaymentInfo.TYPE_ALI_PAY);
      DaoServiceUtil.getEPaymentInfoService().saveOrUpdate(ePaymentInfo);
      //修改以前记录为 付款过并已退款
      EPaymentInfo originInfo = DaoServiceUtil.getEPaymentInfoService()
          .queryBuilder()
          .where(EPaymentInfoDao.Properties.TradeNo.eq(payInfo.getTradeNo()))
          .where(EPaymentInfoDao.Properties.Status.eq(EPaymentInfo.STATUS_PAYED))
          .unique();
      originInfo.setStatus(EPaymentInfo.STATUS_PAYED_AND_REFUND);
      DaoServiceUtil.getEPaymentInfoService().saveOrUpdate(originInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }

  }
}  