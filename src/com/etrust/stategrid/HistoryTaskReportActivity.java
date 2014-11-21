package com.etrust.stategrid;

import com.etrust.stategrid.adapter.TaskReportHistoryAdapter;
import com.etrust.stategrid.bean.Attach;
import com.etrust.stategrid.bean.Defect;
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

public class HistoryTaskReportActivity extends Activity implements OnClickListener {
	private Defect defect;
	
	private ListView task_detial_list;
	private TaskReportHistoryAdapter adapter;
	private DatabasesTransaction db;
	private String[] soundType={"amr","mp3","wma"};
	private String[] videoType={"3gp","mp4","avi","rmvb","wmv"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = DatabasesTransaction.getInstance(this);
		setContentView(R.layout.activity_task_detail);
		defect = (Defect) getIntent().getSerializableExtra("defect");
		setupView();
	}
	private void setupView(){
		ImageButton task_detail_begin_btn = (ImageButton) findViewById(R.id.task_detail_begin_btn);
		task_detail_begin_btn.setVisibility(View.GONE);
		findViewById(R.id.title_right).setOnClickListener(this);
		TextView title_text = (TextView) this.findViewById(R.id.title_text);
		title_text.setText("历史缺陷记录");
		
		task_detial_list = (ListView) findViewById(R.id.task_detial_list);
		
		String video = "";
		String audio = "";
		StringBuffer pic = new StringBuffer();
		
//		List<Attach> attach = new ArrayList<Attach>();
		
		String querySql = "SELECT * FROM " + Constant.T_Attach 
				+ "  WHERE id IN ("+defect.attachIds+")";
		Cursor cursor = db.selectSql(querySql);
		
		while (cursor.moveToNext()) {
			Attach temp = new Attach();
			temp.defect = cursor.getString(cursor.getColumnIndexOrThrow("defect"));
			temp.url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
			temp.realName = cursor.getString(cursor.getColumnIndexOrThrow("realName"));
			temp.suffux = cursor.getString(cursor.getColumnIndexOrThrow("suffux"));
			temp.path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
			
			if(checkIsSound(temp.suffux)){
				audio = temp.path;
			}else if(checkIsVideo(temp.suffux)){
				video = temp.path;
			}else{
				pic.append(temp.path);
				pic.append(",");
			}
//			attach.add(temp);
		}
		
		String[] lable = new String[] { "设备名称","缺陷名称", "缺陷项目", "缺陷等级", "处理方法", "缺陷描述",
				"创建人", "陪同人", "创建日期", "缺陷视频", "缺陷图片","缺陷语音" };
		
		String[] data = new String[] {defect.device,defect.itemCategory,defect.item,defect.bugLevel,
				defect.dealType,defect.description,defect.creatorid,defect.executorid,
				defect.createDate,video,pic.toString(),audio};
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
	//循环格式查询
	public boolean checkIsSound(String type){
		type=type.toLowerCase();
		for(String ele:soundType){
			if(ele.equals(type)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIsVideo(String type){
		type=type.toLowerCase();
		for(String ele:videoType){
			if(ele.equals(type)){
				return true;
			}
		}
		return false;
	}
}
