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
import com.psi.easymanager.module.PxProductCategory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjq on 2016/3/24.
 * 已结账单 销售统计
 */
public class OverBillSaleListAdapter extends RecyclerView.Adapter<OverBillSaleListAdapter.ViewHolder> {

  private Context mContext;
  private List<PxProductCategory> mCategoryList;
  private int mCurrentSelected = -1;

  public OverBillSaleListAdapter(Context context, List<PxProductCategory> categoryList) {
    mContext = context;
    if (categoryList == null) {
      mCategoryList = new ArrayList<PxProductCategory>();
    } else {
      mCategoryList = categoryList;
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_over_bill_sale_list, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, final int position) {
    PxProductCategory category = mCategoryList.get(position);
    holder.mTvName.setText(category.getName());
    holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_sale_container_unsel));
    holder.mViewContent.setBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_sale_content_unsel));
    if (position == mCurrentSelected) {
      holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_container_sel));
      holder.mViewContent.setBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_content_sel));
    }
    if (mOnSaleClickListener != null) {
      holder.mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mOnSaleClickListener.onSaleClick(position);
        }
      });
    }
  }

  @Override public int getItemCount() {
    return mCategoryList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_name) TextView mTvName;
    @Bind(R.id.view_container) CardView mViewContainer;
    @Bind(R.id.view_content) RelativeLayout mViewContent;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 设置数据
   */
  public void setData(List<PxProductCategory> data) {
    if (data == null) return;
    mCategoryList = data;
    this.notifyDataSetChanged();
  }

  /**
   * 设置选中
   */
  public void setSelected(int pos) {
    mCurrentSelected = pos;
    notifyDataSetChanged();
  }

  /**
   * Click
   */
  public static interface OnSaleClickListener {
    void onSaleClick(int pos);
  }

  public OnSaleClickListener mOnSaleClickListener;

  public void setOnSaleClickListener(OnSaleClickListener onSaleClickListener) {
    mOnSaleClickListener = onSaleClickListener;
  }
}
