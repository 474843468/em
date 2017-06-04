package com.psi.easymanager.utils;

import com.psi.easymanager.common.App;
import com.psi.easymanager.module.User;

/**
 * 作者：${ylw} on 2017-03-10 13:58
 */
public class UserUtils {
  public static User getLoginUser() {
    App app = (App) App.getContext();
    if (app == null) return null;
    return app.getUser();
  }
}
