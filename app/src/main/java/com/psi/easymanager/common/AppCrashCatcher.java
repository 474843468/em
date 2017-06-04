package com.psi.easymanager.common;

import android.content.Context;
import android.text.TextUtils;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.PackageUtils;
import com.psi.easymanager.utils.SPUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 进行错误收集，并由用户选择是否发送回来
 */
public class AppCrashCatcher implements Thread.UncaughtExceptionHandler {

  private static AppCrashCatcher sAppCrashCatcher;
  private Context mContext;

  private AppCrashCatcher() {
  }

  public static AppCrashCatcher newInstance() {
    if (sAppCrashCatcher != null) {
      return sAppCrashCatcher;
    } else {
      return new AppCrashCatcher();
    }
  }

  public void setDefaultCrashCatcher() {
    mContext = App.getContext();
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  @Override public void uncaughtException(Thread thread, final Throwable ex) {
    ex.printStackTrace();
    saveToSDCard(ex);
    try {
      Thread.sleep(3000);
      //退出程序
      android.os.Process.killProcess(android.os.Process.myPid());
      System.exit(0);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private String catchErrors(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    printWriter.close();
    return stringWriter.toString();
  }

  private void saveToSDCard(Throwable throwable) {
    String errorMsg = catchErrors(throwable);
    if (TextUtils.isEmpty(errorMsg)) {
      return;
    }
    File errorLogDir = PackageUtils.getErrorLogDir(mContext);
    if (errorLogDir == null) {
      return;
    }
    if (!errorLogDir.exists()) {
      errorLogDir.mkdirs();
    }
    File crash = new File(errorLogDir, Constants.LOG_NAME);
    StringBuilder buffer = new StringBuilder();
    int localVersionCode = new PackageUtils(mContext).getLocalVersionCode();
    buffer.append(
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "---" + localVersionCode
            + "---");
    buffer.append(errorMsg);

    //    List<String> info = DeviceUtils.getDeviceMsg(mContext);
    //    for (String s : info) {
    //      buffer.append(s).append("\n");
    //    }
    if (!crash.exists()) {
      try {
        crash.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    writerError(crash, buffer.toString());
  }

  //把String写入文件
  public void writerError(File fileName, String content) {
    StringBuilder sb = new StringBuilder();
    BufferedReader br = null;
    FileReader fr = null;
    try {
      fr = new FileReader(fileName);
      br = new BufferedReader(fr);
      String line = null;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      Logger.e(e.toString());
      e.printStackTrace();
    } finally {
      IOUtils.closeCloseables(br);
      IOUtils.closeCloseables(fr);
    }
    String errorTime = "a";
    if (sb.length() > 20) {
      //获取崩溃时间
      errorTime = sb.substring(0, 19);
    }
    BufferedWriter bufw = null;
    FileWriter fileWriter = null;
    //上传成功sp存的崩溃时间
    String OldErrorTime = (String) SPUtils.get(mContext, Constants.ERROR_TIME, "0");
    try {
      if (OldErrorTime.equals(errorTime)) {
        fileWriter = new FileWriter(fileName);
      } else {
        //不重复,追加
        fileWriter = new FileWriter(fileName, true);
      }
      bufw = new BufferedWriter(fileWriter);
      bufw.write(content + "<br/>");
      bufw.newLine();
      bufw.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        IOUtils.closeCloseables(bufw);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
