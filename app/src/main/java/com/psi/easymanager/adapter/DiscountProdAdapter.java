package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOrderDetails;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjq on 2016/4/26.
 * 结账页面 打折对话框 商品适配器
 */
public class DiscountProdAdapter extends BaseAdapter {
  private Context mContext;
  private List<PxOrderDetails> mDetailsList;

  public DiscountProdAdapter(Context context, List<PxOrderDetails> detailsList) {
    mContext = context;
    if (detailsList != null) {
      mDetailsList = detailsList;
    } else {
      mDetailsList = new ArrayList<PxOrderDetails>();
    }
  }

  @Override public int getCount() {
    return mDetailsList.size();
  }

  @Override public Object getItem(int position) {
    return mDetailsList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_discount_prod, parent, false);
      viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb_prod);
      viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_prod_name);
      viewHolder.tvDes = (TextView) convertView.findViewById(R.id.tv_des);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    PxOrderDetails details = mDetailsList.get(position);
    viewHolder.tvName.setText(details.getDbProduct().getName());
    viewHolder.cb.setChecked(details.isDiscountChecked());
    viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mDetailsCbChangedListener.onCbChanged(position, isChecked);
      }
    });
    StringBuilder sb = new StringBuilder();
    String orderStatus = details.getOrderStatus();
    if (orderStatus.equals(PxOrderDetails.ORDER_STATUS_UNORDER)) {
      sb.append("(未下单)");
    } String status = details.getStatus();
    if (status.equals(PxOrderDetails.STATUS_DELAY)) {
      sb.append("(待)");
    }
    PxFormatInfo formatInfo = details.getDbFormatInfo();
    if (formatInfo != null) {
      sb.append("(" + formatInfo.getName() + ")");
    }
    PxMethodInfo methodInfo = details.getDbMethodInfo();
    if (methodInfo != null) {
      sb.append("(" + methodInfo.getName() + ")");
    }
    int discountRate = details.getDiscountRate();
    if (discountRate != 100) {
      sb.append("(折扣" + discountRate + "%)");
    }
    viewHolder.tvDes.setText(sb.toString());
    return convertView;
  }

  static class ViewHolder {
    CheckBox cb;
    TextView tvName;
    TextView tvDes;
  }

  /**
   * CheckBox选择
   */
  public static interface OnDetailsCbChangedListener {
    void onCbChanged(int pos, boolean isChecked);
  }

  public OnDetailsCbChangedListener mDetailsCbChangedListener;

  public void setDetailsCbChangedListener(OnDetailsCbChangedListener detailsCbChangedListener) {
    mDetailsCbChangedListener = detailsCbChangedListener;
  }
}
