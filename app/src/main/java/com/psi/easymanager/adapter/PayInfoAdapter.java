package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dorado on 2016/6/5.
 */
public class PayInfoAdapter extends BaseAdapter {
  private Context mContext;
  private List<PxPayInfo> mPxPayInfoList;
  private SimpleDateFormat mSdf;

  public PayInfoAdapter(Context context, List<PxPayInfo> pxPayInfoList) {
    mContext = context;
    mPxPayInfoList = pxPayInfoList;
    mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  }

  @Override public int getCount() {
    return mPxPayInfoList.size();
  }

  @Override public Object getItem(int position) {
    return mPxPayInfoList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pay_info, null);
      viewHolder = new ViewHolder();
      viewHolder.tvType = (TextView) convertView.findViewById(R.id.tv_pay_type);
      viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_pay_time);
      viewHolder.ivDel = (ImageView) convertView.findViewById(R.id.iv_del);
      viewHolder.tvMoney = (TextView) convertView.findViewById(R.id.tv_pay_money);
      viewHolder.tvTicketCode = (TextView) convertView.findViewById(R.id.tv_ticket_code);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    PxPayInfo payInfo = mPxPayInfoList.get(position);
    if (PxPaymentMode.TYPE_VIP.equals(payInfo.getPaymentType())){
      //支付名称
      if (payInfo.getIdCardNum()!=null&&payInfo.getIdCardNum()!=""){
        viewHolder.tvType.setText("实体卡");
      }else {
        viewHolder.tvType.setText("手机");
      }
    }else {
      viewHolder.tvType.setText(payInfo.getPaymentName());
    }

    viewHolder.tvTime.setText(mSdf.format(payInfo.getPayTime()));
    viewHolder.tvMoney.setText(payInfo.getReceived() - payInfo.getChange() + "");
    if (PxPaymentMode.TYPE_GROUP_COUPON.equals(payInfo.getPaymentType())){
      String ticketCode = payInfo.getTicketCode();
      String codeSub4 = ticketCode.substring(ticketCode.length() - 4);
      viewHolder.tvTicketCode.setText("验券码:" + codeSub4);
    } else {
      viewHolder.tvTicketCode.setText("");
    }
    //删除click
    if (mOnPayInfoDelClickListener != null) {
      viewHolder.ivDel.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mOnPayInfoDelClickListener.onPayInfoDelClick(position);
        }
      });
    }
    return convertView;
  }

  class ViewHolder {
    TextView tvType;
    TextView tvTime;
    ImageView ivDel;
    TextView tvMoney;
    TextView tvTicketCode;
  }

  /**
   * Click
   */
  public static interface OnPayInfoDelClickListener {
    void onPayInfoDelClick(int pos);
  }

  public OnPayInfoDelClickListener mOnPayInfoDelClickListener;

  public void setOnPayInfoDelClickListener(OnPayInfoDelClickListener onPayInfoDelClickListener) {
    mOnPayInfoDelClickListener = onPayInfoDelClickListener;
  }

  /**
   * 删除信息
   */
  public void removeData(int pos) {
    Logger.v("---pos"+pos);
    this.mPxPayInfoList.remove(pos);
    this.notifyDataSetChanged();
  }
}
