package com.psi.easymanager.network;

import java.io.Serializable;

public class DataSync implements Serializable {

  private static final long serialVersionUID = 6163820970312343950L;

  public static final int INSERT = 0;
  public static final int UPDATE = 1;
  public static final int DELETE = 2;
  //public static final int SAVE_OR_UPDATE = 3;
  public static final String SYNC_DATA_PREFIX = "sync";
  public static final String INIT_DATA_PREFIX = "init";

  private String prefix = SYNC_DATA_PREFIX;
  private String id;// id
  private String className;//类完整包路径
  private String simpleClassName;//类名
  private int opt; //操作符 (新增，修改，删除)
  private String companyCode;//公司编码
  private Object data;//数据

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getSimpleClassName() {
    return simpleClassName;
  }

  public void setSimpleClassName(String simpleClassName) {
    this.simpleClassName = simpleClassName;
  }

  public int getOpt() {
    return opt;
  }

  public void setOpt(int opt) {
    this.opt = opt;
  }

  public String getCompanyCode() {
    return companyCode;
  }

  public void setCompanyCode(String companyCode) {
    this.companyCode = companyCode;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
