package com.psi.easymanager.upload.module;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxProductConfigPlan;
import com.psi.easymanager.module.PxProductInfo;
import java.io.Serializable;

/**
 * User: ylw
 * Date: 2016-06-14
 * Time: 18:34
 * 上传自定义商品用的配菜方案Rel
 */
public class UpLoadProductConfigRel implements Serializable {
  @Expose private PxProductConfigPlan configPlan;    // 配菜方案
  @Expose private PxProductInfo product;        // 商品
  @Expose private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PxProductConfigPlan getConfigPlan() {
    return configPlan;
  }

  public void setConfigPlan(PxProductConfigPlan configPlan) {
    this.configPlan = configPlan;
  }

  public PxProductInfo getProduct() {
    return product;
  }

  public void setProduct(PxProductInfo product) {
    this.product = product;
  }
}