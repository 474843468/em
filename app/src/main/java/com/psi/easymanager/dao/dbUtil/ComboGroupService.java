package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxComboGroup;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/8/17.
 */
public class ComboGroupService extends DbService<PxComboGroup,Long> {
  public ComboGroupService(AbstractDao dao) {
    super(dao);
  }
}
