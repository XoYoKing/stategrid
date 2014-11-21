package com.etrust.stategrid.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.etrust.stategrid.App;
import com.etrust.stategrid.TabMainActivity;
import com.etrust.stategrid.TaskReportActivity;
import com.etrust.stategrid.adapter.TaskReportAdapter;
import com.etrust.stategrid.bean.CacheBean;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.google.gson.Gson;

public class InfraredService {
	public DatabasesTransaction db;
	
	public InfraredService(){
		
	}
	public InfraredService(DatabasesTransaction dtdb){
		this.db=dtdb;
	}
	public void doUploadInfrared(Context context,JSONObject json,int uploadType,InfraredHandler handle){
		 HttpClientHelper.isNetworkConnected(context);
	      String fileName="";
	      try{
	    	  json.put("filename", "");
	    	  String picname=json.getString("picname");
	    	  fileName=doUploadPicture(picname);
	    	  json.put("filename", fileName);
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }
	      try{
	    	  json.put("hwpicName","");
	    	  String picname=json.getString("kejianguang");
	    	  fileName=doUploadPicture(picname);
	    	  json.put("hwpicName", fileName);
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }
	      HashMap<String, Object> params = new LinkedHashMap<String, Object>();
	      params.put("infraredList",json.toString());
	      CallService.getData(context,handle, "addInfrared", params, false);
	      //上传红外报告
	}
	public class InfraredHandler extends Handler{
		public Context context;
		public JSONObject json;
		public int uploadType;
		public InfraredHandler(Context context,JSONObject jsonInfo,int uploadType){
			this.json=jsonInfo;
			this.context=context;
			this.uploadType=uploadType;
		}
		@Override
		public void handleMessage(Message m) {
			Bundle b = m.getData();
			String data = b.getString("data");
			if(uploadType==0){
					//实时上传
					if(!"true".equals(data)){
						  Toast.makeText(this.context, "红外上传失败", 2000).show();
					}else{
						 Toast.makeText(this.context, "红外上传成功", 2000).show();
					}
			}else{
				if("true".equals(data)){
					try{
						db.deleteData(Constant.T_temp_Infrared, "id=?", new String[]{json.getString("infraredId")});
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
        }
	}
	//上传图片
	public String doUploadPicture(String picNames) {
		StringBuffer filename = new StringBuffer();
		try {
			if (!picNames.isEmpty()) {//如果不为空
				String[] pics = picNames.split(",");//分割数组
				for (int i = 0; i < pics.length; i++) {
					if (pics[i].isEmpty()){
						continue;
					}
					File picFile=new File(pics[i]);
					if(!picFile.exists()){
						continue;
					}
					String reault = new CallService().httpPost(Constant.UPLOAD_URL, picFile);//上传一直和文件
					if (!reault.isEmpty()) {
						JSONObject jsonObject = new JSONObject(reault);
						String name = jsonObject.getString("storeName");
						if (!name.isEmpty()) {
							filename.append(name + ";");
							File oldFile=new File(Constant.File_Path+name);
							if(!oldFile.exists()){
								FileUtils.copyFile(picFile, oldFile);
								picFile.delete();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fname = "";
		if (filename.length() > 0) {
			fname = filename.toString();
			fname = fname.substring(0, filename.lastIndexOf(";"));
		}
        return fname;
    }		
	
}
