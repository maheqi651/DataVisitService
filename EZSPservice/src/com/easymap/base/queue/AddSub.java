package com.easymap.base.queue;

import java.io.Serializable;
import java.util.Date;

public class AddSub implements Serializable{
		private int addCount;
		private String senderID;
		private String serviceID;
		private String ID;
		private String themeCode;
		private String tableCode;
		public int getAddCount() {
			return addCount;
		}
		public void setAddCount(int addCount) {
			this.addCount = addCount;
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

}
