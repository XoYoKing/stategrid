package com.etrust.stategrid;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import com.etrust.stategrid.db.DatabasesTransaction;
import org.json.JSONObject;

import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.EquipTemplate;

import com.etrust.stategrid.adapter.DeviceDetailAdapter;
import com.etrust.stategrid.adapter.TaskReportAdapter;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.utils.FileUtils;


import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Environment;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.widget.ListAdapter;
import android.app.AlertDialog;
import com.nostra13.universalimageloader.utils.StorageUtils;
public class DeviceDetailActivity extends Activity implements OnClickListener {
	private Device device;
	private int deviceid;
	private DatabasesTransaction db;
	int filestate;//拍照状态
	String fileName;

	private DeviceDetailAdapter dda;
	private ListView ddaList;
	public String[] data=new String[7];
	private Uri photoUri;
	private File picFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_detail);
		db = DatabasesTransaction.getInstance(this);
		Device oldDevice = (Device) getIntent().getSerializableExtra("mDevice");
		device=getDeviceById(oldDevice.getId());
		deviceid=device.id;
		fileName=device.fileNames;
		if(fileName==null||"null".equals(fileName)){
			fileName="";
		}
		setupView();
		readHtmlFormAssets();
	}

	private void setupView() {

		TextView title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("设备详情");
		findViewById(R.id.title_right).setOnClickListener(this);
		findViewById(R.id.paizhao).setOnClickListener(paiZhaoListener);
		Button defect_list_btn = (Button) findViewById(R.id.device_detail_defect_btn);
		defect_list_btn.setOnClickListener(this);
		ddaList=(ListView) findViewById(R.id.device_detial_list);
	}

	private void readHtmlFormAssets() {
		try{
			//String content=FileUtils.readTextFile(new File(url));
			String detail=device.getEquipDetail();
			String[] eles=detail.split("E07X");
			/***
			 * 
			 * 
			template=template.replace("device_ts_name", eles[0]);
			template=template.replace("device_equipName",device.getName());
			template=template.replace("device_equipNo", eles[1]);
			template=template.replace("device_equipBh", eles[2]);
			template=template.replace("device_name", eles[3]);
            tipsWebView.loadDataWithBaseURL(null, template, "text/html", "UTF-8", null);
            
            ***/
    		String[] label=new String[]{"变电站","设备名称","设备型号","设备编号","设备类别","生产厂家","照片"};
    		String producer=eles[4];
    		data=new String[]{eles[0],device.getName(),eles[1],eles[2], eles[3],producer,fileName};
    		dda=new DeviceDetailAdapter(this,label,data);
    		ddaList.setAdapter(dda);
            
            
		}catch(Exception e){
			e.printStackTrace();
			//tipsWebView.loadDataWithBaseURL(null, "内容解析出现错误！", "text/html", "UTF-8", null);
		}
	}

	public View.OnClickListener paiZhaoListener=new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

            doPickPhotoAction();
		}
		
	};
	

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right:
			finish();
			break;
		case R.id.device_detail_defect_btn:
			Intent it = new Intent();
			it.setClass(this, DefectHistoryListActivity.class);
			it.putExtra("mDevice", device);
			startActivity(it);
			break;
		}
	}
	
	public JSONObject  getEquipDetail(int id){
		
		return null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		filestate=0;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
					case 20:
						filestate=1;
						try {
							String path=photoUri.getPath();
							String nFile_Path=Constant.File_Path+deviceid+".jpg";
							File oldFile=new File(nFile_Path);
							if(oldFile.exists()){
								oldFile.delete();
							}
							FileUtils.copyFile(picFile, oldFile);
							fileName=nFile_Path;
							device.setFileNames(fileName);
							dda.data[6]=fileName;
							dda.notifyDataSetChanged();
							ContentValues values = new ContentValues();
							values.put("filename", fileName);
							values.put("filestate", filestate);
							db.updateSql(Constant.T_Device, values,"id="+deviceid);
							db.saveUpdateLog("device_pic", "拍照["+device.getName()+"]");
							picFile.delete();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							
						}
						break;
					case 21:
						if (null == data) {
							return;
						}
						// 配置本地上传图片的地址
						Uri u = data.getData();
						String[] proj = { MediaColumns.DATA };
						Cursor cursor = DeviceDetailActivity.this.managedQuery(u, proj,
								null, null, null);
						int column_index = cursor
								.getColumnIndexOrThrow(MediaColumns.DATA);
						cursor.moveToFirst();
						String filePath = "";
						try {
							filePath = cursor.getString(column_index);
							File selFile=new File(filePath);
							if(!selFile.exists()){
								return;
							}
							String nFile_Path=Constant.File_Path+deviceid+".jpg";
							File oldFile=new File(nFile_Path);
							if(oldFile.exists()){
								oldFile.delete();
							}
							FileUtils.copyFile(selFile, oldFile);
							fileName=filePath;
							device.setFileNames(fileName);
							dda.data[6]=fileName;
							dda.notifyDataSetChanged();
							ContentValues values = new ContentValues();
							values.put("filename", fileName);
							filestate=1;
							values.put("filestate", filestate);
							db.updateSql(Constant.T_Device, values,"id="+deviceid);
							db.saveUpdateLog("device_pic", "拍照["+device.getName()+"]");
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), "找不到该图片，请重新选择",
									Toast.LENGTH_LONG).show();
							return;
						}
						break;
			}
		}else {
			//filestate=0;
			return;
		}
	}	
	/**
	 * 读取文件，在ImageView中显示
	 * ***/
	private Bitmap getLoacalBitmap(String url) {
		   try {
		          FileInputStream fis = new FileInputStream(url);
		          return BitmapFactory.decodeStream(fis);
		     } catch (FileNotFoundException e) {
		          e.printStackTrace();
		          return null;
		     }
	}	
	
	
	public Device getDeviceById(int id){
		Device device=new Device();
		String sql="select name,url,path,defectIds,equip_detail,filename from "+Constant.T_Device+" where id="+id;
		Cursor cursor = db.selectSql(sql);
		while(cursor.moveToNext()){
			device.setId(id);
			device.setName(cursor.getString(0));
			device.setUrl(cursor.getString(1));
			device.setPath(cursor.getString(2));
			device.setDefectIds(cursor.getString(3));
			device.setEquipDetail(cursor.getString(4));
			String filename=cursor.getString(5);
			if(filename==null||"".equals(filename)){
				device.setFileNames("");
			}else{
				device.setFileNames(filename);
			}
		}
		return device;
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
	public void doTakePhoto(){
	        String eqipFile=deviceid+".jpg";
	        try{
	            picFile = new File(StorageUtils.getCacheDirectory(DeviceDetailActivity.this), eqipFile);
				if(picFile.exists()==false){
					picFile.createNewFile();
				}
	            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				photoUri = Uri.fromFile(picFile);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(cameraIntent,20);
			}catch(Exception e){
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
				startActivityForResult(intent,21);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
