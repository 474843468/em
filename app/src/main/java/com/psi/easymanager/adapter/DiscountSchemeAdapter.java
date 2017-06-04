package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.psi.easymanager.R;
import com.psi.easymanager.module.PxDiscounScheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lj on 2016/11/23.
 * 结账页面 方案打折对话框 商品适配器
 */
public class DiscountSchemeAdapter extends BaseAdapter {
  private Context mContext;
  private List<PxDiscounScheme> mSchemeList;

  public DiscountSchemeAdapter(Context context, List<PxDiscounScheme> schemeList) {
    mContext = context;
    if (schemeList != null) {
      mSchemeList = schemeList;
    } else {
      mSchemeList = new ArrayList<PxDiscounScheme>();
    }
  }

  @Override public int getCount() {
    return mSchemeList.size();
  }

  @Override public Object getItem(int position) {
    return mSchemeList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    final ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_discount_scheme, parent, false);
      viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb_scheme);
      viewHolder.tvScheme = (TextView) convertView.findViewById(R.id.tv_scheme);
      viewHolder.tvDiscount = (TextView) convertView.findViewById(R.id.tv_discount);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    final PxDiscounScheme discScheme = mSchemeList.get(position);
    viewHolder.tvScheme.setText( discScheme.getName());
    viewHolder.cb.setChecked(discScheme.isSelected());
    int discountRate = discScheme.getRate().intValue();
    if (discountRate < 100 && discountRate > 0) {
      viewHolder.tvDiscount.setText("(折扣" + discountRate + "%)");
    }
    viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //选中
        if (isChecked) {
          for (PxDiscounScheme scheme : mSchemeList) {
            scheme.setIsSelected(false);
          }
          discScheme.setIsSelected(true);
        }
        //未选中
        else {
          discScheme.setIsSelected(false);
        }
        DiscountSchemeAdapter.this.notifyDataSetChanged();
      }
    });
    return convertView;
  }

  static class ViewHolder {
    CheckBox cb;
    TextView tvScheme;
    TextView tvDiscount;
  }
}
