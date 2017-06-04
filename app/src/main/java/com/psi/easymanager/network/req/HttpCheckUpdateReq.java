package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

public class HttpCheckUpdateReq extends HttpReq {


	@Expose
	private String  type;
	@Expose
	private String  versionName;//版本名
	@Expose
	private Integer versionCode;//版本号

	public String getVersionName() {
		return versionName;
	}
	
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	public Integer getVersionCode() {
		return versionCode;
	}
	
	public void setVersionCode(Integer versionCode) {
		this.versionCode = versionCode;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
