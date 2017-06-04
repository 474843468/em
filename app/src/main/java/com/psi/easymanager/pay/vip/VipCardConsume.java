package com.psi.easymanager.pay.vip;

import android.app.Activity;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.VipConsumeEvent;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxVipCardInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpIdCardConsumeReq;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.pay.help.OptOrderLock;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.net.SocketTimeoutException;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by lj on 2017-1-10.
 * id卡消费
 */
//@formatter:off
public class VipCardConsume {
  public static void vipConsume(final Activity act, final PxVipCardInfo vipCardInfo, final double amount,
      final PxPaymentMode paymentMode, final PxOrderInfo orderInfo) {
    //初始化 信息
    final App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动!");
      EventBus.getDefault().post(new VipConsumeEvent(false));
      return;
    }
   final   String userId = user.getObjectId();
    final   String companyCode = user.getCompanyCode();
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
   final String cid = office.getObjectId();

    final HttpIdCardConsumeReq req = new HttpIdCardConsumeReq();
  //  final  String idCardNum = vipCardInfo.getIdcardNum();
    req.setCardId(vipCardInfo.getObjectId());//会员卡ID
    req.setMobile(vipCardInfo.getMobile());//会员手机号
    final String orderNo = orderInfo.getOrderNo();
    req.setOrderId(orderNo);//订单编号
    req.setAmount(amount);//消费金额
    final String tradeNo = Math.abs(companyCode.hashCode()) + "VIP" + UUID.randomUUID().toString().replaceAll("\\-", "");
    req.setTradeNo(tradeNo);//交易流水号
    req.setCid(cid);
    req.setUserId(userId);
    req.setCompanyCode(companyCode);


    final MaterialDialog payDialog = DialogUtils.showDialog(act, "会员支付", "发送请求中请耐心等待!");
    //加锁
    OptOrderLock.optOrderLock(orderInfo,true);
    new RestClient(0, 1000, 10000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i("fail:"+responseString);
        EventBus.getDefault().post(new VipConsumeEvent(false));
        DialogUtils.dismissDialog(payDialog);
        if (throwable instanceof SocketTimeoutException){ //服务器响应超时 需要继续查询
          //ToastUtils.showShort(app,"服务器响应超时，请稍后查询!");
          showCheckResultDialog(act,vipCardInfo,amount,tradeNo,paymentMode,orderNo,orderInfo);
        }else{//连接异常
          ToastUtils.showShort(app,"连接异常，请检查网络");
          OptOrderLock.optOrderLock(orderInfo,false);
        }
      }

      @Override protected void success(String responseString) {
        DialogUtils.dismissDialog(payDialog);
        Logger.json(responseString);
        HttpResp resp = RestClient.getGson().fromJson(responseString, HttpResp.class);
        ToastUtils.showShort(app, resp.getMsg() + "");
        //1000:消费成功  10001:余额不足 1002:已经消费成功
        //不可能存在 1002
        if (resp.getStatusCode() == 1000) {
          EventBus.getDefault().post(new VipConsumeEvent(true, paymentMode, amount, null,vipCardInfo, tradeNo));
        } else {
          EventBus.getDefault().post(new VipConsumeEvent(false));
        }
        //释放
        OptOrderLock.optOrderLock(orderInfo,false);
      }
    }.postOther(app, URLConstants.VIP_CARD_CONSUME, req);
  }

  /**
   * 继续查询 dialog
   */
  private static void showCheckResultDialog(final Activity act, final PxVipCardInfo vipCardInfo, final double amount, final String tradeNo,
      final PxPaymentMode paymentMode, final String orderNo,final PxOrderInfo orderInfo) {
    final MaterialDialog queryDialog = DialogUtils.showContinueQuery(act, "会员消费", "继续查询", "稍后查询");
    MDButton posBtn = queryDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = queryDialog.getActionButton(DialogAction.NEGATIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(queryDialog);
        //继续查询
        continueQuery(act,vipCardInfo,amount,tradeNo,paymentMode,orderNo,orderInfo);
      }
    });
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(queryDialog);
         //释放
        OptOrderLock.optOrderLock(orderInfo,false);
      }
    });
  }

  /**
   * 继续查询 结果
   */
  private static void continueQuery(final Activity act, final PxVipCardInfo vipCardInfo, final double amount, final String tradeNo,
      final PxPaymentMode paymentMode, final String orderNo,final PxOrderInfo orderInfo) {
       //初始化 信息
    final App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动!");
      EventBus.getDefault().post(new VipConsumeEvent(false));
      return;
    }
    String userId = user.getObjectId();
    String companyCode = user.getCompanyCode();
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    String cid = office.getObjectId();

    final HttpIdCardConsumeReq req = new HttpIdCardConsumeReq();
    req.setCardId(vipCardInfo.getObjectId());//会员卡ID
    req.setMobile(vipCardInfo.getMobile());
    req.setOrderId(orderNo);//订单编号
    req.setAmount(amount);//消费金额
    req.setTradeNo(tradeNo);//交易流水号
    req.setCid(cid);
    req.setUserId(userId);
    req.setCompanyCode(companyCode);


    final MaterialDialog queryDialog = DialogUtils.showDialog(act, "会员支付", "查询中,请等待!");
    new RestClient(0, 1000, 10000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i(responseString);
        EventBus.getDefault().post(new VipConsumeEvent(false));
        DialogUtils.dismissDialog(queryDialog);
        if (throwable instanceof SocketTimeoutException){ //服务器响应超时 需要继续查询
          //ToastUtils.showShort(app,"服务器响应超时，请稍后查询!");
          showCheckResultDialog(act,vipCardInfo,amount,tradeNo,paymentMode,orderNo,orderInfo);
        }else{//连接异常
          ToastUtils.showShort(app,"连接异常，请检查网络");
           //释放
          OptOrderLock.optOrderLock(orderInfo,false);
        }
      }

      @Override protected void success(String responseString) {
        DialogUtils.dismissDialog(queryDialog);
        Logger.json(responseString);
        HttpResp resp = RestClient.getGson().fromJson(responseString, HttpResp.class);
        ToastUtils.showShort(app, resp.getMsg() + "");
        //1000:消费成功  1001:余额不足 1002:已经消费成功
        //可能存在 1000
        if (resp.getStatusCode() == 1000) {
          EventBus.getDefault().post(new VipConsumeEvent(true, paymentMode, amount, null,vipCardInfo, tradeNo));
        } else if (resp.getStatusCode() == 1002){
          EventBus.getDefault().post(new VipConsumeEvent(true, paymentMode, amount,null, vipCardInfo, tradeNo));
        }else{
          EventBus.getDefault().post(new VipConsumeEvent(false));
        }
         //释放
        OptOrderLock.optOrderLock(orderInfo,false);
      }
    }.postOther(app, URLConstants.VIP_CARD_CONSUME, req);

  }
}
