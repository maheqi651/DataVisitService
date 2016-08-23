package com.easymap.datacenter.model;

import net.sf.json.JSONObject;

public class Result {
	
	private boolean flag;
	
	private String resultJson;

	public Result() {
		super();
		this.flag = true;
		this.resultJson = "";
	}

	public Result(boolean flag, String resultJson) {
		super();
		this.flag = flag;
		this.resultJson = resultJson;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getResultJson() {
		return resultJson;
	}

	public void setResultJson(String resultJson) {
		this.resultJson = resultJson;
	}

	@Override
	public String toString() {
		JSONObject json=new JSONObject();
		json.put("flag", this.flag);
		json.put("resultJson", resultJson);
		return json.toString();
	}
	
	
}
