package com.etrust.stategrid.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CheckItem implements Serializable {

	public int id;
	public String name;
	public String type;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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

}
