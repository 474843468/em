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
import com.psi.easymanager.dao.PxProductCategoryDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxProductCategory;
import java.util.List;

/**
 * Created by dorado on 2016/6/18.
 */
public class CashMenuChildCateAdapter extends RecyclerView.Adapter<CashMenuChildCateAdapter.ViewHolder> {

  private Context mContext;
  private List<PxProductCategory> mCategoryList;
  private int mSelectedPos;

  public CashMenuChildCateAdapter(Context context, List<PxProductCategory> categoryList) {
    mContext = context;
    mCategoryList = categoryList;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_cate, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, final int position) {
    PxProductCategory category = mCategoryList.get(position);
    holder.mTvName.setText(category.getName());
    PxProductCategory parentCategory = DaoServiceUtil.getProductCategoryService()
        .queryBuilder()
        .where(PxProductCategoryDao.Properties.ObjectId.eq(category.getParentId()))
        .unique();
    if (parentCategory != null){
      holder.mTvParentName.setText("所属分类:" + parentCategory.getName());
    } else {
      holder.mTvParentName.setText("");
    }
    if (mOnChildCateClickListener != null) {
      holder.mViewContainer.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mSelectedPos = position;
          mOnChildCateClickListener.onChildCateClick(position);
        }
      });
    }
  }

  @Override public int getItemCount() {
    return mCategoryList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_name) TextView mTvName;
    @Bind(R.id.tv_parent_name) TextView mTvParentName;
    @Bind(R.id.view_container) View mViewContainer;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * SetData
   */
  public void setData(List<PxProductCategory> data) {
    if (data == null) return;
    this.mCategoryList = data;
    this.notifyDataSetChanged();
  }
  ///**
  // * 设置数据
  // */
  //public void setData(DiffUtil.DiffResult result, List<PxProductCategory> data) {
  //  this.mCategoryList = data;
  //  result.dispatchUpdatesTo(this);
  //}
  /**
   * Click
   */
  public interface OnChildCateClickListener {
    void onChildCateClick(int pos);
  }

  public OnChildCateClickListener mOnChildCateClickListener;

  public void setOnChildCateClickListener(OnChildCateClickListener onChildCateClickListener) {
    mOnChildCateClickListener = onChildCateClickListener;
  }

  /**
   * 获取选中
   */
  public int getSelected(){
    return mSelectedPos;
  }
}
