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

public class ProduceDefectService {
	public DatabasesTransaction db;
	
	public ProduceDefectService(){
		
	}
	public ProduceDefectService(DatabasesTransaction dtdb){
		this.db=dtdb;
	}
	public void doUploadProDefect(Context context,JSONObject json,ProDefectHandler handle){
		 //HttpClientHelper.isNetworkConnected(context)
	      String fileName="";
	      try{
	    	  String picname=json.getString("picname");
	    	  fileName=doUploadPicture(picname);
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }
	      try{
	    	  json.put("filename", fileName);
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }
	      HashMap<String, Object> params = new LinkedHashMap<String, Object>();
	      params.put("defectDetail",json.toString());
	      CallService.getData(context,handle, "addProduceDefect", params, false);
	      //上传缺陷
	}
	public class ProDefectHandler extends Handler{
		public Context context;
		public JSONObject json;
		public ProDefectHandler(Context context,JSONObject jsonInfo){
			this.json=jsonInfo;
			this.context=context;
		}
		@Override
		public void handleMessage(Message m) {
			Bundle b = m.getData();
			String data = b.getString("data");

			if("true".equals(data)){
				try{
					db.deleteData(Constant.T_temp_ProduceDevice,"id=?", new String[]{json.getString("proDfId")});
				}catch(Exception e){
					e.printStackTrace();
				}
			}

        }
	}
	//上传图片
	public String doUploadPicture(String picNames) {
		StringBuffer filename = new StringBuffer();
		try {
			if (!picNames.isEmpty()) {
				String[] pics = picNames.split(",");
				for (int i = 0; i < pics.length; i++) {
					if (pics[i].isEmpty()){
						continue;
					}
					File file=new File(pics[i]);
					if(!file.exists()){
						continue;
					}
					String reault = new CallService().httpPost(Constant.UPLOAD_URL, file);
					if (!reault.isEmpty()) {
						JSONObject jsonObject = new JSONObject(reault);
						String name = jsonObject.getString("storeName");
						if (!name.isEmpty()) {
							filename.append(name + ";");
							File oldFile=new File(Constant.File_Path+name);
							if(!oldFile.exists()){
								FileUtils.copyFile(file, oldFile);
								file.delete();
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
