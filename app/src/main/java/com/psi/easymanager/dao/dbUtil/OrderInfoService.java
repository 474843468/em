package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxOrderInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/3/31.
 */
public class OrderInfoService extends DbService<PxOrderInfo, Long> {
  public OrderInfoService(AbstractDao dao) {
    super(dao);
  }
}
