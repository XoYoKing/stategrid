package com.etrust.stategrid.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Attach implements Serializable {
//下载文件如图片录音 ，视频
	public String url;
	public String realName;
	public String suffux;
	public String fileid;
	
	//local
	public String defect;
	public String path;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getSuffux() {
		return suffux;
	}
	public void setSuffux(String suffux) {
		this.suffux = suffux;
	}
	public String getFileid() {
		return fileid;
	}
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}
	
	
}
