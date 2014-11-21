package com.etrust.stategrid.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CacheBean implements Serializable {

	public String[] cache;
	public int towerid;
	public String userid;
	public int deviceId;
	public String createTime;
	public String[] getCache() {
		return cache;
	}
	public void setCache(String[] cache) {
		this.cache = cache;
	}
	public int getTowerid() {
		return towerid;
	}
	public void setTowerid(int towerid) {
		this.towerid = towerid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
