package com.psi.easymanager.service;

import android.app.IntentService;
import android.content.Intent;
import com.psi.easymanager.BuildConfig;
import com.psi.easymanager.chat.chatutils.XMPPConnectUtils;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.ChatLoginEvent;
import com.psi.easymanager.module.User;
import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ChatLoginService extends IntentService {
  private String user = null;
  private boolean isDestroy;
  public ChatLoginService() {
    super("ChatLoginService");
  }

  @Override protected void onHandleIntent(Intent intent) {
    User admin = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.DelFlag.eq("0"))
        .where(UserDao.Properties.LoginName.eq("admin"))
        .unique();
    if (admin != null) {
      //初始化连接配置
      XMPPTCPConnectionConfiguration  connectionConfiguration = XMPPTCPConnectionConfiguration.builder()
          .setConnectTimeout(10 * 1000)
          .setServiceName(BuildConfig.SERVER_NAME)
          .setUsernameAndPassword(admin.getImUserName(), admin.getInitPassword())
          .setHost(BuildConfig.HOST)
          .setCompressionEnabled(false)
          .setDebuggerEnabled(false)
          .setSendPresence(true)
          .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
          .build();
      //登陆 服务停止或已登陆成功
      while (!isDestroy && user == null) {
        XMPPConnectUtils.connectAndLogin(connectionConfiguration);
        user = XMPPConnectUtils.getConnection().getUser();
        try {
          Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      if (!isDestroy) {
        //发MainActivity 注册连接监听
        EventBus.getDefault().post(new ChatLoginEvent());
        //开启chatSmack service
        startService(new Intent(this, ChatSmackService.class));
        //开启ping service
        startService(new Intent(this, PingService.class));
        //开启connect service
        startService(new Intent(this, ConnectService.class));
      }
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    isDestroy = true;
  }
}
