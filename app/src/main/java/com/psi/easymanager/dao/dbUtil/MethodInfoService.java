package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/5/28.
 */
public class MethodInfoService extends DbService<PxMethodInfo, Long> {
  public MethodInfoService(AbstractDao dao) {
    super(dao);
  }
}
