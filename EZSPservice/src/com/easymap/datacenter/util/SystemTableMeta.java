package com.easymap.datacenter.util;

import java.io.Serializable;


public class SystemTableMeta implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8801373389044389445L;

	private String zdmc; //字段名称
	
	private String gsbm; //归属表名
	
	private String xtid;  //系统id
	
	private String bmc;  //表名称
	
	private String  zdbs;  //字段名称
	
	private String zdcd;   //字段长度
	
	private String zdlx;  //字段类型
	
	private String zdsjylx;//表字段类型到数据元类型映射结果
	
	private String ppjg;  //匹配结果
	
	private String shyj;  //审核意见
	
	private String ppmb;  //匹配目标
	
	private String tjsjy;// 推荐数据元
	
	private String owner; //数据元所属用户

	public SystemTableMeta() {
		super();
	}

	public SystemTableMeta(String zdmc, String gsbm, String xtid, String bmc,
			String zdbs, String zdcd, String zdlx, String zdsjylx, String ppjg,
			String shyj, String ppmb, String tjsjy, String owner) {
		super();
		this.zdmc = zdmc;
		this.gsbm = gsbm;
		this.xtid = xtid;
		this.bmc = bmc;
		this.zdbs = zdbs;
		this.zdcd = zdcd;
		this.zdlx = zdlx;
		this.zdsjylx = zdsjylx;
		this.ppjg = ppjg;
		this.shyj = shyj;
		this.ppmb = ppmb;
		this.tjsjy = tjsjy;
		this.owner = owner;
	}

	public String getZdmc() {
		return zdmc;
	}

	public void setZdmc(String zdmc) {
		this.zdmc = zdmc;
	}

	public String getGsbm() {
		return gsbm;
	}

	public void setGsbm(String gsbm) {
		this.gsbm = gsbm;
	}

	public String getXtid() {
		return xtid;
	}

	public void setXtid(String xtid) {
		this.xtid = xtid;
	}

	public String getBmc() {
		return bmc;
	}

	public void setBmc(String bmc) {
		this.bmc = bmc;
	}

	public String getZdbs() {
		return zdbs;
	}

	public void setZdbs(String zdbs) {
		this.zdbs = zdbs;
	}

	public String getZdcd() {
		return zdcd;
	}

	public void setZdcd(String zdcd) {
		this.zdcd = zdcd;
	}

	public String getZdlx() {
		return zdlx;
	}

	public void setZdlx(String zdlx) {
		this.zdlx = zdlx;
	}

	public String getZdsjylx() {
		return zdsjylx;
	}

	public void setZdsjylx(String zdsjylx) {
		this.zdsjylx = zdsjylx;
	}

	public String getPpjg() {
		return ppjg;
	}

	public void setPpjg(String ppjg) {
		this.ppjg = ppjg;
	}

	public String getShyj() {
		return shyj;
	}

	public void setShyj(String shyj) {
		this.shyj = shyj;
	}

	public String getPpmb() {
		return ppmb;
	}

	public void setPpmb(String ppmb) {
		this.ppmb = ppmb;
	}

	public String getTjsjy() {
		return tjsjy;
	}

	public void setTjsjy(String tjsjy) {
		this.tjsjy = tjsjy;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return owner;
	}
	
	

}
