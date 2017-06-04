package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.TableOrderRel;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: ylw
 * Date: 2016-09-13
 * Time: 16:43
 * 预订单 Adapter
 */
public class ReserveAdapter extends RecyclerView.Adapter<ReserveAdapter.VH> implements View.OnClickListener {
  private Context mContext;
  private List<PxOrderInfo> mReserveOrderList;
  private final SimpleDateFormat mDateSdf;
  private final SimpleDateFormat mTimeSdf;
  private OnItemClickListener mListener;
  private int prePos = -1;
  private int currentPos = -1;
  public ReserveAdapter(Context context, List<PxOrderInfo> reserveOrderList) {
    if (reserveOrderList == null) {
      reserveOrderList = new ArrayList<>();
    }
    mContext = context;
    mReserveOrderList = reserveOrderList;
    mDateSdf = new SimpleDateFormat("MM-dd");
    mTimeSdf = new SimpleDateFormat("HH:mm");
  }

  @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_reserve_order, parent, false);
    view.setOnClickListener(this);
    return new VH(view);
  }

  @Override public void onBindViewHolder(VH holder, int position) {
    PxOrderInfo reserveOrder = mReserveOrderList.get(position);
    TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(reserveOrder.getId()))
        .unique();

    holder.mTvDate.setText(mDateSdf.format(reserveOrder.getDiningTime()));
    holder.mTvTime.setText(mTimeSdf.format(reserveOrder.getDiningTime()));
    holder.mTvLinkMan.setText(reserveOrder.getLinkMan());
    if (PxOrderInfo.RESERVE_STATE_RESERVE.equals(reserveOrder.getReserveState())) {
      holder.mTvTable.setText((unique != null) ? unique.getDbTable().getName() : "无");
    } else{
      holder.mTvTable.setText((unique != null) ? unique.getDbTable().getName() : "零售单");
    }
    holder.mLlContainer.setTag(position);
    //选择状态 标记
    if (currentPos == position) {
      holder.mLlContainer.setBackgroundResource(R.drawable.bg_over_bill_details_sel);
    } else {
      holder.mLlContainer.setBackgroundResource(R.drawable.bg_over_bill_details_unsel);
    }
  }

  @Override public int getItemCount() {
    return mReserveOrderList.size();
  }

  @Override public void onClick(View v) {
    int pos = (int) v.getTag();
    mListener.onItemClick(pos);
  }

  /**
   * 选择item
   */
  public void setSelected(int position) {
    if (currentPos == position) return;
    currentPos = position;
    if (prePos != -1) {
      notifyItemChanged(prePos);
    }
    notifyItemChanged(currentPos);
    prePos = currentPos;
  }

  public void insteadItem(int pos, PxOrderInfo reserveOrder) {
    mReserveOrderList.remove(pos);
    mReserveOrderList.add(pos, reserveOrder);
    notifyItemChanged(pos);
  }

  /**
   * 返回当前选中POS
   */
  public int getCurrentPos() {
    return currentPos;
  }

  class VH extends RecyclerView.ViewHolder {
    private TextView mTvTime;
    private TextView mTvDate;
    private TextView mTvLinkMan;
    private TextView mTvTable;
    private LinearLayout mLlContainer;
    public VH(View itemView) {
      super(itemView);
      mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
      mTvDate = (TextView) itemView.findViewById(R.id.tv_date);
      mTvLinkMan = (TextView) itemView.findViewById(R.id.tv_link_man);
      mTvTable = (TextView) itemView.findViewById(R.id.tv_table);
      mLlContainer = (LinearLayout) itemView.findViewById(R.id.ll_container);
    }
  }

  public void setData(List<PxOrderInfo> reserveOrderList) {
    mReserveOrderList = reserveOrderList;
    notifyDataSetChanged();
    prePos = -1;
    currentPos = -1;
  }

  public interface OnItemClickListener {
    void onItemClick(int pos);
  }

  public void setOnItemClick(OnItemClickListener listener) {
    this.mListener = listener;
  }
}  