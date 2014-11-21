package com.etrust.stategrid.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.text.StaticLayout;

import com.etrust.stategrid.bean.CacheBean;
import com.etrust.stategrid.bean.DataDevice;
import com.etrust.stategrid.bean.Defect;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.bean.EquipContent;
import com.etrust.stategrid.bean.Equipment;
import com.etrust.stategrid.bean.HongwaiBiaozhun;
import com.etrust.stategrid.bean.HongwaiDetail;
import com.etrust.stategrid.bean.Infrared;
import com.etrust.stategrid.bean.ItemCategory;
import com.etrust.stategrid.bean.MainStation;
import com.etrust.stategrid.bean.Orbit;
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.bean.VersionBean;
import com.etrust.stategrid.bean.ZiLiao;
import com.google.gson.Gson;

public class JsonUtils {
	static Gson gson = new Gson();
	

	
	
	public static ItemCategory getIitemCategory(String json) {
		return gson.fromJson(getRightJsonOBJ(json), ItemCategory.class);
	}
	public static TaskBean getTaskBeanFromJson(String json) {
		return gson.fromJson(getRightJsonOBJ(json), TaskBean.class);
	}
	public static VersionBean getVersionBeanFromJson(String json) {
		return gson.fromJson(getRightJsonOBJ(json), VersionBean.class);
	}
	//hongwai
//	public static HongwaiBiaozhun getHongwaiBiaozhunFromJson(String json) {
//		return gson.fromJson(getRightJsonOBJ(json), HongwaiBiaozhun.class);
//		
//	}
	public static List<TaskBean> getTaskBeanListFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<TaskBean>>() {
		}.getType();
		List<TaskBean> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}
	//hongwai
//	public  static List<HongwaiBiaozhun> getHongwaiBiaozhun(String json) {
//		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<HongwaiBiaozhun>>() {
//		}.getType();
//		List<HongwaiBiaozhun>res=gson.fromJson(getRightJsonArr(json), type);
//		return res;
//		
//	}

	public static List<Equipment> getEquipmentListFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Equipment>>() {
		}.getType();
		List<Equipment> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}

	public static List<Orbit> getOrbitListFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Orbit>>() {
		}.getType();
		List<Orbit> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}

	public static List<MainStation> getMainStationListFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<MainStation>>() {
		}.getType();
		List<MainStation> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}

	public static List<Device> getDeviceListFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Device>>() {
		}.getType();
		List<Device> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}
	public static List<Defect> getDefectListFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Defect>>() {
		}.getType();
		List<Defect> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}
	public static List<Infrared> getInfraredListFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Infrared>>() {
		}.getType();
		List<Infrared> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}
	public static List<ItemCategory> getIitemCategoryFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<ItemCategory>>() {
		}.getType();
		List<ItemCategory> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}
	public static List<EquipContent> getEquipContentFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<EquipContent>>() {
		}.getType();
		List<EquipContent> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}	
	public static List<HongwaiBiaozhun> getHongwaiBiaozhunFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<HongwaiBiaozhun>>() {
		}.getType();
		List<HongwaiBiaozhun> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}	
	
	public static List<UserBean> getUserListFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<UserBean>>() {
		}.getType();
		List<UserBean> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}
	public static DataDevice getDataDeviceFromJson(String json) {
		return gson.fromJson(getRightJsonOBJ(json), DataDevice.class);
	}
	public static UserBean getUserBeanFromJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(getRightJsonOBJ(json));
			boolean isLogin = jsonObject.getBoolean("islogin");
			if (isLogin) {
				return gson.fromJson(getRightJsonOBJ(json), UserBean.class);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static List<CacheBean> getCacheFromJson(String json) {
		if(json==null||"".equals(json)){
			return (new ArrayList<CacheBean>());
		}
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<CacheBean>>() {
		}.getType();
		List<CacheBean> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}
	public static String getRightJsonOBJ(String json) {
		int posion = json.indexOf("{");
		if (posion != -1) {
			return json.substring(posion);
		}
		return "{}";
	}

	public static String getRightJsonArr(String json) {
		if(json==null){
			return "[]";
		}
		int posion = json.indexOf("[");
		if (posion != -1) {
			return json.substring(posion);
		}
		return "[]";
	}
	public static List<ZiLiao> getZiLiaoFromJson(String json) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<ZiLiao>>() {
		}.getType();
		List<ZiLiao> res = gson.fromJson(getRightJsonArr(json), type);
		return res;
	}
}
