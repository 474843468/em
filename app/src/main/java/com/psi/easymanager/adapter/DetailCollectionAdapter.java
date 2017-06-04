package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.util.List;

/**
 * Created by dorado on 2016/5/30.
 */
public class DetailCollectionAdapter
    extends RecyclerView.Adapter<DetailCollectionAdapter.ViewHolder> {

  private Context mContext;//上下文
  private List<PxOrderDetails> mCollectionList;//Data

  public DetailCollectionAdapter(Context context, List<PxOrderDetails> collectionList) {
    mContext = context;
    mCollectionList = collectionList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(mContext).inflate(R.layout.item_details_collection, parent, false);
    return new ViewHolder(view);
  }

  //@formatter:off
  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    //当前数据
    PxOrderDetails details = mCollectionList.get(position);
    PxProductInfo dbProduct = details.getDbProduct();
    //名称
    holder.mTvName.setText(dbProduct.getName());
    //状态
    if (PxOrderDetails.ORDER_STATUS_ORDER.equals(details.getOrderStatus())) {
        holder.mTvOrderStatus.setText("已下单");
        holder.mTvOrderStatus.setTextColor(mContext.getResources().getColor(R.color.item_details_ordered));
      if (details.getIsServing() != null && details.getIsServing() == true) {
        holder.mTvOrderStatus.setText("已上菜");
        holder.mTvOrderStatus.setTextColor(mContext.getResources().getColor(R.color.item_details_serving));
      }
    } else if (PxOrderDetails.ORDER_STATUS_UNORDER.equals(details.getOrderStatus())) {
        holder.mTvOrderStatus.setText("未下单");
        holder.mTvOrderStatus.setTextColor(mContext.getResources().getColor(R.color.item_details_unorder));
    } else if (PxOrderDetails.ORDER_STATUS_REFUND.equals(details.getOrderStatus())) {
        holder.mTvOrderStatus.setText("废弃单");
        holder.mTvOrderStatus.setTextColor(mContext.getResources().getColor(R.color.item_details_refund));
    }
    //数量
    holder.mTvRefundNum.setVisibility(View.GONE);
    if (PxProductInfo.IS_TWO_UNIT_TURE.equals(dbProduct.getMultipleUnit())) {
      holder.mTvNum.setText("余" + details.getNum().intValue() + dbProduct.getOrderUnit() + "/" + NumberFormatUtils.formatFloatNumber(details.getMultipleUnitNumber()) + dbProduct.getUnit());
      if ((details.getRefundMultNum() != null &&details.getRefundMultNum() != 0.0) || (details.getRefundNum() != null && details.getRefundNum() != 0.0)){
        holder.mTvRefundNum.setText("(已退" + details.getRefundNum().intValue() + dbProduct.getOrderUnit() + "/" + NumberFormatUtils.formatFloatNumber(details.getRefundMultNum()) + dbProduct.getUnit() + ")");
        holder.mTvRefundNum.setVisibility(View.VISIBLE);
      }
    } else {
      holder.mTvNum.setText("余"+details.getNum().intValue() + dbProduct.getUnit());
      if (details.getRefundNum() != null && details.getRefundNum() != 0.0){
        holder.mTvRefundNum.setText("(已退" + details.getRefundNum().intValue() + dbProduct.getUnit() +")");
        holder.mTvRefundNum.setVisibility(View.VISIBLE);
      }
    }
    //价格
    holder.mTvPrice.setText("￥" + NumberFormatUtils.formatFloatNumber(details.getPrice()));
    //折扣
    holder.mLlDiscount.setVisibility(View.GONE);
    if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(details.getDbOrder().getUseVipCard()) || 100 != details.getDiscountRate() ) {
      holder.mLlDiscount.setVisibility(View.VISIBLE);
      holder.mTvDiscount.setText("-￥" + details.getDiscPrice());
      if (100 != details.getDiscountRate()){
      holder.mTvDis.setText( "折扣: "+details.getDiscountRate()+"%" );
      }else {
      holder.mTvDis.setText( "折扣: ");
      }
    }
    //做法
    holder.mLlMethod.setVisibility(View.GONE);
    if (null != details.getDbMethodInfo()){
      holder.mLlMethod.setVisibility(View.VISIBLE);
      holder.mTvMethod.setText(details.getDbMethodInfo().getName());
    }
    //规格
    holder.mLlFormat.setVisibility(View.GONE);
    if (null != details.getDbFormatInfo()){
      holder.mLlFormat.setVisibility(View.VISIBLE);
      holder.mTvFormat.setText(details.getDbFormatInfo().getName());
    }
    //备注
    holder.mLlRemarks.setVisibility(View.GONE);
    if (details.getRemarks() != null && details.getRemarks().equals("无") == false){
      holder.mLlRemarks.setVisibility(View.VISIBLE);
      holder.mTvRemarks.setText(details.getRemarks());
    }
    //赠品
    holder.mTvGift.setVisibility(View.GONE);
    if (details.getIsGift() != null && PxOrderDetails.GIFT_TRUE.equals(details.getIsGift())) {
      holder.mTvGift.setVisibility(View.VISIBLE);
    }

    //延迟
    holder.mTvDelay.setVisibility(View.GONE);
    if (PxOrderDetails.STATUS_DELAY.equals(details.getStatus())){
      holder.mTvDelay.setVisibility(View.VISIBLE);
    }

    //点击
    if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
      holder. mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          //
          mOnCollectionClickListener.onOrderedClick(holder.getLayoutPosition());
        }
      });
    }

    if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_UNORDER)) {
      holder. mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mOnCollectionClickListener.onUnOrderClick(holder.getLayoutPosition());
        }
      });
    }

     if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
      holder. mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
         ToastUtils.showShort(mContext,"已退商品不能操作");
        }
      });
    }
  }

  @Override public int getItemCount() {
    return mCollectionList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_name) TextView mTvName;
    @Bind(R.id.tv_num) TextView mTvNum;
    @Bind(R.id.tv_refund_num) TextView mTvRefundNum;
    @Bind(R.id.tv_price) TextView mTvPrice;
    @Bind(R.id.view_container) LinearLayout mViewContainer;
    //折扣
    @Bind(R.id.ll_discount) LinearLayout mLlDiscount;
    @Bind(R.id.tv_discount) TextView mTvDiscount;
    @Bind(R.id.tv_dis) TextView mTvDis;
    //做法
    @Bind(R.id.ll_method) LinearLayout mLlMethod;
    @Bind(R.id.tv_method) TextView mTvMethod;
    //规格
    @Bind(R.id.ll_format) LinearLayout mLlFormat;
    @Bind(R.id.tv_format) TextView mTvFormat;
    //备注
    @Bind(R.id.ll_remarks) LinearLayout mLlRemarks;
    @Bind(R.id.tv_remarks) TextView mTvRemarks;

    //赠品
    @Bind(R.id.tv_gift) TextView mTvGift;
    //暂不上菜
    @Bind(R.id.tv_delay) TextView mTvDelay;
    //状态
    @Bind(R.id.tv_order_status) TextView mTvOrderStatus;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 设置
   */
  public void setData(List<PxOrderDetails> data) {
      this.mCollectionList = data;
      this.notifyDataSetChanged();
  }

  /**
   * Click
   */
  public interface OnCollectionClickListener {
    void onOrderedClick(int pos);

    void onUnOrderClick(int pos);
  }

  public OnCollectionClickListener mOnCollectionClickListener;

  public void setOnCollectionClickListener(OnCollectionClickListener onCollectionClickListener) {
    mOnCollectionClickListener = onCollectionClickListener;
  }
}
