package com.psi.easymanager.dao.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.psi.easymanager.dao.DaoMaster;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;

/**
 * Created by dorado on 2016/7/26.
 */
public class DbUpdateHelper extends DaoMaster.DevOpenHelper {
  public DbUpdateHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
    super(context, name, factory);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (1 >= oldVersion && 2 <= newVersion) {
      upgrade1To2(db);
    }
    if (2 >= oldVersion && 3 <= newVersion) {
      upgrade2To3(db);
    }
    if (3 >= oldVersion && 4 <= newVersion) {
      upgrade3To4(db);
    }
    if (4 >= oldVersion && 5 <= newVersion) {
      upgrade4To5(db);
    }
    if (5 >= oldVersion && 6 <= newVersion) {
      upgrade5To6(db);
    }
    if (6 >= oldVersion && 7 <= newVersion) {
      upgrade6To7(db);
    }
    if (7 >= oldVersion && 8 <= newVersion) {
      upgrade7To8(db);
    }
    if (8 >= oldVersion && 9 <= newVersion) {
      upgrade8To9(db);
    }
    if (9 >= oldVersion && 10 <= newVersion) {
      upgrade9To10(db);
    }
    if (10 >= oldVersion && 11 <= newVersion) {
      upgrade10To11(db);
    }
    if (11 >= oldVersion && 12 <= newVersion) {
      upgrade11To12(db);
    }
    if (12 >= oldVersion && 13 <= newVersion) {
      upgrade12To13(db);
    }
    if (13 >= oldVersion && 14 <= newVersion) {
      upgrade13To14(db);
    }
    if (14 >= oldVersion && 15 <= newVersion) {
      upgrade14To15(db);
    }
  }

  private void upgrade1To2(SQLiteDatabase db) {
    /**
     * OrderInfo添加支付金额字段
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'ALI_PAY' TEXT;");
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'WX_PAY' TEXT;");
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'BEST_PAY' TEXT;");
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'POS_PAY' TEXT;");
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'CASH_PAY' TEXT;");

    /**
     * PxProductConfigPlan添加打印份数字段
     */
    db.execSQL("ALTER TABLE 'PxProductConfigPlan' ADD 'COUNT' INTEGER;");

    /**
     * PxPayInfo添加凭证码
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'VOUCHER_CODE' TEXT;");

    /**
     * 删除Details里的COLLECT_ID
     */
    db.execSQL("ALTER TABLE 'OrderDetails' DROP COLUMN 'PX_ORDER_DETAILS_COLLECT_ID'; ");

    /**
     * 删除OrderDetailsCollect
     */
    db.execSQL("DROP TABLE 'OrderDetailsCollect';");

    /**
     * 重建OrderDetailsCollect
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS" + "\"OrderDetailsCollect\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"ORDER_TIME\" INTEGER," + "\"TYPE\" TEXT,"
        + "\"PX_ORDER_INFO_ID\" INTEGER," + "\"DB_CONFIG_ID\" INTEGER);");

    /**
     * 添加订单详情和配餐方案rel
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS" + "\"DetailsConfigRel\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"TYPE\" TEXT," + "\"OPERATE_TIME\" INTEGER,"
        + "\"IS_PRINTED\" INTEGER," + "\"IS_CLEAR\" INTEGER," + "\"DB_ORDER_DETAILS_ID\" INTEGER,"
        + "\"DB_CONFIG_ID\" INTEGER," + "\"ORDER_DETAIL_AND_CONFIG_REL_ID\" INTEGER);");

    //formatter:on
    /**
     * 会员充值表添加充值时间
     */
    db.execSQL("ALTER TABLE 'RechargeRecord' ADD 'RECHARGE_TIME' INTEGER;");

    /**
     * 会员充值表添加收银员
     */
    db.execSQL("ALTER TABLE 'RechargeRecord' ADD 'USER_ID' INTEGER NOT NULL;");

    /**
     * OrderInfo添加交接班状态
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'SHIFT_CHANGE_TYPE' TEXT;");

    /**
     * OrderInfo最终区域
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'FINAL_AREA' TEXT;");

    /**
     * OrderInfo结账收银员
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'CHECK_OUT_USER_ID' INTEGER NOT NULL;");

    /**
     * ProductInfo添加条码
     */
    db.execSQL("ALTER TABLE 'ProductInfo' ADD 'BAR_CODE' TEXT;");

    /**
     * 添加设置信息 SetInfo
     */
    db.execSQL(
        "CREATE TABLE " + "IF NOT EXISTS" + "\"PxSetInfo\" (" + "\"_id\" INTEGER PRIMARY KEY ASC ,"
            + "\"OBJECT_ID\" TEXT," + "\"MODEL\" TEXT," + "\"IS_FAST_OPEN_ORDER\" TEXT,"
            + "\"IS_AUTO_PRINT_CUSTOMER\" TEXT," + "\"IS_AUTO_SWITCH_CASH_BILL\" TEXT,"
            + "\"IS_AUTO_SWITCH_FIND_BILL\" TEXT," + "\"IS_PRINT_REFUND_BILL\" TEXT,"
            + "\"DEL_FLAG\" TEXT);");

    /**
     * 充值记录表添加交接班状态
     */
    db.execSQL("ALTER TABLE 'RechargeRecord' ADD 'SHIFT_CHANGE_TYPE' TEXT;");

    /**
     * 充值记录表添加充值类型
     */
    db.execSQL("ALTER TABLE 'RechargeRecord' ADD 'RECHARGE_TYPE' TEXT;");

    /**
     * 添加营业时间表
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS" + "\"BusinessHours\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"OBJECT_ID\" TEXT," + "\"BUSINESS_TYPE\" TEXT,"
        + "\"CLOSE_TIME\" TEXT," + "\"DEL_FLAG\" TEXT);");
  }

  private void upgrade2To3(SQLiteDatabase db) {
    /**
     * 添加操作原因表
     */
    db.execSQL(
        "CREATE TABLE " + "IF NOT EXISTS" + "\"OptReason\" (" + "\"_id\" INTEGER PRIMARY KEY ASC ,"
            + "\"OBJECT_ID\" TEXT," + "\"NAME\" TEXT," + "\"TYPE\" TEXT," + "\"DEL_FLAG\" TEXT);");
    /**
     * OrderDetails添加操作原因字段
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'PX_OPT_REASON_ID' INTEGER;");

    /**
     * User添加退菜权限
     */
    db.execSQL("ALTER TABLE 'User' ADD 'CAN_RETREAT' TEXT DEFAULT '1';");
  }

  private void upgrade3To4(SQLiteDatabase db) {
    /**
     * 商品添加剩余数量
     */
    db.execSQL("ALTER TABLE 'ProductInfo' ADD 'OVER_PLUS' REAL;");

    /**
     * 添加微信支付
     */
    db.execSQL(
        "CREATE TABLE " + "IF NOT EXISTS" + "\"PxWxPay\" (" + "\"_id\" INTEGER PRIMARY KEY ASC ,"
            + "\"OBJECT_ID\" TEXT," + "\"ORDER_TABLE\" TEXT," + "\"ORDER_NO\" TEXT,"
            + "\"ORDER_TIME\" TEXT," + "\"ORDER_EXPIRE_TIME\" TEXT," + "\"ORDER_STATUS\" TEXT,"
            + "\"ORDER_MONEY\" TEXT," + "\"ORDER_BARCODE\" TEXT," + "\"DEL_FLAG\" TEXT);");
    /**
     * 添加微信支付配置参数
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS" + "\"PxWeiXinpay\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"OBJECT_ID\" TEXT," + "\"KEY\" TEXT,"
        + "\"APP_ID\" TEXT," + "\"MAC_ID\" TEXT," + "\"DEL_FLAG\" TEXT);");
    /**
     * 添加支付宝商户 信息
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXIST" + "\"PxAlipayInfo\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"OBJECT_ID\" TEXT," + "\"ALIPAY_ACCOUNT\" +TEXT,"
        + "\"SELLER_ID\" + TEXT," + "\"DEL_FLAG\" TEXT );");

    /**
     * 商品分类添加类型
     */
    db.execSQL("ALTER TABLE 'ProductCategory' ADD 'TYPE' TEXT DEFAULT '0';");

    /**
     * 商品添加类型
     */
    db.execSQL("ALTER TABLE 'ProductInfo' ADD 'TYPE' TEXT DEFAULT '0';");

    /**
     * 套餐分组
     */
    db.execSQL(
        "CREATE TABLE " + "IF NOT EXIST" + "\"ComboGroup\" (" + "\"_id\" INTEGER PRIMARY KEY ASC ,"
            + "\"OBJECT_ID\" TEXT," + "\"NAME\" TEXT," + "\"TYPE\" TEXT," + "\"ALLOW_NUM\" INTEGER,"
            + "\"DEL_FLAG\" TEXT," + "\"DB_COMBO_ID\" INTEGER NOT NULL );");

    /**
     * 套餐分组和商品rel
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXIST" + "\"ComboProductRel\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"OBJECT_ID\" TEXT," + "\"DEL_FLAG\" TEXT,"
        + "\"NUM\" INTEGER," + "\"WEIGHT\" REAL," + "\"PX_COMBO_GROUP_ID\" INTEGER,"
        + "\"PX_PRODUCT_ID\" INTEGER," + "\"PX_FORMAT_ID\" INTEGER);");

    /**
     * OrderInfo 加锁
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'IS_LOCK' INTEGER;");

    /**
     * OrderDetails是否为套餐内details
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'IN_COMBO' TEXT DEFAULT '0';");

    /**
     * OrderDetails是否是套餐details
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'IS_COMBO_DETAILS' TEXT DEFAULT '0';");

    /**
     * OrderDetails所属套餐的DetailsId
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'PX_COMBO_DETAILS_ID' INTEGER;");

    /**
     * 是否为套餐临时Details
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'IS_COMBO_TEMPORARY_DETAILS' INTEGER;");

    /**
     * Details添加类型(可选或必选)
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'CHOOSE_TYPE' TEXT;");
  }

  private void upgrade4To5(SQLiteDatabase db) {
    /**
     * 规格添加余量
     */
    db.execSQL("ALTER TABLE 'ProductFormatRel' ADD 'STOCK' REAL;");

    /**
     * 规格添加状态
     */
    db.execSQL("ALTER TABLE 'ProductFormatRel' ADD 'STATUS' TEXT;");

    /**
     * 商品添加上架状态
     */
    db.execSQL("ALTER TABLE 'ProductInfo' ADD 'SHELF' TEXT DEFAULT '0';");

    /**
     * 商品添加是否在微信点餐页面显示
     */
    db.execSQL("ALTER TABLE 'ProductInfo' ADD 'VISIBLE' TEXT DEFAULT '0';");

    /**
     * 商品分类添加上架状态
     */
    db.execSQL("ALTER TABLE 'ProductCategory' ADD 'SHELF' TEXT DEFAULT '0';");

    /**
     * 商品分类添加是否在微信点餐页面显示
     */
    db.execSQL("ALTER TABLE 'ProductCategory' ADD 'VISIBLE' TEXT DEFAULT '0';");
  }

  private void upgrade5To6(SQLiteDatabase db) {
    /**
     * OrderDetails是否为赠品
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'IS_GIFT' TEXT DEFAULT '0';");

    /**
     * 添加商品备注表
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"ProductRemarks\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"OBJECT_ID\" TEXT," + "\"DEL_FLAG\" TEXT,"
        + "\"REMARKS\" TEXT);");

    /**
     * OrderDetails添加备注字段
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'REMARKS' TEXT DEFAULT '无';");

    /**
     * OrderInfo添加备注字段
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'REMARKS' TEXT DEFAULT '无';");
  }

  private void upgrade6To7(SQLiteDatabase db) {
    /**
     * 清除订单数据
     */
    DaoServiceUtil.getOrderDetailsService().deleteAll();
    DaoServiceUtil.getOrderInfoService().deleteAll();
    DaoServiceUtil.getExtraDetailsService().deleteAll();
    DaoServiceUtil.getPayInfoService().deleteAll();
    /**
     * OrderDetails添加单价字段
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'UNIT_PRICE' REAL;");
    /**
     * OrderDetails添加会员单价字段
     */
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'UNIT_VIP_PRICE' REAL;");
    /**
     * 删除OrderDetailsCollect表
     */
    db.execSQL("DROP TABLE 'OrderDetailsCollect';");
    /**
     * 删除DetailsConfigRel表
     */
    db.execSQL("DROP TABLE 'DetailsConfigRel';");
    /**
     * 创建PrintDetails表
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PrintDetails\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"STATUS\" TEXT," + // 1: status
        "\"ORDER_STATUS\" TEXT," + // 2: orderStatus
        "\"NUM\" REAL," + // 3: num
        "\"MULTIPLE_UNIT_NUMBER\" REAL," + // 4: multipleUnitNumber
        "\"REMARKS\" TEXT," + // 5: remarks
        "\"IN_COMBO\" TEXT," + // 6: inCombo
        "\"PX_PRODUCT_INFO_ID\" INTEGER," + // 7: pxProductInfoId
        "\"PX_ORDER_INFO_ID\" INTEGER," + // 8: pxOrderInfoId
        "\"PX_OPT_REASON_ID\" INTEGER," + // 9: pxOptReasonId
        "\"PX_FORMAT_INFO_ID\" INTEGER," + // 10: pxFormatInfoId
        "\"PX_METHOD_INFO_ID\" INTEGER);"); // 11: pxMethodInfoId
    /**
     * 创建PrintDetailsCollect表
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PrintDetailsCollect\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"OPERATE_TIME\" INTEGER," + // 1: operateTime
        "\"IS_PRINT\" INTEGER," + // 2: isPrint
        "\"TYPE\" TEXT," + // 3: type
        "\"PX_ORDER_INFO_ID\" INTEGER," + // 4: pxOrderInfoId
        "\"DB_CONFIG_ID\" INTEGER);"); // 5: dbConfigId

    /**
     * 创建PdConfigRel表
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PdConfigRel\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"TYPE\" TEXT," + // 1: type
        "\"OPERATE_TIME\" INTEGER," + // 2: operateTime
        "\"IS_PRINTED\" INTEGER," + // 3: isPrinted
        "\"PX_ORDER_INFO_ID\" INTEGER," + // 4: pxOrderInfoId
        "\"DB_PRINT_DETAILS_ID\" INTEGER," + // 5: dbPrintDetailsId
        "\"DB_CONFIG_ID\" INTEGER," + // 6: dbConfigId
        "\"PD_CONFIG_REL_ID\" INTEGER);"); // 7: PdConfigRelId

    /**
     * 创建桌台订单rel表
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"TableOrderRel\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"ORDER_END_TIME\" INTEGER," + // 1: orderEndTime
        "\"PX_ORDER_INFO_ID\" INTEGER," + // 2: pxOrderInfoId
        "\"PX_TABLE_INFO_ID\" INTEGER);"); // 3: pxTableInfoId

    /**
     * 取消订单和桌台直接关联
     */
    db.execSQL("Alter Table OrderInfo Drop Column PX_TABLE_INFO_ID");
    db.execSQL("Alter Table TableInfo Drop Column PX_ORDER_INFO_ID");

    /**
     * 优惠券
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PxVoucher\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"DEL_FLAG\" TEXT," + // 1: delFlag
        "\"OBJECT_ID\" TEXT," + // 2: objectId
        "\"CODE\" TEXT," + // 3: code
        "\"PRICE\" REAL," + // 4: price
        "\"DERATE_PRICE\" REAL," + // 5: deratePrice
        "\"TYPE\" TEXT," + // 6: type
        "\"START_DATE\" INTEGER," + // 7: startDate
        "\"END_DATE\" INTEGER," + // 8: endDate
        "\"PERMANENT\" TEXT);"); // 9: permanent

    /**
     * 订单添加优惠券金额
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'VOUCHER_MONEY' REAL;");

    /**
     * 打印机加钱箱配置、
     */
    db.execSQL("ALTER TABLE 'PrinterInfo' ADD 'CASH_BOX' TEXT DEFAULT '0';");

    /**
     * 打印机 加报警
     */
    db.execSQL("ALTER TABLE 'PrinterInfo' ADD 'SOUND' TEXT DEFAULT '0';");

    /**
     * 订单 是否反结账
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'IS_REVERSED' TEXT DEFAULT '0';");

    /**
     * 订单 预订单字段 orderType、linkMan、contactPhone、diningTime、reserveState
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'IS_RESERVE_ORDER' TEXT DEFAULT '0';");
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'LINK_MAN' TEXT ;");
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'CONTACT_PHONE' TEXT ;");
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'DINING_TIME' INTEGER ;");
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'RESERVE_STATE' TEXT ;");

    /**
     * 删除PxSetInfo
     */
    db.execSQL("DROP TABLE 'PxSetInfo';");

    /**
     * 重新创建PxSetInfo
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PxSetInfo\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"OBJECT_ID\" TEXT," + // 1: objectId
        "\"DEL_FLAG\" TEXT," + // 2: delFlag
        "\"MODEL\" TEXT," + // 3: model
        "\"IS_FAST_OPEN_ORDER\" TEXT," + // 4: isFastOpenOrder
        "\"IS_AUTO_TURN_CHECKOUT\" TEXT," + // 5: isAutoTurnCheckout
        "\"AUTO_ORDER\" TEXT," + // 6: autoOrder
        "\"OVER_AUTO_START_BILL\" TEXT," + // 7: overAutoStartBill
        "\"IS_AUTO_PRINT_RECHARGE_VOUCHER\" TEXT);"); // 8: isAutoPrintRechargeVoucher

    /**
     * 支付方式
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PaymentMode\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"OBJECT_ID\" TEXT," + // 1: objectId
        "\"DEL_FLAG\" TEXT," + // 2: delFlag
        "\"NAME\" TEXT," + // 3: name
        "\"TYPE\" TEXT," + // 4: type
        "\"SALES_AMOUNT\" TEXT," + // 5: salesAmount
        "\"EDIT\" TEXT," + // 6: edit
        "\"ORDER_NO\" TEXT," + // 7: orderNo
        "\"OPEN_BOX\" TEXT);"); // 8: openBox

    /**
     * 支付信息添加流水号
     */

    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'TRADE_NO' TEXT;");

    /**
     * 支付信息添加备注字段
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'REMARKS' TEXT;");

    /**
     * 支付信息添加会员手机
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'VIP_MOBILE' TEXT;");

    /**
     * 支付信息添加会员ID
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'VIP_ID' TEXT;");

    /**
     * 支付信息取消和OrderDetails关联
     */
    db.execSQL("ALTER TABLE 'OrderDetails' DROP COLUMN 'PX_PAY_INFO_ID';");

    /**
     * 支付信息取消和ExtraDetails关联
     */
    db.execSQL("ALTER TABLE 'ExtraDetails' DROP COLUMN 'PX_PAY_INFO_ID';");

    /**
     * 订单删除和会员关联
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'PX_VIP_INFO_ID';");

    /**
     * 订单删除会员NO
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'VIP_NO';");

    /**
     * 订单删除会员支付金额
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'VIP_CARD_PAY';");

    /**
     * 订单删除现金支付金额
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'CASH_PAY';");

    /**
     * 订单删除支付宝支付金额
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'ALI_PAY';");

    /**
     * 订单删除微信支付金额
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'WX_PAY';");

    /**
     * 订单删除银行卡支付金额
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'BEST_PAY';");

    /**
     * 订单删除翼支付支付金额
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'POS_PAY';");

    /**
     * 定案删除其他支付金额
     */
    db.execSQL("ALTER TABLE 'OrderInfo' DROP COLUMN 'OTHER_PAY';");

    /**
     * 公司 parentIds下级机构   parentId上级机构
     */
    db.execSQL("ALTER TABLE 'Office' ADD 'PARENT_IDS' TEXT;");
    db.execSQL("ALTER TABLE 'Office' ADD 'PARENT_ID' TEXT;");

    /**
     * 添加电子支付信息表
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"EPaymentInfo\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"TYPE\" TEXT," + // 1: type
        "\"PAY_TIME\" INTEGER," + // 2: payTime
        "\"ORDER_NO\" TEXT," + // 3: orderNo
        "\"TABLE_NAME\" TEXT," + // 4: tableName
        "\"STATUS\" TEXT," + // 5: status
        "\"TRADE_NO\" TEXT," + // 6: tradeNo
        "\"PRICE\" REAL," + // 7: price
        "\"IS_HANDLED\" TEXT," + // 8: isHandled
        "\"PAY_INFO_ID\" INTEGER," + // 9: payInfoId
        "\"ORDER_INFO_ID\" INTEGER);"); // 10: orderInfoId
    /**
     * 删除微信 支付记录
     */
    db.execSQL("DROP TABLE 'PxWxPay';");

    /**
     * 删除支付宝 支付记录
     */
    db.execSQL("DROP TABLE 'PxAlipayRecord';");

    /**
     * 删除翼支付 支付记录
     */
    db.execSQL("DROP TABLE 'PxWingPay';");

    /**
     * 支付表添加 支付方式Id
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'PAYMENT_ID' TEXT;");

    /**
     * 支付表添加 付方式类型
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'PAYMENT_TYPE' TEXT;");

    /**
     * 支付表添加 支付方式名称
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'PAYMENT_NAME' TEXT;");

    /**
     * 支付表添加 是否计算入销售额
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'SALES_AMOUNT' TEXT;");

    /**
     * 商品添加销售数量
     */
    db.execSQL("ALTER TABLE 'ProductInfo' ADD 'SALE_NUM' INTEGER;");

    /**
     * PayInfo删除type
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' DROP COLUMN 'TYPE';");
  }

  private void upgrade7To8(SQLiteDatabase db) {
    /**
     * 操作记录表
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"OperationLog\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"ORDER_NO\" TEXT," + // 1: orderNo
        "\"OPERATER\" TEXT," + // 2: operater
        "\"PRODUCT_NAME\" TEXT," + // 3: productName
        "\"TYPE\" TEXT," + // 4: type
        "\"REMARKS\" TEXT," + // 5: remarks
        "\"OPERATER_DATE\" INTEGER," + // 6: operaterDate
        "\"CID\" TEXT," + // companyID
        "\"TOTAL_PRICE\" REAL);");  // 7: totalPrice
    /**
     * 商品添加是否标签打印
     */
    db.execSQL("ALTER TABLE 'ProductInfo' ADD 'IS_LABEL' TEXT DEFAULT '0';");
  }

  private void upgrade8To9(SQLiteDatabase db) {
    /**
     * 配置选项 财务联是否打印分类统计信息
     */
    db.execSQL("ALTER TABLE 'PxSetInfo' ADD 'IS_FINANCE_PRINT_CATEGORY' TEXT DEFAULT '2';");

    /**
     * 订单添加 支付类优惠
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'PAY_PRIVILEGE' REAL DEFAULT 0.0;");

    /**
     * 支付信息添加 支付类优惠
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'PAY_PRIVILEGE' REAL;");

    /**
     * 团购券
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PxBuyCoupons\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"OBJECT_ID\" TEXT," + // 1: objectId
        "\"DEL_FLAG\" TEXT," + // 2: delFlag
        "\"NAME\" TEXT," + // 3: name
        "\"AMOUNT\" REAL," + // 4: amount
        "\"OFFSET_AMOUNT\" REAL," + // 5: offsetAmount
        "\"PAYMENT_MODE_ID\" INTEGER);"); // 6: paymentModeId

    /**
     * PayInfo添加验券码
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'TICKET_CODE' TEXT;");

    /**
     * 商品规格rel添加条码
     */
    db.execSQL("ALTER TABLE 'ProductFormatRel' ADD 'BAR_CODE' TEXT;");

    /**
     * 修改PaymentMode表结构
     * OrderNo由Sting改为Integer
     */
    db.execSQL("ALTER TABLE 'PaymentMode' RENAME TO 'PaymentModeTemporary';");
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PaymentMode\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"OBJECT_ID\" TEXT," + "\"DEL_FLAG\" TEXT,"
        + "\"NAME\" TEXT," + "\"TYPE\" TEXT," + "\"SALES_AMOUNT\" TEXT," + "\"EDIT\" TEXT,"
        + "\"ORDER_NO\" INTEGER," + "\"OPEN_BOX\" TEXT);");
    db.execSQL("INSERT INTO 'PaymentMode'"
        + " (_id,OBJECT_ID,DEL_FLAG,NAME,TYPE,SALES_AMOUNT,EDIT,ORDER_NO,OPEN_BOX) SELECT _id,OBJECT_ID,DEL_FLAG,NAME,TYPE,SALES_AMOUNT,EDIT,ORDER_NO,OPEN_BOX"
        + " FROM PaymentModeTemporary;");
    db.execSQL("DROP TABLE PaymentModeTemporary");
  }

  private void upgrade9To10(SQLiteDatabase db) {
    db.execSQL("Update PxPayInfo Set PAY_PRIVILEGE = " + 0.0 + " Where PAY_PRIVILEGE is null;");
  }

  private void upgrade10To11(SQLiteDatabase db) {
  }

  private void upgrade11To12(SQLiteDatabase db) {
    // SmackUUID 记录表
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"SmackUUIDRecord\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"OPERATE_TIME\" INTEGER," + "\"UUID\" TEXT);");
  }

  private void upgrade12To13(SQLiteDatabase db) {
    db.execSQL("Update PxPayInfo Set PAY_PRIVILEGE = " + 0.0 + " Where PAY_PRIVILEGE is null;");
  }

  private void upgrade13To14(SQLiteDatabase db) {
    /**
     * 商品添加短名称
     */
    db.execSQL("ALTER TABLE 'ProductInfo' ADD 'SHORT_NAME' TEXT;");
    /**
     * PxPromotioInfo 加delFlag、startData、endDate
     */
    db.execSQL("ALTER TABLE 'PromotioInfo' ADD 'DEL_FLAG' TEXT;");
    db.execSQL("ALTER TABLE 'PromotioInfo' ADD 'START_DATE' INTEGER;");
    db.execSQL("ALTER TABLE 'PromotioInfo' ADD 'END_DATE' INTEGER;");

    /**
     * PxPromotioDetails  加delFlag
     */
    db.execSQL("ALTER TABLE 'PromotioDetails' ADD 'DEL_FLAG' TEXT;");
    db.execSQL("ALTER TABLE 'PromotioDetails' ADD 'PX_FORMAT_ID' INTEGER;");

    /**
     * OrderInfo 加 promotioInfo
     */
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'PX_PROMOTIO_ID' INTEGER;");

    /**
     * 建PxTableArea
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"PxTableArea\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ," + "\"OBJECT_ID\" TEXT," + "\"TYPE\" TEXT,"
        + "\"NAME\" TEXT," + "\"DEL_FLAG\" TEXT);");
    /**
     * PayInfo添加idCardNum
     */
    db.execSQL("ALTER TABLE 'PxPayInfo' ADD 'ID_CARD_NUM' TEXT;");
    /**
     * RechargeRecord加score
     */
    db.execSQL("ALTER TABLE 'RechargeRecord' ADD 'SCORE' INTEGER;");
    /**
     * 建PxVipCardInfo
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"VipCardInfo\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"OBJECT_ID\" TEXT," + // 1: objectId
        "\"IDCARD_NUM\" TEXT," + // 2: idcardNum
        "\"CARD_NUM\" TEXT," + // 3: cardNum
        "\"PASSWORD\" TEXT," + // 4: password
        "\"MOBILE\" TEXT," + // 5: mobile
        "\"RECHARGE_MONEY\" REAL," + // 6: rechargeMoney
        "\"RECEIVED_MONEY\" REAL," + // 7: receivedMoney
        "\"ACCOUNT_BALANCE\" REAL," + // 8: accountBalance
        "\"STATUS\" TEXT," + // 9: status
        "\"SCORE\" INTEGER," + // 10: score
        "\"PID\" TEXT," + // 11: pid
        "\"DEL_FLAG\" TEXT," + // 12: delFlag
        "\"PX_VIP_CARD_TYPE_ID\" INTEGER);"); // 13: pxVipCardTypeId

    /**
     * 建PxVipCardType
     */
    db.execSQL("CREATE TABLE " + "IF NOT EXISTS " + "\"VipCardType\" (" + //
        "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
        "\"OBJECT_ID\" TEXT," + // 1: objectId
        "\"NAME\" TEXT," + // 2: name
        "\"TYPE\" TEXT," + // 3: type
        "\"MARGIN_FOREGIFT\" INTEGER," + // 4: marginForegift
        "\"RECHARGE_SCORE\" INTEGER," + // 5: rechargeScore
        "\"REQUIRE_PASSWORD\" TEXT," + // 6: requirePassword
        "\"CONSUME_SEND_SCORE\" TEXT," + // 7: consumeSendScore
        "\"DISCOUNT_RATE\" INTEGER," + // 8: discountRate
        "\"CONSUME_SCORE\" INTEGER," + // 9: consumeScore
        "\"DEL_FLAG\" TEXT," + // 10: delFlag
        "\"PX_DISCOUN_SCHEME_ID\" INTEGER);"); // 11: pxDiscounSchemeId
  }

  /**
   * 14 ->15
   */
  //@formatter:off
  private void upgrade14To15(SQLiteDatabase db) {
    //BTPrintDevice
    db.execSQL("Create table if not exists " + "\"BTPrintDevice\" ("
        + "\"_id\" INTEGER PRIMARY KEY ASC ,"
        + "\"address\" TEXT,"
        + "\"format\" TEXT);");
    //OrderDetails加退菜数量
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'REFUND_NUM' REAL DEFAULT 0.0;");
    db.execSQL("ALTER TABLE 'OrderDetails' ADD 'REFUND_MULT_NUM' REAL DEFAULT 0.0;");
    //OrderInfo  订单类型 (0:默认订单 1：微信点餐(到店) 2：微信点菜(外卖))
    db.execSQL("ALTER TABLE 'OrderInfo' ADD 'TYPE' TEXT DEFAULT '0';");

    // delete printDetails + PdConfigRel + PrintDetailsCollect
    db.delete("PrintDetails",null,null);
    db.delete("PrintDetailsCollect",null,null);
    db.delete("PdConfigRel",null,null);

    //db.execSQL("ALTER TABLE 'PrintDetails' ADD ('FORMAT_NAME' TEXT , 'METHOD_NAME' TEXT , 'REASON_NAME' TEXT)");
    db.execSQL("ALTER TABLE 'PrintDetails' ADD 'FORMAT_NAME' TEXT;");
    db.execSQL("ALTER TABLE 'PrintDetails' ADD 'METHOD_NAME' TEXT;");
    db.execSQL("ALTER TABLE 'PrintDetails' ADD 'REASON_NAME' TEXT;");
  }
}
