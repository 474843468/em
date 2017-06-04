package com.psi.easymanager.chat;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by zjq on 2016/5/3.
 */
public class FireMessage implements Serializable {
  public static final int HALL_LIST_REQ = 1;//获取大厅列表
  public static final int HALL_LIST_RESP = 2;//返回大厅列表

  public static final int PARLOR_LIST_REQ = 3;//获取包间列表
  public static final int PARLOR_LIST_RESP = 4;//返回包间列表

  public static final int ORDER_INFO_REQ = 5;//获取订单信息
  public static final int ORDER_INFO_RESP = 6;//返回订单信息


  public static final int CONFIRM_ORDER_REQ = 11;//确认下单
  public static final int CONFIRM_ORDER_RESP = 12;//确认下单回执

  public static final int CANCEL_ORDER_REQ = 13;//撤单
  public static final int CANCEL_ORDER_RESP = 14;//撤单回执

  public static final int MOVE_TABLE_REQ = 15;//改单时请求 桌台列表
  public static final int MOVE_TABLE_RESP = 16;//改单时返回 桌台列表

  public static final int MODIFY_BILL_REQ = 17;//改单请求
  public static final int MODIFY_BILL_RESP = 18;//改单回执

  public static final int ADD_PROD_REQ = 19;//加菜请求
  public static final int ADD_PROD_RESP = 20;//加菜回执

  public static final int REFUND_PROD_REQ = 21;//退菜请求
  public static final int REFUND_PROD_RESP = 22;//退菜回执

  public static final int GROUP_CHAT_REFRESH_TABLE_LIST = 23;//刷新桌台

  public static final int SERVING_PROD_REQ = 24;//标记上菜
  public static final int SERVING_PROD_RESP = 25;//标记上菜

  //所有空桌台 req、resp 用于开单
  public static final int ALL_EMPTY_TABLE_REQ = 101;
  public static final int ALL_EMPTY_TABLE_RESP = 102;

  //所有已占用桌台 req、resp 用于换桌
  public static final int ALL_OCCUPIED_TABLE_REQ = 103;
  public static final int ALL_OCCUPIED_TABLE_RESP = 104;


  private int operateType;//操作类型
  private String data;//返回数据
  private UUID mUUID;//识别码

  private String mTableId;//桌号

  public FireMessage() {
  }

  public FireMessage(int operateType, String data, UUID UUID) {
    this.operateType = operateType;
    this.data = data;
    this.mUUID = UUID;
  }

  /**
   * Get and Set
   */
  public int getOperateType() {
    return operateType;
  }

  public void setOperateType(int operateType) {
    this.operateType = operateType;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getTableId() {
    return mTableId;
  }

  public void setTableId(String tableId) {
    mTableId = tableId;
  }


  public UUID getUUID() {
    return mUUID;
  }

  public void setUUID(UUID UUID) {
    mUUID = UUID;
  }
}
