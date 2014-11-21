package com.etrust.stategrid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
/**
 * @name DownloadUtils
 * @description 下载工具类，主要用来通知栏更新
 * @author max
 * @date 2013-8-5
 * 
 */
public class DownloadUtils {
	private static final int DATA_BUFFER = 8192; // 8192

	public interface DownloadListener {
		public void downloading(int progress);
		public void downloaded();
	}

	public static long download(String urlStr, File dest, boolean append,
			DownloadListener downloadListener) throws Exception {
		int downloadProgress = 0;
		long remoteSize = 0;
		int currentSize = 0;
		long totalSize = -1;

		if (!append && dest.exists() && dest.isFile()) {
			dest.delete();
		}

		if (append && dest.exists() && dest.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(dest);
				currentSize = fis.available();
			} catch (IOException e) {
				throw e;
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		}

		HttpGet request = new HttpGet(urlStr);

		if (currentSize > 0) {
			request.addHeader("RANGE", "bytes=" + currentSize + "-");
		}

		HttpClient httpClient = HttpClientHelper.getHttpClient();

		InputStream is = null;
		FileOutputStream os = null;
		try {
			HttpResponse response = httpClient.execute(request);

			HttpEntity entity = response.getEntity();

			is = entity.getContent();
			remoteSize = entity.getContentLength();
			if (remoteSize <= 0) {
				throw new RuntimeException("无法获知文件大小");
			}
			if (is == null) {
				throw new RuntimeException("stream is null");
			}

			org.apache.http.Header contentEncoding = response
					.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				is = new GZIPInputStream(is);
			}
			if (!dest.exists()) { // 不存在则新建
				dest.getParentFile().mkdirs();
				dest.createNewFile();
			}
			os = new FileOutputStream(dest, append);
			byte[] buffer = new byte[DATA_BUFFER];
			int readSize = 0;
			while ((readSize = is.read(buffer)) > 0) {
				os.write(buffer, 0, readSize);
				os.flush();
				totalSize += readSize;
				if (downloadListener != null) {
					downloadProgress = (int) (totalSize * 100 / remoteSize);
					downloadListener.downloading(downloadProgress);
				}
			}
			if (totalSize < 0) {
				totalSize = 0;
			}

		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
		}

		if (totalSize < 0) {
			throw new Exception("Download file fail: " + urlStr);
		}

		if (downloadListener != null) {
			downloadListener.downloaded();
		}

		return totalSize;
	}
}