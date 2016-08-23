package com.easymap.servlet.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair; 

public class SearchServiceTest {
	public static void main(String[] args) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
		// It may be more appropriate to use FileEntity class in this particular 
        // instance but we are using a more generic InputStreamEntity to demonstrate
        // the capability to stream out data from any arbitrary source
        // 
        // FileEntity entity = new FileEntity(file, "binary/octet-stream"); 
       // BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("E:/wokeTool/workSpace8.5/EZSPservice/WebRoot/xml/requestFWZY.xml"))); 
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\tomcat6\\webapps\\EZSPservice\\xml\\requestFWSS.xml"),"utf-8")); 
        String data = null; 
        String s ="";
        while((data = br.readLine())!=null){ 
        	//System.out.println(data); 
        	s+=data;
        } 
        System.err.println(new String(s.getBytes(),"UTF-8"));
        HttpPost httppost = new HttpPost("http://localhost:8080/EZSPservice/SearchServiceaction");
        System.out.println(httppost.getURI());
        System.out.println("executing request " + httppost.getRequestLine());
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
        nvps.add(new BasicNameValuePair("str", URLEncoder.encode(s, "utf-8")));  //需要编码转换
        httppost.setEntity(new UrlEncodedFormEntity(nvps));  
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == 200) {
        	HttpEntity result = response.getEntity();
        	String ret = EntityUtils.toString(result);
        	System.err.println(ret);
        	}
        HttpEntity resEntity = response.getEntity();
        System.out.println("----------------------------------------");
        if (resEntity != null) {
            System.out.println("Response content length: " + resEntity.getContentLength());
            System.out.println("Chunked?: " + resEntity.isChunked());
        }
        if (resEntity != null) {
            resEntity.consumeContent();
        }

        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();        
    }

}
