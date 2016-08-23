package com.easymap.memcached;

import java.util.Date;
import java.util.Set;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;

public class MemCachedDao {
	public static ICacheManager<IMemcachedCache> manager;  
    private static final String CLIENTNAME="mclient_0";
    private static final Date date; 
    public static IMemcachedCache cache;
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
        		manager = CacheUtil.getCacheManager(IMemcachedCache.class,  
                        MemcachedCacheManager.class.getName()); 
                manager.setConfigFile("memcached.xml"); 
                manager.start();
                cache = manager.getCache(CLIENTNAME); 
                date=new Date(2000000);  
                cache.put(key, obj, date);
                
        	}
        }
        public void addToMemCached(String key,Object obj){
        	cache.put(key, obj,  date);
        }
        public void deleteFromMemCached(String key)
        {
        	if(cache.containsKey(key))
        	    cache.remove(key);
        }
        public Set<String> keySet(){
        	   return cache.keySet();
        }
        public static Object getMemCached(String key){
        	IMemcachedCache cache = manager.getCache(CLIENTNAME);  
        	return cache.get(key);
        }
        public void closeConnection(){
        	manager.stop();
        }
}
