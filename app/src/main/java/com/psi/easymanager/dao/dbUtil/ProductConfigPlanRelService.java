package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxProductConfigPlanRel;
import de.greenrobot.dao.AbstractDao;

/**
 * User: ylw
 * Date: 2016-06-04
 * Time: 12:35
 * FIXME
 */
public class ProductConfigPlanRelService extends DbService<PxProductConfigPlanRel, Long> {
  public ProductConfigPlanRelService(AbstractDao dao) {
    super(dao);
  }
}