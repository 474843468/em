package com.psi.easymanager.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.CashMenuProductAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.PxProductMethodRefDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.dao.dbUtil.DbCore;
import com.psi.easymanager.event.CashBillAddItemEvent;
import com.psi.easymanager.event.CashBillOrderInfoEvent;
import com.psi.easymanager.event.UpdateProdInfoListStatusEvent;
import com.psi.easymanager.module.ProdInnerOrder;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductMethodRef;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.ui.activity.AddComboActivity;
import com.psi.easymanager.ui.activity.AddProdActivity;
import com.psi.easymanager.ui.activity.MainActivity;
import com.psi.easymanager.utils.PromotioDetailsHelp;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.ErrorGridLayoutManager;
import com.psi.easymanager.widget.ProdInnerOrderDiffCallback;
import com.psi.easymanager.widget.RecyclerViewSpaceItemDecoration;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by psi on 2016/5/16.
 * 收银点菜-模糊查询
 */
//@formatter:off
public class CashMenuFuzzyQueryFragment extends BaseFragment implements CashMenuProductAdapter.OnProductClickListener, TextWatcher {
  @Bind(R.id.tv_fuzzy_query_name) TextView tvFuzzyQueryName;//搜索字眼
  @Bind(R.id.rcv_menu) RecyclerView mRcvMenu;//所有菜单Rcv
  @Bind(R.id.tv_fuzzy_query_page_status) TextView tvPageStatus;//当前页码

  private static final int ADD_DETAILS = 0;
  private MainActivity mAct;
  private FragmentManager mFm;

  //存储搜索字段
  private StringBuilder searchName = new StringBuilder();
  //所有商品
  private List<PxProductInfo> mProductInfoList;
  //所有商品适配器
  private CashMenuProductAdapter mProductAdapter;
  //查询字样
  private String likeName = "";
  //当前所属订单
  private PxOrderInfo mOrderInfo;
  //菜单主页面
  private CashMenuFragment mCashMenuFragment;
  //每页数量
  private static final int PAGE_NUM = 8;
  //当前页码
  private int mCurrentPage = 1;
  //总页码
  private int mTotalPage;
  //异步添加商品Session
  private AsyncSession mProdAsyncSession;

  private UIHandler mUiHandler;
  public static CashMenuFuzzyQueryFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    CashMenuFuzzyQueryFragment fragment = new CashMenuFuzzyQueryFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (MainActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    mUiHandler = new UIHandler();
    //初始化AsyncSession
    mProdAsyncSession = DbCore.getDaoSession().startAsyncSession();
    mCashMenuFragment = (CashMenuFragment) mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cash_menu_fuzzy_query, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(CashBillOrderInfoEvent.class);
    initView();
  }

  private void initView() {
    GridLayoutManager managerDish = new ErrorGridLayoutManager(mAct, 4, GridLayoutManager.VERTICAL, false);
    mProductInfoList = new ArrayList<PxProductInfo>();
    mProductAdapter = new CashMenuProductAdapter(mAct, new ArrayList<ProdInnerOrder>());
    mProductAdapter.setOnProductClickListener(this);
    mRcvMenu.setHasFixedSize(true);
    mRcvMenu.setLayoutManager(managerDish);
    mRcvMenu.setAdapter(mProductAdapter);
    int prodSpaceWidth = getResources().getDimensionPixelSize(R.dimen.menu_rcv_item_horizontal_space_width);
    int prodSpaceHeight = getResources().getDimensionPixelSize(R.dimen.menu_rcv_item_vertical_space_height);
    mRcvMenu.addItemDecoration(new RecyclerViewSpaceItemDecoration(prodSpaceWidth, prodSpaceHeight));
    //监听
    tvFuzzyQueryName.addTextChangedListener(this);
  }

  /**
   * 上一页
   */
  @OnClick(R.id.tv_last_page) public void lastPage() {
    if (null == likeName || likeName.trim().toString().equals("")){
      ToastUtils.showShort(App.getContext(),"请输入搜索条件");
      return;
    }
    //查询数量
    queryNum();
    if (mCurrentPage > 1) {
      mCurrentPage -= 1;
      //查询数据
      queryData();
    }
  }

  /**
   * 下一页
   */
  @OnClick(R.id.tv_next_page) public void nextPage() {
    if (null == likeName || likeName.trim().toString().equals("")){
      ToastUtils.showShort(App.getContext(),"请输入搜索条件");
      return;
    }
    //查询总量
    queryNum();
    if (mCurrentPage < mTotalPage) {
      mCurrentPage += 1;
      //查询数据
      queryData();
    }
  }

  /**
   * 获取全部数量的方法
   */
  private void queryNum() {
    long count = DaoServiceUtil.getProductInfoDao()
        .queryBuilder()
        .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
        .where(PxProductInfoDao.Properties.Py.like("%" + likeName + "%"))
        .count();
    mTotalPage = (int) Math.ceil(count / (double) PAGE_NUM);
  }

  /**
   * 键盘点击
   */
  @OnClick({
      R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
      R.id.btn_8, R.id.btn_9, R.id.btn_0, R.id.btn_123, R.id.btn_a, R.id.btn_b, R.id.btn_c,
      R.id.btn_d, R.id.btn_e, R.id.btn_f, R.id.btn_g, R.id.btn_h, R.id.btn_i, R.id.btn_j,
      R.id.btn_k, R.id.btn_l, R.id.btn_m, R.id.btn_n, R.id.btn_o, R.id.btn_p, R.id.btn_q,
      R.id.btn_r, R.id.btn_s, R.id.btn_t, R.id.btn_u, R.id.btn_v, R.id.btn_w, R.id.btn_x,
      R.id.btn_y, R.id.btn_z
  }) public void keyboardClick(Button button) {
    mCurrentPage = 1;
    searchName.append(button.getText().toString().trim());
    tvFuzzyQueryName.setText(searchName.toString().trim());
  }

  /**
   * 返回
   */
  @OnClick(R.id.btn_back) public void buttonBack(Button button) {
    if (searchName.length() > 1) {
      mCurrentPage = 1;
      searchName.delete(searchName.length() - 1, searchName.length());
      tvFuzzyQueryName.setText(searchName.toString().trim());
    } else {
      clear();
    }
  }

  /**
   * 清空
   */
  @OnClick(R.id.tv_cash_menu_fuzzy_cancel) public void clear() {
    mCurrentPage = 1;
    searchName = new StringBuilder();
    tvFuzzyQueryName.setText("");
  }


  @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override public void afterTextChanged(Editable editable) {
    //每次变化时先清空数据
    likeName = tvFuzzyQueryName.getText().toString();
    if (likeName == null || likeName.trim().toString().equals("")){
      mProductAdapter.clearData();
      tvPageStatus.setText(0 + "/" +0);
      return;
    }
    //查询数量
    queryNum();
    //查询数据
    queryData();
  }

  /**
   * 获取模糊查询数据
   */
  private void queryData() {
    //直接获取菜单数据库中的数据
    PxProductInfoDao productInfoDao = DaoServiceUtil.getProductInfoDao();
    //获取所有PxProductInfo
    QueryBuilder<PxProductInfo> queryBuilder = productInfoDao.queryBuilder()
        .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
        .where(PxProductInfoDao.Properties.Py.like("%" + likeName + "%"))
        .whereOr(PxProductInfoDao.Properties.Shelf.isNull(), PxProductInfoDao.Properties.Shelf.eq(PxProductInfo.SHELF_PUT_AWAY))
        .limit(PAGE_NUM)
        .offset(PAGE_NUM * (mCurrentPage - 1));
    mProductInfoList = queryBuilder.list();
   //更新adapter
    List<ProdInnerOrder> prodInnerOrderList = new ArrayList<>();
    ProdInnerOrder prodInnerOrder = null;
    for (PxProductInfo productInfo : mProductInfoList) {
      prodInnerOrder = new ProdInnerOrder(productInfo,mOrderInfo);
      prodInnerOrderList.add(prodInnerOrder);
    }
    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProdInnerOrderDiffCallback(mProductAdapter.getProductInnerOrderList(),prodInnerOrderList),false);
    mProductAdapter.setData(diffResult,prodInnerOrderList);

    //更新页码
    if (mTotalPage < 1){
      tvPageStatus.setText(0 + "/" + 0);
    } else {
      tvPageStatus.setText(mCurrentPage + "/" + mTotalPage);
    }
  }

  /**
   * 接受CashMenuFragment发的PxOrderInfo信息
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void getOrderInfoEvent(
      CashBillOrderInfoEvent event) {
    mOrderInfo = event.getOrderInfo();
  }

  /**
   * 关闭
   */
  @OnClick(R.id.tv_close) public void closeFuzzyQuery(View view) {
    //回显CashMenuFragment
    FragmentTransaction transaction = mFm.beginTransaction();
    hideExcludeCashBill(transaction);
    if (mCashMenuFragment == null) {
      mCashMenuFragment = CashMenuFragment.newInstance("param");
      transaction.add(R.id.cash_content_right, mCashMenuFragment, Constants.CASH_MENU_TAG);
    } else {
      transaction.show(mCashMenuFragment);
    }
    transaction.commit();
    //回显MainActivity3个悬浮按钮
    mAct.mCashFabs.setVisibility(View.VISIBLE);
  }

  /**
   * 隐藏除了菜单Fragment
   */
  private void hideExcludeCashBill(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if ((fragment instanceof CashBillFragment)) continue;
        transaction.hide(fragment);
      }
    }
  }

  @Override public void onProductClick(int pos) {
   if (mOrderInfo == null){
      ToastUtils.showShort(mAct,"请先开单");
      return;
    }
    //当前商品
    final PxProductInfo currentProdInfo = mProductInfoList.get(pos);
    //是否沽清
    if (currentProdInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)){
      ToastUtils.showShort(mAct,"该商品已沽清，不能添加");
      return;
    }
    //是否为套餐
    if (currentProdInfo.getType()!=null && currentProdInfo.getType().equals(PxProductInfo.TYPE_COMBO)){
      Intent intent = new Intent(mAct, AddComboActivity.class);
      intent.putExtra(AddComboActivity.TYPE,AddComboActivity.TYPE_ADD);
      intent.putExtra(AddComboActivity.COMBO,currentProdInfo);
      intent.putExtra(AddComboActivity.ORDER,mOrderInfo);
      startActivity(intent);
      return;
    }
    //查询可用的规格引用关系
    QueryBuilder<PxProductFormatRel> formatRelQb = DaoServiceUtil.getProductFormatRelService()
        .queryBuilder()
        .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
        .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(currentProdInfo.getId()));
    Join<PxProductFormatRel, PxFormatInfo> formatJoin = formatRelQb.join(PxProductFormatRelDao.Properties.PxFormatInfoId, PxFormatInfo.class);
    formatJoin.where(PxFormatInfoDao.Properties.DelFlag.eq("0"));
    List<PxProductFormatRel> formatRelList = formatRelQb.list();
    //查询可用的做法引用关系
    QueryBuilder<PxProductMethodRef> methodRelQb = DaoServiceUtil.getProductMethodRelService()
        .queryBuilder()
        .where(PxProductMethodRefDao.Properties.DelFlag.eq("0"))
        .where(PxProductMethodRefDao.Properties.PxProductInfoId.eq(currentProdInfo.getId()));
    Join<PxProductMethodRef, PxMethodInfo> methodJoin = methodRelQb.join(PxProductMethodRefDao.Properties.PxMethodInfoId, PxMethodInfo.class);
    methodJoin.where(PxMethodInfoDao.Properties.DelFlag.eq("0"));
    List<PxProductMethodRef> methodRelList = methodRelQb.list();

    //规格可用
    boolean formatCanUse = (formatRelList != null && formatRelList.size() != 0);
    //做法可用
    boolean methodCanUse = (methodRelList != null && methodRelList.size() != 0);
    //有双单位
    boolean hasTwoUnit = currentProdInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE);

    if (!hasTwoUnit && !formatCanUse && !methodCanUse){
      //开启蒙层
      mAct.isShowProgress(true,currentProdInfo.getName());
      //异步执行
      mProdAsyncSession.runInTx(new Runnable() {
        @Override public void run() {
          //促销计划
          PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(mOrderInfo.getDbPromotioById(),null,currentProdInfo);
          double unitPrice = validPromotioDetails == null ? currentProdInfo.getPrice() : validPromotioDetails.getPromotionalPrice();
          double unitVipPrice = validPromotioDetails == null ? currentProdInfo.getVipPrice() : validPromotioDetails.getPromotionalPrice();
          //新建Details
          PxOrderDetails details = new PxOrderDetails();
          //默认不清空
          details.setIsClear(false);
          //数量
          details.setNum((double) 1);
          //多单位数量
          details.setMultipleUnitNumber(0.0);
          //订单
          details.setDbOrder(mOrderInfo);
          //折扣率
          details.setCurrentDiscRate();
          //单价(是否使用促销价)
          details.setUnitPrice(unitPrice);
          //会员单价(是否使用促销价)
          details.setUnitVipPrice(unitVipPrice);
          //价格
          details.setPrice(details.getUnitPrice() * details.getNum());
          //会员价格
          details.setVipPrice(details.getUnitVipPrice() * details.getNum());
          //商品
          details.setDbProduct(currentProdInfo);
          //下单状态
          details.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
          //商品状态
          details.setStatus(PxOrderDetails.STATUS_ORIDINARY);
          //已上菜
          details.setIsServing(false);
          //是否为套餐Details
          details.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
          //是否是套餐内Details
          details.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
          //赠品
          details.setIsGift(PxOrderDetails.GIFT_FALSE);
          //备注
          details.setRemarks("");
          //objId
          details.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
          //储存
          DaoServiceUtil.getOrderDetailsService().save(details);
          //剩余数量
          if (currentProdInfo.getOverPlus() != null && currentProdInfo.getOverPlus() != 0) {
            currentProdInfo.setOverPlus(currentProdInfo.getOverPlus() - 1);
            if (currentProdInfo.getOverPlus() == 0) {
              currentProdInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
            }
            DaoServiceUtil.getProductInfoService().saveOrUpdate(currentProdInfo);
          }
           //handler
          sendHandlerMsg(ADD_DETAILS,details);
        }
      });
    }
    //跳转至添加页面
    else {
      Intent intent = new Intent(mAct, AddProdActivity.class);
      intent.putExtra(AddProdActivity.PROD, currentProdInfo);
      intent.putExtra(AddProdActivity.ORDER, mOrderInfo);
      startActivity(intent);
    }
  }

  @Override public void onProductLongClick(PxProductInfo productInfo) {

  }


  /**
   * send handler msg
   */
  private void sendHandlerMsg(int what, Object obj) {
    Message msg = Message.obtain();
    msg.what = what;
    msg.obj = obj;
    mUiHandler.sendMessage(msg);
  }

  /**
   * UI Handler
   */
  private class UIHandler extends Handler {
    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case ADD_DETAILS:
          PxOrderDetails details = (PxOrderDetails) msg.obj;
          EventBus.getDefault().post(new CashBillAddItemEvent().setDetails(details));
          break;
      }
    }
  }

  /**
   * 刷新商品状态
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void updateProdInfoListStatus(UpdateProdInfoListStatusEvent event) {
    mCurrentPage = 1;
    searchName = new StringBuilder();
    tvFuzzyQueryName.setText("");
    tvPageStatus.setText("0/0");
    //mProductAdapter.setData(new ArrayList<PxProductInfo>());
    List<ProdInnerOrder> prodInnerOrderList = new ArrayList<>();
    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProdInnerOrderDiffCallback(mProductAdapter.getProductInnerOrderList(), prodInnerOrderList), false);
    mProductAdapter.setData(diffResult, prodInnerOrderList);
  }

  /**
   * 重置注入
   */
  @Override public void onDestroy() {
    super.onDestroy();
    if (mUiHandler != null) {
      mUiHandler.removeCallbacksAndMessages(null);
    }
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
  }

  /**
   * 更新商品点击数量
   */
  public void updateProdNum(PxOrderInfo orderInfo) {
   List<ProdInnerOrder> oldProdInnerOrderList = mProductAdapter.getProductInnerOrderList();
    List<ProdInnerOrder> newProdInnerOrderList = new ArrayList<>(oldProdInnerOrderList.size());
    ProdInnerOrder newProdInnerOrder = null;
    for (ProdInnerOrder oldProdInnerOrder : oldProdInnerOrderList) {
      newProdInnerOrder = new ProdInnerOrder(oldProdInnerOrder.getProductInfo(),orderInfo);
      newProdInnerOrderList.add(newProdInnerOrder);
    }
    DiffUtil.DiffResult result = DiffUtil.calculateDiff(new ProdInnerOrderDiffCallback(oldProdInnerOrderList, newProdInnerOrderList),false);
    //mProductAdapter.refreshProdNum(orderInfo);
    mProductAdapter.setData(result,newProdInnerOrderList);
  }
}
