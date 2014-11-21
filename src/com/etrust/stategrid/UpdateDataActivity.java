package com.etrust.stategrid;

import java.io.File;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.etrust.stategrid.bean.Attach;
import com.etrust.stategrid.bean.DataDevice;
import com.etrust.stategrid.bean.Defect;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.bean.HongwaiBiaozhun;
import com.etrust.stategrid.bean.HongwaiDetail;
import com.etrust.stategrid.bean.Infrared;
import com.etrust.stategrid.bean.ItemCategory;
import com.etrust.stategrid.bean.MainStation;
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.bean.TransSub;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.bean.ZiLiao;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.db.TaskDao;
import com.etrust.stategrid.utils.CallService;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DownloadTask;
import com.etrust.stategrid.utils.HttpClientHelper;
import com.etrust.stategrid.utils.JsonUtils;
import com.google.gson.Gson;
import com.etrust.stategrid.bean.EquipContent;

/*
 * 数据检查更新
 */
public class UpdateDataActivity extends Activity {

	private DatabasesTransaction db;
	private int allResult = 0;
	public Context context;
	public boolean noticed = false;
	public boolean netError = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabasesTransaction.getInstance(this);
		setContentView(R.layout.activity_update_data);
		if (HttpClientHelper.isNetworkConnected(this)) {
			boolean hasChange = db.checkExists(Constant.T_update_log, "");// 如果联网状态，提示先提交之前数据
			if (hasChange) {
				Toast.makeText(UpdateDataActivity.this, "请在【数据管理】中提交数据后再更新！",
						3000).show();
				finish();
				return;
			}
			db.cleanDb();// 删除七张表
			noticed = false;
			allResult = 0;
			getAllUser();
			getTaskandOrbit();
			getAllData();
			getItemCategory();
			getEquipContent();
			getAllZujian();//调用服务器方法
			getZiLiaoContent();
		} else {
			Toast.makeText(UpdateDataActivity.this, "请联网后尝试", 2000).show();
			finish();
		}

	}

	private void getAllZujian() {
		HashMap<String, Object> params=new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getAllZuJian", params, false);
		
	}
	private void getAllUser() {
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getAllUser", params, true);
	}

	private void getTaskandOrbit() {
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getTaskandOrbit", params,
				false);
	}

	private void getAllData() {
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getAllData", params, false);
	}

	private void getItemCategory() {
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getitemCategory", params,
				false);
	}

	public void getEquipContent() {
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getEquipContent", params,
				false);
	}
	
	public void getZiLiaoContent(){
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getAllZiLiao", params, false);
	}

	protected Handler serviceHanlder = new Handler() {
		@Override
		public void handleMessage(Message m) {
			allResult = allResult + 1;
			int status = m.what;
			Bundle b = m.getData();//获取服务器发送的全部数据包含，数据，方法，错误
			String data = b.getString("data");
			String error = b.getString("error");
			String method = b.getString("method");

			if (status == 999) {
				Toast.makeText(UpdateDataActivity.this, error, 3000).show();
				finish();
			} else if (method.equals("getAllUser")) {

				if (data == null || data.isEmpty()) {
					return;
				}
				db.deleteData(Constant.T_USER, null, null);// 删除用户表
				// 删除数据
				List<UserBean> userList = JsonUtils.getUserListFromJson(data);

				for (int i = 0; i < userList.size(); i++) {
					UserBean user = userList.get(i);
					ContentValues values = new ContentValues();
					values.put("userid", user.userid);
					values.put("password", user.password);
					values.put("username", user.username);
					values.put("Deptname", user.Deptname);
					values.put("task","");
					db.saveSql(Constant.T_USER, values);
				}
				load.sendEmptyMessage(1);
				// 用户更新完成
			} else if (method.equals("getTaskandOrbit")) {// 更新任务表
				if (data == null || data.isEmpty()) {
					return;
				}
				String sql = "delete from " + Constant.T_Task
						+ " where uploadstatus!=100";// 上传成功状态改为100
				db.execSql(sql);// 删除除上传失败以外的其他数据
				List<TaskBean> userList = JsonUtils
						.getTaskBeanListFromJson(data);
				System.out.println("TaskBean--------"+data);
				for (int i = 0; i < userList.size(); i++) {
					TaskBean task = userList.get(i);
					ContentValues values = new ContentValues();
					values.put("id", task.id);
					values.put("uploadstatus", Constant.Upload_Task_Status_NONE);
					values.put("content", new Gson().toJson(task));
					values.put("datetime", task.getBegintime());
					values.put("resid", task.getResid());
					db.saveSql(Constant.T_Task, values);
				}
				load.sendEmptyMessage(2);
				// 任务更新完成
				
			} else if (method.equals("getAllData")) {
				if (data == null || data.isEmpty()) {
					return;
				}

				HashMap<Integer, Integer> devIdMap = TaskDao.getAllDeviceId(db);
				HashMap<Integer, Integer> defectIdMap = TaskDao
						.getAllDefectId(db);
				HashMap<String, String> attachIdMap = TaskDao
						.getAllAttachId(db);
				HashMap<Integer, Integer> dlDefectIdMap = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> dlDevIdMap = new HashMap<Integer, Integer>();
				
//红外报告
				DataDevice dataDevice = JsonUtils.getDataDeviceFromJson(data);
				List<Infrared> infraredList = dataDevice.getInfrared();
				for (int i = 0; i < infraredList.size(); i++) {
					Infrared infrared = infraredList.get(i);
					ContentValues values = new ContentValues();
					values.put("id", infrared.id);
					values.put("name", infrared.name);
					values.put("url", infrared.url);
					values.put("createTime", infrared.createTime);
					final String fPath = Constant.HTML_Path + "i_"
							+ infrared.id + ".html";
					values.put("path", fPath);//本地存储地址
					final String dUrl = Constant.hongWaiUrl + infrared.id;
					new Thread(new Runnable() {
						@Override
						public void run() {
							HttpClientHelper.saveHtml(dUrl, fPath);//下载地址 保存路径
						}
					}).start();
					db.saveSql(Constant.T_Infrared, values);
				}

				List<MainStation> mainStationList = dataDevice.getDataDevice();
				for (int i = 0; i < mainStationList.size(); i++) {
					MainStation mainStation = mainStationList.get(i);

					// 变电站
					StringBuffer transSubIds = new StringBuffer();
					List<TransSub> transSub = mainStation.getTransSub();
					for (int j = 0; j < transSub.size(); j++) {
						TransSub ts = transSub.get(j);

						transSubIds.append(ts.id);
						if (j != transSub.size() - 1) {
							transSubIds.append(",");
						}
						// 设备
						List<Device> deviceList = ts.getDevice();
						for (int k = 0; k < deviceList.size(); k++) {
							Device device = deviceList.get(k);
							// 缺陷
							List<Defect> defect = device.getDefect();
							StringBuffer defectIds = new StringBuffer();
							for (int x = 0; x < defect.size(); x++) {
								Defect df = defect.get(x);

								defectIds.append(df.id);
								if (x != defect.size() - 1) {
									defectIds.append(",");
								}
								// 附件
								List<Attach> attach = df.getAttach();
								StringBuilder attachIds = new StringBuilder();
								int attachSize = attach.size();
								for (int y = 0; y < attachSize; y++) {
									Attach at = attach.get(y);
									String fileid = at.fileid;
									attachIds.append("'").append(fileid)
											.append("'");
									if (y != attachSize - 1) {
										attachIds.append(",");
									}
									ContentValues values = new ContentValues();
									values.put("id", fileid);
									values.put("defect", df.id);
									values.put("url", at.url);
									values.put("realName", at.realName);
									values.put("suffux", at.suffux);
									values.put("fileid", fileid);
									String fPath = Constant.File_Path + fileid;
									values.put("path", fPath);

									if (!attachIdMap.containsKey(fileid)) {
										// 如果附件不存在，则添加
										db.saveSql(Constant.T_Attach, values);
									}
									// 因为附件不存在更新，所以不需要更新本地文件
									File file = new File(fPath);
									if (file.exists()) {
										continue;
									}
									new DownloadTask(Constant.DOMAN + at.url,//http://192.168.1.185/gisyw_t
											5, fPath, null).start();
								}

								ContentValues values = new ContentValues();
								int defectId = df.id;
								values.put("id", defectId);
								values.put("device", device.id);
								values.put("itemCategory", df.itemCategory);
								values.put("item", df.item);
								values.put("bugLevel", df.bugLevel);
								values.put("dealType", df.dealType);
								values.put("description", df.description);
								values.put("creatorid", df.creatorid);
								values.put("executorid", df.executorid);
								values.put("createDate", df.createDate);
								values.put("attachIds", attachIds.toString());
								values.put("deviceName", device.getName());
								values.put("tsid", ts.id);
								values.put("cateid", df.cateid);
								values.put("itemid", df.itemid);

								dlDefectIdMap.put(defectId, defectId);

								if (!defectIdMap.containsKey(defectId)) {
									db.saveSql(Constant.T_Defect, values);
								}
							}
							int deviceId = device.id;
							dlDevIdMap.put(deviceId, deviceId);
							ContentValues values = new ContentValues();
							values.put("id", device.id);
							values.put("transsub", ts.id);
							values.put("name", device.name);
							values.put("url", device.url);
							values.put("defectIds", defectIds.toString());
							final String fPath = Constant.HTML_Path + "d_"
									+ device.id + ".html";
							final String dUrl = Constant.DEVICE_URL + device.id;
							/***
							 * new Thread(new Runnable() {
							 * 
							 * @Override public void run() {
							 *           HttpClientHelper.saveHtml(dUrl, fPath);
							 *           } } ).start();
							 ***/
							String picFileNames = device.fileNames;
							values.put("path", fPath);
							values.put("type", device.type);
							values.put("contentId", device.contentId);
							values.put("equip_detail", device.equipDetail);
							values.put("filename", Constant.File_Path
									+ picFileNames);
							values.put("filestate", "0");
							values.put("sortorder", device.sortOrder);

							if (picFileNames != null
									&& !"".equals(picFileNames)) {
								String picFileUrl = Constant.File_Path//SdCard+"/tateGrid/File/"
										+ picFileNames;
								File picFile = new File(picFileUrl);
								if (!picFile.exists()) {
									// 如果图片不存在，需要从服务器下载
									new DownloadTask(
											Constant.DEVICE_PIC_DOWNLOAD
													+ picFileNames, 5,
											picFileUrl, null).start();
								}
							}
							if (devIdMap.containsKey(deviceId)) {
								// 如果没有此设备，则添加
								if (picFileNames != null
										&& !"".equals(picFileNames)) {
									ContentValues basicValues = new ContentValues();
									basicValues.put("id", device.id);
									basicValues.put("filename",
											Constant.File_Path + picFileNames);
									db.updateSql(Constant.T_Device,
											basicValues, "id=" + deviceId);
								}
							} else {
								db.saveSql(Constant.T_Device, values);
							}
						}
						ContentValues values = new ContentValues();
						values.put("id", ts.id);
						values.put("manstation", mainStation.id);
						values.put("name", ts.name);
						values.put("latitude", ts.latitude);
						values.put("longitude", ts.longitude);
						values.put("device", "");
						values.put("voltage", ts.voltage);
						values.put("dianLiu", ts.dianLiu);
						db.saveSql(Constant.T_TransSub, values);//变电站表
					}

					ContentValues values = new ContentValues();
					values.put("id", mainStation.id);
					values.put("name", mainStation.name);
					values.put("desc", mainStation.desc);
					values.put("transSub", transSubIds.toString());
					db.saveSql(Constant.T_MainStation, values);
				}
				for (int cdevid : devIdMap.keySet()) {
					if (!dlDevIdMap.containsKey(cdevid)) {
						// 删除服器上不存在的设备
						db.deleteData(Constant.T_Device, "id=?",
								new String[] { "" + cdevid });
					}
				}
				for (int cdefectId : defectIdMap.keySet()) {
					if (!dlDefectIdMap.containsKey(cdefectId)) {
						db.deleteData(Constant.T_Defect, "id=?",
								new String[] { "" + cdefectId });
					}
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {

				}
				load.sendEmptyMessage(3);
				// 设备信息更新完成
			} else if (method.equals("getitemCategory")) {
				List<ItemCategory> ilist = JsonUtils
						.getIitemCategoryFromJson(data);
				for (int i = 0; i < ilist.size(); i++) {
					ItemCategory ict = ilist.get(i);

					ContentValues values = new ContentValues();
					values.put("id", ict.id);
					values.put("name", ict.name);
					values.put("all_item", new Gson().toJson(ict));
					db.saveSql(Constant.T_ItemCategory, values);
				}
				load.sendEmptyMessage(4);
				// 检查大类更新完成
			} else if (method.equals("getEquipContent")) {
				HashMap<Integer, Integer> econtentIdMap = TaskDao
						.getEquipContentId(db);
				HashMap<Integer, Integer> ecDlIdMap = new HashMap<Integer, Integer>();
				List<EquipContent> equipConList = JsonUtils
						.getEquipContentFromJson(data);
				for (int i = 0; i < equipConList.size(); i++) {
					EquipContent equipCon = equipConList.get(i);
					ContentValues values = new ContentValues();
					int ecid = equipCon.getId();
					ecDlIdMap.put(ecid, ecid);
					values.put("id", ecid);
					values.put("name", equipCon.getName());
					values.put("parent_id", equipCon.getParentId());
					values.put("sort_order", equipCon.getSortOrder());
					values.put("is_parent", equipCon.getIsParent());
					values.put("type", equipCon.getType());
					values.put("ts_id", equipCon.getTsid());
					if (!econtentIdMap.containsKey(ecid)) {
						// 如果目录不存在，则添加目录
						db.saveSql(Constant.T_equip_Content, values);
					}
				}
				for (int ecidKey : econtentIdMap.keySet()) {
					if (!ecDlIdMap.containsKey(ecidKey)) {
						db.deleteData(Constant.T_equip_Content, "id=?",
								new String[] { ecidKey + "" });
					}
				}
				load.sendEmptyMessage(5);
			}else if (method.equals("getAllZuJian")) {//红外标准图
				System.out.println("getAllZuJian-----"+data);
				if (data == null || data.isEmpty()) {
					return;
				}
			
				List<HongwaiBiaozhun>list=JsonUtils.getHongwaiBiaozhunFromJson(data);
				for (int i = 0; i < list.size(); i++) {
					
					HongwaiBiaozhun hongwaiBiaozhun=list.get(i);
					ContentValues conleibie=new ContentValues();
					conleibie.put("content", hongwaiBiaozhun.getCatename());
					db.saveSql(Constant.T_device_leibie, conleibie);//往表里存类型
					ContentValues con=new ContentValues();
					con.put("id", hongwaiBiaozhun.getId());
					
					con.put("datetime", hongwaiBiaozhun.getCateid());
					con.put("leibieid", hongwaiBiaozhun.getCatename());
					 
					HongwaiDetail detail=hongwaiBiaozhun.getContent();
					try{
						Gson gson=new Gson();
						con.put("content",gson.toJson(detail));
						
					}catch(Exception e){
						
					}
					String hongwaiurl=detail.getPicname();
					db.saveSql(Constant.T_hongwai_device, con);
					
					String fPath = Constant.File_Path +hongwaiurl ;//指定本地存储路径
					
				
					File file = new File(fPath);
					if (file.exists()) {
						continue;
					}
					new DownloadTask(Constant.HongwaiBiaozhun+"/"+hongwaiurl,//http://192.168.1.185/gisyw_t
							5, fPath, null).start();
					
					
				}
				
			}else if(method.equals("getAllZiLiao")){
				HashMap<Integer,Integer> ziLiaoIdMap=TaskDao.getZiLiaoId(db);
				HashMap<Integer,Integer> zlDlIdMap=new HashMap<Integer,Integer>();
				List<ZiLiao> ziLiaoList=JsonUtils.getZiLiaoFromJson(data);
				for(int i=0;i<ziLiaoList.size();i++){
					ZiLiao ziliao=ziLiaoList.get(i);
					ContentValues values = new ContentValues();
					int zlid=ziliao.getId();
					zlDlIdMap.put(zlid, zlid);
					values.put("id", zlid);
					values.put("name",ziliao.getName());
					values.put("parent_id",ziliao.getParentId());
					values.put("sort_order",ziliao.getSortOrder());
					values.put("is_parent",ziliao.getIsParent());
					if(!ziLiaoIdMap.containsKey(zlid)){
						//如果目录不存在，则添加目录
						db.saveSql(Constant.T_ziliao, values);
					}else{
						final ContentValues valuesf=values;
						final int fzlid=zlid;
						new Thread(new Runnable() {
							@Override
							public void run() {
								db.updateSql(Constant.T_ziliao, valuesf, "id="+fzlid);
							}
						}).start();
					}
				}
				for(int zlidKey:ziLiaoIdMap.keySet()){
					if(!zlDlIdMap.containsKey(zlidKey)){
						db.deleteData(Constant.T_ziliao, "id=?", new String[]{zlidKey+""});
					}
				}
				load.sendEmptyMessage(6);
			}
		}
	};

	protected Handler load = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (allResult >= 5) {
				if (noticed == false) {
					Toast.makeText(UpdateDataActivity.this, "更新数据完毕", 2000)
							.show();
					noticed = true;
					finish();
				}
			}

		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return true;
	}
}
