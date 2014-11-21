package com.etrust.stategrid.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class TransSub implements Serializable {
	
	public int id;//id
	public String name;
	public String latitude;
	public String longitude;
	public List<Device> device;
	  public String voltage;
	  public String dianLiu;
      public String getVoltage() {
		return voltage;
	}
	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}
	public String getDianLiu() {
		return dianLiu;
	}
	public void setDianLiu(String dianLiu) {
		this.dianLiu = dianLiu;
	}
	
	
	//local
	public String manstation;
	public String deviceIds;
	
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
	public List<Device> getDevice() {
		return device;
	}
	public void setDevice(List<Device> device) {
		this.device = device;
	}
}
