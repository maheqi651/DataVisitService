package com.easymap.memcached;

public class MemCachedFactory {
	   private static DISMemCachedDao memcache;
	   private static Object key=new Object();
       private MemCachedFactory(){}
       public static DISMemCachedDao instance(){
    	   synchronized (key) {
    		   if(memcache==null)
        	   {
					memcache=new DISMemCachedDao();
        	   }
		    }
    	   return memcache;
       }
}
