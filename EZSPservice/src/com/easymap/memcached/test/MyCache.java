package com.easymap.memcached.test;
import java.util.Date;

import com.alisoft.xplatform.asf.cache.memcached.client.MemCachedClient;
import com.alisoft.xplatform.asf.cache.memcached.client.SockIOPool;


public class MyCache {
	public static void main(String[] args) {
		MemCachedClient client=new MemCachedClient();
		String [] addr ={"172.18.70.37:11211"};
		Integer [] weights = {3};
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers(addr);
		pool.setWeights(weights);
		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(200);
		pool.setMaxIdle(1000*30*30);
		pool.setMaintSleep(30);
		pool.setNagle(false);
		pool.setSocketTO(30);
		pool.setSocketConnectTO(0);
		pool.initialize();
 
	 
	    client.setCompressEnable( true );          
		client.setCompressThreshold( 64 * 1024 ); 
		
		Date date=new Date(2000000);
		client.set("test1","test1", date);
		
		String str =(String)client.get("test1");
		 
	}
}
