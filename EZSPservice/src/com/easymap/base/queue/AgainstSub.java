package com.easymap.base.queue;

import java.util.Date;

public class AgainstSub {
		private int chkTimeSpan;
		private int chkRecordCount;
		private int curCount;
		private int chkBlockTimeSpan;
		private String senderID;
		private String serviceID;
		private Date updateTime;
		private String ID;
		private String themeCode;
		private String tableCode;
		
		public int getCurCount() {
			return curCount;
		}
		public void setCurCount(int curCount) {
			this.curCount = curCount;
		}
		public int getChkTimeSpan() {
			return chkTimeSpan;
		}
		public void setChkTimeSpan(int chkTimeSpan) {
			this.chkTimeSpan = chkTimeSpan;
		}
		public int getChkRecordCount() {
			return chkRecordCount;
		}
		public void setChkRecordCount(int chkRecordCount) {
			this.chkRecordCount = chkRecordCount;
		}
		public int getChkBlockTimeSpan() {
			return chkBlockTimeSpan;
		}
		public void setChkBlockTimeSpan(int chkBlockTimeSpan) {
			this.chkBlockTimeSpan = chkBlockTimeSpan;
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
		public Date getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
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

}
