package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxOrderDetails;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/7.
 */
public class ExtraDetailsService extends DbService<PxExtraDetails, Long> {
  public ExtraDetailsService(AbstractDao dao) {
    super(dao);
  }
}
