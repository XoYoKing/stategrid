package com.etrust.stategrid;

import org.json.JSONObject;

import com.etrust.stategrid.adapter.TaskReportHistoryAdapter;
import com.etrust.stategrid.bean.Attach;
import com.etrust.stategrid.bean.Defect;
import com.etrust.stategrid.bean.TempDefect;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TempDefectDetail extends Activity implements OnClickListener {
	private ListView task_detial_list;
	private TaskReportHistoryAdapter adapter;
	private DatabasesTransaction db;
	private TempDefect tempDefect=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = DatabasesTransaction.getInstance(this);
		setContentView(R.layout.activity_task_detail);
		tempDefect=(TempDefect) getIntent().getSerializableExtra("tempDefect");
		setupView();
	}
	private void setupView(){
		ImageButton task_detail_begin_btn = (ImageButton) findViewById(R.id.task_detail_begin_btn);
		task_detail_begin_btn.setVisibility(View.GONE);
		
		findViewById(R.id.title_right).setOnClickListener(this);
		TextView title_text = (TextView) this.findViewById(R.id.title_text);
		title_text.setText("缺陷信息");
		
		task_detial_list = (ListView) findViewById(R.id.task_detial_list);
		
		String[] lable = new String[] {"设备名称","缺陷类目", "缺陷名称", "缺陷等级", "处理方法", "缺陷描述",
				"创建人", "陪同人", "创建日期", "缺陷视频", "缺陷图片","缺陷语音" };
		
		String[] data = new String[12];
		for(int i=0;i<=11;i++){
			data[i]="";
		}
		if(tempDefect!=null){
			try{
				JSONObject json=new JSONObject(tempDefect.getContent());
				String itemFullName=json.getString("itemFullName");
				
				String[] dItems=new String[]{"","","",""};
				if(itemFullName!=null&&(itemFullName.length()>1)){
					dItems=itemFullName.split(",");
				}
				
				data[0]=json.getString("deviceName");
				data[1]=dItems[0];
				data[2]=dItems[1];
				int bugLevel=json.getInt("bugLevel");
				if(bugLevel==1){
					data[3]="一般缺陷";
				}else if(bugLevel==2){
					data[3]="重要缺陷";
				}else{
					data[3]="紧急缺陷";
				}
				int dealType=json.getInt("dealType");
				data[4]=((dealType==1)?"自行处理":"上报处理");
				data[5]=json.getString("description");
				data[6]=json.getString("creatorName");
				data[7]=json.getString("executorName");
				data[8]=tempDefect.getDateTime();
				data[9]=json.getString("videoCase");
				data[10]=json.getString("picCase");
				data[11]=json.getString("audioCase");
				if(data[9]==null){
					data[9]="";
				}
				if(data[10]==null){
					data[10]="";
				}
				if(data[11]==null){
					data[11]="";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
	}		
		
		
		adapter = new TaskReportHistoryAdapter(this, lable, data);
		task_detial_list.setAdapter(adapter);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.title_right:
			finish();
			break;
		}
	}
}
