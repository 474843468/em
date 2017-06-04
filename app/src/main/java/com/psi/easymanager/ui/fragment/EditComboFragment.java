package com.psi.easymanager.ui.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.EditComboProdAdapter;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.ConfirmComboEvent;
import com.psi.easymanager.event.EditComboProdEvent;
import com.psi.easymanager.event.ExistComboEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.UpdateProdInfoListStatusEvent;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.ui.activity.AddComboActivity;
import com.psi.easymanager.utils.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/8/17.
 */
public class EditComboFragment extends BaseFragment
    implements EditComboProdAdapter.OnComboClickListener {

  public static final String TYPE_ADD = "Add";
  public static final String TYPE_EDIT = "Edit";
  public static final String TYPE_ORDERED = "Ordered";

  //param key
  public static final String PARAM_PROD = "ParamProd";
  //param key
  public static final String PARAM_ORDER = "ParamOrder";
  //param key
  public static final String PARAM_DETAILS_ID = "ParamDetailsId";
  //param key
  public static final String PARAM_TYPE = "ParamType";

  //商品名
  @Bind(R.id.tv_prod_name) TextView mTvProdName;
  //商品份数
  @Bind(R.id.tv_combo_num) TextView mTvComboNum;
  //ListView
  @Bind(R.id.rcv_prod) RecyclerView mRcvProd;
  //商品
  private PxProductInfo mProductInfo;
  //订单
  private PxOrderInfo mPxOrderInfo;
  //Details
  private PxOrderDetails mPxOrderDetails;
  //类型
  private String mType;

  //数据
  private List<PxOrderDetails> mEditComboProdInfoList;
  //适配器
  private EditComboProdAdapter mEditComboProdAdapter;
  //mAct
  private AddComboActivity mAct;
  //FragmentManager
  private FragmentManager mFm;

  //ExistComboFragment
  private ExistComboFragment mExistComboFragment;

  //@formatter:off
  public static EditComboFragment newInstance(PxProductInfo productInfo, PxOrderInfo pxOrderInfo, Long orderDetailsId, String type) {
    EditComboFragment editComboFragment = new EditComboFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(PARAM_TYPE, type);
    bundle.putSerializable(PARAM_ORDER, pxOrderInfo);
    if (type.equals(TYPE_ADD)) {
      bundle.putSerializable(PARAM_PROD, productInfo);
    } else {
      bundle.putLong(PARAM_DETAILS_ID, orderDetailsId);
    }
    editComboFragment.setArguments(bundle);
    return editComboFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
    mAct = (AddComboActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    if (getArguments() != null) {
      mType = getArguments().getString(PARAM_TYPE);
      mPxOrderInfo = (PxOrderInfo) getArguments().getSerializable(PARAM_ORDER);
      if (mType.equals(TYPE_ADD)) {
        mProductInfo = (PxProductInfo) getArguments().getSerializable(PARAM_PROD);
      } else {
        long orderDetailsId = getArguments().getLong(PARAM_DETAILS_ID);
        mPxOrderDetails = DaoServiceUtil.getOrderDetailsService()
            .queryBuilder()
            .where(PxOrderDetailsDao.Properties.Id.eq(orderDetailsId))
            .unique();
        mProductInfo = mPxOrderDetails.getDbProduct();
      }
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_combo, null);
    ButterKnife.bind(this, view);
    //初始化标题
    initTitle();
    //初始化rcv
    initRcv();
    return view;
  }

  /**
   * 初始化rcv
   */
  //@formatter:off
  private void initRcv() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false);
    mEditComboProdInfoList = new ArrayList<PxOrderDetails>();
    mEditComboProdAdapter = new EditComboProdAdapter(mAct, mEditComboProdInfoList);
    mEditComboProdAdapter.setOnComboClickListener(this);
    mRcvProd.setHasFixedSize(true);
    mRcvProd.setLayoutManager(layoutManager);
    mRcvProd.setAdapter(mEditComboProdAdapter);
  }

  /**
   * 初始化标题
   */
  private void initTitle() {
    if (mType.equals(TYPE_ADD)) {
      mTvProdName.setText(mProductInfo.getName());
      mTvComboNum.setVisibility(View.INVISIBLE);
    } else {
      mTvProdName.setText(mPxOrderDetails.getDbProduct().getName());
      mTvComboNum.setText(mPxOrderDetails.getNum() + "份");
      mTvComboNum.setVisibility(View.VISIBLE);
    }
  }

  /**
   * 编辑套餐内商品数据
   * 给Adapter更新数据
   */
  //@formatter:off
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveComboProdEvent(EditComboProdEvent event) {
    String type = event.getType();
    mPxOrderDetails = event.getComboDetails();
    if (EditComboProdEvent.TYPE_ADD.equals(type) || EditComboFragment.TYPE_EDIT.equals(type)) {
      mEditComboProdInfoList = DaoServiceUtil.getOrderDetailsService()
          .queryBuilder()
          .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mPxOrderInfo.getId()))
          .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
          .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_UNORDER))
          .list();
      mEditComboProdAdapter.setData(mEditComboProdInfoList);
    } else {
     mEditComboProdInfoList = DaoServiceUtil.getOrderDetailsService()
          .queryBuilder()
          .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mPxOrderInfo.getId()))
          .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
          .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_ORDER))
          .list();
      mEditComboProdAdapter.setData(mEditComboProdInfoList);
    }
  }

  /**
   * 接收AddComboFragment确定请求
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void confirm(ConfirmComboEvent event) {
    //获取Event内数据
    String type = event.getType();
    int comboNum = event.getComboNum();
    boolean delay = event.isDelay();

    //事务处理
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //添加状态或编辑状态
      if (ConfirmComboEvent.TYPE_ADD.equals(type) || TYPE_EDIT.equals(type)) {
        //是否沽清
        mProductInfo = DaoServiceUtil.getProductInfoService().queryBuilder().where(PxProductInfoDao.Properties.ObjectId.eq(mProductInfo.getObjectId())).unique();
        if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)){
          ToastUtils.showShort(mAct,"该套餐已沽清，不能添加");
          return;
        }
        //变更ComboDetails数据
        mPxOrderDetails.setNum((double) comboNum);
        mPxOrderDetails.setPrice((double) comboNum * mPxOrderDetails.getUnitPrice());
        mPxOrderDetails.setVipPrice((double) comboNum * mPxOrderDetails.getUnitVipPrice());
        if (delay) {
          mPxOrderDetails.setStatus(PxOrderDetails.STATUS_DELAY);
        } else {
          mPxOrderDetails.setStatus(PxOrderDetails.STATUS_ORIDINARY);
        }
        mPxOrderDetails.setIsComboTemporaryDetails(false);
        //储存
        DaoServiceUtil.getOrderDetailsService().saveOrUpdate(mPxOrderDetails);

        //对应的套餐内Details
        List<PxOrderDetails> detailsList = DaoServiceUtil.getOrderDetailsService()
            .queryBuilder()
            .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mPxOrderInfo.getId()))
            .where(PxOrderDetailsDao.Properties.IsComboTemporaryDetails.eq(true))
            .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
            .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_UNORDER))
            .list();
        for (PxOrderDetails details : detailsList) {
          details.setNum(details.getNum() * comboNum);
          details.setPrice(details.getPrice() * comboNum);
          details.setVipPrice(details.getVipPrice() * comboNum);
          if (delay) {
            details.setStatus(PxOrderDetails.STATUS_DELAY);
          } else {
            details.setStatus(PxOrderDetails.STATUS_ORIDINARY);
          }
          //临时订单变为false
          details.setIsComboTemporaryDetails(false);
          //储存
          DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);
        }
        //剩余数量
        if (ConfirmComboEvent.TYPE_ADD.equals(type)){
          if (mProductInfo.getOverPlus() != null) {
            mProductInfo.setOverPlus(mProductInfo.getOverPlus() - comboNum);
            if (mProductInfo.getOverPlus() == 0) {
              mProductInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
            }
            //储存
            DaoServiceUtil.getProductInfoService().saveOrUpdate(mProductInfo);
            //刷新商品列表状态
            EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
          }
        }
      }
      //已下单状态
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
      Logger.i(e.getMessage());
    } finally {
      db.endTransaction();
    }
    //更新CashBill
    EventBus.getDefault().post(new RefreshCashBillListEvent());
    //关闭mAct
    mAct.finish();
  }


  @Override public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
  }

  /**
   * Item Click
   * @param pos
   */
  //@formatter:on
  @Override public void onComboClick(int pos) {
    PxOrderDetails details = mEditComboProdInfoList.get(pos);
    EventBus.getDefault().postSticky(new ExistComboEvent().setDetails(details).setType(mType));

    FragmentTransaction transaction = mFm.beginTransaction();
    hideFragment(transaction);
    if (mExistComboFragment == null) {
      mExistComboFragment = ExistComboFragment.newInstance("param");
      transaction.add(R.id.fragment_container_right, mExistComboFragment, Constants.EXIST_COMBO_TAG);
    } else {
      transaction.show(mExistComboFragment);
    }
    transaction.commit();
  }

  /**
   * 隐藏Fragment
   */
  public void hideFragment(FragmentTransaction transaction) {
    List<Fragment> fragments = mFm.getFragments();
    for (Fragment fragment : fragments) {
      if (fragment instanceof AddComboFragment) {
        transaction.hide(fragment);
      }
    }
  }
}
