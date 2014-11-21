package com.etrust.stategrid.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ItemCategory implements Serializable {

	public int id;
	public String name;
	public List<CheckItem> checkItem;
	
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
	public List<CheckItem> getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(List<CheckItem> checkItem) {
		this.checkItem = checkItem;
	}

}
