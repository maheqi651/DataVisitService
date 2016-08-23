package com.easymap.datacenter.model;

import java.util.ArrayList;

public class SystemTableMeta {

	public String zdmc="";
	public String gsbm = "";
	public int  xtid = 0;
	public String bmc = "";
	public String zdbs = "";// 字段标识
	public String zdcd = "";
	public String zdlx = "";
	public String zdsjylx = ""; // 字段设计与类型
	public String ppjg = "";
	public String shyj = "";
	public String tjsjy = null;// 推荐数据元
	public String owner = ""; //数据元所属的用户
	
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


	public int getXtid() {
		return xtid;
	}


	public void setXtid(int xtid) {
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


	public String getTjsjy() {
		return tjsjy;
	}


	public void setTjsjy(String tjsjy) {
		this.tjsjy = tjsjy;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public metaData getAmd() {
		return amd;
	}


	public void setAmd(metaData amd) {
		this.amd = amd;
	}

	public metaData amd= new metaData();
	

	// 20151027  得到推荐的值
	
	public String Gettjsjy(boolean ifrefine){
		
		// 要有描述
		if(amd.type == 2){
			if(this.amd.Describe.source==null || this.amd.Describe.source.equals(""))
			return "";
			
			// 之前没有填写过
			if(this.tjsjy==null){
				StringBuffer sb = new StringBuffer();
				ArrayList<String> alist = CheckHelper.getInstance().GetrecommendFromDescri(
						this.amd.Describe.source,ifrefine);
				if(alist.size()>0){
					sb.append(alist.get(0));
					for(int i=1;i<alist.size();i++){
						sb.append(","+alist.get(i));
					}
				}
				
				this.tjsjy = sb.toString();
				
			}
			else
				return this.tjsjy;
		}
		return this.tjsjy;
	}
	
	
//public String Gettjsjy(){
//	boolean ifrefine = true;
//	return this.Gettjsjy(ifrefine);
//	}

	// 可以复用这些
	public void setAmd() {
		// TODO Auto-generated method stub
		this.amd.DataType.source = this.zdsjylx;
		this.amd.Describe.source = this.zdmc;//字段名称
		this.amd.MaxSize.source = this.zdcd;
		this.amd.Name.source = this.zdbs;//字段标识
	}

	// 返回推荐的理由
	public String GetRecommend(){
		StringBuffer sb = new StringBuffer();
		
	//	if(amd.Code.ifMatch==false)
			sb.append("数据元"+amd.Code.fixed+"。");
		if(amd.Name.ifMatch==false)
			sb.append("字段名称不符，"+amd.Name.fixed+"。");
		if(amd.Describe.ifMatch == false){
			sb.append(amd.Describe.fixed+"。");
		}
		if(amd.DataType.ifMatch==false){
			sb.append("数据类型不符，"+amd.DataType.fixed+"。");
		}
		if(amd.MaxSize.ifMatch==false)
			sb.append("字段长度不符，"+amd.MaxSize.fixed+"。");
		
		return sb.toString();
	}
}
