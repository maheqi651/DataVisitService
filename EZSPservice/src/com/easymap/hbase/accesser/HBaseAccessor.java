package com.easymap.hbase.accesser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;




import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.FuzzyRowFilter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;

import com.easymap.ezMDAS.kvQuery.constants.Constants;
import com.easymap.ezMDAS.kvQuery.mappings.KvMapping;
import com.easymap.ezMDAS.kvQuery.pojos.KvInfo;
import com.easymap.ezMDAS.kvQuery.pojos.KvPage;

public class HBaseAccessor {
	private String quorum;
	private String clientPort;
	private String tableName;
	// public String hbaseTableName;
	private HTablePool htPool;
	private HBaseAdmin hbAdmin = null;

	protected HBaseAccessor(String quorum, String clientPort, String tableName) {
		Configuration hadoopConfig = new Configuration();
		hadoopConfig.set("hbase.zookeeper.quorum", quorum);
		hadoopConfig.set("hbase.zookeeper.property.clientPort", clientPort);
		Configuration config = HBaseConfiguration.create(hadoopConfig);
		htPool = new HTablePool(config, Constants.HTABLE_POOL_SIZE);
		this.tableName = tableName;
		try {
			hbAdmin = new HBaseAdmin(config);
		} catch (MasterNotRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 按照指定条件（参数fts）获取一个指定Row的数据，返回Result,尚存问题，暂时不用
	 * 
	 * @param tableName
	 * @param rowKey
	 * @return
	 * @throws IOException
	 */
	public Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> getOneRecord(
			String rowKey, long startTime, long endTime, int maxVersions, List<String> families,
			FilterList fts) throws IOException {
		HTableInterface table = htPool.getTable(tableName);
		Get get = new Get(rowKey.getBytes(Constants.ENCODE));
		if (families != null && !(families.isEmpty())) {
			for (String s : families) {
				get.addFamily(s.getBytes(Constants.ENCODE));
			}
		}
		get.setTimeRange(startTime, endTime);
		if (fts != null) {
			get.setFilter(fts);
		}
		if(maxVersions==0){
		get.setMaxVersions();
		}
		else{
			get.setMaxVersions(maxVersions);
		}
		Result r = table.get(get);
		Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> rtMap 
			= new LinkedHashMap<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>>();
		if(r.getRow()!=null){
		rtMap.put(new String(r.getRow(), Constants.ENCODE), r.getMap());
		}
		table.close();

		return rtMap;
	}

	public Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> getOneRecord(
			String rowKey, int maxVersions,FilterList fts) throws IOException {
		return getOneRecord(rowKey, 0, Long.MAX_VALUE,maxVersions, null, fts);
	}

	/**
	 * 使用Get的setTimeStamp方法
	 * @throws IOException 
	 */
	public NavigableMap<byte[],NavigableMap<byte[],byte[]>> getOneRecordBySecondaryIndex(
			String rowKey, long timestamp) throws IOException{
		
		HTableInterface table = htPool.getTable(tableName);
		Get get = new Get(rowKey.getBytes(Constants.ENCODE));
		get.setTimeStamp(timestamp);
		Result r = table.get(get);
		NavigableMap<byte[],NavigableMap<byte[],byte[]>> nn = r.getNoVersionMap();
		return nn;
	}
	/**
	 * 按照指定条件不使用Filter获取ResultScanner,有效！ 改用scan构造方法中的startRow，stopRow
	 * 
	 * @param tableName
	 * @param rowKey
	 * @return
	 * @throws IOException
	 */

	public Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>>
	     getRecords(String startRow, String stopRow,
			long startTime, long endTime,long rows, int versions, List<String> families, FilterList fts)
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
	
	public long getRecordsCount(String startRow, String stopRow,FilterList fts) throws IOException{
		HTableInterface table = htPool.getTable(tableName);
		long count=0;
		Scan scanner = new Scan(startRow.getBytes(Constants.ENCODE),
				stopRow.getBytes(Constants.ENCODE));
		scanner.setCaching(Constants.SCANNER_CACHING);
		scanner.setMaxVersions(1);
		if(fts==null){
			fts=new FilterList();
		}
		fts.addFilter(new FirstKeyOnlyFilter());//只返回每个row的第一个keyvalue，统计row数量时，比KeyOnlyFilter效率更高
		scanner.setFilter(fts);
		ResultScanner rs = table.getScanner(scanner);
		for(Result r:rs){
			count++;
		}
		return count;
	}
	
	public long getRecordsVersionsCount(String startRow, String stopRow,FilterList fts) throws IOException{
		HTableInterface table = htPool.getTable(tableName);
		long count=0;
		Scan scanner = new Scan(startRow.getBytes(Constants.ENCODE),
				stopRow.getBytes(Constants.ENCODE));
		scanner.setCaching(Constants.SCANNER_CACHING);
		scanner.setMaxVersions();
		if(fts==null){
			fts=new FilterList();
		}
		fts.addFilter(new KeyOnlyFilter());
		scanner.setFilter(fts);
		ResultScanner rs = table.getScanner(scanner);
		Set<Long> tsSet = new HashSet<Long>();
		for(Result r:rs){
			
			NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>
				nMap = r.getMap();
			for(byte[] btsColumn: nMap.keySet()){
				for(byte[] btsQualifier: nMap.get(btsColumn).keySet()){
					for(long curTs: nMap.get(btsColumn).get(btsQualifier).keySet()){
						tsSet.add(curTs);
					}
				}
			}
			count+=tsSet.size();
			tsSet.clear();
		}
		return count;
	}
/*	public Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>>
		getRecords(String startRow, String stopRow,FilterList fts) throws IOException {
		return getRecords(startRow, stopRow, 0, Long.MAX_VALUE,0, null, fts);
	}
	*/
	public Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> getRecords(
			String startRow, String stopRow, long rows, int maxVersions,
			FilterList fts) throws IOException {
		return getRecords(startRow, stopRow, 0, Long.MAX_VALUE, rows,
				maxVersions, null, fts);
	}
	public Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> getRecords(
			String startRow, String stopRow) throws IOException{
		return getRecords(startRow, stopRow, 0, Long.MAX_VALUE, 0,
				0, null, null);
	}
/*	public  Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> getRecords(
			String startRow, String stopRow, long startTime, long endTime, List<String> families,FilterList fts) throws IOException {
		// TODO Auto-generated method stub
		return getRecords(startRow, stopRow, startTime, endTime,0, families, fts);
	}*/

	public Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> getAllRecords() throws IOException {
		HTableInterface table = htPool.getTable(tableName);
		Scan scanner = new Scan();
		scanner.setCaching(Constants.SCANNER_CACHING);
		scanner.setMaxVersions();
		Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> rtMap = new HashMap<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>>();
		ResultScanner rs = table.getScanner(scanner);
		for (Result r : rs) {
			rtMap.put(new String(r.getRow(), Constants.ENCODE), r.getMap());
		}
		rs.close();
		table.close();
		return rtMap;
	}
		
	public boolean checkTableExist(){
		try {
			return hbAdmin.tableExists(tableName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static void main(String[] args) throws IOException{
//		HBaseAccessor hba = new HBaseAccessor("172.18.68.99,172.18.68.101,172.18.68.102", "2222","REG1219");
//		HBaseAccessor hba = new HBaseAccessor("172.18.68.99,172.18.68.101,172.18.68.102", "2222","reg1219-registerObject");
//		FilterList fl = new FilterList();
//		List<Pair<byte[], byte[]>> lpbb = new ArrayList<Pair<byte[],byte[]>>();
//		lpbb.add(new Pair<byte[], byte[]>(("100 ").getBytes(Constants.ENCODE), new byte[] {0,0,0,1}));
////		lpbb.add(new Pair<byte[], byte[]>(("苏E00000").getBytes(Constants.ENCODE), new byte[] {1,1,1,0,0,0,0,1,1}));
//		Filter fz= new FuzzyRowFilter(lpbb);
////		Filter fz= new FuzzyRowFilter(Arrays.asList(new Pair<byte[], byte[]>(("苏E00000").getBytes(Constants.ENCODE), new byte[] {0,0,0,1,0,0,0,1,1})));
//		fl.addFilter(fz);
////		byte b=-128;
//		
//		Map <String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> m 
//			=hba.getRecords("","苏Z",fl);
		
//		Map <String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> m 
//		=hba.getRecords("","云A00283",0,Long.MAX_VALUE,0,null,null);
//		KvMapping km = new KvMapping();
//		KvPage kp = km.NaviMap2PojoIDFirst(m, "aaa");
//		
//		for(KvInfo ki: kp.getKvInfoList()){
//			System.out.println(ki.getTimestampTable());
//		}
//		System.out.println(kp.getKvInfoList().size());
//		System.out.println(kp.getKvInfoList().get(0).getTimestampTable());
		
//		List<Pair<byte[], byte[]>> lpbb = new ArrayList<Pair<byte[],byte[]>>();
//		lpbb.add(new Pair<byte[], byte[]>(("云A00068").getBytes(Constants.ENCODE), new byte[] {0,0,0,0,0,0,0,1,1}));
//		FuzzyRowFilter frf = new FuzzyRowFilter(lpbb);
//		FilterList fl = new FilterList();
//		fl.addFilter(frf);
//		System.out.println(hba.getRecordsCount("", "云B", fl));
		HBaseAccessor hba = new HBaseAccessor("localhost", "2222","gps0401");
		Date d0 = new Date();
//		hba.getRecords("10001#20120401", "10001#20120401~", 0,3, null);
		hba.getOneRecord("10001#20120401", 3000, null);
		Date d1 = new Date();
		System.out.println(d1.getTime()-d0.getTime());
		
		
	}
}
