package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.List;

/**
 * Created by dorado on 2016/11/22.
 */
public class GuestShowAdapter extends BaseAdapter {

  private Context mContext;
  private List<PxOrderDetails> mDetailsList;

  public GuestShowAdapter(Context context, List<PxOrderDetails> detailsList) {
    mContext = context;
    mDetailsList = detailsList;
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

  //@formatter:off
  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_guest_show, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
      viewHolder.mTvNum = (TextView) convertView.findViewById(R.id.tv_num);
      viewHolder.mTvMultNum = (TextView) convertView.findViewById(R.id.tv_mult_num);
      viewHolder.mTvPrice = (TextView) convertView.findViewById(R.id.tv_price);
      viewHolder.mTvFormat = (TextView) convertView.findViewById(R.id.tv_format);
      viewHolder.mTvMethod = (TextView) convertView.findViewById(R.id.tv_method);
      viewHolder.mTvRemarks = (TextView) convertView.findViewById(R.id.tv_remarks);
      viewHolder.mRlExtra = (RelativeLayout) convertView.findViewById(R.id.rl_guest_extra);
      viewHolder.mDivider = convertView.findViewById(R.id.divider_2);
      viewHolder.mTvOrderStatus = (TextView) convertView.findViewById(R.id.tv_order_status);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    PxOrderDetails details = mDetailsList.get(position);
    viewHolder.mTvName.setText(details.getDbProduct().getName());
    viewHolder.mTvOrderStatus.setVisibility(View.GONE);
    if (PxOrderDetails.ORDER_STATUS_ORDER.equals(details.getOrderStatus())){
      viewHolder.mTvOrderStatus.setVisibility(View.VISIBLE);
      viewHolder.mTvOrderStatus.setText("已下单");
      viewHolder.mTvOrderStatus.setTextColor(mContext.getResources().getColor(R.color.green));
    } else if (PxOrderDetails.ORDER_STATUS_UNORDER.equals(details.getOrderStatus())){
      viewHolder.mTvOrderStatus.setVisibility(View.VISIBLE);
      viewHolder.mTvOrderStatus.setText("已确认");
      viewHolder.mTvOrderStatus.setTextColor(mContext.getResources().getColor(R.color.material_blue));
    }
    //双单位数量
    viewHolder.mTvMultNum.setVisibility(View.GONE);
    //双单位
    if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      viewHolder.mTvMultNum.setVisibility(View.VISIBLE);
      viewHolder.mTvNum.setText("(" + NumberFormatUtils.formatFloatNumber(details.getNum()) + details.getDbProduct().getOrderUnit() + ")");
      viewHolder.mTvMultNum.setText("(" + NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber()) + details.getDbProduct().getUnit() + ")");
    }
    //非双单位
    else {
      viewHolder.mTvNum.setText("(" + NumberFormatUtils.formatFloatNumber(details.getNum()) + details.getDbProduct().getUnit() + ")");
    }
    //价格
    if (details.getDbOrder().getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
      viewHolder.mTvPrice.setText(NumberFormatUtils.formatFloatNumber(details.getVipPrice() * details.getDiscountRate() / 100) + "元");
    } else {
      viewHolder.mTvPrice.setText(NumberFormatUtils.formatFloatNumber(details.getPrice() * details.getDiscountRate() / 100) + "元");
    }
    viewHolder.mRlExtra.setVisibility(View.GONE);
    viewHolder.mDivider.setVisibility(View.GONE);
    //规格
    if (details.getDbFormatInfo() != null) {
      viewHolder.mTvFormat.setText("(" + details.getDbFormatInfo().getName() + ")");
      viewHolder.mRlExtra.setVisibility(View.VISIBLE);
      viewHolder.mDivider.setVisibility(View.VISIBLE);
    }
    //做法
    if (details.getDbMethodInfo() != null) {
      viewHolder.mTvMethod.setText("(" + details.getDbMethodInfo().getName() + ")");
      viewHolder.mRlExtra.setVisibility(View.VISIBLE);
      viewHolder.mDivider.setVisibility(View.VISIBLE);
    }
    //备注
    if (details.getRemarks() != null && "无".equals(details.getRemarks()) == false) {
      viewHolder.mTvRemarks.setText("(" + details.getRemarks() + ")");
      viewHolder.mRlExtra.setVisibility(View.VISIBLE);
      viewHolder.mDivider.setVisibility(View.VISIBLE);
    }
    return convertView;
  }

  class ViewHolder {
    TextView mTvName;
    TextView mTvNum;
    TextView mTvMultNum;
    TextView mTvPrice;
    TextView mTvFormat;
    TextView mTvMethod;
    TextView mTvRemarks;
    RelativeLayout mRlExtra;
    View mDivider;
    TextView mTvOrderStatus;
  }

  public void setData(List<PxOrderDetails> data) {
    mDetailsList = data;
    this.notifyDataSetInvalidated();
  }
}
