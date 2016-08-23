package com.easymap.hbase.hbasemodel;

import java.io.Console;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FuzzyRowFilter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Pair;


import com.easymap.ezBigData.comparator.StringHBaseComparable;
import com.easymap.ezBigData.exceptions.EzBigDataConfigException;
import com.easymap.ezMDAS.kvQuery.mappings.KvMapping;
import com.easymap.ezMDAS.kvQuery.pojos.KvInfo;
import com.easymap.ezMDAS.kvQuery.pojos.KvPage;
import com.easymap.hbase.accesser.HBaseAccessor;
import com.easymap.hbase.accesser.HBaseAccessorFactory;
import com.easymap.hbase.util.Constants;
import com.google.common.collect.Iterables;

public class EzBigDataKvQuery {
 
 String hbaseInstance;
 String hbaseTableName;
 HBaseAccessor hba;
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
public EzBigDataKvQuery(String hbaseInstance, String hbaseTableName) throws EzBigDataConfigException {
	super();
	this.hbaseInstance = hbaseInstance;
	this.hbaseTableName = hbaseTableName;
	hba = HBaseAccessorFactory.getHBaseAccessor(hbaseInstance,hbaseTableName);
	
}
 

 public KvPage executeQuery(String rowKey,String stopRow,long beginTimestamp, long endTimestamp, String fuzzyRow, boolean isFuzzy,boolean isBatch, long pageSize, int maxVersions,String[] qualifiers) throws EzBigDataConfigException{
		Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> resultMap;
		KvMapping km = new KvMapping();
		FilterList fl = new FilterList();
		if(qualifiers!=null && qualifiers.length>0){
		FilterList qFilters = new FilterList(FilterList.Operator.MUST_PASS_ONE);
		for(String s:qualifiers){
				try {
//					if(!s.contains(":")){
//						s="DEFAULT:"+s;
//					}
					QualifierFilter qf = new QualifierFilter(CompareOp.EQUAL, new BinaryComparator(s.getBytes(Constants.ENCODE)));

//					QualifierFilter qf = new QualifierFilter(CompareOp.EQUAL, new SubstringComparator(s));
					qFilters.addFilter(qf);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		fl.addFilter(qFilters);
		}
		if(stopRow==null || "".equals(stopRow)){
			stopRow=ROW_END;
		}
		stopRow=stopRow.replace("?", ROW_END);
		//TO DO把FilterList提到此处
		if(pageSize==0){//没有做分页之前的代码部分
		try {	
			if(isBatch){
				throw new EzBigDataConfigException("isBatch必须和pageSize配合使用，请更正");
			}
			if (isFuzzy) {
				FuzzyRowFilter frf = new FuzzyRowFilter(
						Arrays.asList(new Pair<byte[], byte[]>(fuzzyMaskChar2TopByte(fuzzyRow)
								.getBytes(Constants.ENCODE),
								String2ByteMask(fuzzyRow))));
				fl.addFilter(frf);
				int firstFuzzyCharIndex = fuzzyRow
						.indexOf(Constants.FUZZY_MASK_CHAR);
				if (firstFuzzyCharIndex >= 0) {
					String prefixNoFuzzy = fuzzyRow.substring(0,
							fuzzyRow.indexOf(Constants.FUZZY_MASK_CHAR));
					resultMap = hba.getRecords(StringHBaseComparable.
							getBiggerBytesString(rowKey,prefixNoFuzzy), 
							StringHBaseComparable.getSmallerBytesString(stopRow, prefixNoFuzzy+ ROW_END)
							,beginTimestamp,endTimestamp,pageSize,maxVersions, null, fl);// 如果不行的话，此处用getRecords
				} else {// 如果没有通配符
					throw new EzBigDataConfigException("isFuzzy为true时，fuzzyRow必须包含通配符"+Constants.FUZZY_MASK_CHAR+"，请更正");
				}
			
			} else {// isFuzzy、isBatch值都为false
				resultMap = hba.getOneRecord(rowKey,maxVersions, fl);
			}
			
			KvPage kp=km.NaviMap2PojoIDFirst(resultMap,"");
			return kp;
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		}
		else{//如果有分页，pageSize>0
			String nextRowkey="";
			try {	
			if(isFuzzy && isBatch){
			throw new EzBigDataConfigException("isFuzzy与isBatch中只能有一个为true，请更正");
			}
			else if(!isFuzzy && !isBatch){
			throw new EzBigDataConfigException("isFuzzy与isBatch都为false时，无法做分页，请更正");
			}
			else if(isFuzzy){
				FuzzyRowFilter frf = new FuzzyRowFilter(
						Arrays.asList(new Pair<byte[], byte[]>(fuzzyMaskChar2TopByte(fuzzyRow)
//						Arrays.asList(new Pair<byte[], byte[]>(fuzzyMaskChar2TopByte(rowKey)
								.getBytes(Constants.ENCODE),
								String2ByteMask(fuzzyRow))));
				PageFilter pf = new PageFilter(pageSize+1);//多取一个，作为下一页页首
				fl.addFilter(pf);
				fl.addFilter(frf);
//				fl.addFilter(frf);
				int firstFuzzyCharIndex = fuzzyRow
						.indexOf(Constants.FUZZY_MASK_CHAR);
				if (firstFuzzyCharIndex >= 0) {
					String prefixNoFuzzy = fuzzyRow.substring(0,
							fuzzyRow.indexOf(Constants.FUZZY_MASK_CHAR));
					resultMap = hba.getRecords(StringHBaseComparable.
							getBiggerBytesString(rowKey,prefixNoFuzzy),  StringHBaseComparable.getSmallerBytesString(stopRow, prefixNoFuzzy+ ROW_END)
							,beginTimestamp,endTimestamp,pageSize+1, maxVersions,null, fl);// 如果不行的话，此处用getRecords
					nextRowkey=getNextRowkeyAndRemoveLast(resultMap, pageSize);
				} else {// TODO 如果没有通配符
					resultMap = hba.getOneRecord(rowKey,maxVersions, null);
				}
			} else{// if isBatch TODO
				PageFilter pf = new PageFilter(pageSize+1);//多取一个，作为下一页页首
				fl.addFilter(pf);
				 
				resultMap = hba.getRecords(rowKey, stopRow+ROW_END,beginTimestamp,endTimestamp,pageSize+1,maxVersions, null, fl);
				// stopRow resultMap = hba.getRecords(rowKey, ROW_END,beginTimestamp,endTimestamp,pageSize+1,maxVersions, null, fl);
				//"~~"处有误！汉字不兼容
				nextRowkey=getNextRowkeyAndRemoveLast(resultMap, pageSize);
			}
			KvPage kp=km.NaviMap2PojoIDFirst(resultMap,nextRowkey);
			return kp;
			}catch(UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	return null;
	 
 }
 
// private String getNextRowkey (Map<String, Object> mso, long pageSize){
//
// }
 
 public boolean checkTableExist(){
	return hba.checkTableExist();
 }
  
 
	private String getNextRowkeyAndRemoveLast(
			Map<String, NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>>> resultMap,
			long pageSize) {
		String nextRowkey = "";
		if (resultMap.size() < pageSize + 1) {
			// do nothing,已到最后一页，不用删除最后一个。
		} else {
			nextRowkey = Iterables.getLast(resultMap.keySet());
			resultMap.remove(nextRowkey);
		}
		return nextRowkey;
	}

 private byte[] String2ByteMask(String s) throws UnsupportedEncodingException{//仅用于UTF-8,汉字转成bytes都是负数，所以不存在汉字的后两位byte与?重复的问题
//	 List<Byte> lb = new ArrayList<Byte>();
	 byte fmc = Constants.FUZZY_MASK_CHAR;
	 byte[] ss = s.getBytes(Constants.ENCODE);
	 byte[] bs= new byte[ss.length];
	 for(int i=0;i<ss.length;i++){
		 if(fmc==ss[i]){
		 bs[i]=1;
	 }
	 else{
		 bs[i]=0;
	 }
	 }
//	 for(byte b:s.getBytes()){
//		 if(fmc==b){
//			 lb.add((byte) 1);
//		 }
//		 else{
//			 lb.add((byte)0);
//		 }
//	 }
//	 Byte[] bsWrapper = lb.toArray(new Byte[0]);
	 
	return bs;

 }
 
 private String fuzzyMaskChar2TopByte(String rowkey) {
		return rowkey.replace((char)(Constants.FUZZY_MASK_CHAR),
				(char)(Constants.TOP_MASK_CHAR));
	} 
 
 
 public static void main(String args[])
 {
	 byte[] bs = {-12,-113,-65,-65};//UTF-8中排位最后的字符。
		 
			try {
				ROW_END=new String(bs,Constants.ENCODE);
				System.out.println(ROW_END);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
 }

}
