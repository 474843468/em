package com.psi.easymanager.operatedialog;

import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.HideProgressEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.ShowProgressEvent;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by dorado on 2016/9/13.
 */
public class EntiretyDiscDialog {

  public static void dialogOperate(final MaterialDialog dialog, final PxOrderInfo currentOrderInfo) {
    //确认按钮
    final MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
    //输入框
    final EditText etDiscount = (EditText) dialog.getCustomView().findViewById(R.id.et_all_discount);
    etDiscount.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        //监听输入，改变确认按钮是否可用
        if (s.toString() == null || s.toString().trim().equals("")) {
          positiveBtn.setEnabled(false);
        }else {
          Integer discount = Integer.valueOf(s.toString().toString());
          if (discount > 0 && discount < 100 ){
            positiveBtn.setEnabled(true);
          }else {
            etDiscount.setText("");
            positiveBtn.setEnabled(false);
          }
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    //对设置不允许打折的商品是否打折
    final CheckBox cbAllowDiscount = (CheckBox) dialog.getCustomView().findViewById(R.id.cb_all_discount);
    //显示对话框
    dialog.show();
    positiveBtn.setEnabled(false);

    //确认点击
    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
        db.beginTransaction();
        try {
          //开启蒙层
          EventBus.getDefault().post(new ShowProgressEvent());
          //折扣率
          int discount = Integer.parseInt(etDiscount.getText().toString());
          //获取所有未支付的Details
          List<PxOrderDetails> detailsList = DaoServiceUtil.getOrderDetailsDao()
              .queryBuilder()
              .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(currentOrderInfo.getId()))
              .list();
          //遍历所有详情,给详情设置该折扣率
          for (PxOrderDetails details : detailsList) {
            PxProductInfo product = details.getDbProduct();
            //可以对设置不允许打折的商品打折
            if (cbAllowDiscount.isChecked()) {
              details.setDiscountRate(discount);
            }
            //禁止对设置不允许打折的商品打折
            else {
              //仅对允许打折的商品打折
              if (product.getIsDiscount().equals(PxProductInfo.IS_DISCOUNT_TRUE)) {
                details.setDiscountRate(discount);
              }
            }
          }
          DaoServiceUtil.getOrderDetailsService().saveOrUpdate(detailsList);
          dialog.dismiss();
          //通知CashBill刷新
          EventBus.getDefault().post(new RefreshCashBillListEvent());
          db.setTransactionSuccessful();
        } catch (Exception e) {
          e.printStackTrace();
          //关闭蒙层
          EventBus.getDefault().post(new HideProgressEvent());
        } finally {
          db.endTransaction();
        }
      }
    });
  }
}
