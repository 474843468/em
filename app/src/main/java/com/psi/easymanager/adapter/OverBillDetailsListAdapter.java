package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.TableOrderRel;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dorado on 2016/6/7.
 */
public class OverBillDetailsListAdapter extends RecyclerView.Adapter<OverBillDetailsListAdapter.ViewHolder> {

  private Context mContext;
  private List<PxOrderInfo> mOrderInfoList;
  private SimpleDateFormat mSdf;
  //当前选中
  private int mCurrentSelected = -1;

  public OverBillDetailsListAdapter(Context context, List<PxOrderInfo> orderInfoList) {
    mContext = context;
    mOrderInfoList = orderInfoList;
    mSdf = new SimpleDateFormat("HH:mm");
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_over_bill_details_list, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, final int position) {
    PxOrderInfo pxOrderInfo = mOrderInfoList.get(position);
    holder.mTvEndTime.setText(mSdf.format(pxOrderInfo.getEndTime()));
    if (pxOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(pxOrderInfo.getId()))
          .unique();
      holder.mTvTable.setText(unique.getDbTable().getName());
      holder.mTvMoney.setText("金额:" + pxOrderInfo.getAccountReceivable() + "");
    } else {
      holder.mTvTable.setText("零售单");
      holder.mTvMoney.setText("金额:" + pxOrderInfo.getAccountReceivable() + "");
    }
    holder.mTvCashier.setText("收银员:" + pxOrderInfo.getDbUser().getName());
    String orderNo = pxOrderInfo.getOrderNo();
    holder.mTvOrderNo.setText("No." + orderNo.substring(orderNo.length() - 4, orderNo.length()));

    holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_detail_container_unsel));
    holder.mViewContent.setBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_detail_content_unsel));
    if (mCurrentSelected == position) {
      holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_container_sel));
      holder.mViewContent.setBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_content_sel));
    }

    //Click
    if (mOnDetailClickListener != null) {
      holder.mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mOnDetailClickListener.onDetailClick(position);
        }
      });
    }
  }

  @Override public int getItemCount() {
    return mOrderInfoList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_end_time) TextView mTvEndTime;
    @Bind(R.id.tv_table) TextView mTvTable;
    @Bind(R.id.tv_cashier) TextView mTvCashier;
    @Bind(R.id.tv_money) TextView mTvMoney;
    @Bind(R.id.view_container) CardView mViewContainer;
    @Bind(R.id.view_content) RelativeLayout mViewContent;
    @Bind(R.id.tv_order_no) TextView mTvOrderNo;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 设置选中
   */
  public void setSelected(int original,int now) {
    mCurrentSelected = now;
    this.notifyItemChanged(original);
    this.notifyItemChanged(now);
  }

  /**
   * 获取选中位置
   */
  public int getCurrentSelected(){
    return mCurrentSelected;
  }

  /**
   * Click
   */
  public static interface OnDetailClickListener {
    void onDetailClick(int pos);
  }

  public OnDetailClickListener mOnDetailClickListener;

  public void setOnDetailClickListener(OnDetailClickListener onDetailClickListener) {
    mOnDetailClickListener = onDetailClickListener;
  }

  /**
   * 设置数据
   */
  public void setData(List<PxOrderInfo> data) {
    if (data != null) {
      mOrderInfoList = data;
      this.notifyDataSetChanged();
    }
  }
}
