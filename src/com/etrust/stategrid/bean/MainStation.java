package com.etrust.stategrid.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class MainStation implements Serializable {

	public int id;// id
	public String name;
	public String desc;// 说明描述
	public List<TransSub> transSub;// 变电站

	public String tsIds;// 本地用TransSub ids

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<TransSub> getTransSub() {
		return transSub;
	}

	public void setTransSub(List<TransSub> transSub) {
		this.transSub = transSub;
	}

	public String getTsIds() {
		return tsIds;
	}

	public void setTsIds(String tsIds) {
		this.tsIds = tsIds;
	}

}
