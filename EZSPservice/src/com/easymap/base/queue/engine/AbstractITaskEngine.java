package com.easymap.base.queue.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.easymap.base.queue.AddSub;
import com.easymap.base.queue.AgainstSub;
import com.easymap.base.queue.PreCheckSub;

public abstract class AbstractITaskEngine {
	            //检测队列，主要存放超过限制访问被禁止队列
	            
	            //protected static Vector<AgainstSub> checkQueue=new Vector<AgainstSub>();
	
	
	            protected static List<AgainstSub> againstGetQueue=new ArrayList<AgainstSub>();
	            protected static List<AgainstSub> aginstUpdateQueue=new ArrayList<AgainstSub>();
	            
	            protected static List<AgainstSub> checkQueue=new ArrayList<AgainstSub>();
                //更新队列，总队列
	           // protected static Vector<PreCheckSub> precheckQueue =new Vector<PreCheckSub>();
	            protected static List<PreCheckSub> precheckQueue =new ArrayList<PreCheckSub>();
                //待更新队列
	            protected static Queue<AddSub> addSubQueue=new ConcurrentLinkedQueue<AddSub>();
                //更新队列操作
                public abstract  void updateQueue();
                //更新添加队列
                public abstract  void updateAddQueue();
                
                public abstract  void updateAginstQueue();
                //初始化队列
                public abstract void Init();
}
