package com.easymap.memcached.guard;

import java.util.Set;
import java.util.TimerTask;

import com.easymap.dao.authorizationDataDao;
import com.easymap.filter.SoutProx;
import com.easymap.filter.Tools;
import com.easymap.memcached.MemCachedFactory;
import com.easymap.modle.authorization.authorization;

public class SafeTask extends TimerTask {
			private  final static long timespace=Tools.CHECKTIME*1000*60;
			public static authorizationDataDao[] obj1=null;
			public static authorizationDataDao[] obj2=null;
			public static authorizationDataDao[] obj3=(authorizationDataDao[])authorization.getAuthorizationSpecialTablecode();
			 static int counts=1;
			@Override
			public void run() {
				counts++;
				SoutProx.sysoutlog("第"+counts+"开始执行......");
				long timestart=System.currentTimeMillis();
				initSafeTask();
				SoutProx.sysoutlog("第"+counts+"执行时间："+(System.currentTimeMillis()-timestart));
			}
			private static void initSafeTask()
			{
				//遍历
				Set<String> keys=MemCachedFactory.instance().keySet();
				if(keys!=null)
				{
					if(keys.size()>0)
						for(String str : keys)
						{//处理
							dealWith(MemCachedFactory.instance().getMemCached(str));
						}
				}
				obj1=null;
				obj2=null;
				obj3=null;
			}
			private static void dealWith(Object oj)
			{    
				long timetemp=System.currentTimeMillis();
				if(oj!=null)
				{
					if(oj instanceof ValueBean){
						//System.out.println("--------11111111111111111111111111----------------------");
						ValueBean vb=(ValueBean)oj;  
						if(timespace<=timetemp-vb.getTimes())
						{   //如果时间间隔
							deleteFromMemcached(vb);
						}
					}
					
				}
			}
			private static void deleteFromMemcached(ValueBean vb)
			{
				if(vb!=null)
				{
					authorizationDataDao[] objtemp=null;
					//key分为2中 
					 if(vb.getKey().contains(Tools.CONTROLLINK)){
						objtemp=findFromSql(3,vb.getMhtd(),vb.getSenderId());
						packaging(3,objtemp,vb);
					} else if(vb.getKey().contains(Tools.GetDATARESOURCEINFO)){//资源结构
						objtemp=findFromSql(1,vb.getMhtd(),vb.getSenderId());
						packaging(1,objtemp,vb);
					} else  {
						 		//专用接口 
						if(vb.getKey().contains(Tools.QUERY)&&!(vb.getSenderId()+Tools.QUERY).equals(vb.getKey())&&!(vb.getSenderId()+Tools.QUERYDATA).equals(vb.getKey()))
							objtemp=findFromSql(4,vb.getMhtd(),vb.getSenderId());
						else	//查询
						    objtemp=findFromSql(2,vb.getMhtd(),vb.getSenderId());
						packaging(2,objtemp,vb);
					}
				}
			}
			//封装
			private static boolean packaging(int count,authorizationDataDao[] objs,ValueBean vb)
			{
				if(objs!=null&&vb!=null){
					ValueBean vbm=new ValueBean();
					vbm.setKey(vb.getKey());
					vbm.setSenderId(vb.getSenderId());
					vbm.setMhtd(vb.getMhtd());
					vbm.setTimes(System.currentTimeMillis());
					String tempstr[]=new String[objs.length];
					if(count==1||count==2){
						for(int i=0;i<objs.length;i++)
						{
							tempstr[i]=objs[i].getTablecode();
							SoutProx.sysoutlog("1第"+counts+"次 "+tempstr[i]);
						}
					}else if(count==3){
						for(int i=0;i<objs.length;i++)
						{
							tempstr[i]=objs[i].getMethodname();
							SoutProx.sysoutlog("第"+counts+"次 "+tempstr[i]);
						}
					}
					vbm.setStr(tempstr);
					MemCachedFactory.instance().deleteFromMemCached(vb.getKey());//清除
					//SoutProx.sysoutlog("1111第"+counts+"次 "+vb.getKey()+"senderid"+vbm.getSenderId());
					 
					MemCachedFactory.instance().addToMemCached(vbm.getKey(), vbm);
					return true;
				}
				return false;
			}
			private static authorizationDataDao[] findFromSql(int count,String mtid,String senderId)
			{
				return getAuthorizationDataDao(count,mtid,senderId);
			}
			private static authorizationDataDao[] getAuthorizationDataDao(int count,String mtid,String senderId){
				 if(count==1)
				{
					
						obj1=(authorizationDataDao[])authorization.getAuthorizationTablecode4(senderId);
					
					return obj1;
				}else  
				if(count==2)
				{
					
						obj2=(authorizationDataDao[])authorization.getAuthorizationTablecode3(senderId, mtid);
					
					return obj2;
				}else if(count==3)
				{
					if(obj3==null)
					{
						obj3=(authorizationDataDao[])authorization.getAuthorizationSpecialTablecode();
					}
					return obj3;
				}else if(count==4)
				{
					return (authorizationDataDao[])authorization.getAuthorizationSpecialTablecodeFilter(mtid);
				}
				return null;
			}
}
