package com.easymap.memcached.hessian.servlet;

import com.easymap.base.queue.AddSub;
import com.easymap.base.queue.ResultSub;
import com.easymap.base.queue.engine.MyAbstractITaskEngine;

public class ProxyHessianFactory implements HessianHelloWord{
	public static Object key=new Object();
    public static ProxyHessianFactory proxy=null;
    public  MyAbstractITaskEngine mytask=new MyAbstractITaskEngine();
    public static ProxyHessianFactory getInstance(){
    	if(proxy==null){
    		synchronized (key) {
    			proxy=new ProxyHessianFactory();
    		 }
    	} 
    	return proxy;
    	 
    }
	@Override
	public ResultSub get(String senderID, String ServiceID, String Id,
			String themeCode, String tableCode) {
		return mytask.get(senderID, ServiceID, Id, themeCode, tableCode);
	}

	@Override
	public boolean set(AddSub addSub) {
		return mytask.set(addSub);
	}
	
    public void start(){
    	   MyAbstractITaskEngine.start();
    }
}
