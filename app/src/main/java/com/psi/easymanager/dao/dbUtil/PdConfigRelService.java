package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PdConfigRel;
import com.psi.easymanager.module.PrintDetailsCollect;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class PdConfigRelService extends DbService<PdConfigRel,Long> {
  public PdConfigRelService(AbstractDao dao) {
    super(dao);
  }
}
