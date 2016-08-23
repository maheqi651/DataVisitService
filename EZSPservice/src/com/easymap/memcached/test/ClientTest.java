package com.easymap.memcached.test;

import java.util.ArrayList;
import java.util.List;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;

public class ClientTest {

		
		
		
		
	 
	
	 @SuppressWarnings("unchecked")  
	    public static void main(String[] args) {  
		         
			testthread t=new testthread();
			testthread t1=new testthread();
		    new Thread(t).start();
		    new Thread(t1).start();
	                
	                /*  
	                 *  long times=System.currentTimeMillis();
	               for(int i=0;i<100000;i++){
	        	   if(cache.get("bean"+i)!=null)
	               {
	        		    TestBean name=(TestBean) cache.get("bean"+i);
	                    System.out.println(name.getName());
	        		   //cache.remove("bean"+i);
	               }else
	               {
	            	 System.out.println("not found "+"bean"+i);
	               }
	              }*/
	         //  System.out.println("time:"+(System.currentTimeMillis()-times));
//	            List<TestBean> list=new ArrayList<TestBean>();  
//	            list.add(bean);  
//	            cache.put("beanList", list);  
//	            List<TestBean> listClient=(List<TestBean>)cache.get("beanList");  
//	            if(listClient.size()>0){  
//	                TestBean bean4List=listClient.get(0);  
//	                System.out.println(bean4List.getName());  
	            //}  
	        
	    }  

}
