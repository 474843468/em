package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.upload.module.UpLoadProduct;

/**
 * 自定义商品 res
 */
public class HttpSingleProductResp extends HttpResp {
  @Expose private UpLoadProduct product;
  //List<PxProductPrinterRel> respRelList = new ArrayList<PxProductPrinterRel>();
  public UpLoadProduct getProduct() {
    return product;
  }

  public void setProduct(UpLoadProduct product) {
    this.product = product;
  }
}
