package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxProductCategory;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/3/31.
 */
public class ProductCategoryService extends DbService<PxProductCategory, Long> {
  public ProductCategoryService(AbstractDao dao) {
    super(dao);
  }
}
