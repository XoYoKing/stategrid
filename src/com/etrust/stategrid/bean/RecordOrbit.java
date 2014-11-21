package com.etrust.stategrid.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RecordOrbit implements Serializable {

	public String date;// 时间
	public String isarrive;//"已到位/未到位",
	public String iswell;//"是/否"

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getIsarrive() {
		return isarrive;
	}
	public void setIsarrive(String isarrive) {
		this.isarrive = isarrive;
	}
	public String getIswell() {
		return iswell;
	}
	public void setIswell(String iswell) {
		this.iswell = iswell;
	}
}
