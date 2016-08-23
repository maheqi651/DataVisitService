package com.easymap.base.timeout;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebListener;

public class TimeOutAsyncListener implements AsyncListener {

	 @Override
	    public void onComplete(AsyncEvent asyncEvent) throws IOException {
	        System.out.println("AppAsyncListener onComplete");
	    }
	 
	    @Override
	    public void onError(AsyncEvent asyncEvent) throws IOException {
	        System.out.println("AppAsyncListener onError");
	    }
	 
	    @Override
	    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
	        System.out.println("AppAsyncListener onStartAsync");
	    }
	 
	    @Override
	    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
	        System.out.println("AppAsyncListener onTimeout");
	        ServletResponse response = asyncEvent.getAsyncContext().getResponse();
	        PrintWriter out = response.getWriter();
	        response.setCharacterEncoding("utf-8");
	        out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><NOAuthorization>Request TimeOut</NOAuthorization>");
	        
	    }

}
