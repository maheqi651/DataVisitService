package com.easymap.base.pools.glk.bean;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.misc.BASE64Encoder;

import com.easymap.base.pools.DBCPPools;
import com.easymap.base.tool.ReadProperties;
import com.easymap.filter.Tools;

public class DicMapLoad {

	   private Connection connnection = null;
	   private PreparedStatement preparedStatement = null;
	   private ResultSet resultSet = null;
	   private static String DRIVER;
	   private static String URLSTR;
	   private static String USERNAME;
	   private static String USERPASSWORD;
	   private static ReadProperties READPROPERTIES;
	   static {
			try {
				READPROPERTIES = new ReadProperties();
				URLSTR = READPROPERTIES.getUrl();
				USERNAME = READPROPERTIES.getUsername();
				USERPASSWORD = READPROPERTIES.getPassword();
				DRIVER = READPROPERTIES.getDriver();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	   private DicMapLoad(Connection connnection){
		       this.connnection=connnection;
	   }
	  
	   public static DicMapLoad getInstance(Connection connnection)
	   {
		  return  new DicMapLoad(connnection);
	   }
	   
	   public static void dealDicMap() throws Exception{
		   DicMapLoad dcml=DicMapLoad.getInstance(DBCPPools.getInstance().getConnection(DRIVER,URLSTR, USERNAME, USERPASSWORD));
		   String sql="select t.fieldname,f.code,f.name "
			   +" from "+Tools.EZSPATIAL+".ez_std_layers_fields t,"+Tools.EZSPATIAL+".ez_dic_def d,"+Tools.EZSPATIAL+".ez_dic_codes f "
			   +" where t.diccode is not null and d.code=t.diccode and f.defid=d.id";
		   Object[] result=dcml.executeQuery(sql, null);
		   dcml.dealResult(result);
		   System.out.println("字典翻译完成.........");
	   }
	   public void dealResult(Object[] result){
		      if(result!=null)
		      {
		    	  if(result[1]!=null)//处理resultlist
		    	    dealResultList((List<Object[]>)result[1]);
		      }
	   }
	   public void dealResultList(List<Object[]> listresult){
		      if(listresult!=null)
		      {//EntryTools
		    	  for(Object[] o:listresult)
		    	  {
		    		  EntryTools.dicmap.put(o[0]+EntryTools.DICMAPKEYFLAG+o[1], o[2]+"");
		    	  }
		      }
	   }
	   public Object[] executeQuery(String sql, Object[] params)  {
			//long times=System.currentTimeMillis();
			Object[] object = new Object[2];
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			try {
				// 调用SQL
				preparedStatement = connnection.prepareStatement(sql);

				// 参数赋值
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						preparedStatement.setObject(i + 1, params[i]);
					}
				}

				// 执行
				resultSet = preparedStatement.executeQuery();
				ResultSetMetaData rsmd = resultSet.getMetaData();
				int count = rsmd.getColumnCount();
				Map<String,Integer> map = new HashMap<String,Integer>();
				List<Object[]> list = new ArrayList<Object[]>();
				for(int j  = 1 ; j <= count ; j++){
					map.put(rsmd.getColumnName(j), rsmd.getColumnType(j));
				}
				//给数组赋值
				object[0] = map;
				while(resultSet.next()){
					Object[] colName = new Object[count];
					for(int i = 1 ; i <= count ; i++){
						if(resultSet.getObject(i)!=null && resultSet.getObject(i).toString().length()>16 && resultSet.getObject(i).toString().substring(0, 16).equals("oracle.sql.CLOB@"))
						{
							char[] c = new char[(int) ((Clob) resultSet.getObject(i)).length()];
							((Clob) resultSet.getObject(i)).getCharacterStream().read(c);
							colName[i - 1]= new String(c);
						}else if(resultSet.getObject(i)!=null && resultSet.getObject(i).toString().length()>16 &&resultSet.getObject(i).toString().substring(0, 16).equals("oracle.sql.BLOB@")){
							oracle.sql.BLOB blob = (oracle.sql.BLOB) resultSet.getBlob(i);
							InputStream is = blob.getBinaryStream();
							int length = (int) blob.length();
							byte[] data = new byte[length];
							is.read(data);
							is.close();
							BASE64Encoder  encoder = new BASE64Encoder(); 
							colName[i - 1]=encoder.encode(data);
						}else{
							colName[i - 1] = resultSet.getObject(i);
						}
					}
					list.add(colName);
				}
				
				//给数组赋值
				object[1] = list;
				//System.out.println("执行查询条件"+(System.currentTimeMillis()-times));
				
			} catch (SQLException e) {
				e.printStackTrace();
			}catch (IOException e){
				e.printStackTrace();
			}finally{
				free(resultSet, preparedStatement, connnection);
			}
			//System.out.println("执行查询条件-----2222-------"+(System.currentTimeMillis()-times));
			return object;
		}
	   
	   /**
		 * 关闭所有资源
		 */
		public static void free(ResultSet rs,PreparedStatement ps,Connection conn){
			try{
				if(rs != null){
					rs.close();
					rs = null;
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				try{
					if(ps != null){
						ps.close();
						ps = null;
					}
				}catch(SQLException e){
					e.printStackTrace();
				}finally{
					if(conn != null){
						try{
						conn.close();
						conn = null;
						}catch(SQLException e){
							e.printStackTrace();
						}
					}
				}
			}
			
		}
	
}
