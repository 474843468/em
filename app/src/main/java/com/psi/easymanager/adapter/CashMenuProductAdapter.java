package com.psi.easymanager.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.module.ProdInnerOrder;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.List;

/**
 * Created by zjq on 2016/3/15.
 * 菜单适配器
 */
//@formatter:off
public class CashMenuProductAdapter extends RecyclerView.Adapter<CashMenuProductAdapter.ViewHolder> {
  private Context mContext;
  //private List<PxProductInfo> mProductInfoList;
  //private PxOrderInfo mOrderInfo;
  private List<ProdInnerOrder> mProductInnerOrderList;

  public CashMenuProductAdapter(Context context, List<ProdInnerOrder> productInnerOrderList) {
    mContext = context;
    this.mProductInnerOrderList = productInnerOrderList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_product, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    //final PxProductInfo productInfo = mProductInfoList.get(position);
    ProdInnerOrder productInnerOrder = mProductInnerOrderList.get(position);
    final PxProductInfo productInfo = productInnerOrder.getProductInfo();
    if(!TextUtils.isEmpty(productInfo.getShortName())){
      holder.mTvName.setText(productInfo.getShortName());
    } else {
      if (productInfo.getName().length() > 2) {
        holder.mTvName.setText(productInfo.getName().substring(0, 2));
      } else {
        holder.mTvName.setText(productInfo.getName());
      }
    }
    holder.mTvFullName.setText(productInfo.getName());

    //@formatter:on
    //数量角标
    int prodNum = productInnerOrder.getNum();
    if (prodNum > 0) {
      holder.mTvProdNum.setText(String.valueOf(prodNum));
      holder.mTvProdNum.setVisibility(View.VISIBLE);
    } else {
      holder.mTvProdNum.setVisibility(View.GONE);
    }

    //@formatter:off
    holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_prod_container_status_normal));
    holder.mViewContent.setBackgroundResource(R.color.item_prod_content_status_normal);
    //套餐
    if (productInfo.getType() != null && productInfo.getType().equals(PxProductInfo.TYPE_COMBO)) {
      holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_prod_container_status_combo));
      holder.mViewContent.setBackgroundResource(R.color.item_prod_content_status_combo);
    }
    //双单位
    if ((productInfo.getType() == null || productInfo.getType().equals(PxProductInfo.TYPE_ORIGINAL)) && productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_prod_container_status_multiple_unit));
      holder.mViewContent.setBackgroundResource(R.color.item_prod_content_status_multiple_unit);
    }
    //是否沽清
    if (productInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)) {
      holder.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_prod_container_status_stop_sale));
      holder.mViewContent.setBackgroundResource(R.color.item_prod_content_status_stop_sale);
      holder.mTvName.setText("停售");
    }

    //剩余数量
    if (productInfo.getOverPlus() == null || productInfo.getOverPlus() == 0) {
      holder.mTvSurplusNum.setVisibility(View.GONE);
    } else {
      holder.mTvSurplusNum.setVisibility(View.VISIBLE);
      holder.mTvSurplusNum.setText("余量" + NumberFormatUtils.formatFloatNumber(productInfo.getOverPlus()));
    }
    //促销价格
    PxPromotioDetails promotioDetails = productInnerOrder.getPromotioDetails();

    //中划线
     TextPaint paint = holder.mTvPrice.getPaint();
    if (promotioDetails != null){
      holder.mTvPromotioPrice.setVisibility(View.VISIBLE);
      holder.mTvPromotioPrice.setText(promotioDetails.getPromotionalPrice()+"元");
      paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }else{
      holder.mTvPromotioPrice.setVisibility(View.GONE);
      paint.setFlags(0);
    }
    //原价
    String price = productInfo.getPrice() + "元";
    holder.mTvPrice.setText(price);
    //点击
    if (mOnProductClickListener != null) {
      holder.mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mOnProductClickListener.onProductClick(holder.getLayoutPosition());
        }
      });

      //长按
      holder.mViewContainer.setOnLongClickListener(new View.OnLongClickListener() {
        @Override public boolean onLongClick(View v) {
          mOnProductClickListener.onProductLongClick(mProductInnerOrderList.get(holder.getLayoutPosition()).getProductInfo());
          return true;
        }
      });
    }
  }

  @Override public int getItemCount() {
    return mProductInnerOrderList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_name) TextView mTvName;
    @Bind(R.id.tv_full_name) TextView mTvFullName;
    @Bind(R.id.tv_price) TextView mTvPrice;
    @Bind(R.id.view_container) CardView mViewContainer;
    @Bind(R.id.view_content) View mViewContent;
    @Bind(R.id.tv_surplus_num) TextView mTvSurplusNum;
    @Bind(R.id.tv_prod_num) TextView mTvProdNum;
    @Bind(R.id.tv_promotio_price) TextView mTvPromotioPrice;
    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * Click
   */
  public interface OnProductClickListener {
    void onProductClick(int pos);

    void onProductLongClick(PxProductInfo productInfo);
  }

  public OnProductClickListener mOnProductClickListener;

  public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
    mOnProductClickListener = onProductClickListener;
  }
  //@formatter:on

  /**
   * 设置数据
   */
  public void setData(DiffUtil.DiffResult result, List<ProdInnerOrder> newProdInnerOrderList) {
    this.mProductInnerOrderList = newProdInnerOrderList;
    result.dispatchUpdatesTo(this);
  }

  /**
   * 清空数据
   */
  public void clearData() {
    mProductInnerOrderList.clear();
    this.notifyDataSetChanged();
  }

  public List<ProdInnerOrder> getProductInnerOrderList() {
    return mProductInnerOrderList;
  }
}
