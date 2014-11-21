package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.etrust.stategrid.adapter.DataQueryAdapter;
import com.etrust.stategrid.bean.Defect;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

public class DefectHistoryListActivity extends Activity implements
		XListView.IXListViewListener, OnItemClickListener, OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private List<Defect> dList;
	private DataQueryAdapter queryAdapter;

	private TextView title_text;
	
	private DatabasesTransaction db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transsub_list);
		db = DatabasesTransaction.getInstance(this);
		adaptData = new ArrayList<String>();

		setupView();

		title_text.setText("缺陷历史列表");
		Device device = (Device) getIntent().getSerializableExtra("mDevice");
		Log.i("Lucien","Defectlist_device_id:"+device.id);
		dList = new ArrayList<Defect>();
		doGetData(device.id);

	}

	private void setupView() {

		title_text = (TextView) findViewById(R.id.title_text);
		findViewById(R.id.title_right).setOnClickListener(this);

		mListView = (XListView) findViewById(R.id.list_view);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);

		queryAdapter = new DataQueryAdapter(this, adaptData);

		mListView.setAdapter(queryAdapter);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);

	}

	private void doGetData(int deviceId) {
		String querySql = "SELECT * FROM " + Constant.T_Defect 
				+ " WHERE device="+deviceId+" order by createDate desc";
		adaptData.clear();
		dList.clear();
		
		Cursor cursor = db.selectSql(querySql);
		
		while (cursor.moveToNext()) {
			Defect temp = new Defect();
			temp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
			temp.device = cursor.getString(cursor.getColumnIndexOrThrow("device"));//
			temp.itemCategory = cursor.getString(cursor.getColumnIndexOrThrow("itemCategory"));
			temp.item = cursor.getString(cursor.getColumnIndexOrThrow("item"));
			temp.bugLevel = cursor.getString(cursor.getColumnIndexOrThrow("bugLevel"));
			temp.dealType = cursor.getString(cursor.getColumnIndexOrThrow("dealType"));
			temp.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
			temp.creatorid = cursor.getString(cursor.getColumnIndexOrThrow("creatorid"));
			temp.executorid = cursor.getString(cursor.getColumnIndexOrThrow("executorid"));
			temp.createDate = cursor.getString(cursor.getColumnIndexOrThrow("createDate"));
			temp.attachIds = cursor.getString(cursor.getColumnIndexOrThrow("attachIds"));
			temp.device=cursor.getString(cursor.getColumnIndexOrThrow("deviceName"));
			adaptData.add("["+temp.createDate +"]"+temp.itemCategory+"  "+temp.item+"  "+temp.bugLevel);
			dList.add(temp);
		}
		loadOver();
		
		queryAdapter.notifyDataSetChanged();
	}

	private void loadOver() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.title_right:
			finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int rightPosion = (int) arg3;
		Defect defect = dList.get(rightPosion);
		Intent it = new Intent();
		it.setClass(this, HistoryTaskReportActivity.class);
		it.putExtra("defect", defect);
		startActivity(it);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
