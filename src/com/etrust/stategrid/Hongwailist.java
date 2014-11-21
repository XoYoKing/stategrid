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

public class Hongwailist extends Activity implements XListView.IXListViewListener,
		OnItemClickListener, OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private List<TempInfrared> dList;
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


		title_text.setText("红外标准列表");
		dList = new ArrayList<TempInfrared>();
		tempInfraredList();
	
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

	private void tempInfraredList(){
		String querySql = "SELECT * FROM " + Constant.T_device_leibie+" order by datetime desc ";
		Cursor cursor = db.selectSql(querySql);
		adaptData.clear();
		dList.clear();

		
		int i=1;
		String rowNo="";
		while (cursor.moveToNext()) {
			TempInfrared temp = new TempInfrared();
			//temp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
			temp.content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
			String name="";
			try{
				JSONObject json=new JSONObject(temp.content);
				name=json.getString("leibie");
			}catch(Exception e){
				
			}
			if(i<10){
				rowNo="0"+i;
			}else{
				rowNo=""+i;
			}
			temp.dateTime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
			adaptData.add(rowNo+" ["+temp.content+"]");
			dList.add(temp);
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
		int rightPosion = (int) arg3;
		TempInfrared infrared = dList.get(rightPosion);
		Intent it = new Intent();
		it.setClass(this, HongwaiBuweiList.class);
		it.putExtra("tempInfrared", infrared.content);
		startActivity(it);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
