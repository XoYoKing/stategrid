package com.etrust.stategrid.service;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.etrust.stategrid.App;
import com.etrust.stategrid.LoginActivity;
import com.etrust.stategrid.R;
import com.etrust.stategrid.TaskingMapActivity;
import com.etrust.stategrid.bean.Equipment;
import com.etrust.stategrid.bean.Orbit;
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;
import com.etrust.stategrid.utils.Utils;
import com.google.gson.Gson;

public class LocalService extends Service {

	private NotificationManager mNM;
	private final IBinder mBinder = new LocalBinder();

	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();

	private TaskBean tBean;
	private List<Equipment> eList;
	private int posion = -1;
	private boolean isSend = false;

	private double oldlat = 0;
	private double oldlog = 0;
	private double currlat;
	private double currlog;

	@Override
	public void onCreate() {
		Log.i("Lucien", "service onCreate");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i("Lucien", "onStartCommand");
		App app = (App) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			app.mBMapManager.init(App.strKey, new App.MyGeneralListener());
		}
		tBean = app.getmCurrentTaskBean();
		if (app == null) {
			Log.i("Lucien", "app == null");
			return START_STICKY;
		}
		if (tBean == null) {
			Log.i("Lucien", "tBean==null");
			return START_STICKY;
		}
		eList = tBean.getEquipment();

		mLocClient = new LocationClient(getApplication());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		//option.setPriority(LocationClientOption.NetWorkFirst);
		option.setPriority(LocationClientOption.GpsFirst);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000 * 20); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.disableCache(true);// 禁止启用缓存定位
		option.setAddrType("all"); // 设置地址信息，仅设置为“all”时有地址信息，默认无地址信息
		mLocClient.setLocOption(option);

		mLocClient.start();
		mLocClient.requestOfflineLocation();
		Log.i("Lucien", "mLocClient.start:" + mLocClient.isStarted());
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.i("Lucien", "service onDestroy");
		// Cancel the persistent notification.
		// mNM.cancel(R.id.title_id);
		if (mLocClient != null)
			mLocClient.stop();
		// startService(new Intent(this,LocalService.class));
		// Toast.makeText(this, "onDestroy", 2000).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		LocalService getService() {
			return LocalService.this;
		}
	}

	private void upLoadPosion(double lat, double log) {
		long nowCu = System.currentTimeMillis();
		String time = Utils.yyyyMMddHHmmss(nowCu);
		Orbit temp = new Orbit();
		temp.latitude = lat;
		temp.longitude = log;
		temp.date = time;
		temp.id = (int) System.currentTimeMillis();
		temp.msid = Integer.parseInt(eList.get(0).msid);
		tBean.orbitList.add(temp);
		ContentValues values = new ContentValues();
		values.put("content", new Gson().toJson(tBean));
		DatabasesTransaction.getInstance(this).updateSql(Constant.T_Task, values, "id='" + tBean.id + "'");
		oldlat = currlat;
		oldlog = currlog;
	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location == null){
				return;
			}
			
			currlat = location.getLatitude();
			currlog = location.getLongitude();

			Log.i("Lucien", "service>Log:" + currlog + "Lat:" + currlat);

			if (currlat != 4.9E-324 && currlog != 4.9E-324) {
				if (oldlat != currlat || oldlog != currlog) {
					if (Utils.getDistatce(oldlat, currlat, oldlog, currlog) > 10) {
						upLoadPosion(currlat, currlog);
						
					}
				}
			} else {
				return;
			}

			for (int i = 0; i < eList.size(); i++) {
				Equipment oTemp = eList.get(i);
				double fixedLat = oTemp.latitude;
				double fixedLag = oTemp.longitude;

				if (Utils.checkRange(currlat, currlog, fixedLat, fixedLag, tBean.range)) {
					posion = i;
					if (!isSend) {
						ContentValues tempEq= new ContentValues();
						tempEq.put("latitude", currlat);
						tempEq.put("longitude",currlog);
						tempEq.put("datetime", DateUtils.getCurrentDateTime());
						DatabasesTransaction db=DatabasesTransaction.getInstance(LocalService.this);
						db.updateSql(Constant.T_temp_Arrive, tempEq,null);
						//保存当前到位变电站信息
						showNotification();
						isSend = true;
						
						//DatabasesTransaction.getInstance(LocalService.this).saveUpdateLog("isSend","到位提醒");
					}
					break;
				} else {
					if (posion != -1 && posion == i) {
						posion = -1;
						isSend = false;
					}
				}
			}

		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	private void showNotification() {
         
		Equipment eq=eList.get(posion);
		//定义一个Notification: 
		Notification notification = new Notification(R.drawable.icon_mark_arriving, "您已到达-"
				+ eList.get(posion).tsname, System.currentTimeMillis());
        
		Intent it = new Intent(this, TaskingMapActivity.class);
		it.putExtra("taskData", tBean);
		it.putExtra("from", LocalService.class.getSimpleName());
		it.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, it, 0);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.setLatestEventInfo(this, "到位提醒", "您已到达-" + eList.get(posion).tsname, contentIntent);
		

		mNM.notify(R.id.title_id, notification);
	}
	
}
