package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

public class DeviceListActivity extends Activity implements XListView.IXListViewListener,
		OnItemClickListener, OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private List<Device> dList;
	private DataQueryAdapter queryAdapter;

	private TextView title_text;
	private String from;
	private boolean isHistory = false;

	private DatabasesTransaction db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = DatabasesTransaction.getInstance(this);

		setContentView(R.layout.activity_transsub_list);
		adaptData = new ArrayList<String>();

		setupView();
		from = getIntent().getCharSequenceExtra("from").toString();

		if (TaskingMapActivity.class.getSimpleName().equals(from)
		    ||DeviceDetailActivity.class.getSimpleName().equals(from)		
			) {
			title_text.setText("查看历史缺陷--设备列表");
			isHistory = true;
			int id = getIntent().getIntExtra("mTransSubID", 1);
			dList = new ArrayList<Device>();
			doGetData(id);
		} else if (DataQueryFragment.class.getSimpleName().equals(from)) {
			title_text.setText("资料查询--设备列表");
			isHistory = false;
			int id = getIntent().getIntExtra("mTransSubID", 1);
			dList = new ArrayList<Device>();
			doGetData(id);
		} else if ("search".equals(from)) {
			title_text.setText("资料查询--搜索结果");
			isHistory = false;
			dList = (List<Device>) getIntent().getSerializableExtra("searchList");
			for (int i = 0; i < dList.size(); i++) {
				adaptData.add(dList.get(i).name);
			}
			queryAdapter.notifyDataSetChanged();
		}else if("defectDevice".equals(from)){
			//添加缺陷上传，选择设备 2014-06-12 11:16
			title_text.setText("缺陷上传--选择设备");
			isHistory = false;
			int id = getIntent().getIntExtra("mTransSubID", 1);
			dList = new ArrayList<Device>();
			doGetData(id);
		}
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
		String querySql = "SELECT * FROM " + Constant.T_Device+" where transsub='"+id+"' order by type asc,sortorder asc";
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
			temp.fileNames = cursor.getString(cursor.getColumnIndexOrThrow("filename"));
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

		if (isHistory) {
			it.setClass(this, DefectHistoryListActivity.class);
		} else {
			it.setClass(this, DeviceDetailActivity.class);
		}
		it.putExtra("mDevice", device);
		startActivity(it);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
