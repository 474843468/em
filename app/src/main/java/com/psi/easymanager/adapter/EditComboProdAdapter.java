package com.psi.easymanager.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.module.AppComboGroupProdInfo;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxProductInfo;
import java.util.List;

/**
 * Created by dorado on 2016/8/19.
 */
public class EditComboProdAdapter extends RecyclerView.Adapter<EditComboProdAdapter.ViewHolder> {

  private Context mContext;
  private List<PxOrderDetails> mDetailsList;

  public EditComboProdAdapter(Context context, List<PxOrderDetails> detailsList) {
    mContext = context;
    mDetailsList = detailsList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_edit_combo_prod, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, final int position) {
    PxOrderDetails details = mDetailsList.get(position);
    PxProductInfo productInfo = details.getDbProduct();
    PxFormatInfo formatInfo = details.getDbFormatInfo();
    holder.mTvName.setText(productInfo.getName());

    //取消中划线
    holder.mTvName.getPaint().setFlags(0);
    holder.mTvName.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
    //设置中划线
    if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
      TextPaint paint = holder.mTvName.getPaint();
      paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
      holder.mTvName.setTextColor(mContext.getResources().getColor(R.color.red));
    }
    //规格
    if (formatInfo != null) {
      holder.mTvFormat.setText("(" + formatInfo.getName() + ")");
      holder.mTvFormat.setVisibility(View.VISIBLE);
    } else {
      holder.mTvFormat.setText("");
      holder.mTvFormat.setVisibility(View.GONE);
    }
    holder.mTvNum.setText(details.getNum() + "");
    if (details.getChooseType().equals(AppComboGroupProdInfo.TYPE_REQUIRED)) {
      holder.mTvType.setText("(必选)");
      holder.mTvType.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
    } else {
      holder.mTvType.setText("(自选)");
      holder.mTvType.setTextColor(mContext.getResources().getColor(R.color.primary_text));
    }
    holder.mRlClick.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mOnComboClickListener.onComboClick(position);
      }
    });
  }

  @Override public int getItemCount() {
    return mDetailsList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_name) TextView mTvName;
    @Bind(R.id.tv_format) TextView mTvFormat;
    @Bind(R.id.tv_num) TextView mTvNum;
    @Bind(R.id.tv_type) TextView mTvType;
    @Bind(R.id.rl_click) RelativeLayout mRlClick;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public void setData(List<PxOrderDetails> data) {
    if (data == null) return;
    this.mDetailsList = data;
    this.notifyDataSetChanged();
  }

  /**
   * Click
   */
  public static interface OnComboClickListener {
    void onComboClick(int pos);
  }

  private OnComboClickListener mOnComboClickListener;

  public void setOnComboClickListener(OnComboClickListener comboClickListener) {
    mOnComboClickListener = comboClickListener;
  }
}
