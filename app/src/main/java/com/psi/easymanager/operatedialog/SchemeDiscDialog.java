package com.psi.easymanager.operatedialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.DiscountSchemeAdapter;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.HideProgressEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.ShowProgressEvent;
import com.psi.easymanager.module.PxDiscounScheme;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.utils.ListViewHeightUtil;
import com.psi.easymanager.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
/**
 * Created by lj on 2016/11/23.
 */
public class SchemeDiscDialog {
  //@formatter:off
  public static void dialogOperate(final MaterialDialog dialog,final List<PxDiscounScheme> schemeList,final List<PxOrderDetails> detailsList,final Context act) {
    final MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
    //不允许打折的商品是否计入折扣
    final CheckBox cbSchemeDisc = (CheckBox) dialog.getCustomView().findViewById(R.id.cb_scheme_discount);
    //打折方案列表
    ListView lvSchemeDiscount = (ListView) dialog.getCustomView().findViewById(R.id.lv_discount_scheme);
    DiscountSchemeAdapter adapter = new DiscountSchemeAdapter(act, schemeList);
    //设置ListView
    lvSchemeDiscount.setAdapter(adapter);
    lvSchemeDiscount.setDivider(null);
    //重新计算高度
    ListViewHeightUtil.setListViewHeightBasedOnChildren(lvSchemeDiscount);
    //显示对话框
    dialog.show();
    //确认按钮点击
    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int pos = -1;
        for (PxDiscounScheme scheme : schemeList) {
          if (scheme.isSelected()){
            pos = schemeList.indexOf(scheme);
          }
        }
        //处理打折
        if (pos == -1){
          ToastUtils.showShort(act,"请选择一个折扣方案");
        } else {
          operateDetailsSchemeDisc(schemeList.get(pos).getRate(), cbSchemeDisc.isChecked(), detailsList);
          //关闭
          dialog.dismiss();
        }
      }
    });
  }
  /**
   * 处理打折方案
   */
  //@formatter:off
  private static void operateDetailsSchemeDisc(int discount, boolean discAll, List<PxOrderDetails> detailsList) {
    try {
      if (detailsList != null && detailsList.size() != 0) {
        //开启蒙层
        EventBus.getDefault().post(new ShowProgressEvent());
      }
      for (PxOrderDetails details : detailsList) {
        //不允许打折的商品也打折
        if (discAll) {
          modifyDetailsBySchemeDisc(discount, details);
        } else {
          //商品是否允许打折
          boolean canNotDisc = details.getDbProduct().getIsDiscount().equals(PxProductInfo.IS_DISCOUNT_FALSE);
          //如果不允许，处理下一条数据
          if (canNotDisc) continue;
          modifyDetailsBySchemeDisc(discount, details);
        }
      }
      //通知CashBill刷新
      EventBus.getDefault().post(new RefreshCashBillListEvent());
    } catch (Exception e) {
      e.printStackTrace();
      //关闭蒙层
      EventBus.getDefault().post(new HideProgressEvent());
    }
  }

  /**
   * 打折 更改Details状态
   */
  private static void modifyDetailsBySchemeDisc(int discount, PxOrderDetails details) {
    details.setDiscountRate(discount);
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);
  }
}
