package com.easymap.base.queue;

import java.util.Date;

public class PreCheckSub {
		private int chkBlockTime;
		private int timespan;
		private Date blockTime;
		private String senderID;
		private String serviceID;
		private String ID;
		private String themeCode;
		private String tableCode;
		private int chkRecordCount;
		private int curCount;
		
		public int getTimespan() {
			return timespan;
		}
		public void setTimespan(int timespan) {
			this.timespan = timespan;
		}
		public int getChkBlockTime() {
			return chkBlockTime;
		}
		public void setChkBlockTime(int chkBlockTime) {
			this.chkBlockTime = chkBlockTime;
		}
		public Date getBlockTime() {
			return blockTime;
		}
		public void setBlockTime(Date blockTime) {
			this.blockTime = blockTime;
		}
		public String getSenderID() {
			return senderID;
		}
		public void setSenderID(String senderID) {
			this.senderID = senderID;
		}
		public String getServiceID() {
			return serviceID;
		}
		public void setServiceID(String serviceID) {
			this.serviceID = serviceID;
		}
		public String getID() {
			return ID;
		}
		public void setID(String iD) {
			ID = iD;
		}
		public String getThemeCode() {
			return themeCode;
		}
		public void setThemeCode(String themeCode) {
			this.themeCode = themeCode;
		}
		public String getTableCode() {
			return tableCode;
		}
		public void setTableCode(String tableCode) {
			this.tableCode = tableCode;
		}
		public int getChkRecordCount() {
			return chkRecordCount;
		}
		public void setChkRecordCount(int chkRecordCount) {
			this.chkRecordCount = chkRecordCount;
		}
		public int getCurCount() {
			return curCount;
		}
		public void setCurCount(int curCount) {
			this.curCount = curCount;
		}
	
 
}
