package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

public class HttpDataSyncReq extends HttpReq {

  @Expose private String loginName;//用户登录名
  @Expose private String type;//（数据同步类型 0：收银端 1：服务生端）
  @Expose private String password;//店家密码
  @Expose private String install; //是否新安装的app （0：是 1：否）
  @Expose private String initPassword;//初始化密码
  @Expose private String versionName;//版本名称


  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getInstall() {
    return install;
  }

  public void setInstall(String install) {
    this.install = install;
  }

  public String getInitPassword() {
    return initPassword;
  }

  public void setInitPassword(String initPassword) {
    this.initPassword = initPassword;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

}
