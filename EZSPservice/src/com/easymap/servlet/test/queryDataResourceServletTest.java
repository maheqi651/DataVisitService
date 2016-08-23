package com.easymap.servlet.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import junit.framework.TestCase;
/**
 * 数据资源QueryDataResource
 * @author kate
 *
 */
public class queryDataResourceServletTest extends TestCase {

	public static void main(String[] args) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
		// It may be more appropriate to use FileEntity class in this particular 
        // instance but we are using a more generic InputStreamEntity to demonstrate
        // the capability to stream out data from any arbitrary source
        // 
        // FileEntity entity = new FileEntity(file, "binary/octet-stream"); 
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\tomcat6\\webapps\\EZSPservice\\xml\\requestSJZYCX.xml"))); 
        String data = null; 
        String s ="";
        while((data = br.readLine())!=null){ 
        //	System.out.println(data); 
        	s+=data;
        } 
        System.out.println(s);
        HttpPost httppost = new HttpPost("http://localhost:8080/EZSPservice/queryDataResourceServlet.action");
        System.out.println(httppost.getURI());
        System.out.println("executing request " + httppost.getRequestLine());
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
        nvps.add(new BasicNameValuePair("str", s));  
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
