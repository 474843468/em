package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxOptReason;
import com.psi.easymanager.module.PxOrderInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/3/31.
 */
public class OptReasonService extends DbService<PxOptReason, Long> {
  public OptReasonService(AbstractDao dao) {
    super(dao);
  }
}
