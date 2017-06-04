package com.psi.easymanager.chat.chatutils;

import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sm.predicates.ForEveryStanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * Created by dorado on 2016/6/23.
 */
public class XMPPConnectUtils {

  private static XMPPTCPConnection sConnection;

  /**
   * 连接并登录
   */
  public static void connectAndLogin(XMPPTCPConnectionConfiguration configuration) {
    if (sConnection != null && sConnection.isConnected()) return;
    sConnection = new XMPPTCPConnection(configuration);
    sConnection.addRequestAckPredicate(ForEveryStanza.INSTANCE);
    sConnection.setUseStreamManagement(true);
    sConnection.setUseStreamManagementResumption(false);
    try {
      sConnection.connect();
      sConnection.login();
    } catch (XMPPException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SmackException e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取状态
   */
  public static boolean getConnectStatus() {
    if (sConnection == null || !sConnection.isConnected()) {
      return false;
    }
    return true;
  }

  /**
   * 关闭连接
   */
  public static void closeConnection() {
    sConnection.disconnect();
  }

  /**
   * 获取连接对象
   */
  public static XMPPTCPConnection getConnection() {
    return sConnection;
  }
}