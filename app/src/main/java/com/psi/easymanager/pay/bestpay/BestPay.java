package com.psi.easymanager.pay.bestpay;

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
import com.psi.easymanager.network.req.BestPayReq;
import com.psi.easymanager.network.req.BestQueryReq;
import com.psi.easymanager.network.req.BestReverseReq;
import com.psi.easymanager.network.resp.BestPayResp;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.pay.help.OptOrderLock;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by psi on 2016/10/20.
 * 发起翼支付付款
 */

public class BestPay {
  /**
   * 发起翼支付付款
   *
   * @param orderInfo 订单信息
   * @param payMoney 付款金额
   * @param authCode 条码
   */
  //@formatter:off
  public static void reqBestPay(final Activity act, final PxOrderInfo orderInfo,
      final String payMoney, String authCode) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动!");
      return;
    }
    //所需参数
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();
    Office office = DaoServiceUtil.getOfficeDao().queryBuilder().unique();
    final Date orderDate = new Date();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    //当做交易流水号
    final String orderReqNo = getUUID();

    BestPayReq bestPayReq = new BestPayReq();
    bestPayReq.setAttach(office.getObjectId());
    bestPayReq.setAttachAmt("0");
    bestPayReq.setBarcode(authCode);
    bestPayReq.setCid(office.getObjectId());
    bestPayReq.setCompanyCode(companyCode);
    bestPayReq.setGoodsName("消费");
    bestPayReq.setOrderAmt(String.valueOf((int) (Double.valueOf(payMoney) * 100)));
    bestPayReq.setOrderDate(sdf.format(orderDate));
    bestPayReq.setOrderNo(orderInfo.getOrderNo());
    bestPayReq.setOrderReqNo(orderReqNo);
    bestPayReq.setPageNo(0);
    bestPayReq.setPageSize(0);
    bestPayReq.setProductAmt(String.valueOf((int) (Double.valueOf(payMoney) * 100)));
    bestPayReq.setStoreId(office.getObjectId());
    bestPayReq.setUserId(userId);

    final MaterialDialog payDialog = DialogUtils.showDialog(act, "翼支付支付中", "发送请求中请耐心等待!");
    //加锁
    OptOrderLock.optOrderLock(orderInfo, true);
    new RestClient(0, 10000, 45000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        //ConnectException  ConnectTimeoutException  SocketTimeoutException
        DialogUtils.dismissDialog(payDialog);
        if (throwable instanceof SocketTimeoutException) {//响应超时
          //继续查询或手动确认结果
          showCheckResultOrManualConfirm(act, "响应超时", "服务器响应超时,请继续查询或关闭?", orderReqNo, payMoney,
              orderInfo.getOrderNo(), orderInfo, sdf.format(orderDate));
        } else {
          ToastUtils.showShort(App.getContext(), "连接超时,请检查网络连接");
          //释放
          OptOrderLock.optOrderLock(orderInfo, false);
        }
      }

      @Override protected void success(String responseString) {
        Logger.i("statusCode:" + "--Success:" + responseString);
        //dismiss dialog
        DialogUtils.dismissDialog(payDialog);
        Gson gson = new Gson();
        BestPayResp resp = gson.fromJson(responseString, BestPayResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        if (resp.isSuccess()) {
          BestPaySuccessResult result = resp.getResult();
          if (BestPaySuccessResult.SUCCESS.equals(result.getTransStatus())) {
            //订单完成后通知CheckOutFragment界面更新
            double money = Double.valueOf(payMoney);
            EventBus.getDefault()
                .post(new OnLinePaySuccessEvent(OnLinePaySuccessEvent.BEST_PAY, orderReqNo, money));
            //释放
            OptOrderLock.optOrderLock(orderInfo, false);
          } else {
            //释放
            OptOrderLock.optOrderLock(orderInfo, false);
          }
        } else {
          //释放
          OptOrderLock.optOrderLock(orderInfo, false);
        }
      }
    }.postOther(act, URLConstants.BEST_PAY, bestPayReq);
  }

  /**
   * 继续查询 或 手动确认结果
   */
  private static void showCheckResultOrManualConfirm(final Activity act, String title,
      String content, final String tradeNo, final String payMoney, final String orderNo,
      final PxOrderInfo orderInfo, final String orderDate) {
    final MaterialDialog dialog =
        DialogUtils.showCheckResultOrManualConfirmWithBestPay(act, title, content);
    MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(dialog);
        queryResult(act, tradeNo, payMoney, orderNo, orderInfo, orderDate);
      }
    });
    MDButton negBtn = dialog.getActionButton(DialogAction.NEGATIVE);
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //释放
        OptOrderLock.optOrderLock(orderInfo, false);
        DialogUtils.dismissDialog(dialog);
      }
    });
  }

  /**
   * 继续查询支付结果
   */
  private static void queryResult(final Activity act, final String tradeNo, final String payMoney,
      final String orderNo, final PxOrderInfo orderInfo, final String orderDate) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动");
      return;
    }
    String userId = user.getObjectId();
    String companyCode = user.getCompanyCode();
    Office office = DaoServiceUtil.getOfficeDao().queryBuilder().unique();

    BestQueryReq bestQueryReq = new BestQueryReq();
    bestQueryReq.setCid(office.getObjectId());
    bestQueryReq.setCompanyCode(companyCode);
    bestQueryReq.setOrderDate(orderDate);
    bestQueryReq.setOrderNo(orderNo);
    bestQueryReq.setOrderReqNo(tradeNo);
    bestQueryReq.setPageNo(0);
    bestQueryReq.setPageSize(0);
    bestQueryReq.setUserId(userId);

    final MaterialDialog searchDialog = DialogUtils.showDialog(act, "查询结果", "查询中请耐心等待!");

    new RestClient(0, 10000, 20000, 10000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i("statusCode:" + "--responseString:" + responseString + "--throwable:"
            + throwable.toString());
        DialogUtils.dismissDialog(searchDialog);
        if (throwable instanceof SocketTimeoutException) {//响应超时
          // 继续查询 还是 手动确认
          showCheckResultOrManualConfirm(act, "查询失败", "请继续查询或关闭?", tradeNo, payMoney, orderNo,
              orderInfo, orderDate);
        } else {
          ToastUtils.showShort(App.getContext(), "连接服务器失败，请稍后查询!");
          //释放
          OptOrderLock.optOrderLock(orderInfo, false);
        }
      }

      @Override protected void success(String responseString) {
        Logger.i("--Success:" + responseString);
        DialogUtils.dismissDialog(searchDialog);
        Gson gson = new Gson();
        BestPayResp resp = gson.fromJson(responseString, BestPayResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        if (resp.isSuccess()) {
          //返回结果A支付中 B支付成功 C支付失败 G订单作废
          BestPaySuccessResult result = resp.getResult();
          if (BestPaySuccessResult.SUCCESS.equals(result.getTransStatus())) {//查询后支付成功
            //订单完成后通知CheckOutFragment界面更新
            double money = Double.valueOf(payMoney);
            EventBus.getDefault()
                .post(new OnLinePaySuccessEvent(OnLinePaySuccessEvent.BEST_PAY, tradeNo, money));
            //释放
            OptOrderLock.optOrderLock(orderInfo, false);
          } else if (BestPaySuccessResult.UNDERWAY.equals(result.getTransStatus())) {
            //继续查询或手动确认结果
            showCheckResultOrManualConfirm(act, "用户输入密码中", "请继续查询或关闭?", tradeNo, payMoney,
                orderInfo.getOrderNo(), orderInfo, orderDate);
          } else {
            //释放
            OptOrderLock.optOrderLock(orderInfo, false);
          }
        } else {
          //释放
          OptOrderLock.optOrderLock(orderInfo, false);
        }
      }
    }.postOther(act, URLConstants.BEST_PAY_QUERY, bestQueryReq);
  }

  /**
   * 生成32为UUID
   */
  private static String getUUID() {
    String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
    return uuid;
  }
}
