package com.psi.easymanager.event;

import com.psi.easymanager.module.BTPrintDevice;

/**
 * 作者：${ylw} on 2017-03-09 14:17
 */
public class SwitchBTDeviceEvent {
  private boolean mSwitch;
  private BTPrintDevice mDevice;

  public SwitchBTDeviceEvent(boolean aSwitch, BTPrintDevice device) {
    mSwitch = aSwitch;
    mDevice = device;
  }

  public boolean isSwitch() {
    return mSwitch;
  }

  public BTPrintDevice getDevice() {
    return mDevice;
  }

  @Override public String toString() {
    return "SwitchBTDeviceEvent{" + "mSwitch=" + mSwitch + ", mDevice=" + mDevice + '}';
  }
}
