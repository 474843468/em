package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.AppShiftCateInfo;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorado on 2016/8/2.
 */

public class DayReportCateAdapter extends BaseAdapter {
  private Context mContext;
  private List<AppShiftCateInfo> mShiftCateInfoList;

  public DayReportCateAdapter(Context context, List<AppShiftCateInfo> shiftCateInfoList) {
    mContext = context;
    if (shiftCateInfoList == null) {
      this.mShiftCateInfoList = new ArrayList<AppShiftCateInfo>();
    } else {
      this.mShiftCateInfoList = shiftCateInfoList;
    }
  }

  @Override public int getCount() {
    return mShiftCateInfoList.size();
  }

  @Override public Object getItem(int position) {
    return mShiftCateInfoList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_day_report_cate, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.mTvCateName = (TextView) convertView.findViewById(R.id.tv_cate_name);
      viewHolder.mTvCateNum = (TextView) convertView.findViewById(R.id.tv_cate_num);
      viewHolder.mTvReceivableMoney = (TextView) convertView.findViewById(R.id.tv_receivable_money);
      viewHolder.mTvActualMoney = (TextView) convertView.findViewById(R.id.tv_actual_money);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    AppShiftCateInfo appShiftCateInfo = mShiftCateInfoList.get(position);
    viewHolder.mTvCateName.setText(appShiftCateInfo.getCateName());
    viewHolder.mTvCateNum.setText(appShiftCateInfo.getCateNumber() + "");
    viewHolder.mTvActualMoney.setText(NumberFormatUtils.formatFloatNumber(appShiftCateInfo.getActualAmount()) + "");
    viewHolder.mTvReceivableMoney.setText(NumberFormatUtils.formatFloatNumber(appShiftCateInfo.getReceivableAmount())+"");
    return convertView;
  }

  class ViewHolder {
    TextView mTvCateName;
    TextView mTvCateNum;
    TextView mTvReceivableMoney;
    TextView mTvActualMoney;
  }

  /**
   * 设置数据
   */
  public void setData(List<AppShiftCateInfo> data) {
    if (data != null) {
      this.mShiftCateInfoList = data;
      this.notifyDataSetChanged();
    }
  }
}
