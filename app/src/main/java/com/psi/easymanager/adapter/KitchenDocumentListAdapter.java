package com.psi.easymanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.AppPrinterDetails;
import com.psi.easymanager.module.PdConfigRel;
import com.psi.easymanager.module.PrintDetails;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by psi on 2016/3/17.
 * 厨房打印单据列表适配器
 */
public class KitchenDocumentListAdapter
    extends RecyclerView.Adapter<KitchenDocumentListAdapter.ViewHolder>
    implements View.OnClickListener {
  public static final int TYPE_ADD = 0;
  public static final int TYPE_RETREAT = 1;
  public static final int TYPE_MERGE = 2;
  public static final int TYPE_MOVE = 3;
  private Context mContext;
  private List mNeedList;

  private int mPrePos = -1;//选择前pos
  public int mCurrentPos = -1;//当前选择Pos
  private String mCategoryName;//类型名字
  private boolean mIsOncePrint;//一次一切
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  //private boolean mIsPrinted;//单据打印状态 false 未打印

  public KitchenDocumentListAdapter(Context context) {
    mContext = context;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item_document_list, parent, false);
    view.setOnClickListener(this);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, int position) {
    if (mNeedList == null) return;
    AppPrinterDetails printerDetails = (AppPrinterDetails) mNeedList.get(position);
    switch (printerDetails.getType()) {
      case AppPrinterDetails.TYPE_DETAILS:
        typeDetails(printerDetails, holder, position);
        break;
      case AppPrinterDetails.TYPE_TABALTER:
        typeTableAlert(printerDetails, holder, position);
        break;
    }
    //选择状态 标记
    if (mCurrentPos == position) {
      holder.mRlItem.setBackgroundColor(mContext.getResources().getColor(R.color.grey_bg));
    } else {
      holder.mRlItem.setBackgroundColor(Color.TRANSPARENT);
    }
    //item 点击
    holder.mRlItem.setTag(holder.getAdapterPosition());
  }

  @Override public void onClick(View v) {
    int pos = (int) v.getTag();
    onCallBackClickListener.onCallBackClick(pos);
    mPrePos = mCurrentPos;
  }

  /**
   * 换并桌 type
   */
  private void typeTableAlert(AppPrinterDetails printerDetails, ViewHolder holder, int position) {
    PxTableAlteration tableAlert = printerDetails.getAlteration();
    PxTableInfo dbOriginalTable = tableAlert.getDbOriginalTable();
    PxTableInfo dbTargetTable = tableAlert.getDbTargetTable();
    PxOrderInfo dbOrder = tableAlert.getDbOrder();
    String type = tableAlert.getType();
    holder.mTvTableNum.setText(sdf.format(tableAlert.getOperateTime()));
    holder.mTvCategory.setText("");
    if (dbOrder != null) {
      holder.mTvPlaceType.setText("No." + dbOrder.getId());
      holder.mTvOrderNo.setText("单号: " + dbOrder.getOrderNo().substring(10, 30));
    }
    //置定type
    int showType = 0;
    if (dbOriginalTable != null && dbTargetTable != null) {
      switch (type) {
        case PxTableAlteration.TYPE_MERGE:
          showType = TYPE_MERGE;
          holder.mTvDocuments.setText(dbOriginalTable.getName() + "并单到" + dbTargetTable.getName());
          break;
        case PxTableAlteration.TYPE_MOVE:
          showType = TYPE_MOVE;
          holder.mTvDocuments.setText(dbOriginalTable.getName() + "移动到" + dbTargetTable.getName());
          break;
      }
    }
    showDetailsType(holder, showType);
    showPrintType(holder, tableAlert.getIsPrinted());
  }

  /**
   * 订单详情 type
   */
  private void typeDetails(AppPrinterDetails printerDetails, ViewHolder holder, int position) {
    PrintDetails details = null;
    String content = "";
    Boolean isPrint = false;
    if (mIsOncePrint) {
      PdConfigRel rel = printerDetails.getRel();
      details = rel.getDbPrintDetails();
      content = packageDocument(details);
      isPrint = rel.getIsPrinted();
    } else {
      List<PrintDetails> detailsList = (List<PrintDetails>) printerDetails.getDetails();
      PrintDetailsCollect collect = printerDetails.getCollect();
      details = detailsList.get(0);
      content = toDBC(packageDocuments(printerDetails));
      isPrint = collect.getIsPrint();
    }
    showPrintType(holder, isPrint);
    holder.mTvDocuments.setText(content);
    //填充基础信息
    PxOrderInfo dbOrder = details.getDbOrder();

    //桌位单
    if (dbOrder.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(dbOrder.getId()))
          .unique();
      PxTableInfo dbTable = unique.getDbTable();
      String type = dbTable.getType();
     if (type != null) {
       PxTableArea area = DaoServiceUtil.getTableAreaService()
           .queryBuilder()
           .where(PxTableAreaDao.Properties.Type.eq(type))
           .unique();
       holder.mTvPlaceType.setText(area == null ? "大厅" : area.getName());
     }else{
       holder.mTvPlaceType.setText("大厅");
     }
      holder.mTvTableNum.setText(dbTable.getName());
    } else { //零售单
      holder.mTvPlaceType.setText("零售单");
      holder.mTvTableNum.setText("无桌");
    }
    holder.mTvCategory.setText(mCategoryName);
    holder.mTvOrderNo.setText("单号: " + dbOrder.getOrderNo().substring(10, 30));
    //置定type
    int type = (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) ? 1 : 0;
    showDetailsType(holder, type);
  }

  @Override public int getItemCount() {
    return mNeedList == null ? 0 : mNeedList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    private TextView mTvAddType;//加菜类型
    private TextView mTvMergeType;//并桌
    private TextView mTvMoveType;//移桌
    private TextView mTvRetreatType;//退菜类型
    private TextView mTvPlaceType;//0大厅，1包厢
    private TextView mTvTableNum;//桌名
    private TextView mTvCategory;//商品分类
    private TextView mTvDocuments;//商品列表
    private RelativeLayout mRlItem;//item容器
    private TextView mTvPrinted;//已打印
    private TextView mTvNoPrinted;//未打印
    private TextView mTvOrderNo;//单号

    public ViewHolder(View itemView) {
      super(itemView);
      mTvAddType = (TextView) itemView.findViewById(R.id.tv_document_list_add_type);
      mTvMergeType = (TextView) itemView.findViewById(R.id.tv_document_list_merge_type);
      mTvMoveType = (TextView) itemView.findViewById(R.id.tv_document_list_move_type);
      mTvRetreatType = (TextView) itemView.findViewById(R.id.tv_document_list_retreat_type);
      mTvPlaceType = (TextView) itemView.findViewById(R.id.tv_kitchen_print_place_type);

      mTvTableNum = (TextView) itemView.findViewById(R.id.tv_kitchen_print_table_num);
      mTvCategory = (TextView) itemView.findViewById(R.id.tv_kitchen_print_category);
      mTvDocuments = (TextView) itemView.findViewById(R.id.tv_kitchen_print_documents);
      mRlItem = (RelativeLayout) itemView.findViewById(R.id.rl_document_list_view);
      mTvPrinted = (TextView) itemView.findViewById(R.id.tv_document_list_printed);

      mTvNoPrinted = (TextView) itemView.findViewById(R.id.tv_document_list_no_printed);
      mTvOrderNo = (TextView) itemView.findViewById(R.id.tv_kitchen_print_order_no);
    }
  }

  /**
   * 显示商品状态 1退菜 0加菜 2并桌 3移桌
   */
  private void showDetailsType(ViewHolder holder, int type) {
    holder.mTvAddType.setVisibility((type == TYPE_ADD) ? View.VISIBLE : View.GONE);
    holder.mTvRetreatType.setVisibility((type == TYPE_RETREAT) ? View.VISIBLE : View.GONE);
    holder.mTvMoveType.setVisibility((type == TYPE_MOVE) ? View.VISIBLE : View.GONE);
    holder.mTvMergeType.setVisibility((type == TYPE_MERGE) ? View.VISIBLE : View.GONE);
  }

  /**
   * 显示是否已打印字样
   */
  private void showPrintType(ViewHolder holder, Boolean isPrint) {
    if (isPrint == null) {
      isPrint = false;
    }
    holder.mTvPrinted.setVisibility(isPrint ? View.VISIBLE : View.GONE);
    holder.mTvNoPrinted.setVisibility(isPrint ? View.GONE : View.VISIBLE);
  }

  /**
   * 填充数据
   */
  public void setData(List list, boolean isOncePrint) {
      this.mIsOncePrint = isOncePrint;
      //重置状态
      mPrePos = -1;
      mCurrentPos = -1;
      //清空 、 填充新数据
      mNeedList = list;
      this.notifyDataSetChanged();
  }

  /**
   * 包装 所有orderdetails
   */
  private String packageDocuments(AppPrinterDetails printerDetails) {
    List<PrintDetails> detailsList = (List<PrintDetails>) printerDetails.getDetails();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < detailsList.size(); i++) {
      PrintDetails details = detailsList.get(i);
      if (i > 0) {
        sb.append("   " + packageDocument(details));
      } else {
        sb.append(packageDocument(details));
      }
    }
    return sb.toString();
  }

  /**
   * 包装单个商品 名字和数量
   */
  private String packageDocument(PrintDetails details) {
    PxProductInfo product = details.getDbProduct();
    boolean twoUnit = PxProductInfo.IS_TWO_UNIT_TURE.equals(product.getMultipleUnit());
    String prodName = product.getName();
    String unit = product.getUnit();
    String orderUnit = product.getOrderUnit();

    String nameAndNum = null;
    if (twoUnit) {
      nameAndNum = prodName+ "(" + getMultiUnit(details.getMultipleUnitNumber()) + unit + ")" + "/" + details.getNum().intValue() + orderUnit;
    } else {
      nameAndNum = prodName + "(" + details.getNum().intValue() + unit + ")";
    }
    return nameAndNum;
  }

  /**
   * 半角转换为全角
   */
  private String toDBC(String input) {
    char[] c = input.toCharArray();
    for (int i = 0; i < c.length; i++) {
      if (c[i] == 12288) {
        c[i] = (char) 32;
        continue;
      }
      if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
    }
    return new String(c);
  }

  /**
   * 科学计数法截取 后两位
   */
  private String getMultiUnit(Double num) {
    String multiNum = String.valueOf(num);
    int indexOf = multiNum.indexOf(".");
    String result;
    try {
      result = (indexOf == -1) ? String.valueOf(num) : multiNum.substring(0, indexOf + 3);
    } catch (Exception e) {
      e.printStackTrace();
      result = multiNum;
    }
    return result;
  }

  /**
   * 设置商品类型名
   */
  public void setCategoryName(String name) {
    if (name.isEmpty()) {
      mCategoryName = "未知类型";
    }
    mCategoryName = name;
  }

  /**
   * 设定选择状态
   */
  public void setSelected(int position) {
    if (mCurrentPos == position) return;
    mCurrentPos = position;
    if (mPrePos != -1) {
      notifyItemChanged(mPrePos);
    }
    notifyItemChanged(mCurrentPos);
    mPrePos = mCurrentPos;
  }

  /**
   * 单据条目
   */
  public interface OnCallBackClickListener {
    void onCallBackClick(int pos);
  }

  private OnCallBackClickListener onCallBackClickListener;

  public void setOnCallClickListener(OnCallBackClickListener onCallBackClickListener) {
    this.onCallBackClickListener = onCallBackClickListener;
  }
}
