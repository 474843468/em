package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * Created by Administrator on 2016-12-13.
 */
public class SysExceptionLogReq extends HttpReq {
    public static final String EASYMANAGER_TYPE = "0";
    private static final long serialVersionUID = 7544177353464176307L;
    @Expose
    private String msg;        //异常信息
    @Expose
    private int androidVersion;//Android系统版本
    @Expose
    private int appVersion;//App版本号
    @Expose
    private String code;        // 店铺编码
    @Expose
    private String crashTime;//崩溃时间

    @Expose
    private String deviceName;//设备名称
    @Expose
    private String name;        // 店铺名称
    @Expose
    private String type;        // 类型 （0：收银机 1：服务生 2：IOS服务生）

    public String getCrashTime() {
        return crashTime;
    }

    public void setCrashTime(String crashTime) {
        this.crashTime = crashTime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(int androidVersion) {
        this.androidVersion = androidVersion;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SysExceptionLogReq{" +
                "msg='" + msg + '\'' +
                ", androidVersion=" + androidVersion +
                ", appVersion=" + appVersion +
                ", code='" + code + '\'' +
                ", crashTime='" + crashTime + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
