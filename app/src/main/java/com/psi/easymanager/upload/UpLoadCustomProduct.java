package com.psi.easymanager.upload;

import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.PxProductConfigPlanRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxProductConfigPlanRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.upload.module.UpLoadProduct;
import com.psi.easymanager.upload.module.UpLoadProductConfigRel;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpProductReq;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.utils.NetUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: ylw
 * Date: 2016-06-14
 * Time: 14:45
 * 上传自定义商品
 */
public class UpLoadCustomProduct {
  // @formatter:off
  private App mApp;
  private String mCompanyCode;
  private String mUserId;
  private boolean isUploading = false;//是否有任务正在上传
  //单一线程
  private  ExecutorService sDbEngine = null;//单线程上传
  // @formatter:on
  private UpLoadCustomProduct() {
    initBasicValue();
  }

  private void initBasicValue() {
    mApp = (App) App.getContext();
    User user = mApp.getUser();
    if (user != null) {
      mUserId = user.getObjectId();
      mCompanyCode = user.getCompanyCode();
    }
  }

  public static UpLoadCustomProduct getInstance() {
    return UploadCustomHolder.sInstance;
  }

  private static class UploadCustomHolder {
    private static final UpLoadCustomProduct sInstance = new UpLoadCustomProduct();
  }

  private void getThread() {
    if (sDbEngine == null) {
      sDbEngine = Executors.newSingleThreadExecutor();
    }
  }

  /**
   * 对外上传单个的
   */
  public void upLoadSingleProd(final PxProductInfo productInfo) {
    getThread();
    sDbEngine.execute(new Runnable() {
      @Override public void run() {
        upLoad(productInfo);
      }
    });
  }

  private void upLoad(PxProductInfo productInfo) {
    if (productInfo == null) return;//有任务在进行 不上传
    //配菜方案List
    List<PxProductConfigPlanRel> planRelList = DaoServiceUtil.getProductConfigPlanRelService()
        .queryBuilder()
        .where(PxProductConfigPlanRelDao.Properties.DelFlag.eq("0"))
        .where(PxProductConfigPlanRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
        .list();
    List<UpLoadProductConfigRel> upLoadPlanRelList = new ArrayList<>();
    for (PxProductConfigPlanRel rel : planRelList) {
      upLoadPlanRelList.add(getUpLoadPlanRel(rel));
    }
    //获得需要上传的自定义商品并且上传
    UpLoadProduct upLoadCustomProductInfo = getUpLoadCustomProductInfo(productInfo);
    performUploadCustomProduct(productInfo, upLoadCustomProductInfo, upLoadPlanRelList);
  }

  /**
   * 上传多个
   */
  public void upLoadProdList() {
    if (isUploading) return;
    getThread();
    sDbEngine.execute(new Runnable() {
      @Override public void run() {
        upLoad();
      }
    });
  }

  private void upLoad() {
    List<PxProductInfo> dbProdList;
    try {
      dbProdList = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
          .where(PxProductInfoDao.Properties.IsCustom.eq(true))
          .where(PxProductInfoDao.Properties.IsUpLoad.eq(false))
          .list();
    } catch (Exception e) {
      e.printStackTrace();
      Logger.i(e.toString());
      return;
    }
    if (dbProdList == null || dbProdList.size() == 0) {
      return;
    }
    for (PxProductInfo productInfo : dbProdList) {
      //配菜方案List
      List<PxProductConfigPlanRel> planRelList = DaoServiceUtil.getProductConfigPlanRelService()
          .queryBuilder()
          .where(PxProductConfigPlanRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductConfigPlanRelDao.Properties.PxProductInfoId.eq(productInfo.getId()))
          .list();
      List<UpLoadProductConfigRel> upLoadPlanRelList = new ArrayList<>();
      for (PxProductConfigPlanRel rel : planRelList) {
        upLoadPlanRelList.add(getUpLoadPlanRel(rel));
      }
      //获得需要上传的自定义商品并且上传
      UpLoadProduct upLoadCustomProductInfo = getUpLoadCustomProductInfo(productInfo);
      performUploadCustomProduct(productInfo, upLoadCustomProductInfo, upLoadPlanRelList);
    }
  }

  /**
   * 需要上传的自定义商品
   */
  private UpLoadProduct getUpLoadCustomProductInfo(PxProductInfo productInfo) {
    UpLoadProduct customProductInfo = new UpLoadProduct();
    customProductInfo.setId(productInfo.getObjectId());
    customProductInfo.setName(productInfo.getName());
    customProductInfo.setCategory(productInfo.getDbCategory());
    customProductInfo.setPy(productInfo.getPy());
    customProductInfo.setCode(productInfo.getCode());
    customProductInfo.setPrice(productInfo.getPrice());
    customProductInfo.setVipPrice(productInfo.getPrice());
    customProductInfo.setMultipleUnit(productInfo.getMultipleUnit());
    customProductInfo.setUnit(productInfo.getUnit());
    customProductInfo.setOrderUnit(productInfo.getOrderUnit());
    customProductInfo.setIsDiscount(productInfo.getIsDiscount());
    customProductInfo.setIsGift(UpLoadProduct.COMMODITY);
    customProductInfo.setIsPrint(UpLoadProduct.ALLOW_PRINT);
    customProductInfo.setChangePrice(productInfo.getChangePrice());
    customProductInfo.setStatus(UpLoadProduct.NORMAL);
    return customProductInfo;
  }

  /**
   * planRel
   */
  private UpLoadProductConfigRel getUpLoadPlanRel(PxProductConfigPlanRel rel) {
    UpLoadProductConfigRel configRel = new UpLoadProductConfigRel();
    configRel.setConfigPlan(rel.getDbProductConfigPlan());
    configRel.setProduct(rel.getDbProduct());
    configRel.setId(rel.getObjectId());
    return configRel;
  }

  /**
   * 上传自定义商品
   *
   * @param dbProduct 数据库中product
   * @param upLoadProduct 需要上传的product
   */
  private void performUploadCustomProduct(final PxProductInfo dbProduct,
      UpLoadProduct upLoadProduct, List<UpLoadProductConfigRel> planRelList) {
    if (mCompanyCode == null || mUserId == null) {
      initBasicValue();
    }
    if (mCompanyCode == null || mUserId == null) {
      return;
    }
    //没网
    if (!NetUtils.isConnected(mApp)) {
      return;
    }
    isUploading = true;
    HttpProductReq productReq = new HttpProductReq();
    productReq.setCompanyCode(mCompanyCode);
    productReq.setUserId(mUserId);
    productReq.setProductInfo(upLoadProduct);
    productReq.setProductConfigPlannRelList(planRelList);

    new RestClient(RestClient.SYNC_CLIENT, 0, 1000, 15000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.i("Fireupload_fail" + "--responseBody:" + responseString);
        isUploading = false;
      }

      @Override protected void success(String responseString) {
        Gson gson = new Gson();
        HttpResp resp = gson.fromJson(responseString, HttpResp.class);
        Logger.i(
            "Fireupload_succ" + "--responseBody:" + responseString + "====" + resp.getStatusCode());
        //标记为已上传
        if (resp.getStatusCode() == 1) {
          saveResponse(dbProduct, responseString);
        }
        isUploading = false;
      }
    }.postOther(mApp, URLConstants.UPLOAD_PRODUCT, productReq);
  }

  /**
   * 解析json保存自定义商品已上传成功
   */
  private void saveResponse(PxProductInfo dbProduct, String responseString) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      dbProduct.setIsUpLoad(true);
      DaoServiceUtil.getProductInfoService().update(dbProduct);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 关闭线程
   */
  public void closePool() {
    if (sDbEngine != null) {
      sDbEngine.shutdown();
      sDbEngine = null;
    }
  }
}