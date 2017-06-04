package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.Office;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class EPaymentInfoService extends DbService<EPaymentInfo,Long> {
  public EPaymentInfoService(AbstractDao dao) {
    super(dao);
  }
}
