package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.TableOrderRel;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class TableOrderRelService extends DbService<TableOrderRel,Long> {
  public TableOrderRelService(AbstractDao dao) {
    super(dao);
  }
}
