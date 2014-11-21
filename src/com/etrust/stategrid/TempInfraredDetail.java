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
import com.etrust.stategrid.bean.TempInfrared;
import com.etrust.stategrid.db.DatabasesTransaction;

public class TempInfraredDetail extends Activity implements OnClickListener {

	private ListView infrared_detial_list;
	private InfraredHistoryAdapter adapter;
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
		title_text.setText("红外信息");

		infrared_detial_list = (ListView) findViewById(R.id.task_detial_list);
		String[] lable = new String[] { "设备名称", "电压等级", "仪器型号", "负荷电流", "额定电流",
				"测试距离", "湿度", "环境温度", "检测时间", "最高温度点", "第一点温度", "第二点温度",
				"诊断和缺陷分析", "处理意见", "备注", "红外照片" };

		String[] data = new String[16];
		for(int i=0;i<=15;i++){
			data[i]="";
		}
		try{
			for(int i=0;i<=14;i++){
				data[i]=json.getString("attr"+(i+1));
			}
			data[15]=json.getString("picname");
		}catch(Exception e){
			e.printStackTrace();
		}
		adapter = new InfraredHistoryAdapter(this, lable, data);
		infrared_detial_list.setAdapter(adapter);
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
