package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.BTPrintDevice;
import de.greenrobot.dao.AbstractDao;

/**
 * 作者：${ylw} on 2017-03-09 12:13
 */
public class BTDevicesService extends DbService<BTPrintDevice,Long> {
  public BTDevicesService(AbstractDao dao) {
    super(dao);
  }
}
