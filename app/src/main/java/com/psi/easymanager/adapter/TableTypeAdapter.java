package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.AppTableType;
import com.psi.easymanager.module.PxTableArea;
import java.util.List;

/**
 * Created by zjq 2016/3/21.
 * 找单 餐桌分类适配器
 */
public class TableTypeAdapter extends BaseAdapter {
  private List<AppTableType> mTableTypeList;
  private Context context;

  public TableTypeAdapter(Context context, List<AppTableType> tableTypeList) {
    this.context = context;
    this.mTableTypeList = tableTypeList;
  }

  @Override public int getCount() {
    return mTableTypeList.size();
  }

  @Override public Object getItem(int position) {
    return mTableTypeList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    Holder holer;
    if (convertView == null) {
      holer = new Holder();
      convertView = LayoutInflater.from(context).inflate(R.layout.item_table_type, null);
      holer.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
      convertView.setTag(holer);
    } else {
      holer = (Holder) convertView.getTag();
    }
    AppTableType tableType = mTableTypeList.get(position);
    if (AppTableType.ALL.equals(tableType.getType()) || AppTableType.RETAIL.equals(tableType.getType())){//全部、零售
      holer.mTvName.setText(tableType.getName());
    }else {//桌台区域
      PxTableArea tableArea = tableType.getTableArea();
      holer.mTvName.setText(tableArea.getName());
    }
    return convertView;
  }

  class Holder {
    TextView mTvName;
  }

  /**
   * 设置数据
   */
  public void setData(List<AppTableType> data) {
    if (null == data) {
      return;
    } else {
      mTableTypeList = data;
      this.notifyDataSetChanged();
    }
  }
}
