package com.etrust.stategrid.bean;





public class HongwaiBiaozhun  {
	int id;
	int cateid;
	String  catename;
	HongwaiDetail content;
	public HongwaiBiaozhun(){
		
	}

	
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getCateid() {
		return cateid;
	}



	public void setCateid(int cateid) {
		this.cateid = cateid;
	}



	public String getCatename() {
		return catename;
	}



	public void setCatename(String catename) {
		this.catename = catename;
	}



	public HongwaiDetail getContent() {
		return content;
	}



	public void setContent(HongwaiDetail content) {
		this.content = content;
	}



	public String toString(){
		return  "content"+this.content;
	}
	
	

}
