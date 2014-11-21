package com.etrust.stategrid.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StatFs;
import android.view.inputmethod.InputMethodManager;

public class Utils {

	public static String subString(String data, String match, String newStr) {

		StringBuffer sb = new StringBuffer();
		int b = data.indexOf(match);
		sb.append(data.substring(0, b));

		int e = data.indexOf("\",", b);
		String caixi = data.substring(b, e);

		int douhao = caixi.indexOf(",");
		if (douhao != -1) {

			String deal_one = caixi.substring(0, douhao) + "\",";
			String deal_two = "\"" + newStr + "\":\""
					+ caixi.substring(douhao + 1);
			sb.append(deal_one);
			sb.append(deal_two);
		} else {
			sb.append(caixi + "\",");
			sb.append("\"" + newStr + "\":\"");
		}
		sb.append(data.substring(e));
		return sb.toString();
	}

	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
							case '0' :
							case '1' :
							case '2' :
							case '3' :
							case '4' :
							case '5' :
							case '6' :
							case '7' :
							case '8' :
							case '9' :
								value = (value << 4) + aChar - '0';
								break;
							case 'a' :
							case 'b' :
							case 'c' :
							case 'd' :
							case 'e' :
							case 'f' :
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A' :
							case 'B' :
							case 'C' :
							case 'D' :
							case 'E' :
							case 'F' :
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default :
								throw new IllegalArgumentException(
										"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	@SuppressLint("NewApi")
	public static long getUsableSpace(File path) {
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	public static final int IO_BUFFER_SIZE = 8 * 1024;

	@SuppressLint("NewApi")
	public static int getBitmapSize(Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (hasHttpConnectionBug()) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	public static boolean hasHttpConnectionBug() {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
	}

	public static String yyyyMMddHHmmss(long time) {
		String fmt = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Date date = new Date(time);
		String dateStr = sdf.format(date);
		return dateStr;
	}
	/**
	 * 计算显示的发帖时间
	 * 
	 * @param time
	 * @return
	 */
	public static String MDHMtimeForKeyu(String time) {
		if (time == null)
			return time;
		String fmt = "MM月dd日    ";
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Date date = new Date(Long.parseLong(time));
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(date);
		Calendar endCalendar = Calendar.getInstance();
		int diffDay = DateUtils.getDiffDays(
				DateUtils.getFormatTime(startCalendar.getTime()),
				DateUtils.getFormatTime(endCalendar.getTime()));
		String dateStr = sdf.format(date);
		if (0 == diffDay) {
			long diffSecond = DateUtils.getDiff(
					DateUtils.getFormatTime(endCalendar.getTime()),
					DateUtils.getFormatTime(startCalendar.getTime()));
			if (diffSecond != 0) {
				int displayDiff = (int) (diffSecond / 60);
				if (displayDiff < 60) {
					dateStr = displayDiff + "分钟前";
				} else if (displayDiff < 60 * 24) {
					dateStr = (displayDiff / 60) + "小时前";
				}
			}
		}
		return dateStr;
	}
	public static String getYMDHMStime(Date date) {
		if (date == null) {
			return "null";
		}
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(date);
	}

	public static String getContentType(String url) {
		String contentType = null;
		HttpPost request = new HttpPost(url);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(request);
			contentType = response.getHeaders("Content-Type")[0].getValue();
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return contentType;
	}

	public static String formatAudioTime(String time) {
		if (time == null || "".equals(time.trim())) {
			return "";
		}
		int i = Integer.parseInt(time);
		int m = i / 60;
		int s = i % 60;
		return m + "'" + s + "\"";
	}

	public static boolean isCellPhoneNumber(String numberStr) {
		Pattern p = Pattern.compile("[0-9]{11}");
		Matcher m = p.matcher(numberStr);
		return m.matches();
	}

	public static boolean isCellEmailNumber(String emailStr) {
		Pattern p = Pattern
				.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
		Matcher m = p.matcher(emailStr);
		return m.matches();
	}

	public static boolean isPasswordGoodFormat(String password) {
		if (password.length() < 6 || password.length() > 16) {
			return false;
		}
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (c < 0 || c > 255) {
				return false;
			}
		}
		return true;
	}

	public static int dip2Px(Context context, int dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	public static int px2dip(Context context, int px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}
	public static String md5(String pars) {

		if (pars == null) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		MessageDigest md = null;
		String data = pars;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(data.getBytes());

			for (byte b : md.digest())
				buf.append(String.format("%02x", b & 0xff));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 截取url的最后一段做为图像文件的全名带文件类型
	 * 
	 * @param url
	 * @return 图像文件名
	 */
	public static String getFileFullName(String url) {
		try {
			if (url == null || "".equals(url)) {
				return url;
			}
			int start = url.lastIndexOf("/");
			if (start == -1) {
				return url;
			}
			return url.substring(start + 1);
		} catch (StringIndexOutOfBoundsException e) {

		}
		return url;
	}

	/**
	 * 
	 * @return name 生成随机名字
	 */
	public static String makeImgName(String dstName) {
		String name = "";

		String fileName = String.valueOf(Calendar.getInstance()
				.getTimeInMillis());
		String extName = dstName.substring(dstName.lastIndexOf("."));
		if (extName.equals(".JPG")) {
			extName = ".jpg";
		}
		if (extName.equals(".PNG")) {
			extName = ".png";
		}
		if (extName.equals(".BMP")) {
			extName = ".bmp";
		}
		if (extName.equals(".GIF")) {
			extName = ".gif";
		}
		if (extName.equals(".JPEG")) {
			extName = ".jpeg";
		}
		if (extName.equals(".Jpg")) {
			extName = ".jpg";
		}
		if (extName.equals(".Png")) {
			extName = ".png";
		}
		if (extName.equals(".Bmp")) {
			extName = ".bmp";
		}
		if (extName.equals(".Gif")) {
			extName = ".gif";
		}
		if (extName.equals(".Jpeg")) {
			extName = ".jpeg";
		}
		name = fileName + extName;
		return name;
	}

	public static void closeKeyboard(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static int getDistatce(double lat1, double lat2, double lon1,double lon2) { 
        double R = 6371; 
        double distance = 0.0; 
        double dLat = (lat2 - lat1) * Math.PI / 180; 
        double dLon = (lon2 - lon1) * Math.PI / 180; 
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) 
                + Math.cos(lat1 * Math.PI / 180) 
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) 
                * Math.sin(dLon / 2); 
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R; 
        return (int) (distance*1000); 
    }
	/**
	 * @param lat 纬度
	 * @param lag 经度
	 * @param fixedLat  定点纬度
	 * @param fixedLag  定点经度
	 * @param r 半径
	 */
	public static Boolean checkRange(Double lat,Double lag, Double fixedLat, Double fixedLag,
			Integer r) {
		double R = 6371;// 地球半径
		double distance = 0.0;
		int DEFAULT_DIV_SCALE = 10;//精度
		
		double dLat = Double.valueOf(new BigDecimal(String.valueOf((lat - fixedLat)))
				.multiply(new BigDecimal(String.valueOf(Math.PI)))
				.divide(new BigDecimal(String.valueOf(180)), DEFAULT_DIV_SCALE,
						BigDecimal.ROUND_HALF_EVEN).toString());
		double dLon = Double.valueOf(new BigDecimal(String.valueOf((lag - fixedLag)))
				.multiply(new BigDecimal(String.valueOf(Math.PI)))
				.divide(new BigDecimal(String.valueOf(180)),DEFAULT_DIV_SCALE,
						BigDecimal.ROUND_HALF_EVEN).toString());
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(lat * Math.PI / 180)
				* Math.cos(fixedLat * Math.PI / 180) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R * 1000;
		System.out.println(distance);
		if (distance > Double.valueOf(String.valueOf(r))) {
			return false;
		}
		return true;
	}
}
