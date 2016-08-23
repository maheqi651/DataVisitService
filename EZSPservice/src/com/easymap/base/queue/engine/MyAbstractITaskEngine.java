package com.easymap.base.queue.engine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.easymap.base.queue.AddSub;
import com.easymap.base.queue.AgainstSub;
import com.easymap.base.queue.PreCheckSub;
import com.easymap.base.queue.ResultSub;
import com.easymap.base.readdatabase.ConnectioFWKZ;
import com.easymap.filter.Tools;
import com.easymap.memcached.guard.SafeTask;
import com.easymap.memcached.hessian.servlet.HessianHelloWord;

public class MyAbstractITaskEngine extends AbstractITaskEngine implements
		ITaskEngine {
	//select * from EZ_P_FUNCTION_SERVICE t  where t.chk_timespan>0
	  private static String sql="SELECT T.* FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE  T.chk_timespan>0 ";
         
	  private final static Object keyObject=new Object();
	    
		@Override
		public void Init() {
			   //初始化队列，从数据库查询
			ConnectioFWKZ cfwdb=new ConnectioFWKZ();
			List<Map<String,Object>> initlist=cfwdb.executeQueryMAP(sql, null);
			if(initlist!=null)
			{
				for(Map<String,Object> initmap:initlist)
				{
					PreCheckSub pre=new PreCheckSub();
					pre.setBlockTime(new Date());
					pre.setID(String.valueOf(initmap.get("ID")));
					pre.setSenderID((String)initmap.get("FUNCCODE"));
					pre.setServiceID((String)initmap.get("SERCODE"));
					pre.setCurCount(0);
					//t.CHK_TIMESPAN,t.CHK_RECORDCOUNT,T.CHK_BLOCKTIMESPAN
					pre.setChkRecordCount(Integer.parseInt(String.valueOf(initmap.get("CHK_RECORDCOUNT"))));
					pre.setChkBlockTime(Integer.parseInt(String.valueOf(initmap.get("CHK_BLOCKTIMESPAN"))));
					pre.setTimespan(Integer.parseInt(String.valueOf(initmap.get("CHK_TIMESPAN"))));
					precheckQueue.add(pre);
				}
			}
			
	
		}
		@Override
		public void updateAginstQueue() {
			   Timer timer = new Timer();
	           timer.schedule(new AgainstUpdateTask(), 0,10*1000);			
		}
		@Override
		public void updateQueue() {
			   Timer timer = new Timer();
	           timer.schedule(new PreCheckUpdateTask(), 0,10*1000);
		}
		@Override
		public void updateAddQueue() {
			//先在违法队列比对，没有的话，更新到总队列
			// TODO Auto-generated method stub
			Timer timer = new Timer();
	        timer.schedule(new AddSubUpdateTask(), 0,10*1000);
		}
		 
		public void UpdateGetQueue(){
			        Timer timer = new Timer();
	                timer.schedule(new UpdateGetQueue(), 0,15*1000);
		}
		public  ResultSub get(String senderID, String ServiceID, String Id,
				String themeCode, String tableCode) {
			long times=System.currentTimeMillis();
			ResultSub resultsub=new ResultSub();
			resultsub.setFlag(true);
			synchronized (keyObject) {
				for(AgainstSub ast:againstGetQueue)
				{
					 if(senderID.equals(ast.getSenderID())&&ServiceID.equals(ast.getServiceID()))
					 {
						 resultsub.setFlag(false);//不可以访问
						 resultsub.setBlockTime(ast.getUpdateTime());
						 resultsub.setChkBlockTimeSpan(ast.getChkBlockTimeSpan());
						 break;
					 }
				}
			} 
			System.out.println("消耗："+(System.currentTimeMillis()-times));
			return resultsub;
		}
       
		public  boolean set(AddSub addSub) 
		{
			addSubQueue.add(addSub);
			return true;
		}
		
		class PreCheckUpdateTask extends TimerTask{

			@Override
			public void run() {
				   //更新总对列
				   //数据库查找所有队列
				ConnectioFWKZ cfwdb=new ConnectioFWKZ();
				List<Map<String,Object>> initlist=cfwdb.executeQueryMAP(sql, null);
				 
				   //遍历数据库队列，比对
				 if(initlist!=null)
				 {
					for(Map<String,Object> initmap:initlist)
					{
						dealQueue(initmap);
					}
				 } 
				  
				
				
			}
			   /* 
			    * 如果队列存在该条记录，检测时间是否条数是过线，如果过线把它添加到违法列表，
			    * 并把时间和访问次数更新为最新
			    * 如果条数没有过线，检测时间是否过期，过期的话，更新数据，没有过期跳到下一条记录
			    * */
			 public  void dealQueue(Map<String,Object> initmap){
				     String sendserid=(String)initmap.get("FUNCCODE");
				     String serviceid=(String)initmap.get("SERCODE");
				     /*  int chkcount=Integer.parseInt(String.valueOf(initmap.get("CHK_RECORDCOUNT")));
				     int timespan=Integer.parseInt(String.valueOf(initmap.get("CHK_TIMESPAN")));
				     int blocktimespan=Integer.parseInt(String.valueOf(initmap.get("CHK_BLOCKTIMESPAN")));
				     */
				     Date datenow=new Date();
				     boolean flag=true;
				    PreCheckSub delprecheck =null;
				     for(PreCheckSub prec:precheckQueue)
				     {
				    	 //System.out.println("-------dealQueue------------");
				    	 if(sendserid.equals(prec.getSenderID())&&serviceid.equals(prec.getServiceID()))
				    	 {
				    		 flag=false;
				    		 if(Integer.parseInt(String.valueOf(initmap.get("CHK_RECORDCOUNT")))!=prec.getChkRecordCount()||Integer.parseInt(String.valueOf(initmap.get("CHK_BLOCKTIMESPAN")))!=prec.getChkBlockTime()||Integer.parseInt(String.valueOf(initmap.get("CHK_TIMESPAN")))!=prec.getTimespan())
				    		 {   //如果数据库更新，更新队列
				    			 System.out.println("更新数据库中修改的row.................................SenderID:"+prec.getSenderID()+"..........serviceid:"+serviceid);
				    			 synchronized (precheckQueue) {
				    		     prec.setChkRecordCount(Integer.parseInt(String.valueOf(initmap.get("CHK_RECORDCOUNT"))));
				    		     prec.setChkBlockTime(Integer.parseInt(String.valueOf(initmap.get("CHK_BLOCKTIMESPAN"))));
				    		     prec.setTimespan(Integer.parseInt(String.valueOf(initmap.get("CHK_TIMESPAN"))));
				    		 }
				    		 }
				    		// checkqueue delete  dasub:BCF04011-9003-4EFE-9562-778879E4 GZSVR_ZT00136 检测时间段：0 阻止时间段： 1
				    		 if(prec.getChkRecordCount()<prec.getCurCount())
				    		 {//过限添加到违法队列
				    			 AgainstSub aginst=new AgainstSub();
				    			 aginst.setChkBlockTimeSpan(prec.getChkBlockTime());
				    			 aginst.setSenderID(prec.getSenderID());
				    			 aginst.setServiceID(prec.getServiceID());
				    			 aginst.setUpdateTime(new Date());
				    			 aginst.setCurCount(prec.getCurCount());
				    			 aginst.setChkRecordCount(prec.getChkRecordCount());
				    			 System.out.println("add checkqueue  aginst:"+aginst.getSenderID()+" "+aginst.getServiceID()+" 检测时间段："+aginst.getChkTimeSpan()+" 阻止时间段： "+aginst.getChkBlockTimeSpan());
				    			 synchronized (checkQueue) 
				    			 {
				    			 checkQueue.add(aginst);
				    			 }
				    			 delprecheck=prec;
				    			 
				    		 }else{
				    			 if(CompareDate(datenow,prec.getBlockTime(),prec.getTimespan()))
					    		 {
				    				 //过期更新数据
					    			 prec.setBlockTime(new Date());
					    			 prec.setCurCount(0);
					    		 } 
				    		 }
				    		 
				    		 break;
				    	 }
				     }
				     if(flag)
				     {//no this row add to queue
				    	    PreCheckSub pre=new PreCheckSub();
							pre.setBlockTime(new Date());
							pre.setID(String.valueOf(initmap.get("ID")));
							pre.setSenderID((String)initmap.get("FUNCCODE"));
							pre.setServiceID((String)initmap.get("SERCODE"));
							pre.setCurCount(0);
							//t.CHK_TIMESPAN,t.CHK_RECORDCOUNT,T.CHK_BLOCKTIMESPAN
							pre.setChkRecordCount(Integer.parseInt(String.valueOf(initmap.get("CHK_RECORDCOUNT"))));
							pre.setChkBlockTime(Integer.parseInt(String.valueOf(initmap.get("CHK_BLOCKTIMESPAN"))));
							pre.setTimespan(Integer.parseInt(String.valueOf(initmap.get("CHK_TIMESPAN"))));
							precheckQueue.add(pre); 
							System.out.println("add precheckQueue  aginst:"+pre.getSenderID()+" "+pre.getServiceID()+" 检测时间段："+pre.getTimespan()+" 阻止时间段： "+pre.getChkBlockTime());
				     }
				     if(delprecheck!=null)
				     {
				    	 synchronized (precheckQueue) {
				    		 precheckQueue.remove(delprecheck);
						  }
				     }
			 }
	    	
	    }
		public boolean CompareDate(Date nowdate,Date begindate,int starttimespan)
		{
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long tspan=nowdate.getTime()-begindate.getTime();
			//System.out.println(format.format(nowdate)+"---"+format.format(begindate));
			long comparespan=1000*60*starttimespan;
			if(tspan>=comparespan)
			{//过期
				return true;
			}
			return false;
		}
		//违法队列  时间到期清空
		class AgainstUpdateTask extends TimerTask{
			
			@Override
			public void run()
			{
				   List<AgainstSub> delcheckQueue=new ArrayList<AgainstSub>();
				   synchronized (checkQueue) {
					   for(AgainstSub asub:checkQueue)
					   {
						   //System.out.println("--111---");
						   Date nowdate=new Date();
						   if(CompareDate(nowdate, asub.getUpdateTime(), asub.getChkBlockTimeSpan()))
						   {   //去掉
							   delcheckQueue.add(asub);
						   }
					   }
					   for(AgainstSub dasub:delcheckQueue)
					   {
						   System.out.println("checkqueue delete  dasub:"+dasub.getSenderID()+" "+dasub.getServiceID()+" 检测时间段："+dasub.getChkTimeSpan()+" 阻止时间段： "+dasub.getChkBlockTimeSpan());
						   checkQueue.remove(dasub);
					   }
					   delcheckQueue=null;
				   }
			}
	    }
		
		class UpdateGetQueue extends TimerTask{

			@Override
			public void run() {
				   aginstUpdateQueue=new  ArrayList<AgainstSub>();
				   synchronized (checkQueue) 
				   {
					    for(AgainstSub ag:checkQueue)
					    {
					    	aginstUpdateQueue.add(ag);
					    }
				   }
				   synchronized (keyObject) {
				   againstGetQueue=aginstUpdateQueue;
				   }
				   
			}
		}
		
		
		//添加队列，仅仅往里面添加更新时间
		class AddSubUpdateTask extends TimerTask{
			@Override
			public void run() {
				   while(!addSubQueue.isEmpty())
				   {
					   System.out.println("--addd---");
					   AddSub asub=addSubQueue.poll();
					   updateTaskQueue(asub);
				   }
				   
				
			}
			public void updateTaskQueue(AddSub asub){
				 String senderid=asub.getSenderID();
				 String serviceid=asub.getServiceID();
				 int upcount=asub.getAddCount();
				 System.out.println(upcount);
				 synchronized (precheckQueue) {
					 for(PreCheckSub prec:precheckQueue)
					 { 
						 if(senderid.equals(prec.getSenderID())&&serviceid.equals(prec.getServiceID()))
						 {
							 prec.setCurCount(prec.getCurCount()+upcount);
							 break;
						 }
					 }
				 }
		    	  
			}
	    	
	    }
		
		public static void start(){
			   System.out.println("------访问维护程序开始启动------");
			   MyAbstractITaskEngine myab= new MyAbstractITaskEngine();
			   myab.Init();
			   myab.updateQueue();
			   myab.updateAginstQueue();
			   myab.updateAddQueue();
			   myab.UpdateGetQueue();
			   System.out.println("------访问维护程序启动完成------");
		}
		public static void stop(){

		}

		

}
