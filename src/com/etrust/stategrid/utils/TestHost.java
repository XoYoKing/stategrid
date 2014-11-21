package com.etrust.stategrid.utils;
  
import org.apache.http.HttpResponse; 
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.util.EntityUtils; 
  
public class TestHost{ 
    public static String hostConnect(String url) { 
       String result =null; 
       HttpGet httpGet = new HttpGet(url); 
       try { 
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet); 
            if (httpResponse.getStatusLine().getStatusCode() == 200) 
               {//使用getEntity方法活得返回结果 
                   result = EntityUtils.toString(httpResponse.getEntity()); 
                }else{ 
                    result = httpResponse.getStatusLine().toString(); 
                } 
       }catch (Exception e) { 
           System.out.println(e); 
       } 
       return result; 
    }
}
