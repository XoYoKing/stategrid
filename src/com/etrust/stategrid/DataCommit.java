package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.etrust.stategrid.bean.Coordinate;
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.CallService;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DeviceService;
import com.etrust.stategrid.utils.InfraredService;
import com.etrust.stategrid.utils.JsonUtils;
import com.etrust.stategrid.utils.ProduceDefectService;
import com.google.gson.Gson;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.app.Activity;

//数据上传
public class DataCommit extends Activity {
	private DatabasesTransaction db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabasesTransaction.getInstance(this);
		beginCommit();

	}
	public void beginCommit() {
			doUploadInfrared();//上传红外
			doUploadCacheMl();//上传缺陷 （包含设备的缺陷）
			doUploadTask();//上传任务（包含轨迹坐标任务状态）
			doUploadCoordinate();//上传更新变电站坐标
			doUploadDevice();//上传具体设备信息（设备的照片什么的）
			doUploadProduceDefect();//生产设施
			doUploadPlanEquip();//重点巡视设备上传
			douploadNoTourReason();//未巡视原因描述
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				
			}
			db.deleteData(Constant.T_update_log,null,null);//删除操作日志
			Toast.makeText(this, "数据提交完成！",Toast.LENGTH_LONG ).show();
			finish();
	}
	private void douploadNoTourReason() {
		// TODO Auto-generated method stub
		JSONArray list=new JSONArray();
		String querySql="SELECT * FROM " + Constant.T_NOTOUR_REASON+" order by id desc";
		Cursor cursor=db.selectSql(querySql);
		while (cursor.moveToNext()) {
			 String taskid=cursor.getString(cursor.getColumnIndexOrThrow("taskid"));
			 String stationname=cursor.getString(cursor.getColumnIndexOrThrow("stationname"));
			 String reason=cursor.getString(cursor.getColumnIndexOrThrow("reason"));
			 try{
				 JSONObject json=new JSONObject();
				 json.put("taskid", taskid);
				 json.put("tsid", stationname);
				 json.put("description", reason);
				 list.put(json);
			 }catch(Exception e){
				 
			 }
		}
		cursor.close();
		if(list.length()==0){
			return;
		}
		HashMap<String,Object> params=new LinkedHashMap<String,Object>();
	    params.put("plantsList",list.toString());
	    CallService.getData(this, reasonHanlder, "updatePlanTranSub", params, false);
		
	}
	public void doUploadPlanEquip(){
		JSONArray list=new JSONArray();
		String querySql = "SELECT * FROM " + Constant.T_PlanEquip+" order by datetime desc";
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			 String id=cursor.getString(cursor.getColumnIndexOrThrow("id"));
			 String content=cursor.getString(cursor.getColumnIndexOrThrow("content"));
			 String datetime=cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
			 try{
				 JSONObject json=new JSONObject();
				 json.put("content", content);
				 json.put("id", id);
				 json.put("datetime", datetime);
				 list.put(json);
			 }catch(Exception e){
				 
			 }
		}
		cursor.close();
		if(list.length()==0){
			return;
		}
		HashMap<String,Object> params=new LinkedHashMap<String,Object>();
	    params.put("planEquipList",list.toString());
	    CallService.getData(this, planEquipHanlder, "updatePlanEquip", params, false);
	}	
	//变电站坐标
	public void doUploadCoordinate(){
		String querySql = "SELECT * FROM " + Constant.T_Coordinate+" order by datetime desc";
		List<Coordinate> coordList=new ArrayList<Coordinate>();
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			 String userid=cursor.getString(cursor.getColumnIndexOrThrow("userid"));
			 String latitude=cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
			 String longitude=cursor.getString(cursor.getColumnIndexOrThrow("longitude"));
			 String tsid=cursor.getString(cursor.getColumnIndexOrThrow("tsid"));
			 String datetime=cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
			 
			 Coordinate coord=new Coordinate();
			 coord.setUserid(userid);
			 coord.setLatitude(latitude);
			 coord.setLongitude(longitude);
			 coord.setTsid(tsid);
			 coord.setDatetime(datetime);
			 coordList.add(coord);
		}
		cursor.close();
		HashMap<String,Object> params=new LinkedHashMap<String,Object>();
	    params.put("coordinateList", new Gson().toJson(coordList));
	    CallService.getData(this, coordinateHanlder, "addCoordinate", params, false);
	    //更新坐标
	}
	

	
	public void doUploadTask(){
		String querySql = "SELECT * FROM " + Constant.T_Task
				+ " WHERE uploadstatus="+Constant.Upload_Task_Status_Change+" or uploadstatus=100";
		List<TaskBean> tlist = new ArrayList<TaskBean>();
		
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
			System.out.println("content==============="+content);
			tlist.add(JsonUtils.getTaskBeanFromJson(content));
		}
		cursor.close();
		if(tlist.size()==0){
			return;
		}
		HashMap<String,Object> params=new LinkedHashMap<String,Object>();
	    params.put("taskList", new Gson().toJson(tlist));
		CallService.getData(this, taskServiceHanlder, "updateTaskList", params, false);
		
	}
	
	
	//上传红外报告
	public void doUploadInfrared(){
		String querySql = "SELECT * FROM " + Constant.T_temp_Infrared+" order by datetime desc";
		Cursor cursor = db.selectSql(querySql);
		JSONArray jsons=new JSONArray();
		try{
			while (cursor.moveToNext()) {
				String id=cursor.getString(cursor.getColumnIndexOrThrow("id"));
				String content=cursor.getString(cursor.getColumnIndexOrThrow("content"));
				JSONObject json=new JSONObject(content);
				json.put("infraredId", id);
				jsons.put(json);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		cursor.close();
		boolean has=db.checkExists(Constant.T_temp_Infrared, "");
		if(has==false){
			return;
		}
		final InfraredService infraredService=new InfraredService(db);
		try{
			for(int i=0;i<jsons.length();i++){
	            final JSONObject json=jsons.getJSONObject(i);
				final InfraredService.InfraredHandler inHandler=new InfraredService(db).new InfraredHandler(DataCommit.this,json,1);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try{
							infraredService.doUploadInfrared(DataCommit.this, json, 1, inHandler);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}).start();
				Thread.sleep(1000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void doUploadDevice(){
		String querySql = "SELECT id,name,filename FROM " + Constant.T_Device+" where filestate=1 ";
		// filestate=1
		Cursor cursor = db.selectSql(querySql);
		JSONArray jsons=new JSONArray();
		try{
			while (cursor.moveToNext()) {
				String id=cursor.getString(cursor.getColumnIndexOrThrow("id"));
				String name=cursor.getString(cursor.getColumnIndexOrThrow("name"));
				String filename=cursor.getString(cursor.getColumnIndexOrThrow("filename"));
				JSONObject json=new JSONObject();
				if(filename==null||"".equals(filename)||"null".equals(filename)){
					continue;
				}
				json.put("id", id);
				json.put("name",name);
				json.put("picname",filename);
				jsons.put(json);
			}
			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(jsons.length()==0){
			return;
		}
        final DeviceService deviceService=new 	DeviceService(db);
		try{
			for(int i=0;i<jsons.length();i++){
	            final JSONObject json=jsons.getJSONObject(i);
				final DeviceService.DeviceHandler inHandler=new DeviceService(db).new DeviceHandler(this,json);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try{
							deviceService.doUploadDevice(DataCommit.this, json, inHandler);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}).start();
				Thread.sleep(1000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}        
	}
	//开始上传
	public void doUploadCacheMl(){
		JSONArray jsons=new JSONArray();
		String querySql = "SELECT * FROM " + Constant.T_temp_Defect+ "  order by datetime desc";
		Cursor cursor = db.selectSql(querySql);
		try{
			while (cursor.moveToNext()) {
				String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
				String defectId=cursor.getString(cursor.getColumnIndexOrThrow("id"));
				JSONObject json=new JSONObject(content);
				json.put("defectId", defectId);
				jsons.put(json);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		cursor.close();
		
		boolean has=db.checkExists(Constant.T_temp_Defect, "");
		if(has==false){
			return;
		}
		final CallService callService=new CallService(db);
		for(int i=0;i<jsons.length();i++){
			try{
				final JSONObject json=jsons.getJSONObject(i);
				final CallService.HandlerML handleMl = new CallService(db).new HandlerML(DataCommit.this,json,1);
				new Thread(new Runnable() {
					@Override
					public void run() {
	                          callService.doUploadDefectML(DataCommit.this, json, 1,handleMl);
					}
				}).start();
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void doUploadProduceDefect(){
		String querySql = "SELECT * FROM " + Constant.T_temp_ProduceDevice+" order by datetime desc";
		Cursor cursor = db.selectSql(querySql);
		JSONArray jsons=new JSONArray();
		try{
			while (cursor.moveToNext()) {
				String id=cursor.getString(cursor.getColumnIndexOrThrow("id"));
				String content=cursor.getString(cursor.getColumnIndexOrThrow("content"));
				JSONObject json=new JSONObject(content);
				json.put("proDfId", id);
				jsons.put(json);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		cursor.close();
		boolean has=db.checkExists(Constant.T_temp_ProduceDevice, "");
		if(has==false){
			return;
		}
		final ProduceDefectService proDefectService=new ProduceDefectService(db);
		try{
			for(int i=0;i<jsons.length();i++){
	            final JSONObject json=jsons.getJSONObject(i);
				final ProduceDefectService.ProDefectHandler inHandler=new ProduceDefectService(db).new ProDefectHandler(DataCommit.this,json);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try{
							proDefectService.doUploadProDefect(DataCommit.this, json, inHandler);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}).start();
				Thread.sleep(1000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected Handler taskServiceHanlder = new Handler() {
		@Override
		public void handleMessage(Message m) {
			Bundle b = m.getData();
			String data = b.getString("data");
			if (data!= null&&!"".equals(data)) {
				try{
					JSONArray jsons=new JSONArray(data);
					for(int i=0;i<jsons.length();i++){
						JSONObject json=jsons.getJSONObject(i);
						ContentValues values = new ContentValues();
						int taskId=json.getInt("id");
						int updateResult=json.getInt("result");
						if(updateResult==1){
							 values.put("uploadstatus","2");//上传成功
						}else{
							 values.put("uploadstatus","100");//上传失败
						}
						db.updateSql(Constant.T_Task, values, "id="+taskId);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	};	
	
	
	protected Handler planEquipHanlder = new Handler() {
		@Override
		public void handleMessage(Message m) {
			Bundle b = m.getData();
			String data = b.getString("data");
			if (data!= null&&!"".equals(data)) {
				try{
					JSONArray jsons=new JSONArray(data);
					for(int i=0;i<jsons.length();i++){
						JSONObject json=jsons.getJSONObject(i);
						db.deleteData(Constant.T_PlanEquip, "id=?", new String[]{json.getString("id")});
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	};
	protected Handler reasonHanlder = new Handler() {
		@Override
		public void handleMessage(Message m) {
			Bundle b = m.getData();
			String data = b.getString("data");
			if(data!= null&&!"".equals(data)) {
				 if("true".equals(data)){
					 db.deleteData(Constant.T_NOTOUR_REASON,null,null);
				 }
			}		
		}
	};
	
	protected Handler coordinateHanlder = new Handler() {
		@Override
		public void handleMessage(Message m) {
			int status = m.what;
			Bundle b = m.getData();
			String data = b.getString("data");
			if("true".equals(data)){
				   //坐标上传成功
				   db.deleteData(Constant.T_Coordinate, null, null);
			}
		}
	};	
}
