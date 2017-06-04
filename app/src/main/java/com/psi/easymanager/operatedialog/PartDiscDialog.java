package com.psi.easymanager.operatedialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.DiscountProdAdapter;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.HideProgressEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.ShowProgressEvent;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.utils.ListViewHeightUtil;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by dorado on 2016/9/13.
 */
public class PartDiscDialog {
  public static void dialogOperate(final MaterialDialog dialog, final List<PxOrderDetails> detailsList,Context act) {
    final MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
    //折扣输入
    final EditText etDiscount = (EditText) dialog.getCustomView().findViewById(R.id.et_part_discount);
    //不允许打折的商品是否计入折扣
    final CheckBox cbPartDisc = (CheckBox) dialog.getCustomView().findViewById(R.id.cb_part_discount);
    etDiscount.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        //监听输入，改变确认按钮是否可用
        if (s.toString() == null || s.toString().trim().equals("")) {
          positiveBtn.setEnabled(false);
        } else {
          Integer discount = Integer.valueOf(s.toString().toString());
          if (discount > 0 && discount < 99) {
            positiveBtn.setEnabled(true);
          } else {
            etDiscount.setText("");
            positiveBtn.setEnabled(false);
          }
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });

    //详情列表
    ListView lvPartDiscount = (ListView) dialog.getCustomView().findViewById(R.id.lv_part_discount_product);
    DiscountProdAdapter adapter = new DiscountProdAdapter(act, detailsList);
    //ListView中CheckBox选择状态监听
    adapter.setDetailsCbChangedListener(new DiscountProdAdapter.OnDetailsCbChangedListener() {
      @Override public void onCbChanged(int pos, boolean isChecked) {
        //标记该商品为部分打折选中
        detailsList.get(pos).setIsDiscountChecked(isChecked);
      }
    });
    //设置ListView
    lvPartDiscount.setAdapter(adapter);
    lvPartDiscount.setDivider(null);
    //重新计算高度
    ListViewHeightUtil.setListViewHeightBasedOnChildren(lvPartDiscount);
    //显示对话框
    dialog.show();
    positiveBtn.setEnabled(false);
    //确认按钮点击
    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //折扣率
        int discount = Integer.parseInt(etDiscount.getText().toString());
        //处理部分打折
        operateDetailsPartDisc(discount, cbPartDisc.isChecked(), detailsList);
        //关闭
        dialog.dismiss();
      }
    });
  }

  /**
   * 处理部分打折
   */
  //@formatter:off
  private static void operateDetailsPartDisc(int discount, boolean discAll,
      List<PxOrderDetails> detailsList) {
    try {
      if (detailsList != null && detailsList.size() != 0) {
        //开启蒙层
        EventBus.getDefault().post(new ShowProgressEvent());
      }
      for (PxOrderDetails details : detailsList) {
        //未选中，跳过
        if (!details.isDiscountChecked()) continue;
        //不允许打折的商品也打折
        if (discAll) {
          modifyDetailsByPartDisc(discount, details);
        }
        //仅对允许打折的商品打折
        else {
          //商品是否允许打折
          boolean canNotDisc = details.getDbProduct().getIsDiscount().equals(PxProductInfo.IS_DISCOUNT_FALSE);
          //如果不允许，处理下一条数据
          if (canNotDisc) continue;
          modifyDetailsByPartDisc(discount, details);
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
   * 部分打折 更改Details状态
   */
  private static void modifyDetailsByPartDisc(int discount, PxOrderDetails details) {
    details.setDiscountRate(discount);
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);
  }
}
