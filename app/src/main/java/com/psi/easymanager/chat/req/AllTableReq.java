package com.psi.easymanager.chat.req;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 * User: ylw
 * Date: 2017-02-14
 * Time: 15:27
 * FIXME
 */
public class AllTableReq implements Serializable {
  @Expose
  private String mLike;

  public String getLike() {
    return mLike;
  }

  public void setLike(String like) {
    mLike = like;
  }
}