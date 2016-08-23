package com.easymap.datacenter.model;

import java.io.Serializable;

public class SystemTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String xtId;
	
	private String bmc;
	
	private String ppl;
	
	private String yj;
	
	private String owner;

	public SystemTable() {
		super();
	}

	public SystemTable(String xtId, String bmc, String ppl, String yj, String owner) {
		super();
		this.xtId = xtId;
		this.bmc = bmc;
		this.ppl = ppl;
		this.yj = yj;
		this.owner = owner;
	}

	public String getXtId() {
		return xtId;
	}

	public void setXtId(String xtId) {
		this.xtId = xtId;
	}

	public String getBmc() {
		return bmc;
	}

	public void setBmc(String bmc) {
		this.bmc = bmc;
	}

	public String getPpl() {
		return ppl;
	}

	public void setPpl(String ppl) {
		this.ppl = ppl;
	}

	public String getYj() {
		return yj;
	}

	public void setYj(String yj) {
		this.yj = yj;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return owner;
	}
	
	

}
