package com.easymap.memcached.hessian.servlet;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianFactory;
import com.easymap.base.queue.AddSub;
import com.easymap.base.queue.ResultSub;

public class HessianTest {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
		   HessianProxyFactory proxy=new HessianProxyFactory();
		   HessianHelloWord hessianword=null;
		   String urlName="http://10.235.36.2:8099/EZSPservice/HessianHelloWord";
		   hessianword=(HessianHelloWord)proxy.create(HessianHelloWord.class, urlName);
		   
		   
		    AddSub addSub=new AddSub();
		   addSub.setAddCount(50);
		   addSub.setSenderID("KKZH");
		   addSub.setServiceID("GZSVR_ZT0003");
		   hessianword.set(addSub);  
		   ResultSub resultsub=hessianword.get("KKZH", "GZSVR_ZT0003", "", "", "");
		     //hessianword.helloword();
		   System.out.println("--------------------------");
           System.out.println(resultsub.isFlag());
		  
           //KKZH GZSVR_ZT0003
		   
		   
	}

}
