package com.etrust.stategrid.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Orbit implements Serializable {

	public double latitude;// 维度
	public double longitude;// 经度
	public String date;// 时间 yyyy-MM-dd HH:mm:ss
	public int id;// Orbit ID
	public int msid;// 运维站ID
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMsid() {
		return msid;
	}

	public void setMsid(int msid) {
		this.msid = msid;
	}

}
