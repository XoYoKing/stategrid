package com.etrust.stategrid.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Equipment implements Serializable{
	
	public int tsid;//变电站id
	public String tsname;//变电站名称
	public double latitude;//维度
	public double longitude;//经度
	public String msid;//所属运维站ID-新增
	public String ms_name;//所属运维站-新增
	public List<RecordOrbit> recordList;//巡检历史-新增
	
	public int status=0;//0未到点1正在点上2到过该点
	
	public int getTsid() {
		return tsid;
	}

	public void setTsid(int tsid) {
		this.tsid = tsid;
	}

	public String getTsname() {
		return tsname;
	}

	public void setTsname(String tsname) {
		this.tsname = tsname;
	}

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

	public String getMsid() {
		return msid;
	}

	public void setMsid(String msid) {
		this.msid = msid;
	}

	public String getMs_name() {
		return ms_name;
	}

	public void setMs_name(String ms_name) {
		this.ms_name = ms_name;
	}
	
	public List<RecordOrbit> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<RecordOrbit> recordList) {
		this.recordList = recordList;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;

		if (getClass() != o.getClass())
			return false;
		
		if(((Equipment)o).tsid == this.tsid){
			return true;
		}else{
			return false;
		}
	}


}
