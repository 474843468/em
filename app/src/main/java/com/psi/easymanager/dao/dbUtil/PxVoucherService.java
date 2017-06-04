package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxVoucher;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class PxVoucherService extends DbService<PxVoucher,Long> {
  public PxVoucherService(AbstractDao dao) {
    super(dao);
  }
}
