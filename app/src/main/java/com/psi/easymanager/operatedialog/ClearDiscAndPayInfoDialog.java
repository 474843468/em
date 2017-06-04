package com.psi.easymanager.operatedialog;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.PayInfoAdapter;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.pay.alipay.RefundAlipay;
import com.psi.easymanager.pay.bestpay.RefundBestPay;
import com.psi.easymanager.pay.vip.RefundVip;
import com.psi.easymanager.pay.vip.RefundVipCard;
import com.psi.easymanager.pay.wxpay.WxRefundPay;
import com.psi.easymanager.utils.ListViewHeightUtil;
import com.psi.easymanager.utils.ToastUtils;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by dorado on 2016/9/13.清空支付
 */
public class ClearDiscAndPayInfoDialog {
  //@formatter:off
  public static void dialogOperate(final MaterialDialog dialog, final PxOrderInfo currentOrderInfo, final Activity act) {
    //ListView
    ListView lvPayInfo = (ListView) dialog.getCustomView().findViewById(R.id.lv_pay_info);
    //数据
    final List<PxPayInfo> payInfoList = DaoServiceUtil.getPayInfoService()
        .queryBuilder()
        .where(PxPayInfoDao.Properties.PxOrderInfoId.eq(currentOrderInfo.getId()))
        .list();
    //Adapter
    final PayInfoAdapter payInfoAdapter = new PayInfoAdapter(act, payInfoList);
    lvPayInfo.setAdapter(payInfoAdapter);
    //重新计算高度
    ListViewHeightUtil.setListViewHeightBasedOnChildren(lvPayInfo);
    //删除按钮点击
    payInfoAdapter.setOnPayInfoDelClickListener(new PayInfoAdapter.OnPayInfoDelClickListener() {
      @Override public void onPayInfoDelClick(final int pos) {
        new MaterialDialog.Builder(act).title("警告")
            .content("是否清空该支付信息?")
            .positiveText("确认")
            .negativeText("取消")
            .negativeColor(act.getResources().getColor(R.color.primary_text))
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override public void onClick(MaterialDialog dialog, DialogAction which) {
                //本次支付信息
                PxPayInfo payInfo = payInfoList.get(pos);
                //支付方式类型
                 String type = payInfo.getPaymentType();
                //支付宝
                if (PxPaymentMode.TYPE_ALIPAY.equals(type)) {
                  RefundAlipay.refundAlipay(RefundAlipay.FROM_CHECKOUT_FRAGMENT,act,payInfo,payInfoAdapter, pos);
                }
                //微信
                else if (PxPaymentMode.TYPE_WEIXIN.equals(type)){
                  WxRefundPay.refundWxPay(WxRefundPay.FROM_CHECKOUT_FRAGMENT,act,payInfo,payInfoAdapter, pos);
                }
                //翼支付
                else if (PxPaymentMode.TYPE_WINGPAY.equals(type)){
                  RefundBestPay.refundBestPay(RefundBestPay.FROM_CHECKOUT_FRAGMENT,act,payInfo,payInfoAdapter, pos);
                }
                else if (PxPaymentMode.TYPE_POS.equals(type)){
                  ToastUtils.showShort(act,"银行卡支付不能清空,请线下协商解决");
                }
                //会员
                else if (PxPaymentMode.TYPE_VIP.equals(type)){
                  if (payInfo.getIdCardNum()!=null&&payInfo.getIdCardNum()!=""){
                RefundVipCard.refundVipCard(RefundVipCard.FROM_CHECKOUT_FRAGMENT,act,payInfo,payInfoAdapter,pos);//会员卡退款
                }else {
                 RefundVip.refundVip(RefundVip.FROM_CHECKOUT_FRAGMENT,act,payInfo,payInfoAdapter,pos);//会员退款
                }

                }
                else{
                  //删除 PayInfo 并且更新UI
                  deletePayInfoAndRefreshUI(payInfo, payInfoAdapter, pos);
                }
              }
            })
            .canceledOnTouchOutside(false)
            .show();
      }
    });

  }
  /**
   * 删除指定PayInfo 并更新UI
   */
  public static void deletePayInfoAndRefreshUI(PxPayInfo payInfo, PayInfoAdapter payInfoAdapter, int pos) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //删除支付信息
      DaoServiceUtil.getPayInfoService().delete(payInfo);
      db.setTransactionSuccessful();
      //更新ListView
      payInfoAdapter.removeData(pos);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
    //更新页面
    EventBus.getDefault().post(new RefreshCashBillListEvent());
  }

}
