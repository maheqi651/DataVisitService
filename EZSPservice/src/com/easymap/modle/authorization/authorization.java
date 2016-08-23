package com.easymap.modle.authorization;

import com.easymap.base.hcontrol.ResultSetToObject;
import com.easymap.filter.Tools;
/**
 *  
 * @author kate
 *
 */
public class authorization {
	/**
	 *  
	 * @param senderID
	 * @param serviceID
	 * @return true or false
	 */
	public static boolean isAuthorization(String senderID,String serviceID){
		//String sqlT="SELECT T.FUNCCODE,T.SERCODE,T.TABLECODE FROM EZMANAGERV6.EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE = '"+senderID+"' AND T.SERCODE='"+serviceID+"'";
	String sqlT="SELECT T.FUNCCODE,T.SERCODE,T.TABLECODE FROM  "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE = '"+senderID+"' AND T.SERCODE='"+serviceID+"'";
		//String sqlT="SELECT T.FUNCCODE,T.SERCODE,T.TABLECODE FROM EZMANAGERV6.EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE = '"+senderID+"' AND T.SERCODE='"+serviceID+"'";
		System.out.println(sqlT);
	
		String objUrlT="com.easymap.dao.authorizationDao";
		Object[] o = getData(sqlT,objUrlT);
		if(o!=null&&o.length>0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 *  
	 * @param senderID
	 * @param serviceID
	 * @return
	 */
	public static Object[] getAuthorizationTablecode(String senderID,String serviceID){
		String	sqlT="SELECT T.FUNCCODE,T.SERCODE,T.TABLECODE,T1.CHNAME,T1.ENNAME,T1.LAYERTYPE,T1.CODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T,"+Tools.EZSPATIAL+".EZ_STD_LAYERS_LAYER T1 " +
		"WHERE T.FUNCCODE = '"+senderID+"' AND T.SERCODE='"+serviceID+"' AND T.TABLECODE = T1.CODE";
		
		
		 /*String	sqlT="SELECT T.FUNCCODE,T.SERCODE,T.TABLECODE,T1.CHNAME,T1.ENNAME,T1.LAYERTYPE,T1.CODE FROM EZMANAGERV6.EZ_P_FUNCTION_SERVICE T,EZSPATIAL.EZ_STD_LAYERS_LAYER T1 " +
		"WHERE T.FUNCCODE = '"+senderID+"' AND T.SERCODE='"+serviceID+"' AND T.TABLECODE = T1.CODE";
 * 	*/
		
		System.out.println(sqlT);
		String	objUrlT="com.easymap.dao.authorizationDataDao";
		return getData(sqlT,objUrlT);
	}
	
	 public static Object[] getAuthorizationTablecode1(String serviceID){
		String	sqlT="SELECT T.TABLECODE FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T " +
		"WHERE  T.SERVICEID='"+serviceID+"' ";
		System.out.println(sqlT);
		String	objUrlT="com.easymap.dao.authorizationDataDao";
		return getData(sqlT,objUrlT);
	}
	 
	 
	 public static Object[] getAuthorizationTablecode3(String appId,String serviceID){  //专用接口
		 String	sqlT="SELECT T.TABLECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T " +
			"where T.Sercode='"+serviceID+"' "
			+" and t.funccode='"+appId+"'";
		    System.out.println("-----------"+sqlT);
			String	objUrlT="com.easymap.dao.authorizationDataDao";
			return getData(sqlT,objUrlT);
		}
	 public static Object[] getAuthorizationTablecode33(String appId,String serviceID){  //专用接口
		 String	sqlT="SELECT T.ACCESSAUTH FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T " +
			"where T.Sercode='"+serviceID+"' "
			+" and t.funccode='"+appId+"'";
		    System.out.println("-----------"+sqlT);
			String	objUrlT="com.easymap.dao.authorizationDataDao";
			return getData(sqlT,objUrlT);
		}
	 public static Object[] getAuthorizationSpecialTablecode(String serviceID){  //专用接口
			String	sqlT="SELECT T.TABLECODE FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T " +
			"WHERE   T.origin_serviceid is not Null ";
			System.out.println(sqlT);
			String	objUrlT="com.easymap.dao.authorizationDataDao";
			return getData(sqlT,objUrlT);
		}
	 
	 public static Object[] getAuthorizationSpecialTablecodeFilter(String serviceID){  //专用接口filter
			String	sqlT="SELECT T.TABLECODE FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T " +
			"WHERE   T.origin_serviceid is not Null and T.serviceid='"+serviceID+"'";
			System.out.println(sqlT);
			String	objUrlT="com.easymap.dao.authorizationDataDao";
			return getData(sqlT,objUrlT);
		}
	 public static Object[] getAuthorizationSpecialTablecode(){  //专用接口
			String	sqlT="SELECT T.METHODNAME FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T " +
			"WHERE   T.origin_serviceid is not Null ";
			System.out.println(sqlT);
			String	objUrlT="com.easymap.dao.authorizationDataDao";
			return getData(sqlT,objUrlT);
		}
	 public static Object[] getAuthorizationTablecode2(String serviceID){
			String	sqlT="SELECT T.TABLECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T " +
			"where T.Sercode='"+serviceID+"' ";
			String	objUrlT="com.easymap.dao.authorizationDataDao";
			return getData(sqlT,objUrlT);
		}
	 
	 public static Object[] getAuthorizationTablecode4(String senderID){
			String	sqlT="SELECT T.TABLECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T " +
			"where T.funccode='"+senderID+"' ";
			System.out.println(sqlT);
			String	objUrlT="com.easymap.dao.authorizationDataDao";
			return getData(sqlT,objUrlT);
		}
	private static Object[] getData(String sql,String objUrl){
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		try {
			obT = pdb.parseDataEntityBeans(sql,objUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obT;
	}
}
