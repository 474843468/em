package com.psi.easymanager.upload;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxProductCategoryDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.PxProductMethodRefDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.UploadOrderEvent;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductMethodRef;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpOrderReq;
import com.psi.easymanager.network.resp.HttpOrderResp;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.upload.module.UpLoadProduct;
import com.psi.easymanager.upload.module.UpLoadPxOrderDetails;
import com.psi.easymanager.upload.module.UpLoadPxOrderInfo;
import com.psi.easymanager.upload.module.UploadPayInfo;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.UserUtils;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;

/**
 * User: ylw
 * Date: 2016-06-14
 * Time: 12:22
 * 上传订单
 */
public class UpLoadOrder {
  private static final String NULL_USER = "App出现异常,请重新启动!";//无User
  public static final int ORDER_SUCCESS = 0;
  private static final int LIMIT = 200;//上传限制
  private boolean isUploading = false;//是否有任务正在上传
  private CopyOnWriteArrayList<String> mResultList;//上传结果集
  //private ExecutorService mThreadPool;

  // @formatter:on
  private UpLoadOrder() {
  }

  public boolean isUploading() {
    return isUploading;
  }

  /**
   * 提供一个实例
   */
  public static UpLoadOrder getInstance() {
    return UploadOrderHolder.sInstance;
  }

  private static class UploadOrderHolder {
    private static final UpLoadOrder sInstance = new UpLoadOrder();
  }

  /**
   * 对外 提供上传订单方法
   */
  public synchronized void upLoadOrderInfo() {
    if (isUploading) {
      EventBus.getDefault().post(new UploadOrderEvent("正在上传中..."));
      return;
    }
    upLoad();
  }

  /**
   * 对外上传单个订单
   */
  public void uploadSingleOrder(PxOrderInfo orderInfo) {
    ArrayList<PxOrderInfo> list = new ArrayList<>();
    list.add(orderInfo);
    List<UpLoadPxOrderInfo> upLoadOrderInfoList = getUpLoadOrderInfoList(list);
    if (upLoadOrderInfoList.isEmpty()) return;
    //performUpLoadOrderInfo(upLoadOrderInfoList);
    performUploadSingleOrder(upLoadOrderInfoList);
  }

  //@formatter:off
  private void upLoad() {
    //没网
    if (!NetUtils.isConnected(App.getContext())) {
      EventBus.getDefault().post(new UploadOrderEvent("网络异常,请检查网络!"));
      return;
    }
    //第一次上传
    int noUploadCount = (int) DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
        .where(PxOrderInfoDao.Properties.IsUpload.eq(false))
        .count();
    //反结账的
    int reverseCount = (int) DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .whereOr(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_CANCEL), PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
        .where(PxOrderInfoDao.Properties.IsReversed.eq(PxOrderInfo.REVERSE_TRUE))
        .where(PxOrderInfoDao.Properties.IsUpload.eq(true))
        .where(PxOrderInfoDao.Properties.IsUploadReverse.eq(false))
        .count();
    int uploadSize = (noUploadCount + LIMIT - 1) / LIMIT;
    int reverseSize = (reverseCount + LIMIT - 1) / LIMIT;
    //最大限制
    uploadSize = uploadSize > 5 ? 5 : uploadSize;
    reverseSize = reverseSize > 5 ? 5 : reverseSize;
    int totalSize = uploadSize + reverseSize;
    if (totalSize == 0) {
      EventBus.getDefault().post(new UploadOrderEvent("没有可上传的订单!"));
      return;
    }
    //置换上传状态
    isUploading = true;

    ExecutorService threadPool = Executors.newFixedThreadPool(totalSize);
    CyclicBarrier barrier = new CyclicBarrier(totalSize, new Result());
    mResultList = new CopyOnWriteArrayList<>();

    //第一次上传的数据
    for (int i = 0; i < uploadSize; i++) {
      List<PxOrderInfo> list = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
          .where(PxOrderInfoDao.Properties.IsUpload.eq(false))
          .offset(LIMIT * i)
          .limit(LIMIT)
          .list();
      if (list.isEmpty()) {
        mResultList.add("没有可上传的订单!");
      } else {
        threadPool.execute(new Upload(barrier, list));
      }
    }

    //反结账上传的数据
    for (int i = 0; i < reverseSize; i++) {
      List<PxOrderInfo> list = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .whereOr(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_CANCEL), PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
          .where(PxOrderInfoDao.Properties.IsReversed.eq(PxOrderInfo.REVERSE_TRUE))
          .where(PxOrderInfoDao.Properties.IsUpload.eq(true))
          .where(PxOrderInfoDao.Properties.IsUploadReverse.eq(false))
          .offset(LIMIT * i)
          .limit(LIMIT)
          .list();
      if (list.isEmpty()) {
        mResultList.add("没有可上传的订单!");
      }else {
        threadPool.execute(new Upload(barrier, list));
      }
    }
  }

  //@formatter:on
  //结果
  class Result implements Runnable {
    @Override public void run() {
      //汇总结果
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < mResultList.size(); i++) {
        sb.append(mResultList.get(i));
        if (i != mResultList.size() - 1) {
          sb.append("\n");
        }
      }
      EventBus.getDefault().post(new UploadOrderEvent(sb.toString()).setResult(ORDER_SUCCESS));
      //恢复状态
      isUploading = false;
    }
  }

  //具体上传
  class Upload implements Runnable {

    private List<PxOrderInfo> mOrderInfoList;
    private CyclicBarrier mBarrier;

    public Upload(CyclicBarrier barrier, List<PxOrderInfo> list) {
      this.mOrderInfoList = list;
      this.mBarrier = barrier;
    }

    @Override public void run() {
      List<UpLoadPxOrderInfo> upLoadOrderInfoList = getUpLoadOrderInfoList(mOrderInfoList);
      if (upLoadOrderInfoList.isEmpty()) {
        mResultList.add("没有可上传的订单!");
      } else {
        //上传
        performUpLoadOrderInfo(upLoadOrderInfoList);
      }
      try {
        mBarrier.await();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 上传订单信息
   */
  //@formatter:on
  private void performUpLoadOrderInfo(final List<UpLoadPxOrderInfo> upLoadOrderInfoList) {

    User loginUser = UserUtils.getLoginUser();
    if (loginUser == null) {
      mResultList.add(NULL_USER);
      return;
    }

    //上传订单
    HttpOrderReq req = new HttpOrderReq();
    req.setCompanyCode(loginUser.getCompanyCode());
    req.setUserId(loginUser.getObjectId());
    req.setList(upLoadOrderInfoList);

    new RestClient(RestClient.SYNC_CLIENT, 0, 1000, 60000, 10000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i(responseString + "");
        if (throwable instanceof SocketTimeoutException) {

          mResultList.add("响应超时,请稍后重试!");
        } else {
          mResultList.add("连接超时,请检查网络");
        }
      }

      @Override protected void success(String responseString) {
        Logger.i("" + responseString);
        Gson gson = new Gson();
        HttpOrderResp resp = gson.fromJson(responseString, HttpOrderResp.class);
        //标记为已上传
        if (resp.getStatusCode() == 1) {
          ////全部成功
          //List<String> successOrderList = resp.getSuccessOrderList();
          //if (successOrderList.size() == upLoadOrderInfoList.size()) {
          //  saveAllIsUpLoadToTrue(upLoadOrderInfoList);
          //} else {//部分成功
          //  savePartIsUploadToTrue(successOrderList);
          //}
          saveAllIsUpLoadToTrue(upLoadOrderInfoList);
          mResultList.add("上传成功");
        } else {
          mResultList.add("" + resp.getMsg());
        }
      }
    }.postOther(App.getContext(), URLConstants.UPLOAD_ORDER, req);
  }

  /**
   * 结账完毕 及时上传单个订单 不收集结果
   */
  //@formatter:off
  private void performUploadSingleOrder(final List<UpLoadPxOrderInfo> upLoadOrderInfoList) {
    User loginUser = UserUtils.getLoginUser();
    if (loginUser == null) return;

    //上传订单
    HttpOrderReq req = new HttpOrderReq();
    req.setCompanyCode(loginUser.getCompanyCode());
    req.setUserId(loginUser.getObjectId());
    req.setList(upLoadOrderInfoList);

    new RestClient(RestClient.SYNC_CLIENT, 0, 1000, 60000, 10000) {
      @Override protected void start() {}

      @Override protected void finish() {}

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i(responseString + "");
      }

      @Override protected void success(String responseString) {
        Logger.i(responseString + "");
        Gson gson = new Gson();
        HttpOrderResp resp = gson.fromJson(responseString, HttpOrderResp.class);
        //标记为已上传
        if (resp.getStatusCode() == 1) {
          ////全部成功
          //List<String> successOrderList = resp.getSuccessOrderList();
          //if (successOrderList.size() == upLoadOrderInfoList.size()) {
          //} else {//部分成功
          //  savePartIsUploadToTrue(successOrderList);
          //}
          saveAllIsUpLoadToTrue(upLoadOrderInfoList);
        }
      }
    }.postOther(App.getContext(), URLConstants.UPLOAD_ORDER, req);
  }

  /**
   * 全部成功保存上传状态为true
   */
  // @formatter:off
  private void saveAllIsUpLoadToTrue(List<UpLoadPxOrderInfo> upLoadOrderInfoList) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      for (UpLoadPxOrderInfo upLoadPxOrderInfo : upLoadOrderInfoList) {
        PxOrderInfo dbOrderInfo = DaoServiceUtil.getOrderInfoService()
            .queryBuilder()
            .where(PxOrderInfoDao.Properties.IsReserveOrder.eq(PxOrderInfo.IS_REVERSE_ORDER_FALSE))
            .where(PxOrderInfoDao.Properties.OrderNo.eq(upLoadPxOrderInfo.getOrderNo())) // 订单请求号查找数据库
            .unique();
        if (dbOrderInfo != null) {
          dbOrderInfo.setIsUpload(true);
          dbOrderInfo.setIsUploadReverse(true);
          DaoServiceUtil.getOrderInfoService().update(dbOrderInfo);
        }
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }
  /**
   * 部分成功的
   */
  private void savePartIsUploadToTrue(List<String> successOrderList) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      List<PxOrderInfo> dbOrderList = new ArrayList<>();
      for (String orderNo : successOrderList) {
        PxOrderInfo dbOrder = DaoServiceUtil.getOrderInfoService()
            .queryBuilder()
            .where(PxOrderInfoDao.Properties.OrderNo.eq(orderNo))
            .unique();
        if (dbOrder != null){
          dbOrderList.add(dbOrder);
        }
      }
      if (!dbOrderList.isEmpty()) {
        DaoServiceUtil.getOrderInfoService().update(dbOrderList);
      }
      db.setTransactionSuccessful();
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      db.endTransaction();
    }
  }

  /**
   * 需要上传的订单list
   */
  private List<UpLoadPxOrderInfo> getUpLoadOrderInfoList(List<PxOrderInfo> dbOrderInfoList) {
    List<UpLoadPxOrderInfo> upLoadPxOrderInfoList = new ArrayList<>();
    for (PxOrderInfo orderInfo : dbOrderInfoList) {
      UpLoadPxOrderInfo upLoadOrderInfo = getUpLoadOrderInfo(orderInfo);
      //null情况 订单下有自定义商品且未上传成功的+有使用会员卡且会员未上传成功的
      if (upLoadOrderInfo != null) {
        upLoadPxOrderInfoList.add(upLoadOrderInfo);
      }
    }
    return upLoadPxOrderInfoList;
  }

  /**
   * 需要上传的单个订单
   */
  //@formatter:off
  private UpLoadPxOrderInfo getUpLoadOrderInfo(PxOrderInfo dbOrderInfo) {
    if (dbOrderInfo == null) return null;
    UpLoadPxOrderInfo info = new UpLoadPxOrderInfo();//构建需要的orderInfo
    //开单服务员ID
    User waiter = dbOrderInfo.getDbWaiter();
    //服务生为空 传user的ID
    info.setWaiterId((waiter == null) ? dbOrderInfo.getDbUser().getObjectId() : waiter.getObjectId());
    info.setCurrentUser(info.getWaiterId());
    info.setCompanyId(BTPrintConstants.office.getObjectId());
    info.setCode(dbOrderInfo.getOrderNo());
    //桌台
    if (dbOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)){
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(dbOrderInfo.getId()))
        .unique();
       PxTableInfo dbTable = unique.getDbTable();
       info.setTable(dbTable);
    } else {
      info.setTable(null);
    }
    //找零
    double totalChange = dbOrderInfo.getTotalChange();
    info.setTotalChange(totalChange);
    //订单总价
    info.setTotalPrice(dbOrderInfo.getTotalPrice());
    info.setAccountReceivable(dbOrderInfo.getAccountReceivable());
    info.setOrderNo(dbOrderInfo.getOrderNo());
    info.setOrderReqNo(dbOrderInfo.getOrderReqNo());
    //服务器真实价格 = 收银端真实价 - 总找零
    //info.setRealPrice(dbOrderInfo.getRealPrice() -dbOrderInfo.getTotalChange());
    info.setRealPrice(dbOrderInfo.getRealPrice());
    info.setDiscountPrice(dbOrderInfo.getDiscountPrice());
    info.setPayType(dbOrderInfo.getPayType());
    info.setStatus(dbOrderInfo.getStatus());
    info.setTailMoney(dbOrderInfo.getTailMoney());
    info.setTail((dbOrderInfo.getTailMoney() - 0) > 0 ? UpLoadPxOrderInfo.TAIL_YES : UpLoadPxOrderInfo.TAIL_NO);//用抹零金额判断是否抹零
    //dbOrderInfo.getVipCardPay()> 0.0
    info.setUseVipCard(dbOrderInfo.getUseVipCard());
    info.setFree((dbOrderInfo.getTailMoney() - dbOrderInfo.getAccountReceivable()) > 0 ? "1" : "0");// 抹零金额减去应收金额是否免单(0:否 1:是)
    info.setExtraMoney(dbOrderInfo.getExtraMoney());
    info.setComplementMoney(dbOrderInfo.getComplementMoney());
    //订单结束时间
    info.setOrderEndTime(dbOrderInfo.getEndTime().getTime());
    //反结账
    String isReversed = dbOrderInfo.getIsReversed();
    info.setIsReversed(isReversed == null ? "0" : "1");
    //type
    info.setType(dbOrderInfo.getType());
    //支付类优惠
    double dbOrderInfoPayPrivilege = dbOrderInfo.getPayPrivilege();
    info.setPayPrivilege(dbOrderInfoPayPrivilege);

    //合并上传订单详情
    List<UpLoadPxOrderDetails> uploadDetailsList = mergeDetails(dbOrderInfo);
    info.setPxOrderDetailsList(uploadDetailsList);

    //支付信息
    List<PxPayInfo> payInfoList = DaoServiceUtil.getPayInfoService()
        .queryBuilder()
        .where(PxPayInfoDao.Properties.PxOrderInfoId.eq(dbOrderInfo.getId()))
        .list();
    if (payInfoList!= null) {
      List<UploadPayInfo> uploadPayInfoList = new ArrayList<>();
        for (PxPayInfo payInfo : payInfoList) {
          UploadPayInfo uploadPayInfo = new UploadPayInfo();
          String type = payInfo.getPaymentType();
          //支付时间
          uploadPayInfo.setPayTime(payInfo.getPayTime().getTime());
          //实收
          double received = payInfo.getReceived();
          uploadPayInfo.setReceived(new BigDecimal(received));
          //找零
          double change = payInfo.getChange();
          uploadPayInfo.setChange(new BigDecimal(change));
          //支付方式ID
          uploadPayInfo.setPaymentId(payInfo.getPaymentId());
          if (payInfo.getVoucherCode() != null){
            //凭证吗 银行卡
            uploadPayInfo.setVoucherCode(payInfo.getVoucherCode());
          }
          if (PxPaymentMode.TYPE_VIP.equals(type) || PxPaymentMode.TYPE_ALIPAY.equals(type) || PxPaymentMode.TYPE_WEIXIN.equals(type)){ //在线支付需要流水号
            //支付宝 微信 会员卡
            uploadPayInfo.setTradeNo(payInfo.getTradeNo());
          }
          //支付方式类型
          uploadPayInfo.setPaymentType(payInfo.getPaymentType());
          //支付方式名称
          uploadPayInfo.setPaymentName(payInfo.getPaymentName());
          //是否计算入销售额(0:是 1：否)
          uploadPayInfo.setSalesAmount(payInfo.getSalesAmount());
          //支付类优惠
          double payPrivilege = payInfo.getPayPrivilege() == null ? 0 : payInfo.getPayPrivilege();
          uploadPayInfo.setPayPrivilege(payPrivilege);
          //验券码
          uploadPayInfo.setTicketCode(payInfo.getTicketCode());
          uploadPayInfoList.add(uploadPayInfo);
        }
       info.setPayInfoList(uploadPayInfoList);
    }
    return info;
  }

  //@formatter:off

  /**
   * 合并已下单的上传详情
   * orderStatus = 已下单
   * inCombo = null / "0"
   */
  private List<UpLoadPxOrderDetails> mergeDetails(PxOrderInfo orderInfo) {
    List<UpLoadPxOrderDetails> mergeDetailsList = new ArrayList<>();
    SQLiteDatabase db = DaoServiceUtil.getOrderDetailsDao().getDatabase();
    //Product、Format、Method、num、multiNum、status、gift、unitPrice、discPrice、discountRate
    Cursor cursor = db.rawQuery("Select PX_PRODUCT_INFO_ID,PX_FORMAT_INFO_ID,PX_METHOD_INFO_ID,"
            + "sum(NUM) as N,SUM(MULTIPLE_UNIT_NUMBER) AS MN,STATUS,IS_GIFT,UNIT_PRICE,DISCOUNT_RATE,sum(PRICE),sum(VIP_PRICE),sum(FINAL_PRICE)"
            + " From OrderDetails"
            + " Where PX_ORDER_INFO_ID = " + orderInfo.getId()
            + " And ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER
            + " And IN_COMBO = '0'"
            + " Group by PX_PRODUCT_INFO_ID,PX_FORMAT_INFO_ID,PX_METHOD_INFO_ID,STATUS,IS_GIFT,UNIT_PRICE,DISCOUNT_RATE",null);
    while (cursor.moveToNext()) {
      UpLoadPxOrderDetails uploadDetails = new UpLoadPxOrderDetails();
      int prodId = cursor.getInt(0);
      int formatId = cursor.getInt(1);
      int methodId = cursor.getInt(2);
      double num = cursor.getDouble(3);
      double multiNum = cursor.getDouble(4);
      String status = cursor.getString(5);
      String gift = cursor.getString(6);
      double unitPrice = cursor.getDouble(7);
      int discountRate = cursor.getInt(8);
      double price = cursor.getDouble(9);
      double vipPrice = cursor.getDouble(10);
      double finalPrice = cursor.getDouble(11);
      //查询
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.Id.eq(prodId))
          .unique();
      PxFormatInfo formatInfo = DaoServiceUtil.getFormatInfoService()
          .queryBuilder()
          .where(PxFormatInfoDao.Properties.Id.eq(formatId))
          .unique();
      PxMethodInfo methodInfo = DaoServiceUtil.getMethodInfoService()
          .queryBuilder()
          .where(PxMethodInfoDao.Properties.Id.eq(methodId))
          .unique();

      //规格Rel
      PxProductFormatRel productFormatRel = null;
      //formatName
      uploadDetails.setFormatName("");
      if (formatInfo != null) {
        productFormatRel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(formatInfo.getId()))
            .unique();
        uploadDetails.setFormatName(formatInfo.getName());
      }
      //做法Ref
      PxProductMethodRef methodRef = null;
      if (methodInfo != null) {
        methodRef = DaoServiceUtil.getProductMethodRelService()
            .queryBuilder()
            .where(PxProductMethodRefDao.Properties.PxMethodInfoId.eq(methodInfo.getId()))
            .where(PxProductMethodRefDao.Properties.PxProductInfoId.eq(methodInfo.getId()))
            .unique();
      }

      //构造上传的ProductInfo
      UpLoadProduct upLoadProduct = new UpLoadProduct();
      upLoadProduct.setId(productInfo.getObjectId());
      upLoadProduct.setName(productInfo.getName());
      PxProductCategory dbCategory = DaoServiceUtil.getProductCategoryService()
          .queryBuilder()
          .where(PxProductCategoryDao.Properties.Id.eq(productInfo.getPxProductCategoryId()))
          .unique();
      upLoadProduct.setCategory(dbCategory);
      upLoadProduct.setChangePrice(productInfo.getChangePrice());
      upLoadProduct.setIsDiscount(productInfo.getIsDiscount());
      upLoadProduct.setIsGift(productInfo.getIsGift());
      upLoadProduct.setCode(productInfo.getCode());
      upLoadProduct.setMultipleUnit(productInfo.getMultipleUnit());
      upLoadProduct.setVipPrice(productInfo.getVipPrice());
      upLoadProduct.setUnit(productInfo.getUnit());
      upLoadProduct.setOrderUnit(productInfo.getOrderUnit());
      upLoadProduct.setStatus(productInfo.getStatus());
      upLoadProduct.setPrice(productInfo.getPrice());
      upLoadProduct.setPy(productInfo.getPy());
      upLoadProduct.setIsPrint(productInfo.getIsPrint());
      uploadDetails.setProduct(upLoadProduct);
      //是双单位?
      boolean isMultiUnit = (PxProductInfo.IS_TWO_UNIT_TURE).equals(productInfo.getMultipleUnit());

      uploadDetails.setNum(num);
      uploadDetails.setMultiNum(multiNum);
      uploadDetails.setStatus(status);
      uploadDetails.setDiscountRate(discountRate);
      // 是否打折（0：是 1：否  ）
      uploadDetails.setIsDiscount((discountRate == 100) ? "1" : "0");//TODO
      uploadDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_ORDER);
      //是否赠品
      uploadDetails.setIsGift(gift);
      //规格
      if (productFormatRel != null) {
        uploadDetails.setProductFormatRel(productFormatRel);
      }
      //做法
      if (methodRef != null) {
        uploadDetails.setProductMethodRef(methodRef);
      }
      //productName
      uploadDetails.setProductName(productInfo.getName());

      //finalPrice
      if (isMultiUnit) {
        BigDecimal multiBg = new BigDecimal(finalPrice / multiNum);
        uploadDetails.setFinalMultiPrice(multiBg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
      }
      BigDecimal bg = new BigDecimal(finalPrice / num);
      uploadDetails.setFinalPrice(bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
      //originPrice
      uploadDetails.setOriginPrice(unitPrice);
      //price  赠品  直接价格为0
      boolean isGift = PxOrderDetails.GIFT_TRUE.endsWith(gift);
      Double realPrice = (productFormatRel != null) ? productFormatRel.getPrice() : productInfo.getPrice();
      uploadDetails.setPrice(isGift ? 0 : realPrice);
      //加优惠金额
      double discPrice = getDiscPrice(orderInfo, price, vipPrice, discountRate);
      uploadDetails.setDiscPrice(discPrice);

      mergeDetailsList.add(uploadDetails);
    }
    IOUtils.closeCloseables(cursor);
    return mergeDetailsList;
  }

  /**
   * discPrice
   */
  private double getDiscPrice(PxOrderInfo orderInfo, double price, double vipPrice,
      int discountRate) {
    if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(orderInfo.getUseVipCard())) {
      return NumberFormatUtils.formatFloatAndParse(((price - vipPrice * (double) discountRate / 100)));
    } else {
      return NumberFormatUtils.formatFloatAndParse(price * (1 - (double) discountRate / 100));
    }
  }
}