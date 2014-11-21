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
import com.etrust.stategrid.bean.Infrared;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

public class InfraredHistoryListActivity extends Activity implements
		XListView.IXListViewListener, OnItemClickListener, OnClickListener {
	private boolean mIsLoading;
	private XListView mListView;
	private List<String> adaptData;
	private List<Infrared> dList;
	private DataQueryAdapter queryAdapter;

	private TextView title_text;
	private String from;
	private boolean isHistory = false;
	private DatabasesTransaction db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transsub_list);
		adaptData = new ArrayList<String>();
		db = DatabasesTransaction.getInstance(this);
		setupView();

		title_text.setText("红外上传列表");
		isHistory = true;
		int towerid = getIntent().getIntExtra("towerid", 1);
		dList = new ArrayList<Infrared>();
		doGetData(towerid);

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

	private void doGetData(int towerid) {
		if (mIsLoading){
			return;
		}
		mIsLoading = true;

		String querySql = "SELECT * FROM " + Constant.T_Infrared+" order by createTime desc";
		Cursor cursor = db.selectSql(querySql);
		adaptData.clear();
		dList.clear();
		while (cursor.moveToNext()) {
			int id= Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
			String name= cursor.getString(cursor.getColumnIndexOrThrow("name"));
			String url= cursor.getString(cursor.getColumnIndexOrThrow("url"));
			String path= cursor.getString(cursor.getColumnIndexOrThrow("path"));
			String createTime=cursor.getString(cursor.getColumnIndexOrThrow("createTime"));
			Infrared infrared=new Infrared();
			infrared.setId(id);
			infrared.setName(name);
			infrared.setUrl(url);
			infrared.setPath(path);
			infrared.setCreateTime(createTime);
			dList.add(infrared);
			adaptData.add("["+createTime+"]"+name);
		}
		cursor.close();
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
		Infrared infrared = dList.get(rightPosion);
		Intent it = new Intent();
		it.setClass(this, InfraredDetailActivity.class);
		it.putExtra("infrared", infrared);
		startActivity(it);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
