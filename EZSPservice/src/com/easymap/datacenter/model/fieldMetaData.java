package com.easymap.datacenter.model;

import net.sf.json.JSONObject;

/** * @author  Zhangxt 
 * @date 创建时间：2015年9月16日 下午10:01:39 
 * @version 1.0 
 * @parameter  
 * @since  
 * @return  */
public class fieldMetaData {

	// 定义原来的值，和需要修改的值
	//ifMatch 标记是否匹配了
	public String source="";
	public String fixed="";
	public boolean ifMatch = false;
	
	public String getJson(){

		JSONObject  ajo = new JSONObject();
		ajo.put("source", this.source);
		ajo.put("fixed", this.fixed);
		ajo.put("ifMatch", this.ifMatch);
		return ajo.toString();
		}
	



}
