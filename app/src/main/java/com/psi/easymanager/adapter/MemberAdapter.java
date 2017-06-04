package com.psi.easymanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by psi on 2016/5/17.
 * 会员列表适配器
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
  private Context mContext;
  private List<PxVipInfo> mVipInfoList;
  private int mPrePos = -1;//选择前pos
  public int mCurrentPos = -1;//当前选择Pos

  public void setSelected(int position) {
    if (mCurrentPos == position) return;
    mCurrentPos = position;
    if (mPrePos != -1) {
      notifyItemChanged(mPrePos);
    }
    notifyItemChanged(mCurrentPos);
    mPrePos = mCurrentPos;
  }

  public MemberAdapter(Context context, List<PxVipInfo> vipInfoList) {
    mContext = context;
    if (vipInfoList == null) {
      this.mVipInfoList = new ArrayList<>();
    } else {
      this.mVipInfoList = vipInfoList;
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_vip, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    PxVipInfo vipInfo = mVipInfoList.get(position);
    switch (Integer.valueOf(vipInfo.getLevel())) {
      case PxVipInfo.VIP_ONE:
        holder.ivItemIcon.setImageResource(R.mipmap.ic_member_level_two);
        break;
    }
    holder.tvItemMemberName.setText(vipInfo.getName());
    if (vipInfo.getAccountBalance() >= 5000) {
      holder.tvItemMemberMoney.setTextColor(mContext.getResources().getColor(R.color.green));
    } else if (vipInfo.getAccountBalance() < 5000 && vipInfo.getAccountBalance() >= 1000) {
      holder.tvItemMemberMoney.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
    } else if (vipInfo.getAccountBalance() < 1000 && vipInfo.getAccountBalance() > 100) {
      holder.tvItemMemberMoney.setTextColor(
          mContext.getResources().getColor(R.color.material_blue));
    } else {
      holder.tvItemMemberMoney.setTextColor(mContext.getResources().getColor(R.color.red));
    }
    holder.tvItemMemberMoney.setText(
        "余额:" + NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance()) + "(元)");
    holder.tvItemMemberMobile.setText(vipInfo.getMobile());

    if (mCurrentPos == position) {
      holder.cvItemMember.setCardBackgroundColor(Color.parseColor("#bbffbb"));
    } else {
      holder.cvItemMember.setCardBackgroundColor(Color.TRANSPARENT);
    }

    holder.cvItemMember.setTag(holder.getAdapterPosition());

    if (listener != null) {
      holder.cvItemMember.setOnClickListener(new View.OnClickListener() {

        @Override public void onClick(View v) {

          int pos = (Integer) v.getTag();
          listener.onMemberItemClick(pos);
          mPrePos = mCurrentPos;
        }
      });
    }
  }

  @Override public int getItemCount() {
    return mVipInfoList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.iv_item_icon) ImageView ivItemIcon;
    @Bind(R.id.tv_item_member_name) TextView tvItemMemberName;
    @Bind(R.id.tv_item_member_money) TextView tvItemMemberMoney;
    @Bind(R.id.tv_item_member_mobile) TextView tvItemMemberMobile;
    @Bind(R.id.cv_item_member) CardView cvItemMember;
    //    Boolean isChoice;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 添加数据
   */
  public void setData(List<PxVipInfo> vipInfoList) {
    if (vipInfoList == null) return;
    mPrePos = -1;
    mCurrentPos = -1;
    mVipInfoList.clear();
    mVipInfoList.addAll(vipInfoList);
    this.notifyDataSetChanged();
  }

  /**
   * 清除数据
   */
  public void clearData() {
    mVipInfoList.clear();
    this.notifyDataSetChanged();
  }

  /**
   * 点击事件
   */
  public interface onMemberItemClickListener {
    void onMemberItemClick(int pos);
  }

  private onMemberItemClickListener listener;

  public void setListener(onMemberItemClickListener listener) {
    this.listener = listener;
  }

  /**
   * 修改 会员余额
   */
  public void ChangeVipInfoMoney(Double money, int pos) {
    mVipInfoList.get(pos).setAccountBalance(mVipInfoList.get(pos).getAccountBalance() - money);
    notifyItemChanged(pos);
  }

  /**
   * 恢复未选中状态
   */
  public void RecoverNotChoiceStatus() {

    int size = mVipInfoList.size();
    if (mPrePos >= 0 && mPrePos < (size - 1)) {
      notifyItemChanged(mPrePos);
    }
    if (mCurrentPos >= 0 && mCurrentPos < (size - 1)) {
      notifyItemChanged(mCurrentPos);
    }
    mPrePos = -1;
    mCurrentPos = -1;
  }

  /**
   * 修改选中会员的信息（名字，电话，级别,金额--金额只有 “充值”时或者“冲销”时修改）
   *
   * type : 修改项
   * 0: null
   * 1 :name
   * 2 :mobile
   * 3 :level
   * 4 :money
   * pos: 修改位置
   * str: 更新信息
   */
  public void ChangeVipInforation(int type, int pos, String str) {
    PxVipInfo vipInfo = mVipInfoList.get(pos);
    switch (type) {
      case 1:
        vipInfo.setName(str);
        break;
      case 2:
        vipInfo.setMobile(str);
        break;
      case 3:
        vipInfo.setLevel(str);
        break;
      case 4:
        vipInfo.setAccountBalance(Double.parseDouble(str));
        break;
    }
    notifyItemChanged(pos);
  }
}
