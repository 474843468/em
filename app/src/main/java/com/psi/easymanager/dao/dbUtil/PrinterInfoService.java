package com.psi.easymanager.dao.dbUtil;

import com.psi.easymanager.module.PxPrinterInfo;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by psi on 2016/5/4.
 */
public class PrinterInfoService extends DbService<PxPrinterInfo, Long> {
    public PrinterInfoService(AbstractDao dao) {
        super(dao);
    }
}
