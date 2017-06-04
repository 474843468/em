package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PrintDetailsCollect;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class PdCollectService extends DbService<PrintDetailsCollect,Long> {
  public PdCollectService(AbstractDao dao) {
    super(dao);
  }
}
