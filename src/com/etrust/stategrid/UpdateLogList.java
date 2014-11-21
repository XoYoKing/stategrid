package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

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
import com.etrust.stategrid.bean.TempInfrared;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

public class UpdateLogList extends Activity implements XListView.IXListViewListener,
		OnItemClickListener, OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
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


		title_text.setText("操作记录");
		searchUpdateLogList();
	
	}

	private void setupView() {

		title_text = (TextView) findViewById(R.id.title_text);
		findViewById(R.id.title_right).setOnClickListener(this);

		mListView = (XListView) findViewById(R.id.list_view);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);

		queryAdapter = new DataQueryAdapter(this, adaptData);

		mListView.setAdapter(queryAdapter);

	}

	private void searchUpdateLogList(){
		String querySql = "SELECT * FROM " + Constant.T_update_log+" order by datetime desc ";
		Cursor cursor = db.selectSql(querySql);
		adaptData.clear();
		
		int i=1;
		String rowNo="";
		while (cursor.moveToNext()) {
			String content=cursor.getString(cursor.getColumnIndexOrThrow("content"));
			String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
			
			if(i<10){
				rowNo="0"+i;
			}else{
				rowNo=""+i;
			}
			adaptData.add(rowNo+" ["+dateTime+"]"+content);
			i++;
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

	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
