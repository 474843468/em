package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxVipInfo;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by psi on 2016/5/17.
 */
public class VipInfoService extends DbService<PxVipInfo, Long> {
    public VipInfoService(AbstractDao dao) {
        super(dao);
    }
}
