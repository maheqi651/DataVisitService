package com.easymap.hbase.hbasemodel;

public class KvParams {
	private String hbaseTable;
	private String hbaseInstance;
	private String rowkey;
	private String isFuzzy;
	private String fuzzyRow;
	private String returnType;
	private String isBatch;
	private String pageSize;
	private String qualifiers;
	private String maxVersions;
	private String beginTime;
	private String endTime;
	public String getHbaseTable() {
		return hbaseTable;
	}
	public void setHbaseTable(String hbaseTable) {
		this.hbaseTable = hbaseTable;
	}
	public String getHbaseInstance() {
		return hbaseInstance;
	}
	public void setHbaseInstance(String hbaseInstance) {
		this.hbaseInstance = hbaseInstance;
	}
	public String getRowkey() {
		return rowkey;
	}
	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}
	public String getIsFuzzy() {
		return isFuzzy;
	}
	public void setIsFuzzy(String isFuzzy) {
		this.isFuzzy = isFuzzy;
	}
	public String getFuzzyRow() {
		return fuzzyRow;
	}
	public void setFuzzyRow(String fuzzyRow) {
		this.fuzzyRow = fuzzyRow;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getIsBatch() {
		return isBatch;
	}
	public void setIsBatch(String isBatch) {
		this.isBatch = isBatch;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getQualifiers() {
		return qualifiers;
	}
	public void setQualifiers(String qualifiers) {
		this.qualifiers = qualifiers;
	}
	public String getMaxVersions() {
		return maxVersions;
	}
	public void setMaxVersions(String maxVersions) {
		this.maxVersions = maxVersions;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
}
