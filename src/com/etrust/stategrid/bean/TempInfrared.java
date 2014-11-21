package com.etrust.stategrid.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TempInfrared implements Serializable {

	public int id; 
	public String content;
	public String dateTime;
	//local
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

}
