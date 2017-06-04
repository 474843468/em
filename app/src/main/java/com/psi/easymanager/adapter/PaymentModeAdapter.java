package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxPaymentMode;
import java.util.List;

/**
 * Created by dorado on 2016/12/27.
 */

public class PaymentModeAdapter extends BaseAdapter {
  private Context mContext;
  private List<PxPaymentMode> mPaymentModeList;

  public PaymentModeAdapter(Context context, List<PxPaymentMode> list) {
    mContext = context;
    mPaymentModeList = list;
  }

  @Override public int getCount() {
    return mPaymentModeList.size();
  }

  @Override public Object getItem(int position) {
    return mPaymentModeList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    VH vh = null;
    PxPaymentMode paymentMode = mPaymentModeList.get(position);
    if (convertView == null) {
      vh = new VH();
      convertView = View.inflate(mContext, R.layout.item_payment_mode, null);
      vh.tv = (TextView) convertView.findViewById(R.id.tv_payment_mode);
      convertView.setTag(vh);
    } else {
      vh = (VH) convertView.getTag();
    }
    if (paymentMode.getEdit() != null && paymentMode.getEdit().equals(PxPaymentMode.EDIT_FALSE)) {
      vh.tv.setText("[" + paymentMode.getName() + "]");
    } else {
      vh.tv.setText(paymentMode.getName());
    }
    return convertView;
  }

  class VH {
    private TextView tv;
  }
}