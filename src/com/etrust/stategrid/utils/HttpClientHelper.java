package com.etrust.stategrid.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * @Name HttpClientHelper
 * @Description http请求帮助类，取得线程安全的httpclient对象
 * @Date 2013-2-16
 * @author max
 */
public class HttpClientHelper {
	private static HttpClient customerHttpClient;
	private static final int CONNECTION_TIMEOUT = 10 * 1000; // 连接超时10秒

	private HttpClientHelper() {

	}
	/**
	 * 创建线程安全的HttpClient add by max [2012-10-22]
	 * 
	 * @return
	 */
	public static synchronized HttpClient getHttpClient() {

		if (null == customerHttpClient) {

			HttpParams params = new BasicHttpParams();

			// 设置一些基本参数

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			HttpProtocolParams.setUseExpectContinue(params, true);

			HttpProtocolParams
					.setUserAgent(

							params,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "

									+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");

			// 超时设置

			/* 从连接池中取连接的超时时间 */

			ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT);

			/* 连接超时 */

			HttpConnectionParams.setConnectionTimeout(params,
					CONNECTION_TIMEOUT);

			/* 请求超时 */

			HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);

			// 设置我们的HttpClient支持HTTP和HTTPS两种模式

			SchemeRegistry schReg = new SchemeRegistry();

			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));

			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建HttpClient

			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);

			customerHttpClient = new DefaultHttpClient(conMgr, params);

		}

		return customerHttpClient;
	}

	public static String post(String url, List<NameValuePair> params) {
		HttpPost request = new HttpPost(url);
		String msg = "";

		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpClient httpClient = getHttpClient();
			// 设置超时时间10秒
			httpClient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							CONNECTION_TIMEOUT);
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				msg = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return msg;
	}
	public static void saveHtml(String url,String path){
		try {
			StringBuffer htmlContent = new StringBuffer();
			HttpClient client = getHttpClient();
			HttpParams httpParams = client.getParams();

			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);

			HttpResponse response = client.execute(new HttpPost(url));
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(entity.getContent(), "UTF-8"),
						512);//用inputStreamreader把字节流转换为字符流，然后作为缓冲字符流reader的参数，然后可以使用readline()读取每一行字符串 

				String line = null;

				while ((line = reader.readLine()) != null) {
					htmlContent.append(line + "\n");
				}
				reader.close();
				String searchImgReg = "(?x)(src|SRC|background|BACKGROUND)=('|\")(http://.*?/)(.*?.(jpg|JPG|png|PNG|gif|GIF))('|\")";
				
				Pattern pattern = Pattern.compile(searchImgReg);
				Matcher matcher = pattern.matcher(htmlContent);
				while (matcher.find()) {
					String g3 = matcher.group(3);
					String g4 = matcher.group(4);
					String picName = g4.substring(g4.lastIndexOf("/")+1);
					String picPath = Constant.File_Path+picName;
					File localFile=new File(picPath);
					if(localFile.exists()){
						continue;
					}
					new DownloadTask(g3+g4,5, picPath,null).start();
				}
				// 修改图片链接地址
				pattern = Pattern.compile(searchImgReg);
				matcher = pattern.matcher(htmlContent);
				StringBuffer replaceStr = new StringBuffer();
				while (matcher.find()) {
					String g4 = matcher.group(4);
					String picName = g4.substring(g4.lastIndexOf("/")+1);
					matcher.appendReplacement(replaceStr, matcher.group(1)
							+ "='file://"+Constant.File_Path+picName + "'");
				}
				matcher.appendTail(replaceStr);// 添加尾部
				File file = new File(path);
				if (!file.getParentFile().exists()) {
					boolean b = file.getParentFile().mkdirs();
					Log.i("Lucien_html", "mkdirs:" + b);
				}
				FileWriter fw = new FileWriter(file);   
				fw.write(replaceStr.toString());
				fw.flush();
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getContent(String url) {
		try {
			StringBuffer sb = new StringBuffer();

			HttpClient client = getHttpClient();
			HttpParams httpParams = client.getParams();

			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);

			HttpResponse response = client.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(entity.getContent(), "UTF-8"),
						8192);

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				reader.close();
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	public static boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
	        if (mNetworkInfo != null) {  
	        	return mNetworkInfo.isAvailable();  
	        }
	    }  
	    return false;  
	}
}
