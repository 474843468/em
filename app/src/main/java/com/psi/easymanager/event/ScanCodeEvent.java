package com.psi.easymanager.event;

/**
 * User: ylw
 * Date: 2017-01-09
 * Time: 15:41
 * FIXME
 */
public class ScanCodeEvent {

  private String payCode;

  public ScanCodeEvent(String payCode) {

    this.payCode = payCode;
  }

  public String getPayCode() {
    return payCode;
  }
}