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
import com.psi.easymanager.module.EPaymentInfo;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 消息列表
 * Created by dorado on 2016/10/21.
 */
public class EPaymentAdapter extends RecyclerView.Adapter<EPaymentAdapter.ViewHolder>
    implements View.OnClickListener {

  private Context mContext;
  private List<EPaymentInfo> mEPaymentInfoList;
  private SimpleDateFormat mSdf = new SimpleDateFormat("MM-dd HH:mm");
  private ItemClickListener listener;

  public EPaymentAdapter(Context context, List<EPaymentInfo> EPaymentInfoList) {
    mContext = context;
    mEPaymentInfoList = EPaymentInfoList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_epayment, parent, false);
    view.setOnClickListener(this);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    EPaymentInfo ePaymentInfo = mEPaymentInfoList.get(position);
    holder.mTvTime.setText(mSdf.format(ePaymentInfo.getPayTime()));
    holder.mTvOrderNo.setText(ePaymentInfo.getOrderNo());
    holder.mTvTableName.setText(ePaymentInfo.getTableName());
    switch (ePaymentInfo.getType()) {
      case EPaymentInfo.TYPE_ALI_PAY:
        holder.mTvType.setText("支付宝");
        break;
      case EPaymentInfo.TYPE_WX_PAY:
        holder.mTvType.setText("微信");
        break;
      case EPaymentInfo.TYPE_BEST_PAY:
        holder.mTvType.setText("翼支付");
        break;
      case EPaymentInfo.TYPE_VIP_PAY:
        //PxPayInfo payInfo = ePaymentInfo.getDbPayInfo();
        //if (payInfo != null) {
        //  //支付名称
        //  if (payInfo.getIdCardNum() != null && payInfo.getIdCardNum() != "") {
        //    holder.mTvType.setText("实体卡");
        //  } else {
        holder.mTvType.setText("会员");
        //    holder.mTvType.setText("手机");
        //  }
        //} else {
        //  Logger.v("payInfo==null");
        //  holder.mTvType.setText("会员");
        //}
        break;
    }
    holder.mTvMoney.setText(ePaymentInfo.getPrice() + "");

    switch (ePaymentInfo.getStatus()) {
      case EPaymentInfo.STATUS_REFUND:
        holder.mTvStatus.setText("已退款");
        holder.mTvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
        break;
      case EPaymentInfo.STATUS_PAYED:
      case EPaymentInfo.STATUS_PAYED_AND_REFUND:
        holder.mTvStatus.setText("已支付");
        holder.mTvStatus.setTextColor(mContext.getResources().getColor(R.color.material_blue));
        break;
    }
    holder.mLlContainer.setTag(position);
  }

  @Override public int getItemCount() {
    return mEPaymentInfoList.size();
  }

  @Override public void onClick(View v) {
    listener.onItemClick((int) v.getTag());
  }

  public interface ItemClickListener {
    void onItemClick(int pos);
  }

  public void setOnItemClickListener(ItemClickListener listener) {
    this.listener = listener;
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_time) TextView mTvTime;
    @Bind(R.id.tv_order_no) TextView mTvOrderNo;
    @Bind(R.id.tv_table_name) TextView mTvTableName;
    @Bind(R.id.tv_type) TextView mTvType;
    @Bind(R.id.tv_money) TextView mTvMoney;
    @Bind(R.id.tv_status) TextView mTvStatus;
    @Bind(R.id.ll_container) LinearLayout mLlContainer;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public void setData(List<EPaymentInfo> data) {
    if (data != null) {
      this.mEPaymentInfoList = data;
      this.notifyDataSetChanged();
    }
  }
}
