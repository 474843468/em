package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxProductFormatRel;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/5/30.
 */
public class ProductFormatRelService extends DbService<PxProductFormatRel,Long> {
  public ProductFormatRelService(AbstractDao dao) {
    super(dao);
  }
}
