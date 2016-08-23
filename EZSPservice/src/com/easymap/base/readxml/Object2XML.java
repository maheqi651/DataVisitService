package com.easymap.base.readxml;


import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
 
 
 
public class Object2XML {
 /**
  * 对象输到XML文件
  * @param obj 待输出的对象
  * @throws FileNotFoundException
  */
 
 public String object2XML(Document document){
  //构造输出XML文件的字节输出流
	 String XMLStr = document.asXML();//obj.asXML()则为Document对象转换为字符串方法  
	 return XMLStr; 
 }
 /**
  * 把XML文件解码成对象
  * @param inFileName输入的XML文件
  * @return 返回生成的对象
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
	 List<Element> list = d.elements();//的到submit的所有子元素
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
