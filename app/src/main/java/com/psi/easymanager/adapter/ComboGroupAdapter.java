package com.psi.easymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.event.AddComboProdClickEvent;
import com.psi.easymanager.module.AppComboGroupInfo;
import com.psi.easymanager.module.AppComboGroupProdInfo;
import com.psi.easymanager.module.PxComboGroup;
import com.psi.easymanager.widget.NestGridView;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by dorado on 2016/8/18.
 */
public class ComboGroupAdapter extends BaseAdapter {
  private Context mContext;
  private List<AppComboGroupInfo> mGroupInfoList;

  public ComboGroupAdapter(Context context, List<AppComboGroupInfo> groupInfoList) {
    mContext = context;
    mGroupInfoList = groupInfoList;
  }

  @Override public int getCount() {
    return mGroupInfoList.size();
  }

  @Override public Object getItem(int position) {
    return mGroupInfoList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  //@formatter:off
  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_combo_group, null);
      viewHolder = new ViewHolder();
      viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
      viewHolder.mTvNumSelected = (TextView) convertView.findViewById(R.id.tv_num_selected);
      viewHolder.mTvNumAll = (TextView) convertView.findViewById(R.id.tv_num_all);
      viewHolder.mGvProd = (NestGridView) convertView.findViewById(R.id.gv_prod);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    final AppComboGroupInfo appComboGroupInfo = mGroupInfoList.get(position);
    PxComboGroup comboGroup = appComboGroupInfo.getComboGroup();

    //允许选择的数量
    Integer allowNum = appComboGroupInfo.getNumAllow();
    viewHolder.mTvNumAll.setText("" + allowNum);
    //已选数量
    int numSelected = appComboGroupInfo.getNumSelected();
    viewHolder.mTvNumSelected.setText("" + numSelected);

    //名称
    if (comboGroup.getType().equals(PxComboGroup.TYPE_REQUIRED)) {
      viewHolder.mTvName.setText(comboGroup.getName() + "(必选)");
    } else {
      viewHolder.mTvName.setText(comboGroup.getName() + "(自选)");
    }
    //商品
    List<AppComboGroupProdInfo> appProdInfoList = appComboGroupInfo.getAppComboGroupProdInfoList();
    //适配器
    ComboGroupProdAdapter prodAdapter = new ComboGroupProdAdapter(mContext, appProdInfoList, appComboGroupInfo);
    viewHolder.mGvProd.setAdapter(prodAdapter);
    viewHolder.mGvProd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventBus.getDefault().post(new AddComboProdClickEvent().setPos(position).setComboGroup(appComboGroupInfo));
      }
    });
    return convertView;
  }

  class ViewHolder {
    //名称
    TextView mTvName;
    //所选数量
    TextView mTvNumSelected;
    //所有数量
    TextView mTvNumAll;
    //商品列表
    NestGridView mGvProd;
  }
}
