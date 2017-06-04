package com.psi.easymanager.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.dao.BTPrintDeviceDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.BTPrintDevice;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ylw
 * Date: 2017-02-23
 * Time: 16:47
 * BT Devices
 */
public class BTDevicesAdapter extends RecyclerView.Adapter<BTDevicesAdapter.VH> {

  private Context mContext;
  private List<BluetoothDevice> mList;
  private ItemClickListener mListener;

  public BTDevicesAdapter(Context context) {
    mContext = context;
    mList = new ArrayList<>();
  }

  @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_bt_devices, parent, false);
    view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mListener.clickItem((Integer) v.getTag());
      }
    });

    view.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        mListener.longClick((Integer) v.getTag());
        return true;
      }
    });
    return new VH(view);
  }

  @Override public void onBindViewHolder(VH holder, int position) {
    BluetoothDevice device = mList.get(position);
    String address = device.getAddress();
    holder.mTvNameAndAddress.setText(device.getName() + "\n" + address);
    holder.itemView.setTag(position);
    //不同设备类型该值不同，比如computer蓝牙为256、phone 蓝牙为512、打印机蓝牙为1536等等。
    int deviceType = device.getBluetoothClass().getMajorDeviceClass();
    switch (deviceType) {
      case BluetoothClass.Device.Major.COMPUTER:
        holder.mIvDeviceType.setImageResource(R.mipmap.ic_bt_computer);
        break;
      case BluetoothClass.Device.Major.IMAGING:
        holder.mIvDeviceType.setImageResource(R.mipmap.ic_bt_printer);
        break;
      case BluetoothClass.Device.Major.AUDIO_VIDEO:
        holder.mIvDeviceType.setImageResource(R.mipmap.ic_bt_headset);
        break;
      case BluetoothClass.Device.Major.PHONE:
        holder.mIvDeviceType.setImageResource(R.mipmap.ic_bt_phone);
        break;
      case BluetoothClass.Device.Major.UNCATEGORIZED:
        holder.mIvDeviceType.setImageResource(R.mipmap.ic_bt_printer);
        break;
      default:
        holder.mIvDeviceType.setImageResource(R.mipmap.ic_bt_phone);
        break;
    }

    BTPrintDevice dbDevice = DaoServiceUtil.getBTDeviceService()
        .queryBuilder()
        .where(BTPrintDeviceDao.Properties.Address.eq(address))
        .unique();
    holder.mTvAddStatus.setVisibility(dbDevice == null ? View.GONE : View.VISIBLE);
  }

  @Override public int getItemCount() {
    return mList.size();
  }

  public void setData(List<BluetoothDevice> list) {
    this.mList = list;
    notifyDataSetChanged();
  }

  public interface ItemClickListener {
    void clickItem(int pos);

    void longClick(int pos);
  }

  public void setItemClickListener(ItemClickListener listener) {
    this.mListener = listener;
  }

  class VH extends RecyclerView.ViewHolder {
    private TextView mTvNameAndAddress, mTvAddStatus;
    private ImageView mIvDeviceType;

    public VH(View itemView) {
      super(itemView);
      mTvNameAndAddress = (TextView) itemView.findViewById(R.id.tv_name_address);
      mTvAddStatus = (TextView) itemView.findViewById(R.id.tv_add_status);
      mIvDeviceType = (ImageView) itemView.findViewById(R.id.iv_bt_device_type);
    }
  }
}  