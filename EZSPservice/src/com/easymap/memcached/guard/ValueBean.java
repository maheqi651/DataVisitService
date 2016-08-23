package com.easymap.memcached.guard;

import java.io.Serializable;

public class ValueBean implements Serializable{
    private long times;
    private String[] str;
    private String key;
    private String mhtd;
    private String senderId;
    
    
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getMhtd() {
		return mhtd;
	}
	public void setMhtd(String mhtd) {
		this.mhtd = mhtd;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public String[] getStr() {
		return str;
	}
	public void setStr(String[] str) {
		this.str = str;
	}
}
