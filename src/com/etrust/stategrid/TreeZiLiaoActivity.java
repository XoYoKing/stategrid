package com.etrust.stategrid;
import com.etrust.stategrid.adapter.Node;
import com.etrust.stategrid.adapter.TreeAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
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
import android.net.Uri;
import android.database.Cursor;
import android.view.View.OnClickListener;
public class TreeZiLiaoActivity extends Activity  implements OnItemClickListener,OnClickListener{
	ListView code_list;
	List<Node> equipNodes;
	private String[] ziliaoNames;
	private Node ziliaoNode;
	TreeZiLiaoActivity oThis = this;
	TreeAdapter ta;
	private DatabasesTransaction db;
	private Button searchBtn;
	private EditText keywordEdit;
	private static final String[][] fileTypes={
		{".doc","application/msword"}, 
		{".docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, 
		{".xls","application/vnd.ms-excel"},
		{".xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, 
		{".ppt","application/vnd.ms-powerpoint"},
		{".pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation"},
		{".txt","text/plain"},
		{".htm","text/html"},
		{".html","text/html"}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
		TextView title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("技术资料");
		findViewById(R.id.title_right).setOnClickListener(this);
		equipNodes=new ArrayList<Node>();
        code_list = (ListView)findViewById(R.id.code_list);
        code_list.setOnItemClickListener(this);
        db = DatabasesTransaction.getInstance(this);
        ziliaoNode=null;
        
        
        searchBtn=(Button)findViewById(R.id.tree_search_btn);
        //查询按钮
        searchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchZiLiao();
			}
		});
        keywordEdit=(EditText)findViewById(R.id.tree_edittext);
        setPreson();
    }


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// 这句话写在最后面
		Node node=(Node)ta.getItem(position);
		((TreeAdapter)parent.getAdapter()).ExpandOrCollapse(position);
		System.out.println("xxxxxxxxx"+node.getIsParent()+"  "+node.isLeaf());
		if(node.getIsParent()==0&&node.isLeaf()){
			String name=node.getText();
			openFile(name);
			//searchEquipment(Integer.parseInt(nodeValue));
			//equipNode=null;
			//showEquipment();
		}
	}
	
	public void dealRequest(Node node){
		String name=node.getValue();
		//开始打开文件
		finish();
	}
	
	public void searchZiLiao(){
		// 创建根节点
		
		String tsname="查询结果";
		String rootId="0";
		
		String keyword=keywordEdit.getText().toString().trim();
		if("".equals(keyword)){
			setPreson();
			return;
		}
		
        Node root = new Node(tsname,rootId);
        root.setIcon(R.drawable.icon_department);//设置图标
        
        
        int count=0;
		String querySql="select id,name,parent_id,is_parent from "
		        +Constant.T_ziliao+" where name like '%"+keyword+"%'";
		Cursor cursor = db.selectSql(querySql);
		
		int isParent=0;
		while (cursor.moveToNext()) {
			count++;
			rootId=cursor.getString(0);
			tsname=cursor.getString(1);
			isParent=cursor.getInt(3);
			Node node=new Node(tsname,rootId);
			root.add(node);
			node.setParent(root);
			node.setIsParent(isParent);
			if(isParent==1){
				node.setIcon(R.drawable.icon_department);
				setZiLiaoContent(node);
			}
			
		}
		cursor.close();
		if(count==0){
			Toast.makeText(this, "暂无任何查询结果！", 2000).show();
			return;
		}
		
		ta= new TreeAdapter(oThis,root);
		// 设置整个树是否显示复选框
		ta.setCheckBox(false);
		
	    // 设置展开和折叠时图标
		ta.setExpandedCollapsedIcon(R.drawable.tree_ex, R.drawable.tree_ec);
		
		// 设置默认展开级别
		ta.setExpandLevel(2);
		code_list.setAdapter(ta);
		ta.ExpandOrCollapse(0);
	}
	
	public void initTree(){
		
	}
	
	
	// 设置节点,可以通过循环或递归方式添加节点
	private void setPreson(){
		// 创建根节点
		
		String tsname="资料目录";
		String rootId="0";
		
		String querySql="select id,name,parent_id,is_parent from "
		        +Constant.T_ziliao+" where parent_id=0";
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			rootId=cursor.getString(0);
			tsname=cursor.getString(1);
			
		}
		cursor.close();
		
		
        Node root = new Node(tsname,rootId);
        root.setIcon(R.drawable.icon_department);//设置图标
        setZiLiaoContent(root);
		
		
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
	
	
	private void setZiLiaoContent(Node node){
		String querySql="select id,name,is_parent from "
	        +Constant.T_ziliao+" where parent_id="+node.getValue()+" order by sort_order";
		Cursor cursor = db.selectSql(querySql);
		while (cursor.moveToNext()) {
			String id=cursor.getString(0);
			String name=cursor.getString(1);
			Node childNode=new Node(name,id);
			int isParent=cursor.getInt(2);
			childNode.setParent(node);
			childNode.setIsParent(isParent);
			node.add(childNode);
			if(isParent==1){
				childNode.setIcon(R.drawable.icon_department);//设置图标
				setZiLiaoContent(childNode);
			}
		}
		cursor.close();
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
	
	
	private void openFile(String name){
		File file=null;
		boolean isExists=false;
		String typeValue="";
		for(int i=0;i<fileTypes.length;i++){
			String fileType=fileTypes[i][0];
			file=new File(Constant.SdCard+"/ziliao/"+name+fileType);
			if(file.exists()){
				isExists=true;
				typeValue=fileTypes[i][1];
				break;
			}else{
				file=null;
			}
		}
		if(isExists==false){
			Toast.makeText(this, "文件不存在！", 2000).show();
			return;
		}
		Intent intent = new Intent(); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		//设置intent的Action属性  
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), typeValue);
		startActivity(intent);
	}	
}
