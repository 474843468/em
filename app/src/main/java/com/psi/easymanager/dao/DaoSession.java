package com.psi.easymanager.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.psi.easymanager.module.PxDiscounScheme;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductMethodRef;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.module.PxPromotioInfo;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.User;
import com.psi.easymanager.module.PxExtraCharge;
import com.psi.easymanager.module.PxTableExtraRel;
import com.psi.easymanager.module.PxRechargePlan;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.module.PxVipCardType;
import com.psi.easymanager.module.PxVipCardInfo;
import com.psi.easymanager.module.PxRechargeRecord;
import com.psi.easymanager.module.PxProductConfigPlan;
import com.psi.easymanager.module.PxProductConfigPlanRel;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxOrderNum;
import com.psi.easymanager.module.PxBestpay;
import com.psi.easymanager.module.PxSetInfo;
import com.psi.easymanager.module.PxBusinessHours;
import com.psi.easymanager.module.PxOptReason;
import com.psi.easymanager.module.PxWeiXinpay;
import com.psi.easymanager.module.PxAlipayInfo;
import com.psi.easymanager.module.PxComboGroup;
import com.psi.easymanager.module.PxComboProductRel;
import com.psi.easymanager.module.PxProductRemarks;
import com.psi.easymanager.module.PrintDetails;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PdConfigRel;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.PxVoucher;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.PxOperationLog;
import com.psi.easymanager.module.PxBuyCoupons;
import com.psi.easymanager.module.SmackUUIDRecord;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.BTPrintDevice;

import com.psi.easymanager.dao.PxDiscounSchemeDao;
import com.psi.easymanager.dao.PxTableInfoDao;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxPrinterInfoDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.PxProductCategoryDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductMethodRefDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxExtraDetailsDao;
import com.psi.easymanager.dao.PxTableAlterationDao;
import com.psi.easymanager.dao.PxPromotioInfoDao;
import com.psi.easymanager.dao.PxPromotioDetailsDao;
import com.psi.easymanager.dao.OfficeDao;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.PxExtraChargeDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.PxRechargePlanDao;
import com.psi.easymanager.dao.PxVipInfoDao;
import com.psi.easymanager.dao.PxVipCardTypeDao;
import com.psi.easymanager.dao.PxVipCardInfoDao;
import com.psi.easymanager.dao.PxRechargeRecordDao;
import com.psi.easymanager.dao.PxProductConfigPlanDao;
import com.psi.easymanager.dao.PxProductConfigPlanRelDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxOrderNumDao;
import com.psi.easymanager.dao.PxBestpayDao;
import com.psi.easymanager.dao.PxSetInfoDao;
import com.psi.easymanager.dao.PxBusinessHoursDao;
import com.psi.easymanager.dao.PxOptReasonDao;
import com.psi.easymanager.dao.PxWeiXinpayDao;
import com.psi.easymanager.dao.PxAlipayInfoDao;
import com.psi.easymanager.dao.PxComboGroupDao;
import com.psi.easymanager.dao.PxComboProductRelDao;
import com.psi.easymanager.dao.PxProductRemarksDao;
import com.psi.easymanager.dao.PrintDetailsDao;
import com.psi.easymanager.dao.PrintDetailsCollectDao;
import com.psi.easymanager.dao.PdConfigRelDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.PxVoucherDao;
import com.psi.easymanager.dao.PxPaymentModeDao;
import com.psi.easymanager.dao.EPaymentInfoDao;
import com.psi.easymanager.dao.PxOperationLogDao;
import com.psi.easymanager.dao.PxBuyCouponsDao;
import com.psi.easymanager.dao.SmackUUIDRecordDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.BTPrintDeviceDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig pxDiscounSchemeDaoConfig;
    private final DaoConfig pxTableInfoDaoConfig;
    private final DaoConfig pxFormatInfoDaoConfig;
    private final DaoConfig pxMethodInfoDaoConfig;
    private final DaoConfig pxPrinterInfoDaoConfig;
    private final DaoConfig pxProductInfoDaoConfig;
    private final DaoConfig pxProductCategoryDaoConfig;
    private final DaoConfig pxProductFormatRelDaoConfig;
    private final DaoConfig pxProductMethodRefDaoConfig;
    private final DaoConfig pxOrderInfoDaoConfig;
    private final DaoConfig pxOrderDetailsDaoConfig;
    private final DaoConfig pxExtraDetailsDaoConfig;
    private final DaoConfig pxTableAlterationDaoConfig;
    private final DaoConfig pxPromotioInfoDaoConfig;
    private final DaoConfig pxPromotioDetailsDaoConfig;
    private final DaoConfig officeDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig pxExtraChargeDaoConfig;
    private final DaoConfig pxTableExtraRelDaoConfig;
    private final DaoConfig pxRechargePlanDaoConfig;
    private final DaoConfig pxVipInfoDaoConfig;
    private final DaoConfig pxVipCardTypeDaoConfig;
    private final DaoConfig pxVipCardInfoDaoConfig;
    private final DaoConfig pxRechargeRecordDaoConfig;
    private final DaoConfig pxProductConfigPlanDaoConfig;
    private final DaoConfig pxProductConfigPlanRelDaoConfig;
    private final DaoConfig pxPayInfoDaoConfig;
    private final DaoConfig pxOrderNumDaoConfig;
    private final DaoConfig pxBestpayDaoConfig;
    private final DaoConfig pxSetInfoDaoConfig;
    private final DaoConfig pxBusinessHoursDaoConfig;
    private final DaoConfig pxOptReasonDaoConfig;
    private final DaoConfig pxWeiXinpayDaoConfig;
    private final DaoConfig pxAlipayInfoDaoConfig;
    private final DaoConfig pxComboGroupDaoConfig;
    private final DaoConfig pxComboProductRelDaoConfig;
    private final DaoConfig pxProductRemarksDaoConfig;
    private final DaoConfig printDetailsDaoConfig;
    private final DaoConfig printDetailsCollectDaoConfig;
    private final DaoConfig pdConfigRelDaoConfig;
    private final DaoConfig tableOrderRelDaoConfig;
    private final DaoConfig pxVoucherDaoConfig;
    private final DaoConfig pxPaymentModeDaoConfig;
    private final DaoConfig ePaymentInfoDaoConfig;
    private final DaoConfig pxOperationLogDaoConfig;
    private final DaoConfig pxBuyCouponsDaoConfig;
    private final DaoConfig smackUUIDRecordDaoConfig;
    private final DaoConfig pxTableAreaDaoConfig;
    private final DaoConfig bTPrintDeviceDaoConfig;

    private final PxDiscounSchemeDao pxDiscounSchemeDao;
    private final PxTableInfoDao pxTableInfoDao;
    private final PxFormatInfoDao pxFormatInfoDao;
    private final PxMethodInfoDao pxMethodInfoDao;
    private final PxPrinterInfoDao pxPrinterInfoDao;
    private final PxProductInfoDao pxProductInfoDao;
    private final PxProductCategoryDao pxProductCategoryDao;
    private final PxProductFormatRelDao pxProductFormatRelDao;
    private final PxProductMethodRefDao pxProductMethodRefDao;
    private final PxOrderInfoDao pxOrderInfoDao;
    private final PxOrderDetailsDao pxOrderDetailsDao;
    private final PxExtraDetailsDao pxExtraDetailsDao;
    private final PxTableAlterationDao pxTableAlterationDao;
    private final PxPromotioInfoDao pxPromotioInfoDao;
    private final PxPromotioDetailsDao pxPromotioDetailsDao;
    private final OfficeDao officeDao;
    private final UserDao userDao;
    private final PxExtraChargeDao pxExtraChargeDao;
    private final PxTableExtraRelDao pxTableExtraRelDao;
    private final PxRechargePlanDao pxRechargePlanDao;
    private final PxVipInfoDao pxVipInfoDao;
    private final PxVipCardTypeDao pxVipCardTypeDao;
    private final PxVipCardInfoDao pxVipCardInfoDao;
    private final PxRechargeRecordDao pxRechargeRecordDao;
    private final PxProductConfigPlanDao pxProductConfigPlanDao;
    private final PxProductConfigPlanRelDao pxProductConfigPlanRelDao;
    private final PxPayInfoDao pxPayInfoDao;
    private final PxOrderNumDao pxOrderNumDao;
    private final PxBestpayDao pxBestpayDao;
    private final PxSetInfoDao pxSetInfoDao;
    private final PxBusinessHoursDao pxBusinessHoursDao;
    private final PxOptReasonDao pxOptReasonDao;
    private final PxWeiXinpayDao pxWeiXinpayDao;
    private final PxAlipayInfoDao pxAlipayInfoDao;
    private final PxComboGroupDao pxComboGroupDao;
    private final PxComboProductRelDao pxComboProductRelDao;
    private final PxProductRemarksDao pxProductRemarksDao;
    private final PrintDetailsDao printDetailsDao;
    private final PrintDetailsCollectDao printDetailsCollectDao;
    private final PdConfigRelDao pdConfigRelDao;
    private final TableOrderRelDao tableOrderRelDao;
    private final PxVoucherDao pxVoucherDao;
    private final PxPaymentModeDao pxPaymentModeDao;
    private final EPaymentInfoDao ePaymentInfoDao;
    private final PxOperationLogDao pxOperationLogDao;
    private final PxBuyCouponsDao pxBuyCouponsDao;
    private final SmackUUIDRecordDao smackUUIDRecordDao;
    private final PxTableAreaDao pxTableAreaDao;
    private final BTPrintDeviceDao bTPrintDeviceDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        pxDiscounSchemeDaoConfig = daoConfigMap.get(PxDiscounSchemeDao.class).clone();
        pxDiscounSchemeDaoConfig.initIdentityScope(type);

        pxTableInfoDaoConfig = daoConfigMap.get(PxTableInfoDao.class).clone();
        pxTableInfoDaoConfig.initIdentityScope(type);

        pxFormatInfoDaoConfig = daoConfigMap.get(PxFormatInfoDao.class).clone();
        pxFormatInfoDaoConfig.initIdentityScope(type);

        pxMethodInfoDaoConfig = daoConfigMap.get(PxMethodInfoDao.class).clone();
        pxMethodInfoDaoConfig.initIdentityScope(type);

        pxPrinterInfoDaoConfig = daoConfigMap.get(PxPrinterInfoDao.class).clone();
        pxPrinterInfoDaoConfig.initIdentityScope(type);

        pxProductInfoDaoConfig = daoConfigMap.get(PxProductInfoDao.class).clone();
        pxProductInfoDaoConfig.initIdentityScope(type);

        pxProductCategoryDaoConfig = daoConfigMap.get(PxProductCategoryDao.class).clone();
        pxProductCategoryDaoConfig.initIdentityScope(type);

        pxProductFormatRelDaoConfig = daoConfigMap.get(PxProductFormatRelDao.class).clone();
        pxProductFormatRelDaoConfig.initIdentityScope(type);

        pxProductMethodRefDaoConfig = daoConfigMap.get(PxProductMethodRefDao.class).clone();
        pxProductMethodRefDaoConfig.initIdentityScope(type);

        pxOrderInfoDaoConfig = daoConfigMap.get(PxOrderInfoDao.class).clone();
        pxOrderInfoDaoConfig.initIdentityScope(type);

        pxOrderDetailsDaoConfig = daoConfigMap.get(PxOrderDetailsDao.class).clone();
        pxOrderDetailsDaoConfig.initIdentityScope(type);

        pxExtraDetailsDaoConfig = daoConfigMap.get(PxExtraDetailsDao.class).clone();
        pxExtraDetailsDaoConfig.initIdentityScope(type);

        pxTableAlterationDaoConfig = daoConfigMap.get(PxTableAlterationDao.class).clone();
        pxTableAlterationDaoConfig.initIdentityScope(type);

        pxPromotioInfoDaoConfig = daoConfigMap.get(PxPromotioInfoDao.class).clone();
        pxPromotioInfoDaoConfig.initIdentityScope(type);

        pxPromotioDetailsDaoConfig = daoConfigMap.get(PxPromotioDetailsDao.class).clone();
        pxPromotioDetailsDaoConfig.initIdentityScope(type);

        officeDaoConfig = daoConfigMap.get(OfficeDao.class).clone();
        officeDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        pxExtraChargeDaoConfig = daoConfigMap.get(PxExtraChargeDao.class).clone();
        pxExtraChargeDaoConfig.initIdentityScope(type);

        pxTableExtraRelDaoConfig = daoConfigMap.get(PxTableExtraRelDao.class).clone();
        pxTableExtraRelDaoConfig.initIdentityScope(type);

        pxRechargePlanDaoConfig = daoConfigMap.get(PxRechargePlanDao.class).clone();
        pxRechargePlanDaoConfig.initIdentityScope(type);

        pxVipInfoDaoConfig = daoConfigMap.get(PxVipInfoDao.class).clone();
        pxVipInfoDaoConfig.initIdentityScope(type);

        pxVipCardTypeDaoConfig = daoConfigMap.get(PxVipCardTypeDao.class).clone();
        pxVipCardTypeDaoConfig.initIdentityScope(type);

        pxVipCardInfoDaoConfig = daoConfigMap.get(PxVipCardInfoDao.class).clone();
        pxVipCardInfoDaoConfig.initIdentityScope(type);

        pxRechargeRecordDaoConfig = daoConfigMap.get(PxRechargeRecordDao.class).clone();
        pxRechargeRecordDaoConfig.initIdentityScope(type);

        pxProductConfigPlanDaoConfig = daoConfigMap.get(PxProductConfigPlanDao.class).clone();
        pxProductConfigPlanDaoConfig.initIdentityScope(type);

        pxProductConfigPlanRelDaoConfig = daoConfigMap.get(PxProductConfigPlanRelDao.class).clone();
        pxProductConfigPlanRelDaoConfig.initIdentityScope(type);

        pxPayInfoDaoConfig = daoConfigMap.get(PxPayInfoDao.class).clone();
        pxPayInfoDaoConfig.initIdentityScope(type);

        pxOrderNumDaoConfig = daoConfigMap.get(PxOrderNumDao.class).clone();
        pxOrderNumDaoConfig.initIdentityScope(type);

        pxBestpayDaoConfig = daoConfigMap.get(PxBestpayDao.class).clone();
        pxBestpayDaoConfig.initIdentityScope(type);

        pxSetInfoDaoConfig = daoConfigMap.get(PxSetInfoDao.class).clone();
        pxSetInfoDaoConfig.initIdentityScope(type);

        pxBusinessHoursDaoConfig = daoConfigMap.get(PxBusinessHoursDao.class).clone();
        pxBusinessHoursDaoConfig.initIdentityScope(type);

        pxOptReasonDaoConfig = daoConfigMap.get(PxOptReasonDao.class).clone();
        pxOptReasonDaoConfig.initIdentityScope(type);

        pxWeiXinpayDaoConfig = daoConfigMap.get(PxWeiXinpayDao.class).clone();
        pxWeiXinpayDaoConfig.initIdentityScope(type);

        pxAlipayInfoDaoConfig = daoConfigMap.get(PxAlipayInfoDao.class).clone();
        pxAlipayInfoDaoConfig.initIdentityScope(type);

        pxComboGroupDaoConfig = daoConfigMap.get(PxComboGroupDao.class).clone();
        pxComboGroupDaoConfig.initIdentityScope(type);

        pxComboProductRelDaoConfig = daoConfigMap.get(PxComboProductRelDao.class).clone();
        pxComboProductRelDaoConfig.initIdentityScope(type);

        pxProductRemarksDaoConfig = daoConfigMap.get(PxProductRemarksDao.class).clone();
        pxProductRemarksDaoConfig.initIdentityScope(type);

        printDetailsDaoConfig = daoConfigMap.get(PrintDetailsDao.class).clone();
        printDetailsDaoConfig.initIdentityScope(type);

        printDetailsCollectDaoConfig = daoConfigMap.get(PrintDetailsCollectDao.class).clone();
        printDetailsCollectDaoConfig.initIdentityScope(type);

        pdConfigRelDaoConfig = daoConfigMap.get(PdConfigRelDao.class).clone();
        pdConfigRelDaoConfig.initIdentityScope(type);

        tableOrderRelDaoConfig = daoConfigMap.get(TableOrderRelDao.class).clone();
        tableOrderRelDaoConfig.initIdentityScope(type);

        pxVoucherDaoConfig = daoConfigMap.get(PxVoucherDao.class).clone();
        pxVoucherDaoConfig.initIdentityScope(type);

        pxPaymentModeDaoConfig = daoConfigMap.get(PxPaymentModeDao.class).clone();
        pxPaymentModeDaoConfig.initIdentityScope(type);

        ePaymentInfoDaoConfig = daoConfigMap.get(EPaymentInfoDao.class).clone();
        ePaymentInfoDaoConfig.initIdentityScope(type);

        pxOperationLogDaoConfig = daoConfigMap.get(PxOperationLogDao.class).clone();
        pxOperationLogDaoConfig.initIdentityScope(type);

        pxBuyCouponsDaoConfig = daoConfigMap.get(PxBuyCouponsDao.class).clone();
        pxBuyCouponsDaoConfig.initIdentityScope(type);

        smackUUIDRecordDaoConfig = daoConfigMap.get(SmackUUIDRecordDao.class).clone();
        smackUUIDRecordDaoConfig.initIdentityScope(type);

        pxTableAreaDaoConfig = daoConfigMap.get(PxTableAreaDao.class).clone();
        pxTableAreaDaoConfig.initIdentityScope(type);

        bTPrintDeviceDaoConfig = daoConfigMap.get(BTPrintDeviceDao.class).clone();
        bTPrintDeviceDaoConfig.initIdentityScope(type);

        pxDiscounSchemeDao = new PxDiscounSchemeDao(pxDiscounSchemeDaoConfig, this);
        pxTableInfoDao = new PxTableInfoDao(pxTableInfoDaoConfig, this);
        pxFormatInfoDao = new PxFormatInfoDao(pxFormatInfoDaoConfig, this);
        pxMethodInfoDao = new PxMethodInfoDao(pxMethodInfoDaoConfig, this);
        pxPrinterInfoDao = new PxPrinterInfoDao(pxPrinterInfoDaoConfig, this);
        pxProductInfoDao = new PxProductInfoDao(pxProductInfoDaoConfig, this);
        pxProductCategoryDao = new PxProductCategoryDao(pxProductCategoryDaoConfig, this);
        pxProductFormatRelDao = new PxProductFormatRelDao(pxProductFormatRelDaoConfig, this);
        pxProductMethodRefDao = new PxProductMethodRefDao(pxProductMethodRefDaoConfig, this);
        pxOrderInfoDao = new PxOrderInfoDao(pxOrderInfoDaoConfig, this);
        pxOrderDetailsDao = new PxOrderDetailsDao(pxOrderDetailsDaoConfig, this);
        pxExtraDetailsDao = new PxExtraDetailsDao(pxExtraDetailsDaoConfig, this);
        pxTableAlterationDao = new PxTableAlterationDao(pxTableAlterationDaoConfig, this);
        pxPromotioInfoDao = new PxPromotioInfoDao(pxPromotioInfoDaoConfig, this);
        pxPromotioDetailsDao = new PxPromotioDetailsDao(pxPromotioDetailsDaoConfig, this);
        officeDao = new OfficeDao(officeDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        pxExtraChargeDao = new PxExtraChargeDao(pxExtraChargeDaoConfig, this);
        pxTableExtraRelDao = new PxTableExtraRelDao(pxTableExtraRelDaoConfig, this);
        pxRechargePlanDao = new PxRechargePlanDao(pxRechargePlanDaoConfig, this);
        pxVipInfoDao = new PxVipInfoDao(pxVipInfoDaoConfig, this);
        pxVipCardTypeDao = new PxVipCardTypeDao(pxVipCardTypeDaoConfig, this);
        pxVipCardInfoDao = new PxVipCardInfoDao(pxVipCardInfoDaoConfig, this);
        pxRechargeRecordDao = new PxRechargeRecordDao(pxRechargeRecordDaoConfig, this);
        pxProductConfigPlanDao = new PxProductConfigPlanDao(pxProductConfigPlanDaoConfig, this);
        pxProductConfigPlanRelDao = new PxProductConfigPlanRelDao(pxProductConfigPlanRelDaoConfig, this);
        pxPayInfoDao = new PxPayInfoDao(pxPayInfoDaoConfig, this);
        pxOrderNumDao = new PxOrderNumDao(pxOrderNumDaoConfig, this);
        pxBestpayDao = new PxBestpayDao(pxBestpayDaoConfig, this);
        pxSetInfoDao = new PxSetInfoDao(pxSetInfoDaoConfig, this);
        pxBusinessHoursDao = new PxBusinessHoursDao(pxBusinessHoursDaoConfig, this);
        pxOptReasonDao = new PxOptReasonDao(pxOptReasonDaoConfig, this);
        pxWeiXinpayDao = new PxWeiXinpayDao(pxWeiXinpayDaoConfig, this);
        pxAlipayInfoDao = new PxAlipayInfoDao(pxAlipayInfoDaoConfig, this);
        pxComboGroupDao = new PxComboGroupDao(pxComboGroupDaoConfig, this);
        pxComboProductRelDao = new PxComboProductRelDao(pxComboProductRelDaoConfig, this);
        pxProductRemarksDao = new PxProductRemarksDao(pxProductRemarksDaoConfig, this);
        printDetailsDao = new PrintDetailsDao(printDetailsDaoConfig, this);
        printDetailsCollectDao = new PrintDetailsCollectDao(printDetailsCollectDaoConfig, this);
        pdConfigRelDao = new PdConfigRelDao(pdConfigRelDaoConfig, this);
        tableOrderRelDao = new TableOrderRelDao(tableOrderRelDaoConfig, this);
        pxVoucherDao = new PxVoucherDao(pxVoucherDaoConfig, this);
        pxPaymentModeDao = new PxPaymentModeDao(pxPaymentModeDaoConfig, this);
        ePaymentInfoDao = new EPaymentInfoDao(ePaymentInfoDaoConfig, this);
        pxOperationLogDao = new PxOperationLogDao(pxOperationLogDaoConfig, this);
        pxBuyCouponsDao = new PxBuyCouponsDao(pxBuyCouponsDaoConfig, this);
        smackUUIDRecordDao = new SmackUUIDRecordDao(smackUUIDRecordDaoConfig, this);
        pxTableAreaDao = new PxTableAreaDao(pxTableAreaDaoConfig, this);
        bTPrintDeviceDao = new BTPrintDeviceDao(bTPrintDeviceDaoConfig, this);

        registerDao(PxDiscounScheme.class, pxDiscounSchemeDao);
        registerDao(PxTableInfo.class, pxTableInfoDao);
        registerDao(PxFormatInfo.class, pxFormatInfoDao);
        registerDao(PxMethodInfo.class, pxMethodInfoDao);
        registerDao(PxPrinterInfo.class, pxPrinterInfoDao);
        registerDao(PxProductInfo.class, pxProductInfoDao);
        registerDao(PxProductCategory.class, pxProductCategoryDao);
        registerDao(PxProductFormatRel.class, pxProductFormatRelDao);
        registerDao(PxProductMethodRef.class, pxProductMethodRefDao);
        registerDao(PxOrderInfo.class, pxOrderInfoDao);
        registerDao(PxOrderDetails.class, pxOrderDetailsDao);
        registerDao(PxExtraDetails.class, pxExtraDetailsDao);
        registerDao(PxTableAlteration.class, pxTableAlterationDao);
        registerDao(PxPromotioInfo.class, pxPromotioInfoDao);
        registerDao(PxPromotioDetails.class, pxPromotioDetailsDao);
        registerDao(Office.class, officeDao);
        registerDao(User.class, userDao);
        registerDao(PxExtraCharge.class, pxExtraChargeDao);
        registerDao(PxTableExtraRel.class, pxTableExtraRelDao);
        registerDao(PxRechargePlan.class, pxRechargePlanDao);
        registerDao(PxVipInfo.class, pxVipInfoDao);
        registerDao(PxVipCardType.class, pxVipCardTypeDao);
        registerDao(PxVipCardInfo.class, pxVipCardInfoDao);
        registerDao(PxRechargeRecord.class, pxRechargeRecordDao);
        registerDao(PxProductConfigPlan.class, pxProductConfigPlanDao);
        registerDao(PxProductConfigPlanRel.class, pxProductConfigPlanRelDao);
        registerDao(PxPayInfo.class, pxPayInfoDao);
        registerDao(PxOrderNum.class, pxOrderNumDao);
        registerDao(PxBestpay.class, pxBestpayDao);
        registerDao(PxSetInfo.class, pxSetInfoDao);
        registerDao(PxBusinessHours.class, pxBusinessHoursDao);
        registerDao(PxOptReason.class, pxOptReasonDao);
        registerDao(PxWeiXinpay.class, pxWeiXinpayDao);
        registerDao(PxAlipayInfo.class, pxAlipayInfoDao);
        registerDao(PxComboGroup.class, pxComboGroupDao);
        registerDao(PxComboProductRel.class, pxComboProductRelDao);
        registerDao(PxProductRemarks.class, pxProductRemarksDao);
        registerDao(PrintDetails.class, printDetailsDao);
        registerDao(PrintDetailsCollect.class, printDetailsCollectDao);
        registerDao(PdConfigRel.class, pdConfigRelDao);
        registerDao(TableOrderRel.class, tableOrderRelDao);
        registerDao(PxVoucher.class, pxVoucherDao);
        registerDao(PxPaymentMode.class, pxPaymentModeDao);
        registerDao(EPaymentInfo.class, ePaymentInfoDao);
        registerDao(PxOperationLog.class, pxOperationLogDao);
        registerDao(PxBuyCoupons.class, pxBuyCouponsDao);
        registerDao(SmackUUIDRecord.class, smackUUIDRecordDao);
        registerDao(PxTableArea.class, pxTableAreaDao);
        registerDao(BTPrintDevice.class, bTPrintDeviceDao);
    }
    
    public void clear() {
        pxDiscounSchemeDaoConfig.getIdentityScope().clear();
        pxTableInfoDaoConfig.getIdentityScope().clear();
        pxFormatInfoDaoConfig.getIdentityScope().clear();
        pxMethodInfoDaoConfig.getIdentityScope().clear();
        pxPrinterInfoDaoConfig.getIdentityScope().clear();
        pxProductInfoDaoConfig.getIdentityScope().clear();
        pxProductCategoryDaoConfig.getIdentityScope().clear();
        pxProductFormatRelDaoConfig.getIdentityScope().clear();
        pxProductMethodRefDaoConfig.getIdentityScope().clear();
        pxOrderInfoDaoConfig.getIdentityScope().clear();
        pxOrderDetailsDaoConfig.getIdentityScope().clear();
        pxExtraDetailsDaoConfig.getIdentityScope().clear();
        pxTableAlterationDaoConfig.getIdentityScope().clear();
        pxPromotioInfoDaoConfig.getIdentityScope().clear();
        pxPromotioDetailsDaoConfig.getIdentityScope().clear();
        officeDaoConfig.getIdentityScope().clear();
        userDaoConfig.getIdentityScope().clear();
        pxExtraChargeDaoConfig.getIdentityScope().clear();
        pxTableExtraRelDaoConfig.getIdentityScope().clear();
        pxRechargePlanDaoConfig.getIdentityScope().clear();
        pxVipInfoDaoConfig.getIdentityScope().clear();
        pxVipCardTypeDaoConfig.getIdentityScope().clear();
        pxVipCardInfoDaoConfig.getIdentityScope().clear();
        pxRechargeRecordDaoConfig.getIdentityScope().clear();
        pxProductConfigPlanDaoConfig.getIdentityScope().clear();
        pxProductConfigPlanRelDaoConfig.getIdentityScope().clear();
        pxPayInfoDaoConfig.getIdentityScope().clear();
        pxOrderNumDaoConfig.getIdentityScope().clear();
        pxBestpayDaoConfig.getIdentityScope().clear();
        pxSetInfoDaoConfig.getIdentityScope().clear();
        pxBusinessHoursDaoConfig.getIdentityScope().clear();
        pxOptReasonDaoConfig.getIdentityScope().clear();
        pxWeiXinpayDaoConfig.getIdentityScope().clear();
        pxAlipayInfoDaoConfig.getIdentityScope().clear();
        pxComboGroupDaoConfig.getIdentityScope().clear();
        pxComboProductRelDaoConfig.getIdentityScope().clear();
        pxProductRemarksDaoConfig.getIdentityScope().clear();
        printDetailsDaoConfig.getIdentityScope().clear();
        printDetailsCollectDaoConfig.getIdentityScope().clear();
        pdConfigRelDaoConfig.getIdentityScope().clear();
        tableOrderRelDaoConfig.getIdentityScope().clear();
        pxVoucherDaoConfig.getIdentityScope().clear();
        pxPaymentModeDaoConfig.getIdentityScope().clear();
        ePaymentInfoDaoConfig.getIdentityScope().clear();
        pxOperationLogDaoConfig.getIdentityScope().clear();
        pxBuyCouponsDaoConfig.getIdentityScope().clear();
        smackUUIDRecordDaoConfig.getIdentityScope().clear();
        pxTableAreaDaoConfig.getIdentityScope().clear();
        bTPrintDeviceDaoConfig.getIdentityScope().clear();
    }

    public PxDiscounSchemeDao getPxDiscounSchemeDao() {
        return pxDiscounSchemeDao;
    }

    public PxTableInfoDao getPxTableInfoDao() {
        return pxTableInfoDao;
    }

    public PxFormatInfoDao getPxFormatInfoDao() {
        return pxFormatInfoDao;
    }

    public PxMethodInfoDao getPxMethodInfoDao() {
        return pxMethodInfoDao;
    }

    public PxPrinterInfoDao getPxPrinterInfoDao() {
        return pxPrinterInfoDao;
    }

    public PxProductInfoDao getPxProductInfoDao() {
        return pxProductInfoDao;
    }

    public PxProductCategoryDao getPxProductCategoryDao() {
        return pxProductCategoryDao;
    }

    public PxProductFormatRelDao getPxProductFormatRelDao() {
        return pxProductFormatRelDao;
    }

    public PxProductMethodRefDao getPxProductMethodRefDao() {
        return pxProductMethodRefDao;
    }

    public PxOrderInfoDao getPxOrderInfoDao() {
        return pxOrderInfoDao;
    }

    public PxOrderDetailsDao getPxOrderDetailsDao() {
        return pxOrderDetailsDao;
    }

    public PxExtraDetailsDao getPxExtraDetailsDao() {
        return pxExtraDetailsDao;
    }

    public PxTableAlterationDao getPxTableAlterationDao() {
        return pxTableAlterationDao;
    }

    public PxPromotioInfoDao getPxPromotioInfoDao() {
        return pxPromotioInfoDao;
    }

    public PxPromotioDetailsDao getPxPromotioDetailsDao() {
        return pxPromotioDetailsDao;
    }

    public OfficeDao getOfficeDao() {
        return officeDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public PxExtraChargeDao getPxExtraChargeDao() {
        return pxExtraChargeDao;
    }

    public PxTableExtraRelDao getPxTableExtraRelDao() {
        return pxTableExtraRelDao;
    }

    public PxRechargePlanDao getPxRechargePlanDao() {
        return pxRechargePlanDao;
    }

    public PxVipInfoDao getPxVipInfoDao() {
        return pxVipInfoDao;
    }

    public PxVipCardTypeDao getPxVipCardTypeDao() {
        return pxVipCardTypeDao;
    }

    public PxVipCardInfoDao getPxVipCardInfoDao() {
        return pxVipCardInfoDao;
    }

    public PxRechargeRecordDao getPxRechargeRecordDao() {
        return pxRechargeRecordDao;
    }

    public PxProductConfigPlanDao getPxProductConfigPlanDao() {
        return pxProductConfigPlanDao;
    }

    public PxProductConfigPlanRelDao getPxProductConfigPlanRelDao() {
        return pxProductConfigPlanRelDao;
    }

    public PxPayInfoDao getPxPayInfoDao() {
        return pxPayInfoDao;
    }

    public PxOrderNumDao getPxOrderNumDao() {
        return pxOrderNumDao;
    }

    public PxBestpayDao getPxBestpayDao() {
        return pxBestpayDao;
    }

    public PxSetInfoDao getPxSetInfoDao() {
        return pxSetInfoDao;
    }

    public PxBusinessHoursDao getPxBusinessHoursDao() {
        return pxBusinessHoursDao;
    }

    public PxOptReasonDao getPxOptReasonDao() {
        return pxOptReasonDao;
    }

    public PxWeiXinpayDao getPxWeiXinpayDao() {
        return pxWeiXinpayDao;
    }

    public PxAlipayInfoDao getPxAlipayInfoDao() {
        return pxAlipayInfoDao;
    }

    public PxComboGroupDao getPxComboGroupDao() {
        return pxComboGroupDao;
    }

    public PxComboProductRelDao getPxComboProductRelDao() {
        return pxComboProductRelDao;
    }

    public PxProductRemarksDao getPxProductRemarksDao() {
        return pxProductRemarksDao;
    }

    public PrintDetailsDao getPrintDetailsDao() {
        return printDetailsDao;
    }

    public PrintDetailsCollectDao getPrintDetailsCollectDao() {
        return printDetailsCollectDao;
    }

    public PdConfigRelDao getPdConfigRelDao() {
        return pdConfigRelDao;
    }

    public TableOrderRelDao getTableOrderRelDao() {
        return tableOrderRelDao;
    }

    public PxVoucherDao getPxVoucherDao() {
        return pxVoucherDao;
    }

    public PxPaymentModeDao getPxPaymentModeDao() {
        return pxPaymentModeDao;
    }

    public EPaymentInfoDao getEPaymentInfoDao() {
        return ePaymentInfoDao;
    }

    public PxOperationLogDao getPxOperationLogDao() {
        return pxOperationLogDao;
    }

    public PxBuyCouponsDao getPxBuyCouponsDao() {
        return pxBuyCouponsDao;
    }

    public SmackUUIDRecordDao getSmackUUIDRecordDao() {
        return smackUUIDRecordDao;
    }

    public PxTableAreaDao getPxTableAreaDao() {
        return pxTableAreaDao;
    }

    public BTPrintDeviceDao getBTPrintDeviceDao() {
        return bTPrintDeviceDao;
    }

}
