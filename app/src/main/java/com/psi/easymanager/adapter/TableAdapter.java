package com.psi.easymanager.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.Date;
import java.util.List;

/**
 * Created by zjq on 2016/3/21.
 * 找单 单据适配器
 */
public class TableAdapter extends RecyclerView.Adapter {

  private List<PxTableInfo> mTableList;
  private Context mContext;
  private int mCurrentSelected = -1;//当前选中

  public TableAdapter(Context context, List<PxTableInfo> tableList) {
    mContext = context;
    mTableList = tableList;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = null;
    switch (viewType + "") {
      case PxTableInfo.STATUS_EMPTY://空闲
        view = LayoutInflater.from(mContext).inflate(R.layout.item_table_empty, parent, false);
        return new ViewHolderEmpty(view);
      case PxTableInfo.STATUS_OCCUPIED://占用
        view = LayoutInflater.from(mContext).inflate(R.layout.item_table_occupied, parent, false);
        return new ViewHolderOccupied(view);
    }
    return null;
  }

  //@formatter:on
  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    PxTableInfo tableInfo = mTableList.get(position);
    String type = tableInfo.getType();
    switch (holder.getItemViewType() + "") {
      //空闲
      case PxTableInfo.STATUS_EMPTY:
        ViewHolderEmpty holderEmpty = (ViewHolderEmpty) holder;
        //建议人数
        holderEmpty.mTvSuggestPeopleNum.setText("建议" + tableInfo.getPeopleNum() + "人");
        //类型名称
        if (type != null) {
          PxTableArea tableArea = DaoServiceUtil.getTableAreaService()
              .queryBuilder()
              .where(PxTableAreaDao.Properties.DelFlag.eq("0"))
              .where(PxTableAreaDao.Properties.Type.eq(type))
              .unique();
          holderEmpty.mTvTypeName.setText(tableArea == null ? "" : tableArea.getName());
        }else{
          holderEmpty.mTvTypeName.setText("");
        }
        //@formatter:off
        //桌台名称
        holderEmpty.mTvTableNumber.setText(tableInfo.getName());
        //更换背景
        if (position == mCurrentSelected) {
          holderEmpty.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_sel_table_container_status_normal));
          holderEmpty.mViewContent.setBackgroundResource(R.color.item_sel_table_content_status_normal);
        }else{
          holderEmpty.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_empty_table_container_status_normal));
          holderEmpty.mViewContent.setBackgroundResource(R.color.item_empty_table_content_status_normal);
        }

        //点击
        if (mOnTableClickListener != null) {
          holderEmpty.mViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              mOnTableClickListener.onEmptyTableClick(position);
            }
          });
        }
        break;
      //占用
      case PxTableInfo.STATUS_OCCUPIED:
        QueryBuilder<TableOrderRel> queryBuilder = DaoServiceUtil.getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxTableInfoId.eq(tableInfo.getId()));
        Join<TableOrderRel, PxOrderInfo> join = queryBuilder.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
        join.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
        List<TableOrderRel> list = queryBuilder.list();
        if (list == null || list.size() == 0) return;

        ViewHolderOccupied holderOccupied = (ViewHolderOccupied) holder;

        if (list.size() == 1) {
          PxOrderInfo currentOrder = list.get(0).getDbOrder();
          //实际人数
          holderOccupied.mTvPeopleNumber.setText(currentOrder.getActualPeopleNumber() + "人");
          long duration = new Date().getTime() - currentOrder.getStartTime().getTime();
          //持续时间
          holderOccupied.mTvDuration.setText("开单" + duration / 1000 / 60 + "分钟");
        } else {
          //人数
          holderOccupied.mTvPeopleNumber.setText("共" + list.size() + "单");
          //持续时间
          holderOccupied.mTvDuration.setText("");
        }
        //桌台名称
        holderOccupied.mTvTableNumber.setText(tableInfo.getName());
        //类型名称
        if (type != null) {
          PxTableArea tableArea = DaoServiceUtil.getTableAreaService()
              .queryBuilder()
              .where(PxTableAreaDao.Properties.DelFlag.eq("0"))
              .where(PxTableAreaDao.Properties.Type.eq(type))
              .unique();
          holderOccupied.mTvTypeName.setText(tableArea == null ? "":tableArea.getName());
        }else{
          holderOccupied.mTvTypeName.setText(" ");
        }
        //更换背景
        if (position == mCurrentSelected) {
          holderOccupied.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_sel_table_container_status_normal));
          holderOccupied.mViewContent.setBackgroundResource(R.color.item_sel_table_content_status_normal);
        }else {
          holderOccupied.mViewContainer.setCardBackgroundColor(mContext.getResources().getColor(R.color.item_occupy_table_container_status_normal));
          holderOccupied.mViewContent.setBackgroundResource(R.color.item_occupy_table_content_status_normal);
        }

        //点击
        if (mOnTableClickListener != null) {
          holderOccupied.mViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              mOnTableClickListener.onOccupiedTableClick(position);
            }
          });
        }

        break;
    }
  }

  @Override public int getItemCount() {
    return mTableList.size();
  }

  @Override public int getItemViewType(int position) {
    return Integer.parseInt(mTableList.get(position).getStatus());
  }

  /**
   * 已占用Adapter
   */
  class ViewHolderOccupied extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_people_number) TextView mTvPeopleNumber;
    @Bind(R.id.tv_duration) TextView mTvDuration;
    @Bind(R.id.tv_type_name) TextView mTvTypeName;
    @Bind(R.id.tv_table_number) TextView mTvTableNumber;
    @Bind(R.id.view_container) CardView mViewContainer;
    @Bind(R.id.view_content) RelativeLayout mViewContent;

    public ViewHolderOccupied(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 空闲Adapter
   */
  class ViewHolderEmpty extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_suggest_people_num) TextView mTvSuggestPeopleNum;
    @Bind(R.id.tv_type_name) TextView mTvTypeName;
    @Bind(R.id.tv_table_number) TextView mTvTableNumber;
    @Bind(R.id.view_container) CardView mViewContainer;
    @Bind(R.id.view_content) RelativeLayout mViewContent;

    public ViewHolderEmpty(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 设置数据
   */
  public void setData(List<PxTableInfo> data) {
    if (null == data) {
      return;
    } else {
      mTableList = data;
      mCurrentSelected = -1;
      this.notifyDataSetChanged();
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
  public static interface OnTableClickListener {
    void onOccupiedTableClick(int pos);

    void onEmptyTableClick(int pos);
  }

  public OnTableClickListener mOnTableClickListener;

  public void setOnTableClickListener(OnTableClickListener onTableClickListener) {
    mOnTableClickListener = onTableClickListener;
  }
}
