package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxOrderDetails;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/7.
 */
public class OrderDetailsService extends DbService<PxOrderDetails, Long> {
  public OrderDetailsService(AbstractDao dao) {
    super(dao);
  }
}
