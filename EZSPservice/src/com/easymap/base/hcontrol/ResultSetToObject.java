package com.easymap.base.hcontrol;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.easymap.base.readdatabase.ConnectionDB;


/**
 * 通过传�?的ResultSet和Dom名进行自动转换成Object
 * @author kate
 *
 */
public class ResultSetToObject {
	private String strEntity="";
	private ResultSet rsResult=null;
	private  Connection conn;
    private  PreparedStatement ps;
    ConnectionDB cd = new ConnectionDB();
	public ResultSetToObject(){}
	public ResultSetToObject(ResultSet rs,String className){
		this.strEntity=className;
		this.rsResult=rs;
	}
	public  Object[] parseDataEntityBeans(String sql,String strEntity) throws Exception{
		 Object objResutlArray=null;
		if(sql!=null&&!sql.equals("")){
			try {
				conn = cd.getConnection();
					//注册实体,strEntity指定的实体类名称字符�?
					Class<?> classEntity = null;
					try {
						classEntity = Class.forName(strEntity);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//获取实体中定义的方法
					HashMap hmMethods = new HashMap();
					for (int i = 0; i < classEntity.getDeclaredMethods().length; i++){
						MethodEntity methodEntity = new MethodEntity();
						//方法的名�?
				        String methodName = classEntity.getDeclaredMethods()[i].getName();
				        String methodKey = methodName.toUpperCase();
				        //方法的参�?
				        Class[] paramTypes = classEntity.getDeclaredMethods()[i].getParameterTypes();
				        methodEntity.setMethodName(methodName);
				       // System.out.println(methodName);
				        methodEntity.setMethodParamTypes(paramTypes);
				        //处理方法重载
				       if(hmMethods.containsKey(methodKey)){
				            methodEntity.setRepeatMethodNum(methodEntity.getRepeatMethodNum()+1);
				            methodEntity.setRepeatMethodsParamTypes(paramTypes);
				        }else{
				          hmMethods.put(methodKey, methodEntity);
				        }
				    }
					if (conn != null) {
						ps = conn.prepareStatement(sql);
						rsResult = ps.executeQuery();
						DataTableEntity dataTable = null;
						List listResult = new ArrayList();
				   //处理ResultSet结构体信�?\
				    if(rsResult != null){
				     ResultSetMetaData rsMetaData = rsResult.getMetaData();
				      int columnCount = rsMetaData.getColumnCount();
				      dataTable = new DataTableEntity(columnCount);
				      //获取字段名称，类�?
				   for (int i = 0; i < columnCount; i++){
				        String columnName = rsMetaData.getColumnName(i + 1);
				        int columnType = rsMetaData.getColumnType(i + 1);
				        dataTable.setColumnName(columnName, i); 
				        dataTable.setColumnType(columnType, i);
				      }
				    }
				    //处理ResultSet数据信息
				    while(rsResult.next()){
				      //调用方法，根据字段名在hsMethods中查找对应的set方法
				      Object objResult = ParseObjectFromResultSet(rsResult, dataTable, classEntity, hmMethods);
				      listResult.add(objResult);
				    }
				    //以数组方式返
				    objResutlArray = Array.newInstance(classEntity, listResult.size());
				    listResult.toArray((Object[]) objResutlArray);
					rsResult.close();
					ps.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					
				}
			}
			}else{
				System.err.println("无可执行sql");
			}
	
    return (Object[])objResutlArray;
	}
/**
* 从Resultset中解析出单行记录对象，存储在实体对象�?
*/
public  Object ParseObjectFromResultSet(ResultSet rs,DataTableEntity dataTable,Class classEntity, HashMap hsMethods)throws Exception{
    Object objEntity = classEntity.newInstance();
    Method method = null;
    int nColumnCount = dataTable.getColumnCount();
    String[] strColumnNames = dataTable.getColumnNames();
    for (int i = 0; i < nColumnCount; i++){
        //获取字段�?
        Object objColumnValue = rs.getObject(strColumnNames[i]);
        //HashMap中的方法名key�?
        String strMethodKey = null;
        //获取set方法�?
        if (strColumnNames[i] != null){
            strMethodKey = String.valueOf("SET" + strColumnNames[i].toUpperCase());
        }
        //值和方法都不为空,这里方法名不为空即可,值可以为空的
        if (strMethodKey!=  null){
            //判断字段的类�?方法名，参数类型
           try{   
              MethodEntity methodEntity = (MethodEntity)hsMethods.get(strMethodKey);
              if(methodEntity!=null){
              String methodName = methodEntity.getMethodName();
              int repeatMethodNum = methodEntity.getRepeatMethodNum();
              
              Class[] paramTypes = methodEntity.getMethodParamTypes();
             
              method = classEntity.getMethod(methodName, paramTypes);
              
              //如果重载方法�?> 1，则判断是否有java.lang.IllegalArgumentException异常，循环处�?
              try{
                  //设置参数,实体对象，实体对象方法参�?
            	  if(paramTypes[0].getName().equals("java.lang.Integer"))
                  {
            		   if(objColumnValue!=null)
            		   method.invoke(objEntity, new Object[]{Integer.parseInt(objColumnValue.toString())});
            		 /*  if(strMethodKey.contains("ID"))
                           System.out.println("methodName:"+methodName+"::"+objColumnValue+":::"+paramTypes[0].getName()+"::--"+objColumnValue.toString());
            		*/   
                  }else if(paramTypes[0].getName().equals("java.math.BigDecimal")){
                	  //java.math.BigDecimal
                	 // System.out.println("-----------"+objColumnValue);
                	  if(objColumnValue!=null)
               		   method.invoke(objEntity, new Object[]{objColumnValue.toString()});
               		
                  }else{
                	  if(objColumnValue instanceof java.math.BigDecimal)
                	  {
                		 // System.out.println(methodName+"-----big------"+paramTypes[0].getName()+"---------------"+objColumnValue.toString());
                		  if(objColumnValue!=null)
                   		   method.invoke(objEntity, new Object[]{objColumnValue.toString()});
                	  }else{
                		   method.invoke(objEntity, new Object[]{objColumnValue});
                	  }
                  }
              }catch(java.lang.IllegalArgumentException e){
            	    e.printStackTrace();
                  //处理重载方法
                  for(int j = 1; j < repeatMethodNum; j++){
                       try{
                          Class[] repeatParamTypes = methodEntity.getRepeatMethodsParamTypes(j - 1);
                          method = classEntity.getMethod(methodName, repeatParamTypes);
                          method.invoke(objEntity, new Object[]{objColumnValue});
                          break;
                      }catch(java.lang.IllegalArgumentException ex){
                    	  ex.printStackTrace();
                          continue;
                       }
                  }
              }
              }
           }catch (NoSuchMethodException e){
             throw new NoSuchMethodException();
           }catch(Exception ex){
             ex.printStackTrace();
           }
        }
    }
    return objEntity;
}
}
