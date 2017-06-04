package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.upload.module.UpLoadProduct;
import com.psi.easymanager.upload.module.UpLoadProductConfigRel;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义商品req
 */
public class HttpProductReq extends HttpReq {
  @Expose private UpLoadProduct productInfo = new UpLoadProduct(); // 上传单个商品
  @Expose private List<UpLoadProductConfigRel> productConfigPlannRelList = new ArrayList<>();

  public UpLoadProduct getProductInfo() {
    return productInfo;
  }

  public void setProductInfo(UpLoadProduct productInfo) {
    this.productInfo = productInfo;
  }

  public List<UpLoadProductConfigRel> getProductConfigPlannRelList() {
    return productConfigPlannRelList;
  }

  public void setProductConfigPlannRelList(List<UpLoadProductConfigRel> productConfigPlannRelList) {
    this.productConfigPlannRelList = productConfigPlannRelList;
  }
}
