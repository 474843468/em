package com.psi.easymanager.pay.vip;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.adapter.PayInfoAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.EPaymentInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.RefundAlipayEvent;
import com.psi.easymanager.event.VipConsumeEvent;
import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpReverseConsumeReq;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.operatedialog.ClearDiscAndPayInfoDialog;
import com.psi.easymanager.pay.help.OptOrderLock;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import de.greenrobot.dao.query.QueryBuilder;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * User: ylw
 * Date: 2016-10-23
 * Time: 17:57
 * 会员退款
 */
//@formatter:off
public class RefundVip {
  public static final int FROM_CHECKOUT_FRAGMENT = 1;
  public static final int FROM_MESSAGES = 2;

  public static void refundVip(final int from, final Activity act, final PxPayInfo payInfo,
      final PayInfoAdapter payInfoAdapter, final int pos) {
    final App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动!");
      return;
    }
    final PxOrderInfo dbOrder = payInfo.getDbOrder();
    if (dbOrder == null) return;
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();
    final String tradeNo = payInfo.getTradeNo();
    final String vipId = payInfo.getVipId();
    final String orderNo = dbOrder.getOrderNo();
    final HttpReverseConsumeReq req = new HttpReverseConsumeReq();
    req.setUserId(userId);
    req.setCompanyCode(companyCode);
    req.setTradeNo(tradeNo);
    req.setVipId(vipId);
    req.setOrderNo(orderNo);
    req.setSelfTradeNo(tradeNo);
    final MaterialDialog refundDialog = DialogUtils.showDialog(act, "会员退款", "退款中,请耐心等待!");
    //加锁
    OptOrderLock.optOrderLock(dbOrder,true);
    new RestClient(0, 1000, 10000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.json(responseString);
        DialogUtils.dismissDialog(refundDialog);
        if (throwable instanceof SocketTimeoutException) {// TODO 服务器响应超时 需在查询
          ToastUtils.showShort(app, "服务器响应超时,继续查询或稍后查询!");
          showContinueDialog(act,tradeNo,vipId,orderNo,payInfo,payInfoAdapter,pos,from,dbOrder);
        } else { //
          ToastUtils.showShort(app, "连接超时,请检查网络连接!");
          //释放
          OptOrderLock.optOrderLock(dbOrder,false);
        }
      }

      @Override protected void success(String responseString) {
        DialogUtils.dismissDialog(refundDialog);
        Logger.json(responseString);
        HttpResp resp = RestClient.getGson().fromJson(responseString, HttpResp.class);
        ToastUtils.showShort(app, "退款成功");
        //1001 不存在 或 已退
        if (resp.getStatusCode() == 1000) {
          //生成一条退款 EPaymentInfo 并且更新付款的记录为 付款过并已退款
          createRefundInfoAndUpdateOriginInfo(payInfo);
          if (from == FROM_CHECKOUT_FRAGMENT) {
            ClearDiscAndPayInfoDialog.deletePayInfoAndRefreshUI(payInfo, payInfoAdapter, pos);
          } else {
            deletePayInfo(payInfo);
            EventBus.getDefault().post(new RefundAlipayEvent());
          }
          //处理 该订单不再使用会员价
          dealOrderIsUseVip(payInfo);
          //更新页面
          EventBus.getDefault().post(new RefreshCashBillListEvent());
        } else {
        }
        //释放
        OptOrderLock.optOrderLock(dbOrder,false);
      }
    }.postOther(app, URLConstants.VIP_CONSUME_RECORD_REVERSE, req);
  }

  /**
   * 查询退款结果
   */
  //@formatter:off
  private static void showContinueDialog(final Activity act, final String tradeNo, final String vipId, final String orderNo,final PxPayInfo payInfo,
      final PayInfoAdapter payInfoAdapter,final int pos,final int from,final PxOrderInfo orderInfo) {
    final MaterialDialog queryDialog = DialogUtils.showContinueQuery(act, "会员退款", "继续查询", "稍后查询");
    MDButton posBtn = queryDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = queryDialog.getActionButton(DialogAction.NEGATIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
       DialogUtils.dismissDialog(queryDialog);
        continueQueryResult(act,tradeNo,vipId,orderNo,payInfo,payInfoAdapter,pos,from,orderInfo);
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
  //@formatter:on

  /**
   * 继续查询 退款结果
   */
  private static void continueQueryResult(final Activity act, final String tradeNo, final String vipInfoId, final String orderNo,final PxPayInfo payInfo,
      final PayInfoAdapter payInfoAdapter,final int pos,final int from,final PxOrderInfo orderInfo) {
    //初始化 信息
    final App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(App.getContext(), "APP发生异常，请重新启动!");
      EventBus.getDefault().post(new VipConsumeEvent(false));
      return;
    }
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();

    final HttpReverseConsumeReq req = new HttpReverseConsumeReq();
    req.setUserId(userId);
    req.setCompanyCode(companyCode);
    req.setTradeNo(tradeNo);
    req.setVipId(vipInfoId);
    req.setOrderNo(orderNo);
    req.setSelfTradeNo(tradeNo);

    final MaterialDialog refundQueryDialog = DialogUtils.showDialog(act, "会员退款", "查询中,请耐心等待!");

    new RestClient(0, 1000, 10000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.json(responseString);
        DialogUtils.dismissDialog(refundQueryDialog);
        if (throwable instanceof SocketTimeoutException) {// TODO 服务器响应超时 需在查询
          showContinueDialog(act,tradeNo,vipInfoId,orderNo,payInfo,payInfoAdapter,pos,from,orderInfo);
        } else { //
          ToastUtils.showShort(app, "连接超时,请检查网络连接!");
          //释放
          OptOrderLock.optOrderLock(orderInfo,false);
        }
      }

      @Override protected void success(String responseString) {
        DialogUtils.dismissDialog(refundQueryDialog);
        Logger.json(responseString);
        HttpResp resp = RestClient.getGson().fromJson(responseString, HttpResp.class);
        ToastUtils.showShort(app, resp.getMsg() + "");
        //1001 不存在 或 已退
        if (resp.getStatusCode() == 1000) {
          //生成一条退款 EPaymentInfo 并且更新付款的记录为 付款过并已退款
          createRefundInfoAndUpdateOriginInfo(payInfo);
          if (from == FROM_CHECKOUT_FRAGMENT) {
            ClearDiscAndPayInfoDialog.deletePayInfoAndRefreshUI(payInfo, payInfoAdapter, pos);
          } else {
            deletePayInfo(payInfo);
            EventBus.getDefault().post(new RefundAlipayEvent());
          }
          //处理 该订单是否继续使用会员价
          dealOrderIsUseVip(payInfo);
          //更新页面
          EventBus.getDefault().post(new RefreshCashBillListEvent());
        } else {
        }
        //释放
        OptOrderLock.optOrderLock(orderInfo,false);
      }
    }.postOther(app, URLConstants.VIP_CONSUME_RECORD_REVERSE, req);

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

    } finally {
      db.endTransaction();
    }
  }

  /**
   * 声称一条退款的电子支付信息 并更新原信息为已 付款过并已退款
   */
  private static void createRefundInfoAndUpdateOriginInfo(PxPayInfo payInfo) {
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
      ePaymentInfo.setType(EPaymentInfo.TYPE_VIP_PAY);
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
      Logger.e(e.toString());
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 该订单下不再有会员支付信息 恢复订单不用会员价
   */
  private static void dealOrderIsUseVip(PxPayInfo payInfo) {
    if (payInfo == null) return;
    PxOrderInfo dbOrder = payInfo.getDbOrder();
    if (dbOrder == null) return;
    QueryBuilder<PxPayInfo> queryBuilder = DaoServiceUtil.getPayInfoService().queryBuilder();
    queryBuilder.where(PxPayInfoDao.Properties.PxOrderInfoId.eq(dbOrder.getId()));
    queryBuilder.where(PxPayInfoDao.Properties.PaymentType.eq(PxPaymentMode.TYPE_VIP));
    List<PxPayInfo> vipPayList = queryBuilder.list();
    if (vipPayList == null || vipPayList.size() == 0) {
      SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
      db.beginTransaction();
      try {
        dbOrder.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
        DaoServiceUtil.getOrderInfoService().update(dbOrder);
        db.setTransactionSuccessful();
      } catch (Exception e) {

      } finally {
        db.endTransaction();
      }
    }
  }
}