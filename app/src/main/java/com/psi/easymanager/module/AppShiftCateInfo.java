package com.psi.easymanager.module;

/**
 * Created by psi on 2016/7/30.
 * 分类汇总信息（用于交接班账单汇总adapter使用）
 */
public class AppShiftCateInfo {

    //分类名称
    private String mCateName;
    //分类数量
    private int mCateNumber;
    //应收金额
    private double mReceivableAmount;
    //实收金额
    private double mActualAmount;

    public String getCateName() {
        return mCateName;
    }

    public void setCateName(String cateName) {
        mCateName = cateName;
    }

    public int getCateNumber() {
        return mCateNumber;
    }

    public void setCateNumber(int cateNumber) {
        mCateNumber = cateNumber;
    }

    public double getReceivableAmount() {
        return mReceivableAmount;
    }

    public void setReceivableAmount(double receivableAmount) {
        mReceivableAmount = receivableAmount;
    }

    public double getActualAmount() {
        return mActualAmount;
    }

    public void setActualAmount(double actualAmount) {
        mActualAmount = actualAmount;
    }
}
