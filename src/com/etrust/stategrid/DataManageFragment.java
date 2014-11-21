package com.etrust.stategrid;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.etrust.stategrid.adapter.DataQueryAdapter;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.CallService;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.HttpClientHelper;
import com.etrust.stategrid.view.XListView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;

public class DataManageFragment extends Fragment implements
		XListView.IXListViewListener, OnItemClickListener, OnClickListener {

	private XListView mListView;
	private DataQueryAdapter queryAdapter;
	private DatabasesTransaction db;
	private ImageButton commit_btn;
	private ArrayList<String> data;
	Dialog dialog;

	boolean isTaskOver = false;
	boolean netError = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new Dialog(getActivity(), R.style.Dialog);
		dialog.setContentView(R.layout.loading);
		db = DatabasesTransaction.getInstance(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tasklist_layout,
				container, false);
		mListView = (XListView) rootView.findViewById(R.id.list_view);
		commit_btn = (ImageButton) rootView.findViewById(R.id.commit_btn);
		commit_btn.setOnClickListener(this);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		data = new ArrayList<String>();
		getData();
		queryAdapter = new DataQueryAdapter(getActivity(), data);
		mListView.setAdapter(queryAdapter);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
		return rootView;
	}

	public void getData() {
		if (data == null) {
			data = new ArrayList<String>();
		}
		data.clear();
		int coordCount = db.getCount(Constant.T_Coordinate, "");
		int defectCount = db.getCount(Constant.T_temp_Defect, "");
		int infraredCount = db.getCount(Constant.T_temp_ProduceDevice, "");
		int logCount = db.getCount(Constant.T_update_log, "");
		data.add("设备缺陷记录" + defectCount + "条");
		data.add("设施缺陷记录" + infraredCount + "条");
		data.add("坐标更新" + coordCount + "条");
		data.add("操作记录" + logCount + "条");
		data.add("技术资料");
		// data.add("红外标准参考(先参考后上报)");
		// data.add("无线温度管理系统");
		// data.add("红外历史记录");
		// data.add("红外缺陷上报(直接上报)");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int rightPosion = (int) arg3;
		Intent it = new Intent();
		switch (rightPosion) {
		case 0:
			it.setClass(getActivity(), TempDfectList.class);
			startActivity(it);
			break;
		case 2:
			it.setClass(getActivity(), TempCoordList.class);
			startActivity(it);
			break;
		case 3:
			it.setClass(getActivity(), UpdateLogList.class);
			startActivity(it);
			break;
		case 1:
			it.setClass(getActivity(), ProduceList.class);
			startActivity(it);
			break;
		case 4:
			it.setClass(getActivity(), TreeZiLiaoActivity.class);
			startActivity(it);
			break;
		// case 0:
		// it.setClass(getActivity(), Hongwailist.class);
		// startActivity(it);
		// break;
		// case 1:
		// it.setClass(getActivity(), WebviewCeWen.class);
		// startActivity(it);
		// break;
		//
		// case 2:
		// it.setClass(getActivity(), InfraredHistoryListActivity.class);
		// startActivity(it);
		// break;
		// case 3:
		// it.setClass(getActivity(), InfraredReportActivity.class);
		// startActivity(it);
		// break;

		}
	}

	@Override
	public void onRefresh() {
		try {
			getData();
			queryAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onLoadMore() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commit_btn:
			if (!HttpClientHelper.isNetworkConnected(getActivity())) {
				Toast.makeText(getActivity(), "当前网络不可用，请连网后再提交！", 2000).show();
				return;
			}
			commitData();// 联网上传数据
			// onRefresh();
			break;
		}
	}

	public void commitData() {
		dialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				int seconds = 0;
				while (true) {
					try {
						Thread.sleep(500);
						seconds++;
					} catch (Exception e) {

					}
					int logCount = db.getCount(Constant.T_update_log, "");
					if (logCount == 0) {
						dialog.dismiss();
						break;
					}
					if (seconds >= 30) {
						dialog.dismiss();
						break;
					}
				}
			}
		}).start();
		Intent it = new Intent();
		it.setClass(getActivity(), DataCommit.class);
		startActivity(it);
	}
}
