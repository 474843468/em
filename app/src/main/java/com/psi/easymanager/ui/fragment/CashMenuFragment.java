package com.psi.easymanager.ui.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.CashMenuCategoryAdapter;
import com.psi.easymanager.adapter.CashMenuChildCateAdapter;
import com.psi.easymanager.adapter.CashMenuChildCateAdapter.OnChildCateClickListener;
import com.psi.easymanager.adapter.CashMenuProductAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxProductCategoryDao;
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
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductMethodRef;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.ui.activity.AddComboActivity;
import com.psi.easymanager.ui.activity.AddProdActivity;
import com.psi.easymanager.ui.activity.CustomProductActivity;
import com.psi.easymanager.ui.activity.MainActivity;
import com.psi.easymanager.ui.activity.ProdInfoActivity;
import com.psi.easymanager.utils.NumberFormatUtils;
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
 * Created by zjq on 2016/5/26.
 */
public class CashMenuFragment extends BaseFragment
    implements CashMenuCategoryAdapter.OnCateClickListener,
    CashMenuProductAdapter.OnProductClickListener, OnChildCateClickListener {

  private static final int ADD_DETAILS = 0;

  /**
   * 菜单界面
   */
  //菜单
  @Bind(R.id.rcv_menu) RecyclerView mRcvMenu;
  //菜单分类
  @Bind(R.id.rcv_cate) RecyclerView mRcvCate;
  //当前页面
  @Bind(R.id.tv_page_status) TextView tvPageStatus;
  //上页
  @Bind(R.id.tv_next_page) TextView mTvNextPage;
  //下页
  @Bind(R.id.tv_last_page) TextView mTvLastPage;

  //MainActivity
  private MainActivity mAct;
  //Fragment管理器
  private FragmentManager mFm;

  //分类list
  private List<PxProductCategory> mCategoryList;
  //分类adapter
  private CashMenuCategoryAdapter mCategoryAdapter;
  //商品adapter
  private CashMenuProductAdapter mProductAdapter;
  //当前页面的商品list
  private List<PxProductInfo> mCurrentProdList;
  //子分类list
  private List<PxProductCategory> mChildCateList;
  //子分类适配器
  private CashMenuChildCateAdapter mChildCateAdapter;
  //当前分类是否有子分类
  private boolean hasChildCateList;

  //当前页码
  private int mCurrentPage = 1;
  //总页码
  private int mTotalPage = 1;

  //当前OrderInfo
  private PxOrderInfo mCurrentOrderInfo;
  //收银菜单 模糊查询
  private Fragment mCashMenuFuzzyQueryFragment;
  //收银菜单 自定义商品
  private Fragment mCustomProductFragment;

  //限制数量
  private int mLimit = Constants.ONE_PAGE_MENU_NUM;
  //当前分类中的商品总数
  private long mProdListSize = 0;
  //当前分类
  private PxProductCategory mCurrentCate;
  //异步添加商品Session
  private AsyncSession mProdAsyncSession;
  private PxOrderDetails mDetails;
  private UIHandler mUiHandler;

  public static CashMenuFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    CashMenuFragment fragment = new CashMenuFragment();
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
    if (savedInstanceState != null) {
      mCashMenuFuzzyQueryFragment = mFm.findFragmentByTag(Constants.CASH_MENU_FUZZY_QUERY_TAG);
      mCustomProductFragment = mFm.findFragmentByTag(Constants.CUSTOM_PRODUCT_TAG);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cash_menu, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    //初始化Rcv
    initRcv();
    //初始化分类
    initCategory();

    EventBus.getDefault().register(this);
  }

  /**
   * 初始化Rcv
   */
  //@formatter:off
  private void initRcv() {
    //分类
    LinearLayoutManager managerCategory = new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false);
    mCategoryList = new ArrayList<PxProductCategory>();
    mCategoryAdapter = new CashMenuCategoryAdapter(mAct, mCategoryList);
    mCategoryAdapter.setOnCateClickListener(this);
    mRcvCate.setLayoutManager(managerCategory);
    mRcvCate.setHasFixedSize(true);
    mRcvCate.setAdapter(mCategoryAdapter);
    int cateSpaceWidth = getResources().getDimensionPixelSize(R.dimen.cate_rcv_item_horizontal_space_width);
    int cateSpaceHeight = getResources().getDimensionPixelSize(R.dimen.cate_rcv_item_vertical_space_height);
    mRcvCate.addItemDecoration(new RecyclerViewSpaceItemDecoration(cateSpaceWidth, cateSpaceHeight));
    //商品
    GridLayoutManager managerDish = new ErrorGridLayoutManager(mAct, 4, GridLayoutManager.VERTICAL, false);
    mCurrentProdList = new ArrayList<PxProductInfo>();
    mProductAdapter = new CashMenuProductAdapter(mAct, new ArrayList<ProdInnerOrder>());
    mProductAdapter.setOnProductClickListener(this);
    mRcvMenu.setHasFixedSize(true);
    mRcvMenu.setLayoutManager(managerDish);
    mRcvMenu.setAdapter(mProductAdapter);
    int prodSpaceWidth = getResources().getDimensionPixelSize(R.dimen.menu_rcv_item_horizontal_space_width);
    int prodSpaceHeight = getResources().getDimensionPixelSize(R.dimen.menu_rcv_item_vertical_space_height);
    mRcvMenu.addItemDecoration(new RecyclerViewSpaceItemDecoration(prodSpaceWidth, prodSpaceHeight));
    //子分类
    mChildCateList = new ArrayList<PxProductCategory>();
    mChildCateAdapter = new CashMenuChildCateAdapter(mAct,mChildCateList);
    mChildCateAdapter.setOnChildCateClickListener(this);
  }

  /**
   * 初始化分类
   */
  //@formatter:off
  private void initCategory() {
    //读取所有分类
    mCategoryList = DaoServiceUtil.getProductCategoryDao()
        .queryBuilder()
        .where(PxProductCategoryDao.Properties.DelFlag.eq("0"))
        .where(PxProductCategoryDao.Properties.ParentId.eq("0"))
        .whereOr(PxProductCategoryDao.Properties.Shelf.isNull(),PxProductCategoryDao.Properties.Shelf.eq(PxProductCategory.SHELF_PUT_AWAY))
        .orderAsc(PxProductCategoryDao.Properties.OrderNo)
        .list();
    mCategoryList = new ArrayList<>(mCategoryList);
    //如果为空 返回
    //if (mCategoryList == null || mCategoryList.size() == 0) return;
    //添加'全部'分类
    PxProductCategory categoryAll = new PxProductCategory();
    categoryAll.setName("全部");
    mCategoryList.add(0,categoryAll);
    mCategoryAdapter.setData(mCategoryList);
    //默认选中第一项
    mCategoryAdapter.setSelected(0, 0);
    mCurrentCate = mCategoryList.get(0);
    //获取该分类下的内容
    getContentByCate(mCurrentCate);
    //更新页面
    if (hasChildCateList) {
      mRcvMenu.setAdapter(mChildCateAdapter);
      mChildCateAdapter.setData(mChildCateList);
      //隐藏页面
      hidePageStatus();
    } else {
      //更新页码
      updatePageStatus(1);
      //更新rcv
      updateMenuRcv();
    }
  }

  /**
   * 获取该分类下的内容
   */
  private void getContentByCate(PxProductCategory category) {
    if ("全部".equals(category.getName())){
      //当前分类商品总数
      mProdListSize = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
          .whereOr(PxProductInfoDao.Properties.Shelf.isNull(),PxProductInfoDao.Properties.Shelf.eq(PxProductInfo.SHELF_PUT_AWAY))
          .count();
      //当前分类没有子分类
      hasChildCateList = false;
    } else {
      //获取该分类下的子分类
      List<PxProductCategory> childCateList = DaoServiceUtil.getProductCategoryService()
          .queryBuilder()
          .where(PxProductCategoryDao.Properties.ParentId.eq(category.getObjectId()))
          .where(PxProductCategoryDao.Properties.DelFlag.eq("0"))
          .whereOr(PxProductCategoryDao.Properties.Shelf.isNull(),PxProductCategoryDao.Properties.Shelf.eq(PxProductCategory.SHELF_PUT_AWAY))
          .list();
      //如果没有子分类,获取商品
      if (childCateList == null || childCateList.size() == 0) {
        //当前分类商品总数
        mProdListSize = DaoServiceUtil.getProductInfoService()
            .queryBuilder()
            .where(PxProductInfoDao.Properties.PxProductCategoryId.eq(category.getId()))
            .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
            .whereOr(PxProductInfoDao.Properties.Shelf.isNull(),PxProductInfoDao.Properties.Shelf.eq(PxProductInfo.SHELF_PUT_AWAY))
            .count();
        //当前分类没有子分类
        hasChildCateList = false;
      }
      //如果有子分类，获取子分类
      else {
        mChildCateList = childCateList;
        //当前分类有子分类
        hasChildCateList = true;
      }
    }
  }

  /**
   * 更新页码
   */
  private void updatePageStatus(int currentPage) {
    tvPageStatus.setVisibility(View.VISIBLE);
    mTvLastPage.setVisibility(View.VISIBLE);
    mTvNextPage.setVisibility(View.VISIBLE);
    if (mProdListSize != 0) {
      mTotalPage = (int) Math.ceil(mProdListSize / (double) mLimit);
      tvPageStatus.setText(currentPage + "/" + mTotalPage);
    } else {
      tvPageStatus.setText(0 + "/" + 0);
    }
  }
  /**
   * 隐藏页码
   */
  private void hidePageStatus() {
    tvPageStatus.setVisibility(View.INVISIBLE);
    mTvLastPage.setVisibility(View.INVISIBLE);
    mTvNextPage.setVisibility(View.INVISIBLE);
  }

  /**
   * 获取某个分类对应商品的的方法
   */
  //@formatter:off
  private void updateMenuRcv() {
    mRcvMenu.setAdapter(mProductAdapter);
     //截取商品
    if ("全部".equals(mCurrentCate.getName())){
    mCurrentProdList = DaoServiceUtil.getProductInfoService()
        .queryBuilder()
        .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
        .whereOr(PxProductInfoDao.Properties.Shelf.isNull(),PxProductInfoDao.Properties.Shelf.eq(PxProductInfo.SHELF_PUT_AWAY))
        .orderDesc(PxProductInfoDao.Properties.SaleNum)
        .limit(mLimit)
        .offset(mLimit * (mCurrentPage - 1))
        .list();
    } else {
    mCurrentProdList = DaoServiceUtil.getProductInfoService()
        .queryBuilder()
        .where(PxProductInfoDao.Properties.PxProductCategoryId.eq(mCurrentCate.getId()))
        .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
        .whereOr(PxProductInfoDao.Properties.Shelf.isNull(),PxProductInfoDao.Properties.Shelf.eq(PxProductInfo.SHELF_PUT_AWAY))
        .limit(mLimit)
        .offset(mLimit * (mCurrentPage - 1))
        .list();
    }
    //更新rcv
    //mProductAdapter.setData(mCurrentProdList);
    List<ProdInnerOrder> prodInnerOrderList = new ArrayList<>();
    ProdInnerOrder prodInnerOrder = null;
    for (PxProductInfo productInfo : mCurrentProdList) {
      prodInnerOrder = new ProdInnerOrder(productInfo,mCurrentOrderInfo);
      prodInnerOrderList.add(prodInnerOrder);
    }
    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProdInnerOrderDiffCallback(mProductAdapter.getProductInnerOrderList(),prodInnerOrderList),false);
    mProductAdapter.setData(diffResult,prodInnerOrderList);
  }

  /**
   * 分类点击
   */
  //@formatter:off
  @Override public void onCateClick(int pos) {
    try{
      //开启蒙层
      mAct.isShowProgress(true);
      //当前分类
      mCurrentCate = mCategoryList.get(pos);
      mCategoryAdapter.setSelected(mCategoryAdapter.getSelected(),pos);
      mRcvCate.smoothScrollToPosition(pos);
      //获取该分类下的内容
      getContentByCate(mCurrentCate);
      //更新页面
      if (hasChildCateList){
        mRcvMenu.setAdapter(mChildCateAdapter);
        mChildCateAdapter.setData(mChildCateList);
        //隐藏页面
        hidePageStatus();
      } else {
        //更新页面
        mCurrentPage = 1;
        updatePageStatus(mCurrentPage);
        //更新rcv
        updateMenuRcv();
      }
    } finally {
      //关闭蒙层
      mAct.isShowProgress(false);
    }
  }

  /**
   * 子分类点击
   * @param pos
   */
  @Override public void onChildCateClick(int pos) {
    //开启蒙层
    mAct.isShowProgress(true);
    mCurrentCate = mChildCateList.get(pos);
    //获取该分类下的内容
    getContentByCate(mCurrentCate);
    //更新页面
    if (hasChildCateList){
      mRcvMenu.setAdapter(mChildCateAdapter);
      mChildCateAdapter.setData(mChildCateList);
      //隐藏页面
      hidePageStatus();
    } else {
      //更新页面
      mCurrentPage = 1;
      updatePageStatus(mCurrentPage);
      //更新rcv
      updateMenuRcv();
    }
    //关闭蒙层
    mAct.isShowProgress(false);
  }

  /**
   * 商品点击
   */
  @Override public void onProductClick(final int pos) {
    if (mCurrentOrderInfo == null){
      ToastUtils.showShort(mAct,"请先开单");
      return;
    }
    //当前商品
    final PxProductInfo currentProdInfo = mCurrentProdList.get(pos);

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
      intent.putExtra(AddComboActivity.ORDER,mCurrentOrderInfo);
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
          //查询该商品有效的促销计划
          PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(mCurrentOrderInfo.getDbPromotioById(), null, currentProdInfo);
          double unitPrice = validPromotioDetails == null ? currentProdInfo.getPrice() : validPromotioDetails.getPromotionalPrice();
          double unitVipPrice = validPromotioDetails == null ? currentProdInfo.getVipPrice() : validPromotioDetails.getPromotionalPrice();
          //新建Details
          mDetails =  new PxOrderDetails();
          //默认不清空
          mDetails.setIsClear(false);
          //数量
          mDetails.setNum((double) 1);
          //多单位数量
          mDetails.setMultipleUnitNumber(0.0);
          //订单
          mDetails.setDbOrder(mCurrentOrderInfo);
          //折扣率
          mDetails.setCurrentDiscRate();
          //单价 (是否用促销价)
          mDetails.setUnitPrice(unitPrice);
          //会员单价  (是否用促销价)
          mDetails.setUnitVipPrice(unitVipPrice);
          //价格
          mDetails.setPrice(mDetails.getUnitPrice() * mDetails.getNum());
          //会员价格
          mDetails.setVipPrice(mDetails.getUnitVipPrice() * mDetails.getNum());
          //商品
          mDetails.setDbProduct(currentProdInfo);
          //下单状态
          mDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
          //商品状态
          mDetails.setStatus(PxOrderDetails.STATUS_ORIDINARY);
          //已上菜
          mDetails.setIsServing(false);
          //是否为套餐Details
          mDetails.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
          //是否是套餐内Details
          mDetails.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
          //赠品
          mDetails.setIsGift(PxOrderDetails.GIFT_FALSE);
          //备注
          mDetails.setRemarks("");
          //objId
          mDetails.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
          //储存
          DaoServiceUtil.getOrderDetailsService().save(mDetails);
          //剩余数量
          if (currentProdInfo.getOverPlus() != null && currentProdInfo.getOverPlus() != 0) {
            currentProdInfo.setOverPlus(currentProdInfo.getOverPlus() - 1);
            if (currentProdInfo.getOverPlus() == 0) {
              currentProdInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
            }
            DaoServiceUtil.getProductInfoService().saveOrUpdate(currentProdInfo);
          }
          //handler
          sendHandlerMsg(ADD_DETAILS,mDetails);
        }
      });
    } else {  //跳转至添加页面
      Intent intent = new Intent(mAct, AddProdActivity.class);
      intent.putExtra(AddProdActivity.PROD, currentProdInfo);
      intent.putExtra(AddProdActivity.ORDER, mCurrentOrderInfo);
      startActivity(intent);
    }
  }
  /**
   * 商品长按
   */
  @Override public void onProductLongClick(PxProductInfo productInfo) {
    Intent intent = new Intent(mAct, ProdInfoActivity.class);
    intent.putExtra(ProdInfoActivity.PROD_OBJ_ID, productInfo.getObjectId());
    startActivity(intent);
  }

  /**
   * 上一页
   */
  @OnClick(R.id.tv_last_page) public void lastPage(View view) {
    if (mCurrentPage == 1) {
      ToastUtils.showShort(mAct, "已经是第一页,不能翻页");
      return;
    }
    mCurrentPage -= 1;
    updatePageStatus(mCurrentPage);
    updateMenuRcv();
  }

  /**
   * 下一页
   */
  @OnClick(R.id.tv_next_page) public void nextPage(View view) {
    if (mCurrentPage == mTotalPage) {
      ToastUtils.showShort(mAct, "已经是最后一页,不能翻页");
      return;
    }
    mCurrentPage += 1;
    updatePageStatus(mCurrentPage);
    updateMenuRcv();
  }

  /**
   * 自定义商品和模糊查询
   */
  @OnClick({ R.id.tv_custom_product, R.id.tv_fuzzy_search }) public void ivClickEvent(View view) {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideExcludeCashBill(transaction);
    switch (view.getId()) {
      case R.id.tv_custom_product:
        //进入自定义商品Activity
        startActivity(new Intent(mAct, CustomProductActivity.class));
        break;
      case R.id.tv_fuzzy_search:
        if (mCashMenuFuzzyQueryFragment == null) {
          mCashMenuFuzzyQueryFragment = CashMenuFuzzyQueryFragment.newInstance("param");
          transaction.add(R.id.cash_content_right, mCashMenuFuzzyQueryFragment,
              Constants.CASH_MENU_FUZZY_QUERY_TAG);
        } else {
          transaction.show(mCashMenuFuzzyQueryFragment);
        }
        transaction.commit();
        //隐藏MainActivity3个悬浮按钮
        mAct.mCashFabs.setVisibility(View.GONE);
        //发送PxOrderInfo信息给CashMenuFuzzyQueryFragment
        EventBus.getDefault()
            .postSticky(new CashBillOrderInfoEvent().setOrderInfo(mCurrentOrderInfo));
        break;
    }
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

  /**
   * 退出
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
   * 更新OrderInfo
   */
  public void updateOrderInfo() {
    mCurrentOrderInfo = CashBillFragment.mOrderInfo;
  }

  /**
   * 刷新商品状态
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void updateProdInfoListStatus(
      UpdateProdInfoListStatusEvent event) {
    if (mCategoryAdapter == null )  return;
    boolean fromWaiter = event.isFromWaiter();
    if (fromWaiter){
      if (mCurrentOrderInfo == null){
        updateNewProdList();
      }
    }else {
      updateNewProdList();
    }
  }
  /**
   * 刷新最新订单商品数量
   */
  private void updateNewProdList() {
    if (mCategoryAdapter.getSelected() == -1) {
      if (mChildCateAdapter != null) {
        onChildCateClick(mChildCateAdapter.getSelected());
      }
    } else {
      onCateClick(mCategoryAdapter.getSelected());
    }
  }

  /**
   * 数据更新时刷新页面
   */
  public void refreshOnDataUpdate() {
    //初始化分类
    initCategory();
  }

  /**
   * 接收扫码枪结果
   */
  public void searchProdByCode(String code) {
    if (mCurrentOrderInfo == null) {
      ToastUtils.showShort(mAct, "当前无订单,请先下单");
      return;
    }
    if (code.length() != 13) {
      ToastUtils.showShort(mAct, "条码长度错误,请检查条码");
      return;
    }
    //商品编码
    String barCode = code.substring(1, 7);
    //重量(千克)
    double multNum = Double.valueOf(
        NumberFormatUtils.formatFloatNumber(Double.valueOf(code.substring(7, 12)) / 1000));
    //查询商品
    PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
        .queryBuilder()
        .where(PxProductInfoDao.Properties.BarCode.eq(barCode))
        .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
        .unique();
    //查询规格rel
    PxProductFormatRel formatRel = DaoServiceUtil.getProductFormatRelService()
        .queryBuilder()
        .where(PxProductFormatRelDao.Properties.BarCode.eq(barCode))
        .unique();
    if (productInfo != null && formatRel != null) {
      ToastUtils.showShort(App.getContext(), "条码重复，请确定条码的唯一性!");
      return;
    }
    if (productInfo == null && formatRel == null) {
      ToastUtils.showShort(App.getContext(), "条码错误，请确定录入的条码是否正确!");
      return;
    }

    //商品条码
    if (productInfo != null && formatRel == null) {
      //双单位
      if (PxProductInfo.IS_TWO_UNIT_TURE.equals(productInfo.getMultipleUnit())) {
        //双单位不带规格
        addMultipleProdWithoutFormat(productInfo, multNum);
      }
    }

    //规格条码
    if (formatRel != null && productInfo == null) {
      PxProductInfo dbProduct = formatRel.getDbProduct();
      //双单位
      if (PxProductInfo.IS_TWO_UNIT_TURE.equals(dbProduct.getMultipleUnit())) {
        //双单位带规格
        addMultipleProdWithFormat(formatRel, multNum);
      }
    }
  }

  /**
   * 添加双单位不带规格
   */
  private void addMultipleProdWithoutFormat(PxProductInfo productInfo, Double multNum) {
    //停售
    if (productInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)) {
      ToastUtils.showShort(App.getContext(), "该商品处于停售状态,不能添加");
      return;
    }

    //剩余数量
    if (productInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE) && productInfo.getOverPlus() != null && productInfo.getOverPlus() != 0) {
      Double overPlus = productInfo.getOverPlus();
      if (overPlus.doubleValue() < multNum) {
        ToastUtils.showShort(App.getContext(), "剩余数量不足");
        return;
      }
    }
    //促销计划
    PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(mCurrentOrderInfo.getDbPromotioById(), null, productInfo);
    double unitPrice = validPromotioDetails == null ? productInfo.getPrice() : validPromotioDetails.getPromotionalPrice();
    double unitVipPrice = validPromotioDetails == null ? productInfo.getVipPrice() : validPromotioDetails.getPromotionalPrice();
    //添加
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //状态
      String status = PxOrderDetails.STATUS_ORIDINARY;
      //新建Details
      PxOrderDetails details = new PxOrderDetails();
      //默认不清空
      details.setIsClear(false);
      //数量
      details.setNum(1.0);
      //多单位数量
      details.setMultipleUnitNumber(multNum);
      //订单
      details.setDbOrder(mCurrentOrderInfo);
      //折扣率
      details.setCurrentDiscRate();
      //单价
      details.setUnitPrice(unitPrice);
      //会员单价
      details.setUnitVipPrice(unitVipPrice);
      //价格
      details.setPrice(details.getUnitPrice() * details.getMultipleUnitNumber());
      details.setVipPrice(details.getUnitVipPrice() * details.getMultipleUnitNumber());
      //商品
      details.setDbProduct(productInfo);
      //下单状态
      details.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
      //商品状态
      details.setStatus(status);
      //已上菜
      details.setIsServing(false);
      //折扣率
      details.setCurrentDiscRate();
      //规格信息
      details.setDbFormatInfo(null);
      //做法
      details.setDbMethodInfo(null);
      //是否为套餐Details
      details.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
      //是否是套餐内Details
      details.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
      //是否为赠品
      details.setIsGift(PxOrderDetails.GIFT_FALSE);
      //备注
      details.setRemarks("");
      //objId
      details.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
      //储存
      DaoServiceUtil.getOrderDetailsService().save(details);

      ToastUtils.showShort(App.getContext(), "添加成功!");
      //通知CashBill更新页面
      //EventBus.getDefault().post(new RefreshCashBillListEvent());
      EventBus.getDefault().post(new CashBillAddItemEvent().setDetails(details));
      //余量
      if (productInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) {
        if (productInfo.getOverPlus() != null) {
          productInfo.setOverPlus(productInfo.getOverPlus() - multNum);
        } else {
          productInfo.setOverPlus(null);
        }
        if (productInfo.getOverPlus() != null && productInfo.getOverPlus() == 0.0) {
          productInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
        }
        //储存
        DaoServiceUtil.getProductInfoService().saveOrUpdate(productInfo);
        //刷新余量页面
        EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 双单位带规格
   */
  private void addMultipleProdWithFormat(PxProductFormatRel formatRel, double multNum) {
    //停售
    if (PxProductFormatRel.STATUS_STOP_SALE.equals(formatRel.getStatus())) {
      ToastUtils.showShort(App.getContext(), "该规格处于停售状态,不能添加");
      return;
    }
    //余量
    if (PxProductFormatRel.STATUS_ON_SALE.equals(formatRel.getStatus())) {
      if (formatRel.getStock() != null && formatRel.getStock() < multNum) {
        ToastUtils.showShort(App.getContext(), "该规格库存数量不足");
        return;
      }
    }
    //商品
    PxProductInfo productInfo = formatRel.getDbProduct();
    //规格
    PxFormatInfo formatInfo = formatRel.getDbFormat();
    //促销计划
    PxPromotioDetails validPromotioDetails =
        PromotioDetailsHelp.getValidPromotioDetails(mCurrentOrderInfo.getDbPromotioById(),
            formatInfo, productInfo);
    //
    double unitPrice = validPromotioDetails == null ? formatRel.getPrice()
        : validPromotioDetails.getPromotionalPrice();
    double unitVipPrice = validPromotioDetails == null ? formatRel.getVipPrice()
        : validPromotioDetails.getPromotionalPrice();

    //添加
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //状态
      String status = PxOrderDetails.STATUS_ORIDINARY;
      //新建Details
      PxOrderDetails details = new PxOrderDetails();
      //默认不清空
      details.setIsClear(false);
      //数量
      details.setNum(1.0);
      //多单位数量
      details.setMultipleUnitNumber(multNum);
      //订单
      details.setDbOrder(mCurrentOrderInfo);
      //折扣率
      details.setCurrentDiscRate();
      //单价(是否使用促销价)
      details.setUnitPrice(unitPrice);
      //会员单价(是否使用促销价)
      details.setUnitVipPrice(unitVipPrice);
      //价格
      details.setPrice(details.getUnitPrice() * details.getMultipleUnitNumber());
      details.setVipPrice(details.getUnitVipPrice() * details.getMultipleUnitNumber());
      //商品
      details.setDbProduct(productInfo);
      //下单状态
      details.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
      //商品状态
      details.setStatus(status);
      //已上菜
      details.setIsServing(false);
      //折扣率
      details.setCurrentDiscRate();
      //规格信息
      details.setDbFormatInfo(formatInfo);
      //做法
      details.setDbMethodInfo(null);
      //是否为套餐Details
      details.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
      //是否是套餐内Details
      details.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
      //是否为赠品
      details.setIsGift(PxOrderDetails.GIFT_FALSE);
      //备注
      details.setRemarks("");
      //objId
      details.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
      //储存
      DaoServiceUtil.getOrderDetailsService().save(details);

      ToastUtils.showShort(App.getContext(), "添加成功!");
      //通知CashBill更新页面
      EventBus.getDefault().post(new CashBillAddItemEvent().setDetails(details));
      //EventBus.getDefault().post(new RefreshCashBillListEvent());
      //余量
      if (PxProductFormatRel.STATUS_ON_SALE.equals(formatRel.getStatus())) {
        if (formatRel.getStock() != null) {
          formatRel.setStock(Double.valueOf(NumberFormatUtils.formatFloatNumber(formatRel.getStock() - multNum)));
        } else {
          formatRel.setStock(null);
        }
        if (formatRel.getStock() != null && formatRel.getStock() == 0.0) {
          formatRel.setStatus(PxProductFormatRel.STATUS_STOP_SALE);
        }
        //储存
        DaoServiceUtil.getProductInfoService().saveOrUpdate(productInfo);
        //储存
        DaoServiceUtil.getProductFormatRelService().saveOrUpdate(formatRel);
        //刷新余量页面
        EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 更新商品角标和余量
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

  //@formatter:on

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
}
