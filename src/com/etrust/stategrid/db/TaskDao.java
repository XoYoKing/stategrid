package com.etrust.stategrid.db;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.util.SparseIntArray;

import java.util.HashMap;

import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.JsonUtils;
public class TaskDao {
	//查询本地数据数据库数据
		public  static List<TaskBean> getAllTaskBean(String userid,DatabasesTransaction db){
	        List<TaskBean> dataList=new ArrayList<TaskBean>();
	        String querySql = "SELECT * FROM " + Constant.T_Task
			+ " WHERE resid='"+userid+"' order by datetime desc";
			Cursor cursor = db.selectSql(querySql);
			while (cursor.moveToNext()) {
				String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
				TaskBean taskBean=JsonUtils.getTaskBeanFromJson(content);
			    TaskBean tb=new TaskBean();
			    tb.setId(taskBean.getId());
			    tb.setStatus(taskBean.getStatus());
			    tb.setName(taskBean.getName());
			    tb.setBegintime(taskBean.getBegintime());
			    tb.setEnddate(taskBean.getEnddate());
				dataList.add(tb);
				taskBean=null;
			}
			cursor.close();
			System.out.println("dataList============="+dataList.size());
			return dataList;
		}
		public static TaskBean getTaskById(int taskId,DatabasesTransaction db) {
	        TaskBean taskBean=new TaskBean();
	        String querySql = "SELECT * FROM " + Constant.T_Task+ " WHERE id="+taskId;
			Cursor cursor = db.selectSql(querySql);
			while (cursor.moveToNext()) {
				String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
			    taskBean=JsonUtils.getTaskBeanFromJson(content);
			}
			cursor.close();
	        return taskBean;
	    }	
		
		public static String[] getPlanEquipById(int id,DatabasesTransaction db){
			String[] data=new String[2];
			String querySql = "SELECT * FROM " + Constant.T_PlanEquip+ " WHERE id="+id;
			Cursor cursor = db.selectSql(querySql);
			while (cursor.moveToNext()) {
				data[0]=cursor.getString(1);
				data[1]=cursor.getString(2);
			}
			return data;
		}
		
		public static HashMap<Integer,Integer> getAllDeviceId(DatabasesTransaction db){
			HashMap<Integer,Integer> devIdMap=new HashMap<Integer,Integer>();
			String querySql = "SELECT id FROM " + Constant.T_Device;
			Cursor cursor = db.selectSql(querySql);
			while(cursor.moveToNext()) {
				int id=cursor.getInt(0);
				devIdMap.put(id,id);
			}
			return devIdMap;
		}
		
		public static HashMap<Integer,Integer> getEquipContentId(DatabasesTransaction db){
			HashMap<Integer,Integer> econtentIdMap=new HashMap<Integer,Integer>();
			String querySql = "SELECT id FROM " + Constant.T_equip_Content;
			Cursor cursor = db.selectSql(querySql);
			while(cursor.moveToNext()) {
				int id=cursor.getInt(0);
				econtentIdMap.put(id,id);
			}
			return econtentIdMap;
		}
		
		public static HashMap<Integer, Integer> getAllDefectId(DatabasesTransaction db){
			HashMap<Integer, Integer> defectIdMap=new HashMap<Integer, Integer>();
			//HashMap<Integer,Integer> defectIdMap=new HashMap<Integer,Integer>();
			String querySql = "SELECT id FROM " + Constant.T_Defect;
			Cursor cursor = db.selectSql(querySql);
			while(cursor.moveToNext()) {
				int id=cursor.getInt(0);
				defectIdMap.put(id,id);
			}
			return defectIdMap; 
		}
		
		public static HashMap<String,String> getAllAttachId(DatabasesTransaction db){
			HashMap<String,String> attachIdMap=new HashMap<String,String>();
			String querySql = "SELECT id FROM " + Constant.T_Attach;
			Cursor cursor = db.selectSql(querySql);
			while(cursor.moveToNext()) {
				String id=cursor.getString(0);
				attachIdMap.put(id,id);
			}
			return attachIdMap;
		}	
	public static HashMap<Integer,Integer> getZiLiaoId(DatabasesTransaction db){
			
			HashMap<Integer,Integer> ziLiaoIdMap=new HashMap<Integer,Integer>();
			String querySql = "SELECT id FROM " + Constant.T_ziliao;
			Cursor cursor = db.selectSql(querySql);
			while(cursor.moveToNext()) {
				int id=cursor.getInt(0);
				ziLiaoIdMap.put(id,id);
			}
			cursor.close();
			return ziLiaoIdMap;
		}
}
