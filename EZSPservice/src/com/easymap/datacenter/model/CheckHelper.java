package com.easymap.datacenter.model;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

import com.easymap.datacenter.util.ConnectionDB;
import com.easymap.datacenter.util.Constant;
import com.easymap.datacenter.util.DBConnection;

 


/** * @author  Zhangxt 
 * @date 创建时间：2015年9月16日 下午10:07:15 
 * @version 1.0 
 * @parameter  
 * @since  
 * @return  */

public class CheckHelper {
/*
 * 单例模式，每次只是有个对象进行分析
 * 只有一次读取数据库里的规范的文件
 */
	// 从 数据元标识 到 id映射
	public TreeMap<String,Integer> deCodeToId = new TreeMap<String,Integer>();
	
	// 从 字段名称 到  id 映射
	public TreeMap<String,Integer> tagToId = new TreeMap<String,Integer>();
	
	//  从数据元的数据库里面找到所有的规范数据元
	public ArrayList<metaGold> metaList = new ArrayList<metaGold>();

	// 得到单例的对象
	private static CheckHelper uniqueInstance = new CheckHelper();
	
	
	// 得到同义词的集合，“描述”到 “数据元编码”的映射
	public TreeMap<String,String> DescribToMetaCode = new TreeMap<String,String>();

//
//	// 从“数据元编码”到“同义词集合”的映射，给个数据元，得到所有的同义词
//	public TreeMap<String,TreeSet<String>> MetaCodeToDeset 
//		= new TreeMap<String,TreeSet<String>>();
	
	TreeSet<String> ruleKeyWords = new TreeSet<String>(); 
	
	//
	public static String String_DT = "字符型";
	public static String Value_DT  = "数值型";
	public static String Date_DT = "时间日期型";
	public static String other_DT = "其他";
	public static String Bianry_DT = "二进制型";
	// 在这几个地方用到了
	//  meataDold 的RuleMatch 函数
	// 调用 的MapZdsjylx， checkoreal里面
	//  本文件里面的MapZdsjylx 函数
	///
	
	
	
	
	private static boolean ifDebug = false;
	
	private void clear(){
		this.deCodeToId.clear();
		this.DescribToMetaCode.clear();
		this.metaList.clear();
		this.ruleKeyWords.clear();
		this.tagToId.clear();
	
	}
	
	public static void Update() throws ClassNotFoundException, SQLException, IOException{
		System.out.println("更新....==================================");
		uniqueInstance.clear();
		uniqueInstance.inite(); // 第一部分
		System.out.println("更新完毕！==================================");
	}
	
	public static CheckHelper getInstance(boolean update) throws ClassNotFoundException, SQLException, IOException{
		uniqueInstance.clear();
		uniqueInstance.inite(); // 第一部分
		return uniqueInstance;
	}
	public static CheckHelper getInstance() {
	
		return uniqueInstance;
	}
	
	

	private CheckHelper( )   {

		/*
		 * 初始化首先读库
		 * 然后建立map表，方便查找
		 */
		try {
			inite(); // 第一部分
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("ifDebug:"+ifDebug);
	//	ifDebug = true;
		System.out.println("ifDebug-set ture:"+ifDebug);
		
		
		//调试
		if(ifDebug){
			System.out.println("Get all num from rule:");
			for(int i=0;i<metaList.size();i++)
				System.out.println(metaList.get(i).GetNum(ifDebug));
		}
	}
	
	private void buildMap(){
		
		// 建map，注意，在数据库里面读出来的话，可能是空或者null
		for(int i=0;i<metaList.size();i++){
			metaGold ameta = metaList.get(i);
			if(ameta.deCode==null||ameta.deCode.equals("")||this.deCodeToId.put(ameta.deCode, i)!=null)
				if(ifDebug)	System.err.println("deCode Mapping again or null :"+ameta.deCode);
			
			if(ameta.tag==null||ameta.tag.equals("")||this.tagToId.put(ameta.tag, i)!=null)
				if(ifDebug)		System.err.println("tag Mapping again or null :"+ameta.tag);
		}
	}
	
	// 20151028 对之前读到的数据元进行分析,有一些是有派生关系的，进行处理
	private void RefineMetaList(){
		for(int i=0;i<this.metaList.size();i++){
			metaGold amt = this.metaList.get(i);
			if(amt.releation!=null && !amt.releation.equals("")){
				
				int index = amt.releation.indexOf("DE");
				if(index == -1)
					continue;
				String decodeParent = amt.releation.substring(index,index+7);
				Integer meid = this.deCodeToId.get(decodeParent);
				if(meid==null)
				{
					System.err.println(amt.toString()+"|find parent eror|"+decodeParent);
					continue;
				}
				amt.format = this.metaList.get(meid).format;
				amt.dataType = this.metaList.get(meid).dataType;
			}
		}
	}
	
	// 2015 10 28 填充一下数据类型等
	private void FillDataType(){
		for(int i=0;i<this.metaList.size();i++){
			metaGold amt = this.metaList.get(i);
			amt.GetNum(ifDebug);
			if(amt.dataType==null || amt.dataType.equals(""))
			{
				amt.GetXtype();
			}
			else
			{
				if(amt.dataType.contains("(")||amt.dataType.contains("（"))
				{
					String x[] = amt.dataType.split("\\(");
					String y[] = x[0].split("（");
					amt.xtype = y[0].trim();
					System.out.println(amt.deCode+":"+amt.dataType+"=>"+amt.xtype);
					
				}
				else 
				amt.xtype = amt.dataType;//  有的话就不用解析了
			}
		}
	}
	
	
	
	// 这个地方是读oracle的库 20150920
	public static void GetSystemTableMetaList(){}
	
	
	
	
	// 读入数据库
	private void inite( ) throws ClassNotFoundException, SQLException, IOException  {		
		String keywords = Constant.RuleKeyWords;
		
		if(keywords==null||keywords.equals(""))
			ruleKeyWords.clear();
		else{
			if(keywords.endsWith(",")){
				System.out.println("RuleKeyWords 格式错误，请以逗号分隔，末尾不需要逗号！");
				System.exit(0);
			}
			String x[]=keywords.split(",");
			for(int i=0;i<x.length;i++){
				String tp = x[i].trim();
				if(tp!=null&&!tp.equals(""))
					ruleKeyWords.add(tp);
				System.out.println(tp);
			}
		}
		// 以上是读取完可以忽略的某些字串，最后会使用

		
		ConnectionDB dbCon = null;
		PreparedStatement pst = null;
		Connection con = null;
		ResultSet rs = null;
		try {
			dbCon = DBConnection.getEzspatialConnection();
			con = dbCon.getConnection();
			System.out.println("ok connected ,to inite instance helper...");
			pst = con.prepareStatement("select decode, tag,dataType,format,chname,releation"
							+ " from ez_dm_property "+
							"  where state = '标准'"
			);
			rs = pst.executeQuery();
			int countNum = 0;
			while (rs.next()) {
				metaGold  alog = new metaGold();
				countNum++;
				if (countNum % 50000 == 0)
					System.out.println("\n"+countNum+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++");						
				alog.dataType = ( rs.getString("dataType"));
				alog.deCode =  (rs.getString("decode"));
				alog.format =  ( rs.getString("format"));
				alog.tag =  (rs.getString("tag"));
				alog.chname = rs.getString("chname");
				alog.releation = rs.getString("releation");
				metaList.add(alog);	
		    }
		
			// !!!!!!!!!!!!!!!!!!!!!!!!!1
			System.out.println(" buildmap , refienMeatlist and  fillDataType....");
			buildMap(); // 第一步，读取所有的公安数据元
			RefineMetaList();// c处理一下有可能是会有上下位关系的数据元,并且填充相关的数据
			FillDataType();
			System.out.println(" Done ");
			
			// 20151010 一下是读取同义词集合，一个来自于数据元标准，一个来自于同义词的表
			 pst = con.prepareStatement("select decode, desynonym"
								+ " from ez_de_synonym "	);
			 rs = pst.executeQuery();			
		     countNum = 0;
			 while (rs.next()) {
				countNum++;
				if (countNum % 50000 == 0)
					System.out.println("\n"+countNum+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++");						
				String de = rs.getString("desynonym");
				String dec = rs.getString("decode");
				if(de!=null && dec!=null&&!de.equals("")&&!dec.equals(""))
				this.DescribToMetaCode.put(de,dec);			
			 }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			rs.close();
			pst.close();
			con.close();
		}
		
		// 补充,把真正的名字也要加进去
		for(int i=0;i<this.metaList.size();i++){
			metaGold amg = this.metaList.get(i);
			if(amg.chname!=null &&!amg.chname.equals("")){
				this.DescribToMetaCode.put(amg.chname, amg.deCode);				
			}
		}
		
		// 形成每个数据元的同义词集合 
		Iterator iter = this.DescribToMetaCode.entrySet().iterator();
		int oknum = 0;
        while(iter.hasNext()){
            Entry entry = (Entry) iter.next();
            String desc = (String) entry.getKey();
            String deco = (String) entry.getValue();
            Integer id = this.deCodeToId.get(deco);
            if(id!=null){
            this.metaList.get(id).addSynoymset(desc);
            
            oknum++;
            }
            else{
            	System.out.println("can not find in checkhelper  "+
            deco+"-"+desc+" 找到比例："+oknum+"/"+this.deCodeToId.size());
            	
            }
            //deal
        }	
		
		for(int i=0;i<this.metaList.size();i++){
			this.metaList.get(i).addchname();
			this.metaList.get(i).refine(ruleKeyWords);
		}
	}
	
	 
	
	public void checkByCode(metaData aMD){

		// 有数据元代码
		Integer id = this.deCodeToId.get(aMD.Code.source);
		if(id==null||id==-1){
			aMD.type = 2;// 完全不符合，没找到数据元 
			
		}
		else{// 在map中找到了code
			metaGold ameta = this.metaList.get(id);
			aMD.Code.ifMatch = true;				
		// 找到数据元的代码了，直接进行比较
			boolean ifallmatch = true;
			//tag 比较
			if(aMD.Name.source.equalsIgnoreCase(ameta.tag)){
				aMD.Name.ifMatch = true;
			}
			else{
				aMD.Name.fixed= "应该为:"+ameta.tag;
				aMD.Name.ifMatch = false;
				ifallmatch = false;
			}
			// 通过 RuleMatch函数解析规范标准format，进行类型和
			//describe 有么？ ====开始被拷贝				
			if(aMD.Describe.source==null || aMD.Describe.source.equals("")){
				aMD.Describe.ifMatch = false;
				aMD.Describe.fixed = "缺少字段描述";
				ifallmatch = false;
			}
			else{
				aMD.Describe.ifMatch = true;
			}
			
			// dataTypeb 比较 和 format一起													
			//format检查
			//
			if(ameta.RuleMatch(aMD)==true)
				aMD.MaxSize.ifMatch = true;
			else
				ifallmatch = false;
			
			if(ifallmatch == true)
				aMD.type = 0;// 全对
			else
				aMD.type = 1;								
		}
	
	}
	
	public void checkByTag(metaData aMD){

		// 有字段代码
		Integer id = this.tagToId.get(aMD.Name.source);
		if(id==null||id==-1){
			aMD.type = 2;// 完全不符合，没找到数据元 
		}
		else{
			
			metaGold ameta = this.metaList.get(id);
			boolean ifallmatch = true;
		//	aMD.Code.fixed = ameta.deCode+"-"+ameta.chname;// 这里不需要修改，在
			// rulematch 里面修正
			aMD.Code.ifMatch = false;// 如果是false，提供推荐
			
			// 有查到字段名，可以使用
			aMD.Name.ifMatch = true;
			
			////////////////////// 拷贝
										
			//describe 有么？
			
			if(aMD.Describe.source==null || aMD.Describe.source.equals("")){
				aMD.Describe.ifMatch = false;
				aMD.Describe.fixed = "缺少字段描述";
				ifallmatch = false;
			}
			else{
				aMD.Describe.ifMatch = true;
			}
			
			// dataTypeb 比较 和 format一起													
			//format检查
			
			//
			if(ameta.RuleMatch(aMD)==true)
				aMD.MaxSize.ifMatch = true;
			else
				ifallmatch = false;
			
			if(ifallmatch == true)
				aMD.type = 0;// 全对
			else
				aMD.type = 1;
			
			/////////////////////////////////////
		}
	
	}
	
	public void checkByDescrib(metaData aMD){
		String metacode = this.DescribToMetaCode.get(aMD.Describe.source);
		if(metacode!=null&&!metacode.equals(""))
		{
			aMD.Code.source = metacode;
			//赋值一下，这样就相当于有了一个数据元的编码了
		//	aMD.Code.fixed = "推荐数据元标识:"+metacode;
			checkByCode(aMD);
			aMD.Code.ifMatch = false;
			// 如果是false，提供推荐,注意，必须在 调用了函数的后面写，之前会在
			// checkbycode 这个函数里面对 ifmatch进行修改
		}
		// 查找失败，找 tag是否有
		else if(aMD.Name!=null && !aMD.Name.source.equals("")){
					
					checkByTag(aMD);
				}
					else{
						// 啥都没找到
						aMD.type= 2;
					}
	}
	
	// 这里是总体的控制，根据情况的不同，去调用不同的函数
	// 首先check 数据元标识字段
	// 再用同义词，填充上 数据元标识
	// 再查tag
	public void checkBy(metaData aMD){
		if((aMD.Code.source!=null && !aMD.Code.source.equals(""))){			
			checkByCode(aMD);
		}
		else if(aMD.Describe.source!=null&&
				(!aMD.Describe.source.equals("")))	{
			checkByDescrib(aMD);
		}
		// 查找失败，找 tag是否有
		else if(aMD.Name!=null && !aMD.Name.source.equals("")){
			
			checkByTag(aMD);
		}
			else{
				// 啥都没找到
				aMD.type= 2;
			}
		}



// 0920
	// 映射数据类型
	public String MapZdsjylx(String zdlx) {
		// TODO Auto-generated method stub
//	   	DATA_TYPE	COUNT(*)
//	   	9	VARCHAR2	5486 string
//	   	8	DATE	966 日期
//	   	5	NUMBER	450 value
//	   	6	CHAR	319 stirng
//	   	2	NVARCHAR2	33 stirng
//	   	4	TIMESTAMP(6)	17   日期
//	   	10	BLOB	13 二元
//	   	7	CLOB	4 ？
//	   	3	FLOAT	1 value
//	   	1	LONG RAW	1s 
	   	
		if(zdlx.startsWith("VARCHAR2")||zdlx.startsWith("CHAR")||zdlx.startsWith("NVARCHAR2"))
			return CheckHelper.String_DT ;
		else if(zdlx.startsWith("NUMBER")||zdlx.startsWith("FLOAT")||zdlx.startsWith("LONG"))
			return CheckHelper.Value_DT ;
		else if(zdlx.startsWith("DATE")||zdlx.startsWith("TIMESTAMP"))
			return CheckHelper.Date_DT;	
		else if(zdlx.startsWith("BLOB"))
			return CheckHelper.Bianry_DT;// 二进制
			
		return CheckHelper.other_DT;
	}
	
	// 1027
	// recommend 
	
	public ArrayList<String> GetrecommendFromDescri(boolean ifdebug ,String ades,int topsize,float threshold ,boolean ifRefine){
		
		
	//	ifdebug = true;
		metaGold aheapdata[] = new metaGold[topsize];
		
		
		// 定义比较函数
		Comparator<metaGold> compMax = new Comparator<metaGold>() {  
	      public int compare(metaGold o1, metaGold o2) {  
	    	          if( o1.samevaule - o2.samevaule < 0)
	    	        	  return 1;
	    	          else if(o1.samevaule == o2.samevaule)
	    	        	  return 0;
	    	          else return -1;
	    	       
	    	      }  
	    	  }; 
	    	  // 注意，这里生成之后就可以进行build的操作，形成堆了
	    ZTopK atopk = new ZTopK(aheapdata,compMax,aheapdata.length);
	    			
	    			
		
		ArrayList<String> alist = new ArrayList<String>();
		if(ades==null||ades.equals(""))
			return null;
		for(int i=0;i<this.metaList.size();i++){
			metaGold amG = this.metaList.get(i);
			amG.calSameDegree(ades,ifRefine);
			atopk.insertAnelement(amG, compMax);
			
			if(ifdebug)
			System.out.println("搭配： |"+ades+"|  insert: "+amG.samevaule+"-"+amG.chname+"-"+amG.synonymset);
			for (metaGold am : aheapdata) {
				if(am==null)
					continue;
				if(ifdebug)
				System.out.print(am.samevaule+"-"+am.chname + " ");
			}
			if(ifdebug)
			System.out.println();
		}
		
		atopk.sort();
		for(int i=0;i<aheapdata.length;i++){
			if(aheapdata[i].samevaule>=threshold)
				alist.add(aheapdata[i].deCode);
			if(ifdebug)
				System.out.print(aheapdata[i].samevaule+"-"+aheapdata[i].chname + " ");
		}
		if(ifdebug)
		System.out.println("\r\n 找到可推荐的数据元的数量："+alist.size());
		
		aheapdata = null;
		return alist;
		
	}
	
public ArrayList<String> GetrecommendFromDescri(String ades,boolean ifRefine){
	
		int topsize = 8;
		float threshold = 0.3f;
		boolean ifdebug = false;
	//	ifdebug = true;
		return this.GetrecommendFromDescri(ifdebug, ades, topsize,threshold, ifRefine);
	
	}



}
