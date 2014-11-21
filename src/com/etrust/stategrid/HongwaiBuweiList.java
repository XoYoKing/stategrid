package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
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
import com.etrust.stategrid.bean.TempInfrared;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

public class HongwaiBuweiList extends Activity implements XListView.IXListViewListener,
		OnItemClickListener, OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private List<TempInfrared> dList;
	private DataQueryAdapter queryAdapter;
	private TextView title_text;
	private DatabasesTransaction db;
	String first;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = DatabasesTransaction.getInstance(this);

		setContentView(R.layout.activity_transsub_list);
		adaptData = new ArrayList<String>();//实例化

		setupView();
		//取得启动该Activity的Intent对象   
        Intent intent =getIntent();   
        /*取出Intent中附加的数据*/  
        first = intent.getStringExtra("tempInfrared"); 

		title_text.setText(first+"部位参考标准");
		dList = new ArrayList<TempInfrared>();
		tempInfraredList();
	
	}

	private void setupView() {

		title_text = (TextView) findViewById(R.id.title_text);
		findViewById(R.id.title_right).setOnClickListener(this);

		mListView = (XListView) findViewById(R.id.list_view);
		mListView.setPullRefreshEnable(false);//禁止上拉刷新
		mListView.setPullLoadEnable(false);//禁止下拉加载

		queryAdapter = new DataQueryAdapter(this, adaptData);

		mListView.setAdapter(queryAdapter);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);

	}

	private void tempInfraredList(){
		String querySql = "SELECT * FROM " + Constant.T_hongwai_device+" order by datetime desc ";
		String sql="select content from "+Constant.T_hongwai_device+" where leibieid='"+first+"' order by datetime asc";//查红外详情表
		Cursor cursor = db.selectSql(sql);
		adaptData.clear();
		dList.clear();

		
		int i=1;
		String rowNo="";
		while (cursor.moveToNext()) {
			TempInfrared temp = new TempInfrared();
			//temp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));//表中的字段
			//temp.leibieid=cursor.getString(cursor.getColumnIndexOrThrow("leibieid"));
			temp.content = cursor.getString(cursor.getColumnIndexOrThrow("content"));//表中的字段
			System.out.println("");
			String name="";
			try{
				JSONObject json=new JSONObject(temp.content);
				System.out.println("22222222222222"+temp.content);
				name=json.getString("buwei");
			}catch(Exception e){
				
			}
			if(i<10){
				rowNo="0"+i;
			}else{
				rowNo=""+i;
			}
			//temp.dateTime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
			adaptData.add(name);
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
		it.setClass(this, HongwaiDetailActivity.class);
		it.putExtra("tempInfrared", infrared);
		startActivity(it);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
