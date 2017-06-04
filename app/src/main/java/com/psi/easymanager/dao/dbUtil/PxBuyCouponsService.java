package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.PxBuyCoupons;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class PxBuyCouponsService extends DbService<PxBuyCoupons,Long> {
  public PxBuyCouponsService(AbstractDao dao) {
    super(dao);
  }
}
