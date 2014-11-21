package com.etrust.stategrid.bean;

public class PlanEquip implements java.io.Serializable{
	private int id;
	private int tsid;
	private int taskid;
	private int equipid;
	private String equipName;
	private String descrip="";
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTsid() {
		return tsid;
	}
	public void setTsid(int tsid) {
		this.tsid = tsid;
	}
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public int getEquipid() {
		return equipid;
	}
	public void setEquipid(int equipid) {
		this.equipid = equipid;
	}
	public String getEquipName() {
		return equipName;
	}
	public void setEquipName(String equipName) {
		this.equipName = equipName;
	}
	public String getDescrip() {
		return descrip;
	}
	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}
	
	
}
