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

public class QueryData2ServletTest {
	public static void main(String[] args) throws Exception 
	{
        HttpClient httpclient = new DefaultHttpClient();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\tomcat6\\webapps\\EZSPservice\\xml\\requestFZCX.xml"),"utf-8")); 
        String data = null; 
        String s ="";
        while((data = br.readLine())!=null){ 
            // System.out.println(data); 
        	s+=data;
        } 
        System.err.println(new String(s.getBytes(),"UTF-8"));
        HttpPost httppost = new HttpPost("http://localhost:8080/EZSPservice/QueryData2Servlet.action");
        System.out.println(httppost.getURI());
        System.out.println("executing request " + httppost.getRequestLine());
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
        nvps.add(new BasicNameValuePair("str", URLEncoder.encode(s, "utf-8")));   
        httppost.setEntity(new UrlEncodedFormEntity(nvps));  
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == 200)
        {
        	HttpEntity result = response.getEntity();
        	String ret = EntityUtils.toString(result);
        	System.err.println(ret);
        	}
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null)
        {
            resEntity.consumeContent();
        }
        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();        
    }
}
