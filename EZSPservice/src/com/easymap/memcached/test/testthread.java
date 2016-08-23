package com.easymap.memcached.test;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;

public  class  testthread implements Runnable{

	public static ICacheManager<IMemcachedCache> manager;  
	static{
		 manager = CacheUtil.getCacheManager(IMemcachedCache.class,  
	            MemcachedCacheManager.class.getName());  
	     manager.setConfigFile("memcached.xml");  
	     manager.start();  
	}
	
	@Override
	public void run() {
		
        String namestr="11004";
        
        try {  
            IMemcachedCache cache = manager.getCache("mclient_0");  
                for(int i=0;i<100;i++)
        {
    	    if(cache.get(namestr)!=null)
    	    {
    	    	 cache.put(namestr, Integer.parseInt((String.valueOf(cache.get(namestr)))) +i);  
    	    }else{
    	    	 cache.put(namestr, 0); 
    	    }
    	    System.out.println(cache.get(namestr));
           
        }
        }catch(Exception e){
        	e.printStackTrace();
        } finally {  
            //manager.stop();  
        }  
              
		
	}
	}