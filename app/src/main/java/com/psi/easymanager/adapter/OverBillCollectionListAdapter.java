package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.psi.easymanager.R;
import com.psi.easymanager.module.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dorado on 2016/6/7.
 */
public class OverBillCollectionListAdapter
    extends RecyclerView.Adapter<OverBillCollectionListAdapter.ViewHolder> {

  private Context mContext;
  private List<User> mUserList;
  //当前选中
  private int mCurrentSelected = -1;

  public OverBillCollectionListAdapter(Context context, List<User> userList) {
    mContext = context;
    if (userList != null) {
      mUserList = userList;
    } else {
      mUserList = new ArrayList<User>();
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_over_bill_collection_list, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, final int position) {
    User user = mUserList.get(position);
    holder.mTvName.setText(user.getName());
    holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_collect_container_unsel));
    holder.mViewContent.setBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_collect_content_unsel));
    if (position == mCurrentSelected) {
      holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_container_sel));
      holder.mViewContent.setBackgroundColor(mContext.getResources().getColor(R.color.item_over_bill_content_sel));
    }

    //Click
    if (mOnUserClickListener != null) {
      holder.mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mOnUserClickListener.onUserClick(position);
        }
      });
    }
  }

  @Override public int getItemCount() {
    return mUserList.size();
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
   * 设置选中
   */
  public void setSelected(int pos) {
    mCurrentSelected = pos;
    notifyDataSetChanged();
  }

  /**
   * Click
   */
  public static interface OnUserClickListener {
    void onUserClick(int pos);
  }

  public OnUserClickListener mOnUserClickListener;

  public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
    mOnUserClickListener = onUserClickListener;
  }

  /**
   * 设置数据
   */
  public void setData(List<User> data) {
    if (data != null) {
      mUserList = data;
      this.notifyDataSetChanged();
    }
  }
}
