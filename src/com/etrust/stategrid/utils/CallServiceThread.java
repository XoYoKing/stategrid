package com.etrust.stategrid.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//多线程下载问题
public class CallServiceThread extends Thread {
	private Handler handle = null;
	String url = null;
	String nameSpace = null;
	String methodName = null;
	HashMap<String, Object> params = null;

	Context context;
	ProgressDialog progressDialog = null;
	boolean threadRunning = true;

	public CallServiceThread(Handler _hander) {
		this.handle = _hander;
	}

	/*
	 * CallSeivice中调用这个方法
	 * 方法中最后一句启动线程，虚拟机执行run（）方法，执行指定的方法
	 */
	@SuppressWarnings("deprecation")
	public void doStart(String url, String nameSpace, String methodName,
			HashMap<String, Object> params, Context c, boolean alert) {
		this.url = url;
		this.nameSpace = nameSpace;
		this.methodName = methodName;
		this.params = params;
		this.context = c;

		if (alert) {
			progressDialog = new ProgressDialog(c);
			progressDialog.setTitle("网络连接");
			progressDialog.setMessage("正在请求，请稍等......");
			progressDialog.setIndeterminate(true);

			progressDialog.setButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int i) {
							// progressDialog.cancel();
							// threadstop();
						}
					});
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
						}
					});
			progressDialog.show();
		}
		threadRunning = true;
		this.start();
	}

	public void threadstop() {
		if (progressDialog != null)
			progressDialog.dismiss();
		threadRunning = false;
		this.interrupt();
	}

	/*run()获得webservice数据
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		super.run();
		Message message = handle.obtainMessage();
		Bundle b = new Bundle();
		b.putString("method", methodName);
		try {
			ConnectivityManager cwjManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cwjManager.getActiveNetworkInfo() != null) {
				boolean flag = cwjManager.getActiveNetworkInfo().isAvailable();
				if (!flag) {
					message.what = 999;//传出去的值是999
					b.putString("error", "对不起，暂时没有网络可以使用，请确定网络正常后再使用本系统！");
				} else {
					try {
						String result = CallWebService();//用Sting来接webservice返回的结果
						if (result.equals("")) {
							if (progressDialog != null)
								progressDialog.dismiss();
							return;
						}
						if (!threadRunning) {
							if (progressDialog != null)
								progressDialog.dismiss();
							return;
						}
						b.putString("data", result);

					} catch (Exception ex) {
						ex.printStackTrace();
						StringWriter sw = new StringWriter();
						PrintWriter printWriter = new PrintWriter(sw);
						ex.printStackTrace(printWriter);
						String error = "服务器连接失败";
						progressDialog.dismiss();
						message.what = 999;
						
						b.putString("error", error);

					}
				}
			} else {
				message.what = 999;
				b.putString("error", "对不起，暂时没有网络可以使用，请确定网络正常后再使用本系统！");
			}
		} catch (Exception ex) {
			b.putString("data", null);
			ex.printStackTrace();
		} finally {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

		}
		message.setData(b);
		handle.sendMessage(message);

		threadRunning = false;
	}

	/*调用webservice参考  当前类是用多线程实现异步可参考的方式还有AsyncTask
	 * http://www.open-open.com/bbs/view/1320111271749?sort=newest
	 */
	protected String CallWebService() throws Exception {
		String SOAP_ACTION = nameSpace + methodName;
		String response = "";
		// 6步来调用WebService的方法。
		// 1. 指定WebService的命名空间和调用的方法名，代码如下：
		SoapObject request = new SoapObject(nameSpace, methodName);
		// 3. 生成调用WebService方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述，代码如下：
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		// 创建SoapSerializationEnvelope对象时需要通过SoapSerializationEnvelope类的构造方法设置SOAP协
		// 议的版本号。该版本号需要根据服务端WebService的版本号设置。在创建SoapSerializationEnvelope对象后，不要忘了设置
		// SoapSerializationEnvelope类的bodyOut属性，该属性的值就是在第1步创建的SoapObject对象。
		if (params != null && !params.isEmpty()) {
			for (@SuppressWarnings("rawtypes")
			Iterator it = params.entrySet().iterator(); it.hasNext();) {
				Map.Entry e = (Entry) it.next();
				// 2. 设置调用方法的参数值，这一步是可选的，如果方法没有参数，可以省略这一步。设置方法的参数值的代码如下：
				request.addProperty(e.getKey().toString(), e.getValue());
			}// 要注意的是，addProperty方法的第1个参数虽然表示调用方法的参数名，但该参数值并不一定与服务端的WebService类中的方法参数名一致，只要设置参数的顺序一致即可。
		}

		envelope.bodyOut = request;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		//4. 创建HttpTransportSE对象。通过HttpTransportSE类的构造方法可以指定WebService的WSDL文档的URL，代码如下：
		HttpTransportSE androidHttpTrandsport = new HttpTransportSE(url);
		androidHttpTrandsport.debug = true;
		System.out.println(" androidHttpTrandsport " + androidHttpTrandsport);
		try {
			//5. 使用call方法调用WebService方法，代码如下：
			androidHttpTrandsport.call(SOAP_ACTION, envelope);
			if (threadRunning) {
				//6. 使用getResponse方法获得WebService方法的返回结果，代码如下
				// Log.i("Lucien", "bodyin:"+envelope.bodyIn);
				Object temp = envelope.getResponse();
				response = temp.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return response;
	}
}
