package com.psi.easymanager.event;


import com.psi.easymanager.module.PxOrderDetails;

/**
 * Created by psi on 2016/4/28.
 * CashBillCustomPxOrderFragment发送给CustomProductFragment
 */
public class CashBillCustomEvent {

    PxOrderDetails mOrderDetails;

    public PxOrderDetails getOrderDetails() {
        return mOrderDetails;
    }

    public CashBillCustomEvent setOrderDetailsInfo(PxOrderDetails orderDetails) {
        mOrderDetails = orderDetails;
        return this;
    }
}
