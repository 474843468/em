package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxRechargeRecord;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by psi on 2016/5/17.
 */
public class RechargeRecordService extends DbService<PxRechargeRecord, Long> {
    public RechargeRecordService(AbstractDao dao) {
        super(dao);
    }
}
