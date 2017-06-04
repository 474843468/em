package com.psi.easymanager.event;


import com.psi.easymanager.module.PxOrderInfo;

/**
 * Created by psi on 2016/4/20.
 * CashMenuFragment发送给CustomProductFragment
 */
public class CashBillCustomProductInfoEvent {

    PxOrderInfo mOrderInfo;

    public PxOrderInfo getOrderInfo() {
        return mOrderInfo;
    }

    public CashBillCustomProductInfoEvent setOrderInfo(PxOrderInfo orderInfo) {
        mOrderInfo = orderInfo;
        return this;
    }
}
