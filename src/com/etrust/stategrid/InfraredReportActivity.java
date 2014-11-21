package com.etrust.stategrid;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
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

import com.etrust.stategrid.adapter.InfraredReportAdapter;
import com.etrust.stategrid.bean.Device;
import com.etrust.stategrid.bean.TransSub;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;
import com.etrust.stategrid.utils.HttpClientHelper;
import com.etrust.stategrid.utils.InfraredService;
import com.nostra13.universalimageloader.utils.StorageUtils;

//红外报告上传
@SuppressLint({ "ShowToast", "HandlerLeak" })
public class InfraredReportActivity extends Activity implements OnClickListener {

	private ListView task_detial_list;
	private InfraredReportAdapter adapter;
	// take pic
	private File picFile;
	private Uri photoUri;
	private UserBean user;
	public int deviceId = 0;// 变电站设备id
	public String deviceName = "";
	private static final int activity_result_camara_with_data = 1006;// 返回照相机拍照的数据
	private static final int activity_result_cropimage_with_data = 1007;// 返回相册中照片数据
	public static final int activity_result_DEFECT_DEVICE_SELECT = 1010;//返回选择设备的Id
	private DatabasesTransaction db;
	public String imageFiles = "pic";
	public int defectTsid = 0;
	public int imgType=1;
	String checkItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defect_report);
		db = DatabasesTransaction.getInstance(this);
		App app = (App) getApplication();
		user = app.getCurrentUserBean();
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
		title_text.setText("红外上传");
		findViewById(R.id.title_right).setOnClickListener(this);
		Button history_btn = (Button) findViewById(R.id.history_btn);
		history_btn.setText("查看红外历史");
		history_btn.setOnClickListener(this);
		task_detial_list = (ListView) findViewById(R.id.task_detial_list);
		String[] lable = new String[] { "设备名称", "电压等级", "仪器型号", "辐射系数", "额定电流",
				"测试距离（M）", "湿度", "环境温度", "检测时间", "最高温度点（*）", "第一点温度", "第二点温度",
				"诊断和缺陷分析", "处理意见", "备注", "红外照片", "可见光图片" };//参数名

		String dateTime = DateUtils.getCurrentDateTime();
		String[] data = new String[] { "", "", "", "0.95", "", "10", "50%", "",
				dateTime, "", "", "", "", "", "", "", "" };//参数值

		adapter = new InfraredReportAdapter(this, lable, data);
		task_detial_list.setAdapter(adapter);
	}

	public void updateAdapter(int posion, String data) {//传送的位置和数据
		adapter.data[posion] = data;//赋值给指定posion
		adapter.notifyDataSetChanged();
	}

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.task_detail_begin_btn:
			if (deviceId==0) {
				Toast.makeText(getApplicationContext(), "请选择设备后再上传", Toast.LENGTH_LONG
						).show();
				return;
			}
			 checkItem=adapter.data[9];
			if ("".equals(checkItem)||!isNumeric(checkItem)) {//当为空或者非数字的情况下提示
				Toast.makeText(getApplicationContext(), "最高温度点必须为数字", Toast.LENGTH_LONG
						).show();
				return;
			}
			 checkItem=adapter.data[7];
				if (checkItem.length()>0&&!isNumeric(checkItem)) {
					Toast.makeText(getApplicationContext(), "环境温度项必须为数字", Toast.LENGTH_LONG
							).show();
					return;
				}
				 checkItem=adapter.data[10];
					if (checkItem.length()>0&&!isNumeric(checkItem)) {
						Toast.makeText(getApplicationContext(), "第一温度点必须为数字", Toast.LENGTH_LONG
								).show();
						return;
					}
					 checkItem=adapter.data[11];
						if (checkItem.length()>0&&!isNumeric(checkItem)) {
							Toast.makeText(getApplicationContext(), "第二温度点必须为数字", Toast.LENGTH_LONG
									).show();
							return;
						}
			
			JSONObject json = new JSONObject();
			String[] ndata = adapter.data;
			
			try {
				
				for (int i = 0; i < ndata.length ; i++) {
					int pos = i + 1;
					if (ndata[i] != null) {
						ndata[i] = ndata[i].replaceAll("\"", "");
					}
					json.put("attr" + pos, ndata[i]);
					System.out.println();
				}
				String pic = ndata[InfraredReportAdapter.PIC_CASE];
				if (pic == null || "null".equals(pic)) {
					pic = "";
				}
				String kejianguang = ndata[InfraredReportAdapter.Kejianguang_PIC];
				if (kejianguang == null || "null".equals(pic)) {
					kejianguang = "";
				}

				//页面上有的数据
				json.put("deviceid", deviceId);
				System.out.println("deviceId222222222222"+deviceId);
				json.put("filename", "");
				json.put("picname", pic);
				json.put("kejianguang", kejianguang);
				json.put("createtime", DateUtils.getCurrentDateTime());
				json.put("userid", user.userid);
				json.put("infraredId", "0");//存json类型红外报告

			} catch (Exception e) {
e.printStackTrace();
			}
			System.out.println(json.toString());
			final JSONObject jsonf = json;
			final InfraredService infraredService = new InfraredService(db);
			final InfraredService.InfraredHandler inHandler = new InfraredService().new InfraredHandler(
					InfraredReportActivity.this, jsonf, 0);
			boolean hasNetWork = HttpClientHelper.isNetworkConnected(this);//判断是否联网
			if (hasNetWork) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						infraredService.doUploadInfrared(
								InfraredReportActivity.this, jsonf, 0,
								inHandler);
					}
				}).start();
			} else {
				int result = localAddInfrared(json);
				if (result > 0) {
					db.saveUpdateLog("addInfrared", "添加红外");
					Toast.makeText(InfraredReportActivity.this, "红外保存成功", 2000)
							.show();
				} else {
					Toast.makeText(InfraredReportActivity.this, "红外保存失败", 2000)
							.show();
					return;
				}
				// 在本地保存
			}
			finish();
			break;
		case R.id.title_right:
			finish();
			break;
		case R.id.history_btn:
			Intent it = new Intent(InfraredReportActivity.this,
					InfraredHistoryListActivity.class);
			startActivity(it);
			break;
		}
	}
	//判断传过来的参数是否为数字（小数负数）
	public  boolean isNumeric(String str){
		
	     Pattern pattern = Pattern.compile("(-)?\\d+(\\.\\d+)?");
	     return pattern.matcher(str).matches();   
	}
	//
	public int localAddInfrared(JSONObject json) {
		int result = 0;
		try {
			ContentValues values = new ContentValues();
			values.put("content", json.toString());
			values.put("datetime", json.getString("createtime"));
			db.saveSql(Constant.T_temp_Infrared, values);//往数据库中存数据
			result = 1;
			db.saveUpdateLog("addInfrared", "添加红外");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressLint("ShowToast")
	protected Handler serviceHanlder = new Handler() {

		@SuppressLint("ShowToast")
		@Override
		public void handleMessage(Message m) {
			int status = m.what;
			Bundle b = m.getData();
			String data = b.getString("data");
			String error = b.getString("error");
			String method = b.getString("method");

			if (status == 999) {
				Toast.makeText(InfraredReportActivity.this, error, 3000).show();
				Log.i("Lucien", method + ":" + error + data);
			} else {
				Log.i("Lucien", "infrared success>>>>" + data);
				if (data == null || data.isEmpty()) {
					return;
				}

			}
			if ("true".equals(data)) {
				Toast.makeText(InfraredReportActivity.this, "红外上传成功", 2000)
						.show();
				finish();
			} else {
				Toast.makeText(InfraredReportActivity.this, "红外上传失败，请重新尝试",
						2000).show();
			}
		}
	};

	// take photo
	public void doPickPhotoAction(int imgType) {
		this.imgType=imgType;
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
					activity_result_camara_with_data);//跳转此页面
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
			startActivityForResult(intent, activity_result_cropimage_with_data);//跳转此页面
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

	// end take photo activity之间数据交流 有三个方法

	/*
	 * 第一个参数：这个整数requestCode提供给onActivityResult，是以便确认返回的数据是从哪个Activity返回的。
	 * 这个requestCode和startActivityForResult中的requestCode相对应。
	 * 第二个参数：这整数resultCode是由子Activity通过其setResult()方法返回。
	 * 
	 * 第三个参数：一个Intent对象，带有返回的数据。
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case activity_result_cropimage_with_data: // 相册选图
				if (null == data) {
					return;
				}
				// 配置本地上传图片的地址
				Uri u = data.getData();
				String[] proj = { MediaColumns.DATA };
				@SuppressWarnings("deprecation")
				Cursor cursor = InfraredReportActivity.this.managedQuery(u,
						proj, null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaColumns.DATA);
				cursor.moveToFirst();
				String filePath = "";
				try {
					filePath = cursor.getString(column_index);
					if ("pic".equals(imageFiles) && !filePath.isEmpty()) {
						imageFiles = filePath;
						if (imgType==1) {
							updateAdapter(InfraredReportAdapter.PIC_CASE,
									imageFiles);
						}else {
							updateAdapter(InfraredReportAdapter.Kejianguang_PIC, imageFiles);
						}
					
					
					} else if (!filePath.isEmpty()) {
						imageFiles = filePath;
						if (imgType==1) {
							updateAdapter(InfraredReportAdapter.PIC_CASE,
									imageFiles);
						}else {
							updateAdapter(InfraredReportAdapter.Kejianguang_PIC, imageFiles);
						}
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
					if (imgType==1) {
						updateAdapter(InfraredReportAdapter.PIC_CASE,
								imageFiles);
					}else {
						updateAdapter(InfraredReportAdapter.Kejianguang_PIC, imageFiles);
					}
				} else if (path != null && !path.isEmpty()) {
					imageFiles = path;//之前是 imageFiles =imageFiles+“，” path;  避免照片粘连
					if (imgType==1) {
						updateAdapter(InfraredReportAdapter.PIC_CASE,
								imageFiles);
					}else {
						updateAdapter(InfraredReportAdapter.Kejianguang_PIC, imageFiles);
					}
				} else {
					Toast.makeText(getApplicationContext(), "图片不存在请重新拍取",
							Toast.LENGTH_LONG).show();
				}

				break;
		
			case activity_result_DEFECT_DEVICE_SELECT:// 选择设备，显示在平板
				Device device = (Device) data
						.getSerializableExtra("defectDevice");
				deviceId = device.getId();
				String toShow = device.getName();
				//根据传递的ID查询这个设备所在的变电站的等级和电流
				String querySql = "SELECT voltage,dianLiu FROM " + Constant.T_TransSub+" where id='"+device.transsub+"'";
				System.out.println("deviceId1111111111111"+querySql);
				Cursor cursor1 = db.selectSql(querySql);
				TransSub transSub=new TransSub();
				transSub.voltage="";
				transSub.dianLiu="";
				while(cursor1.moveToNext()){
					transSub.voltage=cursor1.getString(0);
					transSub.dianLiu=cursor1.getString(1);
				}

				
				
				System.out.println("deviceId1111111111111"+deviceId);
				deviceName = toShow;
				updateAdapter(InfraredReportAdapter.DEFECT_DEVICE_CASE, toShow);
				updateAdapter(1, transSub.voltage);
				updateAdapter(4, transSub.dianLiu);
				
				break;
			}
		} else {
			photoUri = null;
		}
	}

}
