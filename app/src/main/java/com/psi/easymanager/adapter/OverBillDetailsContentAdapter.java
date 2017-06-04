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
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorado on 2016/6/7.
 */
public class OverBillDetailsContentAdapter
    extends RecyclerView.Adapter<OverBillDetailsContentAdapter.ViewHolder> {

  private Context mContext;
  private List<PxOrderDetails> mDetailsList;

  public OverBillDetailsContentAdapter(Context context, List<PxOrderDetails> detailsList) {
    mContext = context;
    if (detailsList != null) {
      mDetailsList = detailsList;
    } else {
      mDetailsList = new ArrayList<PxOrderDetails>();
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext)
        .inflate(R.layout.item_over_bill_details_content, parent, false);
    return new ViewHolder(view);
  }

  //@formatter:off
  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    PxOrderDetails details = mDetailsList.get(position);
    String name = details.getDbProduct().getName();
    holder.mTvName.setText(name);
    if (details.getIsGift().equals(PxOrderDetails.GIFT_TRUE)){
      holder.mTvName.setText(name+"(赠)");
    }
    //金额
    if (details.getDbOrder().getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)){
      holder.mTvMoney.setText(NumberFormatUtils.formatFloatNumber(details.getVipPrice() * details.getDiscountRate() / 100) + "");
    } else {
      holder.mTvMoney.setText(NumberFormatUtils.formatFloatNumber(details.getPrice() * details.getDiscountRate() / 100) + "");
    }
    //做法
    if (details.getDbMethodInfo() == null) {
      holder.mTvMethod.setVisibility(View.GONE);
    } else {
      holder.mTvMethod.setVisibility(View.VISIBLE);
      holder.mTvMethod.setText("(" + details.getDbMethodInfo().getName() + ")");
    }
    //规格
    if (details.getDbFormatInfo() == null){
      holder.mTvFormat.setVisibility(View.GONE);
    }else {
      holder.mTvFormat.setVisibility(View.VISIBLE);
      holder.mTvFormat.setText("(" + details.getDbFormatInfo().getName() + ")");
    }
    //双单位数量
    holder.mTvMultNum.setVisibility(View.GONE);
    //双单位
    if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      holder.mTvMultNum.setVisibility(View.VISIBLE);
      holder.mTvNum.setText(details.getNum() + details.getDbProduct().getOrderUnit());
      holder.mTvMultNum.setText(NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber()) + details.getDbProduct().getUnit());
    }
    //非双单位
    else {
      holder.mTvNum.setText(details.getNum() + details.getDbProduct().getUnit());
    }
    //折扣率
    holder.mTvDisc.setText("");
    if (details.getDiscountRate() != 100){
      holder.mTvDisc.setText("(" + details.getDiscountRate() + "%)");
    }
    //备注
    holder.mTvRemarks.setVisibility(View.GONE);
    holder.mTvRemarks.setText("");
    if (details.getRemarks() != null && details.getRemarks().equals("") == false){
      holder.mTvRemarks.setText("(" + details.getRemarks() + ")");
      holder.mTvRemarks.setVisibility(View.VISIBLE);
    }
  }

  @Override public int getItemCount() {
    return mDetailsList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_name) TextView mTvName;
    @Bind(R.id.tv_money) TextView mTvMoney;
    @Bind(R.id.tv_num) TextView mTvNum;
    @Bind(R.id.tv_mult_num) TextView mTvMultNum;
    @Bind(R.id.tv_format) TextView mTvFormat;
    @Bind(R.id.tv_method) TextView mTvMethod;
    @Bind(R.id.tv_disc) TextView mTvDisc;
    @Bind(R.id.tv_remarks) TextView mTvRemarks;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 设置数据
   */
  public void setData(List<PxOrderDetails> data) {
    if (data != null) {
      this.mDetailsList = data;
      this.notifyDataSetChanged();
    }
  }
}
