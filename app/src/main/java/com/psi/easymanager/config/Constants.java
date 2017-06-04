package com.psi.easymanager.config;

/**
 * 常量类
 * Created by zjq on 16/1/4.
 */
public class Constants {

  /**
   * 每一页菜单数量
   */
  public static final int ONE_PAGE_MENU_NUM = 16;

  /**
   * 加密盐
   */
  public static final String SALT = "ap4rf7ax";

  /**
   * Smack 端口
   */
  public static final int PORT = 5222;

  //TAG 用于标记添加到栈内的Fragment

  /**
   * TAG-CashBillFragment
   */
  public static final String CASH_BILL_TAG = "CashBill";

  /**
   * TAG-CashMenuFragment
   */
  public static final String CASH_MENU_TAG = "CashMenu";

  /**
   * TAG-FindBillFragment
   */
  public static final String FIND_BILL_TAG = "FindBill";

  /**
   * TAG-CheckOutFragment
   */
  public static final String CHECK_OUT_TAG = "CheckOut";

  /**
   * TAG-AboutFireFragment
   */
  public static final String ABOUT_FIRE_TAG = "AboutFire";

  /**
   * TAG-BusinessModelFragment
   */
  public static final String BUSINESS_MODEL_TAG = "BusinessModel";
  /**
   * TAG-LabelPrintFragment
   */
  public static final String LABEL_PRINT_TAG = "LabelPrintModel";
  /**
   * TAG-BTPrintSettingFragment
   */
  public static final String BT_PRINT_TAG = "BTPrintTag";
  /**
   * TAG-OrdinaryPrinterFragment
   */
  public static final String ORDINARY_PRINTER_TAG = "OrdinaryPrinter";

  /**
   * TAG-PermissionSetFragment
   */
  public static final String PERMISSION_SET_TAG = "PermissionSet";

  /**
   * TAG-UploadSettingFragment
   */
  public static final String UPLOAD_SETTING_TAG = "UploadSetting";

  /**
   * TAG-StartBillFragment
   */
  public static final String START_BILL_TAG = "StartBill";

  /**
   * TAG-ModifyBillFragment
   */
  public static final String MODIFY_BILL_TAG = "ModifyBill";

  /**
   * TAG-OverBillSaleListFragment
   */
  public static final String OVER_BILL_SALE_LIST = "OverBillSaleList";

  /**
   * TAG-OverBillSaleContentFragment
   */
  public static final String OVER_BILL_SALE_CONTENT = "OverBillSaleContent";

  /**
   * TAG-OverBillCollectionListFragment
   */
  public static final String OVER_BILL_COLLECTION_LIST = "OverBillCollectionList";

  /**
   * TAG-OverBillCollectionContentFragment
   */
  public static final String OVER_BILL_COLLECTION_CONTENT = "OverBillCollectionContent";

  /**
   * TAG-OverBillDetailListFragment
   */
  public static final String OVER_BILL_DETAIL_LIST = "OverBillDetailList";

  /**
   * TAG-OverBillDetailsContentFragment
   */
  public static final String OVER_BILL_DETAILS_CONTENT = "OverBillDetailsContent";
  /**
   * TAG-AddReserveFragment
   */
  public static final String ADD_RESERVE = "AddReserve";
  /**
   * TAG-ModifyReserveFragment
   */
  public static final String MODIFY_RESERVE = "ModifyReserve";
  /**
   * TAG-ReserveDetail
   */
  public static final String RESERVE_DETAIL = "ReserveDetail";
  /**
   * TAG-AddMember
   */
  public static final String ADD_MEMBER_TAG = "AddMember";

  /**
   * TAG-VipInfoCenterOperation
   */
  public static final String VIPINFOOPERATIONCENTER = "VipInfoOperationCenter";
  /**
   * TAG-VipInfoChargeAndRecharge
   */
  public static final String VIPINFOCHARGEANDRECHARGE = "VipInfoChargeAndRecharge";

  /**
   * TAG-MemberDetail
   */
  public static final String MEMBER_DETAIL_TAG = "MemberDetail";

  /**
   * TAG-MemberFuzzyQuery
   */
  public static final String MEMBER_FUZZY_QUERY_TAG = "MemberFuzzyQuery";

  /**
   * TAG-CashMenuFuzzyQuery
   */
  public static final String CASH_MENU_FUZZY_QUERY_TAG = "CashMenuFuzzyQuery";

  /**
   * TAG-CustomProduct
   */
  public static final String CUSTOM_PRODUCT_TAG = "CustomProduct";

  /**
   * TAG-WingPayDetail
   */
  public static final String WING_PAY_DETAIL_TAG = "WingPayDetail";

  /**
   * TAG-WxPayDetail
   */
  public static final String WX_PAY_DETAIL_TAG = "WxPayDetail";

  /**
   * TAG-AddCombo
   */
  public static final String ADD_COMBO = "AddCombo";

  /**
   * TAG-EditCombo
   */
  public static final String EDIT_COMBO = "EditCombo";

  /**
   * Tag-ShiftOrderCollect
   */
  public static final String SHIFT_ORDER_COLLECT_TAG = "ShiftOrderCollect";

  /**
   * Tag-ShiftCateCollect
   */
  public static final String SHIFT_CATE_COLLECT_TAG = "ShiftCateCollect";

  /**
   * Tag-ShiftBillCollect
   */
  public static final String SHIFT_BILL_COLLECT_TAG = "ShiftBillCollect";

  /**
   * Tag-ExistCombo
   */
  public static final String EXIST_COMBO_TAG = "ExistCombo";

  //Switch 用于控制启动时tab开关的标记

  /**
   * Switch-OrdinaryPrint
   */
  public static final String SWITCH_ORDINARY_PRINT = "SwitchOrdinaryPrint";

  /**
   * init_storeNum
   */
  public static String INIT_STORE_NUM = "InitStoreNum";

  /**
   * init_storePsd
   */
  public static String INIT_STORE_PSD = "InitStorePsd";
  /**
   * save store Num
   */
  public static String SAVE_STORE_NUM = "storeNum";
  /**
   * save store Pwd
   */
  public static String SAVE_STORE_PWD = "storePwd";
  /**
   * save login Name
   */
  public static String SAVE_LOGIN_NAME = "loginUserName";
  /**
   * save login Pwd
   */
  public static String SAVE_LOGIN_PWD = "loginUserPwd";
  /**
   * remember pwd
   */
  public static String REMEMBER_PWD = "rememberPwd";
  /**
   * is init
   */
  public static String IS_INIT = "isInit";

  /**
   * 上次上传时间
   */
  public static String LAST_UPLOAD_TIME = "lastUploadTime";
  /**
   * 上次更新时间
   */
  public static String LAST_UPDATE_TIME = "lastUpdateTime";

  /**
   * Crash log文件名
   */
  public static final String LOG_NAME = "errorLog.txt";
  /**
   * 是否支持USB打印  0：支持  1：不支持
   */
  public static final String SUPPORT_USB_PRINT = "supportUSBPrint";

  /**
   * /**
   * 旧版本
   */
  public static final String OLD_VERSION_CODE = "oldVersionCode";
  /**
   * 会员中心 请求字段
   */

  public static final String VIP = "vip";
  /**
   * 会员中心 请求 会员列表字段
   */
  public static final String LIST = "list";

  /**
   * 会员中心 请求充值方案
   */
  public static final String RECHARGEPLANLIST = "rechargePlanList";

  /**
   * 会员中心 请求充值记录
   */

  public static final String RECHARGERECORDLIST = "rechargeRecordList";

  /**
   * 会员中心 充值记录冲正
   */

  public static final String REVERSE = "reverse";

  /**
   * 会员中心 获取单个会员的信息
   */
  public static final String GETSINGLEVIPINFO = "getSingleVipInfo";
  /**
   * 是否开启标签打印机
   */
  public static final String SWITCH_LABEL_PRINT = "switchLabelPrint";
  /**
   * 标签打印机IP
   */
  public static final String LABEL_PRINTER_IP = "labelPrinterIp";
  /**
   * 标签纸类型 宽高 间隔
   */
  public static final String LABEL_PRINTER_PAPER_WIDTH = "labelPrinterPaperWidth";
  public static final String LABEL_PRINTER_PAPER_HEIGHT = "labelPrinterPaperHeight";
  public static final String LABEL_PRINTER_PAPER_GAP = "labelPrinterPaperGap";
  /**
   * USB设备检测
   */
  public static final int USB_PRODUCT_ID = 1280;
  public static final String USB_MANUFACTURER_NAME = "Gprinter";
  public static final int USB_CONNECT_STATUS = 3;
  /**
   * 崩溃时间
   */
  public static final String ERROR_TIME = "errortime";
  /**
   * 支持客显的设备 佳田
   */
  public static String[] CUSTOMERS_JT = { "ptksai pos","rk3288" };
  /**
   * 支持客显的设备 好德芯
   */
  public static String[] CUSTOMERS_HDX = { "HDX066H","HDX075", "HDX066" };
  /**
   * 客显设备类型
   */
  public static final String CUSTOMER_DISPLAY_TYPE = "customerDisplayType";
  /**
   * 支持蓝牙打印
   */
  public static String[] BLUE_PRINT = { "HDX065" };
  /**
   * 蓝牙 内置打印机名
   */
  public static final String BT_INNER_PRINTER = "Inner printer";
  /**
   * 当前登录 USer id
   */
  public static final String LOGIN_USER_OBJID = "LoginUserObjId";
}