package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxExtraCharge;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/5/16.
 */
public class ExtraChargeService extends DbService<PxExtraCharge,Long> {
  public ExtraChargeService(AbstractDao dao) {
    super(dao);
  }
}
