package com.psi.easymanager.network.resp;

/**
 * Created by dorado on 2016/6/26.
 */
public class HttpCheckUpdateResp extends HttpResp {

  public static final String FORCE_UPDATE_TRUE = "0";
  public static final String FORCE_UPDATE_FALSE = "1";

  private static final long serialVersionUID = -6249022481432228110L;
  //版本号
  private String versionNumber;
  //url
  private String url;
  // 是否强制更新0:是 1：否
  private String forceUpdate;
  // 更新概要信息
  private String updateInfo;
  //版本code
  private long versionCode;

  public String getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(String versionNumber) {
    this.versionNumber = versionNumber;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getForceUpdate() {
    return forceUpdate;
  }

  public void setForceUpdate(String forceUpdate) {
    this.forceUpdate = forceUpdate;
  }

  public String getUpdateInfo() {
    return updateInfo;
  }

  public void setUpdateInfo(String updateInfo) {
    this.updateInfo = updateInfo;
  }

  public long getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(long versionCode) {
    this.versionCode = versionCode;
  }
}
