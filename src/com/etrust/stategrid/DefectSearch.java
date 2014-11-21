package com.etrust.stategrid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import com.etrust.stategrid.adapter.DataQueryAdapter;
import com.etrust.stategrid.bean.Defect;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;
import com.etrust.stategrid.view.XListView;
import com.etrust.stategrid.bean.CheckItem;
import com.etrust.stategrid.bean.ItemCategory;
import android.view.ContextThemeWrapper;
import android.widget.ListAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View.OnFocusChangeListener;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
public class DefectSearch extends Activity implements XListView.IXListViewListener, OnItemClickListener,
		OnClickListener {
	private XListView mListView;
	private List<String> adaptData;
	private DataQueryAdapter queryAdapter;
	private Calendar c;
	private String choicesValue="";
	private String keyword="";
	private String dateValue="";
	private String beginDate="";
	private String endDate="";
	private int dateType=0;
	private String[] choices = new String[] { "全部缺陷","一般缺陷", "严重缺陷", "危急缺陷"};
	private String[] clearData= new String[] { "全部清空","清空关键字", "清空设备", "清空缺陷","清空缺陷等级","清空开始日期","清空结束日期"};
	private EditText search_edittext;
	private Button equipEdit;
	private Button itemEdit;
	private Button beginDateBtn;
	private Button endDateBtn;
	private Button pageButton;
	private Button levelButton;
	private Button clearButton;
	private List<Defect> dList;
	private DatabasesTransaction db;
	private int tsid=0;
	private String mtsname="";
	private int allCount=0;
	private int currentPage=1;
	private int allPage=0;
	private int pageSize=50;
    public DefectSearch dsThis;
    public static final int activity_result_this=10;
    public static final int activity_result_item=11;
    public int deviceId=0;
    public int itemId=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.defect_search_layout);
		choicesValue="";
		keyword="";
		dateValue="";
		db = DatabasesTransaction.getInstance(this);
		tsid = getIntent().getIntExtra("mTransSubID", 0);
		mtsname=getIntent().getCharSequenceExtra("mtsname").toString();
		dsThis=this;
		setUpView();

		
		
	}
	private void searchCount(String bugLevel,String keyword){
		StringBuilder sb=new StringBuilder();
		sb.append(" tsid=").append(tsid).append(" ");
		if(!"".equals(bugLevel)){
			sb.append(" and bugLevel='").append(bugLevel).append("' ");
		}
		if(!"".equals(keyword)){
			sb.append(" and deviceName like '%").append(keyword).append("%' ");
		}
		if(deviceId!=0){
			sb.append(" and device=").append(deviceId).append(" ");
		}	
		if(itemId!=0){
			sb.append(" and itemid=").append(itemId).append(" ");
		}
		if(!"".equals(beginDate)){
			sb.append(" and createDate>='").append(beginDate).append(" 00:00:00' ");
		}
		if(!"".equals(endDate)){
			sb.append(" and createDate<='").append(endDate).append(" 23:59:59' ");
		}
		String whereSql=sb.toString();
		allCount=db.getCount(Constant.T_Defect, whereSql);
		allPage=(allCount%pageSize==0)?(allCount/pageSize):((allCount/pageSize)+1);
	}


	private void doSearch(int start,int pageSize,String bugLevel,String keyword) {
		StringBuilder sb=new StringBuilder("SELECT * FROM ").append(Constant.T_Defect);
		sb.append(" WHERE tsid=").append(tsid).append(" ");
		if(!"".equals(bugLevel)){
			sb.append(" and bugLevel='").append(bugLevel).append("' ");
		}
		if(!"".equals(keyword)){
			sb.append(" and deviceName like '%").append(keyword).append("%' ");
		}
		if(deviceId!=0){
			sb.append(" and device=").append(deviceId).append(" ");
		}
		if(itemId!=0){
			sb.append(" and itemid=").append(itemId).append(" ");
		}
		if(!"".equals(beginDate)){
			sb.append(" and createDate>='").append(beginDate).append(" 00:00:00' ");
		}
		if(!"".equals(endDate)){
			sb.append(" and createDate<='").append(endDate).append(" 23:59:59' ");
		}	
		sb.append("  order by createDate desc ");
		sb.append(" limit ").append(pageSize).append(" offset ").append((start-1)*pageSize);
		String querySql=sb.toString();
        System.out.println(querySql);
		adaptData.clear();
		dList.clear();
		 
		if(allCount==0){
				start=0;
		}
		

		pageButton.setText("查询到"+allCount+"条记录，当前第"+start+"/"+allPage+"页");
		
		Cursor cursor = db.selectSql(querySql);
		
		int i=1;
		String rowNo="";
		while (cursor.moveToNext()) {
			Defect temp = new Defect();
			temp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("id")));
			temp.device = cursor.getString(cursor.getColumnIndexOrThrow("device"));//
			temp.itemCategory = cursor.getString(cursor.getColumnIndexOrThrow("itemCategory"));
			temp.item = cursor.getString(cursor.getColumnIndexOrThrow("item"));
			temp.bugLevel = cursor.getString(cursor.getColumnIndexOrThrow("bugLevel"));
			temp.dealType = cursor.getString(cursor.getColumnIndexOrThrow("dealType"));
			temp.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
			temp.creatorid = cursor.getString(cursor.getColumnIndexOrThrow("creatorid"));
			temp.executorid = cursor.getString(cursor.getColumnIndexOrThrow("executorid"));
			temp.createDate = cursor.getString(cursor.getColumnIndexOrThrow("createDate"));
			temp.attachIds = cursor.getString(cursor.getColumnIndexOrThrow("attachIds"));
			temp.device=cursor.getString(cursor.getColumnIndexOrThrow("deviceName"));
			
			if(i<10){
				rowNo="0"+i;
			}else{
				rowNo=""+i;
			}
			adaptData.add(rowNo+" ["+temp.createDate +"]"+temp.device+"  "+temp.bugLevel+"  "+temp.item);
			dList.add(temp);
			i++;
		}
		cursor.close();
		loadOver();
		
		queryAdapter.notifyDataSetChanged();
	}
	
	

	public void setUpView() {
		
		adaptData = new ArrayList<String>();
		dList=new ArrayList<Defect>();
		
		TextView title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText(mtsname+"->缺陷列表");
		findViewById(R.id.title_right).setOnClickListener(this);		
		//返回按钮
		search_edittext = (EditText)findViewById(R.id.dsf_edittext);
		//查询条件
		equipEdit= (Button)findViewById(R.id.dsf_equip_edit);
		//选择设备
		equipEdit.setOnClickListener(equipEditListener);
		//添加事件监听
		itemEdit=(Button)findViewById(R.id.dsf_item_edit);
		//选择缺陷
		itemEdit.setOnClickListener(itemEditListener);
		//缺陷选择监听器
		beginDateBtn=(Button)findViewById(R.id.dsf_begin_date);
		//缺陷日期
		endDateBtn=(Button)findViewById(R.id.dsf_end_date);
		
		
		beginDateBtn.setOnClickListener(dateListener);
		endDateBtn.setOnClickListener(dateListener);
		
		
		findViewById(R.id.dfs_button).setOnClickListener(this);
		
		levelButton=(Button)findViewById(R.id.dfs_button_level);
		//缺陷级别
		
		levelButton.setOnClickListener(this);
		
		clearButton=(Button)findViewById(R.id.button_clear);
		clearButton.setOnClickListener(this);
		
		
		findViewById(R.id.button_first).setOnClickListener(this);
		findViewById(R.id.button_previous).setOnClickListener(this);
		findViewById(R.id.button_next).setOnClickListener(this);
		findViewById(R.id.button_last).setOnClickListener(this);

		mListView = (XListView)findViewById(R.id.df_list_view);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		
		pageButton=(Button)findViewById(R.id.button_navigate);

		queryAdapter = new DataQueryAdapter(this, adaptData);

		mListView.setAdapter(queryAdapter);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
		

	}

	private void loadOver() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.dfs_button:
			keyword = search_edittext.getEditableText().toString().trim();
			currentPage=1;
			searchCount(choicesValue,keyword);
			doSearch(1,pageSize,choicesValue,keyword);
			break;
		case R.id.dfs_button_level:
			 showLevel();
			 break;
			 
		case R.id.button_clear:
			showClear();
			break;
		case R.id.button_first:
			 if(allCount==0){
				 return;
			 }
			 currentPage=1;
			 doSearch(1,pageSize,choicesValue,keyword);
			 break;
			  
		case R.id.button_previous:
			if(allCount==0){
				 return;
			}			
			currentPage=currentPage-1;
			if(currentPage<1){
				currentPage=1;
			}
			 doSearch(currentPage,pageSize,choicesValue,keyword);
			 break;	
			 
		case R.id.button_next:
			 if(allCount==0){
				 return;
			 }
			 currentPage=currentPage+1;
			 if(currentPage>allPage){
				 	currentPage=allPage;
			 }
			 doSearch(currentPage,pageSize,choicesValue,keyword);
			 break;	
			 
		case R.id.button_last:
			 if(allCount==0){
				 return;
			 }
			 currentPage=allPage;
			 doSearch(allPage,pageSize,choicesValue,keyword);
			 break;			 
		case R.id.title_right:
		     finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int rightPosion = (int) arg3;
		Defect defect=dList.get(rightPosion);
		Intent it = new Intent();
		it.setClass(this, HistoryTaskReportActivity.class);
		it.putExtra("defect", defect);
		startActivity(it);
	}
	
	
   public View.OnClickListener dateListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
				if(v.getId()==R.id.dsf_begin_date){
					dateType=1;
				}else{
					dateType=2;
				}
				createDateDialog();
		}
    }; 	

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
	
	public void showLevel() {
		final Context dialogContext = new ContextThemeWrapper(this,
				android.R.style.Theme_Light);
		

		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("缺陷等级");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							break;
						case 1:
							break;
						}
						levelButton.setText(choices[which]);
						choicesValue=choices[which];
						if("全部缺陷".equals(choicesValue)){
							choicesValue="";
						}
					}
				});
		builder.create().show();
	}	
	
	public void showClear() {
		final Context dialogContext = new ContextThemeWrapper(this,
				android.R.style.Theme_Light);
		
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, clearData);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("清空");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							itemId=0;
							keyword="";
							deviceId=0;
							choicesValue="";
							equipEdit.setText("选择设备");
							itemEdit.setText("选择缺陷");
							search_edittext.setText("");
							dateValue="";
							beginDateBtn.setText("开始日期");
							endDateBtn.setText("结束日期");
							levelButton.setText("缺陷等级");
							beginDate="";
							endDate="";
							break;
						case 1:
							keyword="";
							search_edittext.setText("");
							break;
						case 2:
							deviceId=0;
							equipEdit.setText("选择设备");
							break;
						case 3:
							itemId=0;
							itemEdit.setText("选择缺陷");
							break;
						case 4:
							choicesValue="";
							levelButton.setText("缺陷等级");
							break;
						case 5:
							beginDate="";
							beginDateBtn.setText("开始日期");
							break;
						case 6:
							endDate="";
							endDateBtn.setText("结束日期");
							break;
						}
						
					}
				});
		builder.create().show();
	}

	
	
	/***
	 * 选择设备监听器
	 * **/
	public OnClickListener equipEditListener=new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(dsThis, TreeActivity.class);
			intent.putExtra("mTransSubID",tsid);
			intent.putExtra("from", "DefectSearch");
			dsThis.startActivityForResult(intent,activity_result_this);	
		}
	};
	/***
	 * 选择缺陷监听器
	 * **/	
	public OnClickListener itemEditListener=new View.OnClickListener(){
		@Override
		public void onClick(View arg0){
				Intent intent = new Intent();
				intent.setClass(dsThis, DefectSelectListActivity.class);
				intent.putExtra("from", "DefectSearchItem");
				dsThis.startActivityForResult(intent,activity_result_item);
	
		}
	};
	
	/***
	 * 自动填充日期
	 * **/
	public OnFocusChangeListener dateEditListener=new OnFocusChangeListener(){
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
		}
	};	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(resultCode == RESULT_OK) {
				switch (requestCode) {
	                case activity_result_this:
	    				Device device=(Device)data.getSerializableExtra("defectDevice");
	    				String name=device.getName();
	    				if(name.length()>10){
	    					name=name.substring(0,10)+"..";
	    				}
	    				deviceId=device.getId();
	    				equipEdit.setText(name);
	                break;
	                case activity_result_item:
	    				CheckItem checkItem = (CheckItem)data.getSerializableExtra("checkItem");
	    				ItemCategory itemCategory = (ItemCategory) data.getSerializableExtra("itemCategory");
	    				itemId=checkItem.id;
	    				String toshow =checkItem.name;
	    				if(toshow.length()>8){
	    					toshow=toshow.substring(0,10)+"..";
	    				}
	    				itemEdit.setText(toshow);
	                break;
	            }
	        }		
	}
	
	
	public void createDateDialog() {
		c=Calendar.getInstance();//获取日期对象
		DatePickerDialog dpd=new DatePickerDialog(
				DefectSearch.this,
				new DatePickerDialog.OnDateSetListener(){	//创建OnDateSetListener监听器
					@Override
					public void onDateSet(
							DatePicker arg0,
							int year, int month,int dayOfMonth) {
						// TODO Auto-generated method stub
						    String realMonth=String.valueOf(month+1);
						    String day=String.valueOf(dayOfMonth);
						    if(month+1<10){
						    	realMonth="0"+realMonth;
						    }
						    if(dayOfMonth<10){
						    	day="0"+day;
						    }
						    String selTime=year+"-"+realMonth+"-"+day;
                            if(dateType==1){
                            	beginDate=selTime;
                            	beginDateBtn.setText(selTime);
                            }else{
                            	endDate=selTime;
                            	endDateBtn.setText(selTime);
                            }
					}    		    	 
    		     },
    		     c.get(Calendar.YEAR),					//传入年份
    		     c.get(Calendar.MONTH),					//传入月份
    		     c.get(Calendar.DAY_OF_MONTH)    		//传入天数  
				);
		dpd.show();	
    }
	
}
