package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxTableInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/3/31.
 */
public class TableInfoService extends DbService<PxTableInfo, Long> {
  public TableInfoService(AbstractDao dao) {
    super(dao);
  }
}
