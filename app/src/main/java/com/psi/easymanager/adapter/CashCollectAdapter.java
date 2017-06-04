package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.AppCashCollect;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.List;

/**
 * Created by dorado on 2016/10/17.
 */
public class CashCollectAdapter extends BaseAdapter {
  private Context mContext;
  private List<AppCashCollect> mCashCollectList;

  public CashCollectAdapter(Context context, List<AppCashCollect> cashCollectList) {
    mContext = context;
    mCashCollectList = cashCollectList;
  }

  @Override public int getCount() {
    return mCashCollectList.size();
  }

  @Override public Object getItem(int position) {
    return mCashCollectList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_cash_collect, null);
      viewHolder = new ViewHolder();
      viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
      viewHolder.mTvNum = (TextView) convertView.findViewById(R.id.tv_num);
      viewHolder.mTvMoney = (TextView) convertView.findViewById(R.id.tv_money);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    AppCashCollect appCashCollect = mCashCollectList.get(position);
    viewHolder.mTvName.setText(appCashCollect.getName());
    viewHolder.mTvNum.setText(appCashCollect.getNum() + "");
    viewHolder.mTvMoney.setText(NumberFormatUtils.formatFloatNumber(appCashCollect.getMoney()) + "");
    return convertView;
  }

  class ViewHolder {
    TextView mTvName;
    TextView mTvNum;
    TextView mTvMoney;
  }
}
