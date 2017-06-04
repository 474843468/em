package com.psi.easymanager.print.net.event;

import java.util.Map;

/**
 * 作者：${ylw} on 2016-12-17 11:09
 * 打印机连接状态
 */
public interface IPrintConnectStatus {
  void dispatchConnectStatus(Map<String,String> map,boolean isAllConnected);
}
