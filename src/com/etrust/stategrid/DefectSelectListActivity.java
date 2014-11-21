package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.etrust.stategrid.adapter.DefectSelectAdapter;
import com.etrust.stategrid.bean.CheckItem;
import com.etrust.stategrid.bean.ItemCategory;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.JsonUtils;
import com.etrust.stategrid.view.ExListView;

public class DefectSelectListActivity extends Activity implements
		OnClickListener {
	private boolean mIsLoading;
	private boolean loadOver;
	private ExListView mListView;
	DefectSelectAdapter mAdapter;
	private DatabasesTransaction db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		db = DatabasesTransaction.getInstance(this);
		setContentView(R.layout.activity_defectselect_list);
		setupView();
	}

	private void setupView() {
		TextView title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("缺陷类目列表");
		findViewById(R.id.title_right).setOnClickListener(this);

		mListView = (ExListView) findViewById(R.id.list_view);
		mListView.setGroupIndicator(null);
		mAdapter = new DefectSelectAdapter(this);
		mListView.setAdapter(mAdapter);
		doGetData();
		mListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent();
				intent.putExtra("itemCategory",
						mAdapter.getGroup(groupPosition));
				intent.putExtra("checkItem",
						mAdapter.getChild(groupPosition, childPosition));
				setResult(RESULT_OK, intent);  
				finish();//结束之后会将结果传回From
				return false;
			}
		});

	}
	private void doGetData() {
		mIsLoading = true;
		loadOver = false;
		
		String querySql = "SELECT * FROM " + Constant.T_ItemCategory;
		mAdapter.data.clear();
		
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			ItemCategory it = new ItemCategory();
			 List<CheckItem> cl = new ArrayList<CheckItem>();
			 
			String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
			String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
			String all = cursor.getString(cursor.getColumnIndexOrThrow("all_item"));
			
			mAdapter.data.add(JsonUtils.getIitemCategory(all));
		}
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.title_right:
			finish();
			break;
		}
	}
}
