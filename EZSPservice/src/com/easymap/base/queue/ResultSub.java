package com.easymap.base.queue;

import java.io.Serializable;
import java.util.Date;

public class ResultSub implements Serializable{
		private boolean flag;
		private Date blockTime;
		private int chkBlockTimeSpan;
		public boolean isFlag() {
			return flag;
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
		public Date getBlockTime() {
			return blockTime;
		}
		public void setBlockTime(Date blockTime) {
			this.blockTime = blockTime;
		}
		public int getChkBlockTimeSpan() {
			return chkBlockTimeSpan;
		}
		public void setChkBlockTimeSpan(int chkBlockTimeSpan) {
			this.chkBlockTimeSpan = chkBlockTimeSpan;
		}
}
