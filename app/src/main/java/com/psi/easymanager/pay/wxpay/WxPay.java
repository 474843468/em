package com.psi.easymanager.pay.wxpay;

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
import com.psi.easymanager.network.req.HttpWeiXinPayQueryReq;
import com.psi.easymanager.network.req.HttpWeiXinPayReq;
import com.psi.easymanager.network.req.HttpWeiXinReverseReq;
import com.psi.easymanager.network.req.ScanPayQueryReqData;
import com.psi.easymanager.network.req.ScanPayReqData;
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
 * 发起微信付款
 */

public class WxPay {
  /**
   * 发起微信付款
   *
   * @param orderInfo 订单信息
   * @param payMoney 付款金额
   * @param authCode 条码
   */
  //@formatter:off
  public static void reqWxPay(final Activity act, final PxOrderInfo orderInfo,
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
    Date startData = new Date();
    //设置当前时间推迟10分钟
    Date expireDate = new Date(startData.getTime() + 600000);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    //当次交易流水号
    final String tradeNo = getUUID();

    ScanPayReqData scanPayReqData = new ScanPayReqData();
    scanPayReqData.setAuth_code(authCode);
    scanPayReqData.setBody("逸掌柜微信支付");
    scanPayReqData.setAttach(office.getName());
    scanPayReqData.setOut_trade_no(tradeNo);
    scanPayReqData.setTotal_fee((int) (Double.valueOf(payMoney) * 100));
    scanPayReqData.setDevice_info(office.getObjectId());
    scanPayReqData.setSpbill_create_ip("192.168.1.66");
    scanPayReqData.setTime_start(sdf.format(startData));
    scanPayReqData.setTime_expire(sdf.format(expireDate));
    scanPayReqData.setGoods_tag("不优惠");
    HttpWeiXinPayReq weiXinPayReq = new HttpWeiXinPayReq();
    weiXinPayReq.setUserId(userId);
    weiXinPayReq.setCompanyCode(companyCode);
    weiXinPayReq.setScanPayReqData(scanPayReqData);
    final String orderNo = orderInfo.getOrderNo();
    weiXinPayReq.setOrderNo(orderNo);

    weiXinPayReq.setSelfTradeNo(tradeNo);

    final MaterialDialog payDialog = DialogUtils.showDialog(act, "微信支付", "发送请求中请耐心等待!");
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
          showCheckResultOrManualConfirm(act, "响应超时", "服务器响应超时,请继续查询或关闭?", tradeNo, payMoney,
              orderNo, orderInfo);
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
        HttpResp resp = gson.fromJson(responseString, HttpResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        if (resp.getStatusCode() == HttpResp.SUCCESS) {//支付成功
          //订单完成后通知CheckOutFragment界面更新
          double money = Double.valueOf(payMoney);
          EventBus.getDefault()
              .post(new OnLinePaySuccessEvent(OnLinePaySuccessEvent.WX_PAY, tradeNo, money));
          //释放
          OptOrderLock.optOrderLock(orderInfo, false);
        } else {
          //释放
          OptOrderLock.optOrderLock(orderInfo, false);
        }
      }
    }.postOther(act, URLConstants.WEIXIN_PAY, weiXinPayReq);
  }

  /**
   * 继续查询 或 手动确认结果
   */
  private static void showCheckResultOrManualConfirm(final Activity act, String title,
      String content, final String tradeNo, final String payMoney, final String orderNo,
      final PxOrderInfo orderInfo) {
    final MaterialDialog dialog =
        DialogUtils.showCheckResultOrManualConfirmWithWeiXin(act, title, content);
    MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(dialog);
        queryResult(act, tradeNo, payMoney, orderNo, orderInfo);
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
      final String orderNo, final PxOrderInfo orderInfo) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动");
      return;
    }
    String userId = user.getObjectId();
    String companyCode = user.getCompanyCode();

    HttpWeiXinPayQueryReq weiXinPayQueryReq = new HttpWeiXinPayQueryReq();
    weiXinPayQueryReq.setUserId(userId);
    weiXinPayQueryReq.setCompanyCode(companyCode);
    ScanPayQueryReqData scanPayQueryReqData = new ScanPayQueryReqData();
    scanPayQueryReqData.setOut_trade_no(tradeNo);
    weiXinPayQueryReq.setScanPayQueryReqData(scanPayQueryReqData);
    weiXinPayQueryReq.setOrderNo(orderNo);

    weiXinPayQueryReq.setSelfTradeNo(tradeNo);

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
              orderInfo);
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
        HttpResp resp = gson.fromJson(responseString, HttpResp.class);
        ToastUtils.showShort(App.getContext(), resp.getMsg());
        //处理查询结果
        //1001：转入退款、1002：未支付、1003：已关闭、1004：已撤销、1005：用户支付中、1006：未支付（确认支付超时）、1007：支付失败（其他原因）
        if (resp.getStatusCode() == HttpResp.SUCCESS) {//查询后支付成功
          //订单完成后通知CheckOutFragment界面更新
          double money = Double.valueOf(payMoney);
          EventBus.getDefault()
              .post(new OnLinePaySuccessEvent(OnLinePaySuccessEvent.WX_PAY, tradeNo, money));
          //释放
          OptOrderLock.optOrderLock(orderInfo, false);
        } else if (resp.getStatusCode() == 1005) {
          showCheckResultOrManualConfirm(act, "用户支付中", "请继续查询或关闭?", tradeNo, payMoney, orderNo,
              orderInfo);
        } else {
          ToastUtils.showShort(App.getContext(), resp.getMsg() + "");
          //释放
          OptOrderLock.optOrderLock(orderInfo, false);
        }
      }
    }.postOther(act, URLConstants.WEIXIN_PAY_QUERY, weiXinPayQueryReq);
  }

  /**
   * 生成32为UUID
   */
  private static String getUUID() {
    String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
    return uuid;
  }
}
