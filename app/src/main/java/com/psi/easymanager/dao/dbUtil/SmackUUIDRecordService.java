package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.SmackUUIDRecord;
import de.greenrobot.dao.AbstractDao;

/**
 * User: ylw
 * Date: 2016-12-28
 * Time: 18:31
 * Smack UUID 记录
 */
public class SmackUUIDRecordService extends DbService<SmackUUIDRecord, Long> {
  public SmackUUIDRecordService(AbstractDao dao) {
    super(dao);
  }
}