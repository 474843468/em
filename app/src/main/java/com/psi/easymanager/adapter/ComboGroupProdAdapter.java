package com.psi.easymanager.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.event.AddComboProdClickEvent;
import com.psi.easymanager.module.AppComboGroupInfo;
import com.psi.easymanager.module.AppComboGroupProdInfo;
import com.psi.easymanager.module.PxComboGroup;
import com.psi.easymanager.module.PxProductInfo;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by dorado on 2016/8/18.
 */
public class ComboGroupProdAdapter extends BaseAdapter {
  private Context mContext;
  private List<AppComboGroupProdInfo> mAppComoGroupProdInfoList;
  private AppComboGroupInfo mAppComboGroupInfo;

  public ComboGroupProdAdapter(Context context, List<AppComboGroupProdInfo> appProductInfoList, AppComboGroupInfo appComboGroupInfo) {
    mContext = context;
    mAppComoGroupProdInfoList = appProductInfoList;
    mAppComboGroupInfo = appComboGroupInfo;
  }

  @Override public int getCount() {
    return mAppComoGroupProdInfoList.size();
  }

  @Override public Object getItem(int position) {
    return mAppComoGroupProdInfoList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_combo_group_prod, null);
      viewHolder = new ViewHolder();
      viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_prod_name);
      viewHolder.mTvNum = (TextView) convertView.findViewById(R.id.tv_prod_num);
      viewHolder.mTvWeight = (TextView) convertView.findViewById(R.id.tv_prod_weight);
      viewHolder.mTvFormat = (TextView) convertView.findViewById(R.id.tv_prod_format);
      viewHolder.mIvSelected = (ImageView) convertView.findViewById(R.id.iv_prod_selected);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    final AppComboGroupProdInfo appComoGroupProdInfo = mAppComoGroupProdInfoList.get(position);
    PxProductInfo productInfo = appComoGroupProdInfo.getProductInfo();
    viewHolder.mTvName.setText(productInfo.getName());
    //数量
    viewHolder.mTvNum.setText(appComoGroupProdInfo.getNum() + "");
    //双单位
    if (productInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      viewHolder.mTvWeight.setText(appComoGroupProdInfo.getWeight() + productInfo.getUnit());
      viewHolder.mTvWeight.setVisibility(View.VISIBLE);
    } else {
      viewHolder.mTvWeight.setVisibility(View.GONE);
    }
    //规格
    if (appComoGroupProdInfo.getFormatInfo() != null) {
      viewHolder.mTvFormat.setText("(" + appComoGroupProdInfo.getFormatInfo().getName() + ")");
      viewHolder.mTvFormat.setVisibility(View.VISIBLE);
    } else {
      viewHolder.mTvFormat.setVisibility(View.GONE);
    }
    //选中
    if (appComoGroupProdInfo.isSelected()) {
      viewHolder.mIvSelected.setVisibility(View.VISIBLE);
    } else {
      viewHolder.mIvSelected.setVisibility(View.GONE);
    }
    return convertView;
  }

  class ViewHolder {
    TextView mTvName;
    TextView mTvNum;
    TextView mTvWeight;
    TextView mTvFormat;
    ImageView mIvSelected;
  }
}
