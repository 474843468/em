package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.User;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/4/6.
 */
public class UserService extends DbService<User, Long> {
  public UserService(AbstractDao dao) {
    super(dao);
  }
}
