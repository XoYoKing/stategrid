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
import com.etrust.stategrid.bean.TransSub;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

public class TransSubListActivity extends Activity implements
		XListView.IXListViewListener, OnItemClickListener, OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private List<TransSub> tsList;
	private DataQueryAdapter queryAdapter;

	private TextView title_text;

	private String from;

	private boolean isSys = false;

	private DatabasesTransaction db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = DatabasesTransaction.getInstance(this);
		
		setContentView(R.layout.activity_transsub_list);
		adaptData = new ArrayList<String>();
		setupView();
		
		CharSequence extraFrom=getIntent().getCharSequenceExtra("from");
		if(extraFrom!=null){
			from = extraFrom.toString();
		}else{
			from="DataQueryFragment";
		}
		

		if (MainStationListActivity.class.getSimpleName().equals(from)) {
			title_text.setText("设置坐标--变电站列表");
			isSys = true;
		} else if (DataQueryFragment.class.getSimpleName().equals(from)) {
			title_text.setText("资料查询--变电站列表");
			isSys = false;
		}
		MainStation mStation = (MainStation) getIntent().getSerializableExtra("mStation");
		
		setFirstData(mStation.id);
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

	private void setFirstData(int msid) {
		String querySql = "SELECT * FROM " + Constant.T_TransSub +" where manstation="+msid+" order by name";
		adaptData.clear();
		tsList = new ArrayList<TransSub>();
		
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			TransSub temp = new TransSub();
			temp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
			temp.manstation =  cursor.getString(cursor.getColumnIndexOrThrow("manstation"));
			temp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
			temp.latitude = cursor.getString(cursor.getColumnIndexOrThrow("latitude"));
			temp.longitude = cursor.getString(cursor.getColumnIndexOrThrow("longitude"));
			temp.deviceIds = cursor.getString(cursor.getColumnIndexOrThrow("device"));
            //temp.msname=cursor.getString(cursor.getColumnIndexOrThrow("msname"));
			adaptData.add(temp.name);
			tsList.add(temp);
		}
		queryAdapter.notifyDataSetChanged();
		
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
		 TransSub mTransSub = tsList.get(rightPosion);
		if (isSys) {
			Intent it = new Intent();
			it.setClass(this, SetCoordinateActivity.class);
			it.putExtra("mTransSub", mTransSub);
			startActivity(it);
		} else {
			Intent it = new Intent();
			it.setClass(this, TransubOperate.class);
			it.putExtra("mTransSubID", mTransSub.id);
			it.putExtra("from", "TransSubListActivity");
			it.putExtra("mTransSub", mTransSub);
			startActivity(it);
		}

	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
