package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxOrderInfo;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dorado on 2016/8/4.
 */
public class RetailOrderAdapter extends RecyclerView.Adapter<RetailOrderAdapter.ViewHolder> {

  private Context mContext;
  private List<PxOrderInfo> mOrderInfoList;
  private SimpleDateFormat mSdf;
  private int mCurrentSelected = -1;//当前选中

  public RetailOrderAdapter(Context context, List<PxOrderInfo> orderInfoList) {
    mContext = context;
    mOrderInfoList = orderInfoList;
    mSdf = new SimpleDateFormat("HH:mm");
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_retail_order, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, final int position) {
    PxOrderInfo orderInfo = mOrderInfoList.get(position);
    holder.mTvTime.setText("开单时间:" + mSdf.format(orderInfo.getStartTime()));
    String orderNo = orderInfo.getOrderNo();
    holder.mTvNo.setText("No." + orderNo.substring(orderNo.length() - 6, orderNo.length()));
    if (mOnRetailOrderClickListener != null) {
      holder.mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mOnRetailOrderClickListener.onRetailOrderClick(position);
        }
      });
    }
    //背景
    holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_retail_container_status_normal));
    holder.mViewContent.setBackgroundResource(R.color.item_retail_content_status_normal);
    if (mCurrentSelected == position) {
      holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_sel_table_container_status_normal));
      holder.mViewContent.setBackgroundResource(R.color.item_sel_table_content_status_normal);
    }
  }

  @Override public int getItemCount() {
    return mOrderInfoList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_time) TextView mTvTime;
    @Bind(R.id.tv_no) TextView mTvNo;
    @Bind(R.id.view_container) CardView mViewContainer;
    @Bind(R.id.view_content) RelativeLayout mViewContent;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public void setData(List<PxOrderInfo> data) {
    if (data != null) {
      mCurrentSelected = -1;
      mOrderInfoList = data;
      this.notifyDataSetChanged();
    }
  }

  /**
   * Click
   */
  public static interface OnRetailOrderClickListener {
    void onRetailOrderClick(int pos);
  }

  public OnRetailOrderClickListener mOnRetailOrderClickListener;

  public void setOnRetailOrderClickListener(OnRetailOrderClickListener onRetailOrderClickListener) {
    mOnRetailOrderClickListener = onRetailOrderClickListener;
  }

  /**
   * 设置选中
   */
  public void setSelected(int pos) {
    mCurrentSelected = pos;
    notifyDataSetChanged();
  }
}
