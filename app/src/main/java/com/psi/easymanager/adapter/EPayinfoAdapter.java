package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.pay.query.module.PxNetPayRecord;
import java.util.List;

/**
 * Created by dorado on 2016/10/29.
 */
public class EPayinfoAdapter extends BaseAdapter {
  private Context mContext;
  private List<PxNetPayRecord> mNetPayRecordList;

  public EPayinfoAdapter(Context context, List<PxNetPayRecord> netPayRecordList) {
    mContext = context;
    mNetPayRecordList = netPayRecordList;
  }

  @Override public int getCount() {
    return mNetPayRecordList.size();
  }

  @Override public Object getItem(int position) {
    return mNetPayRecordList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_epay_info, null);
      viewHolder = new ViewHolder();
      viewHolder.mTvType = (TextView) convertView.findViewById(R.id.tv_type);
      viewHolder.mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
      viewHolder.mTvStatus = (TextView) convertView.findViewById(R.id.tv_status);
      viewHolder.mTvPrice = (TextView) convertView.findViewById(R.id.tv_price);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    PxNetPayRecord record = mNetPayRecordList.get(position);
    //类型
    if (PxNetPayRecord.TYPE_ALIPAY.equals(record.getType())) {
      viewHolder.mTvType.setText("支付宝");
    } else if (PxNetPayRecord.TYPE_WEIXIN.equals(record.getType())) {
      viewHolder.mTvType.setText("微信");
    } else if (PxNetPayRecord.TYPE_BESTPAY.equals(record.getType())){
      viewHolder.mTvType.setText("翼支付");
    }
    //流水号
    viewHolder.mTvTime.setText(record.getUpdateDate());
    //类型
    if (PxNetPayRecord.STATUS_PAY.equals(record.getStatus())) {
      viewHolder.mTvStatus.setText("付款");
    } else {
      viewHolder.mTvStatus.setText("退款");
    }
    //价格
    viewHolder.mTvPrice.setText(record.getFee() + "元");

    return convertView;
  }

  class ViewHolder {
    TextView mTvType;
    TextView mTvTime;
    TextView mTvStatus;
    TextView mTvPrice;
  }
}
