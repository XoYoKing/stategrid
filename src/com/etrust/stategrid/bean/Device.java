package com.etrust.stategrid.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Device implements Serializable {

	public int id;
	public String name;
	public int type;
	public String url;
	public List<Defect> defect;
	public String transsub;
	public String path;
	public String defectIds;
	public int contentId;
	public String equipDetail;
	public String fileNames;
	public int fileState=0;
	public int sortOrder=0;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Defect> getDefect() {
		return defect;
	}

	public void setDefect(List<Defect> defect) {
		this.defect = defect;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDefectIds() {
		return defectIds;
	}

	public void setDefectIds(String defectIds) {
		this.defectIds = defectIds;
	}

	public String getEquipDetail() {
		return equipDetail;
	}

	public void setEquipDetail(String equipDetail) {
		this.equipDetail = equipDetail;
	}

	public String getFileNames() {
		return fileNames;
	}

	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

	public int getFileState() {
		return fileState;
	}

	public void setFileState(int fileState) {
		this.fileState = fileState;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	

}
