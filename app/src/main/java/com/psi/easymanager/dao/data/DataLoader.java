package com.psi.easymanager.dao.data;

import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.OfficeDao;
import com.psi.easymanager.dao.PxAlipayInfoDao;
import com.psi.easymanager.dao.PxBestpayDao;
import com.psi.easymanager.dao.PxBusinessHoursDao;
import com.psi.easymanager.dao.PxBuyCouponsDao;
import com.psi.easymanager.dao.PxComboGroupDao;
import com.psi.easymanager.dao.PxComboProductRelDao;
import com.psi.easymanager.dao.PxDiscounSchemeDao;
import com.psi.easymanager.dao.PxExtraChargeDao;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxOptReasonDao;
import com.psi.easymanager.dao.PxPaymentModeDao;
import com.psi.easymanager.dao.PxPrinterInfoDao;
import com.psi.easymanager.dao.PxProductCategoryDao;
import com.psi.easymanager.dao.PxProductConfigPlanDao;
import com.psi.easymanager.dao.PxProductConfigPlanRelDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.PxProductMethodRefDao;
import com.psi.easymanager.dao.PxProductRemarksDao;
import com.psi.easymanager.dao.PxPromotioDetailsDao;
import com.psi.easymanager.dao.PxPromotioInfoDao;
import com.psi.easymanager.dao.PxRechargePlanDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.PxTableInfoDao;
import com.psi.easymanager.dao.PxVipInfoDao;
import com.psi.easymanager.dao.PxVoucherDao;
import com.psi.easymanager.dao.PxWeiXinpayDao;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxAlipayInfo;
import com.psi.easymanager.module.PxBestpay;
import com.psi.easymanager.module.PxBusinessHours;
import com.psi.easymanager.module.PxBuyCoupons;
import com.psi.easymanager.module.PxComboGroup;
import com.psi.easymanager.module.PxComboProductRel;
import com.psi.easymanager.module.PxDiscounScheme;
import com.psi.easymanager.module.PxExtraCharge;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOptReason;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductConfigPlan;
import com.psi.easymanager.module.PxProductConfigPlanRel;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductMethodRef;
import com.psi.easymanager.module.PxProductRemarks;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.module.PxPromotioInfo;
import com.psi.easymanager.module.PxRechargePlan;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.PxTableExtraRel;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.module.PxVoucher;
import com.psi.easymanager.module.PxWeiXinpay;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.DataSync;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.utils.SPUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjq on 2016/4/6.
 */
public class DataLoader {

  //商品list
  private static List<PxProductInfo> sProductInfoList = new ArrayList<>();
  //附加费桌台关系list
  private static List<PxTableExtraRel> sTableExtraRelList = new ArrayList<>();
  //促销详情list
  private static List<PxPromotioDetails> sPromDetailsList = new ArrayList<>();
  //商品和规格关系list
  private static List<PxProductFormatRel> sProdFormatRelList = new ArrayList<>();
  //商品和做法关系list
  private static List<PxProductMethodRef> sProdMethodRelList = new ArrayList<>();
  //配菜方案 在打印机之后
  private static List<PxProductConfigPlan> sProductConfigPlanList = new ArrayList<>();
  //配菜方案Rel  在商品和配菜方案之后
  private static List<PxProductConfigPlanRel> sProductConfigPlanRelList = new ArrayList<>();
  //套餐分组list
  private static List<PxComboGroup> sComboGroupList = new ArrayList<>();
  //套餐分组和商品rel
  private static List<PxComboProductRel> sComboProdRelList = new ArrayList<>();
  //团购券list
  private static List<PxBuyCoupons> sBuyCouponsList = new ArrayList<>();

  //单例
  private static DataLoader sDataLoader;

  //@formatter:on
  public static DataLoader getInstance() {
    if (sDataLoader == null) {
      sDataLoader = new DataLoader();
    }
    //商品list
    sProductInfoList = new ArrayList<>();
    //促销信息和桌台关系list
    sTableExtraRelList = new ArrayList<>();
    //促销详情list
    sPromDetailsList = new ArrayList<>();
    //商品和规格关系list
    sProdFormatRelList = new ArrayList<>();
    //商品和做法关系list
    sProdMethodRelList = new ArrayList<>();
    //配菜方案
    sProductConfigPlanList = new ArrayList<>();
    //配菜方案关系list
    sProductConfigPlanRelList = new ArrayList<>();
    //套餐分组list
    sComboGroupList = new ArrayList<>();
    //套餐分组和商品关系list
    sComboProdRelList = new ArrayList<>();
    //团购券list
    sBuyCouponsList = new ArrayList<>();

    return sDataLoader;
  }

  /**
   * 储存数据
   */
  public void saveData(List<DataSync> dataList) {
    if (dataList == null || dataList.size() == 0) return;
    SQLiteDatabase db = DaoServiceUtil.getOfficeDao().getDatabase();
    db.beginTransaction();
    try {
      for (DataSync dataSync : dataList) {
        Logger.i(new Gson().toJson(dataSync));
        operateData(dataSync);
      }
      //连接商品和分类
      linkProInfoAndProCate();
      //连接桌台和附加费
      linkTableAndExtra();
      //连接促销详情和促销信息、商品
      linkPromotion();
      //连接商品和规格信息
      linkProdAndFormat();
      //连接商品和做法信息
      linkProdAndMethod();
      //连接配菜方案 打印机之后
      linkProductConfigPlanAndPrinter();
      //连接配菜方案关系 商品和配菜方案之后
      linkProductConfigPlanRelAndProduct();
      //连接商品和套餐分组
      linkProdAndCombo();
      //连接套餐分组和商品
      linkComboAndProd();
      //连接团购券和支付方式
      linkBuyCouponsAndPaymentMode();
      //修改sp，是否初始化
      SPUtils.put(App.getContext(), Constants.IS_INIT, false);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Logger.e("拉取数据异常:" + e.toString());
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 具体操作
   */
  private void operateData(DataSync dataSync) {
    String simpleClassName = dataSync.getSimpleClassName();
    switch (simpleClassName) {
      case "PxProductCategory"://商品分类
        operatePxProductCategory(dataSync);
        break;
      case "PxProductInfo"://商品信息
        operatePxProductInfo(dataSync);
        break;
      case "PxTableInfo"://桌台
        operatePxTableInfo(dataSync);
        break;
      case "User"://用户
        operateUser(dataSync);
        break;
      case "Office"://公司
        operateOffice(dataSync);
        break;
      case "PxDiscounScheme"://打折方案
        operateDiscountScheme(dataSync);
        break;
      case "PxPrinterInfo"://打印机信息
        operatePrinterInfo(dataSync);
        break;
      case "PxExtraCharge"://附加费信息
        operateExtraCharge(dataSync);
        break;
      case "PxTableExtraRel"://附加费桌台关联
        operateTableExtraRel(dataSync);
        break;
      case "PxPromotioInfo"://促销信息
        operatePromotionInfo(dataSync);
        break;
      case "PxPromotioDetails"://促销详情
        operatePromotionDetails(dataSync);
        break;
      case "PxFormatInfo"://规格
        operateFormatInfo(dataSync);
        break;
      case "PxProductFormatRel"://商品规格引用关系
        operateProductFormatRel(dataSync);
        break;
      case "PxMethodInfo"://做法
        operateMethodInfo(dataSync);
        break;
      case "PxProductMethodRef"://商品做法引用关系
        operateProductMethodRel(dataSync);
        break;
      case "PxRechargePlan"://充值计划
        operateRechargePlan(dataSync);
        break;
      case "PxProductConfigPlannRel"://配菜方案关系
        operateProductConfigPlanRel(dataSync);
        break;
      case "PxProductConfigPlan":// 配菜方案
        operateProductConfigPlan(dataSync);
        break;
      case "PxBestpay":// 翼支付
        //operateBestpay(dataSync);
        break;
      case "PxVipInfo"://会员
        operatePxVipInfo(dataSync);
        break;
      case "PxBusinessHours"://营业时间
        operateBusinessHours(dataSync);
        break;
      case "PxOptReason"://操作原因
        operateOptReason(dataSync);
        break;
      case "PxWeiXinpay"://微信支付配置参数
        operateWeiXinpay(dataSync);
        break;
      case "PxAlipayInfo"://支付宝商户信息
        operateAlipayInfo(dataSync);
        break;
      case "PxComboGroup"://套餐分组
        operateComboGroup(dataSync);
        break;
      case "PxComboProductRel"://套餐分组和商品rel
        operateComboProductRel(dataSync);
        break;
      case "PxProductRemarks"://商品备注
        operateProdRemarks(dataSync);
        break;
      case "PxVoucher"://优惠券
        operateVoucher(dataSync);
        break;
      case "PxPaymentMode"://支付方式
        operatePaymentMode(dataSync);
        break;
      case "PxBuyCoupons"://团购券
        operatePxBuyCoupon(dataSync);
        break;
      case "PxTableArea"://桌台区域
        operateTableArea(dataSync);
        break;
    }
  }

  /**
   * 连接商品和分类
   */
  private void linkProInfoAndProCate() {
    for (PxProductInfo productInfo : sProductInfoList) {
      for (PxProductCategory category : DaoServiceUtil.getProductCategoryDao().loadAll()) {
        //该商品所属分类id
        if (productInfo.getCategory() == null) continue;
        String proCateId = productInfo.getCategory().getObjectId();
        //分类id
        String cateId = category.getObjectId();
        //如果两个id相等，则表示此商品属于该分类
        if (!proCateId.equals(cateId)) continue;
        productInfo.setDbCategory(category);
        DaoServiceUtil.getProductInfoService().update(productInfo);
      }
    }
  }

  /**
   * 连接桌台和附加费
   */
  private void linkTableAndExtra() {
    for (PxTableExtraRel rel : sTableExtraRelList) {
      String tableObjId = rel.getTable().getObjectId();
      String extraObjId = rel.getExtra().getObjectId();
      PxTableInfo dbTable = DaoServiceUtil.getTableInfoService()
          .queryBuilder()
          .where(PxTableInfoDao.Properties.ObjectId.eq(tableObjId))
          .unique();
      PxExtraCharge dbExtra = DaoServiceUtil.getExtraChargeService()
          .queryBuilder()
          .where(PxExtraChargeDao.Properties.ObjectId.eq(extraObjId))
          .unique();
      if (dbTable != null && dbExtra != null) {
        rel.setDbTable(dbTable);
        rel.setDbExtraCharge(dbExtra);
        DaoServiceUtil.getTableExtraRelService().update(rel);
      }
    }
  }

  /**
   * 连接促销详情和促销信息、商品
   */
  private void linkPromotion() {
    for (PxPromotioDetails promDetails : sPromDetailsList) {
      String prodObjId = promDetails.getProduct().getObjectId();
      String promInfoObjId = promDetails.getPromotio().getObjectId();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(prodObjId))
          .unique();
      PxPromotioInfo promotioInfo = DaoServiceUtil.getPromotionInfoService()
          .queryBuilder()
          .where(PxPromotioInfoDao.Properties.ObjectId.eq(promInfoObjId))
          .unique();

      if (productInfo != null && promotioInfo != null) {
        promDetails.setDbProduct(productInfo);
        promDetails.setDbPromotio(promotioInfo);
        //Format 可能存在
        PxFormatInfo formatInfo = promDetails.getFormat();
        if (formatInfo != null) {
          PxFormatInfo dbFormat = DaoServiceUtil.getFormatInfoService()
              .queryBuilder()
              .where(PxFormatInfoDao.Properties.ObjectId.eq(formatInfo.getObjectId()))
              .unique();
          promDetails.setDbFormat(dbFormat);
        }

        DaoServiceUtil.getPromotionDetailsService().update(promDetails);
      }
    }
  }

  /**
   * 连接商品和规格
   */
  private void linkProdAndFormat() {
    for (PxProductFormatRel rel : sProdFormatRelList) {
      String prodObjId = rel.getProduct().getObjectId();
      String formatObjId = rel.getFormat().getObjectId();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(prodObjId))
          .unique();
      PxFormatInfo formatInfo = DaoServiceUtil.getFormatInfoService()
          .queryBuilder()
          .where(PxFormatInfoDao.Properties.ObjectId.eq(formatObjId))
          .unique();
      if (productInfo != null && formatInfo != null) {
        rel.setDbProduct(productInfo);
        rel.setDbFormat(formatInfo);
        DaoServiceUtil.getProductFormatRelService().saveOrUpdate(rel);
      }
      //商品添加规格后,恢复商品状态和余量
      if (productInfo != null) {
        productInfo.setStatus(PxProductInfo.STATUS_ON_SALE);
        productInfo.setOverPlus(null);
        DaoServiceUtil.getProductInfoService().update(productInfo);
      }
    }
  }

  /**
   * 连接商品和做法
   */
  private void linkProdAndMethod() {
    for (PxProductMethodRef rel : sProdMethodRelList) {
      String prodObjId = rel.getProduct().getObjectId();
      String methodObjId = rel.getMethod().getObjectId();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(prodObjId))
          .unique();
      PxMethodInfo methodInfo = DaoServiceUtil.getMethodInfoService()
          .queryBuilder()
          .where(PxMethodInfoDao.Properties.ObjectId.eq(methodObjId))
          .unique();
      if (productInfo != null && methodInfo != null) {
        rel.setDbProduct(productInfo);
        rel.setDbMethod(methodInfo);
        DaoServiceUtil.getProductMethodRelService().update(rel);
      }
    }
  }

  /**
   * 连接配菜方案和打印机  在打印机有之后
   */
  private void linkProductConfigPlanAndPrinter() {
    for (PxProductConfigPlan configPlan : sProductConfigPlanList) {
      PxProductConfigPlan dbConfigPlan = DaoServiceUtil.getProductConfigPlanService()
          .queryBuilder()
          .where(PxProductConfigPlanDao.Properties.ObjectId.eq(configPlan.getObjectId()))
          .unique();
      String printerObjId = configPlan.getPrinter().getObjectId();
      PxPrinterInfo printerInfo = DaoServiceUtil.getPrinterInfoService()
          .queryBuilder()
          .where(PxPrinterInfoDao.Properties.ObjectId.eq(printerObjId))
          .unique();
      if (printerInfo != null) {
        dbConfigPlan.setDbPrinter(printerInfo);
        DaoServiceUtil.getProductConfigPlanService().update(dbConfigPlan);
      }
    }
  }

  /**
   * 连接配菜方案Rel和商品 在配菜方案和商品后
   */
  private void linkProductConfigPlanRelAndProduct() {
    for (PxProductConfigPlanRel configPlanRel : sProductConfigPlanRelList) {
      PxProductConfigPlanRel dbConfigPlanRel = DaoServiceUtil.getProductConfigPlanRelService()
          .queryBuilder()
          .where(PxProductConfigPlanRelDao.Properties.ObjectId.eq(configPlanRel.getObjectId()))
          .unique();

      String configPlanObjId = configPlanRel.getConfigPlan().getObjectId();
      String productObjId = configPlanRel.getProduct().getObjectId();
      PxProductConfigPlan productConfigPlan = DaoServiceUtil.getProductConfigPlanService()
          .queryBuilder()
          .where(PxProductConfigPlanDao.Properties.ObjectId.eq(configPlanObjId))
          .unique();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(productObjId))
          .unique();
      if (productConfigPlan != null && productInfo != null) {
        dbConfigPlanRel.setDbProduct(productInfo);
        dbConfigPlanRel.setDbProductConfigPlan(productConfigPlan);
        DaoServiceUtil.getProductConfigPlanRelService().update(dbConfigPlanRel);
      }
    }
  }

  /**
   * 连接商品和套餐分组
   */
  private void linkProdAndCombo() {
    for (PxComboGroup comboGroup : sComboGroupList) {
      PxProductInfo comboId = comboGroup.getComboId();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.DelFlag.eq("0"))
          .where(PxProductInfoDao.Properties.ObjectId.eq(comboId.getObjectId()))
          .unique();
      if (productInfo != null) {
        comboGroup.setDbCombo(productInfo);
        DaoServiceUtil.getComboGroupService().saveOrUpdate(comboGroup);
      }
    }
  }

  /**
   * 连接套餐分组和商品
   */
  private void linkComboAndProd() {
    for (PxComboProductRel rel : sComboProdRelList) {
      String comboGroupObjId = rel.getGroupId().getObjectId();
      String prodObjId = rel.getProductId().getObjectId();
      PxComboGroup comboGroup = DaoServiceUtil.getComboGroupService()
          .queryBuilder()
          .where(PxComboGroupDao.Properties.ObjectId.eq(comboGroupObjId))
          .unique();
      PxProductInfo productInfo = DaoServiceUtil.getProductInfoService()
          .queryBuilder()
          .where(PxProductInfoDao.Properties.ObjectId.eq(prodObjId))
          .unique();
      if (rel.getFormatId() != null) {
        String formatObjId = rel.getFormatId().getObjectId();
        PxFormatInfo formatInfo = DaoServiceUtil.getFormatInfoService()
            .queryBuilder()
            .where(PxFormatInfoDao.Properties.ObjectId.eq(formatObjId))
            .unique();
        rel.setDbFormat(formatInfo);
      }
      if (comboGroup != null && productInfo != null) {
        rel.setDbComboGroup(comboGroup);
        rel.setDbProduct(productInfo);
        DaoServiceUtil.getComboProdRelService().saveOrUpdate(rel);
      }
    }
  }

  /**
   * 连接团购券和支付方式
   */
  private void linkBuyCouponsAndPaymentMode() {
    for (PxBuyCoupons buyCoupons : sBuyCouponsList) {
      String objectId = buyCoupons.getPxPaymentMode().getObjectId();
      PxPaymentMode paymentMode = DaoServiceUtil.getPaymentModeService()
          .queryBuilder()
          .where(PxPaymentModeDao.Properties.ObjectId.eq(objectId))
          .unique();
      if (paymentMode != null) {
        buyCoupons.setDbPayment(paymentMode);
        DaoServiceUtil.getPxBuyCouponsService().saveOrUpdate(buyCoupons);
      }
    }
  }

  /**
   * 公用的转化方法
   */
  public String getDataJson(DataSync dataSync) {
    //objData不能向下转型
    Object objData = dataSync.getData();//数据
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    String dataJson = gson.toJson(objData);
    return dataJson;
  }

  /**
   * 用户转化方法
   */
  public String[] getDataJsonByUserCompany(DataSync dataSync) {
    //objData不能向下转型
    Object objData = dataSync.getData();
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    String dataJson = gson.toJson(objData);
    String companyCode = dataSync.getCompanyCode();
    String[] strings = new String[2];
    strings[0] = dataJson;
    strings[1] = companyCode;
    return strings;
  }

  /**
   * 商品分类
   */
  private void operatePxProductCategory(DataSync dataSync) {
    int opt = dataSync.getOpt(); //操作符 (新增，修改，删除)
    String dataJson = getDataJson(dataSync);
    PxProductCategory category = RestClient.getGson().fromJson(dataJson, PxProductCategory.class);
    switch (opt) {
      case DataSync.INSERT:
        PxProductCategory cate = DaoServiceUtil.getProductCategoryService()
            .queryBuilder()
            .where(PxProductCategoryDao.Properties.ObjectId.eq(category.getObjectId()))
            .unique();
        if (cate == null) {
          if (category.getType() == null) {
            category.setType(PxProductCategory.TYPE_ORDINARY);
          }
          category.setDelFlag("0");
          DaoServiceUtil.getProductCategoryService().saveOrUpdate(category);
        }
        break;
      case DataSync.UPDATE:
        PxProductCategory exist = DaoServiceUtil.getProductCategoryService()
            .queryBuilder()
            .where(PxProductCategoryDao.Properties.ObjectId.eq(category.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setName(category.getName());
          exist.setCode(category.getCode());
          exist.setOrderNo(category.getOrderNo());
          exist.setVersion(category.getVersion());
          exist.setParentId(category.getParentId());
          exist.setLeaf(category.getLeaf());
          exist.setType(category.getType());
          exist.setShelf(category.getShelf());
          exist.setVisible(category.getVisible());
          exist.setDelFlag("0");
          DaoServiceUtil.getProductCategoryService().update(exist);
        } else {
          category.setDelFlag("0");
          DaoServiceUtil.getProductCategoryService().saveOrUpdate(category);
        }
        break;
      case DataSync.DELETE:
        PxProductCategory del = DaoServiceUtil.getProductCategoryService()
            .queryBuilder()
            .where(PxProductCategoryDao.Properties.ObjectId.eq(category.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getProductCategoryService().update(del);
        }
        break;
    }
  }

  /**
   * 商品信息
   */
  private void operatePxProductInfo(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxProductInfo productInfo = RestClient.getGson().fromJson(dataJson, PxProductInfo.class);
    productInfo.setIsCustom(false);
    switch (opt) {
      case DataSync.INSERT:
        PxProductInfo prod = DaoServiceUtil.getProductInfoService()
            .queryBuilder()
            .where(PxProductInfoDao.Properties.ObjectId.eq(productInfo.getObjectId()))
            .unique();
        if (prod == null) {
          if (productInfo.getType() == null) {
            productInfo.setType(PxProductInfo.TYPE_ORIGINAL);
          }
          productInfo.setDelFlag("0");
          DaoServiceUtil.getProductInfoService().save(productInfo);
          sProductInfoList.add(productInfo);
        }
        break;
      case DataSync.UPDATE:
        PxProductInfo exist = DaoServiceUtil.getProductInfoService()
            .queryBuilder()
            .where(PxProductInfoDao.Properties.ObjectId.eq(productInfo.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setIsPrint(productInfo.getIsPrint());
          exist.setName(productInfo.getName());
          exist.setPy(productInfo.getPy());
          exist.setCode(productInfo.getCode());
          exist.setPrice(productInfo.getPrice());
          exist.setVipPrice(productInfo.getVipPrice());
          exist.setUnit(productInfo.getUnit());
          exist.setMultipleUnit(productInfo.getMultipleUnit());
          exist.setOrderUnit(productInfo.getOrderUnit());
          exist.setIsDiscount(productInfo.getIsDiscount());
          exist.setIsGift(productInfo.getIsGift());
          exist.setChangePrice(productInfo.getChangePrice());
          exist.setStatus(productInfo.getStatus());
          exist.setBarCode(productInfo.getBarCode());
          exist.setType(productInfo.getType());
          exist.setShelf(productInfo.getShelf());
          exist.setVisible(productInfo.getVisible());
          exist.setDelFlag("0");
          DaoServiceUtil.getProductInfoService().update(exist);
          //设置分类,添加到list，更新关联
          exist.setCategory(productInfo.getCategory());
          sProductInfoList.add(exist);
        } else {
          productInfo.setDelFlag("0");
          DaoServiceUtil.getProductInfoService().save(productInfo);
          sProductInfoList.add(productInfo);
        }
        break;
      case DataSync.DELETE:
        PxProductInfo del = DaoServiceUtil.getProductInfoService()
            .queryBuilder()
            .where(PxProductInfoDao.Properties.ObjectId.eq(productInfo.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getProductInfoService().update(del);
        }
        break;
    }
  }

  /**
   * 桌台
   */
  private void operatePxTableInfo(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxTableInfo tableInfo = RestClient.getGson().fromJson(dataJson, PxTableInfo.class);
    switch (opt) {
      case DataSync.INSERT:
        PxTableInfo table = DaoServiceUtil.getTableInfoService()
            .queryBuilder()
            .where(PxTableInfoDao.Properties.ObjectId.eq(tableInfo.getObjectId()))
            .unique();
        if (table == null) {
          tableInfo.setDelFlag("0");
          DaoServiceUtil.getTableInfoService().saveOrUpdate(tableInfo);
        }
        break;
      case DataSync.UPDATE:
        PxTableInfo exist = DaoServiceUtil.getTableInfoService()
            .queryBuilder()
            .where(PxTableInfoDao.Properties.ObjectId.eq(tableInfo.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setCode(tableInfo.getCode());
          exist.setName(tableInfo.getName());
          exist.setType(tableInfo.getType());
          exist.setPeopleNum(tableInfo.getPeopleNum());
          exist.setCode(tableInfo.getCode());
          exist.setSortNo(tableInfo.getSortNo());
          exist.setDelFlag("0");
          DaoServiceUtil.getTableInfoService().update(exist);
        } else {
          tableInfo.setDelFlag("0");
          DaoServiceUtil.getTableInfoService().saveOrUpdate(tableInfo);
        }
        break;
      case DataSync.DELETE:
        PxTableInfo del = DaoServiceUtil.getTableInfoService()
            .queryBuilder()
            .where(PxTableInfoDao.Properties.ObjectId.eq(tableInfo.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getTableInfoService().update(del);
        }
        break;
    }
  }

  /**
   * 用户
   */
  private void operateUser(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String[] dataJsonByUser = getDataJsonByUserCompany(dataSync);
    User user = RestClient.getGson().fromJson(dataJsonByUser[0], User.class);
    user.setCompanyCode(dataJsonByUser[1]);
    switch (opt) {
      case DataSync.INSERT:
        User u = DaoServiceUtil.getUserService()
            .queryBuilder()
            .where(UserDao.Properties.ObjectId.eq(user.getObjectId()))
            .unique();
        if (u == null) {
          user.setDelFlag("0");
          DaoServiceUtil.getUserService().saveOrUpdate(user);
        }
        break;
      case DataSync.UPDATE:
        User exist = DaoServiceUtil.getUserService()
            .queryBuilder()
            .where(UserDao.Properties.ObjectId.eq(user.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setLoginName(user.getLoginName());
          exist.setPassword(user.getPassword());
          exist.setNo(user.getNo());
          exist.setName(user.getName());
          exist.setEmail(user.getEmail());
          exist.setPhone(user.getPhone());
          exist.setMobile(user.getMobile());
          exist.setUserType(user.getUserType());
          exist.setLoginIp(user.getLoginIp());
          exist.setLoginDate(user.getLoginDate());
          exist.setLoginFlag(user.getLoginFlag());
          exist.setPhoto(user.getPhoto());
          exist.setOldLoginName(user.getOldLoginName());
          exist.setNewPassword(user.getNewPassword());
          exist.setOldLoginIp(user.getOldLoginIp());
          exist.setOldLoginDate(user.getOldLoginDate());
          exist.setCompanyCode(user.getCompanyCode());
          exist.setMaxTail(user.getMaxTail());
          exist.setInitPassword(user.getInitPassword());
          exist.setImUserName(user.getImUserName());
          exist.setDelFlag("0");
          exist.setCanRetreat(user.getCanRetreat());
          DaoServiceUtil.getUserService().update(exist);
          //清空sp设置
          if (SPUtils.get(App.getContext(), Constants.SAVE_LOGIN_NAME, "")
              .equals(user.getLoginName())) {
            if (!SPUtils.get(App.getContext(), Constants.SAVE_LOGIN_PWD, "")
                .equals(user.getPassword())) {
              //更新用户 删除前信息
              SPUtils.put(App.getContext(), Constants.REMEMBER_PWD, false);
              SPUtils.put(App.getContext(), Constants.SAVE_LOGIN_NAME, "");
              SPUtils.put(App.getContext(), Constants.SAVE_LOGIN_PWD, "");
            }
          }
        } else {
          user.setDelFlag("0");
          DaoServiceUtil.getUserService().saveOrUpdate(user);
        }
        break;
      case DataSync.DELETE:
        User del = DaoServiceUtil.getUserService()
            .queryBuilder()
            .where(UserDao.Properties.ObjectId.eq(user.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getUserService().update(del);
          //删除用户 删除前信息
          if (SPUtils.get(App.getContext(), Constants.SAVE_LOGIN_NAME, "")
              .equals(user.getLoginName())) {
            SPUtils.put(App.getContext(), Constants.REMEMBER_PWD, false);
            SPUtils.put(App.getContext(), Constants.SAVE_LOGIN_NAME, "");
            SPUtils.put(App.getContext(), Constants.SAVE_LOGIN_PWD, "");
          }
        }
        break;
    }
  }

  /**
   * 公司
   */
  private void operateOffice(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String[] dataJsonByOffice = getDataJsonByUserCompany(dataSync);
    Office office = RestClient.getGson().fromJson(dataJsonByOffice[0], Office.class);
    if (office.getCode() == null) {
      office.setCode(dataJsonByOffice[1]);
    }
    switch (opt) {
      case DataSync.INSERT:
        Office o = DaoServiceUtil.getOfficeService()
            .queryBuilder()
            .where(OfficeDao.Properties.ObjectId.eq(office.getObjectId()))
            .unique();
        if (o == null) {
          DaoServiceUtil.getOfficeService().saveOrUpdate(office);
        }
        break;
      case DataSync.UPDATE:
        Office exist = DaoServiceUtil.getOfficeService()
            .queryBuilder()
            .where(OfficeDao.Properties.ObjectId.eq(office.getObjectId()))
            .unique();
        if (exist != null) {

          exist.setParentIds(office.getParentIds());
          exist.setParentId(office.getParentId());
          exist.setName(office.getName());
          exist.setCode(office.getCode());
          exist.setType(office.getType());
          exist.setGrade(office.getGrade());
          exist.setAddress(office.getAddress());
          exist.setZipCode(office.getZipCode());
          exist.setMaster(office.getMaster());
          exist.setPhone(office.getPhone());
          exist.setFax(office.getFax());
          exist.setEmail(office.getEmail());
          exist.setUseable(office.getUseable());
          exist.setLogo(office.getLogo());
          exist.setGroupId(office.getGroupId());
          exist.setInitPassword(office.getInitPassword());
          exist.setCode(office.getCode());
          DaoServiceUtil.getOfficeService().update(exist);
        } else {
          DaoServiceUtil.getOfficeService().saveOrUpdate(office);
        }
        break;
      case DataSync.DELETE:

        break;
    }
  }

  /**
   * 打折方案
   */
  private void operateDiscountScheme(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxDiscounScheme discounScheme = RestClient.getGson().fromJson(dataJson, PxDiscounScheme.class);
    switch (opt) {
      case DataSync.INSERT:
        PxDiscounScheme discScheme = DaoServiceUtil.getDiscounSchemeService()
            .queryBuilder()
            .where(PxDiscounSchemeDao.Properties.ObjectId.eq(discounScheme.getObjectId()))
            .unique();
        if (discScheme == null) {
          discounScheme.setDelFlag("0");
          DaoServiceUtil.getDiscounSchemeService().saveOrUpdate(discounScheme);
        }
        break;
      case DataSync.UPDATE:
        PxDiscounScheme exist = DaoServiceUtil.getDiscounSchemeService()
            .queryBuilder()
            .where(PxDiscounSchemeDao.Properties.ObjectId.eq(discounScheme.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setName(discounScheme.getName());
          exist.setRate(discounScheme.getRate());
          exist.setType(discounScheme.getType());
          exist.setDelFlag("0");
          DaoServiceUtil.getDiscounSchemeService().update(exist);
        } else {
          discounScheme.setDelFlag("0");
          DaoServiceUtil.getDiscounSchemeService().saveOrUpdate(discounScheme);
        }
        break;
      case DataSync.DELETE:
        PxDiscounScheme del = DaoServiceUtil.getDiscounSchemeService()
            .queryBuilder()
            .where(PxDiscounSchemeDao.Properties.ObjectId.eq(discounScheme.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getDiscounSchemeService().update(del);
        }
        break;
    }
  }

  /**
   * 打印机信息
   */
  private void operatePrinterInfo(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxPrinterInfo printerInfo = RestClient.getGson().fromJson(dataJson, PxPrinterInfo.class);
    switch (opt) {
      case DataSync.INSERT:
        PxPrinterInfo p = DaoServiceUtil.getPrinterInfoService()
            .queryBuilder()
            .where(PxPrinterInfoDao.Properties.ObjectId.eq(printerInfo.getObjectId()))
            .unique();
        if (p == null) {
          printerInfo.setDelFlag("0");
          //默认未连接
          printerInfo.setIsConnected(PxPrinterInfo.UNCONNECTED);
          DaoServiceUtil.getPrinterInfoService().saveOrUpdate(printerInfo);
        }
        break;
      case DataSync.UPDATE:
        PxPrinterInfo exist = DaoServiceUtil.getPrinterInfoService()
            .queryBuilder()
            .where(PxPrinterInfoDao.Properties.ObjectId.eq(printerInfo.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setFormat(printerInfo.getFormat());
          exist.setCode(printerInfo.getCode());
          exist.setIpAddress(printerInfo.getIpAddress());
          exist.setType(printerInfo.getType());
          exist.setStatus(printerInfo.getStatus());
          exist.setName(printerInfo.getName());
          exist.setDelFlag("0");
          DaoServiceUtil.getPrinterInfoService().update(exist);
        } else {
          printerInfo.setDelFlag("0");
          DaoServiceUtil.getPrinterInfoService().saveOrUpdate(printerInfo);
        }
        break;
      case DataSync.DELETE:
        PxPrinterInfo del = DaoServiceUtil.getPrinterInfoService()
            .queryBuilder()
            .where(PxPrinterInfoDao.Properties.ObjectId.eq(printerInfo.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getPrinterInfoService().update(del);
        }
        break;
    }
  }

  /**
   * 附加费信息
   */
  private void operateExtraCharge(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxExtraCharge extraCharge = RestClient.getGson().fromJson(dataJson, PxExtraCharge.class);
    switch (opt) {
      case DataSync.INSERT:
        PxExtraCharge e = DaoServiceUtil.getExtraChargeService()
            .queryBuilder()
            .where(PxExtraChargeDao.Properties.ObjectId.eq(extraCharge.getObjectId()))
            .unique();
        if (e == null) {
          extraCharge.setDelFlag("0");
          DaoServiceUtil.getExtraChargeService().saveOrUpdate(extraCharge);
        }
        break;
      case DataSync.UPDATE:
        PxExtraCharge exist = DaoServiceUtil.getExtraChargeService()
            .queryBuilder()
            .where(PxExtraChargeDao.Properties.ObjectId.eq(extraCharge.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setName(extraCharge.getName());
          exist.setServiceCharge(extraCharge.getServiceCharge());
          exist.setServiceType(extraCharge.getServiceType());
          exist.setMinConsume(extraCharge.getMinConsume());
          exist.setServiceStatus(extraCharge.getServiceStatus());
          exist.setConsumeStatus(extraCharge.getConsumeStatus());
          exist.setMinutes(extraCharge.getMinutes());
          exist.setDelFlag("0");
          DaoServiceUtil.getExtraChargeService().update(exist);
        } else {
          extraCharge.setDelFlag("0");
          DaoServiceUtil.getExtraChargeService().saveOrUpdate(extraCharge);
        }
        break;
      case DataSync.DELETE:
        PxExtraCharge del = DaoServiceUtil.getExtraChargeService()
            .queryBuilder()
            .where(PxExtraChargeDao.Properties.ObjectId.eq(extraCharge.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getExtraChargeService().update(del);
        }
        break;
    }
  }

  /**
   * 桌台附加费信息
   */
  private void operateTableExtraRel(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxTableExtraRel pxTableExtraRel =
        RestClient.getGson().fromJson(dataJson, PxTableExtraRel.class);
    switch (opt) {
      case DataSync.INSERT:
        PxTableExtraRel te = DaoServiceUtil.getTableExtraRelService()
            .queryBuilder()
            .where(PxTableExtraRelDao.Properties.ObjectId.eq(pxTableExtraRel.getObjectId()))
            .unique();
        if (te == null) {
          pxTableExtraRel.setDelFlag("0");
          sTableExtraRelList.add(pxTableExtraRel);
          DaoServiceUtil.getTableExtraRelService().saveOrUpdate(pxTableExtraRel);
        }
        break;
      case DataSync.UPDATE:
        PxTableExtraRel exist = DaoServiceUtil.getTableExtraRelService()
            .queryBuilder()
            .where(PxTableExtraRelDao.Properties.ObjectId.eq(pxTableExtraRel.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setTable(pxTableExtraRel.getTable());
          exist.setExtra(pxTableExtraRel.getExtra());
          exist.setDelFlag("0");
          sTableExtraRelList.add(exist);
        } else {
          pxTableExtraRel.setDelFlag("0");
          sTableExtraRelList.add(pxTableExtraRel);
          DaoServiceUtil.getTableExtraRelService().saveOrUpdate(pxTableExtraRel);
        }
        break;
      case DataSync.DELETE:
        PxTableExtraRel del = DaoServiceUtil.getTableExtraRelService()
            .queryBuilder()
            .where(PxTableExtraRelDao.Properties.ObjectId.eq(pxTableExtraRel.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getTableExtraRelService().update(del);
        }
        break;
    }
  }

  /**
   * 促销信息
   */
  private void operatePromotionInfo(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    Gson sGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
        .setDateFormat("yyyy-MM-dd")
        .create();
    PxPromotioInfo pxPromotioInfo = sGson.fromJson(dataJson, PxPromotioInfo.class);
    switch (opt) {
      case DataSync.INSERT:
        PxPromotioInfo promotio = DaoServiceUtil.getPromotionInfoService()
            .queryBuilder()
            .where(PxPromotioInfoDao.Properties.ObjectId.eq(pxPromotioInfo.getObjectId()))
            .unique();
        if (promotio == null) {
          pxPromotioInfo.setDelFlag("0");
          DaoServiceUtil.getPromotionInfoService().saveOrUpdate(pxPromotioInfo);
        }
        break;
      case DataSync.UPDATE:
        PxPromotioInfo exist = DaoServiceUtil.getPromotionInfoService()
            .queryBuilder()
            .where(PxPromotioInfoDao.Properties.ObjectId.eq(pxPromotioInfo.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setDelFlag("0");
          exist.setName(pxPromotioInfo.getName());
          exist.setCode(pxPromotioInfo.getCode());
          exist.setEndTime(pxPromotioInfo.getEndTime());
          exist.setStartTime(pxPromotioInfo.getStartTime());
          exist.setStartDate(pxPromotioInfo.getStartDate());
          exist.setEndDate(pxPromotioInfo.getEndDate());
          exist.setType(pxPromotioInfo.getType());
          exist.setWeekly(pxPromotioInfo.getWeekly());
          DaoServiceUtil.getPromotionInfoService().update(exist);
        } else {
          pxPromotioInfo.setDelFlag("0");
          DaoServiceUtil.getPromotionInfoService().saveOrUpdate(pxPromotioInfo);
        }
        break;
      case DataSync.DELETE:
        PxPromotioInfo del = DaoServiceUtil.getPromotionInfoService()
            .queryBuilder()
            .where(PxPromotioInfoDao.Properties.ObjectId.eq(pxPromotioInfo.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getPromotionInfoService().update(del);
        }
        break;
    }
  }

  /**
   * 促销详情
   */
  private void operatePromotionDetails(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxPromotioDetails pxPromotioDetails =
        RestClient.getGson().fromJson(dataJson, PxPromotioDetails.class);
    switch (opt) {
      case DataSync.INSERT:
        PxPromotioDetails insert = DaoServiceUtil.getPromotionDetailsService()
            .queryBuilder()
            .where(PxPromotioDetailsDao.Properties.ObjectId.eq(pxPromotioDetails.getObjectId()))
            .unique();
        if (insert == null) {
          pxPromotioDetails.setDelFlag("0");
          sPromDetailsList.add(pxPromotioDetails);
          DaoServiceUtil.getPromotionDetailsService().saveOrUpdate(pxPromotioDetails);
        }
        break;
      case DataSync.UPDATE:
        PxPromotioDetails exist = DaoServiceUtil.getPromotionDetailsService()
            .queryBuilder()
            .where(PxPromotioDetailsDao.Properties.ObjectId.eq(pxPromotioDetails.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setDelFlag("0");
          exist.setPromotionalPrice(pxPromotioDetails.getPromotionalPrice());
          DaoServiceUtil.getPromotionDetailsService().saveOrUpdate(exist);
          //
          exist.setPromotio(pxPromotioDetails.getPromotio());
          exist.setProduct(pxPromotioDetails.getProduct());
          exist.setFormat(pxPromotioDetails.getFormat());
          sPromDetailsList.add(exist);
        } else {
          pxPromotioDetails.setDelFlag("0");
          sPromDetailsList.add(pxPromotioDetails);
          DaoServiceUtil.getPromotionDetailsService().saveOrUpdate(pxPromotioDetails);
        }
        break;
      case DataSync.DELETE:
        PxPromotioDetails del = DaoServiceUtil.getPromotionDetailsService()
            .queryBuilder()
            .where(PxPromotioDetailsDao.Properties.ObjectId.eq(pxPromotioDetails.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getPromotionDetailsService().saveOrUpdate(del);
        }
        break;
    }
  }

  /**
   * 规格信息
   */
  private void operateFormatInfo(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxFormatInfo formatInfo = RestClient.getGson().fromJson(dataJson, PxFormatInfo.class);
    switch (opt) {
      case DataSync.INSERT:
        PxFormatInfo f = DaoServiceUtil.getFormatInfoService()
            .queryBuilder()
            .where(PxFormatInfoDao.Properties.ObjectId.eq(formatInfo.getObjectId()))
            .unique();
        if (f == null) {
          formatInfo.setDelFlag("0");
          DaoServiceUtil.getFormatInfoService().save(formatInfo);
        }
        break;
      case DataSync.UPDATE:
        PxFormatInfo exist = DaoServiceUtil.getFormatInfoService()
            .queryBuilder()
            .where(PxFormatInfoDao.Properties.ObjectId.eq(formatInfo.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setName(formatInfo.getName());
          DaoServiceUtil.getFormatInfoService().update(exist);
        } else {
          formatInfo.setDelFlag("0");
          DaoServiceUtil.getFormatInfoService().save(formatInfo);
        }
        break;
      case DataSync.DELETE:
        PxFormatInfo existDel = DaoServiceUtil.getFormatInfoService()
            .queryBuilder()
            .where(PxFormatInfoDao.Properties.ObjectId.eq(formatInfo.getObjectId()))
            .unique();
        //假删除
        if (existDel != null) {
          existDel.setDelFlag("1");
          DaoServiceUtil.getFormatInfoService().update(existDel);
        }
        break;
    }
  }

  /**
   * 商品规格引用关系
   */
  //@formatter:on
  private void operateProductFormatRel(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxProductFormatRel pxProductFormatRel =
        RestClient.getGson().fromJson(dataJson, PxProductFormatRel.class);
    PxProductFormatRel exist = DaoServiceUtil.getProductFormatRelService()
        .queryBuilder()
        .where(PxProductFormatRelDao.Properties.ObjectId.eq(pxProductFormatRel.getObjectId()))
        .unique();
    switch (opt) {
      case DataSync.INSERT:
        if (exist == null) {
          pxProductFormatRel.setDelFlag("0");
          DaoServiceUtil.getProductFormatRelService().save(pxProductFormatRel);
          sProdFormatRelList.add(pxProductFormatRel);
        }
        break;
      case DataSync.UPDATE:
        if (exist != null) {
          exist.setPrice(pxProductFormatRel.getPrice());
          exist.setVipPrice(pxProductFormatRel.getVipPrice());
          exist.setBarCode(pxProductFormatRel.getBarCode());
          exist.setDelFlag("0");
          DaoServiceUtil.getProductFormatRelService().saveOrUpdate(exist);
        } else {
          pxProductFormatRel.setDelFlag("0");
          DaoServiceUtil.getProductFormatRelService().save(pxProductFormatRel);
          sProdFormatRelList.add(pxProductFormatRel);
        }
        break;
      case DataSync.DELETE:
        //假删除
        if (exist != null) {
          exist.setDelFlag("1");
          DaoServiceUtil.getProductFormatRelService().update(exist);
        }
        break;
    }
  }

  /**
   * 做法信息
   */
  private void operateMethodInfo(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxMethodInfo methodInfo = RestClient.getGson().fromJson(dataJson, PxMethodInfo.class);
    switch (opt) {
      case DataSync.INSERT:
        PxMethodInfo m = DaoServiceUtil.getMethodInfoService()
            .queryBuilder()
            .where(PxMethodInfoDao.Properties.ObjectId.eq(methodInfo.getObjectId()))
            .unique();
        if (m == null) {
          methodInfo.setDelFlag("0");
          DaoServiceUtil.getMethodInfoService().save(methodInfo);
        }
        break;
      case DataSync.UPDATE:
        PxMethodInfo exist = DaoServiceUtil.getMethodInfoService()
            .queryBuilder()
            .where(PxMethodInfoDao.Properties.ObjectId.eq(methodInfo.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setName(methodInfo.getName());
          exist.setDelFlag("0");
          DaoServiceUtil.getMethodInfoService().update(exist);
        } else {
          methodInfo.setDelFlag("0");
          DaoServiceUtil.getMethodInfoService().save(methodInfo);
        }
        break;
      case DataSync.DELETE:
        PxMethodInfo existDel = DaoServiceUtil.getMethodInfoService()
            .queryBuilder()
            .where(PxMethodInfoDao.Properties.ObjectId.eq(methodInfo.getObjectId()))
            .unique();
        //假删除
        if (existDel != null) {
          existDel.setDelFlag("1");
          DaoServiceUtil.getMethodInfoService().update(existDel);
        }
        break;
    }
  }

  /**
   * 商品和做法引用关系
   */
  private void operateProductMethodRel(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxProductMethodRef pxProductMethodRef =
        RestClient.getGson().fromJson(dataJson, PxProductMethodRef.class);
    switch (opt) {
      case DataSync.INSERT:
        PxProductMethodRef ref = DaoServiceUtil.getProductMethodRelService()
            .queryBuilder()
            .where(PxProductMethodRefDao.Properties.ObjectId.eq(pxProductMethodRef.getObjectId()))
            .unique();
        if (ref == null) {
          pxProductMethodRef.setDelFlag("0");
          DaoServiceUtil.getProductMethodRelService().save(pxProductMethodRef);
          sProdMethodRelList.add(pxProductMethodRef);
        }
        break;
      case DataSync.UPDATE:
        PxProductMethodRef exist = DaoServiceUtil.getProductMethodRelService()
            .queryBuilder()
            .where(PxProductMethodRefDao.Properties.ObjectId.eq(pxProductMethodRef.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setDelFlag("0");
          DaoServiceUtil.getProductMethodRelService().saveOrUpdate(exist);
        } else {
          pxProductMethodRef.setDelFlag("0");
          DaoServiceUtil.getProductMethodRelService().save(pxProductMethodRef);
          sProdMethodRelList.add(pxProductMethodRef);
        }
        break;
      case DataSync.DELETE:
        PxProductMethodRef unique = DaoServiceUtil.getProductMethodRelService()
            .queryBuilder()
            .where(PxProductMethodRefDao.Properties.ObjectId.eq(pxProductMethodRef.getObjectId()))
            .unique();
        //假删除
        if (unique != null) {
          unique.setDelFlag("1");
          DaoServiceUtil.getProductMethodRelService().update(unique);
        }
        break;
    }
  }

  /**
   * 充值计划
   */
  private void operateRechargePlan(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxRechargePlan rechargePlan = RestClient.getGson().fromJson(dataJson, PxRechargePlan.class);
    switch (opt) {
      case DataSync.INSERT:
        PxRechargePlan r = DaoServiceUtil.getRechargePlanService()
            .queryBuilder()
            .where(PxRechargePlanDao.Properties.ObjectId.eq(rechargePlan.getObjectId()))
            .unique();
        if (r == null) {
          rechargePlan.setDelFlag("0");
          DaoServiceUtil.getRechargePlanService().saveOrUpdate(rechargePlan);
        }
        break;
      case DataSync.UPDATE:
        PxRechargePlan exist = DaoServiceUtil.getRechargePlanService()
            .queryBuilder()
            .where(PxRechargePlanDao.Properties.ObjectId.eq(rechargePlan.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setName(rechargePlan.getName());
          exist.setMoney(rechargePlan.getMoney());
          exist.setLargess(rechargePlan.getLargess());
          exist.setDelFlag("0");
          DaoServiceUtil.getRechargePlanService().update(exist);
        } else {
          rechargePlan.setDelFlag("0");
          DaoServiceUtil.getRechargePlanService().saveOrUpdate(rechargePlan);
        }
        break;
      case DataSync.DELETE:
        PxRechargePlan del = DaoServiceUtil.getRechargePlanService()
            .queryBuilder()
            .where(PxRechargePlanDao.Properties.ObjectId.eq(rechargePlan.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getRechargePlanService().update(del);
        }
        break;
    }
  }

  /**
   * 配菜方案  有打印机信息之后
   */
  private void operateProductConfigPlan(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxProductConfigPlan configPlan =
        RestClient.getGson().fromJson(dataJson, PxProductConfigPlan.class);
    switch (opt) {
      case DataSync.INSERT:
        PxProductConfigPlan p = DaoServiceUtil.getProductConfigPlanService()
            .queryBuilder()
            .where(PxProductConfigPlanDao.Properties.ObjectId.eq(configPlan.getObjectId()))
            .unique();
        if (p == null) {
          configPlan.setDelFlag("0");
          DaoServiceUtil.getProductConfigPlanService().save(configPlan);
          sProductConfigPlanList.add(configPlan);
        }
        break;
      case DataSync.UPDATE:
        PxProductConfigPlan exist = DaoServiceUtil.getProductConfigPlanService()
            .queryBuilder()
            .where(PxProductConfigPlanDao.Properties.ObjectId.eq(configPlan.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setCount(configPlan.getCount());
          exist.setName(configPlan.getName());
          exist.setFlag(configPlan.getFlag());
          exist.setDelFlag("0");
          DaoServiceUtil.getProductConfigPlanService().saveOrUpdate(exist);
          sProductConfigPlanList.add(configPlan);
        } else {
          configPlan.setDelFlag("0");
          DaoServiceUtil.getProductConfigPlanService().save(configPlan);
          sProductConfigPlanList.add(configPlan);
        }
        break;
      case DataSync.DELETE:
        PxProductConfigPlan delete = DaoServiceUtil.getProductConfigPlanService()
            .queryBuilder()
            .where(PxProductConfigPlanDao.Properties.ObjectId.eq(configPlan.getObjectId()))
            .unique();
        if (delete != null) {
          delete.setDelFlag("1");
          DaoServiceUtil.getProductConfigPlanService().saveOrUpdate(delete);
        }
        break;
    }
  }

  /**
   * 配菜方案关系 商品和配菜方案存储后
   */
  private void operateProductConfigPlanRel(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxProductConfigPlanRel configPlanRel =
        RestClient.getGson().fromJson(dataJson, PxProductConfigPlanRel.class);
    switch (opt) {
      case DataSync.INSERT:
        PxProductConfigPlanRel rel = DaoServiceUtil.getProductConfigPlanRelService()
            .queryBuilder()
            .where(PxProductConfigPlanRelDao.Properties.ObjectId.eq(configPlanRel.getObjectId()))
            .unique();
        if (rel == null) {
          configPlanRel.setDelFlag("0");
          DaoServiceUtil.getProductConfigPlanRelService().save(configPlanRel);
          sProductConfigPlanRelList.add(configPlanRel);
        }
        break;
      case DataSync.UPDATE:
        PxProductConfigPlanRel dbConfigPlanRel = DaoServiceUtil.getProductConfigPlanRelService()
            .queryBuilder()
            .where(PxProductConfigPlanRelDao.Properties.ObjectId.eq(configPlanRel.getObjectId()))
            .unique();
        if (dbConfigPlanRel != null) {
          String productObjId = configPlanRel.getProduct().getObjectId();
          String configObjId = configPlanRel.getConfigPlan().getObjectId();
          PxProductInfo dbProduct = DaoServiceUtil.getProductInfoService()
              .queryBuilder()
              .where(PxProductInfoDao.Properties.ObjectId.eq(productObjId))
              .unique();
          PxProductConfigPlan dbConfigPlan = DaoServiceUtil.getProductConfigPlanService()
              .queryBuilder()
              .where(PxProductConfigPlanDao.Properties.ObjectId.eq(configObjId))
              .unique();
          dbConfigPlanRel.setDbProduct(dbProduct);
          dbConfigPlanRel.setDbProductConfigPlan(dbConfigPlan);
          DaoServiceUtil.getProductConfigPlanRelService().saveOrUpdate(dbConfigPlanRel);
        } else {
          configPlanRel.setDelFlag("0");
          DaoServiceUtil.getProductConfigPlanRelService().save(configPlanRel);
          sProductConfigPlanRelList.add(configPlanRel);
        }
        break;
      case DataSync.DELETE:
        PxProductConfigPlanRel exist = DaoServiceUtil.getProductConfigPlanRelService()
            .queryBuilder()
            .where(PxProductConfigPlanRelDao.Properties.ObjectId.eq(configPlanRel.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setDelFlag("1");
          DaoServiceUtil.getProductConfigPlanRelService().saveOrUpdate(exist);
        }
        break;
    }
  }

  /**
   * 翼支付
   */
  private void operateBestpay(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxBestpay bestpay = RestClient.getGson().fromJson(dataJson, PxBestpay.class);
    switch (opt) {
      case DataSync.INSERT:
        PxBestpay p = DaoServiceUtil.getBestpayService()
            .queryBuilder()
            .where(PxBestpayDao.Properties.ObjectId.eq(bestpay.getObjectId()))
            .unique();
        if (p == null) {
          bestpay.setDelFlag("0");
          DaoServiceUtil.getBestpayService().saveOrUpdate(bestpay);
        }
        break;
      case DataSync.UPDATE:
        PxBestpay exist = DaoServiceUtil.getBestpayService()
            .queryBuilder()
            .where(PxBestpayDao.Properties.ObjectId.eq(bestpay.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setShopCode(bestpay.getShopCode());
          exist.setDataKey(bestpay.getDataKey());
          exist.setTradeKey(bestpay.getTradeKey());
          exist.setDelFlag("0");
          DaoServiceUtil.getBestpayService().update(exist);
        } else {
          bestpay.setDelFlag("0");
          DaoServiceUtil.getBestpayService().saveOrUpdate(bestpay);
        }
        break;
      case DataSync.DELETE:

        break;
    }
  }

  /**
   * 存储会员信息
   */
  private void operatePxVipInfo(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxVipInfo vipInfo = RestClient.getGson().fromJson(dataJson, PxVipInfo.class);
    if (vipInfo == null) return;
    switch (opt) {
      case DataSync.INSERT:
        PxVipInfo dbVipInfo = DaoServiceUtil.getVipInfoService()
            .queryBuilder()
            .where(PxVipInfoDao.Properties.ObjectId.eq(vipInfo.getObjectId()))
            .unique();
        if (dbVipInfo == null) {//存会员
          vipInfo.setIsUpLoad(true);
          vipInfo.setIsModify(false);
          vipInfo.setDelFlag("0");
          DaoServiceUtil.getVipInfoService().save(vipInfo);
        }
      case DataSync.UPDATE:
        PxVipInfo dbVipInfo1 = DaoServiceUtil.getVipInfoService()
            .queryBuilder()
            .where(PxVipInfoDao.Properties.ObjectId.eq(vipInfo.getObjectId()))
            .unique();
        if (dbVipInfo1 == null) {//存会员
          vipInfo.setIsUpLoad(true);
          vipInfo.setIsModify(false);
          vipInfo.setDelFlag("0");
          DaoServiceUtil.getVipInfoService().save(vipInfo);
        } else {

        }
        break;
    }
  }

  /**
   * 营业时间
   */
  private void operateBusinessHours(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxBusinessHours businessHours = RestClient.getGson().fromJson(dataJson, PxBusinessHours.class);
    switch (opt) {
      case DataSync.INSERT:
        PxBusinessHours pxBusinessHours = DaoServiceUtil.getBusinessHoursService()
            .queryBuilder()
            .where(PxBusinessHoursDao.Properties.ObjectId.eq(businessHours.getObjectId()))
            .unique();
        if (pxBusinessHours == null) {
          businessHours.setDelFlag("0");
          DaoServiceUtil.getBusinessHoursService().saveOrUpdate(businessHours);
        }
        break;
      case DataSync.UPDATE:
        PxBusinessHours exist = DaoServiceUtil.getBusinessHoursService()
            .queryBuilder()
            .where(PxBusinessHoursDao.Properties.ObjectId.eq(businessHours.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setBusinessType(businessHours.getBusinessType());
          exist.setCloseTime(businessHours.getCloseTime());
          DaoServiceUtil.getBusinessHoursService().saveOrUpdate(exist);
        } else {
          businessHours.setDelFlag("0");
          DaoServiceUtil.getBusinessHoursService().saveOrUpdate(businessHours);
        }
        break;
      case DataSync.DELETE:
        PxBusinessHours del = DaoServiceUtil.getBusinessHoursService()
            .queryBuilder()
            .where(PxBusinessHoursDao.Properties.ObjectId.eq(businessHours.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getBusinessHoursService().saveOrUpdate(del);
        }
        break;
    }
  }

  /**
   * 操作原因
   */
  private void operateOptReason(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxOptReason optReason = RestClient.getGson().fromJson(dataJson, PxOptReason.class);
    switch (opt) {
      case DataSync.INSERT:
        PxOptReason reason = DaoServiceUtil.getOptReasonService()
            .queryBuilder()
            .where(PxOptReasonDao.Properties.ObjectId.eq(optReason.getObjectId()))
            .unique();
        if (reason == null) {
          optReason.setDelFlag("0");
          DaoServiceUtil.getOptReasonService().saveOrUpdate(optReason);
        }
        break;
      case DataSync.UPDATE:
        PxOptReason exist = DaoServiceUtil.getOptReasonService()
            .queryBuilder()
            .where(PxOptReasonDao.Properties.ObjectId.eq(optReason.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setName(optReason.getName());
          exist.setType(optReason.getType());
          DaoServiceUtil.getOptReasonService().saveOrUpdate(exist);
        } else {
          optReason.setDelFlag("0");
          DaoServiceUtil.getOptReasonService().saveOrUpdate(optReason);
        }
        break;
      case DataSync.DELETE:
        PxOptReason del = DaoServiceUtil.getOptReasonService()
            .queryBuilder()
            .where(PxOptReasonDao.Properties.ObjectId.eq(optReason.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getOptReasonService().saveOrUpdate(del);
        }
        break;
    }
  }

  /**
   * 微信支付配置参数
   */
  private void operateWeiXinpay(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxWeiXinpay weiXinpay = RestClient.getGson().fromJson(dataJson, PxWeiXinpay.class);
    switch (opt) {
      case DataSync.INSERT:
        PxWeiXinpay p = DaoServiceUtil.getWeiXinPayService()
            .queryBuilder()
            .where(PxWeiXinpayDao.Properties.ObjectId.eq(weiXinpay.getObjectId()))
            .unique();
        if (p == null) {
          weiXinpay.setDelFlag("0");
          DaoServiceUtil.getWeiXinPayService().saveOrUpdate(weiXinpay);
        }
        break;
      case DataSync.UPDATE:
        PxWeiXinpay exist = DaoServiceUtil.getWeiXinPayService()
            .queryBuilder()
            .where(PxWeiXinpayDao.Properties.ObjectId.eq(weiXinpay.getObjectId()))
            .unique();
        if (exist != null) {
          exist.setKey(weiXinpay.getKey());
          exist.setAppId(weiXinpay.getAppId());
          exist.setMacId(weiXinpay.getMacId());
          exist.setDelFlag("0");
          DaoServiceUtil.getWeiXinPayService().update(exist);
        } else {
          weiXinpay.setDelFlag("0");
          DaoServiceUtil.getWeiXinPayService().saveOrUpdate(weiXinpay);
        }
        break;
      case DataSync.DELETE:

        break;
    }
  }

  /**
   * 支付宝商户信息
   */
  private void operateAlipayInfo(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxAlipayInfo alipayInfo = RestClient.getGson().fromJson(dataJson, PxAlipayInfo.class);
    switch (opt) {
      case DataSync.INSERT:
        PxAlipayInfo insert = DaoServiceUtil.getAlipayInfoService()
            .queryBuilder()
            .where(PxAlipayInfoDao.Properties.ObjectId.eq(alipayInfo.getObjectId()))
            .unique();
        if (insert == null) {
          alipayInfo.setDelFlag("0");
          DaoServiceUtil.getAlipayInfoService().saveOrUpdate(alipayInfo);
        }
        break;
      case DataSync.UPDATE:
        PxAlipayInfo update = DaoServiceUtil.getAlipayInfoService()
            .queryBuilder()
            .where(PxAlipayInfoDao.Properties.ObjectId.eq(alipayInfo.getObjectId()))
            .unique();
        if (update != null) {
          update.setAlipayAccount(alipayInfo.getAlipayAccount());
          update.setSellerId(alipayInfo.getSellerId());
          DaoServiceUtil.getAlipayInfoService().update(update);
        } else {//卸载重装
          alipayInfo.setDelFlag("0");
          DaoServiceUtil.getAlipayInfoService().saveOrUpdate(alipayInfo);
        }
        break;
      case DataSync.DELETE:
        PxAlipayInfo delete = DaoServiceUtil.getAlipayInfoService()
            .queryBuilder()
            .where(PxAlipayInfoDao.Properties.ObjectId.eq(alipayInfo.getObjectId()))
            .unique();
        if (delete != null) {
          delete.setDelFlag("1");
          DaoServiceUtil.getAlipayInfoService().update(delete);
        }
        break;
    }
  }

  /**
   * 套餐分组
   */
  private void operateComboGroup(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxComboGroup pxComboGroup = RestClient.getGson().fromJson(dataJson, PxComboGroup.class);
    switch (opt) {
      case DataSync.INSERT:
        PxComboGroup insert = DaoServiceUtil.getComboGroupService()
            .queryBuilder()
            .where(PxComboGroupDao.Properties.ObjectId.eq(pxComboGroup.getObjectId()))
            .unique();
        if (insert == null) {
          pxComboGroup.setDelFlag("0");
          DaoServiceUtil.getComboGroupService().saveOrUpdate(pxComboGroup);
          sComboGroupList.add(pxComboGroup);
        }
        break;
      case DataSync.UPDATE:
        PxComboGroup update = DaoServiceUtil.getComboGroupService()
            .queryBuilder()
            .where(PxComboGroupDao.Properties.ObjectId.eq(pxComboGroup.getObjectId()))
            .unique();
        if (update == null) {
          pxComboGroup.setDelFlag("0");
          DaoServiceUtil.getComboGroupService().saveOrUpdate(pxComboGroup);
          sComboGroupList.add(pxComboGroup);
        } else {
          update.setAllowNum(pxComboGroup.getAllowNum());
          update.setName(pxComboGroup.getName());
          update.setType(pxComboGroup.getType());
          DaoServiceUtil.getComboGroupService().saveOrUpdate(update);
          //
          update.setComboId(pxComboGroup.getComboId());
          sComboGroupList.add(update);
        }
        break;
      case DataSync.DELETE:
        PxComboGroup del = DaoServiceUtil.getComboGroupService()
            .queryBuilder()
            .where(PxComboGroupDao.Properties.ObjectId.eq(pxComboGroup.getObjectId()))
            .unique();
        if (del != null) {
          del.setDelFlag("1");
          DaoServiceUtil.getComboGroupService().saveOrUpdate(del);
        }
        break;
    }
  }

  /**
   * 套餐分组和商品rel
   */
  private void operateComboProductRel(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxComboProductRel rel = RestClient.getGson().fromJson(dataJson, PxComboProductRel.class);
    PxComboProductRel productRel = DaoServiceUtil.getComboProdRelService()
        .queryBuilder()
        .where(PxComboProductRelDao.Properties.ObjectId.eq(rel.getObjectId()))
        .unique();
    switch (opt) {
      case DataSync.INSERT:
        if (productRel == null) {
          rel.setDelFlag("0");
          DaoServiceUtil.getComboProdRelService().saveOrUpdate(rel);
          sComboProdRelList.add(rel);
        }
        break;
      case DataSync.UPDATE:
        if (productRel == null) {
          rel.setDelFlag("0");
          DaoServiceUtil.getComboProdRelService().saveOrUpdate(rel);
          sComboProdRelList.add(rel);
        } else {
          productRel.setNum(rel.getNum());
          productRel.setWeight(rel.getWeight());
          productRel.setDelFlag("0");
          DaoServiceUtil.getComboProdRelService().saveOrUpdate(productRel);
        }
        break;
      case DataSync.DELETE:
        if (productRel != null) {
          productRel.setDelFlag("1");
          DaoServiceUtil.getComboProdRelService().saveOrUpdate(productRel);
        }
        break;
    }
  }

  /**
   * 商品备注
   */
  private void operateProdRemarks(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxProductRemarks remarks = RestClient.getGson().fromJson(dataJson, PxProductRemarks.class);
    PxProductRemarks productRemarks = DaoServiceUtil.getProdRemarksService()
        .queryBuilder()
        .where(PxProductRemarksDao.Properties.ObjectId.eq(remarks.getObjectId()))
        .unique();
    switch (opt) {
      case DataSync.INSERT:
        if (productRemarks == null) {
          remarks.setDelFlag("0");
          DaoServiceUtil.getProdRemarksService().saveOrUpdate(remarks);
        }
        break;
      case DataSync.UPDATE:
        if (productRemarks == null) {
          remarks.setDelFlag("0");
          DaoServiceUtil.getProdRemarksService().saveOrUpdate(remarks);
        } else {
          productRemarks.setRemarks(remarks.getRemarks());
          DaoServiceUtil.getProdRemarksService().saveOrUpdate(productRemarks);
        }
        break;
      case DataSync.DELETE:
        if (productRemarks != null) {
          productRemarks.setDelFlag("1");
          DaoServiceUtil.getProdRemarksService().saveOrUpdate(productRemarks);
        }
        break;
    }
  }

  /**
   * 优惠券
   */
  private void operateVoucher(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxVoucher pxVoucher = RestClient.getGson().fromJson(dataJson, PxVoucher.class);
    PxVoucher exist = DaoServiceUtil.getVoucherService()
        .queryBuilder()
        .where(PxVoucherDao.Properties.ObjectId.eq(pxVoucher.getObjectId()))
        .unique();
    switch (opt) {
      case DataSync.INSERT:
        if (exist == null) {
          pxVoucher.setDelFlag("0");
          DaoServiceUtil.getVoucherService().saveOrUpdate(pxVoucher);
        }
        break;
      case DataSync.UPDATE:
        if (exist == null) {
          pxVoucher.setDelFlag("0");
          DaoServiceUtil.getVoucherService().saveOrUpdate(pxVoucher);
        } else {
          exist.setPrice(pxVoucher.getPrice());
          exist.setCode(pxVoucher.getCode());
          exist.setDeratePrice(pxVoucher.getDeratePrice());
          exist.setType(pxVoucher.getType());
          exist.setStartDate(pxVoucher.getStartDate());
          exist.setEndDate(pxVoucher.getEndDate());
          exist.setPermanent(pxVoucher.getPermanent());
          exist.setObjectId(pxVoucher.getObjectId());
          DaoServiceUtil.getVoucherService().saveOrUpdate(exist);
        }
        break;
      case DataSync.DELETE:
        if (exist != null) {
          exist.setDelFlag("1");
          DaoServiceUtil.getVoucherService().saveOrUpdate(exist);
        }
        break;
    }
  }

  /**
   * 支付方式
   */
  private void operatePaymentMode(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxPaymentMode paymentMode = RestClient.getGson().fromJson(dataJson, PxPaymentMode.class);
    PxPaymentMode exist = DaoServiceUtil.getPaymentModeService()
        .queryBuilder()
        .where(PxPaymentModeDao.Properties.ObjectId.eq(paymentMode.getObjectId()))
        .unique();
    switch (opt) {
      case DataSync.INSERT:
        if (exist == null) {
          paymentMode.setDelFlag("0");
          DaoServiceUtil.getPaymentModeService().saveOrUpdate(paymentMode);
        }
        break;
      case DataSync.UPDATE:
        if (exist == null) {
          paymentMode.setDelFlag("0");
          DaoServiceUtil.getPaymentModeService().saveOrUpdate(paymentMode);
        } else {
          exist.setEdit(paymentMode.getEdit());
          exist.setName(paymentMode.getName());
          exist.setOpenBox(paymentMode.getOpenBox());
          exist.setSalesAmount(paymentMode.getSalesAmount());
          exist.setType(paymentMode.getType());
          exist.setOrderNo(paymentMode.getOrderNo());
          DaoServiceUtil.getPaymentModeService().update(exist);
        }
        break;
      case DataSync.DELETE:
        if (exist != null) {
          exist.setDelFlag("1");
          DaoServiceUtil.getPaymentModeService().saveOrUpdate(exist);
        }
        break;
    }
  }

  /**
   * 团购券
   */
  private void operatePxBuyCoupon(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxBuyCoupons pxBuyCoupons = RestClient.getGson().fromJson(dataJson, PxBuyCoupons.class);
    PxBuyCoupons exist = DaoServiceUtil.getPxBuyCouponsService()
        .queryBuilder()
        .where(PxBuyCouponsDao.Properties.ObjectId.eq(pxBuyCoupons.getObjectId()))
        .unique();
    switch (opt) {
      case DataSync.INSERT:
        if (exist == null) {
          pxBuyCoupons.setDelFlag("0");
          DaoServiceUtil.getPxBuyCouponsService().saveOrUpdate(pxBuyCoupons);
          sBuyCouponsList.add(pxBuyCoupons);
        }
        break;
      case DataSync.UPDATE:
        if (exist == null) {
          pxBuyCoupons.setDelFlag("0");
          DaoServiceUtil.getPxBuyCouponsService().saveOrUpdate(pxBuyCoupons);
          sBuyCouponsList.add(pxBuyCoupons);
        } else {
          exist.setName(pxBuyCoupons.getName());
          exist.setOffsetAmount(pxBuyCoupons.getOffsetAmount());
          exist.setAmount(pxBuyCoupons.getAmount());
          DaoServiceUtil.getPxBuyCouponsService().saveOrUpdate(exist);
          //
          exist.setPxPaymentMode(pxBuyCoupons.getPxPaymentMode());
          sBuyCouponsList.add(exist);
        }
        break;
      case DataSync.DELETE:
        if (exist != null) {
          exist.setDelFlag("1");
          DaoServiceUtil.getPxBuyCouponsService().saveOrUpdate(exist);
        }
        break;
    }
  }

  /**
   * 桌台区域
   */
  private void operateTableArea(DataSync dataSync) {
    int opt = dataSync.getOpt();
    String dataJson = getDataJson(dataSync);
    PxTableArea tableArea = RestClient.getGson().fromJson(dataJson, PxTableArea.class);
    PxTableArea dbTableArea = DaoServiceUtil.getTableAreaService()
        .queryBuilder()
        .where(PxTableAreaDao.Properties.ObjectId.eq(tableArea.getObjectId()))
        .unique();
    switch (opt) {
      case DataSync.INSERT:
        if (dbTableArea == null) {
          tableArea.setDelFlag("0");
          DaoServiceUtil.getTableAreaService().saveOrUpdate(tableArea);
        }
        break;
      case DataSync.UPDATE:
        if (dbTableArea != null) {
          dbTableArea.setDelFlag("0");
          dbTableArea.setType(tableArea.getType());
          dbTableArea.setName(tableArea.getName());
          DaoServiceUtil.getTableAreaService().update(dbTableArea);
        } else {
          tableArea.setDelFlag("0");
          DaoServiceUtil.getTableAreaService().saveOrUpdate(tableArea);
        }

        break;
      case DataSync.DELETE:
        if (dbTableArea != null) {
          dbTableArea.setDelFlag("1");
          DaoServiceUtil.getTableAreaService().update(dbTableArea);
        }
        break;
    }
  }
}