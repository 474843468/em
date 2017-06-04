package com.psi.easymanager.event;

/**
 * Created by psi on 2016/5/18.
 * VipInfoRechargeAndChargeAgainstFragment发送给MemberCenterActivity
 */
public class MemberUpdateEvent {


    private int type;
    private int position;
    private String string;

    public MemberUpdateEvent(int type, int position, String string) {
        this.type = type;
        this.position = position;
        this.string = string;
    }
   public MemberUpdateEvent(int type, String string) {
        this.type = type;
        this.string = string;
    }
//
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
