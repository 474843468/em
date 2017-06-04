package com.psi.easymanager.event;


import com.psi.easymanager.module.PxOrderInfo;

/**
 * Created by psi on 2016/4/18.
 * CashMenuFragment发送给CashMenuFuzzyQueryFragment
 */
public class CashBillOrderInfoEvent {
    PxOrderInfo mOrderInfo;

    public PxOrderInfo getOrderInfo() {
        return mOrderInfo;
    }

    public CashBillOrderInfoEvent setOrderInfo(PxOrderInfo orderInfo) {
        mOrderInfo = orderInfo;
        return this;
    }
}
