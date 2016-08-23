package com.easymap.modle.SRV;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.minterface.modle;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.authorizationDataDao;
import com.easymap.modle.authorization.authorization;
/**
 * 
 * @author kate
 *
 */
public class srvQueryDataResourceModle implements modle {
	authorization aobj = new authorization();
	public Object[] getDateObjModle(Map map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public String getXML(Map map, String senderID, String methodName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 *  
	 * @param serviceID
	 * @param senderID
	 * @return
	 * @throws Exception
	 */
	public String getXML(String serviceID, String senderID,String methodName)
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
		Element Items = Method.addElement("Items");
		Object[] obT=aobj.getAuthorizationTablecode(senderID, serviceID);
		if(obT!=null&&obT.length>0){
		for(int i=0;i<obT.length;i++){
			authorizationDataDao so =(authorizationDataDao) obT[i];
			Element Item = Items.addElement("Item");
			Element NameI = Item.addElement("Name");
			if(so.getTablecode()!=null){
				NameI.setText(so.getTablecode());
			}else{
				NameI.setText("");
			}
			Element Type = Item.addElement("Type");
			if(so.getLayertype()!=null){
				Type.setText(so.getLayertype());
			}else{
				Type.setText("");
			}
			Element ID = Item.addElement("ID");
			if(so.getCode()!=null){
				ID.setText(so.getCode());
			}else{
				ID.setText("");
			}
		}
		}else{
			Items.setText("null");
		}
		return rootElement.asXML();
	}

}
