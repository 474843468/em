package com.psi.easymanager.print.constant;

import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.Office;
import java.text.SimpleDateFormat;

/**
 * User: ylw
 * Date: 2017-01-06
 * Time: 17:17
 * FIXME
 */
public class BTPrintConstants {
  public static final Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();//office
  public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static final int PRINT_MODE_DETAILS = 0;//打印OrderDetails
  public static final int PRINT_MODE_TABLE = 1;//打印移并桌信息
  public static final int PRINT_MODE_TEST = 2;//打印测试
  public static final int PRINT_KITCHEN_DETAILS = 3;//厨房打印打印订单
  public static final int PRINT_MODE_DETAILS_COLLECTION = 4;//打印点菜单
  public static final int PRINT_MODE_BILL_SUMMARY = 5;//打印账单汇总
  public static final int PRINT_MODE_SALE_COUNT = 6;//打印销售统计
  public static final int PRINT_MODE_CUSTOMERS_AL = 7;//打印结账单客户联
  public static final int PRINT_MODE_FINANCE_INFO = 8;//打印结账单财务联
  public static final int PRINT_MODE_BILL_DETAIL_CUSTOMER = 9;//打印账单明细（客户联）
  public static final int PRINT_MODE_BILL_DETAIL_FINANCE = 10;//打印账单明细（财务联）
  public static final int PRINT_MODE_REFUND_BILL = 11;//打印撤单
  public static final int PRINT_MODE_SHIFT_WORK_ALL_BILLS = 12;//打印交接班 所有账单
  public static final int PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS = 13;//打印日结账单
  public static final int PRINT_MODE_SHIFT_WORK = 14;//打印交接班
  public static final int PRINT_MODE_COLLECT = 15;//打印下单
  public static final int PRINT_MODE_CATEGORY_COLLECT = 16;//打印分类汇总信息
  public static final int PRINT_MODE_COLLECT_REFUND = 17;//打印退菜
  public static final int PRINT_MODE_VIP_CONSUME_RECORD = 18;//打印会员消费记录
  public static final int PRINT_MODE_VIP_RECHARGE_RECORD = 19;//打印会员充值记录
  public static final int PRINT_MODE_FALSE_DATA = 20;//假数据
  public static final byte[][] byteCommands = {
      { 0x1b, 0x40 },//0. 复位打印机
      { 0x1b, 0x4d, 0x00 },//1. 标准ASCII字体
      { 0x1b, 0x4d, 0x01 },//2. 压缩ASCII字体
      { 0x1d, 0x21, 0x00 },//3. 字体不放大
      { 0x1d, 0x21, 0x02 },//4. 宽高加倍
      { 0x1d, 0x21, 0x11 },//5. 宽高加倍
      { 0x1d, 0x21, 0x11 },//6. 宽高加倍
      { 0x1b, 0x45, 0x00 },//7. 取消加粗模式
      { 0x1b, 0x45, 0x01 },//8. 选择加粗模式
      { 0x1b, 0x7b, 0x00 },//9. 取消倒置打印
      { 0x1b, 0x7b, 0x01 },//10. 选择倒置打印
      { 0x1d, 0x42, 0x00 },//11. 取消黑白反显
      { 0x1d, 0x42, 0x01 },//12. 选择黑白反显
      { 0x1b, 0x56, 0x00 },//13. 取消顺时针旋转90°
      { 0x1b, 0x56, 0x01 },//14. 选择顺时针旋转90°
      { 0x1b, 0x61, 0x30 },//15. 左对齐
      { 0x1b, 0x61, 0x31 },//16. 居中对齐
      { 0x1b, 0x61, 0x32 },//17. 右对齐
      { 0x1b, 0x69 },//18. 切纸
  };
}  