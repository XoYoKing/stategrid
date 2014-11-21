package com.etrust.stategrid;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;//接口
import com.baidu.mapapi.map.MKEvent;//地图事件
import com.etrust.stategrid.bean.TaskBean;
import com.etrust.stategrid.bean.UserBean;
import com.etrust.stategrid.utils.JsonUtils;
import com.etrust.stategrid.utils.TimeFileNameGenerator;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


/*
 * 全局方法，有验证百度key
 */
public class App extends Application {
	private static App mInstance = null;
	public boolean m_bKeyRight = true;
	public BMapManager mBMapManager = null;
	public static final String strKey = "C65GSDgNKIBQGAnnVtzhuGuV";
	private UserBean mCurrentUserBean;// 当前登录用户
	private SharedPreferences sp;
	private TaskBean mCurrentTaskBean;// 当前正在执行的任务
	/**
	 * 初始化用户和任务
	 */
	public static final int INIT_UT = 1;
	/**
	 * 初始化data
	 */
	public static final int INIT_D = 1;
	
	private int init = -1;// 0 未初始化

	public static boolean dataChange = false;
/*
 * 一个程序的入口  ，application是用来保存全局变量的
 * 
 */
	@Override
	public void onCreate() {
		super.onCreate();
		//调用Context对象的getSharedPreferences()方法获得的SharedPreferences对象可以被同一应用程序下的其他组件共享.
		//1实例化SharedPreferences 对象
		sp = this.getSharedPreferences("App", MODE_PRIVATE);//代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
		mInstance = this;

		initImageLoader(getApplicationContext());

		initEngineManager(this);

	}

	public void setInit(int init) {
		this.init = init;
		sp.edit().putInt("init", init).commit();//保存并提交数据
	}

	public int getInit() {
		int initValue = 0;
		if (this.init == -1) {
			initValue = sp.getInt("init", 0);//获得SharedPreferences的值
		} else {
			initValue = this.init;
		}
		return initValue;
	}

	public void setCurrentUser(UserBean b) {
		mCurrentUserBean = b;
		if (b != null) {
			sp.edit().putString("UserBean", new Gson().toJson(b)).commit();//保存并提交数据
		}
	}

	public UserBean getCurrentUserBean() {
		if (mCurrentUserBean == null) {
			String loginJson = sp.getString("UserBean", null);//获得SharedPreferences的值
			if (loginJson == null || "null".equals(loginJson)) {
				return null;
			} else {
				mCurrentUserBean = JsonUtils.getUserBeanFromJson(loginJson);
			}
		}
		return mCurrentUserBean;
	}

	public void cancellation() {
		setCurrentUser(null);
		// sp.edit().clear().commit();
		// stopService(new Intent(this, LocalService.class));
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
		}
	}

	public static App getInstance() {
		return mInstance;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {

			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {

			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				App.getInstance().m_bKeyRight = false;
			} else {
				App.getInstance().m_bKeyRight = true;
			}
		}
	}

	public TaskBean getmCurrentTaskBean() {
		if (mCurrentTaskBean == null) {
			String loginJson = sp.getString("TaskBean", null);
			if (loginJson == null || "null".equals(loginJson)) {
				return null;
			} else {
				mCurrentTaskBean = JsonUtils.getTaskBeanFromJson(loginJson);
			}
		}
		return mCurrentTaskBean;
	}

	public void setmCurrentTaskBean(TaskBean mCurrentTaskBean) {
		this.mCurrentTaskBean = mCurrentTaskBean;
		if (mCurrentTaskBean != null) {
			
			//SharedPreferences.Editor editor = sp.edit(); //2实例化editor可有可无
			sp.edit()
					.putString("TaskBean", new Gson().toJson(mCurrentTaskBean))
					.commit();
		}
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new TimeFileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}
}
