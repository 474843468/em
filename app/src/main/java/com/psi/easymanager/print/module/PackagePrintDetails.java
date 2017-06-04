package com.psi.easymanager.print.module;

/**
 * 作者：${ylw} on 2017-03-25 13:41
 */
public class PackagePrintDetails {
  private String formatName;
  private String methodName;
  private String reasonName;

  public String getFormatName() {
    return formatName == null ? "" : formatName;
  }

  public void setFormatName(String formatName) {
    this.formatName = formatName;
  }

  public String getMethodName() {
    return methodName == null ? "" : methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String getReasonName() {
    return reasonName == null ? "" : reasonName;
  }

  public void setReasonName(String reasonName) {
    this.reasonName = reasonName;
  }
}
