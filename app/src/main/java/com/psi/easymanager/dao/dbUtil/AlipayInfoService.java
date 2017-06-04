package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxAlipayInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * User: ylw
 * Date: 2016-08-15
 * Time: 17:50
 * 支付宝商户信息
 */
public class AlipayInfoService extends DbService<PxAlipayInfo, Long> {
  public AlipayInfoService(AbstractDao dao) {
    super(dao);
  }
}