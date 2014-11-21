package com.etrust.stategrid;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etrust.stategrid.adapter.TaskReportAdapter;
import com.etrust.stategrid.bean.CheckItem;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.bean.ItemCategory;
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.CallService;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;
import com.etrust.stategrid.utils.HttpClientHelper;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class TaskReportActivity extends Activity implements OnClickListener {

	private ListView task_detial_list;
	private TaskReportAdapter adapter;
	// take pic
	private File picFile;
	private Uri photoUri;
	private static final int activity_result_camara_with_data = 1006;
	private static final int activity_result_cropimage_with_data = 1007;
	public static final int activity_result_VIDEO_CAPTURE = 1008;
	public static final int activity_result_DEFECT_SELECT = 1009;//选择缺陷类目
	public static final int activity_result_DEFECT_DEVICE_SELECT=1010;//选择设备

	public String imageFiles = "pic";
	public String audioFiles = "audio";
	public String videoFiles = "video";

	private TaskBean tBean;
	private int towerid;
	public int defectTsid=0;
	public int deviceId=0;
	public String deviceName="";
	public String checkItem="";
	public String checkItemAllName="";
	public String toweridName="";
	private DatabasesTransaction db;
	private App app;
	private UserBean user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defect_report);
		db = DatabasesTransaction.getInstance(this);
		tBean = (TaskBean) getIntent().getSerializableExtra("taskData");
		towerid = getIntent().getIntExtra("towerid", -1);//得到上个页面传递的值
		toweridName=(String)getIntent().getCharSequenceExtra("toweridName");
		defectTsid=towerid;
		app=(App) getApplication();
		user=app.getCurrentUserBean();
		

		
		setupView();
	}

	private void setupView() {
		ImageButton task_detail_begin_btn = (ImageButton) this
				.findViewById(R.id.task_detail_begin_btn);
		task_detail_begin_btn.setOnClickListener(this);
		task_detail_begin_btn.setImageResource(R.drawable.commit_img);
		ImageView title_icon = (ImageView) this.findViewById(R.id.title_icon);
		title_icon.setImageResource(R.drawable.upload_icon);

		TextView title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("缺陷上报");

		findViewById(R.id.title_right).setOnClickListener(this);
		Button history_btn = (Button) findViewById(R.id.history_btn);
		history_btn.setText("查看历史缺陷");
		history_btn.setOnClickListener(this);

		task_detial_list = (ListView) findViewById(R.id.task_detial_list);
		String[] lable = new String[] { "任务名称", "创建人", "陪同人", "选择设备","缺陷名称", "缺陷等级",
				"处理方法", "缺陷视频", "缺陷语音", "缺陷照片", "缺陷描述"};
		
		String tBeanName="";
		String bBeanResname="";
		String bBeanAccname="";
		
		if(tBean!=null){
			tBeanName=tBean.name;
			bBeanResname=tBean.resname;
			bBeanAccname=tBean.accname;
		}
		

		String[] data = new String[] {tBeanName, bBeanResname,
				bBeanAccname,"请选择","", "1", "1", videoFiles, audioFiles,
				imageFiles, ""};
		adapter = new TaskReportAdapter(this, lable, data);
		task_detial_list.setAdapter(adapter);
	}

	public void updateAdapter(int posion, String data) {
		adapter.data[posion] = data;
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.task_detail_begin_btn:
			if(deviceId==0){
				Toast.makeText(getApplicationContext(), "请选择设备后再上传！",
						Toast.LENGTH_LONG).show();
				return;
			}
			String checkItem=adapter.data[TaskReportAdapter.DEFECT_CASE];
			if("".equals(checkItem)){
				Toast.makeText(getApplicationContext(), "请选择缺陷类目再上传！",
						Toast.LENGTH_LONG).show();
				return;
			}
			adapter.data[1]=user.getUserid();//当前登录人ID
			final String createTime=DateUtils.getCurrentDateTime();
			
			String[] data=adapter.data;
			
			String defect = data[TaskReportAdapter.DEFECT_CASE];
			String[] dItem = new String[] { "", "", "","" };
			if (defect.length() > 0) {
				dItem = defect.split(",");
			}
			JSONObject params=new JSONObject();
			try{
					params.put("itemCategory", dItem[2]);
					params.put("item", dItem[3]);
					//params.put("deviceid", deviceId);
					params.put("deviceid", deviceId);
					params.put("bugLevel",
							Integer.parseInt(data[TaskReportAdapter.LEVEL_CASE]));
					params.put("dealType",
							Integer.parseInt(data[TaskReportAdapter.DEAL_CASE]));
					params.put("description", data[TaskReportAdapter.DESCRIP_CASE]);
					params.put("creatorid", data[1]);
					params.put("executorid", data[2]);
					
					params.put("audioCase",data[TaskReportAdapter.AUDIO_CASE]);
					params.put("picCase",data[TaskReportAdapter.PIC_CASE]);
					params.put("videoCase", data[TaskReportAdapter.VIDEEO_CASE]);
					
					params.put("filename", "");
					params.put("userid", user.userid);
					params.put("createTime", createTime);
					params.put("defectId", "0");
					
					params.put("creatorName", data[1]);
					params.put("executorName", data[2]);
					params.put("beanName", data[0]);
					params.put("deviceName", toweridName+" "+deviceName);
					
					
					params.put("itemFullName",checkItemAllName);
					params.put("defectTsid",defectTsid);
					params.put("toweridName",toweridName+" "+dItem[0]+"，"+dItem[1]);
				
					
					//data
			}catch(Exception e){
				    e.printStackTrace();
			}			
			final JSONObject json=params;
			final CallService.HandlerML handleMl = new CallService().new HandlerML(TaskReportActivity.this,json,0);
			final CallService callService=new CallService(db);
			boolean hasNetWork=HttpClientHelper.isNetworkConnected(this);
			if(hasNetWork){
				    //如果连网状态
					new Thread(new Runnable() {
						@Override
						public void run() {
							//new CallService().doUploadDefect(
									//TaskReportActivity.this.getApplicationContext(),
									//adapter.data, towerid, tBean.accid, deviceId,false, handle,createTime);
							callService.doUploadDefectML(TaskReportActivity.this.getApplicationContext(), json, 0,handleMl);
							//实时上传
						}
					}).start();
			}else{
				int result=localAddDefect(json);
				if(result>0){
					db.saveUpdateLog("addDefect","添加缺陷");
					Toast.makeText(getApplicationContext(), "缺陷保存成功！",Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), "缺陷保存失败！",Toast.LENGTH_LONG).show();
					return;
				}
			}
			finish();
			break;
		case R.id.title_right:
			finish();
			break;
		case R.id.history_btn:
			Intent it = new Intent(TaskReportActivity.this,TreeActivity.class);
			it.putExtra("from", "TaskReportActivity");
			it.putExtra("mTransSubID", towerid);
			startActivity(it);
			break;
		}
	}

	// take photo
	public void doPickPhotoAction() {
		final Context dialogContext = new ContextThemeWrapper(this,
				android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = "拍照"; // 拍照
		choices[1] = "从相册中选择"; // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("添加照片");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								doTakePhoto();// 用户点击了从照相机获取
							}
							break;
						case 1:
							doCropPhoto();// 从相册中去获取
							break;
						}
					}
				});
		builder.create().show();
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			String fileName = System.currentTimeMillis() + ".jpg";
			picFile = new File(StorageUtils.getCacheDirectory(this), fileName);

			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			if (!picFile.exists()) {
				picFile.createNewFile();
			}
			photoUri = Uri.fromFile(picFile);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(cameraIntent,
					activity_result_camara_with_data);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从相册选取
	 */
	protected void doCropPhoto() {
		try {
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// 不使用图片剪裁
			startActivityForResult(intent, activity_result_cropimage_with_data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putCharSequence("imageFiles", imageFiles);
		outState.putCharSequence("audioFiles", audioFiles);
		outState.putCharSequence("videoFiles", videoFiles);
		if (null != photoUri) {
			outState.putString("photoUri", photoUri.toString());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		imageFiles = savedInstanceState.getString("imageFiles");
		audioFiles = savedInstanceState.getString("audioFiles");
		videoFiles = savedInstanceState.getString("videoFiles");
		if (null != savedInstanceState.getString("photoUri")) {
			photoUri = Uri.parse(savedInstanceState.getString("photoUri"));
		}
	}

	// end take photo

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case activity_result_cropimage_with_data: // 存储器选图
				if (null == data) {
					return;
				}
				// 配置本地上传图片的地址
				Uri u = data.getData();
				String[] proj = { MediaColumns.DATA };
				@SuppressWarnings("deprecation")
				Cursor cursor = TaskReportActivity.this.managedQuery(u, proj,
						null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaColumns.DATA);
				cursor.moveToFirst();
				String filePath = "";
				try {
					filePath = cursor.getString(column_index);
					if ("pic".equals(imageFiles) && !filePath.isEmpty()) {
						imageFiles = filePath;
						updateAdapter(TaskReportAdapter.PIC_CASE, imageFiles);
					} else if (!filePath.isEmpty()) {
						imageFiles = imageFiles + "," + filePath;
						updateAdapter(TaskReportAdapter.PIC_CASE, imageFiles);
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "找不到该图片，请重新选择",
							Toast.LENGTH_LONG).show();
					return;
				}

				Log.i("Lucien_debug", "photoUri1:" + filePath);
				break;
			case activity_result_camara_with_data: // 拍照
				String path = "";
				path = photoUri.getPath();

				Log.i("Lucien_debug", "photoUri2:" + path);
				if ("pic".equals(imageFiles) && !path.isEmpty()) {
					imageFiles = path;
					updateAdapter(TaskReportAdapter.PIC_CASE, imageFiles);
				} else if (path != null && !path.isEmpty()) {
					imageFiles = imageFiles + "," + path;
					updateAdapter(TaskReportAdapter.PIC_CASE, imageFiles);
				} else {
					Toast.makeText(getApplicationContext(), "图片不存在请重新拍取",
							Toast.LENGTH_LONG).show();
				}

				break;
			case activity_result_VIDEO_CAPTURE:
				Uri uri = data.getData();
				Cursor vcursor = this.getContentResolver().query(uri, null,
						null, null, null);
				if (vcursor != null && vcursor.moveToNext()) {
					String vfilePath = vcursor.getString(vcursor
							.getColumnIndex(MediaColumns.DATA));
					updateAdapter(TaskReportAdapter.VIDEEO_CASE, vfilePath);
				}
				break;
			case activity_result_DEFECT_SELECT:
				CheckItem checkItem = (CheckItem)data.getSerializableExtra("checkItem");
				ItemCategory itemCategory = (ItemCategory) data.getSerializableExtra("itemCategory");
				String toshow = itemCategory.name + "," + checkItem.name + ","+itemCategory.id+","+checkItem.id;
				checkItemAllName=toshow;
				updateAdapter(TaskReportAdapter.DEFECT_CASE, toshow);
				updateAdapter(10, checkItem.name);
				break;
				
			case activity_result_DEFECT_DEVICE_SELECT:
				Device device=(Device)data.getSerializableExtra("defectDevice");
				String toShow=device.getName();
				deviceId=device.getId();
				deviceName=toShow;
				updateAdapter(TaskReportAdapter.DEFECT_DEVICE_CASE, toShow);
				break;
			}
			
		} else {
			photoUri = null;
		}
	}
	public int  localAddDefect(JSONObject json){
		int result=0;
		try{
			  //保存在本地
			  ContentValues values = new ContentValues();
			  values.put("content", json.toString());
			  try{
				  values.put("datetime", json.getString("createTime"));
			  }catch(Exception e){
				  values.put("datetime","");
			  }
			  Log.i("localAddDefect", "本地保存缺陷");
			  db.saveSql(Constant.T_temp_Defect, values);
			  result=1;
		}catch(Exception e){
			  e.printStackTrace();
		}
		return result;
	}	

}
