package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxPromotioDetails;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/5/18.
 */
public class PxPromotionDetailsService extends DbService<PxPromotioDetails,Long> {
  public PxPromotionDetailsService(AbstractDao dao) {
    super(dao);
  }
}
