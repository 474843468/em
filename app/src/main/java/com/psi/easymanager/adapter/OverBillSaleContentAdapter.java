package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.module.AppSaleContent;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.List;

/**
 * Created by dorado on 2016/6/6.
 */
public class OverBillSaleContentAdapter
    extends RecyclerView.Adapter<OverBillSaleContentAdapter.ViewHolder> {

  private Context mContext;
  private List<AppSaleContent> mSaleContentList;

  public OverBillSaleContentAdapter(Context context, List<AppSaleContent> saleContentList) {
    mContext = context;
    mSaleContentList = saleContentList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(mContext).inflate(R.layout.item_over_bill_sale_content, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    AppSaleContent appSaleContent = mSaleContentList.get(position);
    holder.mTvName.setText(appSaleContent.getProdName());
    if (appSaleContent.isMultUnitProd()) {
      holder.mTvSaleNum.setText(NumberFormatUtils.formatFloatNumber(appSaleContent.getSaleMultNumber()) + appSaleContent.getUnit());
    } else {
      holder.mTvSaleNum.setText(appSaleContent.getSaleNumber() + appSaleContent.getUnit());
    }
  }

  @Override public int getItemCount() {
    return mSaleContentList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_name) TextView mTvName;
    @Bind(R.id.tv_sale_num) TextView mTvSaleNum;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 设置数据
   */
  public void setData(List<AppSaleContent> data) {
    if (data != null) {
      this.mSaleContentList = data;
      this.notifyDataSetChanged();
    }
  }
}
