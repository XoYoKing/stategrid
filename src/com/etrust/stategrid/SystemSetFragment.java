package com.etrust.stategrid;

import java.util.HashMap;
import java.util.LinkedHashMap;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.etrust.stategrid.bean.VersionBean;
import com.etrust.stategrid.utils.CallService;
import com.etrust.stategrid.utils.HttpClientHelper;
import com.etrust.stategrid.utils.JsonUtils;

public class SystemSetFragment extends Fragment implements OnClickListener {

	private TextView sys_first_title;
	private TextView sys_log;
	private TextView sys_lat;

	private App app;
	
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getActivity().getApplication();
		
		mLocClient = new LocationClient(getActivity().getApplication());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setPriority(LocationClientOption.GpsFirst);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000*5); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.disableCache(false);// 启用缓存定位
		mLocClient.setLocOption(option);
		
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestOfflineLocation();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_systemset_layout,
				container, false);
		rootView.findViewById(R.id.sys_cancle_btn).setOnClickListener(this);
		
		sys_first_title = (TextView) rootView
				.findViewById(R.id.sys_first_title);
		sys_log = (TextView) rootView.findViewById(R.id.sys_log);
		sys_lat = (TextView) rootView.findViewById(R.id.sys_lat);
		
		mLocClient.start();
		mLocClient.requestOfflineLocation();
		sys_log.setText("经度：");
		sys_lat.setText("纬度：");
		
		

		
		
		View set_coordinate_btn = rootView
				.findViewById(R.id.set_coordinate_btn);
		set_coordinate_btn.setOnClickListener(this);
		
		View update_data_btn = rootView
				.findViewById(R.id.update_data_btn);
		update_data_btn.setOnClickListener(this);
		
		TextView update_state = (TextView) update_data_btn
				.findViewById(R.id.tasklist_state);
		TextView update_title = (TextView) update_data_btn
				.findViewById(R.id.tasklist_title);
		TextView update_time = (TextView) update_data_btn
				.findViewById(R.id.tasklist_time);
		update_title.setText("更新数据");
		update_time.setVisibility(View.INVISIBLE);
		update_state.setVisibility(View.INVISIBLE);
		
		
	

		
		TextView tasklist_state = (TextView) set_coordinate_btn
				.findViewById(R.id.tasklist_state);
		TextView tasklist_title = (TextView) set_coordinate_btn
				.findViewById(R.id.tasklist_title);
		TextView tasklist_time = (TextView) set_coordinate_btn
				.findViewById(R.id.tasklist_time);
		
		
		tasklist_title.setText("设置坐标");
		tasklist_time.setVisibility(View.INVISIBLE);
		tasklist_state.setVisibility(View.INVISIBLE);

		return rootView;
	}

//	private void checkVersion() {
//		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
//		int versionCode = 1;
//		try {
//			versionCode = getActivity().getPackageManager().getPackageInfo(
//					getActivity().getPackageName(), 0).versionCode;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		params.put("version", versionCode + "");
//		CallService.getData(getActivity(), serviceHanlder, "checkVersion",
//				params, true);
//	}

	protected Handler serviceHanlder = new Handler() {
		@Override
		public void handleMessage(Message m) {
			int status = m.what;
			Bundle b = m.getData();
			String data = b.getString("data");
			String error = b.getString("error");
			String method = b.getString("method");

			if (status == 999) {
				Toast.makeText(getActivity(), error, 3000).show();
				Log.i("Lucien", method + ":" + error + data);
			} else {
				if ("checkVersion".equals(method)) {
					if (data == null || data.isEmpty()) {
						return;
					}
					VersionBean vben = JsonUtils.getVersionBeanFromJson(data);
					if (vben.result) {
						Uri url = Uri.parse(vben.url);
						Intent intent = new Intent(Intent.ACTION_VIEW, url);
						getActivity().startActivity(intent);
					} else {
						Toast.makeText(getActivity(), "暂时没有更新", 2000).show();
					}
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_coordinate_btn://设置坐标
			getActivity().startActivity(new Intent(getActivity(),MainStationListActivity.class));
			break;
		case R.id.sys_cancle_btn://注销
			app.cancellation();
			Intent it = new Intent(getActivity(),LoginActivity.class);
			getActivity().startActivity(it);
			getActivity().finish();
			break;
		case R.id.update_data_btn://更新数据
			if (!HttpClientHelper.isNetworkConnected(getActivity())){
				Toast.makeText(getActivity(), "网络不可用，请检查网络连接！", 3000).show();
				return;
			}
			Intent up = new Intent(getActivity(),UpdateDataActivity.class);
			up.putExtra("from", SystemSetFragment.class.getSimpleName());
			getActivity().startActivity(up);
			break;
			/***
			 
		case R.id.infrared_History_btn:
			Intent ihb = new Intent(getActivity(),InfraredHistoryListActivity.class);
			ihb.putExtra("from", SystemSetFragment.class.getSimpleName());
			getActivity().startActivity(ihb);
			break;
			
			****/
			//红外列表先不用
		}
		

		
		
		
	}
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return;
			}
			if(location.getLongitude()==4.9E-324){
				sys_log.setText("经度：正在获取……");
				sys_lat.setText("维度：正在获取……");
			}else{
				sys_log.setText("经度："+location.getLongitude());
				sys_lat.setText("维度："+location.getLatitude());
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
