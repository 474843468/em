package com.psi.easymanager.event;


import com.psi.easymanager.module.PxPrinterInfo;

import java.util.List;

/**
 * Created by psi on 2016/5/11.
 * PrinterService发送 KitchenPrintFragment接受
 */
public class PrinterInfoEvent {

    List<PxPrinterInfo> pxPrinterInfoList;

    public PrinterInfoEvent(List<PxPrinterInfo> pxPrinterInfoList) {
        this.pxPrinterInfoList = pxPrinterInfoList;
    }

    public List<PxPrinterInfo> getPxPrinterInfoList() {
        return pxPrinterInfoList;
    }

    public void setPxPrinterInfoList(List<PxPrinterInfo> pxPrinterInfoList) {
        this.pxPrinterInfoList = pxPrinterInfoList;
    }
}
