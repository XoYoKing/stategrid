package com.etrust.stategrid;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.etrust.stategrid.adapter.TabViewAdapter;
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.google.gson.Gson;

public class TaskDetailActivity extends Activity implements OnClickListener {

	private ListView task_detial_list;
	private TabViewAdapter adapter;
	private TaskBean tBean;
	private DatabasesTransaction db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		db = DatabasesTransaction.getInstance(this);

		tBean = (TaskBean) getIntent().getSerializableExtra("taskData");
		setupView();
	}

	private void setupView() {
		ImageButton task_detail_begin_btn = (ImageButton) findViewById(R.id.task_detail_begin_btn);
		// 0未开始1进行中2已暂停3已结束
		if (tBean.status == 0 || tBean.status == 2) {
			task_detail_begin_btn.setVisibility(View.VISIBLE);
			task_detail_begin_btn.setOnClickListener(this);
		} else {
			task_detail_begin_btn.setVisibility(View.GONE);
		}

		task_detial_list = (ListView) findViewById(R.id.task_detial_list);
		String[] lable = new String[] { "任务名称", "任务状态", "负责人", "陪同人", "班组ID", "巡检类型", "开始日期", "结束日期", "任务说明" };

		String[] data = new String[] { tBean.name, tBean.getStatusStr(), tBean.resname, tBean.accname,
				tBean.groupid + "", tBean.getTypeStr(), tBean.begintime, tBean.enddate, tBean.instruction };
		adapter = new TabViewAdapter(this, lable, data);
		task_detial_list.setAdapter(adapter);

		findViewById(R.id.title_right).setOnClickListener(this);
	}

	private void updateTask(int status) {
		tBean.status = status;
		ContentValues values = new ContentValues();
		values.put("uploadstatus", Constant.Upload_Task_Status_Change);
		values.put("content", new Gson().toJson(tBean));
		int i = db.updateSql(Constant.T_Task, values, "id='" + tBean.id + "'");

		if (i > 0) {
			Intent it = new Intent(TaskDetailActivity.this, TaskingMapActivity.class);
			it.putExtra("taskData", tBean);
			startActivity(it);
			finish();
		} else {
			Toast.makeText(TaskDetailActivity.this, "任务开始失败", 2000).show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.task_detail_begin_btn:
			updateTask(1);
			break;
		case R.id.title_right:
			finish();
			break;
		}
	}
}
