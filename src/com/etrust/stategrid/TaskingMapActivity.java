package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;//自定义覆盖物或标注管理类
import com.baidu.mapapi.map.LocationData;//用户位置信息
import com.baidu.mapapi.map.MapController;//地图控制器
import com.baidu.mapapi.map.MapView;//显示地图的View
import com.baidu.mapapi.map.MyLocationOverlay;//一个显示用户当前位置的Overlay
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;//枚举，定位图标状态
import com.baidu.mapapi.map.OverlayItem;//表示单个overlay数据，如自定义标注，建筑等。
//OverlayItem是ItemizedOverlay的基本组件， OverlayItem存储的overlay数据通过ItemizedOverlay添加到地图中。
import com.baidu.mapapi.map.PopupClickListener;//pop弹窗事件监控接口，要初始化PopupOverlay时需指明
import com.baidu.mapapi.map.PopupOverlay;//此类用来生成一个pop弹窗
import com.baidu.platform.comapi.basestruct.GeoPoint;//表示一个地理坐标点，存放经度和纬度，以微度的整数形式存储
import com.etrust.stategrid.bean.Coordinate;
import com.etrust.stategrid.bean.Equipment;
import com.etrust.stategrid.bean.PlanEquip;
import com.etrust.stategrid.bean.RecordOrbit;
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.service.LocalService;
import com.etrust.stategrid.utils.BMapUtil;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.Utils;

import com.google.gson.Gson;

public class TaskingMapActivity extends Activity implements OnClickListener {
	// 覆盖物图标等
	private static int[] TSTATUS = new int[] { R.drawable.icon_mark_red,
			R.drawable.icon_mark_arriving, R.drawable.icon_mark_over };// 覆盖物图标0未到点1正在点上2到过该点
	private MapView mMapView = null;
	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay pop = null;

	private ArrayList<OverlayItem> mItems = null;// 动态数组
	private View viewCache = null;// 坐标覆盖物
	private View popupLeft = null;// 坐标覆盖物点pop←
	private View popupRight = null;// 坐标覆盖物点pop→
	// 定位相关
	private LocationClient mLocClient;
	private LocationData locData = null;
	private MyLocationListenner myListener = new MyLocationListenner();
	// boolean isFirstLoc = true;// 是否首次定位
	private MyLocationOverlay myLocationOverlay;
	private int arrPosion = -1;// 当前到达点的list位置
	boolean isSend = false;// 是否已经提示过
	// data
	private TaskBean tBean;
	private List<Equipment> eList;// 设备列表
	// pop menu
	private PopupWindow mModePopupWindow;
	private View mModeView;
	private NotificationManager nm;

	private DatabasesTransaction db;
	View dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabasesTransaction.getInstance(this);
		Log.i("into_map", "进入地图");
		App app = (App) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			app.mBMapManager.init(App.strKey, new App.MyGeneralListener());
		}

		tBean = (TaskBean) getIntent().getSerializableExtra("taskData");// 得到传递过来的值

		setContentView(R.layout.activity_tasking_map);

		setupView();

		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(14);
		mMapView.setBuiltInZoomControls(true);

		eList = tBean.getEquipment();// 获取此次任务的所有变电站
		initOverlay();// 初始化覆盖物（变电站标记）
		setupOverlay();// 在地图中显示图标
		GeoPoint p = new GeoPoint((int) (34.628954 * 1E6),
				(int) (112.42949 * 1E6));
		mMapController.setCenter(p);// 在给定的中心点GeoPoint上设置地图视图。

		mLocClient = new LocationClient(getApplication());
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setPriority(LocationClientOption.GpsFirst);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000 * 20); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.disableCache(true);// 禁止启用缓存定位
		option.setAddrType("all"); // 设置地址信息，仅设置为“all”时有地址信息，默认无地址信息
		mLocClient.setLocOption(option);

		mLocClient.start();
		mLocClient.requestOfflineLocation();
		// 定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);// 把用户位置信息展现在地图上设置位置数据
											// 设置图标后需刷新MapView使设置生效
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		// myLocationOverlay.enableCompass();
		myLocationOverlay.setLocationMode(LocationMode.NORMAL);// NORMAL普通态：
																// 更新定位数据时不对地图做任何操作
		// 修改定位数据后刷新图层生效
		mMapView.refresh();

		Intent sIt = new Intent(getApplicationContext(), LocalService.class);
		app.setmCurrentTaskBean(tBean);
		startService(sIt);// 服务在通知栏通知
		// 获取NotificationManager:
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (LocalService.class.getSimpleName().equals(
				getIntent().getStringExtra("from"))) {
			nm.cancel(R.id.title_id);// 删除Notification
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (LocalService.class.getSimpleName().equals(
				getIntent().getStringExtra("from"))) {
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			nm.cancel(R.id.title_id);
		}
		super.onNewIntent(intent);
	}

	private void setupView() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		TextView map_title_text = (TextView) this
				.findViewById(R.id.map_title_text);
		map_title_text.setOnClickListener(this);
		this.findViewById(R.id.title_right).setOnClickListener(this);// 返回
		this.findViewById(R.id.history_btn).setOnClickListener(this);// 历史轨迹

		map_title_text.setText(tBean.name);

		// 任务状态设置 暂停，开始结束等
		mModeView = LayoutInflater.from(this).inflate(
				R.layout.mode_popupwindow, null);
		mModeView.findViewById(R.id.puase_btn).setOnClickListener(this);
		mModeView.findViewById(R.id.close_btn).setOnClickListener(this);
		mModeView.findViewById(R.id.destory_btn).setOnClickListener(this);
		mModeView.findViewById(R.id.over_btn).setOnClickListener(this);
		mModePopupWindow = new PopupWindow(mModeView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mModePopupWindow.setBackgroundDrawable(new BitmapDrawable(this
				.getResources()));
		mModeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mModePopupWindow.dismiss();
			}
		});
	}

	// 是否已经巡检过
	private void doArrived() {
		
		isSend = true;

		Equipment equpment = tBean.equipment.get(arrPosion);// 变电站序号
		if (equpment == null) {
			return;
		}
		List<RecordOrbit> recordList = equpment.recordList;// 巡检历史（轨道记录）
		if (recordList == null) {
			recordList = new ArrayList<RecordOrbit>();
		}

		if (recordList.size() > 0) {
			boolean isExist=false;
			long nowCu = System.currentTimeMillis();
			String time = Utils.yyyyMMddHHmmss(nowCu);
			for (int i = 0; i < recordList.size(); i++) {
				RecordOrbit ro = recordList.get(i);// 记录轨道
				System.out.println("rooooo"+ro.date+"  "+ro.isarrive);
				String data = ro.date;
				if((time.substring(0,10)).equals(data.substring(0,10))) {
					isExist=true;
				}
			}
			if(isExist==false){
				RecordOrbit newRO = new RecordOrbit();
				newRO.setDate(time);
				newRO.setIsarrive("已到位");
				newRO.setIswell("是");
				tBean.equipment.get(arrPosion).recordList.add(newRO);
				ContentValues values = new ContentValues();
				values.put("content", new Gson().toJson(tBean));
				db.updateSql(Constant.T_Task, values, "id='" + tBean.id
						+ "'");
				db.saveUpdateLog("isarrive", "已到位");
			}
		}else {
			long nowCu = System.currentTimeMillis();
			String time = Utils.yyyyMMddHHmmss(nowCu);

			RecordOrbit newRO = new RecordOrbit();
			newRO.setDate(time);
			newRO.setIsarrive("已到位");
			newRO.setIswell("是");
			
			recordList.add(newRO);

			ContentValues values = new ContentValues();
			values.put("content", new Gson().toJson(tBean));
			db.updateSql(Constant.T_Task, values, "id='" + tBean.id + "'");
			
			db.saveUpdateLog("isarrive", "已到位");
			System.out.println("itebn"+tBean.id+"我到位了了------------------------------");
		}
	}

	// 任务状态变更 暂停，终止结束
	private void updateTask(int status) {
		tBean.status = status;
		ContentValues values = new ContentValues();
		values.put("uploadstatus", Constant.Upload_Task_Status_Change);
		values.put("content", new Gson().toJson(tBean));
		int i = db.updateSql(Constant.T_Task, values, "id='" + tBean.id + "'");
		db.saveUpdateLog("updateTask", "任务状态变更");
		if (i > 0) {
			 nm.cancel(R.id.title_id);//通知
			stopService(new Intent(TaskingMapActivity.this, LocalService.class));
			Toast.makeText(TaskingMapActivity.this, "任务状态变更成功", 2000).show();
			finish();
		
		} else {
			Toast.makeText(TaskingMapActivity.this, "任务状态变更失败", 2000).show();
		}
	}

	/*
	 * (non-Javadoc)返回历史按钮和任务状态
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_title_text:// 左上角任务名称和选择状态
			if (mModePopupWindow.isShowing()) {
				mModePopupWindow.dismiss();
			} else {
				mModePopupWindow.showAsDropDown(
						findViewById(R.id.map_title_text), 0, 0);
			}
			break;
		case R.id.title_right:
			finish();
			break;
		case R.id.history_btn:
			Intent it = new Intent(this, TrajectoryHistoryActivity.class);
			it.putExtra("taskData", tBean);
			startActivity(it);
			break;
		case R.id.puase_btn:
			if (mModePopupWindow.isShowing()) {
				mModePopupWindow.dismiss();
			}
			// 0未开始 1进行中2 已暂停3已结束 4 结束在任务列表中显示的是（已终止）
			updateTask(2);
			break;
		case R.id.close_btn:
			if (mModePopupWindow.isShowing()) {
				mModePopupWindow.dismiss();
			}
			break;
		case R.id.over_btn:
			if (mModePopupWindow.isShowing()) {
				mModePopupWindow.dismiss();
			}
			updateTask(3);
			break;
		case R.id.destory_btn:
			if (mModePopupWindow.isShowing()) {
				mModePopupWindow.dismiss();
			}
			updateTask(4);
			break;
		}
	}

	// 创建地图坐标覆盖物图标
	public void initOverlay() {
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_mark_red), mMapView);// 覆盖物的样子为红色代表标记

		mItems = new ArrayList<OverlayItem>();// 动态数组

		/**
		 * 将overlay 添加至MapView中
		 */
		mMapView.getOverlays().add(mOverlay);

		viewCache = getLayoutInflater().inflate(R.layout.map_pop_view, null);// 自定义的pop
		popupLeft = viewCache.findViewById(R.id.popleft);
		popupRight = viewCache.findViewById(R.id.popright);
		PopupClickListener popListener = new PopupClickListener() {// 图标点击事件
			@Override
			public void onClickedPopup(int index) {
				if (!isSend) {

					Toast.makeText(TaskingMapActivity.this, "您还未到达该位置", 2000)
							.show();

				} else if (index == 0) {// 点击左边的出现任务，
					Intent it = new Intent(TaskingMapActivity.this,
							TaskReportActivity.class);
					it.putExtra("taskData", tBean);
					it.putExtra("towerid", eList.get(arrPosion).tsid);
					it.putExtra("toweridName", eList.get(arrPosion).tsname);
					// 变电站名称
					// Log.i("Lucien", "您到达该位置" +
					// eList.get(arrPosion).tsid+" "+eList.get(arrPosion).getTsname());
					startActivity(it);
				} else if (index == 1) {// 点击右边的出现重点巡视设备
					// Intent it = new Intent(TaskingMapActivity.this,
					// InfraredReportActivity.class);
					// startActivity(it);
					// 红外上传暂时不用 2014-07-16
					int currentTsid = eList.get(arrPosion).tsid;
					Intent it = new Intent(TaskingMapActivity.this,
							PlanEquipList.class);
					it.putExtra("planTsId", currentTsid);
					it.putExtra("planTaskId", tBean.id);
					startActivity(it);
				}
			}
		};
		pop = new PopupOverlay(mMapView, popListener);// 此类用来生成一个pop弹窗
	}

	// 把变电站位置信息以图标形式展现在地图上
	private void setupOverlay() {

		// Equipment test = new Equipment();
		// test.tsid = 3;
		// test.tsname = "2#变电站";
		// test.latitude = 22.570481;
		// test.longitude = 113.906116;
		// test.status = 0;
		// eList.add(test);

		mOverlay.removeAll();

		for (int i = 0; i < eList.size(); i++) {// eList循环读变电站信息
			Equipment temp = eList.get(i);
			GeoPoint gp = new GeoPoint((int) (temp.latitude * 1E6),
					(int) (temp.longitude * 1E6));

			OverlayItem item = new OverlayItem(gp, temp.tsname, temp.tsname);// 点，标题，片段
			item.setMarker(getResources().getDrawable(TSTATUS[temp.status]));// 设置Overlay把此次任务中变电站标注图标和颜色
			mOverlay.addItem(item);
		}

		mMapView.refresh();

		mItems.clear();
		mItems.addAll(mOverlay.getAllItem());

	}

	/**
	 * 清除所有Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {
		mOverlay.removeAll();
		if (pop != null) {
			pop.hidePop();// 收起pop窗
		}
		mMapView.refresh();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		if (mLocClient != null)
			mLocClient.stop();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	public class MyOverlay extends ItemizedOverlay {// 自定义地图类，用来管理覆盖物或者标注

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			OverlayItem item = getItem(index);

			final int strTsid = eList.get(index).getTsid();
			final String fitsname = eList.get(index).getTsname();

			if (arrPosion == index) {
				Bitmap[] bitMaps = { BMapUtil.getBitmapFromView(popupLeft),
						BMapUtil.getBitmapFromView(popupRight) };
				pop.showPopup(bitMaps, item.getPoint(), 32);
			} else {
				Toast.makeText(TaskingMapActivity.this, "您还未到达该位置", 2000)
						.show();

				LayoutInflater inflater = LayoutInflater
						.from(getApplicationContext());
				dialog = inflater.inflate(R.layout.dialog_pass_thisstation,
						null);
				final EditText miaoshu = (EditText) dialog
						.findViewById(R.id.miaoshu_text);
				new AlertDialog.Builder(TaskingMapActivity.this)
						.setView(dialog)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										db.deleteData(Constant.T_NOTOUR_REASON,
												"taskid=? and stationname=?",
												new String[] { tBean.id + "",
														"" + strTsid });
										ContentValues values = new ContentValues();
										values.put("taskid", tBean.id);
										values.put("stationname", strTsid);
										values.put("reason", miaoshu.getText()
												.toString());
										db.saveSql(Constant.T_NOTOUR_REASON,
												values);
										db.saveUpdateLog("addNOTOUR", "跳过"
												+ fitsname);
									}
								}).setNegativeButton("取消", null).show();
			}
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop != null) {
				pop.hidePop();// pop消失
			}
			return false;
		}
	}

	// 位置监听
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			Log.d("Lucien_loc", "locData.latitude:" + locData.latitude
					+ "  locData.longitude:" + locData.longitude);
			for (int i = 0; i < mItems.size(); i++) {
				OverlayItem oTemp = mItems.get(i);
				double fixedLat = oTemp.getPoint().getLatitudeE6() / 1E6;// 返回GeoPoint的纬度，单位微度
				double fixedLag = oTemp.getPoint().getLongitudeE6() / 1E6;// 返回GeoPoint对象的经度，单位微度
			

				boolean checkRange = Utils.checkRange(locData.latitude,
						locData.longitude, fixedLat, fixedLag, tBean.range);// 根据两点距离判断是否到位
				// boolean alwaysArrive = (fixedLag >= 112.464074 && fixedLag <=
				// 112.464874);//测试用来在无gps情况下，模拟到位

				if ((checkRange == false) && (locData.latitude == 4.9E-324)) {
					// 如果实时定位显示未到达，可能是因为在屋内造成的，需要判断上次到位的坐标
					Coordinate coord = db.getLastArrive();
					double lastLati = Double.parseDouble(coord.getLatitude());
					double lastLong = Double.parseDouble(coord.getLongitude());
					checkRange = Utils.checkRange(lastLati, lastLong, fixedLat,
							fixedLag, tBean.range);
				}
				if (checkRange) {// 判断是否在变电站上
					mItems.get(i).setMarker(
							getResources().getDrawable(TSTATUS[1]));// 正在点上
					if (mOverlay == null) {
						return;
					}
					mOverlay.removeAll();
					mOverlay.addItem(mItems);
					arrPosion = i;// // 当前到达点的list位置
					if (!isSend) {
						Log.i("Lucien", "isSend_map_task:" + isSend);
						doArrived();// 到位后修改状态为已到位或者未到位
					}
					break;
				} else {
					if (arrPosion != -1 && arrPosion == i && isSend == true) {
						mItems.get(i).setMarker(
								getResources().getDrawable(TSTATUS[2]));// 该点已经去过
						arrPosion = -1;
						isSend = false;
					}
				}
			}

			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();
			// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			// if (isFirstLoc) {
			if (locData.latitude != 4.9E-324) {
				// 移动地图到定位点 将给定的位置点以动画形式移动至地图中心 对以给定的点GeoPoint，开始动画显示地图
				Log.d("LocationOverlay", "receive location, animate to it");
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
			}

		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
}
