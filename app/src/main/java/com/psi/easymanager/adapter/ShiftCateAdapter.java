package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.psi.easymanager.R;
import com.psi.easymanager.module.AppShiftCateInfo;

import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psi on 2016/7/30.
 * 交接班-分类汇总适配器
 */
public class ShiftCateAdapter extends RecyclerView.Adapter<ShiftCateAdapter.ViewHolder> {

  private Context mContext;
  private List<AppShiftCateInfo> mShiftCateInfoList;

  public ShiftCateAdapter(Context context, List<AppShiftCateInfo> shiftCateInfoList) {
    mContext = context;
    if (shiftCateInfoList == null) {
      this.mShiftCateInfoList = new ArrayList<>();
    } else {
      this.mShiftCateInfoList = shiftCateInfoList;
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_shift_cate, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    AppShiftCateInfo appShiftCateInfo = mShiftCateInfoList.get(position);
    holder.mTvCateName.setText(appShiftCateInfo.getCateName());
    holder.mTvCateNum.setText(appShiftCateInfo.getCateNumber() + "");
    holder.mTvActualMoney.setText(NumberFormatUtils.formatFloatNumber(appShiftCateInfo.getActualAmount() )+ "");
    holder.mTvOriginMoney.setText(NumberFormatUtils.formatFloatNumber(appShiftCateInfo.getReceivableAmount()) +"");
  }

  @Override public int getItemCount() {
    return mShiftCateInfoList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_cate_name) TextView mTvCateName;
    @Bind(R.id.tv_cate_num) TextView mTvCateNum;
    @Bind(R.id.tv_actual_money) TextView mTvActualMoney;
    @Bind(R.id.tv_origin_money) TextView mTvOriginMoney;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
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
