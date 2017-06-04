package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxSetInfo;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by psi on 2016/7/29.
 */
public class SetService extends DbService<PxSetInfo, Long> {
    public SetService(AbstractDao dao) {
        super(dao);
    }
}
