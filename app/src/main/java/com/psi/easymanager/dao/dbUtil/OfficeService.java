package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.Office;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class OfficeService extends DbService<Office,Long> {
  public OfficeService(AbstractDao dao) {
    super(dao);
  }
}
