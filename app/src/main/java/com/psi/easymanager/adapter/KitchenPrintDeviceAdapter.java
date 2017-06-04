package com.psi.easymanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxProductConfigPlan;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by psi on 2016/3/17.
 * 厨房打印设备适配器
 */
public class KitchenPrintDeviceAdapter
    extends RecyclerView.Adapter<KitchenPrintDeviceAdapter.ViewHolder> {
  private Context mContext;
  private List mDeviceList;
  private int mPrePos = -1;//选择前pos
  public int mCurrentPos = -1;//当前选择Pos
  private Map<String, String> mConnectMap;//打印机连接状态

  public void setSelected(int position) {
    if (mCurrentPos == position) return;
    mCurrentPos = position;
    if (mPrePos != -1) {
      notifyItemChanged(mPrePos);
    }
    notifyItemChanged(mCurrentPos);
    mPrePos = mCurrentPos;
  }

  public KitchenPrintDeviceAdapter(Context context) {
    this.mContext = context;
    this.mConnectMap = new HashMap<>();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_devices, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    if (mDeviceList != null) {
      Object obj = mDeviceList.get(position);
      if (obj instanceof PxProductConfigPlan) {
        PxProductConfigPlan configPlan = (PxProductConfigPlan) obj;
        //配菜方案类型
        typeConfigPlan(holder, configPlan);
      } else {
        PxPrinterInfo device = (PxPrinterInfo) obj;
        typeCashPrinter(holder, device);
      }

      if (mCurrentPos == position) {
        holder.mLlKitchenPrint.setBackgroundColor(mContext.getResources().getColor(R.color.grey_bg));
      } else {
        holder.mLlKitchenPrint.setBackgroundColor(Color.TRANSPARENT);
      }
    }
    //holder.mLlKitchenPrint.setTag(holder.getLayoutPosition());
    holder.mLlKitchenPrint.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onCallClickListener.onCallClick(position);
      }
    });
  }

  /**
   * 打印机类型
   */
  private void typeCashPrinter(ViewHolder holder, PxPrinterInfo device) {
    String connectStatus = mConnectMap.get(device.getIpAddress());
    if (PxPrinterInfo.CONNECTED.equals(connectStatus)) {
      holder.mTvPrintConnected.setVisibility(View.VISIBLE);
      holder.mTvPrintNotConnect.setVisibility(View.GONE);
    } else {
      holder.mTvPrintNotConnect.setVisibility(View.VISIBLE);
      holder.mTvPrintConnected.setVisibility(View.GONE);
    }
    holder.mTvPrinterIP.setText(device.getIpAddress());
    holder.mTvPrintCopies.setText("打印类型:收银");
    //配菜方案名称
    holder.mTvConfig.setText("设备:" + device.getName());
    //设备名称
    holder.mTvPrinterName.setText("收银打印机");
    holder.mTvDoOnce.setVisibility(View.GONE);
    holder.mIvArrow.setVisibility(View.GONE);
  }

  /**
   * 配菜方案类型
   */
  private void typeConfigPlan(ViewHolder holder, PxProductConfigPlan configPlan) {
    PxPrinterInfo device = configPlan.getDbPrinter();
    String connectStatus = mConnectMap.get(device.getIpAddress());
    holder.mTvDoOnce.setVisibility(View.VISIBLE);
    holder.mIvArrow.setVisibility(View.VISIBLE);
    if (PxPrinterInfo.CONNECTED.equals(connectStatus)) {
      holder.mTvPrintConnected.setVisibility(View.VISIBLE);
      holder.mTvPrintNotConnect.setVisibility(View.GONE);
    } else {
      holder.mTvPrintNotConnect.setVisibility(View.VISIBLE);
      holder.mTvPrintConnected.setVisibility(View.GONE);
    }
    holder.mTvPrinterIP.setText(device.getIpAddress());

    //类型
    String deviceType = device.getType();
    switch (deviceType) {
      case PxPrinterInfo.TYPE_CASH:
        deviceType = "收银";
        break;
      case PxPrinterInfo.TYPE_KITCH:
        deviceType = "后厨";
        break;
      case PxPrinterInfo.TYPE_CONSUME:
        deviceType = "消费底联";
        break;
    }
    holder.mTvPrintCopies.setText("打印类型:" + deviceType);
    holder.mTvDoOnce.setText((PxProductConfigPlan.NO_ONCE_PRINT.equals(configPlan.getFlag())) ? "一菜一切:否" : "一菜一切:是");
    //配菜方案名称
    holder.mTvConfig.setText("设备:" + device.getName());
    //设备名称
    holder.mTvPrinterName.setText("配菜方案: " + configPlan.getName());
  }

  @Override public int getItemCount() {
    return mDeviceList.size();
  }

  //@Override public void onClick(View v) {
  //  onCallClickListener.onCallClick((int) v.getTag());
  //}

  class ViewHolder extends RecyclerView.ViewHolder {
    private TextView mTvPrinterName;//设备名称
    private TextView mTvPrintConnected;//已连接
    private TextView mTvPrintNotConnect;//未连接
    private TextView mTvPrinterIP;//设备IP
    private TextView mTvDoOnce;//是否一菜一切
    private TextView mTvPrintCopies;//打印份数
    private TextView mTvConfig;//配菜方案
    private LinearLayout mLlKitchenPrint;//item容器
    private ImageView mIvArrow;//右箭头

    public ViewHolder(View itemView) {
      super(itemView);
      mTvPrinterName = (TextView) itemView.findViewById(R.id.tv_kitchen_print_device_name);
      mTvPrintConnected = (TextView) itemView.findViewById(R.id.tv_kitchen_connected);
      mTvPrintNotConnect = (TextView) itemView.findViewById(R.id.tv_kitchen_not_connect);
      mTvPrinterIP = (TextView) itemView.findViewById(R.id.tv_kitchen_print_ip);

      mTvDoOnce = (TextView) itemView.findViewById(R.id.tv_kitchen_print_do_once);
      mTvPrintCopies = (TextView) itemView.findViewById(R.id.tv_kitchen_print_copies);
      mTvConfig = (TextView) itemView.findViewById(R.id.tv_kitchen_print_config);
      mLlKitchenPrint = (LinearLayout) itemView.findViewById(R.id.ll_kitchen_print_view);
      mIvArrow = (ImageView) itemView.findViewById(R.id.iv_kitchen_print_temp);
    }
  }

  /**
   * 添加数据
   */
  public void setData(List devices) {
    if (null != devices) {
      //重置状态
      mPrePos = -1;
      mCurrentPos = -1;
      //清空 、填充新数据
      mDeviceList = devices;
      this.notifyDataSetChanged();
    }
  }

  /**
   * 只用于刷新连接状态
   */
  public void setRefreshData(Map<String, String> map) {
    if (mDeviceList != null && !mDeviceList.isEmpty()) {
      this.mConnectMap = map;
      this.notifyItemRangeChanged(0, mDeviceList.size());
    }
  }

  /**
   * 设备条目
   */
  public interface OnCallClickListener {
    void onCallClick(int pos);
  }

  private OnCallClickListener onCallClickListener;

  public void setOnCallClickListener(OnCallClickListener onCallClickListener) {
    this.onCallClickListener = onCallClickListener;
  }
}