package com.easymap.modle.SRV;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.hcontrol.ResultSetToObject;
import com.easymap.base.minterface.modle;
import com.easymap.base.tool.JDBC_ColumnType;
import com.easymap.base.tool.JDBC_ColumnTypeFileds;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.GetDataResourceInfoBean;
import com.easymap.dao.GetInfoBean;
import com.easymap.dao.SRVdirectory;
import com.easymap.filter.Tools;

public class GetDataResourceInfoModle implements modle {

	public String getXML(Map map, String senderID, String methodName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public String getXML(String senderID,String methodName,String tablecode)
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
		Object[] obT=getDateObjModle(tablecode);
		if(obT!=null&&obT.length>0){
			GetDataResourceInfoBean so =(GetDataResourceInfoBean) obT[0];
			
			Element item1 = Services.addElement("Item");
			Element code1 = item1.addElement("Name");
			code1.setText("ID");
			Element  Value1 = item1.addElement("Value");
			Value1.setAttributeValue("type", "String");
			Element  data1 = Value1.addElement("Data");
			if(so.getID()==null)
				data1.setText("");
			else
				data1.setText(so.getID());
			
			
			
			Element item2 = Services.addElement("Item");
			Element code2 = item2.addElement("Name");
			code2.setText("Type");
			Element  Value2 = item2.addElement("Value");
			Value2.setAttributeValue("type", "String");
			Element  data2 = Value2.addElement("Data");
			if(so.getLAYERTYPE()==null)
				data2.setText("");
			else
				data2.setText(so.getLAYERTYPE());
			
			
			
			Element item3 = Services.addElement("Item");
			Element code3 = item3.addElement("Name");
			code3.setText("DispName");
 
			
			Element  Value3 = item3.addElement("Value");
			Value3.setAttributeValue("type", "String");
			Element  data3 = Value3.addElement("Data");
		 
			if(so.getCHNAME()==null)
				data3.setText("");
			else
				data3.setText(so.getCHNAME());
			
			Element item4 = Services.addElement("Item");
			Element code4 = item4.addElement("Name");
			code4.setText("Description");
		    Element  Value4 = item4.addElement("Value");
		    Value4.setAttributeValue("type", "String");
		    Element  data4 = Value4.addElement("Data");
			
			if(so.getINFORMATION()==null)
				data4.setText("");
			else
				data4.setText(so.getINFORMATION());
			
			Element item5 = Services.addElement("Item");
			Element code5 = item5.addElement("Name");
			code5.setText("Fields");
		    Element  Value5 = item5.addElement("Value");
		    Value5.setAttributeValue("type", "object");
		    Element  Fields = Value5.addElement("Fields");
			
		    for(int i=0;i<obT.length;i++)
		    {
		    	GetDataResourceInfoBean got =(GetDataResourceInfoBean) obT[i];
		    	Element  Field = Fields.addElement("Field");
		         if(got.getFIELDTYPE()==null)
		        	 Field.setAttributeValue("type", "String");
		         else
		         {
		        	/* Field.setAttributeValue("type", got.getFIELDTYPE());//type需要转换
*/		    	     Field.setAttributeValue("type", JDBC_ColumnTypeFileds.translate_InteractType(Integer.parseInt(got.getFIELDTYPE())));//type需要转换
 	         }
		         
		         if(got.getPARENTCODE()==null)
		        	 Field.setAttributeValue("prec", "");
		         else
		        	 Field.setAttributeValue("prec", got.getPARENTCODE());
		         
		         if(got.getDecimalsize()==null)
		        	 Field.setAttributeValue("scale", "");
		         else
		        	 Field.setAttributeValue("scale", got.getDecimalsize());
		         
		         if(got.getFIELDSIZE()==null)
		        	 Field.setAttributeValue("length", "");
		         else
		        	 Field.setAttributeValue("length", got.getFIELDSIZE());
		         
		         if(got.getDecode()==null)
		        	 Field.setAttributeValue("DECODE", "");
		         else
		        	 Field.setAttributeValue("DECODE",got.getDecode());
		         Field.setAttributeValue("DQCODE", "");
		         if(got.getDiccode()==null)
		        	 Field.setAttributeValue("DICCODE", "");
		         else
		        	 Field.setAttributeValue("DICCODE", got.getDiccode());
		    	
		         if(got.getALIASNAME()==null)
		        	 Field.setAttributeValue("CHNAME", "");
		         else
		        	 Field.setAttributeValue("CHNAME",got.getALIASNAME());
		    	if(got.getFIELDNAME()==null)
		    		Field.setText("");
				else
					Field.setText(got.getFIELDNAME());
		    }
		}else{
			Element Service = Services.addElement("Item");
			Element Status = Service.addElement("ErrorCode");
			Status.setText("Access Denied");
		}
		return rootElement.asXML();
	}
	 
	public Object[] getDateObjModle(String tablecode) throws Exception {
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		sqlT="SELECT F.FIELDTYPE, F.FIELDSIZE, F.FIELDNAME,  F.Parentcode,  F.Decode, F.Diccode,F.Decimalsize,F.PARENTCODE,T.ID,T.LAYERTYPE,T.CHNAME,T.INFORMATION , F.ALIASNAME"
	    + " from "+Tools.EZSPATIAL+".EZ_STD_LAYERS_FIELDS F INNER join "+Tools.EZSPATIAL+".EZ_STD_LAYERS_LAYER T ON F.PARENTCODE = T.CODE  AND t.Code = '"+tablecode+"' ";
		objUrlT="com.easymap.dao.GetDataResourceInfoBean";
		System.out.println(sqlT);
		obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		return obT;
	}
	@Override
	public Object[] getDateObjModle(Map map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
