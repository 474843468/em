package com.psi.easymanager.network.resp;

import com.psi.easymanager.pay.query.module.PxNetPayRecord;
import java.io.Serializable;
import java.util.List;

/**
 * User: ylw
 * Date: 2016-10-25
 * Time: 18:46
 * 网络支付记录
 */
public class HttpNetPayRecordResp implements Serializable{
  private List<PxNetPayRecord> list;

  public List<PxNetPayRecord> getList() {
    return list;
  }

  public void setList(List<PxNetPayRecord> list) {
    this.list = list;
  }
}  