package com.easymap.base.readxml;


import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
 
 
 
public class Object2XML {
 /**
  * �����䵽XML�ļ�
  * @param obj ������Ķ���
  * @throws FileNotFoundException
  */
 
 public String object2XML(Document document){
  //�������XML�ļ����ֽ������
	 String XMLStr = document.asXML();//obj.asXML()��ΪDocument����ת��Ϊ�ַ�������  
	 return XMLStr; 
 }
 /**
  * ��XML�ļ�����ɶ���
  * @param inFileName�����XML�ļ�
  * @return �������ɵĶ���
  * @throws FileNotFoundException
  */
 public Document xml2Object(String xmlStr){
	 Document document = null; 
	 try {
		document = DocumentHelper.parseText(xmlStr);
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  return document;
 }
 public String getXmlNodeValue(Element d,String nodeName){
	 List<Element> list = d.elements();//�ĵ�submit��������Ԫ��
	String str="";
	   for(Iterator it=list.iterator();it.hasNext();){
		   Element currentelement = (Element)it.next();
		   if(currentelement.getName().equals(nodeName)){
			   str= currentelement.getText();
		   }else{
			   getXmlNodeValue(currentelement,nodeName);
		   }
	   }
	return str;
 }
}
