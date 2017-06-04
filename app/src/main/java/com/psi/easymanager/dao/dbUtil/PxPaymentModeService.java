package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxPaymentMode;
import de.greenrobot.dao.AbstractDao;

/**
 * User: ylw
 * Date: 2016-10-12
 * Time: 18:30
 * FIXME
 */
public class PxPaymentModeService extends DbService<PxPaymentMode, Long> {
  public PxPaymentModeService(AbstractDao dao) {
    super(dao);
  }
}