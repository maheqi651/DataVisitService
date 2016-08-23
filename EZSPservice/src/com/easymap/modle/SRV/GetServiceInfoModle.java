package com.easymap.modle.SRV;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.hcontrol.ResultSetToObject;
import com.easymap.base.minterface.modle;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.GetInfoBean;
import com.easymap.dao.SRVdirectory;
import com.easymap.filter.Tools;
import com.easymap.modle.authorization.authorization;

public class GetServiceInfoModle implements modle {

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
		String methodId=serviceIdMethod.getServiceIdMethod(methodName);
		if(authorization.isAuthorization(senderID,methodId)) 
		{   
		Object[] obT=getDateObjModle(serviceID,senderID);
		if(obT!=null&&obT.length>0){
			GetInfoBean so =(GetInfoBean) obT[0];
			
			Element item1 = Services.addElement("Item");
			Element code1 = item1.addElement("Code");
			code1.setText("ServiceName");
			Element dispName1 = item1.addElement("DispName");
			dispName1.setText(so.getNAME());
			Element  desp1 = item1.addElement("Desp");
		    desp1.setText(so.getINFO());
			Element  Value1 = item1.addElement("Value");
			Value1.setAttributeValue("type", "String");
			Element  data1 = Value1.addElement("Data");
			data1.setText(so.getMETHODNAME());
			
			
			
			Element item2 = Services.addElement("Item");
			Element code2 = item2.addElement("Code");
			code2.setText("Type");
			Element dispName2 = item2.addElement("DispName");
			dispName2.setText(so.getTYPE());
			Element  desp2 = item2.addElement("Desp");
		    desp2.setText("Res：资源目录接口, RBSP：数据访问接口, NoSQL：非结构化信息访问接口, Analyse：分析类接口 ,Monitor：服务监控接口");
			Element  Value2 = item2.addElement("Value");
			Value2.setAttributeValue("type", "String");
			Element  data2 = Value2.addElement("Data");
			data2.setText(so.getTYPE());
			
			
			Element item3 = Services.addElement("Item");
			Element code3 = item3.addElement("Code");
			code3.setText("Version");
			Element dispName3 = item3.addElement("DispName");
			dispName3.setText("版本信息");
			Element  desp3 = item3.addElement("Desp");
		    desp3.setText("最新版本记录");
			
			Element  Value3 = item3.addElement("Value");
			Value3.setAttributeValue("type", "String");
			Element  data3 = Value3.addElement("Data");
			data3.setText("1.0.0.0");
			
			
			Element item4 = Services.addElement("Item");
			Element code4 = item4.addElement("Code");
			code4.setText("TableCodes");
			Element dispName4 = item4.addElement("DispName");
			dispName4.setText("可访问数据资源代码");
			Element  desp4 = item4.addElement("Desp");
		    desp4.setText("数据表TABLECODE列表");
		    Element  Value4 = item4.addElement("Value");
		    Value4.setAttributeValue("type", "String");
		    for(int i=0;i<obT.length;i++)
		    {
		    	GetInfoBean got =(GetInfoBean) obT[i];
		    	Element  data4 = Value4.addElement("Data");
		    	if(got.getTABLECODE()==null)
		    		data4.setText("");
		    	else
		    	data4.setText(got.getTABLECODE());
		    }
		}else{
			Element Service = Services.addElement("Item");
			Element Status = Service.addElement("ErrorCode");
			Status.setText("Access Denied");
		}
		}else{
			Element Service = Services.addElement("Item");
			Element Status = Service.addElement("ErrorCode");
			Status.setText("Access Denied");
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
		sqlT="SELECT T.NAME, T.INFO, T.TYPE, T.SERVICEID, T.PARENTSERVICEID, T.METHODNAME ,p.tablecode "
        +" FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T,"+Tools.YW6+".ez_p_function_service p"
        +" WHERE T.SERVICEID = '"+serviceID+"' and T.serviceid=p.sercode and p.funccode='"+senderID+"'";
		objUrlT="com.easymap.dao.GetInfoBean";
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
