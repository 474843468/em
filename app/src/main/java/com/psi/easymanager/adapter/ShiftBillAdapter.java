package com.psi.easymanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.psi.easymanager.R;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxOrderInfo;

import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.NumberFormatUtils;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psi on 2016/7/30.
 * 交接班-所有账单信息适配器
 */
public class ShiftBillAdapter extends RecyclerView.Adapter<ShiftBillAdapter.ViewHolder> {

  private Context mContext;
  private List<PxOrderInfo> mOrderInfoList;

  public ShiftBillAdapter(Context context, List<PxOrderInfo> orderInfoList) {
    mContext = context;
    if (orderInfoList == null) {
      this.mOrderInfoList = new ArrayList<>();
    } else {
      this.mOrderInfoList = orderInfoList;
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_shift_bill, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    PxOrderInfo orderInfo = mOrderInfoList.get(position);
    if (orderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
          .unique();
      holder.mTvTableName.setText(unique.getDbTable().getName());
    } else {
      holder.mTvTableName.setText("零售单");
    }
    holder.mTvOrderNo.setText("账单号:" + orderInfo.getOrderNo());
    holder.mTvReceivableMoney.setText("应收金额:" + orderInfo.getAccountReceivable());
    holder.mTvTailMoney.setText("抹零:" + orderInfo.getTailMoney());
    double realValue = orderInfo.getRealPrice() - (orderInfo.getTotalChange() == null ? 0 : orderInfo.getTotalChange());
    holder.mTvRealMoney.setText("实际收入:" + NumberFormatUtils.formatFloatNumber(realValue));

    //支付方式收入
    SQLiteDatabase database = DaoServiceUtil.getPayInfoDao().getDatabase();
    Cursor cursor = database.rawQuery("Select sum(pay.RECEIVED),pay.PAYMENT_NAME"
        + " From PxPayInfo pay"
        + " Where pay.PX_ORDER_INFO_ID = " + orderInfo.getId()
        + " Group by pay.PAYMENT_NAME", null);
    StringBuilder sb = new StringBuilder();
    while (cursor.moveToNext()) {
      Double received = cursor.getDouble(0);
      String name = cursor.getString(1);
      sb.append(name + ":\t\t" + received + "\t\t");
    }
    IOUtils.closeCloseables(cursor);
    holder.mTvPaymentReceived.setText(sb.toString());
  }

  @Override public int getItemCount() {
    return mOrderInfoList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_table_name) TextView mTvTableName;
    @Bind(R.id.tv_order_no) TextView mTvOrderNo;
    @Bind(R.id.tv_receivable_money) TextView mTvReceivableMoney;
    @Bind(R.id.tv_tail_money) TextView mTvTailMoney;
    @Bind(R.id.tv_real_money) TextView mTvRealMoney;
    @Bind(R.id.tv_payment_received) TextView mTvPaymentReceived;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  /**
   * 设置数据
   */
  public void setData(List<PxOrderInfo> data) {
    if (data != null) {
      this.mOrderInfoList = data;
      this.notifyDataSetChanged();
    }
  }
}
