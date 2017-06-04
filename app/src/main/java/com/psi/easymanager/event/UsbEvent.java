package com.psi.easymanager.event;

/**
 * Created by psi on 2016/5/28.
 * OrdinaryPrinterFragment发送 其他activity或Fragment接收
 */
public class UsbEvent {

    private int printerID;

    public UsbEvent(int printerID) {
        this.printerID = printerID;
    }

    public int getPrinterID() {
        return printerID;
    }

    public void setPrinterID(int printerID) {
        this.printerID = printerID;
    }
}
