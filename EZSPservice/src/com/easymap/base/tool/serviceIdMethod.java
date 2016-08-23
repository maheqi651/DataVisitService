package com.easymap.base.tool;

import com.easymap.base.hcontrol.ResultSetToObject;
import com.easymap.dao.SRVdirectory;
/**
 * @author cloudma
 *
 */
public  class serviceIdMethod {
	public static String getServiceIdMethod(String methodName) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		String serviceId="";
		String sqlT="SELECT T.NAME,T.INFO,T.TYPE,T.SERVICEID,T.PARENTSERVICEID,T.METHODNAME FROM EZSPATIAL.EZ_SERVICE_INFO T WHERE T.METHODNAME='"+methodName+"'";
		String objUrlT="com.easymap.dao.SRVdirectory";
		Object[] obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		for(int i=0;i<obT.length;i++){
			SRVdirectory so =(SRVdirectory) obT[i];
			serviceId=so.getSERVICEID();
		}
		return serviceId;
	}
}
