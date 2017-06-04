package com.psi.easymanager.config;

import com.psi.easymanager.BuildConfig;

/**
 * User: ylw
 * Date: 2016-10-21
 * Time: 18:29
 * FIXME
 */
public class URLConstants {
  /**
   * 公网   服务器地址 、Smack Host 、 服务器名
   */
  //public static final String BASE_URL = "http://api.yizhanggui.cc/api/";
  //public static final String HOST = "im.yizhanggui.cc";
  //public static final String SERVER_NAME = "iz25tedwz5sz";

  /**
   * 221  服务器地址 、Smack Host 、 服务器名
   */
  //public static final String BASE_URL = "http://192.168.1.221:80/api/";
  //public static final String HOST = "192.168.1.62";
  //public static final String SERVER_NAME = "2013-20150715FR";

  /**
   * 62  服务器地址 、Smack Host 、 服务器名
   */
  //public static String BASE_URL = "http://192.168.1.62:8182/api/";
  //public static final String HOST = "192.168.1.62";
  //public static final String SERVER_NAME = "2013-20150715FR";

  /**
   * 63  服务器地址 、Smack Host 、 服务器名
   */
  //public static String BASE_URL = "http://192.168.1.63:8080/api/";
  //public static final String HOST = "192.168.1.62";
  //public static final String SERVER_NAME = "2013-20150715FR";

  /**
   * 测试
   */
  //public static final String BASE_URL = "http://192.168.1.209:8181/api/";
  //public static final String HOST = "192.168.1.62";
  //public static final String SERVER_NAME = "2013-20150715FR";
  /**
   * 测试
   */
  //public static final String BASE_URL = "http://192.168.1.63:8080/api/";
  //public static final String HOST = "192.168.1.62";
  //public static final String SERVER_NAME = "2013-20150715FR";

  //alipay退款
  public static final String ALI_REFUND = BuildConfig.BASE_URL + "alipay/refund";
  //alipay退款结果查询
  public static final String ALI_REFUND_QUERY = BuildConfig.BASE_URL + "alipay/refundQuery";
  //alipay 付款
  public static final String ALI_TRADE = BuildConfig.BASE_URL + "alipay/trade";
  //alipay 付款结果查询
  public static final String ALI_TRADE_QUERY = BuildConfig.BASE_URL + "alipay/query";
  //上传自定义商品
  public static final String UPLOAD_PRODUCT = BuildConfig.BASE_URL + "product/uploadProductInfo";
  //上传订单
  public static final String UPLOAD_ORDER = BuildConfig.BASE_URL + "order/uploadOrderInfo";
  //版本更新
  public static final String VERSION_UPDATE = BuildConfig.BASE_URL + "version/update";
  //数据更新
  public static final String DATA_SYNC = BuildConfig.BASE_URL + "v1/dataSync/list";
  //微信支付
  public static final String WEIXIN_PAY = BuildConfig.BASE_URL + "weixinpay/pay";
  //微信支付结果查询
  public static final String WEIXIN_PAY_QUERY = BuildConfig.BASE_URL + "weixinpay/payQuery";
  //微信退款
  public static final String WEIXIN_RETURN = BuildConfig.BASE_URL + "weixinpay/refund";
  //微信退款 结果查询
  public static final String WEIXIN_RETURN_QUERY = BuildConfig.BASE_URL + "weixinpay/refundQuery";
  //微信撤销
  public static final String WEIXIN_REVERSE = BuildConfig.BASE_URL + "weixinpay/reverse";
  //会员充值记录
  public static final String VIP_RECHARGE_RECORD_LIST =
      BuildConfig.BASE_URL + "vip/rechargeRecordList";
  //会员卡充值记录
  public static final String VIP_CARD_RECHARGE_RECORD_LIST =
      BuildConfig.BASE_URL + "idcard/rechargeRecordIdCardList";
  //会员登录
  public static final String VIP_LOGIN = BuildConfig.BASE_URL + "vip/login";
  //会员卡登录
  public static final String VIP_CARD_LOGIN = BuildConfig.BASE_URL + "idcard/idCardlogin";
  //会员消费
  public static final String VIP_CONSUME = BuildConfig.BASE_URL + "vip/consume";
  //会员卡消费
  public static final String VIP_CARD_CONSUME = BuildConfig.BASE_URL + "idcard/idCardConsume";
  //会员充值
  public static final String VIP_RECHARGE = BuildConfig.BASE_URL + "vip/vipRecharge";
  //会员卡充值
  public static final String VIP_CARD_RECHARGE = BuildConfig.BASE_URL + "idcard/idCradRecharge";
  //会员消费记录冲正
  public static final String VIP_CONSUME_RECORD_REVERSE =
      BuildConfig.BASE_URL + "vip/reverseConsume";
  //会员卡消费记录冲正
  public static final String VIP_CARD_CONSUME_RECORD_REVERSE =
      BuildConfig.BASE_URL + "idcard/idCardReverse";
  //会员充值记录冲正
  public static final String VIP_RECHARGE_RECORD_REVERSE = BuildConfig.BASE_URL + "vip/reverse";
  //会员卡充值记录冲正
  public static final String VIP_CARD_RECHARGE_RECORD_REVERSE =
      BuildConfig.BASE_URL + "idcard/reverse";
  //添加新会员
  public static final String VIP_ADD_NEW = BuildConfig.BASE_URL + "vip/addVip";
  //会员修改
  public static final String VIP_UPDATEINFO = BuildConfig.BASE_URL + "vip/updateVipInfo";
  //获取全部会员列表
  public static final String VIP_LIST = BuildConfig.BASE_URL + "vip/list";
  //模糊查询获取会员列表
  public static final String VIP_VIPLIST = BuildConfig.BASE_URL + "vip/vipList";
  //订单 的所有在线支付 包含支付宝、微信
  public static final String NET_PAY_RECORD = BuildConfig.BASE_URL + "payrecord/list";
  //退菜 撤单 OperateLogs
  public static final String REFUND_OPERATE_RECORD = BuildConfig.BASE_URL + "optlogs";
  //上传崩溃信息
  public static final String UPLOAD_ERROR_LOG = BuildConfig.BASE_URL + "exlog";
  //翼支付支付
  public static final String BEST_PAY = BuildConfig.BASE_URL + "v1/bestpay/pay";
  //翼支付查询
  public static final String BEST_PAY_QUERY = BuildConfig.BASE_URL + "v1/bestpay/query";
  //翼支付退款
  public static final String BEST_PAY_REFUND = BuildConfig.BASE_URL + "v1/bestpay/refund";
  //翼支付冲正
  public static final String BEST_PAY_REVERSE = BuildConfig.BASE_URL + "v1/bestpay/reverse";
}