package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxBuyCoupons;
import com.psi.easymanager.module.PxVoucher;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorado on 2016/10/20.
 * 团购券适配器
 */
public class BuyCouponAdapter extends BaseAdapter {
  private Context mContext;
  private List<PxBuyCoupons> mBuyCouponsList;

  public BuyCouponAdapter(Context context, List<PxBuyCoupons> buyCouponsList) {
    mContext = context;
    if (buyCouponsList == null) {
      mBuyCouponsList = new ArrayList<PxBuyCoupons>();
    } else {
      mBuyCouponsList = buyCouponsList;
    }
  }

  @Override public int getCount() {
    return mBuyCouponsList.size();
  }

  @Override public Object getItem(int position) {
    return mBuyCouponsList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_buy_coupon, null);
      viewHolder = new ViewHolder();
      viewHolder.mTvName= (TextView) convertView.findViewById(R.id.tv_name);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    PxBuyCoupons pxBuyCoupons = mBuyCouponsList.get(position);
    viewHolder.mTvName.setText(pxBuyCoupons.getName());
    return convertView;
  }

  class ViewHolder {
    TextView mTvName;
  }
}
