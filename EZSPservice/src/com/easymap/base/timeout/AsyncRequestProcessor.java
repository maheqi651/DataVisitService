package com.easymap.base.timeout;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import com.easymap.modle.SRV.SRVQueryDataModle;
import com.easymap.modle.SRV.SRVQueryDataModleTimeout;


public class AsyncRequestProcessor implements Runnable {

	 private HttpServletResponse response;
	  
	 private String senderID,
	 dataObjectCode,  themeCode,
	 methodName ,where, fields, order;
	 private long start,  max;
	 private boolean istotal;
	    public AsyncRequestProcessor() {
	    }
	 
	    public AsyncRequestProcessor(HttpServletResponse response, String senderID, String dataObjectCode,
	    		String themeCode,
				String methodName, String where, String fields, String order,
				long start, long max,boolean istotal) {
	        this.response = response;
	        this.senderID=senderID;
	        this.dataObjectCode=dataObjectCode;
	        this.themeCode=themeCode;
	        this.methodName=methodName;
	        this.where=where;
	        this.fields=fields;
	        this.max=max;
	        this.start=start;
	        this.istotal=istotal;
	        this.order=order;
	    }
	 
	    @Override
	    public void run() {
	       
	        String result=longProcessing();
	        try {
	        	response.setContentType("text/html;charset=utf-8");
	            PrintWriter out = response.getWriter();
	           // out.write(result);
	            out.print(result);
				out.flush();
				out.close();
	        } catch (Exception e) {
	        }
	         
	    }
	 
	    private String longProcessing() {
	        String result="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	        SRVQueryDataModleTimeout sqdr = new SRVQueryDataModleTimeout();
			try {
				result += sqdr.getXML(senderID, dataObjectCode, themeCode,methodName,
						where, fields, order, start, max,istotal);
			} catch (Exception e) 
			{
				e.printStackTrace();
				result += "<ERR>" + e.getMessage() + "</ERR>";
			}finally {
				return result;
			}
	    }

}
