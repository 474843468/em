package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductMethodRef;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/5/30.
 */
public class ProductMethodRelService extends DbService<PxProductMethodRef, Long> {
  public ProductMethodRelService(AbstractDao dao) {
    super(dao);
  }
}
