package com.psi.easymanager.ui.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.kyleduo.switchbutton.SwitchButton;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.ComboGroupAdapter;
import com.psi.easymanager.adapter.RefundReasonAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.PxComboGroupDao;
import com.psi.easymanager.dao.PxComboProductRelDao;
import com.psi.easymanager.dao.PxOptReasonDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.AddComboProdClickEvent;
import com.psi.easymanager.event.ComboRefundProdEvent;
import com.psi.easymanager.event.ConfirmComboEvent;
import com.psi.easymanager.event.EditComboProdEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.SpeechEvent;
import com.psi.easymanager.event.UpdateProdInfoListStatusEvent;
import com.psi.easymanager.module.AppComboGroupInfo;
import com.psi.easymanager.module.AppComboGroupProdInfo;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxComboGroup;
import com.psi.easymanager.module.PxComboProductRel;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxOperationLog;
import com.psi.easymanager.module.PxOptReason;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.print.MakePrintDetails;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.ui.activity.AddComboActivity;
import com.psi.easymanager.utils.ListViewHeightUtil;
import com.psi.easymanager.utils.PromotioDetailsHelp;
import com.psi.easymanager.utils.ToastUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/8/17.
 */
public class AddComboFragment extends BaseFragment {

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

  //ListView
  @Bind(R.id.lv_combo_group) ListView mLvComboGroup;
  //延迟
  @Bind(R.id.sb_wait) SwitchButton mSbWait;
  //延迟title
  @Bind(R.id.tv_wait_title) TextView mTvWait;
  //删除按钮
  @Bind(R.id.btn_del) Button mBtnDel;
  //退菜按钮
  @Bind(R.id.btn_refund) Button mBtnRefund;
  //取餐按钮
  @Bind(R.id.btn_take_food) Button mBtnTakeFood;

  //商品
  private PxProductInfo mProductInfo;
  //订单
  private PxOrderInfo mPxOrderInfo;
  //Details
  private PxOrderDetails mPxOrderDetails;
  //类型
  private String mType;

  //适配器
  private ComboGroupAdapter mComboGroupAdapter;
  //数据
  private List<AppComboGroupInfo> mAppComboGroupInfoList;
  //商品
  private AddComboActivity mAct;
  //份数
  private int mNum = 1;
  //可用退菜原因
  private List<PxOptReason> mReasonList;
  //所选退菜原因
  private PxOptReason mRefundReason;

  //@formatter:off
  public static AddComboFragment newInstance(PxProductInfo productInfo, PxOrderInfo pxOrderInfo, Long orderDetailsId, String type) {
    AddComboFragment addComboFragment = new AddComboFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(PARAM_TYPE, type);
    bundle.putSerializable(PARAM_ORDER, pxOrderInfo);
    if (TYPE_ADD.equals(type)) {
      bundle.putSerializable(PARAM_PROD, productInfo);
    } else {
      bundle.putLong(PARAM_DETAILS_ID, orderDetailsId);
    }
    addComboFragment.setArguments(bundle);
    return addComboFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
    if (getArguments() != null) {
      mType = getArguments().getString(PARAM_TYPE);
      mPxOrderInfo = (PxOrderInfo) getArguments().getSerializable(PARAM_ORDER);
      if (TYPE_ADD.equals(mType)) {
        mProductInfo = (PxProductInfo) getArguments().getSerializable(PARAM_PROD);
      } else {
        long orderDetailsId = getArguments().getLong(PARAM_DETAILS_ID);
        mPxOrderDetails = DaoServiceUtil.getOrderDetailsService()
            .queryBuilder()
            .where(PxOrderDetailsDao.Properties.Id.eq(orderDetailsId))
            .unique();
      }
    }
    //mAct
    mAct = (AddComboActivity) getActivity();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_add_combo, null);
    ButterKnife.bind(this, view);
    //是否为编辑状态
    if (TYPE_EDIT.equals(mType)) {
      //获取商品
      mProductInfo = mPxOrderDetails.getDbProduct();
      //获取是否暂不上菜
      if (mPxOrderDetails.getStatus().equals(PxOrderDetails.STATUS_ORIDINARY)) {
        mSbWait.setChecked(false);
      } else {
        mSbWait.setChecked(true);
      }
      //份数
      mNum = (int) mPxOrderDetails.getNum().doubleValue();
      //显示删除按钮
      mBtnDel.setVisibility(View.VISIBLE);
      //隐藏退菜按钮
      mBtnRefund.setVisibility(View.GONE);
      //隐藏取餐按钮
      mBtnTakeFood.setVisibility(View.GONE);
    }

    //是否为已下单状态
    if (TYPE_ORDERED.equals(mType)) {
      //获取商品
      mProductInfo = mPxOrderDetails.getDbProduct();
      //获取是否暂不上菜
      if (mPxOrderDetails.getStatus().equals(PxOrderDetails.STATUS_ORIDINARY)) {
        mSbWait.setChecked(false);
      } else {
        mSbWait.setChecked(true);
      }
      mSbWait.setEnabled(false);
      //份数
      mNum = (int) mPxOrderDetails.getNum().doubleValue();
      //隐藏删除按钮
      mBtnDel.setVisibility(View.INVISIBLE);
      //显示退菜按钮
      mBtnRefund.setVisibility(View.VISIBLE);
      //显示取餐按钮
      mBtnTakeFood.setVisibility(View.VISIBLE);
    }
    //新建套餐details
    saveComboTemporary();
    //初始化ListView
    initLv();
    return view;
  }



  /**
   * 呼叫取餐
   */
  //@formatter:on
  @OnClick(R.id.btn_take_food) public void takeFood() {
    String no = mPxOrderInfo.getOrderNo().substring(mPxOrderInfo.getOrderNo().length() - 6);
    String num = String.valueOf(Integer.parseInt(no));
    String content = "请" + num + "号到前台取餐";
    EventBus.getDefault().post(new SpeechEvent().setContent(content));

    mPxOrderDetails.setIsServing(true);
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(mPxOrderDetails);

    List<PxOrderDetails> inComboDetails = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
        .list();
    for (PxOrderDetails details : inComboDetails) {
      details.setIsServing(true);
    }
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(inComboDetails);

    EventBus.getDefault().post(new RefreshCashBillListEvent());
    mBtnTakeFood.setClickable(true);
  }

  /**
   * 储存临时套餐
   */
  private void saveComboTemporary() {
    if (!TYPE_ADD.equals(mType)) return;
    //新建Details
    mPxOrderDetails = new PxOrderDetails();
    //促销计划
    PxPromotioDetails validPromotioDetails =
        PromotioDetailsHelp.getValidPromotioDetails(mPxOrderInfo.getDbPromotioById(), null,
            mProductInfo);
    //默认不清空
    mPxOrderDetails.setIsClear(false);
    //未打印
    mPxOrderDetails.setIsPrinted(false);
    //数量
    mPxOrderDetails.setNum((double) mNum);
    //多单位数量
    mPxOrderDetails.setMultipleUnitNumber(0.0);
    //订单
    mPxOrderDetails.setDbOrder(mPxOrderInfo);
    //折扣率
    mPxOrderDetails.setCurrentDiscRate();
    //单价 (有促销就用促销价)
    mPxOrderDetails.setUnitPrice(validPromotioDetails == null ? mProductInfo.getPrice()
        : validPromotioDetails.getPromotionalPrice());
    //会员单价(有促销就用促销价)
    mPxOrderDetails.setUnitVipPrice(validPromotioDetails == null ? mProductInfo.getVipPrice()
        : validPromotioDetails.getPromotionalPrice());
    //价格
    mPxOrderDetails.setPrice(mPxOrderDetails.getUnitPrice() * mNum);
    //会员价格
    mPxOrderDetails.setVipPrice(mPxOrderDetails.getUnitVipPrice() * mNum);
    //商品
    mPxOrderDetails.setDbProduct(mProductInfo);
    //下单状态
    mPxOrderDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
    //已上菜
    mPxOrderDetails.setIsServing(false);
    //是否延迟
    mPxOrderDetails.setStatus(PxOrderDetails.STATUS_ORIDINARY);
    //剩余数量
    if (mProductInfo.getOverPlus() != null && mProductInfo.getOverPlus() != 0) {
      mProductInfo.setOverPlus(mProductInfo.getOverPlus() - 1);
      if (mProductInfo.getOverPlus() == 0) {
        mProductInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
      }
    }
    //是否为套餐Details
    mPxOrderDetails.setIsComboDetails(PxOrderDetails.IS_COMBO_TRUE);
    //是否是套餐内Details
    mPxOrderDetails.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
    //临时
    mPxOrderDetails.setIsComboTemporaryDetails(true);
    //赠品
    mPxOrderDetails.setIsGift(PxOrderDetails.GIFT_FALSE);
    //备注
    mPxOrderDetails.setRemarks("");
    //objId
    mPxOrderDetails.setObjectId(
        UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis());
    //储存
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(mPxOrderDetails);
  }

  /**
   * 初始化ListView 编辑时
   */
  //@formatter:off
  private void initLv() {
    //分组列表
    List<PxComboGroup> comboGroupList = DaoServiceUtil.getComboGroupService()
        .queryBuilder()
        .where(PxComboGroupDao.Properties.DbComboId.eq(mProductInfo.getId()))
        .where(PxComboGroupDao.Properties.DelFlag.eq("0"))
        .orderDesc(PxComboGroupDao.Properties.Type)
        .list();
    //创建用于ListView的分组数据
    mAppComboGroupInfoList = new ArrayList<AppComboGroupInfo>();
    for (PxComboGroup comboGroup : comboGroupList) {
      AppComboGroupInfo appComboGroupInfo = new AppComboGroupInfo();
      //设置分组
      appComboGroupInfo.setComboGroup(comboGroup);
      //当前分组对应的relList
      List<PxComboProductRel> relList = DaoServiceUtil.getComboProdRelService()
          .queryBuilder()
          .where(PxComboProductRelDao.Properties.PxComboGroupId.eq(comboGroup.getId()))
          .where(PxComboProductRelDao.Properties.DelFlag.eq("0"))
          .list();
      //设置允许数量
      if (comboGroup.getType().equals(PxComboGroup.TYPE_REQUIRED)) {
        if (relList != null && relList.size() != 0) {
          appComboGroupInfo.setNumAllow(relList.size());
        } else {
          appComboGroupInfo.setNumAllow(0);
        }
      } else {
        appComboGroupInfo.setNumAllow(comboGroup.getAllowNum());
      }
      //创建用于ListView嵌套的GirdView数据
      ArrayList<AppComboGroupProdInfo> appProdList = new ArrayList<AppComboGroupProdInfo>();
      //遍历分组商品relList
      for (PxComboProductRel rel : relList) {
        AppComboGroupProdInfo appComboGroupProdInfo = new AppComboGroupProdInfo();
        //商品
        appComboGroupProdInfo.setProductInfo(rel.getDbProduct());
        //数量
        appComboGroupProdInfo.setNum(rel.getNum());
        //重量
        if (rel.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          appComboGroupProdInfo.setWeight(rel.getWeight());
        }
        //规格
        if (rel.getDbFormat() != null) {
          appComboGroupProdInfo.setFormatInfo(rel.getDbFormat());
        }
        //类型
        appComboGroupProdInfo.setType(comboGroup.getType());
        //是否选择
        appComboGroupProdInfo.setSelected(false);
        //添加到list
        appProdList.add(appComboGroupProdInfo);

        //如果是添加状态
        if (TYPE_ADD.equals(mType)) {
          //如果是必选
          if (PxComboGroup.TYPE_REQUIRED.equals(comboGroup.getType())) {
            //新建
            PxOrderDetails newDetails = new PxOrderDetails();
            //默认不清空
            newDetails.setIsClear(false);
            //未打印
            newDetails.setIsPrinted(false);
            //数量
            newDetails.setNum(appComboGroupProdInfo.getNum().doubleValue());
            //重量
            if (appComboGroupProdInfo.getWeight() == null){
              newDetails.setMultipleUnitNumber(0.0);
            } else {
              newDetails.setMultipleUnitNumber(appComboGroupProdInfo.getWeight());
            }
            //订单
            newDetails.setDbOrder(mPxOrderInfo);
            //折扣率
            newDetails.setCurrentDiscRate();
            //单价
            newDetails.setUnitPrice(appComboGroupProdInfo.getProductInfo().getPrice());
            //会员单价
            newDetails.setUnitVipPrice(appComboGroupProdInfo.getProductInfo().getVipPrice());
            //价格
            newDetails.setPrice(newDetails.getUnitPrice() * mNum);
            //会员价格
            newDetails.setVipPrice(newDetails.getUnitVipPrice() * mNum);
            //商品
            newDetails.setDbProduct(appComboGroupProdInfo.getProductInfo());
            //下单状态
            newDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
            //已上菜
            newDetails.setIsServing(false);
            //是否延迟
            newDetails.setStatus(PxOrderDetails.STATUS_ORIDINARY);
            //是否为套餐Details
            newDetails.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
            //是否是套餐内Details
            newDetails.setInCombo(PxOrderDetails.IN_COMBO_TRUE);
            //所在套餐Details
            newDetails.setDbComboDetails(mPxOrderDetails);
            //临时
            newDetails.setIsComboTemporaryDetails(true);
            //必选
            newDetails.setChooseType(PxOrderDetails.TYPE_REQUIRED);
            //赠品
            newDetails.setIsGift(PxOrderDetails.GIFT_FALSE);
            //备注
            newDetails.setRemarks("");
            //规格
            newDetails.setDbFormatInfo(rel.getDbFormat());
            //objId
            newDetails.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
            //储存
            DaoServiceUtil.getOrderDetailsService().saveOrUpdate(newDetails);
            //所选数量+1
            appComboGroupInfo.setNumSelected(appComboGroupInfo.getNumSelected() + 1);
            //选中
            appComboGroupProdInfo.setSelected(true);
          }
        }

        //如果是编辑状态
        if (TYPE_EDIT.equals(mType) || TYPE_ORDERED.equals(mType)) {
          //如果是可选
          if (PxComboGroup.TYPE_OPTIONAL.equals(comboGroup.getType())) {
            //查询已添加的商品
            PxOrderDetails selectedDetails = null;
            if (rel.getDbFormat() == null) {
              selectedDetails = DaoServiceUtil.getOrderDetailsService()
                  .queryBuilder()
                  .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mPxOrderInfo.getId()))
                  .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
                  .where(PxOrderDetailsDao.Properties.PxProductInfoId.eq(rel.getDbProduct().getId()))
                  .where(PxOrderDetailsDao.Properties.PxFormatInfoId.isNull())
                  .unique();
            } else {
              selectedDetails = DaoServiceUtil.getOrderDetailsService()
                  .queryBuilder()
                  .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mPxOrderInfo.getId()))
                  .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
                  .where(PxOrderDetailsDao.Properties.PxProductInfoId.eq(rel.getDbProduct().getId()))
                  .where(PxOrderDetailsDao.Properties.PxFormatInfoId.eq(rel.getDbFormat().getId()))
                  .unique();
            }
            if (selectedDetails != null) {
              //所选数量+1
              appComboGroupInfo.setNumSelected(appComboGroupInfo.getNumSelected() + 1);
              //选中
              appComboGroupProdInfo.setSelected(true);
            }
          }
          //如果是必选
          else {
            //所选数量+1
            appComboGroupInfo.setNumSelected(appComboGroupInfo.getNumSelected() + 1);
            //选中
            appComboGroupProdInfo.setSelected(true);
          }
        }
      }
      //设置prodList
      appComboGroupInfo.setAppComboGroupProdInfoList(appProdList);
      //添加至list
      mAppComboGroupInfoList.add(appComboGroupInfo);
    }
    //发送给EditComboFragment,更新页面
    EventBus.getDefault().post(new EditComboProdEvent().setType(mType).setComboDetails(mPxOrderDetails));
    //设置ComboGroupAdapter
    mComboGroupAdapter = new ComboGroupAdapter(mAct, mAppComboGroupInfoList);
    mLvComboGroup.setAdapter(mComboGroupAdapter);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
  }

  /**
   * 商品点击
   */
  //@formatter:off
  @Subscribe(threadMode = ThreadMode.MAIN) public void OnProdClickEvent(AddComboProdClickEvent event) {
    //如果没有套餐Details,返回
    if (mPxOrderDetails == null) return;
    //获取AppComboGroup
    AppComboGroupInfo appComboGroup = event.getComboGroup();
    //获取pos
    int pos = event.getPos();
    //获取ProdInfoList
    List<AppComboGroupProdInfo> prodInfoList = appComboGroup.getAppComboGroupProdInfoList();
    //获取所点击的ProdInfo
    AppComboGroupProdInfo appProdInfo = prodInfoList.get(pos);
    //当前prodInfo所限制的数量
    Integer prodAllowNum = appProdInfo.getNum();
    //包含的商品
    PxProductInfo productInfo = appProdInfo.getProductInfo();
    //包含的规格
    PxFormatInfo formatInfo = appProdInfo.getFormatInfo();

    //添加或编辑状态 && 可选
    if ((TYPE_ADD.equals(mType) || TYPE_EDIT.equals(mType)) && PxComboGroup.TYPE_OPTIONAL.equals(appComboGroup.getComboGroup().getType())) {
      //如果之前没选
      if (!appProdInfo.isSelected()) {
        if (appComboGroup.getNumSelected() >= appComboGroup.getNumAllow()) {
          ToastUtils.showShort(mAct, "超过了该分组商品上限,不能添加");
          return;
        }
        //新建
        PxOrderDetails newDetails = new PxOrderDetails();
        //默认不清空
        newDetails.setIsClear(false);
        //未打印
        newDetails.setIsPrinted(false);
        //数量
        newDetails.setNum(prodAllowNum.doubleValue());
        //重量
        if (appProdInfo.getWeight() == null){
          newDetails.setMultipleUnitNumber(0.0);
        } else {
          newDetails.setMultipleUnitNumber(appProdInfo.getWeight());
        }
        //订单
        newDetails.setDbOrder(mPxOrderInfo);
        //折扣率
        newDetails.setCurrentDiscRate();
        //单价
        newDetails.setUnitPrice(productInfo.getPrice());
        //会员单价
        newDetails.setUnitVipPrice(productInfo.getVipPrice());
        //价格
        newDetails.setPrice(newDetails.getUnitPrice() * mNum);
        //会员价格
        newDetails.setVipPrice(newDetails.getUnitVipPrice() * mNum);
        //商品
        newDetails.setDbProduct(productInfo);
        //规格
        newDetails.setDbFormatInfo(formatInfo);
        //下单状态
        newDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
        //已上菜
        newDetails.setIsServing(false);
        //是否延迟
        newDetails.setStatus(PxOrderDetails.STATUS_ORIDINARY);
        //是否为套餐Details
        newDetails.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
        //是否是套餐内Details
        newDetails.setInCombo(PxOrderDetails.IN_COMBO_TRUE);
        //所在套餐Details
        newDetails.setDbComboDetails(mPxOrderDetails);
        //临时
        newDetails.setIsComboTemporaryDetails(true);
        //可选
        newDetails.setChooseType(PxOrderDetails.TYPE_OPTIONAL);
        //赠品
        newDetails.setIsGift(PxOrderDetails.GIFT_FALSE);
        //备注
        newDetails.setRemarks("");
        //objId
        newDetails.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
        //储存
        DaoServiceUtil.getOrderDetailsService().saveOrUpdate(newDetails);
        //所选数量+1
        appComboGroup.setNumSelected(appComboGroup.getNumSelected() + 1);
        //选中
        appProdInfo.setSelected(true);
      }
      //如果之前选中了
      else {
        PxOrderDetails details = null;
        if (formatInfo != null) {
          details = DaoServiceUtil.getOrderDetailsService()
              .queryBuilder()
              .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
              .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mPxOrderInfo.getId()))
              .where(PxOrderDetailsDao.Properties.PxProductInfoId.eq(productInfo.getId()))
              .where(PxOrderDetailsDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
              .unique();
        } else {
          details = DaoServiceUtil.getOrderDetailsService()
              .queryBuilder()
              .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
              .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mPxOrderInfo.getId()))
              .where(PxOrderDetailsDao.Properties.PxProductInfoId.eq(productInfo.getId()))
              .unique();
        }
        if (details != null) {
          //删除
          DaoServiceUtil.getOrderDetailsService().delete(details);
          //所选数量-1
          appComboGroup.setNumSelected(appComboGroup.getNumSelected() - 1);
          //不选中
          appProdInfo.setSelected(false);
        }
      }
      //更新GroupAdapter
      mComboGroupAdapter.notifyDataSetChanged();
      //发送给EditComboFragment,更新页面
      EventBus.getDefault().post(new EditComboProdEvent().setType(EditComboProdEvent.TYPE_ADD).setComboDetails(mPxOrderDetails));
    }


    //已下单状态
    if (TYPE_ORDERED.equals(mType)) {

    }
  }


  /**
   * 接收长按退菜事件
   */
  //@formatter:off
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveRefundEvent(ComboRefundProdEvent event) {

  }

  /**
   * 确定,发送给EditCombo
   */
  @OnClick(R.id.btn_confirm) public void addConfirm() {
    for (AppComboGroupInfo groupInfo:mAppComboGroupInfoList){
      PxComboGroup comboGroup = groupInfo.getComboGroup();
      if (comboGroup.getType().equals(PxComboGroup.TYPE_OPTIONAL)){
        int numAllow = groupInfo.getNumAllow();
        int numSelected = groupInfo.getNumSelected();
        if (numSelected < numAllow){
          ToastUtils.showShort(mAct, "(" + comboGroup.getName() + ")" + "分组所选商品数量不足,请重新添加");
          return;
        }
      }
    }
    EventBus.getDefault().post(new ConfirmComboEvent().setType(mType).setComboNum(mNum).setDelay(mSbWait.isChecked()));
  }

  /**
   * 删除
   */
  @OnClick(R.id.btn_del) public void delCombo(){
    if (TYPE_EDIT.equals(mType) == false) return;
    List<PxOrderDetails> orderDetailsList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
        .list();
    DaoServiceUtil.getOrderDetailsService().delete(orderDetailsList);
    DaoServiceUtil.getOrderDetailsService().delete(mPxOrderDetails);
    //余量
    mProductInfo = DaoServiceUtil.getProductInfoService().queryBuilder().where(PxProductInfoDao.Properties.ObjectId.eq(mProductInfo.getObjectId())).unique();
    if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE) || ((mProductInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) && mProductInfo.getOverPlus() != null && mProductInfo.getOverPlus() != 0)) {
      mProductInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(mProductInfo.getObjectId()))
          .unique();
      mProductInfo.setOverPlus(mProductInfo.getOverPlus() + mPxOrderDetails.getNum());
      mProductInfo.setStatus(PxProductInfo.STATUS_ON_SALE);
      DaoServiceUtil.getProductInfoService().saveOrUpdate(mProductInfo);
      EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
    }
    //刷新CashBill
    EventBus.getDefault().post(new RefreshCashBillListEvent());
    //关闭
    mAct.finish();
  }

  /**
   * 退菜
   */
  @OnClick(R.id.btn_refund) public void refundComboClick(){
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("退菜")
        .customView(R.layout.layout_dialog_refund_prod, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .build();
    //确定按钮
    final MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
    //数量
    EditText etNum = (EditText) dialog.getCustomView().findViewById(R.id.et_num);
    etNum.setText("数量:" + 1);
    etNum.setEnabled(false);
    //退菜原因lv
    ListView lv = (ListView) dialog.getCustomView().findViewById(R.id.lv_refund_reasons);
    //查询可用的退货原因
    mReasonList = DaoServiceUtil.getOptReasonService()
        .queryBuilder()
        .where(PxOptReasonDao.Properties.DelFlag.eq("0"))
        .where(PxOptReasonDao.Properties.Type.eq(PxOptReason.REFUND_REASON))
        .list();
    RefundReasonAdapter reasonAdapter = new RefundReasonAdapter(mAct, mReasonList);
    lv.setAdapter(reasonAdapter);
    ListViewHeightUtil.setListViewHeightBasedOnChildren(lv);
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mRefundReason = mReasonList.get(position);
      }
    });
    mRefundReason = null;
    //显示
    dialog.show();
    positiveBtn.setEnabled(true);
    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
       refundCombo(1,mRefundReason);
      }
    });
  }

  /**
   * 退套餐
   */
  private void refundCombo(int num, PxOptReason refundReason) {
    if (TYPE_ORDERED.equals(mType) == false) return;
    //map
    SparseArray<PrintDetailsCollect> collectArray = new SparseArray<>();

    List<Long> printIdList = new ArrayList<>();
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //退菜时间
      Date refundDate = new Date();
      //退菜状态
      mPxOrderDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
      //退菜时间
      mPxOrderDetails.setOperateTime(refundDate);
      //储存
      DaoServiceUtil.getOrderDetailsService().saveOrUpdate(mPxOrderDetails);

      //套餐内Details
      List<PxOrderDetails> orderDetailsList = DaoServiceUtil.getOrderDetailsService()
          .queryBuilder()
          .where(PxOrderDetailsDao.Properties.PxComboDetailsId.eq(mPxOrderDetails.getId()))
          .list();
      for (PxOrderDetails details:orderDetailsList){
        details.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
        details.setDbReason(refundReason);
        DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);
        //生成PrintDetails
        MakePrintDetails.makeNotMergePrintDetails(details,refundDate,collectArray,details.getNum(),details.getMultipleUnitNumber(),printIdList);
      }

      //刷新CashBill
      EventBus.getDefault().post(new RefreshCashBillListEvent());
      //生成操作记录
      makeOperationRecord(refundDate,num);
      //结束事务
      db.setTransactionSuccessful();
    } catch (Exception e){
      Logger.e(e.toString());
    } finally {
      db.endTransaction();
    }
    //后厨打印
    PrintTaskManager.printKitchenTask(collectArray,printIdList,true);
    //关闭
    mAct.finish();
  }

  /**
   * 生成操作记录
   */

  private void makeOperationRecord( Date refundDate, int num) {
    App app = (App) App.getContext();
    if (app == null || app.getUser() == null) return;
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    PxOperationLog operationRecord = new PxOperationLog();
    operationRecord.setCid(office.getObjectId());
    //操作日期
    operationRecord.setOperaterDate(refundDate.getTime());
    //操作人员
    operationRecord.setOperater(app.getUser().getName());
    //订单序列号
    operationRecord.setOrderNo(mPxOrderInfo.getOrderNo());
    //类型
    operationRecord.setType(PxOperationLog.TYPE_REFUND);
    //名称
    StringBuilder sb = new StringBuilder();
    sb.append(mPxOrderDetails.getDbProduct().getName());
    sb.append("[" + num + mPxOrderDetails.getDbProduct().getUnit() + "]");
    operationRecord.setProductName(sb.toString());
    //操作缘由
    if (mRefundReason != null) {
      operationRecord.setRemarks(mRefundReason.getName());
    }
    //价格
    if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(mPxOrderDetails.getDbOrder().getUseVipCard())) {
      operationRecord.setTotalPrice(mPxOrderDetails.getUnitVipPrice() * num);
    } else {
      operationRecord.setTotalPrice(mPxOrderDetails.getUnitPrice() * num);
    }
    DaoServiceUtil.getOperationRecordService().saveOrUpdate(operationRecord);
  }

}
