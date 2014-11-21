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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etrust.stategrid.adapter.ProduceAdapter;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ProduceReportActivity extends Activity implements OnClickListener {

	private ListView task_detial_list;
	private ProduceAdapter adapter;
	// take pic
	private File picFile;
	private Uri photoUri;
	private UserBean user;
	private static final int activity_result_camara_with_data = 1006;
	private static final int activity_result_cropimage_with_data = 1007;
	private DatabasesTransaction db;
	public String imageFiles = "pic";
	public int tsid;
	private String mtsname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_produce_report);
		findViewById(R.id.btn_pro_defect).setOnClickListener(cmtListener);
		db = DatabasesTransaction.getInstance(this);
		App app = (App) getApplication();
		user = app.getCurrentUserBean();
		tsid = getIntent().getIntExtra("mTransSubID", 0);
		mtsname=getIntent().getCharSequenceExtra("mtsname").toString();
		setupView();
	}

	private void setupView() {
		ImageButton cmt_btn_produce = (ImageButton) this
				.findViewById(R.id.btn_pro_defect);
		cmt_btn_produce.setImageResource(R.drawable.commit_img);
		ImageView title_icon = (ImageView) this.findViewById(R.id.pbt_title_icon);
		title_icon.setImageResource(R.drawable.upload_icon);

		TextView title_text = (TextView) findViewById(R.id.title_text_produce);
		title_text.setText("设施缺陷上报");
		findViewById(R.id.pbt_title_right).setOnClickListener(this);
		
		
	
		task_detial_list = (ListView) findViewById(R.id.pro_defect_detial_list);
		String[] lable = new String[] { "变电站名称","设备名称","缺陷等级","缺陷分类","缺陷描述", "缺陷照片" };
		String[] data = new String[] {mtsname,"","","","",""};
		adapter = new ProduceAdapter(this, lable, data);
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
			JSONObject json=new JSONObject();
			String[] ndata=adapter.data;
			try{
			    if("".equals(ndata[1])){
			    	Toast.makeText(ProduceReportActivity.this, "设备名称不能空", 1000).show();
			    	return;
			    }
			    if("".equals(ndata[2])){
			    	Toast.makeText(ProduceReportActivity.this, "请选择缺陷等级", 1000).show();
			    	return;
			    }
			    if("".equals(ndata[3])){
			    	Toast.makeText(ProduceReportActivity.this, "请选择缺陷分类", 1000).show();
			    	return;
			    }

				String pic=ndata[5];
				if(pic==null||"null".equals(pic)){
					pic="";
				}
				json.put("tsid", tsid);
				json.put("name", ndata[1]);
				json.put("level", ndata[2]);
				json.put("defcate", ndata[3]);
				json.put("descrip", ndata[4]);
				json.put("picname", pic);
				json.put("createtime", DateUtils.getCurrentDateTime());
				json.put("userId", user.userid);
				json.put("userName", user.username);
				
			}catch(Exception e){
				e.printStackTrace();
			}		
			int result=addProDefect(json);
			if(result>0){
				db.saveUpdateLog("addProDefect", "添加生产缺陷["+ndata[1]+"]");//数据保存日志
				Toast.makeText(ProduceReportActivity.this, "保存成功", 2000).show();
				finish();
			}else{
				Toast.makeText(ProduceReportActivity.this, "保存失败", 2000).show();
				return;
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
	//往数据库中加（保存）数据的方法
	public int addProDefect(JSONObject json){
		int result=0;
		try{
			ContentValues values = new ContentValues();
			values.put("content", json.toString());//全部输入保存为json，把JSONObject对象转换为json格式的字符串
			values.put("datetime", json.getString("createtime"));//保存时间
			db.saveSql(Constant.T_temp_ProduceDevice, values);
			result=1;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
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
							String status = Environment.getExternalStorageState();
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
		if (null != photoUri) {
			outState.putString("photoUri", photoUri.toString());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		imageFiles = savedInstanceState.getString("imageFiles");
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
				Cursor cursor = ProduceReportActivity.this.managedQuery(u,
						proj, null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaColumns.DATA);
				cursor.moveToFirst();
				String filePath = "";
				try {
					filePath = cursor.getString(column_index);
					if ("pic".equals(imageFiles) && !filePath.isEmpty()) {
						imageFiles = filePath;
						updateAdapter(ProduceAdapter.PIC_CASE,
								imageFiles);
					} else if (!filePath.isEmpty()) {
						imageFiles = imageFiles + "," + filePath;
						updateAdapter(ProduceAdapter.PIC_CASE,imageFiles);
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
					updateAdapter(ProduceAdapter.PIC_CASE, imageFiles);
				} else if (path != null && !path.isEmpty()) {
					imageFiles = imageFiles + "," + path;
					updateAdapter(ProduceAdapter.PIC_CASE, imageFiles);
				} else {
					Toast.makeText(getApplicationContext(), "图片不存在请重新拍取",
							Toast.LENGTH_LONG).show();
				}

				break;
			}
		} else {
			photoUri = null;
		}
	}

}
