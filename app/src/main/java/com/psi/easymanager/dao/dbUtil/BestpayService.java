package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxBestpay;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by psi on 2016/6/16.
 */
public class BestpayService extends DbService<PxBestpay, Long> {
    public BestpayService(AbstractDao dao) {
        super(dao);
    }
}
