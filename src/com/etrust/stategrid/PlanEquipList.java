package com.etrust.stategrid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import com.etrust.stategrid.utils.DateUtils;

import com.etrust.stategrid.adapter.DataQueryAdapter;
import com.etrust.stategrid.adapter.PEquipAdapter;
import com.etrust.stategrid.adapter.PlanEquipHolder;
import com.etrust.stategrid.bean.PlanEquip;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.db.TaskDao;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.view.XListView;
import android.content.ContentValues;
import android.content.Intent;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.etrust.stategrid.bean.TaskBean;
public class PlanEquipList  extends Activity implements
OnItemClickListener, OnClickListener {
	
	
	private ListView mListView;
	private List<String> adaptData;
	private List<Map<String,String>> dataMap;
	private List<PlanEquip> dList;
	private DataQueryAdapter queryAdapter;
	private PEquipAdapter adapter;
	private TextView title_text;
	private int planTaskId;
	private int planTsid;
	private DatabasesTransaction db;
	private PlanEquip currentEquip;
	private AlertDialog dlg=null;
	private EditText desEt;
	private View dialogView;
	private String commonDescrip="";
	private CheckBox checkBox;
	private Button cmtbtn;
	private Map<Integer,PlanEquipHolder> holderMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_equip_layout);
		checkBox=(CheckBox)findViewById(R.id.ll_plan_equip_cb);
		cmtbtn=(Button)findViewById(R.id.ll_plan_equip_btn);
		adaptData = new ArrayList<String>();
		dataMap=new ArrayList<Map<String,String>>();
		dList = new ArrayList<PlanEquip>();
		planTaskId=getIntent().getIntExtra("planTaskId", 0);
		planTsid=getIntent().getIntExtra("planTsId", 0);
	    db = DatabasesTransaction.getInstance(this);
	    checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(checkBox.isChecked()){
					 changeCheck(true);
				}else{
					 changeCheck(false);
				}
			}
	    	
	    });
	    cmtbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				findChecked();
			}
	    });	
		setupView();
    }
	
	private void setupView() {
		title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("重点巡视设备");
		findViewById(R.id.title_right).setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.plan_equip_list);
		planEquipList();
		//queryAdapter = new DataQueryAdapter(this, adaptData);
		//mListView.setAdapter(queryAdapter);
		
		adapter=new PEquipAdapter(this,dataMap);
		mListView.setAdapter(adapter);
		holderMap=adapter.holderMap;
		mListView.setOnItemClickListener(this);

	}	
	public void planEquipList(){
		    TaskBean tBean=TaskDao.getTaskById(planTaskId, db);
		    tBean.setOrbitList(null);
		    tBean.setEquipment(null);
		    dList.clear();
		    adaptData.clear();
		    dataMap.clear();
			List<PlanEquip> allEquip=tBean.getEquipList();
			int i=0;
			for(PlanEquip planEquip:allEquip){
				if(planEquip.getTsid()==planTsid){
					dList.add(planEquip);
					Map<String,String> ldata=new HashMap<String,String>();
					ldata.put("checked","0");
					String[] data=TaskDao.getPlanEquipById(planEquip.getId(), db);
					if(data[1]!=null){
						//
						planEquip.setDescrip(data[0]);
						//adaptData.add(planEquip.getEquipName()+"[已巡检"+data[1]+"]");
						ldata.put("title",(i+1)+"  "+planEquip.getEquipName()+"[已巡检"+data[1]+"]");
						ldata.put("content",data[0]);
					}else{
						//adaptData.add(planEquip.getEquipName());
						ldata.put("title",(i+1)+"  "+planEquip.getEquipName());
						ldata.put("content","");
					}
					dataMap.add(ldata);
					i++;
				}
			}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.title_right:
			finish();
			break;
		}		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		    int rightPosion = (int) arg3;
		    currentEquip=dList.get(rightPosion);
		    Intent it = new Intent(PlanEquipList.this, PlanEquipReportActivity.class);
		    it.putExtra("currentEquip",dList.get(rightPosion));
			startActivity(it);
	}

	
	public void createDialog(){
		commonDescrip="";
		LayoutInflater factory=LayoutInflater.from(this);
		dialogView=factory.inflate(R.layout.plan_equip_descrip, null);
		desEt=(EditText)dialogView.findViewById(R.id.ped_des_et);
		AlertDialog.Builder builder=new AlertDialog.Builder(PlanEquipList.this);
		builder.setTitle("巡检描述")
		       .setView(dialogView)
		       .setPositiveButton("保存",
				        new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								// TODO Auto-generated method stub
								commonDescrip=desEt.getEditableText().toString().trim();
								if("".equals(commonDescrip)){
									Toast.makeText(PlanEquipList.this, "巡检描述不能为空", 1500).show();
									return;
								}
								commitData();
						}
				}).setNeutralButton("取消",
						        new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int whichButton) {
										// TODO Auto-generated method stub
										dlg.dismiss();
								}
				}
		);
		dlg=builder.create();
		dlg.show();
	}
	/**
	 * 提交巡检数据
	 * */
	public void commitData(){
		String datetime=DateUtils.getCurrentDateTime();
		for(int i=0;i<dataMap.size();i++){
			Map<String,String> itemData=dataMap.get(i);
			String content=itemData.get("content");
			PlanEquip equip=dList.get(i);
			db.deleteData(Constant.T_PlanEquip, "id=?", new String[]{""+equip.getId()});
			ContentValues values = new ContentValues();
			values.put("id", equip.getId());
			values.put("content",content);
			if("1".equals(itemData.get("checked"))&&!"".equals(commonDescrip)){
					values.put("content",commonDescrip);
					itemData.put("content", commonDescrip);
			}
			values.put("datetime",datetime);
			db.saveSql(Constant.T_PlanEquip, values);
			
		}
		Toast.makeText(PlanEquipList.this, "提交成功", 1000).show();
		commonDescrip="";
		db.saveUpdateLog("PlanEquipXj","重点设备巡检");
		adapter.notifyDataSetChanged();
	}
	public void findChecked(){
		boolean isChecked=false;
		for(int i=0;i<dataMap.size();i++){
			if("1".equals((dataMap.get(i)).get("checked"))){
				isChecked=true;
				break;
			}
		}
		if(isChecked&&"".equals(commonDescrip)){
			createDialog();
		}else{
			commitData();
		}
	}
	public void changeCheck(boolean isChecked){
		for(int i=0;i<dataMap.size();i++){
			if(isChecked){
				dataMap.get(i).put("checked", "1");
			}else{
				dataMap.get(i).put("checked", "0");
			}
			
		}
		adapter.notifyDataSetChanged();
	}

}
