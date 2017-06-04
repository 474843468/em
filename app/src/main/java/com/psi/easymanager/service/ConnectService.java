package com.psi.easymanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.psi.easymanager.chat.chatutils.XMPPConnectUtils;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by dorado on 2016/6/28.
 */
public class ConnectService extends Service {
  private Timer mTimer;
  private TimerTask mTimerTask;

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    mTimer = new Timer();
    mTimerTask = new TimerTask() {
      @Override public void run() {
        if (XMPPConnectUtils.getConnection() != null && !XMPPConnectUtils.getConnection().isConnected()) {
          try {
            XMPPConnectUtils.getConnection().connect();
          } catch (SmackException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          } catch (XMPPException e) {
            e.printStackTrace();
          }
        }
      }
    };
    mTimer.schedule(mTimerTask, 1000, 3000);
  }

  @Override public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
    if (mTimer != null) {
      mTimerTask.cancel();
      mTimerTask =null;
    }
  }
}
