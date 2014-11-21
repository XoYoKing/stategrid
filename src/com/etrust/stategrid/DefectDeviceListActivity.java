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
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

public class DefectDeviceListActivity extends Activity implements XListView.IXListViewListener,
		OnItemClickListener, OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private List<Device> dList;
	private DataQueryAdapter queryAdapter;

	private TextView title_text;

	private DatabasesTransaction db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = DatabasesTransaction.getInstance(this);

		setContentView(R.layout.activity_transsub_list);
		adaptData = new ArrayList<String>();

		setupView();

		//添加缺陷上传，选择设备 2014-06-12 11:16
		title_text.setText("缺陷上传--选择设备");
		int id = getIntent().getIntExtra("deviceTransSubID", 0);
		dList = new ArrayList<Device>();
		doGetData(id);

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

	private void doGetData(int id) {
		String querySql = "SELECT * FROM " + Constant.T_Device+" where transsub='"+id+"' order by type asc,name asc";
		Log.i("Lucien transsub", "transsub="+id);
		System.out.println(querySql);
		Cursor cursor = db.selectSql(querySql);
		adaptData.clear();
		dList.clear();

		while (cursor.moveToNext()) {
			Device temp = new Device();
			temp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
			temp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
			temp.url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
			temp.path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
			temp.transsub = cursor.getString(cursor.getColumnIndexOrThrow("transsub"));
			temp.defectIds = cursor.getString(cursor.getColumnIndexOrThrow("defectIds"));

			adaptData.add(temp.name);
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
		Device device = dList.get(rightPosion);
		Intent it = new Intent();
		it.putExtra("defectDevice", device);
		setResult(RESULT_OK, it); 
		finish();//结束之后会将结果传回From
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
