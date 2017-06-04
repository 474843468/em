package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxOperationLog;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/5/28.
 */
public class OperationLogService extends DbService<PxOperationLog,Long> {
  public OperationLogService(AbstractDao dao) {
    super(dao);
  }
}
