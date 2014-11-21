package com.etrust.stategrid.bean;

public class Coordinate {
	   public int id;
	   public String datetime;
	   public String userid;
	   public String latitude;
	   public String longitude;
	   public String tsid;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getTsid() {
		return tsid;
	}
	public void setTsid(String tsid) {
		this.tsid = tsid;
	}
	   
}
