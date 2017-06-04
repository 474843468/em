package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxDiscounScheme;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/18.
 */
public class DiscounSchemeService extends DbService<PxDiscounScheme, Long> {
  public DiscounSchemeService(AbstractDao dao) {
    super(dao);
  }
}
