package com.etrust.stategrid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;

import com.etrust.stategrid.adapter.TaskListAdapter;
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.db.TaskDao;
import com.etrust.stategrid.utils.Utils;
import com.etrust.stategrid.view.XListView;
//任务列表
public class TaskListFragment extends Fragment implements
		XListView.IXListViewListener, OnItemClickListener {

	private XListView mListView;
	private TaskListAdapter taskAdapter;
	private List<TaskBean> dataList;
	private UserBean user;
	private DatabasesTransaction db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App app = (App) getActivity().getApplication();
		user = app.getCurrentUserBean();
		db = DatabasesTransaction.getInstance(getActivity());
		   //doGetData();
	}
	public void onStart() {  
	        super.onStart();  
	        doGetData();
	    } 
	  public void onResume() {  
	        super.onResume();  
	        doGetData();
	    } 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tasklist_layout,
				container, false);

		ImageButton com_btn = (ImageButton) rootView.findViewById(R.id.commit_btn);
		com_btn.setVisibility(View.GONE);
		mListView = (XListView) rootView.findViewById(R.id.list_view);

		dataList = new ArrayList<TaskBean>();
		taskAdapter = new TaskListAdapter(this.getActivity(), dataList);
		mListView.setAdapter(taskAdapter);

		mListView.setXListViewListener(this);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setOnItemClickListener(this);

		doGetData();
		return rootView;
	}

	private void doGetData() {
		if(dataList==null){
			dataList=new ArrayList<TaskBean>();
		}
		dataList.clear();
		dataList=TaskDao.getAllTaskBean(user.userid, db);
		taskAdapter.setData(dataList);
		taskAdapter.notifyDataSetChanged();
		loadOver();
	}
	
	private void loadOver() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TaskBean tBean = (TaskBean) arg0.getAdapter().getItem(arg2);
		TaskBean fullTb=TaskDao.getTaskById(tBean.getId(),db);
		System.out.println("taskdata-------"+fullTb);
		Intent it = new Intent();
		// 0未开始1进行中2已暂停3已结束
		if (tBean.status != 1) {//如果不是进行中打开详情页面
			it.setClass(getActivity(), TaskDetailActivity.class);
		}else{
			it.setClass(getActivity(), TaskingMapActivity.class);
		}
		it.putExtra("taskData", fullTb);
		getActivity().startActivity(it);
	}

	@Override
	public void onRefresh() {
		try{
			doGetData();
			mListView.setRefreshTime(Utils.getYMDHMStime(new Date()));
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void onLoadMore() {
	}
}
