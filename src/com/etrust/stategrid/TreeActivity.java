package com.etrust.stategrid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.etrust.stategrid.adapter.Node;
import com.etrust.stategrid.adapter.TreeAdapter;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.widget.ListAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.database.Cursor;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
public class TreeActivity  extends Activity implements OnItemClickListener,OnClickListener{
	ListView code_list;
	List<Node> equipNodes;
	private String[] equipNames;
	private Node equipNode;
	TreeActivity oThis = this;
	TreeAdapter ta;
	private DatabasesTransaction db;
	private String from;
	private Button searchBtn;
	private EditText keywordEdit;
	private int tsid;
	private boolean isSearch=false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        from=getIntent().getCharSequenceExtra("from").toString();
        setContentView(R.layout.activity_tree);
        
		TextView title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("设备目录");        
		findViewById(R.id.title_right).setOnClickListener(this);
		equipNodes=new ArrayList<Node>();
        code_list = (ListView)findViewById(R.id.code_list);
        code_list.setOnItemClickListener(this);
        db = DatabasesTransaction.getInstance(this);
        tsid = getIntent().getIntExtra("mTransSubID", 0);
        equipNode=null;
        
        
        searchBtn=(Button)findViewById(R.id.tree_search_btn);
        //查询按钮
        searchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchEquip();
			}
		});
        keywordEdit=(EditText)findViewById(R.id.tree_edittext);
        
        setPreson(tsid);
    }	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// 这句话写在最后面
		Node node=(Node)ta.getItem(position);
		((TreeAdapter)parent.getAdapter()).ExpandOrCollapse(position);
		String nodeValue=node.getValue();
		if(node.getIsParent()==0&&node.isLeaf()){
			searchEquipment(Integer.parseInt(nodeValue));
			equipNode=null;
			showEquipment();
		}
	}
	
	public void dealRequest(Node node){
		String equipId=node.getValue();
		Device device=getDeviceById(equipId);
		if("TaskReportAdapter".equals(from)){
			    //如果来自缺陷上传
				Intent it = new Intent();
				it.putExtra("defectDevice", device);
				setResult(RESULT_OK, it); //传数据必须要finish
				finish();//结束之后会将结果传回From
		}else if("ProDefectDetailAdapter".equals(from)){//本页可向InfraredReportAdapter返回设备ID和名字
			Intent it = new Intent();
			it.putExtra("defectDevice",device);
			setResult(RESULT_OK, it); 
			finish();//结束之后会将结果传回 
	}
		else if("TransSubListActivity".equals(from)){
			    //进入设备详情，需要查询设备详情
			Intent it = new Intent();
			it.setClass(this, DeviceDetailActivity.class);
			it.putExtra("mDevice", device);
			startActivity(it);
		}else if("TaskReportActivity".equals(from)){
			Intent it = new Intent();
			it.setClass(this, DefectHistoryListActivity.class);
			it.putExtra("mDevice",device);
			startActivity(it);
		}else if("DefectSearch".equals(from)){
			Intent it = new Intent();
			it.putExtra("defectDevice",device);
			setResult(RESULT_OK, it);
			finish();//结束之后会将结果传回From
		}
    }	
	
	
	// 设置节点,可以通过循环或递归方式添加节点
	private void setPreson(int tsid){
		// 创建根节点
		
		String tsname="飞跃运维站";
		String rootId="0";
		
		String querySql="select id,name,parent_id,is_parent from "
		        +Constant.T_equip_Content+" where ts_id="+tsid+" and parent_id=0";
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			rootId=cursor.getString(0);
			tsname=cursor.getString(1);
		}
		cursor.close();
		
		
        Node root = new Node(tsname,rootId);
        root.setIcon(R.drawable.icon_department);//设置图标
        setEquipContent(root);
		
		
		ta= new TreeAdapter(oThis,root);
		// 设置整个树是否显示复选框
		ta.setCheckBox(false);
		
	    // 设置展开和折叠时图标
		ta.setExpandedCollapsedIcon(R.drawable.tree_ex, R.drawable.tree_ec);
		
		// 设置默认展开级别
		ta.setExpandLevel(1);
		code_list.setAdapter(ta);
		ta.ExpandOrCollapse(0);
	}
	private void searchEquipment(int contentId){
		equipNodes.clear();
		equipNames=new String[0];
		String sql="select id,name from "+Constant.T_Device+" where contentId="+contentId+" order by sortorder asc";
		Cursor cursor2 = db.selectSql(sql);
		int k=0;
		String rowNo="";
		while(cursor2.moveToNext()){
			 k++;
			 if(k<10){
				 rowNo="0"+k;
			 }else{
				 rowNo=""+k;
			 }
			 String eid=cursor2.getString(0);
			 String ename=cursor2.getString(1);
			 Node equipNode=new Node(rowNo+" "+ename,eid);
			 equipNodes.add(equipNode);
		}
		cursor2.close();
		equipNames=new String[equipNodes.size()];
		for(int i=0;i<equipNodes.size();i++){
			equipNames[i]=equipNodes.get(i).getText();
		}
		isSearch=false;
	}
	
	
	
	
	
	
	
	private void setEquipContent(Node node){
		String querySql="select id,name,is_parent from "
	        +Constant.T_equip_Content+" where parent_id="+node.getValue()+" order by sort_order";
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			String id=cursor.getString(0);
			String name=cursor.getString(1);
			Node childNode=new Node(name,id);
			int isParent=cursor.getInt(2);
			childNode.setParent(node);
			childNode.setIcon(R.drawable.icon_department);//设置图标
			childNode.setIsParent(isParent);
			node.add(childNode);
			if(isParent==1){
				setEquipContent(childNode);
			}
		}
		cursor.close();
	}
	public Device getDeviceById(String id){
		Device device=new Device();
		String sql="select name,url,path,defectIds,equip_detail,filename,transsub from "+Constant.T_Device+" where id="+id;
		Cursor cursor = db.selectSql(sql);
		while(cursor.moveToNext()){
			device.setId(Integer.parseInt(id));
			device.setName(cursor.getString(0));
			device.setUrl(cursor.getString(1));
			device.setPath(cursor.getString(2));
			device.setDefectIds(cursor.getString(3));
			device.setEquipDetail(cursor.getString(4));
			device.transsub=cursor.getString(6);
			String filename=cursor.getString(5);
			if(filename==null||"".equals(filename)){
				device.setFileNames("");
			}else{
				device.setFileNames(filename);
			}
		}
		return device;
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.title_right:
			finish();
			break;
		}
	}	
	
	
	public void showEquipment() {
		if(equipNodes.size()==0){
			Toast.makeText(this, "未查询到任何设备！", 2000).show();
			return;
		}
		if(equipNodes.size()==1&&isSearch==false){
			dealRequest(equipNodes.get(0));
			return;
		}
		
		
		final Context dialogContext = new ContextThemeWrapper(this,
				android.R.style.Theme_Light);
		
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, equipNames);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("设备列表");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						equipNode=equipNodes.get(which);
						if(equipNode!=null){
							dealRequest(equipNode);
						}
					}
				});
		builder.create().show();
	}
	
	
	private void searchEquip() {
		String keyword=keywordEdit.getEditableText().toString().trim();
		if("".equals(keyword)){
			Toast.makeText(this, "请输入关键字", 1000).show();
			return;
		}
		isSearch=true;
		equipNodes.clear();
		equipNames=new String[0];
		
		int k=0;
		String rowNo="";
		String sql="select id,name from "+Constant.T_Device+"  WHERE transsub="+tsid+" and name LIKE '%" + keyword + "%' order by sortorder asc";
		Cursor cursor2 = db.selectSql(sql);
		while(cursor2.moveToNext()){
			 k++;
			 if(k<10){
				 rowNo="0"+k;
			 }else{
				 rowNo=""+k;
			 }
			 String eid=cursor2.getString(0);
			 String ename=cursor2.getString(1);
			 Node equipNode=new Node(rowNo+" "+ename,eid);
			 equipNodes.add(equipNode);
		}
		cursor2.close();
		equipNames=new String[equipNodes.size()];
		for(int i=0;i<equipNodes.size();i++){
			equipNames[i]=equipNodes.get(i).getText();
		}
		showEquipment();
	}	
	
}
