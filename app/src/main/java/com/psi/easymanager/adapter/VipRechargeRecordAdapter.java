package com.psi.easymanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import com.psi.easymanager.R;

import com.psi.easymanager.module.PxRechargeRecord;

import com.psi.easymanager.utils.NumberFormatUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * created wangzhen 2016/10/06
 * 充值记录适配器
 */
public class VipRechargeRecordAdapter
    extends RecyclerView.Adapter<VipRechargeRecordAdapter.ViewHolder> {
  private Context mContext;
  private List<PxRechargeRecord> mRechargeRecordList;
  private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public VipRechargeRecordAdapter(Context context, List<PxRechargeRecord> rechargeRecordList) {
    mContext = context;
    if (rechargeRecordList == null) {
      this.mRechargeRecordList = new ArrayList<>();
    } else {
      this.mRechargeRecordList = rechargeRecordList;
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_vip_recharge_record, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    if (mRechargeRecordList != null) {
      PxRechargeRecord record = mRechargeRecordList.get(position);
      holder.tvRecordTime.setText(format.format(record.getRechargeTime()));
      holder.tvRecordMoney.setText(NumberFormatUtils.formatFloatNumber(record.getMoney()) + "");
      holder.tvRecordGiving.setText(NumberFormatUtils.formatFloatNumber(record.getGiving()) + "");
      if (listener != null) {
        holder.tvRecordAction.setOnClickListener(new View.OnClickListener() {

          @Override public void onClick(View v) {
            listener.onRechargeRecordItemClick(position);
          }
        });
      }
    }
  }

  @Override public int getItemCount() {
    return mRechargeRecordList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_vip_recharge_record_time) TextView tvRecordTime;
    @Bind(R.id.tv_vip_recharge_record_money) TextView tvRecordMoney;
    @Bind(R.id.tv_vip_recharge_record_giving) TextView tvRecordGiving;
    @Bind(R.id.btn_vip_recharge_record_action) Button tvRecordAction;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 添加数据
   */
  public void setData(List<PxRechargeRecord> rechargeRecordList) {
    if (rechargeRecordList != null) {
      mRechargeRecordList = rechargeRecordList;
      this.notifyDataSetChanged();
    }
  }

  /**
   * 点击事件
   */
  public interface onRechargeRecordClickListener {
    void onRechargeRecordItemClick(int pos);
  }

  public onRechargeRecordClickListener listener;

  public void setListener(onRechargeRecordClickListener listener) {
    this.listener = listener;
  }
}
