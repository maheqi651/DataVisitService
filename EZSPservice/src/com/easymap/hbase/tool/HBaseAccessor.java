package com.easymap.hbase.tool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.DependentColumnFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FuzzyRowFilter;
//import org.apache.hadoop.hbase.regionserver.StoreFile.BloomType;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.regionserver.BloomType;
//import org.apache.hadoop.hbase.io.hfile.Compression;
import org.apache.hadoop.hbase.io.compress.Compression;

import com.easymap.hbase.util.Constants;



public class HBaseAccessor {
	public String quorum;
	public String clientPort;
	public String defaultFSName;
//	public String hbaseTableName;
	public HTablePool htPool;
	public HBaseAdmin hbAdmin = null;
	private FileSystem fs =null;
	private SimpleDateFormat sdfOut = new SimpleDateFormat(Constants.OUTPUT_FORMAT);
	protected HBaseAccessor(String quorum, String clientPort, String defaultFSName){
		Configuration hadoopConfig = new Configuration();
		hadoopConfig.set("hbase.zookeeper.quorum", quorum);
		hadoopConfig.set("hbase.zookeeper.property.clientPort", clientPort);
		hadoopConfig.set("hbase.client.keyvalue.maxsize", Constants.HBASE_CLIENT_KEYVALUE_MAXSIZE);
		hadoopConfig.set("fs.defaultFS", defaultFSName);//Hadoop2考虑换成fs.defaultFS
//		hadoopConfig.set("fs.default.name", defaultFSName);//Hadoop2考虑换成fs.defaultFS
		Configuration config = HBaseConfiguration.create(hadoopConfig);
		htPool = new HTablePool(config, Constants.HTABLE_POOL_SIZE);
		try {
			hbAdmin = new HBaseAdmin(config);
			if(!"".equals(defaultFSName)){
			fs = FileSystem.get(hadoopConfig);
			}
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(htPool!=null){
			htPool.close();
		}
		if(hbAdmin!=null){
			hbAdmin.close();
		}
	};
	
	public boolean checkTableExist(String tableName) throws IOException{
//		System.out.println("check tableName exist==================="+tableName);
		return hbAdmin.tableExists(tableName);
		
	}
	
	public boolean createTableWithOutColumnFamily(String tableName){
		List<String> ls =new ArrayList<String>();
		ls.add("DEFAULT");
		return createTableWithColumnFamily(tableName, ls);
	}
	public Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> 
	               getRecords(String startRow, String stopRow,
			long startTime, long endTime,long rows, int versions, List<String> families,
			   FilterList fts,String tableName)
			   throws IOException {
		HTableInterface table = htPool.getTable(tableName);
		Scan scanner = new Scan(startRow.getBytes(Constants.ENCODE),
				stopRow.getBytes(Constants.ENCODE));
		if (families != null && !(families.isEmpty())) {
			for (String s : families) {
				scanner.addFamily(s.getBytes(Constants.ENCODE));
			}
		}
		if (fts != null) {
			scanner.setFilter(fts);
		}
		scanner.setCaching(Constants.SCANNER_CACHING);
		scanner.setTimeRange(startTime, endTime);
		if(versions==0){
			scanner.setMaxVersions();
		}
		else{
			scanner.setMaxVersions(versions);
		}
		ResultScanner rs = table.getScanner(scanner);
		
		Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> rtMap 
			= new LinkedHashMap<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>>();
		if(rows==0){
		for(Result r:rs){
			rtMap.put(new String(r.getRow(), Constants.ENCODE), r.getMap());
		}
		}
		else{
			long count=0;
			for(Result r:rs){
				rtMap.put(new String(r.getRow(), Constants.ENCODE), r.getMap());
				count++;
				if(count>=rows){
					break;
				}
			}
		}
		rs.close();
		table.close();
		return rtMap;
	}
	public boolean createTableWithColumnFamily(String tableName, List<String> columnNames){
		HTableDescriptor htDescriptor = new HTableDescriptor(tableName);
		for (String hcolumnDescriptor : columnNames) {
			HColumnDescriptor hcDescriptor = new HColumnDescriptor(
					hcolumnDescriptor);
			hcDescriptor.setMaxVersions(Integer.MAX_VALUE);// 实际应用中，此处应当将DEVICE族设为1
			hcDescriptor.setBloomFilterType(BloomType.ROW);
			hcDescriptor.setInMemory(true);
			htDescriptor.addFamily(hcDescriptor);

			hcDescriptor.setCompressionType(Compression.Algorithm.GZ);
		}
		try {
			hbAdmin.createTable(htDescriptor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean deleteTable(String tableName){
		try {
			if(hbAdmin.tableExists(tableName)){
			hbAdmin.disableTable(tableName);
			hbAdmin.deleteTable(tableName);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public JSONObject listAllTables(){
		try {
			HTableDescriptor[] htds = hbAdmin.listTables();
			
		 	return  HTableDescriptors2JSONObject(htds);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject listTables(String regex){
		try {
			HTableDescriptor[] htds = hbAdmin.listTables(regex);
			
		 	return  HTableDescriptors2JSONObject(htds);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject HTableDescriptors2JSONObject(HTableDescriptor[] htds) throws IOException{
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		Path rootP=new Path(Constants.HBASE_ROOTDIR_IN_HDFS);
		for(HTableDescriptor htd:htds){
			JSONObject joTemp = new JSONObject();
			joTemp.put("name", htd.getNameAsString());
			Path curPath = HTableDescriptor.getTableDir(
					rootP, htd.getName());
			if(Constants.IS_HBASE_DEPLOYED_ON_HDFS){
			FileStatus fileStatus = fs.getFileStatus(curPath);
			joTemp.put("size", Long.toString(countDirSize(curPath)));
//			joTemp.put("lastAccess",sdfOut.format(
//					new Date(fileStatus.getAccessTime())));
			joTemp.put("lastModification",sdfOut.format(
					new Date(fileStatus.getModificationTime())));
			joTemp.put("owner", fileStatus.getOwner());
			}
			ja.add(joTemp);
		
		}
		jo.put("result", ja);
		return jo;
	}
	
	private long countDirSize(Path path) throws IOException{
		long totalSize=0;
        FileStatus[] fileStatus = fs.listStatus(path);  
        for(int i=0;i<fileStatus.length;i++){  
            if(fileStatus[i].isDir()){  
                Path p = new Path(fileStatus[i].getPath().toString());  
                totalSize+=countDirSize(p);  
            }else{  
//                System.out.println(fileStatus[i].getPath().toString());  
            	totalSize+=fileStatus[i].getLen();
            }  
        }
		
		return totalSize;
		
	}
	
	/*
	 * coder:maheqi
	 * coderTime:2014.8.4
	 * 删除row根据起始rowkey 删除行
	 */
	
	public void deleteByRow(String tableName,String startRow,String stopRow, FilterList fl,long beginTime, long endTime)
			throws IOException {
		HTableInterface table = htPool.getTable(tableName);
		Scan scanner = new Scan(startRow.getBytes(Constants.ENCODE),stopRow.getBytes(Constants.ENCODE));
		scanner.setFilter(fl);
		scanner.setCaching(Constants.SCANNER_CACHING);
		scanner.setMaxVersions();
		scanner.setTimeRange(beginTime, endTime);
		ResultScanner rs = table.getScanner(scanner);
		Delete d=null;
		//System.out.println("删除数据Rowkey为");
		List<Delete> listdd=new ArrayList<Delete>();
		int count=0;
//		long longint=System.currentTimeMillis();
		for(Result r:rs){
			//删除查询到的key
			count++;
			d=new Delete(r.getRow());
//			d.setTimestamp(Long.MAX_VALUE);
			listdd.add(d);
			if(count%10==0)
			{
				table.delete(listdd);
				listdd.clear();
				//System.out.println("总删除数据"+count+"条");
			}
		}
		if(listdd.size()>0)
		{
			table.delete(listdd);
//			listdd=null;
		}
		System.out.println("总删除数据"+count+"条");
		rs.close();
		//System.out.println("总用时："+(System.currentTimeMillis()-longint));
	}

	/*
	 * coder:maheqi
	 * coderTime:2014.08.04
	 * 模糊匹配删除
	 * */
	public void deleteByFuzzy(String tableName,String rowkey, FilterList fl,long beginTime, long endTime)
			throws IOException {
		//首先查找符合条件的rowkey

		HTableInterface table = htPool.getTable(tableName);
		List<Pair<byte[],byte[]>> listp=new ArrayList<Pair<byte[],byte[]>>();
		Pair<byte[],byte[]> pair=new Pair<byte[],byte[]>();
		//添加pair 和pair的两个参数
		pair.setFirst(fuzzyMaskChar2TopByte(rowkey).getBytes(Constants.ENCODE));
		pair.setSecond(string2ByteMask(rowkey));
		listp.add(pair);
		Filter  frf=new FuzzyRowFilter(listp);
		//	FuzzyRowFilter frfs=FuzzyRowFilter.parseFrom(condition.getBytes());
		//FuzzyRowFilter(List<Pair<byte[],byte[]>> fuzzyKeysData)
		fl.addFilter(frf);
		Scan scanner = new Scan();
		scanner.setFilter(fl);
		scanner.setCaching(Constants.SCANNER_CACHING);
		scanner.setMaxVersions();
		scanner.setTimeRange(beginTime, endTime);
		ResultScanner rs = table.getScanner(scanner);
		//=null;
		//System.out.println("删除数据Rowkey为");
        List<Delete> listd=new ArrayList<Delete>();
        int count=0;
        //long longint=System.currentTimeMillis();
		for(Result r:rs){
			count++;
			//删除查询到的key
			System.out.println(new String(r.getRow()));
			Delete d=new Delete(r.getRow());
			listd.add(d);
			//System.out.println("删除数据第"+count+"条");
			if(count%10000==0)
			{
			//System.out.println("删除数据第"+count+"条");
			table.delete(listd);
			 
			listd.clear();
			//批量删除
			}
		}
		if(listd.size()>0)
		{
			table.delete(listd);
//			listd=null;
		}
		System.out.println("总删除数据"+count+"条");
		rs.close();
		//System.out.println("总用时："+(System.currentTimeMillis()-longint));
		
	}

	public void insertData(String tableName, String rowkey, String family,
			String qualifier, String value) {
		long ts = new Date().getTime();
		insertData(tableName, rowkey, family, qualifier, ts, value);
	}

	public void insertData(String tableName, String rowkey, String family,
			String qualifier, long timeStamp, String value) {
		HTableInterface table = htPool.getTable(tableName);
		Put p;
		try {
			p = new Put(rowkey.getBytes(Constants.ENCODE));
			p.add(family.getBytes(Constants.ENCODE),
					qualifier.getBytes(Constants.ENCODE), timeStamp,
					value.getBytes(Constants.ENCODE));
			table.put(p);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取单个Value数据，可能表不存在或rowkey不存在
	 */
	public Map<String,String> getSingleRowCFWithoutTimestamp(String tableName, String rowkey, String family
			){
		HTableInterface table = htPool.getTable(tableName);
		Get get;
		Result r;
		NavigableMap<byte[],byte[]> nbb = null;
		try {
			get = new Get(rowkey.getBytes(Constants.ENCODE));
			get.addFamily(Constants.MAPPING_META_DATA_FAMILY.getBytes(Constants.ENCODE));
			get.setMaxVersions(1);
			r = table.get(get);
			
			NavigableMap<byte[],NavigableMap<byte[],byte[]>> nbnbb =r.getNoVersionMap();
			if(nbnbb!=null){
			nbb =nbnbb.get(family.getBytes(Constants.ENCODE));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byteStreamMap2StringMap(nbb);
		
	}
	
	private Map<String,String> byteStreamMap2StringMap(Map<byte[],byte[]> mbb){
		Map<String,String> mss =new HashMap<String, String>();
		if(mbb!=null){
		for(Entry<byte[],byte[]>ebb: mbb.entrySet()){
			try {
				mss.put(new String(ebb.getKey(),Constants.ENCODE),
						new String(ebb.getValue(),Constants.ENCODE));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		return mss;
	}
	/*
	 * String2ByteMask 主要处理string为Pair fuzzyinfo参数使用的01 byte
	 */
	private byte[] string2ByteMask(String rowkey) throws UnsupportedEncodingException{//仅用于UTF-8,汉字转成bytes都是负数，所以不存在汉字的后两位byte与?重复的问题
		byte fmc = Constants.FUZZY_MASK_CHAR;
		byte[] ss = rowkey.getBytes(Constants.ENCODE);
		byte[] bs= new byte[ss.length];
		for(int i=0;i<ss.length;i++){
			if(fmc==ss[i]){
				bs[i]=1;
			}
			else{
				bs[i]=0;
			}
		}
		return bs;
	}
	
	private String fuzzyMaskChar2TopByte(String rowkey) {
		return rowkey.replace((char)(Constants.FUZZY_MASK_CHAR),
				(char)(Constants.TOP_MASK_CHAR));
	}
	public static void main(String[] args) throws IOException{
//		HBaseAccessor hba = new HBaseAccessor("172.18.68.99,172.18.68.101,172.18.68.102", "2222","hdfs://t1:9000");
////		hba.createTableWithOutColumnFamily("bcccc1");
//		System.out.print(hba.checkTableExist("gps"));
		
//		char c= (char)Constants.FUZZY_MASK_CHAR;
//		System.out.println(c);
		
		HBaseAccessor hba = new HBaseAccessor("localhost", "2222","");
		Map<String, String> nbb =hba.getSingleRowCFWithoutTimestamp(Constants.MAPPING_META_DATA_TABLE,
				"gps0405random#StreamKv01", Constants.MAPPING_META_DATA_FAMILY);
		System.out.println("========"+nbb.get("scriptPath"));
		
	}

	public void deleteByFuzzy(String tableName, String fuzzyRow) throws IOException {
		// TODO Auto-generated method stub
		deleteByFuzzy(tableName, fuzzyRow, new FilterList(), 0, Long.MAX_VALUE);
	}
}
