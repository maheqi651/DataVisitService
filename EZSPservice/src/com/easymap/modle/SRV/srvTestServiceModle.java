package com.easymap.modle.SRV;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.hcontrol.ResultSetToObject;
import com.easymap.base.minterface.modle;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.SRVdirectory;
import com.easymap.filter.Tools;

public class srvTestServiceModle implements modle {

	public String getXML(Map map, String senderID, String methodName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public String getXML(String serviceID, String senderID, String methodName)
	throws Exception {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
		Element ServiceID = rootElement.addElement("ServiceID");
		ServiceID.setText(serviceIdMethod.getServiceIdMethod(methodName));
		Element Method = rootElement.addElement("Method");
		Element Name = Method.addElement("Name");
		Name.setText(methodName);
		Element Security = Method.addElement("Security");
		Security.addAttribute("Algorithm","");
		Security.setText("");
		Element Services = Method.addElement("Items");
		Object[] obT=getDateObjModle(serviceID,senderID);
		if(obT!=null&&obT.length>0){
		for(int i=0;i<obT.length;i++){
			SRVdirectory so =(SRVdirectory) obT[i];
			Element Service = Services.addElement("Item");
			Element NameI = Service.addElement("Name");
			if(so.getMETHODNAME()!=null){
				NameI.setText(so.getMETHODNAME());
			}else{
				NameI.setText("");
			}
			Element Type = Service.addElement("Type");
			if(so.getTYPE()!=null){
				Type.setText(so.getTYPE());
			}else{
				Type.setText("");
			}
			/*Element Cname = Service.addElement("Cname");
			if(so.getNAME()!=null){
				Cname.setText(so.getNAME());
			}else{
				Cname.setText("");
			}*/
			Element ID = Service.addElement("ID");
			if(so.getSERVICEID()!=null){
				ID.setText(so.getSERVICEID());
			}else{
				ID.setText("");
			}
			Element Status = Service.addElement("Status");
			if(judge(serviceID,senderID))
				Status.setText("Success");
			else
				Status.setText("Access Denied");
			Element ErrorCode = Service.addElement("ErrorCode");
			ErrorCode.setText("NONE");
		}
		}else{
			Element Service = Services.addElement("Item");
			Element Status = Service.addElement("ErrorCode");
			Status.setText("ServiceId IS NOT EXIST");
		}
		return rootElement.asXML();
	}
	public Object[] getDateObjModle(Map map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public Object[] getDateObjModle(String serviceID,String senderID) throws Exception {
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		sqlT="SELECT T.NAME,T.INFO,T.TYPE,T.SERVICEID,T.PARENTSERVICEID,T.METHODNAME FROM  "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T WHERE T.SERVICEID='"+serviceID+"'";
		objUrlT="com.easymap.dao.SRVdirectory";
		obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		return obT;
	}
	public boolean judge(String serviceID,String senderID)  {
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] o = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		sqlT="SELECT T.FUNCCODE,T.SERCODE,T.TABLECODE,T.THEMECODE  FROM "+Tools.YW6+".ez_p_function_service T WHERE T.sercode='"+serviceID+"'"
		+" and T.funccode='"+senderID+"'";
		objUrlT="com.easymap.dao.EPFSBean";
	    try {
			o  = pdb.parseDataEntityBeans(sqlT,objUrlT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(o!=null&&o.length>0){
			return true;
		} 
		return false;
	}
	
}
