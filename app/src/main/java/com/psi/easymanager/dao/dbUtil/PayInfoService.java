package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxPayInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class PayInfoService extends DbService<PxPayInfo,Long> {
  public PayInfoService(AbstractDao dao) {
    super(dao);
  }
}
