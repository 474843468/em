package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class ExampleDaoGenerator {
  private static Entity pxDiscounScheme;//折扣方案
  private static Entity pxTableInfo;//桌台
  private static Entity pxFormatInfo;//规格信息
  private static Entity pxMethodInfo;//做法信息
  private static Entity pxPrinterInfo;//打印信息
  private static Entity pxProductInfo;//商品信息
  private static Entity pxProductCategory;//商品分类信息
  private static Entity pxOrderInfo;//订单信息
  private static Entity pxOrderDetails;//订单详情
  private static Entity pxProductFormatRel;//商品规格引用关系
  private static Entity pxProductMethodRel;//商品做法引用关系
  private static Entity pxPromotioInfo;//促销计划信息
  private static Entity pxPromotioDetails;//促销计划详情
  private static Entity office;//公司
  private static Entity user;//用户
  private static Entity pxExtraCharge;//附加费
  private static Entity pxTableExtraRel;//附加费桌台关联
  private static Entity pxExtraDetails;//订单附加费详情
  private static Entity pxTableAlteration;//桌台变更
  private static Entity pxRechargePlan;//会员充值方案
  private static Entity pxVipInfo;//会员信息表
  private static Entity pxVipCardType;//会员卡卡类型
  private static Entity pxVipCardInfo;//会员信息表
  private static Entity pxRechargeRecord;//会员充值记录
  private static Entity pxPxProductConfigPlan;//配菜方案
  private static Entity pxProductConfigPlanRel;//配菜方案Rel
  private static Entity pxPayInfo;//付款信息
  private static Entity pxOrderNum;//维护每日订单号
  private static Entity pxBestpay;//翼支付
  private static Entity pxSetInfo;//设置信息
  private static Entity pxBusinessHours;//营业时间
  private static Entity pxOptReason;//操作原因
  private static Entity PxWeiXinpay;//微信支付配置参数
  private static Entity pxAlipayInfo;//支付宝信息
  private static Entity pxComboGroup;//套餐分组表
  private static Entity pxComboProductRel;//套餐分组和商品rel
  private static Entity pxProductRemarks;//商品备注
  private static Entity printDetails;//打印信息
  private static Entity printDetailsConfigRel;//打印信息和配菜方案rel
  private static Entity printDetailsCollect;//打印信息汇总
  private static Entity tableOrderRel;//桌台和订单rel
  private static Entity pxVoucher;//优惠券
  private static Entity pxPaymentMode;//支付方式
  private static Entity ePaymentInfo;//电子支付信息
  private static Entity pxOperationLog;//操作记录
  private static Entity pxBuyCoupons;//团购券
  private static Entity uuidRecord;
  private static Entity pxTableArea;
  private static Entity btPrintDevice;

  //@formatter:on
  public static void main(String[] args) throws Exception {

    Schema schema = new Schema(15, "com.psi.easymanager.module");
    schema.setDefaultJavaPackageDao("com.psi.easymanager.dao");

    //折扣方案
    addPxDiscounScheme(schema);
    //桌台
    addPxTableInfo(schema);
    //规格信息
    addPxFormatInfo(schema);
    //做法信息
    addPxMethodInfo(schema);
    //net打印机信息
    addPxPrinterInfo(schema);
    //商品信息
    addPxProductInfo(schema);
    //商品分类信息
    addPxProductCategory(schema);
    //连接商品分类和商品信息(1:N)
    linkProdInfoAndProdCate(schema);
    //商品规格引用关系
    addPxProductFormatRel(schema);
    //商品做法引用关系
    addPxProductMethodRel(schema);
    //订单信息
    addPxOrderInfo(schema);
    //订单详情
    addPxOrderDetails(schema);
    //订单附加费详情
    addPxExtraDetails(schema);
    //桌台变更详情
    addPxTableAlteration(schema);
    //连接订单详情和规格
    linkOrderDetailsAndFormatInfo(schema);
    //连接订单详情和做法
    linkOrderDetailsAndMethodInfo(schema);
    //促销计划信息
    addPxPromotioInfo(schema);
    //促销计划详情
    addPxPromotioDetails(schema);
    //促销计划信息和促销计划详情(1:N)
    linkPromInfoAndPromDetails(schema);
    //连接订单详情和促销计划
    linkPromInfoAndOrderInfo();
    //公司
    addOffice(schema);
    //用户
    addUser(schema);
    //连接订单 用户
    linkOrderInfoAndUser(schema);
    //连接公司 用户
    linkUserAndOffice(schema);
    //附加费
    addPxExtraCharge(schema);
    //附加费桌台关联
    addPxTableExtraRel(schema);
    //会员充值方案
    addPxRechargePlan(schema);
    //会员信息表
    addPxVipInfo(schema);
    //会员卡类型
    addPxVipCardType(schema);
    //会员卡信息表
    addPxVipCardInfo(schema);
    //会员充值记录
    addPxRechargeRecord(schema);
    //配菜方案
    addPxProductConfigPlan(schema);
    //配菜方案Rel
    addProductConfigPlannRel(schema);
    //关联商品和配菜方案
    linkProductAndProductConfigPlan(schema);
    //付款信息
    addPxPayInfo(schema);
    //订单号码
    addPxOrderNum(schema);
    //翼支付
    addPxBestpay(schema);
    //设置信息
    addPxSetInfo(schema);
    //营业时间
    addPxBusinessHours(schema);
    //操作原因
    addPxOptReason(schema);
    //微信配置参数
    addPxWeiXinpay(schema);
    //商户支付宝信息
    addPxAlipayInfo(schema);
    //套餐分组
    addComboGroup(schema);
    //套餐商品和分组rel
    addComboGroupAndProdRel(schema);
    //商品备注
    addPxProductRemarks(schema);
    //打印信息
    addPrintDetails(schema);
    //打印汇总
    addPrintDetailsCollect(schema);
    //打印信息和配菜方案rel
    addPrintDetailsConfigRel(schema);
    //桌台和订单rel
    addTableOrderRel(schema);
    //优惠券
    addVoucher(schema);
    //支付方式
    addPaymentMode(schema);
    //电子支付信息
    addEPaymentInfo(schema);
    //操作记录
    addOperationRecord(schema);
    //团购券
    addPxBuyCoupons(schema);
    //smack UUID记录
    addSmackUUId(schema);
    //桌台区域
    addTableArea(schema);
    //蓝牙打印机
    addBTDevice(schema);
    new DaoGenerator().generateAll(schema, "D:/EasyManager/app/src/main/java");
  }

  /**
   * 折扣方案
   */
  //@formatter:off
  private static void addPxDiscounScheme(Schema schema) {
    pxDiscounScheme = schema.addEntity("PxDiscounScheme");
    pxDiscounScheme.setJavaDoc("折扣方案");
    pxDiscounScheme.setTableName("DiscounScheme");
    pxDiscounScheme.setHasKeepSections(true);
    pxDiscounScheme.implementsSerializable();
    pxDiscounScheme.addIdProperty().primaryKeyAsc();

    pxDiscounScheme.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxDiscounScheme.addStringProperty("name").javaDocField("折扣方案名称").codeBeforeField("@Expose");
    pxDiscounScheme.addIntProperty("rate").javaDocField("折扣值(0-100)").codeBeforeField("@Expose");
    pxDiscounScheme.addStringProperty("type").javaDocField("方案类型(0:折扣 1：会员)").codeBeforeField("@Expose");
    pxDiscounScheme.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 桌台
   */
  //@formatter:off
  private static void addPxTableInfo(Schema schema) {
    pxTableInfo = schema.addEntity("PxTableInfo");
    pxTableInfo.setJavaDoc("桌台信息");
    pxTableInfo.setTableName("TableInfo");
    pxTableInfo.setHasKeepSections(true);
    pxTableInfo.implementsSerializable();
    pxTableInfo.addIdProperty().primaryKeyAsc();

    pxTableInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxTableInfo.addStringProperty("code").javaDocField("桌位编号)").codeBeforeField("@Expose");
    pxTableInfo.addStringProperty("name").javaDocField("桌位名称").codeBeforeField("@Expose");
    pxTableInfo.addStringProperty("type").javaDocField("桌位区域(0:大厅 1：包厢)").codeBeforeField("@Expose");
    pxTableInfo.addIntProperty("peopleNum").javaDocField("建议人数").codeBeforeField("@Expose");
    pxTableInfo.addStringProperty("status").javaDocField("桌位状态(0:空闲 1：占用)").codeBeforeField("@Expose");
    pxTableInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxTableInfo.addIntProperty("sortNo").javaDocField("排序号").codeBeforeField("@Expose");
  }

  /**
   * 规格信息
   */
  //@formatter:off
  private static void addPxFormatInfo(Schema schema) {
    pxFormatInfo = schema.addEntity("PxFormatInfo");
    pxFormatInfo.setJavaDoc("规格信息");
    pxFormatInfo.setTableName("FormatInfo");
    pxFormatInfo.setHasKeepSections(true);
    pxFormatInfo.implementsSerializable();
    pxFormatInfo.addIdProperty().primaryKeyAsc();

    pxFormatInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxFormatInfo.addStringProperty("name").javaDocField("规格名称").codeBeforeField("@Expose");
    pxFormatInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 做法信息
   */
  private static void addPxMethodInfo(Schema schema) {
    pxMethodInfo = schema.addEntity("PxMethodInfo");
    pxMethodInfo.setJavaDoc("做法信息");
    pxMethodInfo.setTableName("MethodInfo");
    pxMethodInfo.setHasKeepSections(true);
    pxMethodInfo.implementsSerializable();
    pxMethodInfo.addIdProperty().primaryKeyAsc();

    pxMethodInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxMethodInfo.addStringProperty("name").javaDocField("做法名称").codeBeforeField("@Expose");
    pxMethodInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 打印机信息
   */
  //@formatter:off
  private static void addPxPrinterInfo(Schema schema) {
    pxPrinterInfo = schema.addEntity("PxPrinterInfo");
    pxPrinterInfo.setJavaDoc("Information of current printer.");
    pxPrinterInfo.setTableName("PrinterInfo");
    pxPrinterInfo.setHasKeepSections(true);
    pxPrinterInfo.implementsSerializable();
    pxPrinterInfo.addIdProperty();

    pxPrinterInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxPrinterInfo.addStringProperty("code").javaDocField("打印机编码").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("ipAddress").javaDocField("Ip Address.").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("type").javaDocField("类型（0：收银 1：后厨）").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("status").javaDocField("是否启用（0：启用 1：停用）").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("name").javaDocField("打印机名称").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("remarks").javaDocField("备注").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("format").javaDocField("规格（0：58mm 1：60mm）").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("isConnected").javaDocField("是否连接(0未连接,1已连接)").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    // 6->7
    pxPrinterInfo.addStringProperty("cashBox").javaDocField("是否配置钱箱0 否 1是").codeBeforeField("@Expose");
    pxPrinterInfo.addStringProperty("sound").javaDocField("是否报警0 否 1是").codeBeforeField("@Expose");
  }

  /**
   * 商品信息
   */
  //@formatter:off
  private static void addPxProductInfo(Schema schema) {
    pxProductInfo = schema.addEntity("PxProductInfo");
    pxProductInfo.setTableName("ProductInfo");
    pxProductInfo.setHasKeepSections(true);
    pxProductInfo.implementsSerializable();
    pxProductInfo.addIdProperty().primaryKeyAsc();

    pxProductInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxProductInfo.addStringProperty("name").javaDocField("商品名称").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("py").javaDocField("中文拼音首字母缩写（由程序生成").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("code").javaDocField("商品编码").codeBeforeField("@Expose");
    pxProductInfo.addDoubleProperty("price").javaDocField("商品单价").codeBeforeField("@Expose");
    pxProductInfo.addDoubleProperty("vipPrice").javaDocField("会员特价（默认与商品价格一致）").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("unit").javaDocField("结账单位").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("multipleUnit").javaDocField("是否多单位菜（0：是 1：否").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("orderUnit").javaDocField("点菜单位").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("isDiscount").javaDocField("允许打折（0：允许 1：不允许）").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("isGift").javaDocField("是否为赠品(0：是  1 ：否)").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("isPrint").javaDocField("商品发送后厨是否出单(0:出单 1：不出单)").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("changePrice").javaDocField("是否允许收银改价（0：是 1：否）").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("status").javaDocField("商品状态 (0:正常 1：停售)").codeBeforeField("@Expose");
    pxProductInfo.addBooleanProperty("isCustom").javaDocField("是否为自定义商品").codeBeforeField("@Expose");
    pxProductInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxProductInfo.addBooleanProperty("isUpLoad").javaDocField("是否已上传(只针对自定义商品)").codeBeforeField("@Expose");
    //1->2
    pxProductInfo.addStringProperty("barCode").javaDocField("条码").codeBeforeField("@Expose");
    //3->4
    pxProductInfo.addDoubleProperty("overPlus").javaDocField("剩余数量").codeBeforeField("@Expose");
    //3->4
    pxProductInfo.addStringProperty("type").javaDocField("0:普通分类 1：套餐分类").codeBeforeField("@Expose");
    //4->5
    pxProductInfo.addStringProperty("shelf").javaDocField("上架 (0:上架  1：下架)").codeBeforeField("@Expose");
    //4->5
    pxProductInfo.addStringProperty("visible").javaDocField("是否在微信点餐页面显示 0：显示 1：不显示").codeBeforeField("@Expose");
    //6->7
    pxProductInfo.addIntProperty("saleNum").javaDocField("销售数量").codeBeforeField("@Expose");
    //7->8
    pxProductInfo.addStringProperty("isLabel").javaDocField("使用标签打印机打印(0打印 1不打印)").codeBeforeField("@Expose");
    //13->14
    pxProductInfo.addStringProperty("shortName").javaDocField("商品短名称").codeBeforeField("@Expose");
  }

  /**
   * 商品分类信息
   */
  private static void addPxProductCategory(Schema schema) {
    pxProductCategory = schema.addEntity("PxProductCategory");
    pxProductCategory.setTableName("ProductCategory");
    pxProductCategory.setHasKeepSections(true);
    pxProductCategory.implementsSerializable();
    pxProductCategory.addIdProperty().primaryKeyAsc();

    pxProductCategory.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxProductCategory.addIntProperty("orderNo").javaDocField("排序号").codeBeforeField("@Expose");
    pxProductCategory.addStringProperty("code").javaDocField("类型编码").codeBeforeField("@Expose");
    pxProductCategory.addIntProperty("version").javaDocField("数据版本").codeBeforeField("@Expose");
    pxProductCategory.addStringProperty("name").javaDocField("分类名称").codeBeforeField("@Expose");
    pxProductCategory.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxProductCategory.addStringProperty("parentId").javaDocField("父节点id").codeBeforeField("@Expose");
    pxProductCategory.addStringProperty("leaf").javaDocField("是否为叶子节点:0：是 1：否").codeBeforeField("@Expose");
    //3->4
    pxProductCategory.addStringProperty("type").javaDocField("类型 0:普通分类 1：套餐分类").codeBeforeField("@Expose");
    //4->5
    pxProductCategory.addStringProperty("shelf").javaDocField("上架 (0:上架  1：下架)").codeBeforeField("@Expose");
    //4->5
    pxProductCategory.addStringProperty("visible").javaDocField("是否在微信点餐页面显示 0：显示 1：不显示").codeBeforeField("@Expose");

    Property pxParentCategoryId = pxProductCategory.addLongProperty("pxParentCategoryId").notNull().getProperty();
    pxProductCategory.addToOne(pxProductCategory, pxParentCategoryId, "dbParentCategory");

    ToMany cateToCate = pxProductCategory.addToMany(pxProductCategory, pxParentCategoryId);
    cateToCate.setName("dbChildCateList");
  }

  /**
   * 关联商品信息和商品分类
   */
  private static void linkProdInfoAndProdCate(Schema schema) {
    Property pxProductCategoryId = pxProductInfo.addLongProperty("pxProductCategoryId").notNull().getProperty();
    pxProductInfo.addToOne(pxProductCategory, pxProductCategoryId, "dbCategory");

    ToMany prodCateToProdInfo = pxProductCategory.addToMany(pxProductInfo, pxProductCategoryId);
    prodCateToProdInfo.setName("dbProductInfoList");
  }

  /**
   * 商品规格引用信息
   */
  //@formatter:off
  private static void addPxProductFormatRel(Schema schema) {
    pxProductFormatRel = schema.addEntity("PxProductFormatRel");
    pxProductFormatRel.setTableName("ProductFormatRel");
    pxProductFormatRel.setHasKeepSections(true);
    pxProductFormatRel.implementsSerializable();
    pxProductFormatRel.addIdProperty().primaryKeyAsc();

    pxProductFormatRel.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxProductFormatRel.addDoubleProperty("price").javaDocField("价格").codeBeforeField("@Expose");
    pxProductFormatRel.addDoubleProperty("vipPrice").javaDocField("会员价").codeBeforeField("@Expose");
    pxProductFormatRel.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    //4->5
    pxProductFormatRel.addDoubleProperty("stock").javaDocField("库存余量").codeBeforeField("@Expose");
    //4->5
    pxProductFormatRel.addStringProperty("status").javaDocField("销售状态(0:正常  1:停售)").codeBeforeField("@Expose");
    //8->9
    pxProductFormatRel.addStringProperty("barCode").javaDocField("条码").codeBeforeField("@Expose");

    Property pxFormatInfoId = pxProductFormatRel.addLongProperty("pxFormatInfoId").getProperty();
    pxProductFormatRel.addToOne(pxFormatInfo, pxFormatInfoId, "dbFormat");

    Property pxProductInfoId = pxProductFormatRel.addLongProperty("pxProductInfoId").getProperty();
    pxProductFormatRel.addToOne(pxProductInfo, pxProductInfoId, "dbProduct");
  }

  /**
   * 商品做法引用关系
   */
  private static void addPxProductMethodRel(Schema schema) {
    pxProductMethodRel = schema.addEntity("PxProductMethodRef");
    pxProductMethodRel.setTableName("PxProductMethodRef");
    pxProductMethodRel.setHasKeepSections(true);
    pxProductMethodRel.implementsSerializable();
    pxProductMethodRel.addIdProperty().primaryKeyAsc();

    pxProductMethodRel.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxProductMethodRel.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");

    Property pxMethodInfoId = pxProductMethodRel.addLongProperty("pxMethodInfoId").getProperty();
    pxProductMethodRel.addToOne(pxMethodInfo, pxMethodInfoId, "dbMethod");

    Property pxProductInfoId = pxProductMethodRel.addLongProperty("pxProductInfoId").getProperty();
    pxProductMethodRel.addToOne(pxProductInfo, pxProductInfoId, "dbProduct");
  }

  /**
   * 订单信息
   */
  private static void addPxOrderInfo(Schema schema) {
    pxOrderInfo = schema.addEntity("PxOrderInfo");
    pxOrderInfo.setTableName("OrderInfo");
    pxOrderInfo.setHasKeepSections(true);
    pxOrderInfo.implementsSerializable();
    pxOrderInfo.addIdProperty().primaryKeyAsc();

    pxOrderInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxOrderInfo.addDoubleProperty("totalPrice").javaDocField("订单总价").codeBeforeField("@Expose");
    pxOrderInfo.addDoubleProperty("accountReceivable").javaDocField("应收款").codeBeforeField("@Expose");
    pxOrderInfo.addDoubleProperty("realPrice").javaDocField("实收款").codeBeforeField("@Expose");
    pxOrderInfo.addDoubleProperty("totalChange").javaDocField("总的找零").codeBeforeField("@Expose");
    pxOrderInfo.addDoubleProperty("discountPrice").javaDocField("优惠金额").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("payType").javaDocField("支付方式(0:现金 1:刷卡 2：会员卡 3:其他)").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("status").javaDocField("订单状态(0:未结账 1：结账 2:撤单)").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("tail").javaDocField("是否抹零（0：是 1：否  2:请选择").codeBeforeField("@Expose");
    pxOrderInfo.addDoubleProperty("tailMoney").javaDocField("抹零金额").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("useVipCard").javaDocField("是否刷会员卡(0：不刷 1：手机 2实体卡)").codeBeforeField("@Expose");
    pxOrderInfo.addIntProperty("actualPeopleNumber").javaDocField("App使用，实际用餐人数.").codeBeforeField("@Expose");
    pxOrderInfo.addDateProperty("startTime").javaDocField("App使用,开始时间").codeBeforeField("@Expose");
    pxOrderInfo.addDateProperty("endTime").javaDocField("App使用，结束时间").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("officeName").javaDocField("公司名称").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("orderInfoType").javaDocField("订单类型 桌位单或零售单").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("extraType").javaDocField("App使用，附加费类型").codeBeforeField("@Expose");
    pxOrderInfo.addDateProperty("lastMoveTableTime").javaDocField("上次换桌时间,用于计算附加费").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("orderNo").javaDocField("订单号").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("orderReqNo").javaDocField("订单请求流水号").codeBeforeField("@Expose");
    pxOrderInfo.addBooleanProperty("isUpload").javaDocField("订单是否已上传(用于上传营业数据)").codeBeforeField("@Expose");
    pxOrderInfo.addDoubleProperty("extraMoney").javaDocField("附加费金额").codeBeforeField("@Expose");
    pxOrderInfo.addDoubleProperty("complementMoney").javaDocField("补足金额").codeBeforeField("@Expose");
    //1->2添加
    pxOrderInfo.addStringProperty("shiftChangeType").javaDocField("交接班状态(0:未交接 1:冻结 2:已交接 )").codeBeforeField("@Expose");
    //1->2添加
    pxOrderInfo.addStringProperty("finalArea").javaDocField("最终区域(0:大厅 1:包厢)").codeBeforeField("@Expose");
    //3->4添加在线支付锁定
    pxOrderInfo.addBooleanProperty("isLock").javaDocField("订单是否锁定(true:锁定 false:不锁定)-针对在线支付").codeBeforeField("@Expose");
    //5->6
    pxOrderInfo.addStringProperty("remarks").javaDocField("备注").codeBeforeField("@Expose");
    //6->7
    pxOrderInfo.addDoubleProperty("voucherMoney").javaDocField("优惠券金额").codeBeforeField("@Expose");
    //6->7
    pxOrderInfo.addStringProperty("isReversed").javaDocField("是否反结账(0:未反结账 1:反结账)").codeBeforeField("@Expose");
    //6->7
    pxOrderInfo.addBooleanProperty("isUploadReverse").javaDocField("反结账的订单是否上传").codeBeforeField("@Expose");
    //6->7 预订单
    pxOrderInfo.addStringProperty("isReserveOrder").javaDocField("订单类型(0:普通订单 1:预订单)").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("linkMan").javaDocField("联系人").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("contactPhone").javaDocField("联系电话").codeBeforeField("@Expose");
    pxOrderInfo.addDateProperty("diningTime").javaDocField("用餐时间").codeBeforeField("@Expose");
    pxOrderInfo.addStringProperty("reserveState").javaDocField("预订单状态 0:已预定、1:已到达(只针对预订单状态)").codeBeforeField("@Expose");
    //8->9
    pxOrderInfo.addDoubleProperty("payPrivilege").javaDocField("支付类优惠").codeBeforeField("@Expose");
    //14->15
    pxOrderInfo.addStringProperty("type").javaDocField("订单类型 (0:默认订单 1：微信点餐(到店) 2：微信点菜(外卖))").codeBeforeField("@Expose");
  }

  /**
   * 订单详情
   */
  private static void addPxOrderDetails(Schema schema) {
    pxOrderDetails = schema.addEntity("PxOrderDetails");
    pxOrderDetails.setTableName("OrderDetails");
    pxOrderDetails.setHasKeepSections(true);
    pxOrderDetails.implementsSerializable();
    pxOrderDetails.addIdProperty().primaryKeyAsc().codeBeforeField("@Expose");

    pxOrderDetails.addStringProperty("objectId").javaDocField("对应服务器id");
    pxOrderDetails.addDoubleProperty("price").javaDocField("价格").codeBeforeField("@Expose");
    pxOrderDetails.addDoubleProperty("vipPrice").javaDocField("会员价格").codeBeforeField("@Expose");
    pxOrderDetails.addDoubleProperty("num").javaDocField("数量").codeBeforeField("@Expose");
    pxOrderDetails.addStringProperty("status").javaDocField("商品状态(0:正常 1：延迟)").codeBeforeField("@Expose");
    pxOrderDetails.addIntProperty("discountRate").javaDocField("折扣率(0-100)").codeBeforeField("@Expose");
    pxOrderDetails.addStringProperty("isDiscount").javaDocField("是否打折（0：是 1：否）").codeBeforeField("@Expose");
    pxOrderDetails.addStringProperty("orderStatus").javaDocField("下单状态(0:未下单,1:已下单,2:退货)").codeBeforeField("@Expose");
    pxOrderDetails.addDoubleProperty("multipleUnitNumber").javaDocField("多单位数量 一般为重量").codeBeforeField("@Expose");
    pxOrderDetails.addBooleanProperty("isServing").javaDocField("已上菜 服务端使用").codeBeforeField("@Expose");
    pxOrderDetails.addDateProperty("operateTime").javaDocField("操作时间(下单时间或者退货时间)").codeBeforeField("@Expose");
    pxOrderDetails.addBooleanProperty("isPrinted").javaDocField("已打印").codeBeforeField("@Expose");
    pxOrderDetails.addDoubleProperty("finalPrice").javaDocField("结账时的最终价格,用于已结账单").codeBeforeField("@Expose");
    pxOrderDetails.addBooleanProperty("isClear").javaDocField("是否已清空，用于厨房打印").codeBeforeField("@Expose");
    //3->4
    pxOrderDetails.addStringProperty("inCombo").javaDocField("是否为套餐内的Details").codeBeforeField("@Expose");
    //3->4
    pxOrderDetails.addStringProperty("isComboDetails").javaDocField("是否为套餐Details").codeBeforeField("@Expose");
    //3->4
    pxOrderDetails.addBooleanProperty("isComboTemporaryDetails").javaDocField("是否为套餐所需的临时Details");
    //3->4
    pxOrderDetails.addStringProperty("chooseType").javaDocField("类型(必选或可选,用于套餐)").codeBeforeField("@Expose");
    //5->6
    pxOrderDetails.addStringProperty("isGift").javaDocField("是否为赠品").codeBeforeField("@Expose");
    //5->6
    pxOrderDetails.addStringProperty("remarks").javaDocField("备注").codeBeforeField("@Expose");
    //6->7
    pxOrderDetails.addDoubleProperty("unitPrice").javaDocGetterAndSetter("单价").codeBeforeField("@Expose");
    //6->7
    pxOrderDetails.addDoubleProperty("unitVipPrice").javaDocGetterAndSetter("单价(会员)").codeBeforeField("@Expose");
    //14>15
    pxOrderDetails.addDoubleProperty("refundNum").javaDocGetterAndSetter("已退数量").codeBeforeField("@Expose");
    pxOrderDetails.addDoubleProperty("refundMultNum").javaDocGetterAndSetter("已退重量").codeBeforeField("@Expose");

    //连接订单信息
    Property pxOrderInfoId = pxOrderDetails.addLongProperty("pxOrderInfoId").getProperty();
    pxOrderDetails.addToOne(pxOrderInfo, pxOrderInfoId, "dbOrder");
    //连接商品
    Property pxProductInfoId = pxOrderDetails.addLongProperty("pxProductInfoId").getProperty();
    pxOrderDetails.addToOne(pxProductInfo, pxProductInfoId, "dbProduct");

    //订单信息和订单详情一对多关系
    ToMany orderInfoToDetails = pxOrderInfo.addToMany(pxOrderDetails, pxOrderInfoId);
    orderInfoToDetails.setName("dbOrderDetailsList");

    //订单详情和商品多对一关系
    ToMany prodInfoToOrderDetails = pxProductInfo.addToMany(pxOrderDetails, pxProductInfoId);
    prodInfoToOrderDetails.setName("dbOrderDetailsList");

    //3->4
    //连接订单详情
    Property pxComboDetailsId = pxOrderDetails.addLongProperty("pxComboDetailsId").getProperty();
    pxOrderDetails.addToOne(pxOrderDetails,pxComboDetailsId,"dbComboDetails");
    //订单详情和订单详情一对多关系
    ToMany comboDetailsToOrderDetails = pxOrderDetails.addToMany(pxOrderDetails, pxComboDetailsId);
    comboDetailsToOrderDetails.setName("dbDetailsInCombo");
  }

  /**
   * 订单附加费详情
   */
  private static void addPxExtraDetails(Schema schema) {
    pxExtraDetails = schema.addEntity("PxExtraDetails");
    pxExtraDetails.setTableName("ExtraDetails");
    pxExtraDetails.setHasKeepSections(true);
    pxExtraDetails.implementsSerializable();
    pxExtraDetails.addIdProperty().primaryKeyAsc().codeBeforeField("@Expose");

    pxExtraDetails.addDoubleProperty("price").javaDocField("价格").codeBeforeField("@Expose");
    pxExtraDetails.addDoubleProperty("payPrice").javaDocField("已付价格").codeBeforeField("@Expose");
    pxExtraDetails.addBooleanProperty("isPrinted").javaDocField("已打印").codeBeforeField("@Expose");
    pxExtraDetails.addDateProperty("startTime").javaDocField("开始时间").codeBeforeField("@Expose");
    pxExtraDetails.addDateProperty("stopTime").javaDocField("结束时间").codeBeforeField("@Expose");
    pxExtraDetails.addStringProperty("tableName").javaDocField("桌名").codeBeforeField("@Expose");
    pxExtraDetails.addStringProperty("extraName").javaDocField("附加费名").codeBeforeField("@Expose");
    pxExtraDetails.addBooleanProperty("isComplement").javaDocField("是否为补足最低额").codeBeforeField("@Expose");

    //连接订单信息
    Property pxOrderInfoId = pxExtraDetails.addLongProperty("pxOrderInfoId").getProperty();
    pxExtraDetails.addToOne(pxOrderInfo, pxOrderInfoId, "dbOrder");

    //订单信息和附加费详情一对多关系
    ToMany orderInfoToDetails = pxOrderInfo.addToMany(pxExtraDetails, pxOrderInfoId);
    orderInfoToDetails.setName("dbExtraDetailsList");

    //订单信息添加当前附加费
    Property pxExtraDetailsId = pxOrderInfo.addLongProperty("pxExtraDetailsId").getProperty();
    pxOrderInfo.addToOne(pxExtraDetails, pxExtraDetailsId, "dbCurrentExtra");
  }

  /**
   * 桌台变更
   */
  private static void addPxTableAlteration(Schema schema) {
    pxTableAlteration = schema.addEntity("PxTableAlteration");
    pxTableAlteration.setTableName("TableAlteration");
    pxTableAlteration.setHasKeepSections(true);
    pxTableAlteration.implementsSerializable();
    pxTableAlteration.addIdProperty().primaryKeyAsc().codeBeforeField("@Expose");

    pxTableAlteration.addDateProperty("operateTime").javaDocField("操作时间").codeBeforeField("@Expose");
    pxTableAlteration.addStringProperty("type").javaDocField("类型 0:移动 1：合并").codeBeforeField("@Expose");
    pxTableAlteration.addBooleanProperty("isPrinted").javaDocField("已打印").codeBeforeField("@Expose");
    pxTableAlteration.addBooleanProperty("isClear").javaDocField("是否清空").codeBeforeField("@Expose");

    //关联订单
    Property pxOrderInfoId = pxTableAlteration.addLongProperty("pxOrderInfoId").getProperty();
    pxTableAlteration.addToOne(pxOrderInfo, pxOrderInfoId, "dbOrder");
    //关联原桌台
    Property pxOriginalTableId = pxTableAlteration.addLongProperty("pxOriginalTableId").getProperty();
    pxTableAlteration.addToOne(pxTableInfo, pxOriginalTableId, "dbOriginalTable");
    //关联目标
    Property pxTargetTableId = pxTableAlteration.addLongProperty("pxTargetTableId").getProperty();
    pxTableAlteration.addToOne(pxTableInfo, pxTargetTableId, "dbTargetTable");
  }


  /**
   * 连接订单详情和规格
   */
  private static void linkOrderDetailsAndFormatInfo(Schema schema) {
    Property pxFormatInfoId = pxOrderDetails.addLongProperty("pxFormatInfoId").getProperty();
    pxOrderDetails.addToOne(pxFormatInfo, pxFormatInfoId, "dbFormatInfo");
  }

  /**
   * 连接订单详情和做法
   */
  private static void linkOrderDetailsAndMethodInfo(Schema schema) {
    Property pxMethodInfoId = pxOrderDetails.addLongProperty("pxMethodInfoId").getProperty();
    pxOrderDetails.addToOne(pxMethodInfo, pxMethodInfoId, "dbMethodInfo");
  }

  /**
   * 促销计划信息
   */
  private static void addPxPromotioInfo(Schema schema) {
    pxPromotioInfo = schema.addEntity("PxPromotioInfo");
    pxPromotioInfo.setTableName("PromotioInfo");
    pxPromotioInfo.setHasKeepSections(true);
    pxPromotioInfo.implementsSerializable();
    pxPromotioInfo.addIdProperty().primaryKeyAsc();

    pxPromotioInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxPromotioInfo.addStringProperty("name").javaDocField("促销计划名").codeBeforeField("@Expose");
    pxPromotioInfo.addStringProperty("code").javaDocField("促销编码").codeBeforeField("@Expose");
    pxPromotioInfo.addStringProperty("type").javaDocField("促销计划类型（0：长期有效 1：指定时间 2：每周特定）").codeBeforeField("@Expose");
    pxPromotioInfo.addStringProperty("startTime").javaDocField("开始时间").codeBeforeField("@Expose");
    pxPromotioInfo.addStringProperty("endTime").javaDocField("结束时间").codeBeforeField("@Expose");
    pxPromotioInfo.addDateProperty("startDate").javaDocField("开始日期").codeBeforeField("@Expose");
    pxPromotioInfo.addDateProperty("endDate").javaDocField("结束日期").codeBeforeField("@Expose");
    pxPromotioInfo.addStringProperty("weekly").javaDocField("每周几有效").codeBeforeField("@Expose");



    pxPromotioInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 促销计划详情
   */
  private static void addPxPromotioDetails(Schema schema) {
    pxPromotioDetails = schema.addEntity("PxPromotioDetails");
    pxPromotioDetails.setTableName("PromotioDetails");
    pxPromotioDetails.setHasKeepSections(true);
    pxPromotioDetails.implementsSerializable();
    pxPromotioDetails.addIdProperty().primaryKeyAsc();

    pxPromotioDetails.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxPromotioDetails.addDoubleProperty("promotionalPrice").javaDocField("促销价").codeBeforeField("@Expose");
    pxPromotioDetails.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  //Product
    Property pxProductInfoId = pxPromotioDetails.addLongProperty("pxProductInfoId").notNull().getProperty();
    pxPromotioDetails.addToOne(pxProductInfo, pxProductInfoId, "dbProduct");
  //Format
    Property pxFormatId = pxPromotioDetails.addLongProperty("pxFormatId").getProperty();
    pxPromotioDetails.addToOne(pxFormatInfo,pxFormatId,"dbFormat");
  }

  /**
   * 关联 促销计划信息和促销计划详情
   */
  private static void linkPromInfoAndPromDetails(Schema schema) {
    Property pxPromotioInfoId = pxPromotioDetails.addLongProperty("pxPromotioInfoId").notNull().getProperty();
    pxPromotioDetails.addToOne(pxPromotioInfo, pxPromotioInfoId, "dbPromotio");

    ToMany promInfoToPromDetails = pxPromotioInfo.addToMany(pxPromotioDetails, pxPromotioInfoId);
    promInfoToPromDetails.setName("dbPromDetailsList");
  }

  /**
   *
   * 关联 订单关联促销计划 + 订单详情关联促销计划详情
   */
  private static void linkPromInfoAndOrderInfo() {
    Property promotioId = pxOrderInfo.addLongProperty("pxPromotioId").getProperty();
    pxOrderInfo.addToOne(pxPromotioInfo, promotioId, "dbPromotioInfo");
    //
    //Property promotioDetailsId = pxOrderDetails.addLongProperty("pxPromotioDetailsId").getProperty();
    //pxOrderDetails.addToOne(pxPromotioDetails, promotioDetailsId, "dbPromotioDetails");
  }
  /**
   * 公司
   */
  private static void addOffice(Schema schema) {
    office = schema.addEntity("Office");
    office.setTableName("Office");
    office.setHasKeepSections(true);
    office.implementsSerializable();
    office.addIdProperty();

    office.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    office.addStringProperty("code").javaDocField("机构编码").codeBeforeField("@Expose");
    office.addStringProperty("type").javaDocField("机构类型（1：公司；2：部门；3：小组）").codeBeforeField("@Expose");
    office.addStringProperty("grade").javaDocField("机构等级（1：一级；2：二级；3：三级；4：四级）").codeBeforeField("@Expose");
    office.addStringProperty("address").javaDocField("联系地址").codeBeforeField("@Expose");
    office.addStringProperty("zipCode").javaDocField("邮政编码").codeBeforeField("@Expose");
    office.addStringProperty("master").javaDocField("负责人").codeBeforeField("@Expose");
    office.addStringProperty("phone").javaDocField("电话").codeBeforeField("@Expose");
    office.addStringProperty("fax").javaDocField("传真").codeBeforeField("@Expose");
    office.addStringProperty("email").javaDocField("邮箱").codeBeforeField("@Expose");
    office.addStringProperty("useable").javaDocField("是否可用 (0:可用 1:不可用) ").codeBeforeField("@Expose");
    office.addStringProperty("logo").javaDocField("店铺logo").codeBeforeField("@Expose");
    office.addStringProperty("groupId").javaDocField("群组id").codeBeforeField("@Expose");
    office.addStringProperty("name").javaDocField("店铺名字").codeBeforeField("@Expose");
    office.addStringProperty("initPassword").javaDocField("聊天登录密码").codeBeforeField("@Expose");
    //6->7
    office.addStringProperty("parentIds").javaDocField("下级机构").codeBeforeField("@Expose");
    office.addStringProperty("parentId").javaDocField("上级机构").codeBeforeField("@Expose");
  }

  /**
   * 用户
   */
  private static void addUser(Schema schema) {
    user = schema.addEntity("User");
    user.setTableName("User");
    user.setHasKeepSections(true);
    user.implementsSerializable();
    user.addIdProperty().primaryKeyAsc();

    user.addStringProperty("objectId").javaDocField("").codeBeforeField("@SerializedName(\"id\") @Expose");
    user.addStringProperty("loginName").javaDocField("登录名").codeBeforeField("@Expose");
    user.addStringProperty("password").javaDocField("密码").codeBeforeField("@Expose");
    user.addStringProperty("no").javaDocField("工号").codeBeforeField("@Expose");
    user.addStringProperty("name").javaDocField("姓名").codeBeforeField("@Expose");
    user.addStringProperty("email").javaDocField("邮箱").codeBeforeField("@Expose");
    user.addStringProperty("phone").javaDocField("电话").codeBeforeField("@Expose");
    user.addStringProperty("mobile").javaDocField("手机").codeBeforeField("@Expose");
    user.addStringProperty("userType").javaDocField("用户类型").codeBeforeField("@Expose");
    user.addStringProperty("loginIp").javaDocField("最后登陆IP").codeBeforeField("@Expose");
    user.addDateProperty("loginDate").javaDocField("最后登陆日期").codeBeforeField("@Expose");
    user.addStringProperty("loginFlag").javaDocField("是否允许登陆").codeBeforeField("@Expose");
    user.addStringProperty("photo").javaDocField("头像").codeBeforeField("@Expose");
    user.addStringProperty("oldLoginName").javaDocField("原登录名").codeBeforeField("@Expose");
    user.addStringProperty("newPassword").javaDocField("新密码").codeBeforeField("@Expose");
    user.addStringProperty("oldLoginIp").javaDocField("上次登陆IP").codeBeforeField("@Expose");
    user.addDateProperty("oldLoginDate").javaDocField("上次登陆日期").codeBeforeField("@Expose");
    user.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    user.addDoubleProperty("maxTail").javaDocField("最大抹零限制").codeBeforeField("@Expose");
    user.addStringProperty("companyCode").javaDocField("公司编码").codeBeforeField("@Expose");
    user.addStringProperty("initPassword").javaDocField("初始化密码").codeBeforeField("@Expose");
    user.addStringProperty("imUserName").javaDocField("初始登录名").codeBeforeField("@Expose");
    //2->3
    user.addStringProperty("canRetreat").javaDocField("是否允许退菜(1:允许 0:不允许)").codeBeforeField("@Expose");
  }

  /**
   * 关联订单信息和用户
   */
  private static void linkOrderInfoAndUser(Schema schema) {
    Property userId = pxOrderInfo.addLongProperty("userId").notNull().getProperty();
    pxOrderInfo.addToOne(user, userId, "dbUser");

    Property waiterId = pxOrderInfo.addLongProperty("waiterId").notNull().getProperty();
    pxOrderInfo.addToOne(user, waiterId, "dbWaiter");

    Property checkOutUserId = pxOrderInfo.addLongProperty("checkOutUserId").notNull().getProperty();
    pxOrderInfo.addToOne(user, checkOutUserId ,"dbCheckOutUser");
  }

  /**
   * 关联用户和公司
   */
  private static void linkUserAndOffice(Schema schema) {
    Property primaryPersonId = office.addLongProperty("primaryPersonId").notNull().getProperty();
    office.addToOne(user, primaryPersonId, "primaryPerson");

    Property deputyPersonId = office.addLongProperty("deputyPersonId").notNull().getProperty();
    office.addToOne(user, deputyPersonId, "deputyPerson");

    Property officeId = user.addLongProperty("officeId").notNull().getProperty();
    user.addToOne(office, officeId, "company");
  }

  /**
   * 附加费
   */
  private static void addPxExtraCharge(Schema schema) {
    pxExtraCharge = schema.addEntity("PxExtraCharge");
    pxExtraCharge.setJavaDoc("附加费信息");
    pxExtraCharge.setTableName("ExtraCharge");
    pxExtraCharge.setHasKeepSections(true);
    pxExtraCharge.implementsSerializable();
    pxExtraCharge.addIdProperty().primaryKeyAsc();

    pxExtraCharge.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxExtraCharge.addStringProperty("name").javaDocField("附加费名称").codeBeforeField("@Expose");
    pxExtraCharge.addDoubleProperty("serviceCharge").javaDocField("服务费").codeBeforeField("@Expose");
    pxExtraCharge.addStringProperty("serviceType").javaDocField("计费标准(0:每桌固定 1：按人数 2：按时间)").codeBeforeField("@Expose");
    pxExtraCharge.addDoubleProperty("minConsume").javaDocField("最低消费金额").codeBeforeField("@Expose");
    pxExtraCharge.addStringProperty("serviceStatus").javaDocField("是否启用服务费(0:不启用 1:启用)").codeBeforeField("@Expose");
    pxExtraCharge.addStringProperty("consumeStatus").javaDocField("是否启动最低消费(0:不启用 1:启用)").codeBeforeField("@Expose");
    pxExtraCharge.addIntProperty("minutes").javaDocField("计费时长").codeBeforeField("@Expose");
    pxExtraCharge.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 附加费桌台关联信息
   */
  private static void addPxTableExtraRel(Schema schema) {
    pxTableExtraRel = schema.addEntity("PxTableExtraRel");
    pxTableExtraRel.setJavaDoc("附加费桌台关联");
    pxTableExtraRel.setTableName("TableExtraRel");
    pxTableExtraRel.setHasKeepSections(true);
    pxTableExtraRel.implementsSerializable();
    pxTableExtraRel.addIdProperty().primaryKeyAsc();

    pxTableExtraRel.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxTableExtraRel.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");

    Property pxTableInfoId = pxTableExtraRel.addLongProperty("pxTableInfoId").getProperty();
    pxTableExtraRel.addToOne(pxTableInfo, pxTableInfoId, "dbTable");

    Property pxExtraChargeId = pxTableExtraRel.addLongProperty("pxExtraChargeId").getProperty();
    pxTableExtraRel.addToOne(pxExtraCharge, pxExtraChargeId, "dbExtraCharge");
  }

  /**
   * 会员充值方案
   */
  private static void addPxRechargePlan(Schema schema) {
    pxRechargePlan = schema.addEntity("PxRechargePlan");
    pxRechargePlan.setJavaDoc("会员充值方案");
    pxRechargePlan.setTableName("RechargePlan");
    pxRechargePlan.setHasKeepSections(true);
    pxRechargePlan.implementsSerializable();
    pxRechargePlan.addIdProperty().primaryKeyAsc();

    pxRechargePlan.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxRechargePlan.addStringProperty("name").javaDocField("充值方案名称").codeBeforeField("@Expose");
    pxRechargePlan.addDoubleProperty("money").javaDocField("充值金额").codeBeforeField("@Expose");
    pxRechargePlan.addDoubleProperty("largess").javaDocField("赠送金额").codeBeforeField("@Expose");
    pxRechargePlan.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 会员信息表
   */
  private static void addPxVipInfo(Schema schema) {
    pxVipInfo = schema.addEntity("PxVipInfo");
    pxVipInfo.setJavaDoc("会员信息");
    pxVipInfo.setTableName("VipInfo");
    pxVipInfo.setHasKeepSections(true);
    pxVipInfo.implementsSerializable();
    pxVipInfo.addIdProperty().primaryKeyAsc();

    pxVipInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxVipInfo.addStringProperty("name").javaDocField("会员名称").codeBeforeField("@Expose");
    pxVipInfo.addStringProperty("code").javaDocField("会员编码").codeBeforeField("@Expose");
    pxVipInfo.addStringProperty("mobile").javaDocField("会员手机号").codeBeforeField("@Expose");
    pxVipInfo.addStringProperty("level").javaDocField("会员级别").codeBeforeField("@Expose");
    pxVipInfo.addDoubleProperty("accountBalance").javaDocField("会员账户余额").codeBeforeField("@Expose");
    pxVipInfo.addBooleanProperty("isUpLoad").javaDocField("是否上传").codeBeforeField("@Expose");
    pxVipInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxVipInfo.addBooleanProperty("isModify").javaDocField("是否修改信息").codeBeforeField("@Expose");

  }

  /**
   * 会员卡类型
   */
  private static void addPxVipCardType(Schema schema) {
    pxVipCardType = schema.addEntity("PxVipCardType");
    pxVipCardType.setJavaDoc("会员卡类型");
    pxVipCardType.setTableName("VipCardType");
    pxVipCardType.setHasKeepSections(true);
    pxVipCardType.implementsSerializable();
    pxVipCardType.addIdProperty().primaryKeyAsc();

    pxVipCardType.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxVipCardType.addStringProperty("name").javaDocField("卡类型名称").codeBeforeField("@Expose");
    pxVipCardType.addStringProperty("type").javaDocField("优惠方式（0：使用会员价 1：打折 2：使用折扣方案）").codeBeforeField("@Expose");
    pxVipCardType.addIntProperty("marginForegift").javaDocField("押金").codeBeforeField("@Expose");
    pxVipCardType.addIntProperty("rechargeScore").javaDocField("折扣率").codeBeforeField("@Expose");
    pxVipCardType.addStringProperty("requirePassword").javaDocField("是否需要密码（0：不需要 1：需要）").codeBeforeField("@Expose");
    pxVipCardType.addStringProperty("consumeSendScore").javaDocField("消费时是否赠送积分(0：不赠送 1：赠送)").codeBeforeField("@Expose");
    pxVipCardType.addIntProperty("discountRate").javaDocField("充值多少元送1积分").codeBeforeField("@Expose");
    pxVipCardType.addIntProperty("consumeScore").javaDocField("消费多少元送1积分").codeBeforeField("@Expose");
    pxVipCardType.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");

    //关联折扣方案
    Property pxDiscounSchemeId = pxVipCardType.addLongProperty("pxDiscounSchemeId").getProperty();
    pxVipCardType.addToOne(pxDiscounScheme, pxDiscounSchemeId, "dbDiscounScheme");
  }
  /**
   * 会员卡信息表
   */
  private static void addPxVipCardInfo(Schema schema) {
    pxVipCardInfo = schema.addEntity("PxVipCardInfo");
    pxVipCardInfo.setJavaDoc("会员卡信息");
    pxVipCardInfo.setTableName("VipCardInfo");
    pxVipCardInfo.setHasKeepSections(true);
    pxVipCardInfo.implementsSerializable();
    pxVipCardInfo.addIdProperty().primaryKeyAsc();

    pxVipCardInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxVipCardInfo.addStringProperty("idcardNum").javaDocField("ID卡内部卡号").codeBeforeField("@Expose");
    pxVipCardInfo.addStringProperty("cardNum").javaDocField("卡号").codeBeforeField("@Expose");
    pxVipCardInfo.addStringProperty("password").javaDocField("密码（明文）").codeBeforeField("@Expose");
    pxVipCardInfo.addStringProperty("mobile").javaDocField("会员手机号").codeBeforeField("@Expose");
    pxVipCardInfo.addDoubleProperty("rechargeMoney").javaDocField("充值金额").codeBeforeField("@Expose");
    pxVipCardInfo.addDoubleProperty("receivedMoney").javaDocField("实收金额").codeBeforeField("@Expose");
    pxVipCardInfo.addDoubleProperty("accountBalance").javaDocField("余额").codeBeforeField("@Expose");
    pxVipCardInfo.addStringProperty("status").javaDocField("卡状态（0：未使用 1：使用）").codeBeforeField("@Expose");
    pxVipCardInfo.addIntProperty("score").javaDocField("积分").codeBeforeField("@Expose");
    pxVipCardInfo.addStringProperty("pid").javaDocField("总公司").codeBeforeField("@Expose");
    pxVipCardInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    //关联会员卡类型
    Property pxVipCardTypeId = pxVipCardInfo.addLongProperty("pxVipCardTypeId").getProperty();
    pxVipCardInfo.addToOne(pxVipCardType, pxVipCardTypeId, "dbVipCardType");
  }


  /**
   * 会员充值记录
   */
  private static void addPxRechargeRecord(Schema schema) {
    pxRechargeRecord = schema.addEntity("PxRechargeRecord");
    pxRechargeRecord.setJavaDoc("会员充值记录");
    pxRechargeRecord.setTableName("RechargeRecord");
    pxRechargeRecord.setHasKeepSections(true);
    pxRechargeRecord.implementsSerializable();
    pxRechargeRecord.addIdProperty().primaryKeyAsc();

    pxRechargeRecord.addIntProperty("score").javaDocField("积分").codeBeforeField("@Expose");
    pxRechargeRecord.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxRechargeRecord.addDoubleProperty("money").javaDocField("充值金额").codeBeforeField("@Expose");
    pxRechargeRecord.addDoubleProperty("giving").javaDocField("赠送金额").codeBeforeField("@Expose");
    pxRechargeRecord.addBooleanProperty("isUpLoad").javaDocField("是否上传").codeBeforeField("@Expose");
    pxRechargeRecord.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");

    //1->2
    pxRechargeRecord.addDateProperty("rechargeTime").javaDocField("充值时间").codeBeforeField("@SerializedName(\"updateDate\") @Expose");
    //1->2
    //关联收银员
    Property userId = pxRechargeRecord.addLongProperty("userId").notNull().getProperty();
    pxRechargeRecord.addToOne(user, userId, "dbUser");
    //1->2添加
    pxRechargeRecord.addStringProperty("shiftChangeType").javaDocField("交接班状态(0:未交接 1:冻结 2:已交接 )").codeBeforeField("@Expose");
    //1->2添加
    pxRechargeRecord.addStringProperty("rechargeType").javaDocField("(充值类型 0:现金)").codeBeforeField("@Expose");


    //关联会员
    //1->2
    Property pxVipInfoId = pxRechargeRecord.addLongProperty("pxVipInfoId").notNull().getProperty();
    pxRechargeRecord.addToOne(pxVipInfo, pxVipInfoId, "dbVipInfo");

    //关联充值计划
    Property pxRechargePlanId = pxRechargeRecord.addLongProperty("pxRechargePlanId").notNull().getProperty();
    pxRechargeRecord.addToOne(pxRechargePlan, pxRechargePlanId, "dbRechargePlan");
  }

  /**
   * 配菜方案
   */
  private static void addPxProductConfigPlan(Schema schema) {
    pxPxProductConfigPlan = schema.addEntity("PxProductConfigPlan");
    pxPxProductConfigPlan.setJavaDoc("配菜方案");
    pxPxProductConfigPlan.setTableName("PxProductConfigPlan");
    pxPxProductConfigPlan.setHasKeepSections(true);
    pxPxProductConfigPlan.implementsSerializable();
    pxPxProductConfigPlan.addIdProperty().primaryKeyAsc();

    pxPxProductConfigPlan.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxPxProductConfigPlan.addStringProperty("name").javaDocField("配菜方案名称").codeBeforeField("@Expose");
    pxPxProductConfigPlan.addStringProperty("flag").javaDocField("是否一菜一切 1是 0 否").codeBeforeField("@Expose");
    pxPxProductConfigPlan.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxPxProductConfigPlan.addIntProperty("count").javaDocField("打印份数").codeBeforeField("@Expose");

    //连接打印机
    Property pxPrinterInfoId = pxPxProductConfigPlan.addLongProperty("pxPrinterInfoId").getProperty();
    pxPxProductConfigPlan.addToOne(pxPrinterInfo,pxPrinterInfoId,"dbPrinter");
  }

  /**
   * 商品和配菜方案关系
   */
  private static void addProductConfigPlannRel(Schema schema) {
    pxProductConfigPlanRel =schema.addEntity("PxProductConfigPlanRel");
    pxProductConfigPlanRel.setTableName("ProductConfigPlanRel");
    pxProductConfigPlanRel.setHasKeepSections(true);
    pxProductConfigPlanRel.implementsSerializable();
    pxProductConfigPlanRel.addIdProperty().primaryKeyAsc();

    pxProductConfigPlanRel.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxProductConfigPlanRel.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 配菜方案关系 连接 商品、配菜方案
   */
  private static void linkProductAndProductConfigPlan(Schema schema) {
    //连接商品
    Property pxProductInfoId = pxProductConfigPlanRel.addLongProperty("pxProductInfoId").getProperty();
    pxProductConfigPlanRel.addToOne(pxProductInfo,pxProductInfoId,"dbProduct");
    //连接配菜方案
    Property pxProductConfigPlanId = pxProductConfigPlanRel.addLongProperty("pxProductConfigPlanId").getProperty();
    pxProductConfigPlanRel.addToOne(pxPxProductConfigPlan,pxProductConfigPlanId,"dbProductConfigPlan");
  }


  /**
   * 付款信息
   */
  private static void addPxPayInfo(Schema schema) {
    pxPayInfo = schema.addEntity("PxPayInfo");
    pxPayInfo.setJavaDoc("付款信息");
    pxPayInfo.setTableName("PxPayInfo");
    pxPayInfo.setHasKeepSections(true);
    pxPayInfo.implementsSerializable();
    pxPayInfo.addIdProperty().primaryKeyAsc();

    pxPayInfo.addDateProperty("payTime").javaDocField("付款时间").codeBeforeField("@Expose");
    pxPayInfo.addDoubleProperty("received").javaDocField("实收金额").codeBeforeField("@Expose");
    pxPayInfo.addDoubleProperty("change").javaDocField("找零").codeBeforeField("@Expose");
    pxPayInfo.addStringProperty("voucherCode").javaDocField("凭证码(用于POS刷卡)" ).codeBeforeField("@Expose");
    //6->7
    pxPayInfo.addStringProperty("tradeNo").javaDocField("流水号(支付宝、微信)" ).codeBeforeField("@Expose");
    //6->7
    pxPayInfo.addStringProperty("remarks").javaDocField("备注,免单原因等").codeBeforeField("@Expose");
    //6->7
    pxPayInfo.addStringProperty("vipMobile").javaDocField("会员手机").codeBeforeField("@Expose");
    pxPayInfo.addStringProperty("vipId").javaDocField("会员id").codeBeforeField("@Expose");
    pxPayInfo.addStringProperty("idCardNum").javaDocField("会员内部卡号").codeBeforeField("@Expose");
    //6->7
    pxPayInfo.addStringProperty("paymentId").javaDocField("支付方式Id").codeBeforeField("@Expose");
    pxPayInfo.addStringProperty("paymentType").javaDocField("支付方式类型").codeBeforeField("@Expose");
    pxPayInfo.addStringProperty("paymentName").javaDocField("支付方式名称").codeBeforeField("@Expose");
    pxPayInfo.addStringProperty("salesAmount").javaDocField("是否计算入销售额(0:是 1：否)").codeBeforeField("@Expose");
    //8->9
    pxPayInfo.addDoubleProperty("payPrivilege").javaDocField("支付类优惠").codeBeforeField("@Expose");
    //8->9
    pxPayInfo.addStringProperty("ticketCode").javaDocField("验券码").codeBeforeField("@Expose");

    //连接订单信息
    Property pxOrderInfoId = pxPayInfo.addLongProperty("pxOrderInfoId").getProperty();
    pxPayInfo.addToOne(pxOrderInfo, pxOrderInfoId, "dbOrder");

    //订单信息和付款信息一对多关系
    ToMany orderInfoToPayInfos= pxOrderInfo.addToMany(pxPayInfo, pxOrderInfoId);
    orderInfoToPayInfos.setName("dbPayInfoList");
  }

  /**
   * 订单号码
   */
  private static void addPxOrderNum(Schema schema) {
    pxOrderNum = schema.addEntity("PxOrderNum");
    pxOrderNum.setJavaDoc("订单号码");
    pxOrderNum.setTableName("OrderNum");
    pxOrderNum.setHasKeepSections(true);
    pxOrderNum.implementsSerializable();
    pxOrderNum.addIdProperty().primaryKeyAsc();

    pxOrderNum.addStringProperty("date").javaDocField("开单日期").codeBeforeField("@Expose");
    pxOrderNum.addIntProperty("num").javaDocField("开单数量").codeBeforeField("@Expose");
  }

  /**
   * 翼支付
   */
  private static void addPxBestpay(Schema schema) {
    pxBestpay = schema.addEntity("PxBestpay");
    pxBestpay.setJavaDoc("翼支付相关参数");
    pxBestpay.setTableName("PxBestpay");
    pxBestpay.setHasKeepSections(true);
    pxBestpay.implementsSerializable();
    pxBestpay.addIdProperty().primaryKeyAsc();

    pxBestpay.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxBestpay.addStringProperty("shopCode").javaDocField("商户代码").codeBeforeField("@Expose");
    pxBestpay.addStringProperty("dataKey").javaDocField("数据key").codeBeforeField("@Expose");
    pxBestpay.addStringProperty("tradeKey").javaDocField("交易key").codeBeforeField("@Expose");
    pxBestpay.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 设置信息
   */
  private static void addPxSetInfo(Schema schema) {
    pxSetInfo = schema.addEntity("PxSetInfo");
    pxSetInfo.setJavaDoc("设置信息");
    pxSetInfo.setTableName("PxSetInfo");
    pxSetInfo.setHasKeepSections(true);
    pxSetInfo.implementsSerializable();
    pxSetInfo.addIdProperty().primaryKeyAsc();

    pxSetInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxSetInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxSetInfo.addStringProperty("model").javaDocField("商业模式(1:默认)").codeBeforeField("@Expose");
    pxSetInfo.addStringProperty("isFastOpenOrder").javaDocField("是否快速开单(1:是2:否)").codeBeforeField("@Expose");
    pxSetInfo.addStringProperty("isAutoTurnCheckout").javaDocField("点餐完毕是否自动切换到结账页面(1:是2:否)").codeBeforeField("@Expose");
    pxSetInfo.addStringProperty("autoOrder").javaDocField("商品添加后自动下单(1:是2:否)").codeBeforeField("@Expose");
    pxSetInfo.addStringProperty("overAutoStartBill").javaDocField("结账完毕自动开单(1:是2:否)").codeBeforeField("@Expose");
    pxSetInfo.addStringProperty("isAutoPrintRechargeVoucher").javaDocField("会员充值消费是否打印凭证(1:是2:否)").codeBeforeField("@Expose");
    pxSetInfo.addStringProperty("isFinancePrintCategory").javaDocField("财务联是否打印分类统计信息(1:是2:否)").codeBeforeField("@Expose");
  }

  /**
   * 营业时间
   */
  //2-->3
  private static void addPxBusinessHours(Schema schema) {
    pxBusinessHours = schema.addEntity("PxBusinessHours");
    pxBusinessHours.setJavaDoc("营业时间");
    pxBusinessHours.setTableName("BusinessHours");
    pxBusinessHours.setHasKeepSections(true);
    pxBusinessHours.implementsSerializable();
    pxBusinessHours.addIdProperty().primaryKeyAsc();

    pxBusinessHours.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxBusinessHours.addStringProperty("businessType").javaDocField("营业结束时间类型(0:当日 1:次日早晨)").codeBeforeField("@Expose");
    pxBusinessHours.addStringProperty("closeTime").javaDocField("闭店时间").codeBeforeField("@Expose");
    pxBusinessHours.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 操作原因
   */
  //2-->3
  private static void addPxOptReason(Schema schema) {
    pxOptReason = schema.addEntity("PxOptReason");
    pxOptReason.setJavaDoc("操作原因");
    pxOptReason.setTableName("OptReason");
    pxOptReason.setHasKeepSections(true);
    pxOptReason.implementsSerializable();
    pxOptReason.addIdProperty().primaryKeyAsc();

    pxOptReason.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxOptReason.addStringProperty("name").javaDocField("原因").codeBeforeField("@Expose");
    pxOptReason.addStringProperty("type").javaDocField("类型(0:打折原因 1：撤单原因 2：取消结账原因 3：退货原因)").codeBeforeField("@Expose");
    pxOptReason.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");

    //订单详情关联操作原因
    //2-->3
    Property pxOptReasonId = pxOrderDetails.addLongProperty("pxOptReasonId").getProperty();
    pxOrderDetails.addToOne(pxOptReason,pxOptReasonId,"dbReason");
  }


  /**
   * 微信支付配置参数
   */
  private static void addPxWeiXinpay(Schema schema) {
    PxWeiXinpay = schema.addEntity("PxWeiXinpay");
    PxWeiXinpay.setJavaDoc("微信支付相关参数");
    PxWeiXinpay.setTableName("PxWeiXinpay");
    PxWeiXinpay.setHasKeepSections(true);
    PxWeiXinpay.implementsSerializable();
    PxWeiXinpay.addIdProperty().primaryKeyAsc();

    PxWeiXinpay.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    PxWeiXinpay.addStringProperty("key").javaDocField("微信key").codeBeforeField("@Expose");
    PxWeiXinpay.addStringProperty("appId").javaDocField("商户APPID").codeBeforeField("@Expose");
    PxWeiXinpay.addStringProperty("macId").javaDocField("商户号").codeBeforeField("@Expose");
    PxWeiXinpay.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }

  /**
   * 商户 支付宝信息
   */
  //3 -> 4
  private static void addPxAlipayInfo(Schema schema) {
    pxAlipayInfo = schema.addEntity("PxAlipayInfo");
    pxAlipayInfo.setJavaDoc("支付宝商户信息");
    pxAlipayInfo.setTableName("PxAlipayInfo");
    pxAlipayInfo.setHasKeepSections(true);
    pxAlipayInfo.implementsSerializable();
    pxAlipayInfo.addIdProperty().primaryKeyAsc();

    pxAlipayInfo.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxAlipayInfo.addStringProperty("alipayAccount").javaDocField("商家支付宝账号").codeBeforeField("@Expose");
    pxAlipayInfo.addStringProperty("sellerId").javaDocField("合作伙伴身份ID").codeBeforeField("@Expose");
    pxAlipayInfo.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
  }


  /**
   * 套餐分组
   * @param schema
   */
  private static void addComboGroup(Schema schema) {
    pxComboGroup = schema.addEntity("PxComboGroup");
    pxComboGroup.setJavaDoc("套餐分组");
    pxComboGroup.setTableName("ComboGroup");
    pxComboGroup.setHasKeepSections(true);
    pxComboGroup.implementsSerializable();
    pxComboGroup.addIdProperty().primaryKeyAsc();

    pxComboGroup.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxComboGroup.addStringProperty("name").javaDocField("分组名称").codeBeforeField("@Expose");
    pxComboGroup.addStringProperty("type").javaDocField("0:允许自选 1：必须全部选择").codeBeforeField("@Expose");
    pxComboGroup.addIntProperty("allowNum").javaDocField("允许点单的数量,默认为1").codeBeforeField("@Expose");
    pxComboGroup.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");

    //连接商品
    Property dbComboId = pxComboGroup.addLongProperty("dbComboId").notNull().getProperty();
    pxComboGroup.addToOne(pxProductInfo,dbComboId,"dbCombo");
  }

  /**
   * 套餐分组和商品rel
   * @param schema
   */
  private static void addComboGroupAndProdRel(Schema schema) {
    pxComboProductRel = schema.addEntity("PxComboProductRel");
    pxComboProductRel.setJavaDoc("套餐分组和商品rel");
    pxComboProductRel.setTableName("ComboProductRel");
    pxComboProductRel.setHasKeepSections(true);
    pxComboProductRel.implementsSerializable();
    pxComboProductRel.addIdProperty().primaryKeyAsc();

    pxComboProductRel.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxComboProductRel.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxComboProductRel.addIntProperty("num").javaDocField("数量").codeBeforeField("@Expose");
    pxComboProductRel.addDoubleProperty("weight").javaDocField("重量").codeBeforeField("@Expose");
    //关联套餐分组
    Property pxComboGroupId = pxComboProductRel.addLongProperty("pxComboGroupId").getProperty();
    pxComboProductRel.addToOne(pxComboGroup,pxComboGroupId, "dbComboGroup");
    //关联商品
    Property pxProductId = pxComboProductRel.addLongProperty("pxProductId").getProperty();
    pxComboProductRel.addToOne(pxProductInfo, pxProductId, "dbProduct");
    //关联规格
    Property pxFormatId = pxComboProductRel.addLongProperty("pxFormatId").getProperty();
    pxComboProductRel.addToOne(pxFormatInfo,pxFormatId,"dbFormat");
  }

  /**
   * 商品备注
   * @param schema
   */
  private static void addPxProductRemarks(Schema schema) {
    pxProductRemarks = schema.addEntity("PxProductRemarks");
    pxProductRemarks.setJavaDoc("商品备注");
    pxProductRemarks.setTableName("ProductRemarks");
    pxProductRemarks.setHasKeepSections(true);
    pxProductRemarks.implementsSerializable();
    pxProductRemarks.addIdProperty().primaryKeyAsc();

    pxProductRemarks.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxProductRemarks.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxProductRemarks.addStringProperty("remarks").javaDocField("备注").codeBeforeField("@Expose");
  }

  /**
   * 打印信息
   */
  //6->7
  private static void addPrintDetails(Schema schema){
    printDetails = schema.addEntity("PrintDetails");
    printDetails.setJavaDoc("打印信息");
    printDetails.setTableName("PrintDetails");
    printDetails.setHasKeepSections(true);
    printDetails.implementsSerializable();
    printDetails.addIdProperty().primaryKeyAsc();

    printDetails.addStringProperty("status").javaDocField("商品状态(0:正常 1：延迟)").codeBeforeField("@Expose");
    printDetails.addStringProperty("orderStatus").javaDocField("下单状态(0:未下单,1:已下单,2:退货)").codeBeforeField("@Expose");
    printDetails.addDoubleProperty("num").javaDocField("数量").codeBeforeField("@Expose");
    printDetails.addDoubleProperty("multipleUnitNumber").javaDocField("多单位数量 一般为重量").codeBeforeField("@Expose");
    printDetails.addStringProperty("remarks").javaDocField("备注").codeBeforeField("@Expose");
    printDetails.addStringProperty("inCombo").javaDocField("是否为套餐内的Details").codeBeforeField("@Expose");
    //连接商品
    Property pxProductInfoId =printDetails.addLongProperty("pxProductInfoId").getProperty();
    printDetails.addToOne(pxProductInfo, pxProductInfoId, "dbProduct");
    //连接订单信息
    Property pxOrderInfoId = printDetails.addLongProperty("pxOrderInfoId").getProperty();
    printDetails.addToOne(pxOrderInfo, pxOrderInfoId, "dbOrder");
    //关联操作原因
    Property pxOptReasonId = printDetails.addLongProperty("pxOptReasonId").getProperty();
    printDetails.addToOne(pxOptReason,pxOptReasonId,"dbReason");
    //关联规格
    Property pxFormatInfoId = printDetails.addLongProperty("pxFormatInfoId").getProperty();
    printDetails.addToOne(pxFormatInfo, pxFormatInfoId, "dbFormatInfo");
    //关联做法
    Property pxMethodInfoId = printDetails.addLongProperty("pxMethodInfoId").getProperty();
    printDetails.addToOne(pxMethodInfo, pxMethodInfoId, "dbMethodInfo");

    //14->15
    printDetails.addStringProperty("formatName").javaDocField("规格名称");
    printDetails.addStringProperty("methodName").javaDocField("做法名称");
    printDetails.addStringProperty("reasonName").javaDocField("退撤菜原因");
  }

  /**
   * 打印汇总
   */
  //6->7
  private static void addPrintDetailsCollect(Schema schema) {
    printDetailsCollect = schema.addEntity("PrintDetailsCollect");
    printDetailsCollect.setJavaDoc("打印汇总类");
    printDetailsCollect.setTableName("PrintDetailsCollect");
    printDetailsCollect.setHasKeepSections(true);
    printDetailsCollect.implementsSerializable();
    printDetailsCollect.addIdProperty().primaryKeyAsc();

    printDetailsCollect.addDateProperty("operateTime").javaDocField("操作时间").codeBeforeField("@Expose");
    printDetailsCollect.addBooleanProperty("isPrint").javaDocField("是否已打印").codeBeforeField("@Expose");
    printDetailsCollect.addStringProperty("type").javaDocField("下单类型(0:下单2:退菜)").codeBeforeField("@Expose");

    //连接订单信息
    Property pxOrderInfoId = printDetailsCollect.addLongProperty("pxOrderInfoId").getProperty();
    printDetailsCollect.addToOne(pxOrderInfo, pxOrderInfoId, "dbOrder");

    //连接配餐方案
    Property dbConfigId = printDetailsCollect.addLongProperty("dbConfigId").getProperty();
    printDetailsCollect.addToOne(pxPxProductConfigPlan,dbConfigId,"dbConfig");
  }

  /**
   * PrintDetails和配菜方案Rel表
   */
  //6->7
  private static void addPrintDetailsConfigRel(Schema schema) {
    printDetailsConfigRel = schema.addEntity("PdConfigRel");
    printDetailsConfigRel.setJavaDoc("打印详情和配菜方案rel");
    printDetailsConfigRel.setTableName("PdConfigRel");
    printDetailsConfigRel.setHasKeepSections(true);
    printDetailsConfigRel.implementsSerializable();
    printDetailsConfigRel.addIdProperty().primaryKeyAsc();

    printDetailsConfigRel.addStringProperty("type").javaDocField("0：下单 1:退单)").codeBeforeField("@Expose");
    printDetailsConfigRel.addDateProperty("operateTime").javaDocField("操作时间(下单时间或者退货时间)").codeBeforeField("@Expose");
    printDetailsConfigRel.addBooleanProperty("isPrinted").javaDocField("是否已打印").codeBeforeField("@Expose");

    //连接订单信息
    Property pxOrderInfoId = printDetailsConfigRel.addLongProperty("pxOrderInfoId").getProperty();
    printDetailsConfigRel.addToOne(pxOrderInfo, pxOrderInfoId, "dbOrder");

    //连接订单详情
    Property dbPdId = printDetailsConfigRel.addLongProperty("dbPrintDetailsId").getProperty();
    printDetailsConfigRel.addToOne(printDetails,dbPdId,"dbPrintDetails");

    //连接配餐方案
    Property dbConfigId = printDetailsConfigRel.addLongProperty("dbConfigId").getProperty();
    printDetailsConfigRel.addToOne(pxPxProductConfigPlan,dbConfigId,"dbConfig");

    //打印汇总和rel 一对多
    Property printInfoConfigRelId = printDetailsConfigRel.addLongProperty("PdConfigRelId").getProperty();
    printDetailsConfigRel.addToOne(printDetailsCollect, printInfoConfigRelId, "dbPdCollect");

    ToMany pdToCollect = printDetailsCollect.addToMany(printDetailsConfigRel, printInfoConfigRelId);
    pdToCollect.setName("dbPdConfigRelList");
  }

  /**
   * 桌台和订单rel
   */
  //6->7
  private static void addTableOrderRel(Schema schema) {
    tableOrderRel = schema.addEntity("TableOrderRel");
    tableOrderRel.setJavaDoc("桌台和订单Rel");
    tableOrderRel.setTableName("TableOrderRel");
    tableOrderRel.setHasKeepSections(true);
    tableOrderRel.implementsSerializable();
    tableOrderRel.addIdProperty().primaryKeyAsc();

    tableOrderRel.addDateProperty("orderEndTime").javaDocField("订单结束时间").codeBeforeField("@Expose");

    //关联订单
    Property pxOrderInfoId = tableOrderRel.addLongProperty("pxOrderInfoId").getProperty();
    tableOrderRel.addToOne(pxOrderInfo,pxOrderInfoId, "dbOrder");
    //关联桌台
    Property pxTableInfoId = tableOrderRel.addLongProperty("pxTableInfoId").getProperty();
    tableOrderRel.addToOne(pxTableInfo, pxTableInfoId, "dbTable");
  }


  /**
   * 优惠券
   * @param schema
   */
  private static void addVoucher(Schema schema) {
    pxVoucher = schema.addEntity("PxVoucher");
    pxVoucher.setJavaDoc("优惠券");
    pxVoucher.setTableName("PxVoucher");
    pxVoucher.setHasKeepSections(true);
    pxVoucher.implementsSerializable();
    pxVoucher.addIdProperty().primaryKeyAsc();

    pxVoucher.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxVoucher.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxVoucher.addStringProperty("code").javaDocField("编码").codeBeforeField("@Expose");
    pxVoucher.addDoubleProperty("price").javaDocField("优惠券金额").codeBeforeField("@Expose");
    pxVoucher.addDoubleProperty("deratePrice").javaDocField("减免金额").codeBeforeField("@Expose");
    pxVoucher.addStringProperty("type").javaDocField("优惠券类型(0:默认1:其他)说明默认为0").codeBeforeField("@Expose");
    pxVoucher.addDateProperty("startDate").javaDocField("开始日期").codeBeforeField("@Expose");
    pxVoucher.addDateProperty("endDate").javaDocField("结束日期").codeBeforeField("@Expose");
    pxVoucher.addStringProperty("permanent").javaDocField("是否永久有效(0：否 1：是)").codeBeforeField("@Expose");
  }

  /**
   * 支付方式
   */
  private static void addPaymentMode(Schema schema) {
    pxPaymentMode = schema.addEntity("PxPaymentMode");
    pxPaymentMode.setJavaDoc("支付方式");
    pxPaymentMode.setTableName("PaymentMode");
    pxPaymentMode.setHasKeepSections(true);
    pxPaymentMode.implementsSerializable();
    pxPaymentMode.addIdProperty().primaryKeyAsc();

    pxPaymentMode.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxPaymentMode.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");

    pxPaymentMode.addStringProperty("name").javaDocField("付款方式名称").codeBeforeField("@Expose");
    pxPaymentMode.addStringProperty("type").javaDocField("支付类型(0:现金 1:银行卡 2:会员卡 3：团购（外卖）平台 4：代金券 5：团购券 6：免单 7：挂账 8：微信 9：支付宝 10：其它 11：翼支付)").codeBeforeField("@Expose");
    pxPaymentMode.addStringProperty("salesAmount").javaDocField("是否计算入销售额(0:是 1：否)").codeBeforeField("@Expose");
    pxPaymentMode.addStringProperty("edit").javaDocField("是否可编辑 0:是 1：否").codeBeforeField("@Expose");
    pxPaymentMode.addIntProperty("orderNo").javaDocField("排序号").codeBeforeField("@Expose");
    pxPaymentMode.addStringProperty("openBox").javaDocField("是否打开钱箱（0:是 1：否）").codeBeforeField("@Expose");
  }

  /**
   * 电子支付信息
   * 6 - > 7
   */
  private static void addEPaymentInfo(Schema schema) {
    ePaymentInfo = schema.addEntity("EPaymentInfo");
    ePaymentInfo.setJavaDoc("电子支付信息");
    ePaymentInfo.setTableName("EPaymentInfo");
    ePaymentInfo.setHasKeepSections(true);
    ePaymentInfo.implementsSerializable();
    ePaymentInfo.addIdProperty().primaryKeyAsc();

    ePaymentInfo.addStringProperty("type").javaDocField("类型(0:支付宝 1:微信 2:会员)").codeBeforeField("@Expose");
    ePaymentInfo.addDateProperty("payTime").javaDocField("支付时间").codeBeforeField("@Expose");
    ePaymentInfo.addStringProperty("orderNo").javaDocField("单号").codeBeforeField("@Expose");
    ePaymentInfo.addStringProperty("tableName").javaDocField("桌名").codeBeforeField("@Expose");
    ePaymentInfo.addStringProperty("status").javaDocField("状态(0:已付款 1:已退款 2:付款过并已退款)").codeBeforeField("@Expose");
    ePaymentInfo.addStringProperty("tradeNo").javaDocField("交易码").codeBeforeField("@Expose");
    ePaymentInfo.addDoubleProperty("price").javaDocField("支付金额").codeBeforeField("@Expose");
    ePaymentInfo.addStringProperty("isHandled").javaDocField("已处理(0:未处理 1:已处理)").codeBeforeField("@Expose");

    Property payInfoId = ePaymentInfo.addLongProperty("payInfoId").getProperty();
    ePaymentInfo.addToOne(pxPayInfo,payInfoId,"dbPayInfo");

    Property orderInfoId = ePaymentInfo.addLongProperty("orderInfoId").getProperty();
    ePaymentInfo.addToOne(pxOrderInfo,orderInfoId,"dbOrder");
  }

  /**
   * 操作记录
   */
  private static void addOperationRecord(Schema schema) {
    pxOperationLog = schema.addEntity("PxOperationLog");
    pxOperationLog.setJavaDoc("操作记录");
    pxOperationLog.setTableName("OperationLog");
    pxOperationLog.setHasKeepSections(true);
    pxOperationLog.implementsSerializable();
    pxOperationLog.addIdProperty().primaryKeyAsc();

    pxOperationLog.addStringProperty("orderNo").javaDocField("订单号").codeBeforeField("@Expose");
    pxOperationLog.addStringProperty("operater").javaDocField("操作人名称").codeBeforeField("@Expose");
    pxOperationLog.addStringProperty("productName").javaDocField("商品名称").codeBeforeField("@Expose");
    pxOperationLog.addStringProperty("type").javaDocField("类型 0：退单 1：撤单").codeBeforeField("@Expose");
    pxOperationLog.addStringProperty("remarks").javaDocField("操作缘由").codeBeforeField("@Expose");
    pxOperationLog.addStringProperty("cid").javaDocField("公司ID").codeBeforeField("@Expose");

    pxOperationLog.addLongProperty("operaterDate").javaDocField("操作日期").codeBeforeField("@Expose");
    pxOperationLog.addDoubleProperty("totalPrice").javaDocField("价格").codeBeforeField("@Expose");
  }


  /**
   * 团购券
   */
  private static void addPxBuyCoupons(Schema schema) {
    pxBuyCoupons = schema.addEntity("PxBuyCoupons");
    pxBuyCoupons.setJavaDoc("团购群");
    pxBuyCoupons.setTableName("PxBuyCoupons");
    pxBuyCoupons.setHasKeepSections(true);
    pxBuyCoupons.implementsSerializable();
    pxBuyCoupons.addIdProperty().primaryKeyAsc();

    pxBuyCoupons.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxBuyCoupons.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");

    pxBuyCoupons.addStringProperty("name").javaDocField("团购券名称").codeBeforeField("@Expose");
    pxBuyCoupons.addDoubleProperty("amount").javaDocField("金额").codeBeforeField("@Expose");
    pxBuyCoupons.addDoubleProperty("offsetAmount").javaDocField("抵消金额").codeBeforeField("@Expose");

    //关联支付方式
    Property paymentModeId = pxBuyCoupons.addLongProperty("paymentModeId").getProperty();
    pxBuyCoupons.addToOne(pxPaymentMode,paymentModeId,"dbPayment");
  }
  /**
   * Smack UUID 记录
   */
  private static void addSmackUUId(Schema schema) {
    uuidRecord = schema.addEntity("SmackUUIDRecord");
    uuidRecord.setJavaDoc("Smack UUID 记录");
    uuidRecord.setTableName("SmackUUIDRecord");
    uuidRecord.setHasKeepSections(true);
    uuidRecord.implementsSerializable();
    uuidRecord.addIdProperty().primaryKeyAsc();

    uuidRecord.addStringProperty("uuid").javaDocField("UUID").codeBeforeField("@Expose");
    uuidRecord.addDateProperty("operateTime").javaDocField("操作时间").codeBeforeField("@Expose");
  }
  /**
   * 桌台区域
   */
  private static void addTableArea(Schema schema) {
    pxTableArea = schema.addEntity("PxTableArea");
    pxTableArea.setJavaDoc("桌台区域");
    pxTableArea.setTableName("PxTableArea");
    pxTableArea.setHasKeepSections(true);
    pxTableArea.implementsSerializable();
    pxTableArea.addIdProperty().primaryKeyAsc();

    pxTableArea.addStringProperty("delFlag").javaDocField("虚拟删除 0：正常 1：删除 2：审核").codeBeforeField("@Expose");
    pxTableArea.addStringProperty("objectId").javaDocField("对应服务器id").codeBeforeField("@SerializedName(\"id\") @Expose");
    pxTableArea.addStringProperty("type").javaDocField("区域(0:大厅，1包厢)").codeBeforeField("@Expose");
    pxTableArea.addStringProperty("name").javaDocField("名称").codeBeforeField("@Expose");
  }
  /**
   * 添加蓝牙打印机
   */
  private static void addBTDevice(Schema schema){
    btPrintDevice = schema.addEntity("BTPrintDevice");
    btPrintDevice.setJavaDoc("蓝牙打印机");
    btPrintDevice.setTableName("BTPrintDevice");
    btPrintDevice.setHasKeepSections(true);
    btPrintDevice.implementsSerializable();
    btPrintDevice.addIdProperty().primaryKeyAsc();

    btPrintDevice.addStringProperty("address").javaDocField("Address");
    btPrintDevice.addStringProperty("format").javaDocField("规格(0：58mm 1：60mm)");

  }
}