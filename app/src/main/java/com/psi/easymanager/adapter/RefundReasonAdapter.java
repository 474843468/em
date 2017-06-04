package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxOptReason;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorado on 2016/8/5.
 */
public class RefundReasonAdapter extends BaseAdapter {
  private Context mContext;
  private List<PxOptReason> mOptReasonList;

  public RefundReasonAdapter(Context context, List<PxOptReason> optReasonList) {
    mContext = context;
    if (optReasonList == null) {
      mOptReasonList = new ArrayList<PxOptReason>();
    } else {
      mOptReasonList = optReasonList;
    }
  }

  @Override public int getCount() {
    return mOptReasonList.size();
  }

  @Override public Object getItem(int position) {
    return mOptReasonList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_refund_reason, null);
      holder = new Holder();
      holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
      convertView.setTag(holder);
    } else {
      holder = (Holder) convertView.getTag();
    }
    PxOptReason reason = mOptReasonList.get(position);
    holder.mTvName.setText(reason.getName());
    return convertView;
  }

  class Holder {
    TextView mTvName;
  }

  public void setData(List<PxOptReason> data) {
    if (data != null) {
      this.mOptReasonList = data;
      this.notifyDataSetChanged();
    }
  }
}
