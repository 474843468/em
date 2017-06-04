package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxComboProductRel;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/8/17.
 */
public class ComboProdRelService extends DbService<PxComboProductRel,Long> {
  public ComboProdRelService(AbstractDao dao) {
    super(dao);
  }
}
