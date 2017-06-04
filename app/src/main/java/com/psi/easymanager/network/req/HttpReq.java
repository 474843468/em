package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class HttpReq<T> implements Serializable {

  public static final String ASC = "0";
  public static final String DESC = "1";

  private static final long serialVersionUID = 4102840620413917634L;
  @Expose private String userId;// uid
  @Expose private String companyCode;
  @Expose private String cid;//公司id;
  private String sortField;// 排序字段
  @Expose private Integer pageNo = 1;
  private Integer PageSize = 10;
  private String sortRule = "0"; // 排序规则 升序或降序

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getCompanyCode() {
    return companyCode;
  }

  public void setCompanyCode(String companyCode) {
    this.companyCode = companyCode;
  }

  public String getSortField() {
    return sortField;
  }

  public void setSortField(String sortField) {
    this.sortField = sortField;
  }

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public Integer getPageSize() {
    return PageSize;
  }

  public void setPageSize(Integer pageSize) {
    PageSize = pageSize;
  }

  public String getSortRule() {
    return sortRule;
  }

  public void setSortRule(String sortRule) {
    this.sortRule = sortRule;
  }

  public String getCid() {
    return cid;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }
}
