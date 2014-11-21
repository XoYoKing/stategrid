package com.etrust.stategrid;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.etrust.stategrid.bean.ItemCategory;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.db.DatabasesTransaction;
import com.etrust.stategrid.utils.CallService;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;
import com.etrust.stategrid.utils.HttpClientHelper;
import com.etrust.stategrid.utils.JsonUtils;
import com.google.gson.Gson;

public class InitDataActivity extends Activity {

	private App app;
	private DatabasesTransaction db;
	private boolean isUserLoadOver = false;
	private boolean isItemLoadOver = false;
	private boolean isFromSetting = false;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabasesTransaction.getInstance(this);//获得实例
		/***
		 * //保存初始化的到位wwf
		 ***/

		setContentView(R.layout.activity_init);

		String from = getIntent().getStringExtra("from");
		if (SystemSetFragment.class.getSimpleName().equals(from)) {
			isFromSetting = true;
			RelativeLayout par_id = (RelativeLayout) this
					.findViewById(R.id.par_id);
			par_id.setBackgroundColor(Color.TRANSPARENT);
		}

		app = (App) getApplication();
		context = InitDataActivity.this.getApplicationContext();
		SharedPreferences sp = context.getSharedPreferences("FirstLoginSp",
				Context.MODE_PRIVATE);
		// 判断是否是首次登录
		if (isFromSetting) {
			if (HttpClientHelper.isNetworkConnected(this)) {
				db.cleanDb();
				getAllUser();
				getItemCategory();
			} else {
				Toast.makeText(InitDataActivity.this, "请联网后尝试", 2000).show();
				finish();
			}
		} else {//非首次安装
			if (app.getInit() != App.INIT_UT) {
				if (HttpClientHelper.isNetworkConnected(this)) {
					Log.i("First Login", "首次登录，需要下载数据");
					db.cleanDb();
					getAllUser();
					getItemCategory();
				} else {
					Intent it = new Intent(InitDataActivity.this,
							LoginActivity.class);
					startActivity(it);
					finish();
				}
			} else {
				UserBean user = app.getCurrentUserBean();
				if (user != null) {// 已经登录过就直接进入tab
					Intent it = new Intent(InitDataActivity.this,
							TabMainActivity.class);
					it.putExtra("userBean", user);
					startActivity(it);
					finish();
				} else {// 非第一次安装进入输入账号页面
					Intent it = new Intent(InitDataActivity.this,
							LoginActivity.class);
					startActivity(it);
					finish();
				}
			}
		}

	//	db.addshebeiliebie();
	}

	/*
	 * 下载用户表
	 */
	private void getAllUser() {
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getAllUser", params, true);
	}

	private void getItemCategory() {
		HashMap<String, Object> params = new LinkedHashMap<String, Object>();
		CallService.getData(this, serviceHanlder, "getitemCategory", params,
				false);
	}

	public void initArrive() {//初始化坐标信息
		db.deleteData(Constant.T_temp_Arrive, null, null);
		ContentValues tempEq = new ContentValues();
		tempEq.put("latitude", 0);
		tempEq.put("longitude", 0);
		tempEq.put("datetime", DateUtils.getCurrentDateTime());
		db.saveSql(Constant.T_temp_Arrive, tempEq);
	}

	protected Handler serviceHanlder = new Handler() {
		@Override
		public void handleMessage(Message m) {
			int status = m.what;
			Bundle b = m.getData();
			String data = b.getString("data");
			String error = b.getString("error");
			String method = b.getString("method");

			if (status == 999) {
				Toast.makeText(InitDataActivity.this, error, 3000).show();
				finish();
				Log.i("Lucien", method + ":" + error + data);
				//load.sendEmptyMessage(7);
			} else if (method.equals("getAllUser")) {
				 Log.i("Lucien", "getAllUser success>>>>" + data);
				if (data == null || data.isEmpty()) {
					isUserLoadOver = true;
					return;
				}
				db.deleteData(Constant.T_USER, null, null);
				// 删除数据
				List<UserBean> userList = JsonUtils.getUserListFromJson(data);

				for (int i = 0; i < userList.size(); i++) {
					UserBean user = userList.get(i);
					ContentValues values = new ContentValues();
					values.put("userid", user.userid);
					values.put("password", user.password);
					values.put("username", user.username);
					values.put("Deptname", user.Deptname);
					values.put("task", user.task);
					db.saveSql(Constant.T_USER, values);
				}
				isUserLoadOver = true;
				load.sendEmptyMessage(6);
			} else if (method.equals("getitemCategory")) {
				List<ItemCategory> ilist = JsonUtils
						.getIitemCategoryFromJson(data);
				for (int i = 0; i < ilist.size(); i++) {
					ItemCategory ict = ilist.get(i);
					ContentValues values = new ContentValues();
					values.put("id", ict.id);
					values.put("name", ict.name);
					values.put("all_item", new Gson().toJson(ict));
					db.saveSql(Constant.T_ItemCategory, values);
				}
				isItemLoadOver = true;
				load.sendEmptyMessage(6);
			}
		}
	};
	protected Handler load = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 6) {
				if (!isFromSetting) {
					if (isUserLoadOver && isItemLoadOver) {
						app.setInit(App.INIT_UT);
						initArrive();
						Intent it = new Intent(InitDataActivity.this,
								LoginActivity.class);
						startActivity(it);
						finish();
					}
				} else {
					if (isUserLoadOver && isItemLoadOver) {
						Toast.makeText(InitDataActivity.this, "更新数据完毕", 2000)
								.show();
						finish();
					}
				}
			}
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return true;
	}
}
