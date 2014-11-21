package com.etrust.stategrid.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserBean implements Serializable{
	
	public String userid;
	public String username;
	public String password;
	public String Deptname;
	public String task;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {  
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDeptname() {
		return Deptname;
	}
	public void setDeptname(String deptname) {
		Deptname = deptname;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
}
