package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.etrust.stategrid.bean.Orbit;
import com.etrust.stategrid.bean.TaskBean;

public class TrajectoryHistoryActivity extends Activity implements OnClickListener {

	private MapView mMapView = null;

	private MapController mMapController = null;

	private MyOverlay mOverlay = null;
	private ArrayList<OverlayItem> mItems = null;

	// data
	private TaskBean tBean;
	private List<Orbit> eList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		App app = (App) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			app.mBMapManager.init(App.strKey, new App.MyGeneralListener());
		}

		tBean = (TaskBean) getIntent().getSerializableExtra("taskData");
		eList = tBean.orbitList;
		
		setContentView(R.layout.activity_tasking_map);

		setupView();

		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(14);
		mMapView.setBuiltInZoomControls(true);
		
		initOverlay();
		setupOverlay();
		
		if(eList.size()>0){
			GeoPoint p = new GeoPoint((int) (eList.get(0).getLatitude() * 1E6),
					(int) (eList.get(0).getLongitude() * 1E6));
			mMapController.setCenter(p);
		}
		

	}
	
	private void setupView() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		TextView map_title_text = (TextView) this
				.findViewById(R.id.map_title_text);
		map_title_text.setOnClickListener(this);
		this.findViewById(R.id.title_right).setOnClickListener(this);
		this.findViewById(R.id.history_btn).setVisibility(View.INVISIBLE);

		map_title_text.setText(tBean.name+"——默认轨迹");
		map_title_text.setCompoundDrawables(null, null, null, null);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right:
			finish();
			break;
		}
	}

	public void initOverlay() {
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_mark_point), mMapView);
		mItems = new ArrayList<OverlayItem>();
		
		/**
		 * 将overlay 添加至MapView中
		 */
		mMapView.getOverlays().add(mOverlay);
	}
	private void setupOverlay(){
		
		mOverlay.removeAll();
		
		for (int i = 0; i < eList.size(); i++) {
			Orbit temp = eList.get(i);
			GeoPoint gp = new GeoPoint((int) (temp.latitude * 1E6),
					(int) (temp.longitude * 1E6));
			OverlayItem item = new OverlayItem(gp,"", temp.date);
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

	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {
			
			return false;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			
			return false;
		}
	}
}
