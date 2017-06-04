package com.psi.easymanager.ui.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxExtraChargeDao;
import com.psi.easymanager.dao.PxOrderNumDao;
import com.psi.easymanager.dao.PxProductRemarksDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.ConfirmStartBillEvent;
import com.psi.easymanager.event.FindBillRefreshStatusEvent;
import com.psi.easymanager.event.StartBillEvent;
import com.psi.easymanager.module.PxExtraCharge;
import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxOrderNum;
import com.psi.easymanager.module.PxProductRemarks;
import com.psi.easymanager.module.PxPromotioInfo;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.PxTableExtraRel;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.ui.activity.MainActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.PromotioDetailsHelp;
import com.psi.easymanager.utils.ToastUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/5/27.
 */
public class StartBillFragment extends BaseFragment {
  //删除桌台信息
  @Bind(R.id.iv_delete_table) ImageView mIvDeleteTable;
  //桌台名
  @Bind(R.id.tv_table_name) TextView mTvTableName;
  //桌台人数
  @Bind(R.id.tv_people_number) TextView mTvPeopleNumber;
  //桌台rl
  @Bind(R.id.rl_table) RelativeLayout mRlTable;
  //人数rl
  @Bind(R.id.rl_people_num) RelativeLayout mRlPeopleNum;
  //分割线
  @Bind(R.id.divider_3) View mDivider3;
  //分割线
  @Bind(R.id.divider_4) View mDivider4;
  //桌台rb
  @Bind(R.id.rb_table) RadioButton mRbTable;
  //零售rb
  @Bind(R.id.rb_retail) RadioButton mRbRetail;
  //自定义备注
  @Bind(R.id.et_custom_remark) EditText mEtCustomRemark;
  //促销计划
  @Bind(R.id.tv_promotio) TextView mTvPromotioInfo;

  //MainActivity
  private MainActivity mAct;
  //Fragment管理器
  private FragmentManager mFm;

  //当前订单
  private PxOrderInfo mOrderInfo;
  //当前桌台
  private PxTableInfo mTableInfo;

  //订单类型,默认为桌位单
  private String mOrderType = ORDER_TYPE_TABLE;
  //桌位单
  private static final String ORDER_TYPE_TABLE = "1";
  //零售单
  private static final String ORDER_TYPE_RETAIL = "2";

  //找单
  private Fragment mFindBillFragment;
  //客单
  private Fragment mCashBillFragment;
  //菜单
  private Fragment mCashMenuFragment;

  //订单日期用 sdf
  private SimpleDateFormat mSdfDate = new SimpleDateFormat("yyyyMMdd");
  //序列号用 sdf
  private SimpleDateFormat mSdfOrderReq = new SimpleDateFormat("yyyyMMddHHmmss");

  //促销计划
  private PxPromotioInfo mPromotioInfo;

  public static StartBillFragment newInstance(String param) {
    Bundle bundle = new Bundle();
    bundle.putString("param", param);
    StartBillFragment fragment = new StartBillFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (MainActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    if (savedInstanceState != null) {
      mFindBillFragment = mFm.findFragmentByTag(Constants.FIND_BILL_TAG);
      mCashBillFragment = mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
      mCashMenuFragment = mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_start_bill, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(StartBillEvent.class);
  }

  /**
   * 取消
   */
  @OnClick(R.id.ibtn_cancel) public void cancel() {
    showBillMenu();
  }

  /**
   * 确认
   */
  @OnClick(R.id.ibtn_confirm) public void confirm(ImageButton iBtn) {
    //防止双击事件
    iBtn.setEnabled(false);
    if (mOrderType.equals(ORDER_TYPE_TABLE)) {//桌位单
      //if (mTableInfo != null && mTableInfo.getStatus().equals(PxTableInfo.STATUS_OCCUPIED)) {
      //  ToastUtils.showShort(mAct, "该桌台已占用");
      //  return;
      //}
      //开启对话框
      if (mTableInfo != null && mOrderInfo != null) {
        //开启蒙层
        mAct.isShowProgress(true);
        //处理桌台订单
        startTableBill();
        showBillMenu();
        //关闭蒙层
        mAct.isShowProgress(false);
      } else {
        ToastUtils.showShort(mAct, "请选择桌台");
      }
    } else if (mOrderType.equals(ORDER_TYPE_RETAIL)) {//零售单
      //开启蒙层
      mAct.isShowProgress(true);
      //处理零售单
      startRetailBill();
      showBillMenu();
      //关闭蒙层
      mAct.isShowProgress(false);
    }
    iBtn.setEnabled(true);
  }

  /**
   * 开单 桌位单
   */
  private void startTableBill() {
    if (mOrderInfo.getActualPeopleNumber() == null || mOrderInfo.getActualPeopleNumber() == 0) {
      ToastUtils.showShort(mAct, "请选择人数");
      return;
    }
    App app = (App) App.getContext();
    if (app == null) return;
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(app, "请重启APP!");
      return;
    }
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      // 开始时间
      mOrderInfo.setStartTime(new Date());
      //未完成
      mOrderInfo.setStatus(PxOrderInfo.STATUS_UNFINISH);
      //抹零金额
      mOrderInfo.setTailMoney((double) 0);
      //优惠金额
      mOrderInfo.setDiscountPrice((double) 0);
      //支付类优惠
      mOrderInfo.setPayPrivilege((double) 0);
      //是否刷卡
      mOrderInfo.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
      //用户
      mOrderInfo.setDbUser(user);
      DaoServiceUtil.getOrderInfoService().save(mOrderInfo);

      //桌台状态
      mTableInfo.setStatus(PxTableInfo.STATUS_OCCUPIED);
      DaoServiceUtil.getTableInfoService().saveOrUpdate(mTableInfo);

      //实收
      mOrderInfo.setRealPrice((double) 0);
      //应收
      mOrderInfo.setAccountReceivable((double) 0);
      //总的找零
      mOrderInfo.setTotalChange((double) 0);
      //订单类型
      mOrderInfo.setOrderInfoType(PxOrderInfo.ORDER_INFO_TYPE_TABLE);
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mOrderInfo);
      //建立桌台和订单连接
      TableOrderRel tableOrderRel = new TableOrderRel();
      tableOrderRel.setDbOrder(mOrderInfo);
      tableOrderRel.setDbTable(mTableInfo);
      DaoServiceUtil.getTableOrderRelService().saveOrUpdate(tableOrderRel);
      //查询可用的附加费引用关系
      QueryBuilder<PxTableExtraRel> extraRelQb = DaoServiceUtil.getTableExtraRelService()
          .queryBuilder()
          .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
          .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(mTableInfo.getId()));
      Join<PxTableExtraRel, PxExtraCharge> extraJoin =
          extraRelQb.join(PxTableExtraRelDao.Properties.PxExtraChargeId, PxExtraCharge.class);
      extraJoin.where(PxExtraChargeDao.Properties.DelFlag.eq("0"));
      PxTableExtraRel rel = extraRelQb.unique();
      //如果不为空，表示有可用的附加费
      if (rel != null) {
        //附加费信息
        PxExtraCharge extraCharge = rel.getDbExtraCharge();
        if (PxExtraCharge.ENABLE_TRUE.equals(extraCharge.getServiceStatus())) {
          //新建附加费详情
          PxExtraDetails pxExtraDetails = new PxExtraDetails();
          //开始时间
          pxExtraDetails.setStartTime(new Date());
          //价格
          pxExtraDetails.setPrice((double) 0);
          //订单
          pxExtraDetails.setDbOrder(mOrderInfo);
          //附加费名
          pxExtraDetails.setExtraName(extraCharge.getName());
          //桌台名
          pxExtraDetails.setTableName(mTableInfo.getName());
          //已付款
          pxExtraDetails.setPayPrice((double) 0);
          //已打印
          pxExtraDetails.setIsPrinted(false);
          //储存
          DaoServiceUtil.getExtraDetailsService().save(pxExtraDetails);
          //订单更新当前附加费详情
          mOrderInfo.setDbCurrentExtra(pxExtraDetails);
        }
      }
      //订单号
      PxOrderNum orderNum = DaoServiceUtil.getOrderNumService()
          .queryBuilder()
          .where(PxOrderNumDao.Properties.Date.eq(mSdfDate.format(new Date())))
          .unique();
      if (orderNum == null) {
        PxOrderNum pxOrderNum = new PxOrderNum();
        pxOrderNum.setDate(mSdfDate.format(new Date()));
        pxOrderNum.setNum(1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(pxOrderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(pxOrderNum.getNum(), user);
        mOrderInfo.setOrderNo(orderReqNum);
        mOrderInfo.setOrderReqNo(orderReqNum);
      } else {
        orderNum.setNum(orderNum.getNum() + 1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(orderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(orderNum.getNum(), user);
        mOrderInfo.setOrderNo(orderReqNum);
        mOrderInfo.setOrderReqNo(orderReqNum);
      }

      //备注合并
      String remarks = mEtCustomRemark.getText().toString().trim();
      mOrderInfo.setRemarks(remarks);

      //促销计划
      if (mPromotioInfo != null) {
        mOrderInfo.setDbPromotioInfo(mPromotioInfo);
      }
      //更新订单
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mOrderInfo);
      //由客单页面接收
      EventBus.getDefault().post(new ConfirmStartBillEvent().setOrderInfo(mOrderInfo));
      //找单页面接收
      EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
      db.setTransactionSuccessful();
    } catch (Exception e) {

    } finally {
      db.endTransaction();
    }
  }

  /**
   * 生成reqNum
   */
  private String createOrderReqNum(int orderNum, User user) {
    //公司编码
    int companyHashCode = user.getCompanyCode().hashCode();
    //公司编码求绝对值
    int absHashCode = Math.abs(Integer.valueOf(companyHashCode));
    //公司编码绝对值的String表示
    String strHashCode = String.valueOf(absHashCode);
    //当前日期
    String strDate = mSdfOrderReq.format(new Date());
    //当前序号
    String strSer = String.format("%06d", orderNum);
    String strOrderNum =
        new StringBuilder().append(strHashCode).append(strDate).append(strSer).toString();
    return strOrderNum;
  }

  /**
   * 开单 零售单
   */
  private void startRetailBill() {
    App app = (App) App.getContext();
    if (app == null) return;
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(app, "请重启APP!");
      return;
    }

    //订单号
    PxOrderNum orderNum = DaoServiceUtil.getOrderNumService()
        .queryBuilder()
        .where(PxOrderNumDao.Properties.Date.eq(mSdfDate.format(new Date())))
        .unique();

    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      mOrderInfo = new PxOrderInfo();
      //type
      mOrderInfo.setType(PxOrderInfo.TYPE_DEFAULT);
      //reverse
      mOrderInfo.setIsReserveOrder(PxOrderInfo.IS_REVERSE_ORDER_FALSE);
      //是否锁定状态 默认不锁定
      mOrderInfo.setIsLock(false);
      //开始时间
      mOrderInfo.setStartTime(new Date());
      //未完成
      mOrderInfo.setStatus(PxOrderInfo.STATUS_UNFINISH);
      //抹零
      mOrderInfo.setTailMoney((double) 0);
      //优惠金额
      mOrderInfo.setDiscountPrice((double) 0);
      //支付类优惠
      mOrderInfo.setPayPrivilege((double) 0);
      //是否刷卡
      mOrderInfo.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
      //用户
      mOrderInfo.setDbUser(user);
      //实收
      mOrderInfo.setRealPrice((double) 0);
      //应收
      mOrderInfo.setAccountReceivable((double) 0);
      //总的找零
      mOrderInfo.setTotalChange((double) 0);
      //订单类型
      mOrderInfo.setOrderInfoType(PxOrderInfo.ORDER_INFO_TYPE_RETAIL);
      if (orderNum == null) {
        PxOrderNum pxOrderNum = new PxOrderNum();
        pxOrderNum.setDate(mSdfDate.format(new Date()));
        pxOrderNum.setNum(1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(pxOrderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(pxOrderNum.getNum(), user);
        mOrderInfo.setOrderNo(orderReqNum);
        mOrderInfo.setOrderReqNo(orderReqNum);
      } else {
        orderNum.setNum(orderNum.getNum() + 1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(orderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(orderNum.getNum(), user);
        mOrderInfo.setOrderNo(orderReqNum);
        mOrderInfo.setOrderReqNo(orderReqNum);
      }
      //备注合并
      String remarks = mEtCustomRemark.getText().toString().trim();
      mOrderInfo.setRemarks(remarks);
      //促销计划
      if (mPromotioInfo != null) {
        mOrderInfo.setDbPromotioInfo(mPromotioInfo);
      }
      DaoServiceUtil.getOrderInfoService().save(mOrderInfo);
      //由客单页面接收
      EventBus.getDefault().post(new ConfirmStartBillEvent().setOrderInfo(mOrderInfo));
      //找单页面更新
      EventBus.getDefault().post(new FindBillRefreshStatusEvent());
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 设置人数
   */
  //@formatter:off
  @OnClick(R.id.rl_people_num) public void setPeopleNumber() {
    if (mOrderType == ORDER_TYPE_TABLE && mTableInfo == null){
      ToastUtils.showShort(mAct,"请选择桌台");
      return;
    }
    new MaterialDialog.Builder(mAct).title("警告")
        .content("输入就餐人数")
        .inputType(InputType.TYPE_CLASS_NUMBER)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .input("人数", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence input) {
            if (input.toString().trim() !=null && !input.toString().toString().equals("") && input.toString().length() > 4){
              ToastUtils.showShort(mAct,"输入过长!");
              dialog.getInputEditText().setText("");
              return;
            }
            if (input.toString() == null || input.toString().trim().equals("") || Integer.valueOf(input.toString().trim()).intValue() <= 0) {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            } else {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            mTvPeopleNumber.setText(dialog.getInputEditText().getText().toString());
            mOrderInfo.setActualPeopleNumber(Integer.valueOf(dialog.getInputEditText().getText().toString().trim()));
          }
        })
        .show();
  }


  /**
   * 切换桌台单 / 零售单
   */
  //@formatter:off
  @OnCheckedChanged({ R.id.rb_table, R.id.rb_retail }) public void setOrderType(RadioButton rb) {
    if (rb.isChecked()) {
      FragmentTransaction transaction = mFm.beginTransaction();
      switch (rb.getId()) {
        case R.id.rb_table://桌位单
          //切换订单类型
          mOrderType = ORDER_TYPE_TABLE;
          //显示桌位 人数信息
          mRlTable.setVisibility(View.VISIBLE);
          mRlPeopleNum.setVisibility(View.VISIBLE);
          mDivider3.setVisibility(View.VISIBLE);
          mDivider4.setVisibility(View.VISIBLE);
          //隐藏右侧
          hideRightFragment(transaction);
          //显示找单
          showFindBillFragment(transaction);
          break;
        case R.id.rb_retail://零售单
          //切换订单类型
          mOrderType = ORDER_TYPE_RETAIL;
          //隐藏桌位 人数信息
          mRlTable.setVisibility(View.GONE);
          mRlPeopleNum.setVisibility(View.GONE);
          mDivider3.setVisibility(View.GONE);
          mDivider4.setVisibility(View.GONE);
          //隐藏右侧
          hideRightFragment(transaction);
          break;
      }
      transaction.commit();
    }
  }

  /**
   * 清空桌台信息
   */
  @OnClick(R.id.iv_delete_table) public void deleteTableInfo() {
    mTableInfo = null;
    mTvTableName.setText("");
    mTvPeopleNumber.setText("");
    mIvDeleteTable.setVisibility(View.GONE);
    mEtCustomRemark.setText("");
  }

  /**
   * 显示找单
   */
  private void showFindBillFragment(FragmentTransaction transaction) {
    mFindBillFragment = mFm.findFragmentByTag(Constants.FIND_BILL_TAG);
    if (mFindBillFragment == null) {
      mFindBillFragment = FindBillFragment.newInstance(true);
      transaction.add(R.id.cash_content_right, mFindBillFragment, Constants.FIND_BILL_TAG);
    } else {
      transaction.show(mFindBillFragment);
    }
  }

  /**
   * 显示菜单页面
   */
  private void showBillMenu() {
    //显示MainActivity3个悬浮按钮
    mAct.mCashFabs.setVisibility(View.VISIBLE);
    //更新FindBill标签
    FindBillFragment.mCurrentLeftFragment = FindBillFragment.BILL;
    //更新页面
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    mCashBillFragment = mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    mCashMenuFragment = mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    //客单
    if (mCashBillFragment  == null) {
      mCashBillFragment  = CashBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mCashBillFragment , Constants.CASH_BILL_TAG);
    } else {
      transaction.show(mCashBillFragment );
    }
    //菜单
    if (mCashMenuFragment == null) {
      mCashMenuFragment = CashMenuFragment.newInstance("param");
      transaction.add(R.id.cash_content_right,mCashMenuFragment, Constants.CASH_MENU_TAG);
    } else {
      transaction.show(mCashMenuFragment);
    }
    transaction.commit();
  }

  /**
   * 隐藏所有Fragment
   */
  private void hideAllFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments == null) return;
    for (Fragment fragment : allAddedFragments) {
      transaction.hide(fragment);
    }
  }

  /**
   * 隐藏右侧fragment
   */
  private void hideRightFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments == null) return;
    for (Fragment fragment : allAddedFragments) {
      if (!(fragment instanceof StartBillFragment)) {
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 重置显示信息
   */
  public void resetData() {
    //滞空Order和Table
    mTableInfo = null;
    mOrderInfo = null;
    //滞空显示
    mTvTableName.setText("");
    mTvPeopleNumber.setText("");
    mIvDeleteTable.setVisibility(View.GONE);
    mRbTable.setChecked(true);
    mEtCustomRemark.setText("");
  }

  /**
   * 退出
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
  }

  /**
   * 接收FindBill传来的桌台信息，开单
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void getTableInfoFromFindBill(StartBillEvent event) {
    //重置数据
    resetData();
    //获取桌台
    mTableInfo = event.getTableInfo();
    //订单信息
    mOrderInfo = new PxOrderInfo();
    //type
    mOrderInfo.setType(PxOrderInfo.TYPE_DEFAULT);
    //reverse
    mOrderInfo.setIsReserveOrder(PxOrderInfo.IS_REVERSE_ORDER_FALSE);
    //是否锁定状态 默认不锁定
    mOrderInfo.setIsLock(false);
    //设置默认人数
    mOrderInfo.setActualPeopleNumber(mTableInfo.getPeopleNum());
    mTvPeopleNumber.setText(mTableInfo.getPeopleNum() + "");
    //显示桌台名称
    String type = mTableInfo.getType();
    String tableType = null;

    if (type != null) {
      PxTableArea area = DaoServiceUtil.getTableAreaService()
          .queryBuilder()
          .where(PxTableAreaDao.Properties.Type.eq(type))
          .unique();
      tableType = area == null ? "大厅" : area.getName();
    }else{
      tableType = "大厅";
    }
    mTvTableName.setText("(" + tableType + ")" + mTableInfo.getName());
    //显示删除桌台按钮
    mIvDeleteTable.setVisibility(View.VISIBLE);
  }


  /**
   * 设置备注
   */
  //@formatter:on
  @OnClick(R.id.rl_remarks) public void setRemarks() {
    //备注验证
    final List<PxProductRemarks> list = DaoServiceUtil.getProdRemarksService()
        .queryBuilder()
        .where(PxProductRemarksDao.Properties.DelFlag.eq("0"))
        .list();
    if (list == null || list.size() == 0) {
      ToastUtils.showShort(mAct, "请在后台添加备注信息");
      return;
    }
    //对话框
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("备注")
        .customView(R.layout.layout_dialog_choose_remarks, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .build();
    final TextView tvRemarks = (TextView) dialog.getCustomView().findViewById(R.id.tv_remarks);
    //Tags
    final TagFlowLayout remarkTags =
        (TagFlowLayout) dialog.getCustomView().findViewById(R.id.tags_remarks);
    //TagAdapter
    TagAdapter tagRemarksAdapter = new TagAdapter<PxProductRemarks>(list) {
      @Override public View getView(FlowLayout parent, int position, PxProductRemarks remarks) {
        TextView tv = (TextView) LayoutInflater.from(mAct)
            .inflate(R.layout.item_tags_remark, remarkTags, false);
        tv.setText(remarks.getRemarks());
        return tv;
      }
    };
    remarkTags.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
      @Override public boolean onTagClick(View view, int position, FlowLayout parent) {
        PxProductRemarks remarks = list.get(position);
        if (tvRemarks.getText() != null
            && tvRemarks.getText().toString().trim().equals("") == false) {
          tvRemarks.append(" ," + remarks.getRemarks());
        } else {
          tvRemarks.append(remarks.getRemarks());
        }
        return false;
      }
    });
    remarkTags.setAdapter(tagRemarksAdapter);
    //确定按钮
    MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);

    //显示
    dialog.show();

    //确认点击
    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //
        mEtCustomRemark.append(" " + tvRemarks.getText().toString().trim());
        //关闭dialog
        dialog.dismiss();
      }
    });
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (!hidden) {
      mPromotioInfo = null;
      mTvPromotioInfo.setVisibility(View.INVISIBLE);
    }
  }

  //促销计划
  //@formatter:on
  @OnClick(R.id.rl_promotio_info) public void selectPromotioDetails() {
    //获取有效促销计划
    final List<PxPromotioInfo> promotioInfoList = PromotioDetailsHelp.getPromotioInfoList();
    if (promotioInfoList.isEmpty()) {
      ToastUtils.showShort(null, "请在后台添加促销计划!");
      return;
    }
    String[] items = new String[promotioInfoList.size()];
    for (int i = 0; i < promotioInfoList.size(); i++) {
      PxPromotioInfo promotioInfo = promotioInfoList.get(i);
      items[i] = promotioInfo.getName();
    }
    final MaterialDialog selectPromotioDialog = DialogUtils.showListDialog(mAct, "促销计划", items);
    MDButton posBtn = selectPromotioDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = selectPromotioDialog.getActionButton(DialogAction.NEGATIVE);
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(selectPromotioDialog);
      }
    });
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int selectedIndex = selectPromotioDialog.getSelectedIndex();
        mPromotioInfo = promotioInfoList.get(selectedIndex);
        mTvPromotioInfo.setText(mPromotioInfo.getName());
        mTvPromotioInfo.setVisibility(View.VISIBLE);
        DialogUtils.dismissDialog(selectPromotioDialog);
      }
    });
  }

  //删除促销计划
  @OnClick(R.id.tv_promotio) public void deletePromotioDetails() {
    mPromotioInfo = null;
    mTvPromotioInfo.setVisibility(View.INVISIBLE);
  }
}
