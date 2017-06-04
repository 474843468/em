package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxVoucher;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorado on 2016/10/20.
 * 优惠券适配器
 */
public class CouponAdapter extends BaseAdapter {
  private Context mContext;
  private List<PxVoucher> mVoucherList;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  public CouponAdapter(Context context, List<PxVoucher> voucherList) {
    mContext = context;
    if (voucherList == null) {
      mVoucherList = new ArrayList<PxVoucher>();
    } else {
      mVoucherList = voucherList;
    }
  }

  @Override public int getCount() {
    return mVoucherList.size();
  }

  @Override public Object getItem(int position) {
    return mVoucherList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_coupon, null);
      viewHolder = new ViewHolder();
      viewHolder.mTvMoney = (TextView) convertView.findViewById(R.id.tv_money);
      viewHolder.mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    PxVoucher pxVoucher = mVoucherList.get(position);
    viewHolder.mTvMoney.setText(pxVoucher.getPrice() + "元");
    if (PxVoucher.PERMANENT_TRUE.equals(pxVoucher.getPermanent())) {
      viewHolder.mTvTime.setText("[永久]");
    } else {
      viewHolder.mTvTime.setText("[" + sdf.format(pxVoucher.getStartDate()) + "~" + sdf.format(pxVoucher.getEndDate()) + "]");
    }
    return convertView;
  }

  class ViewHolder {
    TextView mTvMoney;
    TextView mTvTime;
  }
}
