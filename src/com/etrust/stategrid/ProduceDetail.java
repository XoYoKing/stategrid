package com.etrust.stategrid;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.etrust.stategrid.adapter.InfraredHistoryAdapter;
import com.etrust.stategrid.adapter.ProDefectDetailAdapter;
import com.etrust.stategrid.adapter.ProduceAdapter;
import com.etrust.stategrid.bean.TempInfrared;
import com.etrust.stategrid.db.DatabasesTransaction;


public class ProduceDetail extends Activity implements OnClickListener {

	private ListView infrared_detial_list;
	private ProDefectDetailAdapter adapter;
	// take pic
	private DatabasesTransaction db;
    private TempInfrared temInf;
    private JSONObject json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		db = DatabasesTransaction.getInstance(this);
		temInf=(TempInfrared)getIntent().getSerializableExtra("tempInfrared");
		try{
			json=new JSONObject(temInf.getContent());
		}catch(Exception e){
			
		}
		
		setupView();
	}

	private void setupView() {
		ImageButton task_detail_begin_btn = (ImageButton) findViewById(R.id.task_detail_begin_btn);
		task_detail_begin_btn.setVisibility(View.GONE);
		findViewById(R.id.title_right).setOnClickListener(this);
		TextView title_text = (TextView) this.findViewById(R.id.title_text);
		title_text.setText("生产缺陷");

		infrared_detial_list = (ListView) findViewById(R.id.task_detial_list);
		String[] lable = new String[] {"设备名称","缺陷等级","缺陷分类","缺陷描述","上报人", "上报时间","缺陷照片" };

		String[] data = new String[7];
		for(int i=0;i<=6;i++){
			data[i]="";
		}
		try{
			data[0]=json.getString("name");
			//data[1]=json.getString("dengji");
			int bugLevel=json.getInt("level");
			
			if(bugLevel==1){
				data[1]="一般缺陷";
			}else if(bugLevel==2){
				data[1]="严重缺陷";
			}else{
				data[1]="危急缺陷";
			}
			//data[2]=json.getString("fenlei");
			int bugfenlei=json.getInt("defcate");
			// String[] fenlei= new String[] { "房屋设施","通风设施", "上下水系统", 
					// "空调系统","照明系统","围墙大门","电缆沟","绿化","其他"};
			if(bugfenlei==1){
				data[2]="房屋设施";
			}else if(bugfenlei==2){
				data[2]="通风设施";
			}else if(bugfenlei==3){
				data[2]="上下水系统";
			}
			else if(bugfenlei==4){
				data[2]="空调系统";
			}else if(bugfenlei==5){
				data[2]="照明系统";
			}
			else if(bugfenlei==6){
				data[2]="围墙大门";
			}else if(bugfenlei==7){
				data[2]="电缆沟";
			}
			else if(bugfenlei==8){
				data[2]="绿化";
			}else {
				data[2]="其他";
			}
			
			
			data[3]=json.getString("descrip");
			data[4]=json.getString("userName");
			data[5]=json.getString("createtime");
			data[6]=json.getString("picname");
		}catch(Exception e){
			e.printStackTrace();
		}
		adapter = new ProDefectDetailAdapter(this, lable, data);
		infrared_detial_list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void updateAdapter(int posion, String data) {
		adapter.data[posion] = data;
		adapter.notifyDataSetChanged();
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
