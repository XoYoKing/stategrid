package com.etrust.stategrid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.etrust.stategrid.bean.Coordinate;
import com.etrust.stategrid.utils.Constant;
import com.etrust.stategrid.utils.DateUtils;

public class DatabasesTransaction {

	private static DataBaseHelper dataBaseHelper = null;
	private static DatabasesTransaction ct = null;
	private SQLiteDatabase db;

	private DatabasesTransaction() {
		db = dataBaseHelper.getWritableDatabase();
	}

	public static DatabasesTransaction getInstance(Context context) {
		if (ct == null) {
			dataBaseHelper = new DataBaseHelper(
					context.getApplicationContext(),
					DataBaseHelper.DATABASE_NAME, null,
					DataBaseHelper.DATABASE_VERSION);
			ct = new DatabasesTransaction();
		}
		return ct;
	}

	public Cursor selectSql(String sql) {
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public long saveSql(String table, ContentValues values) {
		return db.insert(table, null, values);
	}

	public void saveUpdateLog(String code, String content) {
		ContentValues values = new ContentValues();
		values.put("code", code);
		values.put("content", content);
		values.put("datetime", DateUtils.getCurrentDateTime());
		db.insert(Constant.T_update_log, null, values);
	}

	// 设备类别添加模拟数据content=leibie datatime=paixu InitDataActivity.java
	// public void addshebeiliebie() {
	// try {
	// ContentValues values = new ContentValues();
	//
	// values.put("content", "互感器");
	// values.put("datetime", "3");
	// saveSql(Constant.T_device_leibie, values);
	// ContentValues values1 = new ContentValues();
	// values1.put("content", "变压器");
	// values1.put("datetime", "1");
	// saveSql(Constant.T_device_leibie, values1);
	// ContentValues values2 = new ContentValues();
	// values2.put("content", "隔离开关");
	// values2.put("datetime", "2");
	// saveSql(Constant.T_device_leibie, values2);
	// ContentValues values3 = new ContentValues();
	// values3.put("content", "开关");
	// values3.put("datetime", "4");
	// saveSql(Constant.T_device_leibie, values3);
	//
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public void deleteData(String tableName, String whereSql, String[] whereArgs) {
		db.delete(tableName, whereSql, whereArgs);
	}

	public int updateSql(String table, ContentValues values, String whereSql) {
		return db.update(table, values, whereSql, null);
	}

	public boolean checkExists(String table, String whereSql) {
		String sql = "select count(*) from " + table + " where 1=1 ";
		if (whereSql != null && !"".equals(whereSql)) {
			sql = sql + " and " + whereSql;
		}
		int count = 0;
		Cursor cursor = selectSql(sql);
		while (cursor.moveToNext()) {
			count = Integer.parseInt(cursor.getString(0));
		}
		cursor.close();
		if (count > 0) {
			return true;
		}
		return false;
	}

	public Coordinate getLastArrive() {
		String sql = "select latitude,longitude from " + Constant.T_temp_Arrive
				+ " order by datetime desc";
		Cursor cursor = selectSql(sql);
		int m = 0;
		Coordinate coord = new Coordinate();
		coord.setLatitude("0");
		coord.setLongitude("0");
		while (cursor.moveToNext()) {
			if (m == 0) {
				coord.setLatitude(cursor.getString(0));
				coord.setLongitude(cursor.getString(1));
				break;
			}
		}
		return coord;
	}

	public int getCount(String table, String whereSql) {
		String sql = "select count(*) from " + table + " where 1=1 ";
		if (whereSql != null && !"".equals(whereSql)) {
			sql = sql + " and " + whereSql;
		}
		int count = 0;
		Cursor cursor = selectSql(sql);
		while (cursor.moveToNext()) {
			count = Integer.parseInt(cursor.getString(0));
		}
		cursor.close();
		return count;
	}

	public void execSql(String sql) {
		db.execSQL(sql);
	}

	public void cleanDb() {
		dataBaseHelper.cleanDb(db);
	}

	private static class DataBaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "data.db";
		private static final int DATABASE_VERSION = 1;
		
		private static final String T_ziliao_SQL="create table if not exists "+ Constant.T_ziliao+
				"(id integer primary key, name varchar, parent_id integer," +
				" sort_order integer, is_parent integer);";	
		
		// 未巡视变电站原因描述表
		private static final String CREATE_NOTOUR_REASON = "CREATE TABLE if not exists "
				+ Constant.T_NOTOUR_REASON
				+ "( id integer PRIMARY KEY ,taskid TEXT , "
				+ "stationname TEXT , " + "reason TEXT " + ");";

		private static final String CREATE_USER_SQL = "CREATE TABLE if not exists "
				+ Constant.T_USER
				+ "( userid TEXT PRIMARY KEY , "
				+ "username TEXT , "
				+ "password TEXT , "
				+ "Deptname TEXT , "
				+ "task TEXT " + ");";

		private static final String CREATE_Task_SQL = "CREATE TABLE if not exists "
				+ Constant.T_Task
				+ "( id TEXT PRIMARY KEY , "
				+ "uploadstatus INTEGER  DEFAULT "
				+ Constant.Upload_Task_Status_NONE
				+ ","
				+ "content TEXT,resid varchar(30) " + ",datetime varchar(30));";

		private static final String CREATE_ManStation_SQL = "CREATE TABLE if not exists "
				+ Constant.T_MainStation
				+ "( id TEXT PRIMARY KEY , "
				+ "name TEXT , " + "desc TEXT , " + "transSub TEXT  " + ");";

		private static final String CREATE_TransSub_SQL = "CREATE TABLE if not exists "
				+ Constant.T_TransSub
				+ "( id TEXT PRIMARY KEY , "
				+ "manstation TEXT , "
				+ "name TEXT , "
				+ "latitude TEXT , "
				+ "longitude TEXT , "
				+ "device TEXT , "
				+ "voltage TEXT , "
				+ "dianLiu TEXT  " + ");";// 添加电压电流两个字段

		private static final String CREATE_Device_SQL = "CREATE TABLE if not exists "
				+ Constant.T_Device
				+ "( id integer PRIMARY KEY , "
				+ "transsub TEXT , "
				+ "name TEXT , "
				+ "url TEXT , "
				+ "path TEXT , "
				+ "defectIds TEXT,type integer,contentId integer,equip_detail text,"
				+ "filename varchar,filestate integer,sortorder integer);";
		// hongwai
		private static final String CREATE_HONGWAI_DEVICE_SQL = "CREATE TABLE if not exists "
				+ Constant.T_hongwai_device
				+ "(id integer primary key,leibieid text,content text,datetime text);";// 原有基础加个“类别”字段

		private static final String CREATE_DEVICE_LEIBIE_SQL = "create table if not exists "
				+ Constant.T_device_leibie
				+ "(id integer,content text primary key,datetime text);";// 此表主要存储设备类别类别设为主键
		private static final String CREATE_Defect_SQL = "CREATE TABLE if not exists "
				+ Constant.T_Defect
				+ "( id TEXT PRIMARY KEY , "
				+ "device TEXT , "
				+ "itemCategory TEXT , "
				+ "item TEXT , "
				+ "bugLevel TEXT , "
				+ "dealType TEXT , "
				+ "description TEXT , "
				+ "creatorid TEXT , "
				+ "executorid TEXT , "
				+ "createDate TEXT , "
				+ " deviceId TEXT,"
				+ "attachIds TEXT,deviceName Text,tsid integer,cateid integer,itemid integer);";
		private static final String CREATE_Attach_SQL = "CREATE TABLE if not exists "
				+ Constant.T_Attach
				+ "( id varchar PRIMARY KEY , "
				+ "defect TEXT , "
				+ "url TEXT , "
				+ "realName TEXT , "
				+ "suffux TEXT , " + "path TEXT ,fileid TEXT" + ");";

		private static final String CREATE_ItemCategory_SQL = "CREATE TABLE if not exists "
				+ Constant.T_ItemCategory
				+ "( id TEXT PRIMARY KEY , "
				+ "name TEXT , " + "all_item TEXT " + ");";

		private static final String CREATE_Infrared_SQL = "CREATE TABLE if not exists "
				+ Constant.T_Infrared
				+ "( id TEXT PRIMARY KEY , "
				+ "name TEXT , "
				+ "url TEXT , "
				+ "path TEXT,createTime text "
				+ ");";

		private static final String CREATE_COORD_SQL = "create table if not exists "
				+ Constant.T_Coordinate
				+ "(id integer primary key,userid text,latitude text,longitude text,tsid integer,datetime text,tsname text);";

		private static final String CREATE_TEMP_Infrared_SQL = "create table if not exists "
				+ Constant.T_temp_Infrared
				+ "(id integer primary key,content text,datetime text);";

		private static final String T_temp_Defect_SQL = "create table if not exists "
				+ Constant.T_temp_Defect
				+ "(id integer primary key,content text,datetime text);";

		private static final String T_temp_Arrive_SQL = "create table if not exists "
				+ Constant.T_temp_Arrive
				+ "(id integer primary key,latitude double,longitude double,datetime varchar(30));";

		private static final String T_equip_content_SQL = "create table if not exists "
				+ Constant.T_equip_Content
				+ "(id integer primary key, name varchar, parent_id "
				+ "integer, sort_order integer, is_parent integer, type integer,ts_id integer);";

		private static final String T_plan_equip_sql = "create table if not exists "
				+ Constant.T_PlanEquip
				+ "(id integer primary key,content text,datetime varchar(30));";

		// T_temp_Defect日志信息

		private static final String CREATE_UPDATE_LOG_SQL = "create table if not exists "
				+ Constant.T_update_log
				+ "(id integer primary key,code varchar(30),content varchar(50),datetime varchar(30));";

		private static final String CREATE_Temp_ProduceDevice_SQL = "create table if not exists "
				+ Constant.T_temp_ProduceDevice
				+ "(id integer primary key,content text,datetime text);";

		public DataBaseHelper(Context context, java.lang.String name,
				CursorFactory factory, int version) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_USER_SQL);
			db.execSQL(CREATE_Task_SQL);
			db.execSQL(CREATE_ManStation_SQL);
			db.execSQL(CREATE_TransSub_SQL);
			db.execSQL(CREATE_Device_SQL);
			db.execSQL(CREATE_Defect_SQL);
			db.execSQL(CREATE_Attach_SQL);
			db.execSQL(CREATE_ItemCategory_SQL);
			db.execSQL(CREATE_Infrared_SQL);
			db.execSQL(CREATE_COORD_SQL);
			db.execSQL(CREATE_UPDATE_LOG_SQL);
			db.execSQL(CREATE_TEMP_Infrared_SQL);
			db.execSQL(T_temp_Defect_SQL);
			db.execSQL(T_temp_Arrive_SQL);
			db.execSQL(T_equip_content_SQL);
			db.execSQL(T_plan_equip_sql);
			db.execSQL(CREATE_Temp_ProduceDevice_SQL);//
			db.execSQL(CREATE_HONGWAI_DEVICE_SQL);// 增加红外参照表
			db.execSQL(CREATE_DEVICE_LEIBIE_SQL);// 增加设备类别表
			db.execSQL(CREATE_NOTOUR_REASON);
			db.execSQL(T_ziliao_SQL);//资料表

		}

		public void cleanDb(SQLiteDatabase db) {
			Log.i("Lucien_db", db.getPath());

			// 不能删除数据
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_MainStation);
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_TransSub);
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_ItemCategory);
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_Infrared);
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_PlanEquip);
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_temp_ProduceDevice);
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_PlanEquip);// 如果存在（EXISTS）删除
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_hongwai_device);
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_device_leibie);
			db.execSQL("DROP TABLE IF EXISTS " + Constant.T_NOTOUR_REASON);
			onCreate(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			cleanDb(db);
		}

	}
}
