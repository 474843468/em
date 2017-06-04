package com.psi.easymanager.print.usb;

import com.gprinter.aidl.GpService;
import java.io.Serializable;

/**
 * Created by psi on 2016/11/15.
 */

public class AppGpService implements Serializable {

  //Usb打印服务
  private GpService mGpService;

  public AppGpService(GpService mGpService) {
    this.mGpService = mGpService;
  }

  public GpService getGpService() {
    return mGpService;
  }

  public void setGpService(GpService mGpService) {
    this.mGpService = mGpService;
  }
}
