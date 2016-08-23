package com.easymap.hbase.util;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;



public class Constants {
  
	public final static PatternLayout PATTERN_LAYOUT = new PatternLayout("%p %t %c - %m%n");
	public static Logger LOGGER = Logger.getLogger("EzBigData系统及运行日志");
	public static Level RUNNING_LOG_LEVEL=Level.ERROR;
	public static Level IMPORT_LOG_LEVEL=Level.ERROR;
	public static String ROWKEYCON="_";
	public static long IMPORT_CACHE_SIZE = 10;

	//TO DO进一步工作应当是将代码中所有“HBase01”替换
	public final static String DEFAULT_HBASE_INSTANCE="HBase01";
	
	public static String EZDBAAS_URL;
	public static String PATH;
	public static String HBASE_INSTANCES_CONFIG_PATH;
	
	//由于导入方面设置缓存的缘故，所以1个映射只能对应1个HBaseAccessor，所以每个映射导入线程数不能超过这个值
	public final static int HTABLE_POOL_SIZE= 10;//每个HBaseAccessor对应的PoolSize,不是全局的。
	
	public static String HBASE_ORACLE_MAPPING_PATH;
	public final static String GROUP_JOINER = "#";//用于rowKey、索引的连接，及索引表表名。索引表表名
	//主表名+连接符+索引名称+连接符+索引类别+连接符+索引CF+连接符+索引Q+连接符+索引CF。。。
	public final static String TRACE_COMBINE_ROWKEY_TIME_FORMAT="yyyyMMdd";
	public final static String REGISTER_COMBINE_ROWKEY_TIME_FORMAT="yyyyMMddHH";
	public final static String DEFAULT_KV_ROWKEY_TIME_FORMAT ="yyyyMMddHHmmss";
	public final static String TRACE_SPATIAL_LEVEL="14";
	public final static String REGISTER_SPATIAL_LEVEL="14";
	public final static int ROW_SUBMIT_QUANTITY=10000 ;
	static String[] ost= {"VARCHAR2","VARCHAR","CHAR","NVARCHAR","varchar","char","binary",
		"varbinary","longtext","mediumtext","tinytext","text"};
//	static String[] ost= {"VARCHAR2","VARCHAR","NVARCHAR"};
	public final static List<String> ORACLE_STRING_TYPES=Arrays.asList(ost);
	static String[] ont={"BINARY_FLOAT","BINARY_DOUBLE","NUMBER","LONG","DOUBLE",
		"FLOAT","int","decimal","bigint","mediumint","smallint","tinyint","double",
		"float"};
	public final static List<String> ORACLE_NUMBER_TYPES=Arrays.asList(ont);
	static String[] odt={"DATE","TIMESTAMP","date","timestamp","datetime","time","year"};
	public final static List<String> ORACLE_DATE_TYPES=Arrays.asList(odt);
	public final static String HBASE_DATE_FORMAT="yyyyMMddHHmmss";
	public final static String ORACLE_DATE_FORMAT="yyyymmddhh24miss";
	public final static String MYSQL_DATE_FORMAT="%Y%m%d%H%i%s";
	
	public final static int WRITE_BUFFER_SIZE=1024 * 1024 * 128;
	public final static int SCANNER_CACHING=250000;
	public final static String ENCODE="UTF-8";
	public final static boolean WAL_OPEN=false;
	public static int NUMBER_OF_NODES;
	
	static String[] st={"STRING","NUMBER","DATE"};
	public final static List<String> STREAM_TYPES=Arrays.asList(st);
	static String[] sdt={"DATE"};
	public final static List<String> STREAM_DATE_TYPES=Arrays.asList(sdt);
	public final static String STREAM_QAS_PACKAGE="com.easymap.ezDBaaSManager.streamQAS";
	
	public final static String DEFAULT_CF="DEFAULT";
	
	public final static boolean IS_HBASE_DEPLOYED_ON_HDFS=false;
	public final static String HBASE_ROOTDIR_IN_HDFS="/hbase";
	
	public final static String OUTPUT_FORMAT="yyyy/MM/dd HH:mm:ss";
	public final static String INPUT_FORMAT="yyyy/MM/dd HH:mm:ss";
	public final static String INPUT_NOSPACE_FORMAT="yyyyMMddHHmmss";
	
	public final static String HBASE_CLIENT_KEYVALUE_MAXSIZE = "0";
	//HBase最大Cell大小，设为0即是没有限制。
	public final static Object ENTRY_COUNT_LOCK = new Object();
	public static List<String> MAPREDUCE_IN_PROGRESS = new ArrayList<String>();
	
	public final static char FUZZY_MASK_CHAR='?';
	public final static char TOP_MASK_CHAR=' ';
	
	 public static String ROW_END;
	 static{
		 byte[] bs = {-12,-113,-65,-65};//UTF-8中排位最后的字符。
			try {
				ROW_END=new String(bs,Constants.ENCODE);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	 
	 public static String CONFIG_BACKUP_FOLDER;
	 static{
		 String os = System.getProperty("os.name");
		 if (os != null && os.startsWith("Windows")) {   
			 CONFIG_BACKUP_FOLDER="D:\\EzDBaaSManagerConfigBackUp\\";
		 }
		 else if(os != null && os.startsWith("Linux")){
			 CONFIG_BACKUP_FOLDER="/home/hadoop/EzDBaaSManagerConfigBackUp/";
		 }
		 File f = new File(CONFIG_BACKUP_FOLDER);
		 if(!f.exists()){
			 f.mkdir();
		 }
		 
	 }
	 public final static String CONFIG_BACKUP_TIME_FORMAT="_MM_dd";
	 public final static long  CONFIG_BACKUP_INTERVAL=24*60*60*1000;
	 
		public final static String SECONDARY_INDEX_COLUMNFAMILY="DEFAULT";
		 public final static String SECONDARY_INDEX_QUALIFIER_RK="rowkey";
		 
	public final static String MAPPING_META_DATA_TABLE="EzBigDataMapping";
	public final static String MAPPING_META_DATA_FAMILY="DEFAULT";//表示字段映射属性数据的列族，包括dataType、OracleURL等
	public final static String MAPPING_COLUMN_DATA_FAMILY="ColumnMapping";//表示字段映射的关系的列族
	public final static String MAPPING_SCRIPT_PATH_QUALIFIER = "scriptPath";
	public final static String MAPPING_SCRIPT_TYPE_QUALIFIER = "scriptType";
	public static void main(String[] args){
		System.out.print(Constants.class.getResource("../").getPath());
	}
}
	

