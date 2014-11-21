package com.etrust.stategrid.utils;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import android.os.Handler;
import android.util.Log;
//多线程下载，
public class DownloadTask extends Thread {
	private int blockSize, downloadSizeMore;
	private int threadNum = 3;
	String urlStr, threadNo, fileName;
	
	private int downloadedSize = 0;
	private int fileSize = 0;
	Handler handle;
	public DownloadTask(String urlStr, int threadNum, String fileName,Handler handle) {  
		this.urlStr = urlStr;
		this.threadNum = threadNum;
		this.fileName = fileName;
		this.handle =handle;
	}

	@Override
	public void run() {
		FileDownloadThread[] fds = new FileDownloadThread[threadNum];
		try {
			URL url = new URL(urlStr);//文件位置
			URLConnection conn = url.openConnection();
		//	conn.setr
		//	conn.setConnectTimeout(timeoutMillis);
			//1 得到文件长度
			fileSize = conn.getContentLength();
			//blockSize = fileSize / threadNum;
			//上面的算法错误，没有考虑有余数的情况
			blockSize =(fileSize +threadNum-1)/threadNum;//计算每条线程下载的长度
			//downloadSizeMore = (fileSize % threadNum);
			final File file = new File(fileName);//本地存储的名字
			if(file.exists()){
				return;
			}
			if (!file.getParentFile().exists()) {
				boolean b = file.getParentFile().mkdirs();
				Log.i("Lucien_media", "mkdirs:" + b);
			}
			for (int i = 0; i < threadNum; i++) {
				int beginIndex=i*blockSize;
				int endIndex=(i + 1) * blockSize - 1;
				if(i==threadNum-1){
					endIndex=fileSize-1;
				}
				FileDownloadThread fdt = new FileDownloadThread(url, file,beginIndex, endIndex);
				fdt.setName("Thread" + i);
				fdt.start();
				fds[i] = fdt;
			}
			boolean finished = false;
			while (!finished) {
				//downloadedSize = downloadSizeMore;
				finished = true;
				for (int i = 0; i < fds.length; i++) {
					//downloadedSize += fds[i].getDownloadSize();
					if (!fds[i].isFinished()) {
						finished = false;
					}
				}
			}
			if(handle!=null){
				handle.sendEmptyMessage(99);
			}
			Log.i("Lucien_down", "url:"+urlStr);
		} catch (Exception e) {

		}

	}
}
