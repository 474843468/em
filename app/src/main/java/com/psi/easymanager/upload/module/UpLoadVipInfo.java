package com.psi.easymanager.upload.module;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 * User: ylw
 * Date: 2016-06-12
 * Time: 09:42
 * 用于上传会员信息
 */
public class UpLoadVipInfo implements Serializable {
  @Expose private String name; // 会员名称
  @Expose private String code; // 会员编码
  @Expose private String mobile; // 手机号
  @Expose private String level; // 会员级别
  @Expose private Double accountBalance = 0.0; // 账户余额
  @Expose private String id;//转ObjId
  @Expose private String delFlag;//虚拟删除 0：正常 1：删除 2：审核
  @Expose private String companyCode;//公司编码

  public String getDelFlag() {
    return delFlag;
  }

  public String getCompanyCode() {
    return companyCode;
  }

  public void setCompanyCode(String companyCode) {
    this.companyCode = companyCode;
  }

  public void setDelFlag(String delFlag) {
    this.delFlag = delFlag;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public Double getAccountBalance() {
    return accountBalance;
  }

  public void setAccountBalance(Double accountBalance) {
    this.accountBalance = accountBalance;
  }
}