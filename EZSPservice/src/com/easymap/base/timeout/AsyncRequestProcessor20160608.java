package com.easymap.base.timeout;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;

import com.easymap.modle.SRV.SRVQueryDataModle;


public class AsyncRequestProcessor20160608 implements Runnable {

	 private AsyncContext asyncContext;
	  
	 private String senderID,
	 dataObjectCode,  themeCode,
	 methodName ,where, fields, order;
	 private long start,  max;
	 private boolean istotal;
	    public AsyncRequestProcessor20160608() {
	    }
	 
	    public AsyncRequestProcessor20160608(AsyncContext asyncCtx, String senderID, String dataObjectCode,
	    		String themeCode,
				String methodName, String where, String fields, String order,
				long start, long max,boolean istotal) {
	        this.asyncContext = asyncCtx;
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
	        	asyncContext.getResponse().setContentType("text/html;charset=utf-8");
	            PrintWriter out = asyncContext.getResponse().getWriter();
	           // out.write(result);
	            out.print(result);
				out.flush();
				out.close();
	        } catch (Exception e) {
	        }
	        asyncContext.complete();
	    }
	 
	    private String longProcessing() {
	        String result="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	    	SRVQueryDataModle sqdr = new SRVQueryDataModle();
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
