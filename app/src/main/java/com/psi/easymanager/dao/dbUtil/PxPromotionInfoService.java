package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxPromotioInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/5/18.
 */
public class PxPromotionInfoService extends DbService<PxPromotioInfo,Long> {
  public PxPromotionInfoService(AbstractDao dao) {
    super(dao);
  }
}
