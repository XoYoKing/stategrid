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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.etrust.stategrid.adapter.TaskReportAdapter;
import com.etrust.stategrid.bean.CacheBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.google.gson.Gson;

public class CallService {
	public DatabasesTransaction db;

	public CallService() {

	}

	public CallService(DatabasesTransaction dtdb) {
		this.db = dtdb;
	}

	public static void getData(Context c, Handler serviceHanlder,
			String method, HashMap<String, Object> params, boolean alert) {
		CallServiceThread thread = new CallServiceThread(serviceHanlder);

		thread.doStart(Constant.url, Constant.nameSpace, method, params, c,
				alert);//启动线程联网
	}

	public void doUploadDefectML(Context context, JSONObject json,
			int uploadType, HandlerML handleMl) {
		// HttpClientHelper.isNetworkConnected(context)
		String fileName = "";
		try {
			fileName = uploadFile(json.getString("audioCase"),
					json.getString("picCase"), json.getString("videoCase"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			json.put("filename", fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("defectDetail", json.toString());
		CallService.getData(context, handleMl, "addDefect", params, false);
		// 上传缺陷
	}

	public void doUploadDefect(Context context, String[] data, int towerid,
			String userid, int deviceId, boolean isCache, Myhandler hanlder,
			String createTime) {
		boolean succes = true;
		StringBuffer filename = new StringBuffer();
		try {

			if (!data[TaskReportAdapter.AUDIO_CASE].isEmpty()
					&& !"audio".equals(data[TaskReportAdapter.AUDIO_CASE])) {
				String reault = httpPost(Constant.UPLOAD_URL, new File(
						data[TaskReportAdapter.AUDIO_CASE]));
				// 上传图片或视频
				if (!reault.isEmpty()) {
					JSONObject jsonObject = new JSONObject(reault);
					String name = jsonObject.getString("storeName");
					if (!name.isEmpty()) {
						filename.append(name + ";");
					} else {
						succes = false;
					}
				} else {
					succes = false;
				}
			}
			if (!data[TaskReportAdapter.PIC_CASE].isEmpty()
					&& !"pic".equals(data[TaskReportAdapter.PIC_CASE])) {
				String[] pics = data[TaskReportAdapter.PIC_CASE].split(",");
				// 上传图片
				for (int i = 0; i < pics.length; i++) {
					if (pics[i].isEmpty())
						continue;
					String reault = httpPost(Constant.UPLOAD_URL, new File(
							pics[i]));
					if (!reault.isEmpty()) {
						JSONObject jsonObject = new JSONObject(reault);
						String name = jsonObject.getString("storeName");
						if (!name.isEmpty()) {
							filename.append(name + ";");
						} else {
							succes = false;
						}
					} else {
						succes = false;
					}
				}
			}
			if (!data[TaskReportAdapter.VIDEEO_CASE].isEmpty()
					&& !"video".equals(data[TaskReportAdapter.VIDEEO_CASE])) {
				String reault = httpPost(Constant.UPLOAD_URL, new File(
						data[TaskReportAdapter.VIDEEO_CASE]));
				if (!reault.isEmpty()) {
					JSONObject jsonObject = new JSONObject(reault);
					String name = jsonObject.getString("storeName");
					if (!name.isEmpty()) {
						filename.append(name + ";");
					} else {
						succes = false;
					}
				} else {
					succes = false;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			succes = false;
		} catch (IOException e) {
			e.printStackTrace();
			succes = false;
		} catch (JSONException e) {
			e.printStackTrace();
			succes = false;
		}
		if (!succes) {
			doSaveCatch(context, data, towerid, userid, deviceId, createTime);
			return;
		}
		String fname = "";
		if (filename.length() > 0) {
			fname = filename.toString();
			fname = fname.substring(0, filename.lastIndexOf(";"));
		}
		String defect = data[TaskReportAdapter.DEFECT_CASE];
		String[] dItem = new String[] { "", "", "", "", "" };
		if (defect.length() > 0) {
			dItem = defect.split(",");
			// dItem[2] = dItem[2].replace("类型：", "");
		}

		// HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		JSONObject params = new JSONObject();
		try {
			params.put("itemCategory", dItem[3]);
			params.put("item", dItem[4]);
			// params.put("deviceid", deviceId);
			params.put("towerid", deviceId);
			params.put("bugLevel",
					Integer.parseInt(data[TaskReportAdapter.LEVEL_CASE]));
			params.put("dealType",
					Integer.parseInt(data[TaskReportAdapter.DEAL_CASE]));
			params.put("description", data[TaskReportAdapter.DESCRIP_CASE]);
			params.put("creatorid", data[1]);
			params.put("executorid", data[2]);
			params.put("filename", fname);
			params.put("userid", userid);
			params.put("createTime", createTime);
		} catch (Exception e) {

		}
		HashMap<String, Object> params2 = new LinkedHashMap<String, Object>();
		String defectDetail = params.toString();
		params2.put("defectDetail", defectDetail);

		CallService.getData(context, hanlder, "addDefect", params2, false);
	}

	public class HandlerML extends Handler {
		public Context context;
		public JSONObject json;
		public int uploadType;

		public HandlerML(Context context, JSONObject jsonInfo, int uploadType) {
			this.json = jsonInfo;
			this.context = context;
			this.uploadType = uploadType;
		}

		@Override
		public void handleMessage(Message m) {
			Bundle b = m.getData();
			String data = b.getString("data");
			if (uploadType == 0) {
				// 实时上传
				if ("false".equals(data)) {
					Toast.makeText(this.context, "缺陷上报失败", 2000).show();
				} else if ("true".equals(data)) {
					Toast.makeText(this.context, "缺陷上报成功", 2000).show();
				}
			} else {
				if ("true".equals(data)) {
					try {
						db.deleteData(Constant.T_temp_Defect, "id=?",
								new String[] { json.getString("defectId") });
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class Myhandler extends Handler {
		String[] cache;
		int towerid;
		String userid;
		int deviceId;
		Context context;
		boolean isCache = false;
		String createTime;

		public Myhandler(Context context, String[] cache, int towerid,
				String userid, int deviceId, boolean isCache, String createTime) {
			this.cache = cache;
			this.towerid = towerid;
			this.userid = userid;
			this.context = context;
			this.isCache = isCache;
			this.deviceId = deviceId;
			this.createTime = createTime;
		}

		@Override
		public void handleMessage(Message m) {

			int status = m.what;
			Bundle b = m.getData();
			String data = b.getString("data");
			String error = b.getString("error");
			String method = b.getString("method");

			if (status == 999) {
				doSaveCatch(context, cache, towerid, userid, deviceId,
						createTime);
			} else {
				if (data == null || data.isEmpty() || "false".equals(data)) {
					doSaveCatch(context, cache, towerid, userid, deviceId,
							createTime);
					Log.i("Lucien_upload", "data:" + data);
				} else {
					Log.i("Lucien_upload", "data:" + data);
					if (isCache) {
						CacheBean cbean = new CacheBean();
						cbean.cache = cache;
						cbean.towerid = towerid;
						cbean.userid = userid;
						cbean.deviceId = deviceId;
						cbean.createTime = this.createTime;
						SharedPreferences sp = context.getSharedPreferences(
								"CacheReprot", Context.MODE_PRIVATE);
						String cacheStr = sp.getString("cache", "");
						if (!cacheStr.isEmpty()) {
							List<CacheBean> cList = JsonUtils
									.getCacheFromJson(cacheStr);
							boolean isremove = cList.remove(cbean);
							Log.i("Lucien", "remove cache:" + isremove);
						}
					}
					Toast.makeText(context, "缺陷上传成功", 1000).show();
				}
			}
		}
	}

	public void doSaveCatch(Context context, String[] cache, int towerid,
			String userid, int deviceId, String createTime) {
		CacheBean cbean = new CacheBean();
		cbean.cache = cache;
		cbean.towerid = towerid;
		cbean.userid = userid;
		cbean.deviceId = deviceId;
		cbean.createTime = createTime;
		SharedPreferences sp = context.getSharedPreferences("CacheReprot",
				Context.MODE_PRIVATE);
		String cacheStr = sp.getString("cache", "");
		if (!cacheStr.isEmpty()) {
			List<CacheBean> cList = JsonUtils.getCacheFromJson(cacheStr);
			cList.add(cbean);
			sp.edit().putString("cache", new Gson().toJson(cList)).commit();
		} else {
			List<CacheBean> cList = new ArrayList<CacheBean>();
			cList.add(cbean);
			sp.edit().putString("cache", new Gson().toJson(cList)).commit();
		}
	}
	/*
	 * post方式请求
	 */
	public String httpPost(String url, File file)
			throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		MultipartEntity mpEntity = new MultipartEntity();

		if (file != null && file.exists()) {
			ContentBody f1 = new FileBody(file);
			mpEntity.addPart("file", f1);
		}

		// 设置参数实体
		httpPost.setEntity(mpEntity);
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 连接超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
		// 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				60000);
		//发送请求得到响应
		HttpResponse httpResp = httpClient.execute(httpPost);
		String json = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
		if (null == json) {
			json = "";
		}
		return json;
	}

	// 得到上传后的文件名称
	public String uploadFile(String audioCase, String picCase, String videoCase)
			throws Exception {
		if (picCase == null || "null".equals(picCase)) {
			picCase = "";
		}
		if (audioCase == null || "null".equals(audioCase)) {
			audioCase = "";
		}
		if (videoCase == null || "null".equals(videoCase)) {
			videoCase = "";
		}
		StringBuffer filename = new StringBuffer("");
		if (!audioCase.isEmpty() && !"audio".equals(audioCase)) {
			File file = new File(audioCase);
			if (file.exists()) {
				String reault = httpPost(Constant.UPLOAD_URL, file);
				// 上传图片或视频
				if (!reault.isEmpty()) {
					JSONObject jsonObject = new JSONObject(reault);
					String name = jsonObject.getString("storeName");
					if (!name.isEmpty()) {
						filename.append(name + ";");
						// 开始转移数据
						File oldFile = new File(Constant.File_Path + name);
						if (!oldFile.exists()) {
							FileUtils.copyFile(file, oldFile);
							file.delete();
						}
					}
				}
			}
		}
		if (!picCase.isEmpty() && !"pic".equals(picCase)) {
			String[] pics = picCase.split(",");
			// 上传图片
			for (int i = 0; i < pics.length; i++) {
				if (pics[i].isEmpty())
					continue;
				File file = new File(pics[i]);
				if (!file.exists()) {
					continue;
				}
				String reault = httpPost(Constant.UPLOAD_URL, file);
				if (!reault.isEmpty()) {
					JSONObject jsonObject = new JSONObject(reault);
					String name = jsonObject.getString("storeName");
					if (!name.isEmpty()) {
						filename.append(name + ";");
						// 开始转移数据
						File oldFile = new File(Constant.File_Path + name);
						if (!oldFile.exists()) {
							FileUtils.copyFile(file, oldFile);
							file.delete();
						}
					}
				}
			}
		}
		if (!videoCase.isEmpty() && !"video".equals(videoCase)) {
			File videoCaseFile = new File(videoCase);
			if (videoCaseFile.exists()) {
				String reault = httpPost(Constant.UPLOAD_URL, videoCaseFile);
				if (!reault.isEmpty()) {
					JSONObject jsonObject = new JSONObject(reault);
					String name = jsonObject.getString("storeName");
					if (!name.isEmpty()) {
						filename.append(name + ";");
						// 开始转移数据
						File oldFile = new File(Constant.File_Path + name);
						if (!oldFile.exists()) {
							FileUtils.copyFile(videoCaseFile, oldFile);
							videoCaseFile.delete();
						}
					}
				}
			}
		}
		String names = filename.toString();
		if (names.length() > 1) {
			names = names.substring(0, names.length() - 1);
		}
		return names;
	}
}
