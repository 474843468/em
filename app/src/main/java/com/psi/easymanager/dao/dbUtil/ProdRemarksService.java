package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxProductRemarks;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class ProdRemarksService extends DbService<PxProductRemarks,Long> {
  public ProdRemarksService(AbstractDao dao) {
    super(dao);
  }
}
