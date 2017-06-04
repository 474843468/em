package com.psi.easymanager.event;

/**
 * Created by psi on 2016/5/23.
 * MemberFuzzyQueryFragment发送 MemberFragment接收
 */
public class VipInfoListEvent {

    private String likeName;

    public VipInfoListEvent(String likeName) {
        this.likeName = likeName;
    }

    public String getLikeName() {
        return likeName;
    }

    public void setLikeName(String likeName) {
        this.likeName = likeName;
    }
}
