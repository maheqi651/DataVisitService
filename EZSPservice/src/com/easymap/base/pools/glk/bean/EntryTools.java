package com.easymap.base.pools.glk.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.james.mime4j.field.Fields;

public class EntryTools {
      public static Set<String> RYJBXXSET=new HashSet<String>();
      public static Set<String> RYJBXXEXSET=new HashSet<String>();
      public static Set<String> WPJDCJBXX=new HashSet<String>();
      public static Set<String> WPJDCJBXXMT=new HashSet<String>();
      public static Map<String,String> dicmap=new HashMap<String,String>();
      public final static String DICMAPKEYFLAG="@";
      public static String ISSTARTFW="false";
	  public static String HESSIANURL=""; 
      public static String XMLPATH="D:\\apache-tomcat-7-8099\\webapps\\EZSPservice\\relation\\personLabels.xml";
      public static String WPXMLPATH="D:\\apache-tomcat-7-8099\\webapps\\EZSPservice\\relation\\wpLabels.xml";
      public static Map<String,String[]> FROMBJ=new HashMap<String,String[]>();
      public static Map<String,String[]> FROMWPBJ=new HashMap<String,String[]>();
      static{
    	  Field[] filds=RYJBXX.class.getDeclaredFields();
    	  for(Field f:filds)
    	  {
    		  RYJBXXSET.add(f.getName());
    	  }
    	  Field[] filds1=RYJBXXEX.class.getDeclaredFields();
    	  for(Field fs:filds1)
    	  {
    		  RYJBXXEXSET.add(fs.getName());
    	  }
    	  Field[] filds2=WPJDCJBXX.class.getDeclaredFields();
    	  for(Field fs:filds2)
    	  {
    		  WPJDCJBXX.add(fs.getName());
    	  }
    	  Field[] filds3=WPJDCJBXXMT.class.getDeclaredFields();
    	  for(Field fs:filds3)
    	  {
    		  WPJDCJBXXMT.add(fs.getName());
    	  }
      }
      
      public static void main(String args[]){
    	/*  long t=System.currentTimeMillis();
    	  Field[] filds=RYJBXX.class.getDeclaredFields();
    	  for(Field f:filds)
    	  {    
    		  //System.out.println(f.getName());
    		  RYJBXXSET.add(f.getName());
    	  }
    	  Field[] filds1=RYJBXXEX.class.getDeclaredFields();
    	  for(Field fs:filds1)
    	  {
    		  RYJBXXEXSET.add(fs.getName());
    		  //System.out.println(fs.getName());
    	  }
    	  System.out.println("时间："+(System.currentTimeMillis()-t));
    	  System.out.println(RYJBXXSET.contains("GZDB_UPDATETIME")+" time:"+(System.currentTimeMillis()-t));
    */	  
      }
      
}
