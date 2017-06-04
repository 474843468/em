package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.psi.easymanager.R;

import com.psi.easymanager.module.PxOrderInfo;

import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * created wangzhen 2016/10/06
 * 会员消费记录适配器
 */
public class VipConsumeRecordAdapter
    extends RecyclerView.Adapter<VipConsumeRecordAdapter.ViewHolder> {
  private Context mContext;
  private List<PxOrderInfo> orderInfoList;


  public VipConsumeRecordAdapter(Context context, List<PxOrderInfo> orderInfoList) {
    mContext = context;
    if (orderInfoList == null) {
      this.orderInfoList = new ArrayList<>();
    } else {
      this.orderInfoList = orderInfoList;
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(mContext).inflate(R.layout.item_vip_recharge_record, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    PxOrderInfo record = orderInfoList.get(position);
    holder.tvRecordTime.setText(record.getStartTime().toLocaleString());
    holder.tvRecordMoney.setText(NumberFormatUtils.formatFloatNumber(record.getRealPrice()) + "");
  }

  @Override public int getItemCount() {
    return orderInfoList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_vip_consume_record_time) TextView tvRecordTime;
    @Bind(R.id.tv_vip_consume_record_money) TextView tvRecordMoney;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 添加数据
   */
  public void setData(List<PxOrderInfo> consumeRecordList) {
    if (consumeRecordList == null) return;
    orderInfoList.clear();

    orderInfoList.addAll(consumeRecordList);

    this.notifyDataSetChanged();
  }

  /**
   * 清除数据
   */
  public void clearData() {
    orderInfoList.clear();
    this.notifyDataSetChanged();
  }

  //  /**
  //   * 点击事件
  //   */
  //  public interface onRechargeRecordClickListener {
  //    void onRechargeRecordItemClick(int pos);
  //  }
  //
  //  public onRechargeRecordClickListener listener;
  //
  //  public void setListener(onRechargeRecordClickListener listener) {
  //    this.listener = listener;
  //  }
}
