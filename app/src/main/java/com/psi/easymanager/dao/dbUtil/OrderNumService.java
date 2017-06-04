package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxOrderNum;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/3/31.
 */
public class OrderNumService extends DbService<PxOrderNum, Long> {
  public OrderNumService(AbstractDao dao) {
    super(dao);
  }
}
