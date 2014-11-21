package com.etrust.stategrid.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class TaskBean implements Serializable{
	public int id;//任务id
	public String name;//任务名称
	public String resid;//负责人id
	public String resname;//负责人
	public String accid;//陪同人id
	public String accname;//陪同人
	public int range;//
	public int status;//0未开始1进行中2已终止3已结束4 已暂停
	public int groupid;//班组id
	public String begintime;//开始日期
	public String enddate;//结束日期
	public String instruction;//任务说明
	public int type;//巡检类型  0正常巡视1标准巡视2特殊巡视
	public List<Equipment> equipment;//变电站列表
	public List<Orbit> orbitList;//默认巡检轨迹-新增
	public List<PlanEquip> equipList;
	//equipList
	private static String[] TYPE_STR = new String []{
		"正常巡视","标准巡视","特殊巡视"
	};
	private static String[] STATUS_STR = new String []{
		"未开始","进行中","已暂停","已结束","已终止"
	};
	public String getStatusStr(){
		return STATUS_STR[status];
	}
	public String getTypeStr(){
		return TYPE_STR[type];
	}
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
	public String getResid() {
		return resid;
	}
	public void setResid(String resid) {
		this.resid = resid;
	}
	public String getResname() {
		return resname;
	}
	public void setResname(String resname) {
		this.resname = resname;
	}
	public String getAccid() {
		return accid;
	}
	public void setAccid(String accid) {
		this.accid = accid;
	}
	public String getAccname() {
		return accname;
	}
	public void setAccname(String accname) {
		this.accname = accname;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public String getBegintime() {
		return begintime;
	}
	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}
	public String getEndtime() {
		return enddate;
	}
	public void setEndtime(String enddate) {
		this.enddate = enddate;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<Equipment> getEquipment() {
		return equipment;
	}
	public void setEquipment(List<Equipment> equipment) {
		this.equipment = equipment;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public List<Orbit> getOrbitList() {
		return orbitList;
	}
	public void setOrbitList(List<Orbit> orbitList) {
		this.orbitList = orbitList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskBean other = (TaskBean) obj;
		if (id == other.id)
			return true;
		return false;
	}
	public List<PlanEquip> getEquipList() {
		return equipList;
	}
	public void setEquipList(List<PlanEquip> equipList) {
		this.equipList = equipList;
	}
	
}
