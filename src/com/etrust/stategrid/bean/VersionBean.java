package com.etrust.stategrid.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VersionBean implements Serializable {

	public boolean result;
	public String url;
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
