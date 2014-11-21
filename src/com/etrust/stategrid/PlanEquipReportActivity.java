package com.etrust.stategrid;


import org.json.JSONObject;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.etrust.stategrid.adapter.PlanEquipAdapter;
import com.etrust.stategrid.bean.PlanEquip;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.db.TaskDao;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;

public class PlanEquipReportActivity extends Activity implements OnClickListener {

	private ListView task_detial_list;
	private PlanEquipAdapter adapter;
	private DatabasesTransaction db;
	private PlanEquip currentEquip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_produce_report);
		findViewById(R.id.btn_pro_defect).setOnClickListener(cmtListener);
		db = DatabasesTransaction.getInstance(this);
		currentEquip=(PlanEquip)getIntent().getSerializableExtra("currentEquip");
		setupView();
	}

	private void setupView() {
		ImageButton cmt_btn_produce = (ImageButton)this.findViewById(R.id.btn_pro_defect);
		cmt_btn_produce.setImageResource(R.drawable.commit_img);
		ImageView title_icon = (ImageView) this.findViewById(R.id.pbt_title_icon);
		title_icon.setImageResource(R.drawable.upload_icon);

		TextView title_text = (TextView) findViewById(R.id.title_text_produce);
		title_text.setText(currentEquip.getEquipName());
		findViewById(R.id.pbt_title_right).setOnClickListener(this);

		task_detial_list = (ListView) findViewById(R.id.pro_defect_detial_list);
		String[] lable = new String[] {"巡视描述"};
		
		String[] dataEquip=TaskDao.getPlanEquipById(currentEquip.getId(), db);
		if(dataEquip[1]!=null){
			currentEquip.setDescrip(dataEquip[0]);
			//缺陷描述
		}
		String[] data = new String[] {currentEquip.getDescrip()};
		adapter = new PlanEquipAdapter(this, lable, data);
		task_detial_list.setAdapter(adapter);
	}

	public void updateAdapter(int posion, String data) {
		adapter.data[posion] = data;
		adapter.notifyDataSetChanged();
	}
	
	
	
	public View.OnClickListener cmtListener=new View.OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			    String[] ndata=adapter.data;
			    if("".equals(ndata[0].trim())){
			    	Toast.makeText(PlanEquipReportActivity.this, "巡视描述不能为空", 1000).show();
			    	return;
			    }
                ContentValues values = new ContentValues();
                values.put("id", currentEquip.getId());
    			values.put("content",ndata[0]);
    			values.put("datetime",DateUtils.getCurrentDateTime());
    			db.deleteData(Constant.T_PlanEquip, "id=?", new String[]{currentEquip.getId()+""});
    			long result=db.saveSql(Constant.T_PlanEquip, values);
    			if(result>0){
    				Toast.makeText(PlanEquipReportActivity.this, "保存成功", 1000).show();
    				db.saveUpdateLog("PlanEquipXj", currentEquip.getEquipName()+"巡检");
    				finish();
    			}else{
    				Toast.makeText(PlanEquipReportActivity.this, "保存失败", 1000).show();
    			}			
		}
		
	};
	
	
	
	
	
//本地输入数据保存为json
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pbt_title_right:
			finish();
			break;
	
		}
	}
}
