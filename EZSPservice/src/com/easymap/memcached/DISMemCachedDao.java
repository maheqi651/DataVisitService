package com.easymap.memcached;

import java.util.Date;
import java.util.Set;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;

public class DISMemCachedDao {
	public static ICacheManager<IMemcachedCache> manager;  
    private static final String CLIENTNAME="mclient_0";
    private static final String CLIENTNAME2="mclient_2";
    private static final Date date; 
    public static IMemcachedCache cache;
    public static boolean flag=true;
    //manager.start();  
       static{
        	manager = CacheUtil.getCacheManager(IMemcachedCache.class,  
                    MemcachedCacheManager.class.getName()); 
            manager.setConfigFile("memcached.xml"); 
            manager.start();
            cache = manager.getCache(CLIENTNAME); 
            date=new Date(2000000);
        }
        public void addToMemCached(String key,Object obj,Date date){
        	try{
        		cache.put(key, obj, date);
        	}catch(Exception e){
        		initMemcached2();
        		cache.put(key, obj, date);
        	}
        	 
        }
        public static  void initMemcached2(){
        	    synchronized (DISMemCachedDao.class) {
        	    	
        	    	manager = CacheUtil.getCacheManager(IMemcachedCache.class,  
                            MemcachedCacheManager.class.getName()); 
        	    	if(flag)
        	    	{
        	    		flag=false;
        	    		try{
        	    			manager.setConfigFile("memcached2.xml"); 
                            manager.start();
                            cache = manager.getCache(CLIENTNAME2);
        	    		}catch(Exception e)
        	    		{
        	    			initMemcached2();
        	    		}
        	    	}else{
        	    		flag=true;
        	    		try{
        	    			manager.setConfigFile("memcached.xml"); 
                            manager.start();
                            cache = manager.getCache(CLIENTNAME); 
        	    		}catch(Exception e)
        	    		{
        	    			initMemcached2();
        	    		}
        	    		
        	    	}
                    
		    	}
        }
        
        public void addToMemCached(String key,Object obj){
        	try{
        		cache.put(key, obj, date);
        	}catch(Exception e){
        		initMemcached2();
        		cache.put(key, obj, date);
        	}
        }
        
        
        public void deleteFromMemCached(String key)
        {   
        	try{
        		if(cache.containsKey(key))
            	    cache.remove(key);
        	}catch(Exception e){
        		initMemcached2();
        	}
        	
        }
        public Set<String> keySet(){
        	Set<String> resultset=null;
        	try{
          	    resultset=cache.keySet();
        	}catch(Exception e){
        		initMemcached2();
        	}
        	return resultset;
        }
        public static Object getMemCached(String key){
        	Object obj=null;
        	try{
        	   
        	 obj=cache.get(key);
        	}catch(Exception e){
        		initMemcached2();
        	}
        	return obj;
        }
        public void closeConnection(){
        	try{
        		manager.stop();
           	}catch(Exception e){
           		initMemcached2();
           	}
        }
}
