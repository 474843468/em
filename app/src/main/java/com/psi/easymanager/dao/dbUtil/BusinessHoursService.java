package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxBusinessHours;
import com.psi.easymanager.module.PxSetInfo;
import de.greenrobot.dao.AbstractDao;

/**
 * Created by zjq on 2016/7/29.
 */
public class BusinessHoursService extends DbService<PxBusinessHours, Long> {
    public BusinessHoursService(AbstractDao dao) {
        super(dao);
    }
}
