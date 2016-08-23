package com.easymap.base.pools;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

public class DBCPPools {
	static Map<String,BasicDataSource> bsdsmap=new HashMap<String,BasicDataSource>();
    private static  int count=0;
	private final static String CONNECT="@";
	static Object key=new Object();
	public   Connection getConnection(String driverName,String url,String username,String password) throws SQLException{
		if(bsdsmap.get(url+CONNECT+username+CONNECT+password)!=null)
		{        // System.out.println("get by map:"+url+CONNECT+username+CONNECT+password);
		         // long times=System.currentTimeMillis();
		         //Connection conn=bsdsmap.get(url+CONNECT+username+CONNECT+password).getConnection();
				 //System.out.println(count+"  end:"+(System.currentTimeMillis()-times)+"ms:"+url+CONNECT+username+CONNECT+password);
				 //count++;
				 return bsdsmap.get(url+CONNECT+username+CONNECT+password).getConnection();
		}else{
			synchronized(key){//防止多余加载
				//count++;
				BasicDataSource	ds=new BasicDataSource();
				ds.setDriverClassName(driverName);
				ds.setUrl(url);
				ds.setUsername(username);
				ds.setPassword(password);
				ds.setInitialSize(5);
				ds.setMaxActive(50);
				ds.setMaxIdle(30);
				ds.setMaxWait(6000000);
				ds.setRemoveAbandoned(true);
				ds.setRemoveAbandonedTimeout(300);
				bsdsmap.put(url+CONNECT+username+CONNECT+password, ds);
				System.out.println("new connection:"+url+CONNECT+username+CONNECT+password);
				return  ds.getConnection(); 
			}
		}
	}
	public static DBCPPools getInstance(){
		   return new DBCPPools();
	}
 
	
}
