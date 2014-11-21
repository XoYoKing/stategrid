package com.etrust.stategrid.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Defect implements Serializable {

	public int id;//id
	public String itemCategory;//缺陷大类
	public String item;//缺陷项目
	public String bugLevel;//缺陷等级 1一般缺陷2重要缺陷3紧急缺陷
	public String dealType;//处理方法 1自行处理 2上报处理
	public String description;//描述
	public String creatorid;//创建人
	public String executorid;// 陪同人
	public String createDate;//创建日期
	public List<Attach> attach;//附件
	public int cateid;
	public int itemid;
	
	//LOCAL
	public String attachIds;
	public String device;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItemCategory() {
		return itemCategory;
	}
	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getBugLevel() {
		return bugLevel;
	}
	public void setBugLevel(String bugLevel) {
		this.bugLevel = bugLevel;
	}
	public String getDealType() {
		return dealType;
	}
	public void setDealType(String dealType) {
		this.dealType = dealType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreatorid() {
		return creatorid;
	}
	public void setCreatorid(String creatorid) {
		this.creatorid = creatorid;
	}
	public String getExecutorid() {
		return executorid;
	}
	public void setExecutorid(String executorid) {
		this.executorid = executorid;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public List<Attach> getAttach() {
		return attach;
	}
	public void setAttach(List<Attach> attach) {
		this.attach = attach;
	}
	public int getCateid() {
		return cateid;
	}
	public void setCateid(int cateid) {
		this.cateid = cateid;
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	
	
}
