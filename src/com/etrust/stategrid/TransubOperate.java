package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.etrust.stategrid.bean.TransSub;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

import android.os.Bundle;

public class TransubOperate extends Activity implements XListView.IXListViewListener,
		OnItemClickListener, OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private DataQueryAdapter queryAdapter;
	private TextView title_text;
	private int tsid=0;
	private TransSub mTransSub;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transsub_list);
		adaptData = new ArrayList<String>();
		setupView();
		tsid = getIntent().getIntExtra("mTransSubID", 0);
		mTransSub = (TransSub) getIntent().getSerializableExtra("mTransSub");
	}

	private void setupView() {
		title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("数据查询");
		findViewById(R.id.title_right).setOnClickListener(this);

		mListView = (XListView) findViewById(R.id.list_view);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		mListView.setOnItemClickListener(this);
		
		adaptData=new ArrayList<String>();
		adaptData.add("设备列表");
		adaptData.add("缺陷查询");
		adaptData.add("设施缺陷上报");
//		adaptData.add("红外参考标准上报");

		queryAdapter = new DataQueryAdapter(this, adaptData);
		mListView.setAdapter(queryAdapter);
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
				int rightPosition = (int) arg3;
				Intent it = new Intent();
				it.putExtra("mTransSubID",tsid);
				it.putExtra("mtsname",mTransSub.name);
				switch(rightPosition){
						case 0:
							it.setClass(this, TreeActivity.class);
							it.putExtra("from", "TransSubListActivity");
							startActivity(it);
						    break;
						case 1:
							it.setClass(this, DefectSearch.class);
							it.putExtra("from", "TransSubListActivity");
							startActivity(it);
						    break;
						case 2:
							it.setClass(this, ProduceReportActivity.class);
							it.putExtra("from", "TransSubListActivity");
							it.putExtra("mTransSub",mTransSub);
							startActivity(it);
						    break;
//						case 3:
//							it.setClass(this, HongwaiReportActivity.class);
//							it.putExtra("from", "TransSubListActivity");
//							//it.putExtra("mTransSub",mTransSub);
//							startActivity(it);
//						    break;
				}

	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
