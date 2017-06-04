package com.psi.easymanager.pay.alipay;

import android.app.Activity;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.OnLinePaySuccessEvent;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.AlipayReq;
import com.psi.easymanager.network.req.AlipaySearchReq;
import com.psi.easymanager.network.resp.HttpAlipayResp;
import com.psi.easymanager.pay.help.OptOrderLock;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.net.SocketTimeoutException;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

/**
 * User: ylw
 * Date: 2016-10-20
 * Time: 11:46
 * 发起支付宝付款
 */
public class Alipay {
  /**
   * 发起支付宝付款
   *
   * @param orderInfo 订单信息
   * @param payMoney 付款金额
   * @param authCode 条码
   */
  //@formatter:off
  public static void reqAlipay(final Activity act, final PxOrderInfo orderInfo, final String payMoney, String authCode) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动!");
      return;
    }
    String companyCode = user.getCompanyCode();
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    String subject = office.getName();
    String userId = user.getObjectId();

    //当次交易流水号
    String orderNo = orderInfo.getOrderNo();
    final String tradeNo = orderNo + "_" + UUID.randomUUID().toString().replaceAll("\\-", "");

    AlipayReq alipayReq = new AlipayReq();
    alipayReq.setCompanyCode(companyCode);
    alipayReq.setAuthCode(authCode);
    alipayReq.setOutTradeNo(tradeNo);
    alipayReq.setSubject(subject);
    alipayReq.setTotalAmount(payMoney);
    alipayReq.setUserId(userId);
    alipayReq.setOrderNo(orderNo);
    alipayReq.setSelfTradeNo(tradeNo);

    final MaterialDialog payDialog = DialogUtils.showDialog(act, "支付宝支付", "发送请求中请耐心等待!");
    //枷锁
     OptOrderLock.optOrderLock(orderInfo,true);
    new RestClient(0, 1000, 40000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i("statusCode:" + "--responseString:" + responseString + "--throwable:" + throwable.toString());
        //ConnectException  ConnectTimeoutException  SocketTimeoutException
        DialogUtils.dismissDialog(payDialog);
        if (throwable instanceof SocketTimeoutException) {//响应超时
          //继续查询或手动确认结果
          showCheckResultOrManualConfirm(act, "响应超时", "服务器响应超时,请继续查询或稍后查询?", tradeNo,payMoney,orderInfo);
        } else {
          ToastUtils.showShort(App.getContext(), "连接超时,请检查网络连接");
          //释放
           OptOrderLock.optOrderLock(orderInfo,false);
        }
      }

      @Override protected void success(String responseString) {
        Logger.i("statusCode:" + "--Success:" + responseString);
        //dismiss dialog
        DialogUtils.dismissDialog(payDialog);
        Gson gson = new Gson();
        HttpAlipayResp resp = gson.fromJson(responseString, HttpAlipayResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg() + "");
        if (resp.getStatusCode() == 1 && resp.getStatus().equals(HttpAlipayResp.SUCCESS)) { //成功
          //订单完成后通知CheckOutFragment界面更新
          EventBus.getDefault().post(new OnLinePaySuccessEvent(OnLinePaySuccessEvent.ALI_PAY,tradeNo,Double.valueOf(payMoney)));
        }else{
        }
         //释放
        OptOrderLock.optOrderLock(orderInfo,false);
      }
    }.postOther(act, URLConstants.ALI_TRADE, alipayReq);
  }

  /**
   * 继续查询 或
   */
  private static void showCheckResultOrManualConfirm(final Activity act, String title, String content, final String tradeNo,
      final String payMoney,final PxOrderInfo orderInfo) {
    final MaterialDialog dialog = DialogUtils.showCheckResultOrManualConfirm(act, title, content);
    MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(dialog);
        queryResult(act, tradeNo,payMoney,orderInfo);
      }
    });
    MDButton negBtn = dialog.getActionButton(DialogAction.NEGATIVE);
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //释放
        OptOrderLock.optOrderLock(orderInfo,false);
        DialogUtils.dismissDialog(dialog);
      }});
  }

  /**
   * 继续查询支付结果
   */
  private static void queryResult(final Activity act, final String tradeNo,final String payMoney,final PxOrderInfo orderInfo) {
    AlipaySearchReq searchReq = getAlipaySearchReq(tradeNo);
    final MaterialDialog searchDialog = DialogUtils.showDialog(act, "查询结果", "查询中请耐心等待!");

    new RestClient(0, 10000, 20000, 10000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        DialogUtils.dismissDialog(searchDialog);
        if (throwable instanceof SocketTimeoutException) {//响应超时
          // 继续查询 还是 手动确认
          showCheckResultOrManualConfirm(act, "查询失败", "请继续查询或稍后查询?", tradeNo,payMoney,orderInfo);
        } else {
          ToastUtils.showShort(App.getContext(), "连接服务器失败，请稍后查询!");
          //释放
          OptOrderLock.optOrderLock(orderInfo,false);
        }
      }

      @Override protected void success(String responseString) {
        Logger.i("--Success:" + responseString);
        DialogUtils.dismissDialog(searchDialog);
        Gson gson = new Gson();
        HttpAlipayResp resp = gson.fromJson(responseString, HttpAlipayResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        //处理查询结果
        if (resp.getStatusCode() == 1 && resp.getStatus().equals(HttpAlipayResp.SUCCESS)) {//支付成功
          //订单完成后通知CheckOutFragment界面更新
          EventBus.getDefault().post(new OnLinePaySuccessEvent(OnLinePaySuccessEvent.ALI_PAY,tradeNo, Double.valueOf(payMoney)));
        } else {

        }
        //释放
        OptOrderLock.optOrderLock(orderInfo,false);
      }
    }.postOther(act, URLConstants.ALI_TRADE_QUERY, searchReq);
  }

  /**
   * 获取Req
   */
  private static AlipaySearchReq getAlipaySearchReq(String tradeNo) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动");
      return null;
    }
    String userId = user.getObjectId();
    String companyCode = user.getCompanyCode();

    AlipaySearchReq searchReq = new AlipaySearchReq();
    searchReq.setUserId(userId);
    searchReq.setCompanyCode(companyCode);
    searchReq.setOutTradeNo(tradeNo);
    searchReq.setSelfTradeNo(tradeNo);
    return searchReq;
  }
}  