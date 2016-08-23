package com.easymap.datacenter.model;

import net.sf.json.JSONObject;

/** * @author  Zhangxt 
 * @date 创建时间：2015年9月16日 下午10:01:18 
 * @version 1.0 
 * @parameter  
 * @since  
 * @return  
 * */
public class metaData {

	/*
	 * @Description: ${0 符合标准；1 部分符合；2 完全不符标准}
	 */
	public int type =0; // 0 符合标准；1 部分符合；2 完全不符标准
	// 默认都是找到的
	
	public fieldMetaData Code = new fieldMetaData();// decode 主键查找，可以为空；空则查找 	
	public fieldMetaData Name = new fieldMetaData() ;//字段名称，二号主键
	public fieldMetaData Describe = new fieldMetaData();// 描述,不能为空
	public fieldMetaData DataType = new fieldMetaData(); // 数据类型，完全匹配		
	public fieldMetaData MaxSize = new fieldMetaData() ;		
	public String Format; // 解析检查
	
	public boolean ifEmpty(){
		
		if((this.Name.source==null || this.Name.source.equals(""))
				&&
		   (this.Describe.source==null||this.Describe.source.equals(""))
		        &&
		   (this.MaxSize.source==null || this.MaxSize.source.equals(""))
		   )
			return true;
		return false;
	}
	


	/** * @author  Zhangxt
	 * @date 创建时间：2015年9月16日 下午10:01:18 
	 * @version 1.0 
	 * @parameter  
	 * @since 
	 * @return  
	 */
	public String getJson(){

		JSONObject  ajo = new JSONObject();
		ajo.put("type", this.type);
		ajo.put("Code", this.Code.getJson());
		ajo.put("Name", this.Name.getJson());
		ajo.put("Describe", this.Describe.getJson());
		ajo.put("DataType", this.DataType.getJson());
		ajo.put("MaxSize", this.MaxSize.getJson());
		return ajo.toString();
		

	}
	
}
