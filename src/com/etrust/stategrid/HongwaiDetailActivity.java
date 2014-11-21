package com.etrust.stategrid;

import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.etrust.stategrid.adapter.ProDefectDetailAdapter;
import com.etrust.stategrid.bean.TempInfrared;
import com.etrust.stategrid.db.DatabasesTransaction;

//红外标准参考
public class HongwaiDetailActivity extends Activity implements OnClickListener {

	private ListView infrared_detial_list;
	private ProDefectDetailAdapter adapter;
	private TempInfrared temInf;
    private JSONObject json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		DatabasesTransaction.getInstance(this);
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
		findViewById(R.id.history_btn).setOnClickListener(this);//红外上报
		TextView title_text = (TextView) this.findViewById(R.id.title_text);
		title_text.setText("红外标准参考");

		infrared_detial_list = (ListView) findViewById(R.id.task_detial_list);
		String[] lable = new String[] {"设备类别","部位","热像特征","故障特征","处理建议","备注", "红外标准图普","I类缺陷标准","II类缺陷标准","III类缺陷标准" };

		String[] data = new String[10];
		for(int i=0;i<=9;i++){
			data[i]="";
		}
		try{
			data[0]=json.getString("leibie");
			data[1]=json.getString("buwei");
			data[2]=json.getString("rexiangtezheng");
			data[3]=json.getString("guzhangtezheng");
			data[4]=json.getString("chulijianyi");
			data[5]=json.getString("beizhu");
			data[6]=json.getString("picname");
			data[7]=json.getString("Ilei");
			data[8]=json.getString("IIlei");
			data[9]=json.getString("IIIlei");
			

			
			
		
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
		case R.id.history_btn:
			Intent intent=new Intent();
			intent.setClass(this, InfraredReportActivity.class);
			startActivity(intent);
		}
	}
}
