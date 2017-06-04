package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.dao.BTPrintDeviceDao;
import com.psi.easymanager.dao.EPaymentInfoDao;
import com.psi.easymanager.dao.OfficeDao;
import com.psi.easymanager.dao.PdConfigRelDao;
import com.psi.easymanager.dao.PrintDetailsCollectDao;
import com.psi.easymanager.dao.PrintDetailsDao;
import com.psi.easymanager.dao.PxAlipayInfoDao;
import com.psi.easymanager.dao.PxBestpayDao;
import com.psi.easymanager.dao.PxBusinessHoursDao;
import com.psi.easymanager.dao.PxBuyCouponsDao;
import com.psi.easymanager.dao.PxComboGroupDao;
import com.psi.easymanager.dao.PxComboProductRelDao;
import com.psi.easymanager.dao.PxDiscounSchemeDao;
import com.psi.easymanager.dao.PxExtraChargeDao;
import com.psi.easymanager.dao.PxExtraDetailsDao;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxOperationLogDao;
import com.psi.easymanager.dao.PxOptReasonDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxOrderNumDao;
import com.psi.easymanager.dao.PxPayInfoDao;
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
import com.psi.easymanager.dao.PxRechargeRecordDao;
import com.psi.easymanager.dao.PxSetInfoDao;
import com.psi.easymanager.dao.PxTableAlterationDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.PxTableInfoDao;
import com.psi.easymanager.dao.PxVipCardInfoDao;
import com.psi.easymanager.dao.PxVipCardTypeDao;
import com.psi.easymanager.dao.PxVipInfoDao;
import com.psi.easymanager.dao.PxVoucherDao;
import com.psi.easymanager.dao.PxWeiXinpayDao;
import com.psi.easymanager.dao.SmackUUIDRecordDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.UserDao;

/**
 * Created by zjq on 2016/3/31.
 * 用于获取各种DbService
 */
public class DaoServiceUtil {
  private static TableInfoService sTableInfoService;
  private static OrderInfoService sOrderInfoService;
  private static OrderDetailsService sOrderDetailsService;
  private static ProductCategoryService sProductCategoryService;
  private static ProductInfoService sProductInfoService;
  private static UserService sUserService;
  private static OfficeService sOfficeService;
  private static DiscounSchemeService sDiscounSchemeService;
  private static PrinterInfoService sPrinterInfoService;
  private static ExtraChargeService sExtraChargeService;
  private static TableExtraRelService sTableExtraRelService;
  private static PxPromotionInfoService sPromotionInfoService;
  private static PxPromotionDetailsService sPromotionDetailsService;
  private static FormatInfoService sFormatInfoService;
  private static ProductFormatRelService sProductFormatRelService;
  private static MethodInfoService sMethodInfoService;
  private static ProductMethodRelService sProductMethodRelService;
  private static ExtraDetailsService sExtraDetailsService;
  private static TableAlterationService sTableAlterationService;
  private static VipInfoService sVipInfo;
  private static RechargeRecordService sRechargeRecordService;
  private static RechargePlanService sRechargePlanService;
  private static ProductConfigPlanService sProductConfigPlanService;
  private static ProductConfigPlanRelService sProductConfigPlanRelService;
  private static PayInfoService sPayInfoService;
  private static OrderNumService sOrderNumService;
  private static BestpayService sBestpayService;
  private static SetService sSetService;
  private static BusinessHoursService sBusinessHoursService;
  private static OptReasonService sOptReasonService;
  private static WeiXinPayService sWeiXinPayService;
  private static AlipayInfoService sAlipayInfoService;
  private static ComboGroupService sComboGroupService;
  private static ComboProdRelService sComboProdRelService;
  private static ProdRemarksService sProdRemarksService;
  private static PrintDetailsService sPrintDetailsService;
  private static PdCollectService sPdCollectService;
  private static PdConfigRelService sPdConfigRelService;
  private static TableOrderRelService sTableOrderRelService;
  private static PxVoucherService sVoucherService;
  private static PxPaymentModeService sPaymentModeService;
  private static EPaymentInfoService sEPaymentInfoService;
  private static OperationLogService sOperationLogService;
  private static PxBuyCouponsService sBuyCouponsService;
  private static SmackUUIDRecordService sSmackUUIDRecordService;
  private static PxTableAreaService sPxTableAreaService;
  private static VipCardTypeService sVipCardType;
  private static VipCardInfoService sVipCardInfo;
  private static BTDevicesService sBtDevicesService;

  /**
   * TableInfo
   */
  public static PxTableInfoDao getTableInfoDao() {
    return DbCore.getDaoSession().getPxTableInfoDao();
  }

  public static TableInfoService getTableInfoService() {
    if (sTableInfoService == null) {
      sTableInfoService = new TableInfoService(getTableInfoDao());
    }
    return sTableInfoService;
  }

  /**
   * OrderInfo
   */

  public static PxOrderInfoDao getOrderInfoDao() {
    return DbCore.getDaoSession().getPxOrderInfoDao();
  }

  public static OrderInfoService getOrderInfoService() {
    if (sOrderInfoService == null) {
      sOrderInfoService = new OrderInfoService(getOrderInfoDao());
    }
    return sOrderInfoService;
  }

  /**
   * OrderDetails
   */

  public static PxOrderDetailsDao getOrderDetailsDao() {
    return DbCore.getDaoSession().getPxOrderDetailsDao();
  }

  public static OrderDetailsService getOrderDetailsService() {
    if (sOrderDetailsService == null) {
      sOrderDetailsService = new OrderDetailsService(getOrderDetailsDao());
    }
    return sOrderDetailsService;
  }

  /**
   * ProductCategory
   */
  public static PxProductCategoryDao getProductCategoryDao() {
    return DbCore.getDaoSession().getPxProductCategoryDao();
  }

  public static ProductCategoryService getProductCategoryService() {
    if (sProductCategoryService == null) {
      sProductCategoryService = new ProductCategoryService(getProductCategoryDao());
    }
    return sProductCategoryService;
  }

  /**
   * ProductInfo
   */
  public static PxProductInfoDao getProductInfoDao() {
    return DbCore.getDaoSession().getPxProductInfoDao();
  }

  public static ProductInfoService getProductInfoService() {
    if (sProductInfoService == null) {
      sProductInfoService = new ProductInfoService(getProductInfoDao());
    }
    return sProductInfoService;
  }

  /**
   * User
   */
  public static UserDao getUserDao() {
    return DbCore.getDaoSession().getUserDao();
  }

  public static UserService getUserService() {
    if (sUserService == null) {
      sUserService = new UserService(getUserDao());
    }
    return sUserService;
  }

  /**
   * Office
   */
  public static OfficeDao getOfficeDao() {
    return DbCore.getDaoSession().getOfficeDao();
  }

  public static OfficeService getOfficeService() {
    if (sOfficeService == null) {
      sOfficeService = new OfficeService(getOfficeDao());
    }
    return sOfficeService;
  }

  /**
   * DiscountScheme
   */
  public static PxDiscounSchemeDao getDiscountSchemeDao() {
    return DbCore.getDaoSession().getPxDiscounSchemeDao();
  }

  public static DiscounSchemeService getDiscounSchemeService() {
    if (sDiscounSchemeService == null) {
      sDiscounSchemeService = new DiscounSchemeService(getDiscountSchemeDao());
    }
    return sDiscounSchemeService;
  }

  /**
   * PrinterInfo
   */
  public static PxPrinterInfoDao getPrinterInfoDao() {
    return DbCore.getDaoSession().getPxPrinterInfoDao();
  }

  public static PrinterInfoService getPrinterInfoService() {
    if (sPrinterInfoService == null) {
      sPrinterInfoService = new PrinterInfoService(getPrinterInfoDao());
    }
    return sPrinterInfoService;
  }

  /**
   * ExtraCharge
   */
  public static PxExtraChargeDao getExtraChargeDao() {
    return DbCore.getDaoSession().getPxExtraChargeDao();
  }

  public static ExtraChargeService getExtraChargeService() {
    if (sExtraChargeService == null) {
      sExtraChargeService = new ExtraChargeService(getExtraChargeDao());
    }
    return sExtraChargeService;
  }

  /**
   * TableExtraRel
   */
  public static PxTableExtraRelDao getTableExtraRelDao() {
    return DbCore.getDaoSession().getPxTableExtraRelDao();
  }

  public static TableExtraRelService getTableExtraRelService() {
    if (sTableExtraRelService == null) {
      sTableExtraRelService = new TableExtraRelService(getTableExtraRelDao());
    }
    return sTableExtraRelService;
  }

  /**
   * PromotionInfo
   */
  public static PxPromotioInfoDao getPromotionInfoDao() {
    return DbCore.getDaoSession().getPxPromotioInfoDao();
  }

  public static PxPromotionInfoService getPromotionInfoService() {
    if (sPromotionInfoService == null) {
      sPromotionInfoService = new PxPromotionInfoService(getPromotionInfoDao());
    }
    return sPromotionInfoService;
  }

  /**
   * PromotionDetails
   */
  public static PxPromotioDetailsDao getPromotionDetailsDao() {
    return DbCore.getDaoSession().getPxPromotioDetailsDao();
  }

  public static PxPromotionDetailsService getPromotionDetailsService() {
    if (sPromotionDetailsService == null) {
      sPromotionDetailsService = new PxPromotionDetailsService(getPromotionDetailsDao());
    }
    return sPromotionDetailsService;
  }

  /**
   * FormatInfo
   */
  public static PxFormatInfoDao getFormatInfoDao() {
    return DbCore.getDaoSession().getPxFormatInfoDao();
  }

  public static FormatInfoService getFormatInfoService() {
    if (sFormatInfoService == null) {
      sFormatInfoService = new FormatInfoService(getFormatInfoDao());
    }
    return sFormatInfoService;
  }

  /**
   * ProductFormatInfo
   */
  public static PxProductFormatRelDao getProductFormatRelDao() {
    return DbCore.getDaoSession().getPxProductFormatRelDao();
  }

  public static ProductFormatRelService getProductFormatRelService() {
    if (sProductFormatRelService == null) {
      sProductFormatRelService = new ProductFormatRelService(getProductFormatRelDao());
    }
    return sProductFormatRelService;
  }

  /**
   * MethodIno
   */
  public static PxMethodInfoDao getMethodInfoDao() {
    return DbCore.getDaoSession().getPxMethodInfoDao();
  }

  public static MethodInfoService getMethodInfoService() {
    if (sMethodInfoService == null) {
      sMethodInfoService = new MethodInfoService(getMethodInfoDao());
    }
    return sMethodInfoService;
  }

  /**
   * ProductMethodInfo
   */
  public static PxProductMethodRefDao getProductMethodRelDao() {
    return DbCore.getDaoSession().getPxProductMethodRefDao();
  }

  public static ProductMethodRelService getProductMethodRelService() {
    if (sProductMethodRelService == null) {
      sProductMethodRelService = new ProductMethodRelService(getProductMethodRelDao());
    }
    return sProductMethodRelService;
  }

  /**
   * ExtraDetails
   */
  public static PxExtraDetailsDao getExtraDetailsDao() {
    return DbCore.getDaoSession().getPxExtraDetailsDao();
  }

  public static ExtraDetailsService getExtraDetailsService() {
    if (sExtraDetailsService == null) {
      sExtraDetailsService = new ExtraDetailsService(getExtraDetailsDao());
    }
    return sExtraDetailsService;
  }

  /**
   * TableAlteration
   */
  public static PxTableAlterationDao getTableAlterationDao() {
    return DbCore.getDaoSession().getPxTableAlterationDao();
  }

  public static TableAlterationService getTableAlterationService() {
    if (sTableAlterationService == null) {
      sTableAlterationService = new TableAlterationService(getTableAlterationDao());
    }
    return sTableAlterationService;
  }

  /**
   * VipInfo
   */
  public static PxVipInfoDao getVipInfoDao() {
    return DbCore.getDaoSession().getPxVipInfoDao();
  }

  public static VipInfoService getVipInfoService() {
    if (sVipInfo == null) {
      sVipInfo = new VipInfoService(getVipInfoDao());
    }
    return sVipInfo;
  }

  /**
   * VipCardType 卡类型
   */
  public static PxVipCardTypeDao getVipCardTypeDao() {
    return DbCore.getDaoSession().getPxVipCardTypeDao();
  }

  public static VipCardTypeService getVipCardTypeService() {
    if (sVipCardType == null) {
      sVipCardType = new VipCardTypeService(getVipCardTypeDao());
    }
    return sVipCardType;
  }
  ///**
  // * VipCardRel
  // */
  //public static PxVipCardRelDao getVipCardRelDao() {
  //  return DbCore.getDaoSession().getPxVipCardRelDao();
  //}
  //
  //public static VipCardRelService getVipCardRelService() {
  //  if (sVipCardRel == null) {
  //    sVipCardRel = new VipCardRelService(getVipCardRelDao());
  //  }
  //  return sVipCardRel;
  //}

  /**
   * VipCardInfo id卡信息
   */
  public static PxVipCardInfoDao getVipCardInfoDao() {
    return DbCore.getDaoSession().getPxVipCardInfoDao();
  }

  public static VipCardInfoService getVipCardInfoService() {

    if (sVipCardInfo == null) {
      sVipCardInfo = new VipCardInfoService(getVipCardInfoDao());
    }
    return sVipCardInfo;
  }

  /**
   * RechargeRecord
   */
  public static PxRechargeRecordDao getRechargeRecordDao() {
    return DbCore.getDaoSession().getPxRechargeRecordDao();
  }

  public static RechargeRecordService getRechargeRecordService() {
    if (sRechargeRecordService == null) {
      sRechargeRecordService = new RechargeRecordService(getRechargeRecordDao());
    }
    return sRechargeRecordService;
  }

  /**
   * RechargePlan
   */
  public static PxRechargePlanDao getRechargePlanDao() {
    return DbCore.getDaoSession().getPxRechargePlanDao();
  }

  public static RechargePlanService getRechargePlanService() {
    if (sRechargePlanService == null) {
      sRechargePlanService = new RechargePlanService(getRechargePlanDao());
    }
    return sRechargePlanService;
  }

  /**
   * PxProductConfigPlan
   */
  public static PxProductConfigPlanDao getProductConfigPlanDao() {
    return DbCore.getDaoSession().getPxProductConfigPlanDao();
  }

  public static ProductConfigPlanService getProductConfigPlanService() {
    if (sProductConfigPlanService == null) {
      sProductConfigPlanService = new ProductConfigPlanService(getProductConfigPlanDao());
    }
    return sProductConfigPlanService;
  }

  /**
   * PxProductConfigPlanRel
   */
  public static PxProductConfigPlanRelDao getProductConfigPlanRelDao() {
    return DbCore.getDaoSession().getPxProductConfigPlanRelDao();
  }

  public static ProductConfigPlanRelService getProductConfigPlanRelService() {
    if (sProductConfigPlanRelService == null) {
      sProductConfigPlanRelService = new ProductConfigPlanRelService(getProductConfigPlanRelDao());
    }
    return sProductConfigPlanRelService;
  }

  /**
   * PayInfo
   */
  public static PxPayInfoDao getPayInfoDao() {
    return DbCore.getDaoSession().getPxPayInfoDao();
  }

  public static PayInfoService getPayInfoService() {
    if (sPayInfoService == null) {
      sPayInfoService = new PayInfoService(getPayInfoDao());
    }
    return sPayInfoService;
  }

  /**
   * OrderNum
   */
  public static PxOrderNumDao getOrderNumDao() {
    return DbCore.getDaoSession().getPxOrderNumDao();
  }

  public static OrderNumService getOrderNumService() {
    if (sOrderNumService == null) {
      sOrderNumService = new OrderNumService(getOrderNumDao());
    }
    return sOrderNumService;
  }

  /**
   * Bestpay
   */
  public static PxBestpayDao getBestpayDao() {
    return DbCore.getDaoSession().getPxBestpayDao();
  }

  public static BestpayService getBestpayService() {
    if (sBestpayService == null) {
      sBestpayService = new BestpayService(getBestpayDao());
    }
    return sBestpayService;
  }

  /**
   * SetInfo
   */
  public static PxSetInfoDao getSetInfoDao() {
    return DbCore.getDaoSession().getPxSetInfoDao();
  }

  public static SetService getSetInfoService() {
    if (sSetService == null) {
      sSetService = new SetService(getSetInfoDao());
    }
    return sSetService;
  }

  /**
   * 营业时间
   */
  public static PxBusinessHoursDao getBusinessHoursDao() {
    return DbCore.getDaoSession().getPxBusinessHoursDao();
  }

  public static BusinessHoursService getBusinessHoursService() {
    if (sBusinessHoursService == null) {
      sBusinessHoursService = new BusinessHoursService(getBusinessHoursDao());
    }
    return sBusinessHoursService;
  }

  /**
   * 操作原因
   */
  public static PxOptReasonDao getOptReasonDao() {
    return DbCore.getDaoSession().getPxOptReasonDao();
  }

  public static OptReasonService getOptReasonService() {
    if (sOptReasonService == null) {
      sOptReasonService = new OptReasonService(getOptReasonDao());
    }
    return sOptReasonService;
  }

  /**
   * WeiXinPay
   */
  public static PxWeiXinpayDao getWeiXinPayDao() {
    return DbCore.getDaoSession().getPxWeiXinpayDao();
  }

  public static WeiXinPayService getWeiXinPayService() {
    if (sWeiXinPayService == null) {
      sWeiXinPayService = new WeiXinPayService(getWeiXinPayDao());
    }
    return sWeiXinPayService;
  }

  /**
   * AlipayInfo
   */
  public static PxAlipayInfoDao getAlipayInfoDao() {
    return DbCore.getDaoSession().getPxAlipayInfoDao();
  }

  public static AlipayInfoService getAlipayInfoService() {
    if (sAlipayInfoService == null) {
      sAlipayInfoService = new AlipayInfoService(getAlipayInfoDao());
    }
    return sAlipayInfoService;
  }

  /**
   * ComboGroup
   */
  public static PxComboGroupDao getComboGroupDao() {
    return DbCore.getDaoSession().getPxComboGroupDao();
  }

  public static ComboGroupService getComboGroupService() {
    if (sComboGroupService == null) {
      sComboGroupService = new ComboGroupService(getComboGroupDao());
    }
    return sComboGroupService;
  }

  /**
   * ComboProdRel
   */
  public static PxComboProductRelDao getComboProdRelDao() {
    return DbCore.getDaoSession().getPxComboProductRelDao();
  }

  public static ComboProdRelService getComboProdRelService() {
    if (sComboProdRelService == null) {
      sComboProdRelService = new ComboProdRelService(getComboProdRelDao());
    }
    return sComboProdRelService;
  }

  /**
   * ProdRemarks
   */
  public static PxProductRemarksDao getProductRemarksDao() {
    return DbCore.getDaoSession().getPxProductRemarksDao();
  }

  public static ProdRemarksService getProdRemarksService() {
    if (sProdRemarksService == null) {
      sProdRemarksService = new ProdRemarksService(getProductRemarksDao());
    }
    return sProdRemarksService;
  }

  /**
   * PrintDetails
   */
  public static PrintDetailsDao getPrintDetailsDao() {
    return DbCore.getDaoSession().getPrintDetailsDao();
  }

  public static PrintDetailsService getPrintDetailsService() {
    if (sPrintDetailsService == null) {
      sPrintDetailsService = new PrintDetailsService(getPrintDetailsDao());
    }
    return sPrintDetailsService;
  }

  /**
   * PdCollect
   */
  public static PrintDetailsCollectDao getPrintDetailsCollectDao() {
    return DbCore.getDaoSession().getPrintDetailsCollectDao();
  }

  public static PdCollectService getPdCollectService() {
    if (sPdCollectService == null) {
      sPdCollectService = new PdCollectService(getPrintDetailsCollectDao());
    }
    return sPdCollectService;
  }

  /**
   * PdConfigRel
   */
  public static PdConfigRelDao getPdConfigRelDao() {
    return DbCore.getDaoSession().getPdConfigRelDao();
  }

  public static PdConfigRelService getPdConfigRelService() {
    if (sPdConfigRelService == null) {
      sPdConfigRelService = new PdConfigRelService(getPdConfigRelDao());
    }
    return sPdConfigRelService;
  }

  /**
   * TableOrderRel
   */
  public static TableOrderRelDao getTableOrderRelDao() {
    return DbCore.getDaoSession().getTableOrderRelDao();
  }

  public static TableOrderRelService getTableOrderRelService() {
    if (sTableOrderRelService == null) {
      sTableOrderRelService = new TableOrderRelService(getTableOrderRelDao());
    }
    return sTableOrderRelService;
  }

  /**
   * PxVoucher
   */
  public static PxVoucherDao getVoucherDao() {
    return DbCore.getDaoSession().getPxVoucherDao();
  }

  public static PxVoucherService getVoucherService() {
    if (sVoucherService == null) {
      sVoucherService = new PxVoucherService(getVoucherDao());
    }
    return sVoucherService;
  }

  /**
   * PxPaymentMode
   */
  public static PxPaymentModeDao getPaymentModeDao() {
    return DbCore.getDaoSession().getPxPaymentModeDao();
  }

  public static PxPaymentModeService getPaymentModeService() {
    if (sPaymentModeService == null) {
      sPaymentModeService = new PxPaymentModeService(getPaymentModeDao());
    }
    return sPaymentModeService;
  }

  /**
   * EPaymentInfo
   */
  public static EPaymentInfoDao getEPaymentInfoDao() {
    return DbCore.getDaoSession().getEPaymentInfoDao();
  }

  public static EPaymentInfoService getEPaymentInfoService() {
    if (sEPaymentInfoService == null) {
      sEPaymentInfoService = new EPaymentInfoService(getEPaymentInfoDao());
    }
    return sEPaymentInfoService;
  }

  /**
   * OperationRecord
   */
  public static PxOperationLogDao getOperationRecordDao() {
    return DbCore.getDaoSession().getPxOperationLogDao();
  }

  public static OperationLogService getOperationRecordService() {
    if (sOperationLogService == null) {
      sOperationLogService = new OperationLogService(getOperationRecordDao());
    }
    return sOperationLogService;
  }

  /**
   * PxBuyCoupons
   */
  public static PxBuyCouponsDao getPxBuyCouponsDao() {
    return DbCore.getDaoSession().getPxBuyCouponsDao();
  }

  public static PxBuyCouponsService getPxBuyCouponsService() {
    if (sBuyCouponsService == null) {
      sBuyCouponsService = new PxBuyCouponsService(getPxBuyCouponsDao());
    }
    return sBuyCouponsService;
  }

  /**
   * Smack UUID Record
   */
  public static SmackUUIDRecordDao getSmackUUIDRecordDao() {
    return DbCore.getDaoSession().getSmackUUIDRecordDao();
  }

  public static SmackUUIDRecordService getSmackUUIDRecordService() {
    if (sSmackUUIDRecordService == null) {
      sSmackUUIDRecordService = new SmackUUIDRecordService(getSmackUUIDRecordDao());
    }
    return sSmackUUIDRecordService;
  }

  /**
   * PxTableArea
   */
  public static PxTableAreaDao getTableAreaDao() {
    return DbCore.getDaoSession().getPxTableAreaDao();
  }

  public static PxTableAreaService getTableAreaService() {
    if (sPxTableAreaService == null) {
      sPxTableAreaService = new PxTableAreaService(getTableAreaDao());
    }
    return sPxTableAreaService;
  }

  /**
   * BtPrintDevice
   */
  public static BTPrintDeviceDao getBTDeviceDao() {
    return DbCore.getDaoSession().getBTPrintDeviceDao();
  }

  public static BTDevicesService getBTDeviceService() {
    if (sBtDevicesService == null) {
      sBtDevicesService = new BTDevicesService(getBTDeviceDao());
    }
    return sBtDevicesService;
  }
}
