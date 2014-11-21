package com.etrust.stategrid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.etrust.stategrid.adapter.DataQueryAdapter;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.bean.MainStation;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;

public class DataQueryFragment extends Fragment implements XListView.IXListViewListener, OnItemClickListener,
		OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private List<MainStation> msList;
	private DataQueryAdapter queryAdapter;
	private EditText search_edittext;

	private DatabasesTransaction db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = DatabasesTransaction.getInstance(getActivity());

		adaptData = new ArrayList<String>();
		msList = new ArrayList<MainStation>();
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

	private void doSearch(String key) {
		String querySql = "SELECT * FROM " + Constant.T_Device + " WHERE name LIKE '%" + key + "%' order by transsub";
		Cursor cursor = db.selectSql(querySql);

		if (!cursor.moveToFirst()) {
			Toast.makeText(getActivity(), "没有结果,请更换关键字", 2000).show();
			return;
		} else {
			search_edittext.getEditableText().clear();
			List<Device> dList = new ArrayList<Device>();

			while (cursor.moveToNext()) {
				Device temp = new Device();
				temp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
				temp.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
				temp.url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
				temp.path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
				temp.transsub = cursor.getString(cursor.getColumnIndexOrThrow("transsub"));
				temp.defectIds = cursor.getString(cursor.getColumnIndexOrThrow("defectIds"));
				dList.add(temp);
			}
			Intent it = new Intent(getActivity(), DeviceListActivity.class);
			it.putExtra("searchList", (Serializable) dList);
			it.putExtra("from", "search");
			startActivity(it);
		}
		cursor.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dataquery_layout, container, false);

		search_edittext = (EditText) rootView.findViewById(R.id.search_edittext);
		rootView.findViewById(R.id.search_button).setOnClickListener(this);

		mListView = (XListView) rootView.findViewById(R.id.list_view);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);

		queryAdapter = new DataQueryAdapter(getActivity(), adaptData);

		doGetData();
		mListView.setAdapter(queryAdapter);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);

		return rootView;
	}

	private void loadOver() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.search_button:
			String searchStr = search_edittext.getEditableText().toString();
			if (!searchStr.isEmpty()) {
				doSearch(searchStr);
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int rightPosion = (int) arg3;

		MainStation mStation = msList.get(rightPosion);
		Intent it = new Intent();
		it.setClass(getActivity(), TransSubListActivity.class);
		it.putExtra("mStation", mStation);
		it.putExtra("from", DataQueryFragment.class.getSimpleName());
		getActivity().startActivity(it);
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
