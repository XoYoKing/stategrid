package com.etrust.stategrid.utils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
 
import android.util.Log;

public class FileDownloadThread extends Thread{
    private static final int BUFFER_SIZE=1024;
    private URL url;
    private File file;
    private int startPosition;
    private int endPosition;
    private int curPosition;//已下载文件长度
    private boolean finished=false;
    private int downloadSize=0;
    public FileDownloadThread(URL url,File file,int startPosition,int endPosition){
        this.url=url;
        this.file=file;
        this.startPosition=startPosition;
        this.curPosition=startPosition;
        this.endPosition=endPosition;
    }
    @Override
    public void run() {
        BufferedInputStream bis = null;
        RandomAccessFile fos = null;                                               
        byte[] buf = new byte[BUFFER_SIZE];
        URLConnection con = null;
        try {
            con = url.openConnection();
            con.setAllowUserInteraction(true);
            	//3指定文件从什么位置下载，到什么位置结束
            con.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);
            //4保存文件指定每个线程从什么位置写入数据
            fos = new RandomAccessFile(file, "rwd");
            //写入文件的位置
            fos.seek(startPosition);
            bis = new BufferedInputStream(con.getInputStream());//得到写入的文件 
            int len = 0; 
            while (((len=bis.read(buf, 0, BUFFER_SIZE))!=-1)) {
            	fos.write(buf, 0, len);//循环写入
              //  curPosition = curPosition + len;
            }
            this.finished = true;
            bis.close();
            fos.close();
            System.out.println("down load "+file);
        } catch (IOException e) {
          Log.d(getName() +" Error:", e.getMessage());
        }
    }
 
    public boolean isFinished(){
        return finished;
    }
 
    public int getDownloadSize() {
        return downloadSize;
    }
}
