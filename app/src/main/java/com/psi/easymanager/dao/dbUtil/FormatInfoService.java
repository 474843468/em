package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxFormatInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by dorado on 2016/5/28.
 */
public class FormatInfoService extends DbService<PxFormatInfo,Long> {
  public FormatInfoService(AbstractDao dao) {
    super(dao);
  }
}
