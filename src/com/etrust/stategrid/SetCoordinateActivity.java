package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.etrust.stategrid.adapter.TabViewAdapter;
import com.etrust.stategrid.bean.Coordinate;
import com.etrust.stategrid.bean.TransSub;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.CallService;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;
import com.etrust.stategrid.utils.HttpClientHelper;
import com.google.gson.Gson;

public class SetCoordinateActivity extends Activity implements OnClickListener {

	private ListView task_detial_list;
	private TabViewAdapter adapter;
	private TransSub mTransSub;
	private UserBean user;
	private ImageButton task_detail_begin_btn;

	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	private DatabasesTransaction db;
	private double currentLatutide=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		db = DatabasesTransaction.getInstance(this);
		currentLatutide=0;
		mLocClient = new LocationClient(getApplication());
		mLocClient.registerLocationListener(myListener);//注册定位监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setPriority(LocationClientOption.GpsFirst);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(3000); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位  设置扫描间隔，单位是毫秒
		option.disableCache(true);// 禁止启用缓存定位
		mLocClient.setLocOption(option);//client相关的参数设定
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestOfflineLocation();//离线定位请求，异步返回，结果在locationListener中获取.
		mTransSub = (TransSub) getIntent().getSerializableExtra("mTransSub");
		setupView();

		mLocClient.start();// 启动定位sdk
		//mLocClient.requestOfflineLocation();

		App app = (App) getApplication();
		user = app.getCurrentUserBean();
	}

	private void setupView() {
		task_detail_begin_btn = (ImageButton) findViewById(R.id.task_detail_begin_btn);
		task_detail_begin_btn.setVisibility(View.VISIBLE);
		task_detail_begin_btn.setOnClickListener(this);
		task_detail_begin_btn.setImageResource(R.drawable.commit_img);
		task_detail_begin_btn.setEnabled(false);

		TextView title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("设置坐标");
		findViewById(R.id.title_right).setOnClickListener(this);

		task_detial_list = (ListView) findViewById(R.id.task_detial_list);
		String[] lable = new String[] { "名称", "历史纬度", "历史经度", "当前纬度", "当前经度" };

		String[] data = new String[] { mTransSub.name, mTransSub.latitude,
				mTransSub.longitude, "正在获取……", "正在获取……" };
		adapter = new TabViewAdapter(this, lable, data);
		task_detial_list.setAdapter(adapter);

	}

	private void setCoordinate() {
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		List<Coordinate> coordList=new ArrayList<Coordinate>();
		 Coordinate coord=new Coordinate();
		 coord.setUserid(user.userid);
		 coord.setLatitude(adapter.data[3]);
		 coord.setLongitude(adapter.data[4]);
		 coord.setTsid(mTransSub.id+"");
		 coord.setDatetime(DateUtils.getCurrentDate());
		 coordList.add(coord);
		 params.put("coordinateList", new Gson().toJson(coordList));
		if (HttpClientHelper.isNetworkConnected(this)) {
			    //如果连网，向服务器发送数据
			    if(currentLatutide==4.9E-324){
			    	Toast.makeText(SetCoordinateActivity.this, "请获取坐标后再提交！", 2000).show();
			    	return;
			    }
				CallService.getData(this, serviceHanlder, "addCoordinate", params, true);
		}else{
		    if(currentLatutide==4.9E-324){
		    	Toast.makeText(SetCoordinateActivity.this, "请获取坐标后再提交！", 2000).show();
		    	return;
		    }
			//存在本地
			ContentValues values = new ContentValues();
			values.put("userid",user.userid);
			values.put("latitude",adapter.data[3]);
			values.put("longitude",adapter.data[4]);
			values.put("tsid",mTransSub.id);
			values.put("datetime",DateUtils.getCurrentDateTime());
			values.put("tsname",mTransSub.name);
			db.execSql("delete from "+Constant.T_Coordinate+" where tsid="+mTransSub.id+" and userid='"+user.userid+"'");
			//先删除原有的数据
			db.saveSql(Constant.T_Coordinate, values);
			//保存最新坐标
			db.saveUpdateLog("addCoordinate","坐标更新");
			Log.i("coordinate", "本地存储最新坐标");
			Toast.makeText(SetCoordinateActivity.this, "提交成功", 2000).show();
		}
	}

	protected Handler serviceHanlder = new Handler() {
		@Override
		public void handleMessage(Message m) {
			int status = m.what;
			Bundle b = m.getData();
			String data = b.getString("data");
			String error = b.getString("error");
			String method = b.getString("method");

			if (status == 999) {
				Toast.makeText(SetCoordinateActivity.this, error, 3000).show();
				Log.i("Lucien", method + ":" + error);
			} else {
				Log.i("Lucien", "success>>>>" + data);
				if (data == null || data.isEmpty()) {
					return;
				} else if ("addCoordinate".equals(method)) {
					if ("true".equals(data)) {
						Toast.makeText(SetCoordinateActivity.this, "提交成功", 2000)
								.show();
					} else {
						Toast.makeText(SetCoordinateActivity.this, "提交失败", 2000)
								.show();
					}
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.task_detail_begin_btn:
			if(currentLatutide==0||currentLatutide==4.9E-324){
				Toast.makeText(SetCoordinateActivity.this, "请获取坐标后再提交！", 2000).show();
				return;
			}
			setCoordinate();
			break;
		case R.id.title_right:
			finish();
			break;
		}
	}

	public class MyLocationListenner implements BDLocationListener {//实现百度接口

		@Override
		public void onReceiveLocation(BDLocation location) {//定位请求回调函数
			if (location == null){
				return;
			}
			currentLatutide=location.getLatitude();//获取纬度坐标
			adapter.data = new String[] { mTransSub.name, mTransSub.latitude,
					mTransSub.longitude, "" + location.getLatitude(),
					"" + location.getLongitude()
			};
			if(currentLatutide==4.9E-324){
				adapter.data[3]="正在获取……";
				adapter.data[4]="正在获取……";
			}
			adapter.notifyDataSetChanged();
			task_detail_begin_btn.setEnabled(true);
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
}
