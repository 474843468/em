package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxRechargePlan;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by psi on 2016/5/20.
 */
public class RechargePlanService extends DbService<PxRechargePlan, Long> {
  public RechargePlanService(AbstractDao dao) {
    super(dao);
  }


}
