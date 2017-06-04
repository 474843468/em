package com.psi.easymanager.pay.wxpay;

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
import com.psi.easymanager.network.req.HttpWeiXinReturnQueryReq;
import com.psi.easymanager.network.req.HttpWeiXinReturnReq;
import com.psi.easymanager.network.req.RefundQueryReqData;
import com.psi.easymanager.network.req.RefundReqData;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.operatedialog.ClearDiscAndPayInfoDialog;
import com.psi.easymanager.pay.help.OptOrderLock;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

import static com.psi.easymanager.print.constant.BTPrintConstants.office;

/**
 * Created by psi on 2016/10/20.
 * 微信退款
 */

public class WxRefundPay {
  public static final int FROM_CHECKOUT_FRAGMENT = 1;
  public static final int FROM_MESSAGES = 0;
  /**
   * 微信发起退款
   */
  public static void refundWxPay(final int from ,final Activity act ,final PxPayInfo payInfo, final PayInfoAdapter payInfoAdapter, final int pos) {
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
    final String tradeNo = payInfo.getTradeNo();
    double money = payInfo.getReceived();
    //当次交易退单流水号
    final String refundNumber = UUID.randomUUID().toString().trim().replaceAll("-", "");

    String userId = user.getObjectId();
    String companyCode = user.getCompanyCode();
    Office office = DaoServiceUtil.getOfficeDao().queryBuilder().unique();
    final PxOrderInfo dbOrder = payInfo.getDbOrder();
    if (dbOrder == null) return;

    HttpWeiXinReturnReq returnReq = new HttpWeiXinReturnReq();
    returnReq.setUserId(userId);
    returnReq.setCompanyCode(companyCode);
    RefundReqData refundReqData = new RefundReqData();
    refundReqData.setOut_trade_no(tradeNo);
    refundReqData.setDevice_info(office.getObjectId());
    refundReqData.setOut_refund_no(refundNumber);
    refundReqData.setTotal_fee((int)(money*100));
    refundReqData.setRefund_fee((int)(money*100));
    refundReqData.setOp_user_id(userId);
    refundReqData.setRefund_fee_type("CNY");

    returnReq.setScanRefundReqData(refundReqData);
    final String orderNo = dbOrder.getOrderNo();
    returnReq.setOrderNo(orderNo);

    returnReq.setSelfTradeNo(tradeNo);

    final MaterialDialog refundDialog = DialogUtils.showDialog(act, "微信退款", "退款中,请耐心等待!");
    //枷锁
    OptOrderLock.optOrderLock(dbOrder,true);

    new RestClient(0, 1000, 10000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.e(responseString + "--throwable:" + throwable.toString());
        DialogUtils.dismissDialog(refundDialog);
        if (throwable instanceof SocketTimeoutException){//响应超时
          // 继续查询 还是 手动确认
          showCheckResultOrManualConfirm(from,refundNumber,payInfo,payInfoAdapter,pos,act,"响应超时","请继续查询或稍后查询?",orderNo,dbOrder);
        }else{
          ToastUtils.showShort(App.getContext(), "连接超时,请检查网络连接");
          //释放
          OptOrderLock.optOrderLock(dbOrder,false);
        }
      }

      @Override protected void success(String responseString) {
        Logger.json(responseString);
        DialogUtils.dismissDialog(refundDialog);
        HttpResp resp = new Gson().fromJson(responseString, HttpResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        if(resp.getStatusCode() == HttpResp.SUCCESS){//发起退款请求成功
          //生成一条退款 EPaymentInfo 并且更新付款的记录为 付款过并已退款
          createRefundInfoAndUpdateOriginInfo(payInfo);
          if(from == FROM_CHECKOUT_FRAGMENT){
            ClearDiscAndPayInfoDialog.deletePayInfoAndRefreshUI(payInfo,payInfoAdapter,pos);
          }else {
            deletePayInfo(payInfo);
            //更新页面
            EventBus.getDefault().post(new RefreshCashBillListEvent());
            EventBus.getDefault().post(new RefundAlipayEvent());
          }
        }
        //释放
        OptOrderLock.optOrderLock(dbOrder,false);
      }
    }.postOther(act, URLConstants.WEIXIN_RETURN, returnReq);
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
  private static void showCheckResultOrManualConfirm(final int from ,final String refundNumber,final PxPayInfo payInfo, final PayInfoAdapter payInfoAdapter,
      final int pos,final Activity act, String title, String content,final String orderNo,final PxOrderInfo orderInfo) {
    final MaterialDialog dialog = DialogUtils.showCheckResultOrManualConfirm(act, title, content);
    MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(dialog);
        queryRefundResult(from,refundNumber,act,payInfo,payInfoAdapter,pos,orderNo,orderInfo);
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
  private static void queryRefundResult(final int from ,final String refundNumber,final Activity act,final PxPayInfo payInfo,
      final PayInfoAdapter payInfoAdapter, final int pos,final String orderNo,final PxOrderInfo orderInfo) {
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

    RefundQueryReqData refundQueryReqData = new RefundQueryReqData();
    refundQueryReqData.setOut_trade_no(tradeNo);
    refundQueryReqData.setDevice_info(office.getObjectId());
    refundQueryReqData.setOut_refund_no(refundNumber);
    HttpWeiXinReturnQueryReq refundQueryReq = new HttpWeiXinReturnQueryReq();
    refundQueryReq.setRefundQueryReqData(refundQueryReqData);
    refundQueryReq.setUserId(userId);
    refundQueryReq.setCompanyCode(companyCode);
    refundQueryReq.setOrderNo(orderNo);

    refundQueryReq.setSelfTradeNo(tradeNo);
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
          showCheckResultOrManualConfirm(from,refundNumber,payInfo,payInfoAdapter,pos,act,"响应超时","请继续查询或稍后查询?",orderNo,orderInfo);
        }else{
          ToastUtils.showShort(App.getContext(), "连接超时,请稍后查询!");
          //释放
          OptOrderLock.optOrderLock(orderInfo,false);
        }
      }

      @Override protected void success(String responseString) {
        Logger.json(responseString);
        DialogUtils.dismissDialog(queryRefundDialog);
        HttpResp resp = new Gson().fromJson(responseString, HttpResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        if(resp.getStatusCode() == HttpResp.SUCCESS){
          createRefundInfoAndUpdateOriginInfo(payInfo);
          if(from == FROM_CHECKOUT_FRAGMENT){
            ClearDiscAndPayInfoDialog.deletePayInfoAndRefreshUI(payInfo,payInfoAdapter,pos);//查询退款成功
          }else {
            deletePayInfo(payInfo);
            //更新页面
            EventBus.getDefault().post(new RefreshCashBillListEvent());
            EventBus.getDefault().post(new RefundAlipayEvent());
          }
        }
        //释放
        OptOrderLock.optOrderLock(orderInfo,false);
      }
    }.postOther(act, URLConstants.WEIXIN_RETURN_QUERY, refundQueryReq);
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
      ePaymentInfo.setType(EPaymentInfo.TYPE_WX_PAY);
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
      Logger.e(e.toString());
    } finally {
      db.endTransaction();
    }

  }
}
