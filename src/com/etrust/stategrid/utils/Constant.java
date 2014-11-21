package com.etrust.stategrid.utils;

import android.os.Environment;


public class Constant {
	public static String DOMAN = "http://192.168.3.107";
			//"http://www.hy100.cn";
	//"http://192.168.1.201";
	public static String serverUrl="http://192.168.1.120/gisyw_t";
	public static String localHost="http://192.168.1.120:8080/gisyw_t";
	//"http://192.168.1.201/login";
	public static String url = DOMAN+"/services/MyWebService?wsdl";
	public static String nameSpace = "http://webservice.gis.com";
	public static String updateServer;
	public static String hongWaiUrl=DOMAN+"/raz/contentClient?id=";
	
	public static String map_load =  DOMAN+"/fileOperate/downloadMap";
	
	public static String DEVICE_URL = DOMAN+"/equipment/contentClient?id=";
	public static String UPLOAD_URL = DOMAN+"/fileOperate/upload";
	public static String UPLOAD_DEVICE_URL = DOMAN+"/fileOperate/uploadDevicePic";
	public static String DEVICE_PIC_DOWNLOAD=DOMAN+"/fileOperate/download?filename=";
	public static String HongwaiBiaozhun=DOMAN+"/uploadDir";
	public static String T_ziliao="t_ziliao";//各种资料参考
	public static String T_USER = "t_user";
	
	public static String T_Task = "t_task";
	/**
	 * no change,don't upload
	 */
	public static int Upload_Task_Status_NONE = 0;
	/**
	 *  changed,upload
	 */
	public static int Upload_Task_Status_Change = 1;
	
	public static String T_MainStation = "t_mainstation";
	public static String T_TransSub = "t_transsub";
	public static String T_Device = "t_device";
	public static String T_Defect = "t_defect";
	public static String T_Attach = "t_attach";
	
	public static String T_Infrared = "t_infrared";
	public static String T_temp_Infrared = "t_temp_infrared";
	public static String T_temp_Defect = "t_temp_defect";
	public static String T_Coordinate="t_coordinate";//添加坐标
	public static String T_update_log="t_update_log";//数据更新日志
	public static String T_temp_Arrive="t_temp_arrive";
	public static String T_PlanEquip="t_plan_equip";
	
	
	public static String T_ItemCategory = "t_itemcategory";
	public static String SdCard = Environment.getExternalStorageDirectory().getPath();
	public static String File_Path = SdCard+"/tateGrid/File/";
	public static String HTML_Path = SdCard+"/StateGrid/HTML/";
	public static String HTML_Pic_Path =SdCard+ "/StateGrid/HTML/PIC/";//SdCard+ "/StateGrid/HTML/PIC/"
	public static String TestHostWork=DOMAN+"/equipment/testNetWork";//testNetWork
	
	public static String T_equip_Content="t_equip_content";
	public static String T_temp_ProduceDevice = "t_temp_producedevice";
	public static String T_hongwai_device="t_hongwai_device";//红外标准图谱参考详情
	public static String T_device_leibie="t_device_leibieString";//设备类别
	public static String T_NOTOUR_REASON="t_notour_reason";
}
