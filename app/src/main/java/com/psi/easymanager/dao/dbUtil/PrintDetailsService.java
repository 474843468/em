package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PrintDetails;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class PrintDetailsService extends DbService<PrintDetails,Long> {
  public PrintDetailsService(AbstractDao dao) {
    super(dao);
  }
}
