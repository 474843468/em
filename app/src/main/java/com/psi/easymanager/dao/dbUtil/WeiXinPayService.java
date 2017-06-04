package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxWeiXinpay;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by psi on 2016/8/12.
 */
public class WeiXinPayService extends DbService<PxWeiXinpay, Long> {
  public WeiXinPayService(AbstractDao dao) {
    super(dao);
  }
}
