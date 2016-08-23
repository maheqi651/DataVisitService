package com.easymap.datacenter.model;

public class ModelMappingNode {
	public String ModelName;
	public String ClassName;
	public String ViewName;
	public String Property;
	public String Field;
	public String Key;
	public String TotalFieldName;
	public String TotalPropertyName;
	public String ModelPropertyName;
	
	public ModelMappingNode(){
		
	}
	public ModelMappingNode(String tableName, String fieldName, String modelName, 
			String propertyName, String className, String modelPropertyName, String key){
		ViewName = tableName;
		
		//带有表名的字段名全路径名
		TotalFieldName = fieldName;
		ModelName = modelName;
		
		//Java Class的全路径属性名称
		TotalPropertyName = propertyName;
		ClassName = className;
		Key = key;
		
		ModelPropertyName = modelPropertyName;
		
		String ModelSepartor = "\\.";
		
		//模型的属性全路径名称
		if (ModelPropertyName != null)
		{
			String []PropertyNameList = ModelPropertyName.split(ModelSepartor);
			Property = PropertyNameList[PropertyNameList.length-1];
		}
		
		//表的字段名
		String []PropertyNameList = TotalFieldName.split(ModelSepartor);
		Field = PropertyNameList[PropertyNameList.length-1];
	}
}
