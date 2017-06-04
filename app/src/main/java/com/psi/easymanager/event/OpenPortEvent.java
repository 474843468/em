package com.psi.easymanager.event;

/**
 * Created by psi on 2016/12/2.
 * 打开端口
 */

public class OpenPortEvent {

  public static final String CASH_BILL_PORT = "0x01";//点菜单

  public static final String CUSTOM_BILL_PORT = "0x02";//结账单客户联
  public static final String FINANCE_BILL_PORT = "0x03";//结账单财务联
  public static final String OPEN_DRAWER_PORT = "0x04";//开启钱箱
  public static final String VIP_CONSUME_PORT = "0x05";//会员消费

  public static final String BILL_CONNECT_PORT = "0x06";//账单汇总
  public static final String BILL_DETAIL_WITH_CUSTOM_PORT = "0x07";//账单明细(客户联)
  public static final String BILL_DETAIL_WITH_FINANCE_PORT = "0x08";//账单明细(财务联)
  public static final String SALE_CONNECT_PORT = "0x09";//销售统计

  public static final String DAY_REPORT_PORT = "0x10";//日结
  public static final String SHIFT_BILL_CONNECT_PORT = "0x11";//账单汇总信息
  public static final String SHIFT_BILL_CATE_PORT = "0x12";//分类汇总信息
  public static final String SHIFT_CONNECT_DATA_PORT = "0x13";//交接后统计数据

  public static final String VIP_RECHARGE_PORT = "0x14";//会员充值

  private String type;

  public OpenPortEvent() {
  }

  public OpenPortEvent(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
