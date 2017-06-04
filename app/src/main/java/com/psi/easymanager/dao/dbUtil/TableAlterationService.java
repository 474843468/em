package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.module.PxTableInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/3/31.
 */
public class TableAlterationService extends DbService<PxTableAlteration, Long> {
  public TableAlterationService(AbstractDao dao) {
    super(dao);
  }
}
