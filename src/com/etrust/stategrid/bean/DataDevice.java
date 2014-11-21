package com.etrust.stategrid.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
/**
 * 
 * @author yi
 * @return
 * @param
 *
 */
public class DataDevice implements Serializable{
	
	public List<MainStation> DataDevice;//运维站
	public List<Infrared> Infrared;//红外
	
	public List<MainStation> getDataDevice() {
		return DataDevice;
	}
	public void setDataDevice(List<MainStation> dataDevice) {
		DataDevice = dataDevice;
	}
	public List<Infrared> getInfrared() {
		return Infrared;
	}
	public void setInfrared(List<Infrared> infrared) {
		Infrared = infrared;
	}
}
