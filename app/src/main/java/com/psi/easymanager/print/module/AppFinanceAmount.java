package com.psi.easymanager.print.module;

import java.io.Serializable;

/**
 * Created by psi on 2016/6/2.
 * App财务联打印总价,应收等等
 */
public class AppFinanceAmount implements Serializable{

    //总金额
    private double mPayCashTotal = 0;
    //应收金额
    private double mPayCashReceivable = 0;
    //实收金额
    private double mPayCashActual = 0;
    //找零金额
    private double mPayCashChange = 0;

    public AppFinanceAmount() {
    }

    public double getmPayCashTotal() {
        return mPayCashTotal;
    }

    public void setmPayCashTotal(double mPayCashTotal) {
        this.mPayCashTotal = mPayCashTotal;
    }

    public double getmPayCashReceivable() {
        return mPayCashReceivable;
    }

    public void setmPayCashReceivable(double mPayCashReceivable) {
        this.mPayCashReceivable = mPayCashReceivable;
    }

    public double getmPayCashActual() {
        return mPayCashActual;
    }

    public void setmPayCashActual(double mPayCashActual) {
        this.mPayCashActual = mPayCashActual;
    }

    public double getmPayCashChange() {
        return mPayCashChange;
    }

    public void setmPayCashChange(double mPayCashChange) {
        this.mPayCashChange = mPayCashChange;
    }
}
