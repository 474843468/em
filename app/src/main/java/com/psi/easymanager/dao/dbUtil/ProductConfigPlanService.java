package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxProductConfigPlan;
import de.greenrobot.dao.AbstractDao;

/**
 * User: ylw
 * Date: 2016-06-03
 * Time: 16:30
 * FIXME
 */
public class ProductConfigPlanService extends DbService<PxProductConfigPlan, Long> {
  public ProductConfigPlanService(AbstractDao dao) {
    super(dao);
  }
}