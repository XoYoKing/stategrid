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
import com.etrust.stategrid.bean.MainStation;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

public class MainStationListActivity extends Activity implements
		XListView.IXListViewListener, OnItemClickListener, OnClickListener {
	private boolean mIsLoading;
	private XListView mListView;

	private List<String> adaptData;
	private List<MainStation> msList;
	private DataQueryAdapter queryAdapter;

	private TextView title_text;

	private DatabasesTransaction db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabasesTransaction.getInstance(this);
		setContentView(R.layout.activity_transsub_list);
		adaptData = new ArrayList<String>();
		msList = new ArrayList<MainStation>();
		setupView();
		title_text.setText("系统设置--运维站列表");
		doGetData();
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

	private void doGetData() {
		String querySql = "SELECT * FROM " + Constant.T_MainStation;
		adaptData.clear();
		msList.clear();

		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			MainStation temp = new MainStation();
			temp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
			temp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
			temp.desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));
			temp.tsIds = cursor.getString(cursor.getColumnIndexOrThrow("transSub"));

			adaptData.add(temp.name);
			msList.add(temp);
		}
		queryAdapter.notifyDataSetChanged();
		loadOver();
	}

	private void loadOver() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.title_right:
			goBack();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int rightPosion = (int) arg3;

		MainStation mStation = msList.get(rightPosion);
		Intent it = new Intent();
		it.setClass(this, TransSubListActivity.class);
		it.putExtra("mStation", mStation);
		it.putExtra("from", MainStationListActivity.class.getSimpleName());
		startActivity(it);
	}

	@Override
	public void onBackPressed() {
		goBack();
	}

	private void goBack() {
		finish();
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
