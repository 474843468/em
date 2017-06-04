package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.AppPayInfoCollect;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorado on 2016/10/20.
 */
public class CheckoutPayinfoAdapter extends BaseAdapter {
  private Context mContext;
  private List<AppPayInfoCollect> mPayInfoList;

  public CheckoutPayinfoAdapter(Context context, List<AppPayInfoCollect> payInfoList) {
    mContext = context;
    if (payInfoList == null) {
      mPayInfoList = new ArrayList<AppPayInfoCollect>();
    } else {
      mPayInfoList = payInfoList;
    }
  }

  @Override public int getCount() {
    return mPayInfoList.size();
  }

  @Override public Object getItem(int position) {
    return mPayInfoList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_checkout_pay_info, null);
      viewHolder = new ViewHolder();
      viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
      viewHolder.mTvMoney = (TextView) convertView.findViewById(R.id.tv_money);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    AppPayInfoCollect payInfoCollect = mPayInfoList.get(position);
    viewHolder.mTvName.setText((position + 1) + ". " + payInfoCollect.getName() + "");
    viewHolder.mTvMoney.setText(NumberFormatUtils.formatFloatNumber(payInfoCollect.getReceived())+ "");
    return convertView;
  }

  class ViewHolder {
    TextView mTvName;
    TextView mTvMoney;
  }
}
