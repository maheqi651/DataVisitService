package com.easymap.hbase.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.dom4j.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

public class XmlAccessor4Mapping {
	String filePath;
	
	private static XmlAccessor4Mapping xa= new XmlAccessor4Mapping();
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFilePath() {
		return filePath;
	}

	OutputFormat of= OutputFormat.createPrettyPrint();
	private XmlAccessor4Mapping(){
		//setFilePath的工作在InitSystem处完成
	}
	
	public static XmlAccessor4Mapping getInstance(){
		return xa;
	}
//	public XmlAccessor4Mapping(String filePath){
//		
//		this.filePath = filePath;
//	}
	
	Map<String,Document> msd = new HashMap<String, Document>();
	
	/**
	 * 获取Node属性
	 * @param filePath
	 * @param xPathExpr
	 * @return
	 */
	public List<String> getNodeAttributeValue(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		List<String> result = new ArrayList<String>();
		try {
			if(msd.get(xPathExpr)==null){
			doc =  saxReader.read(new File(filePath));
			msd.put(xPathExpr, doc);
			}
			else doc =msd.get(xPathExpr);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Attribute attribute = (Attribute) iter.next();
		result.add(attribute.getValue());
		}
		return result;
	}
	
	public List<String> getNodeAttributeValue(String xPathExpr){
		return getNodeAttributeValue(filePath, xPathExpr);
	}
	
	
	
	/**
	 * 获取Node内容
	 * @param filePath
	 * @param xPathExpr
	 * @return
	 */
	public List<String> getNodeText(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		List<String> result = new ArrayList<String>();
		try {
			if(msd.get(xPathExpr)==null){
			doc =  saxReader.read(new File(filePath));
			msd.put(xPathExpr, doc);
			}
			else doc =msd.get(xPathExpr);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		result.add(element.getText());
		}
		return result;
	}
	
	public List<String> getNodeText(String xPathExpr){
		return getNodeText(filePath, xPathExpr);
	}
	
	/**
	 * 
	 * @param filePath
	 * @param xPathExpr
	 * @param value
	 */
	public void setNodeAttribute(String filePath, String xPathExpr, String value){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			if(msd.get(xPathExpr)==null){
			doc =  saxReader.read(new File(filePath));
			msd.put(xPathExpr, doc);
			}
			else doc =msd.get(xPathExpr);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Attribute attribute = (Attribute) iter.next();
		attribute.setValue(value);
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			} 
	}
	
	public void setNodeAttribute(String xPathExpr, String value){
		setNodeAttribute(filePath, xPathExpr, value);
	}
	
//	/**
//	 * 
//	 * @param filePath
//	 * @param xPathExpr
//	 * @param value
//	 */
//	public void setNodeText(String filePath, String xPathExpr, String value){
//		//也可用于新增节点
//		SAXReader saxReader = new SAXReader();
//		Document doc = null;
//		try {
//			doc =  saxReader.read(new File(filePath));
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // 读取xml文档
//		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
//		Iterator iter = list.iterator();
//		while (iter.hasNext()) {
//		Element element = (Element) iter.next();
//		element.setText(value);
//		}
//		
//		try {
//			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
//			writer.write(doc);
//			writer.close();
//			} catch (Exception ex) {
//			ex.printStackTrace();
//			}
//	}
//	
//	public void setNodeText(String xPathExpr, String value){
//		setNodeText(filePath, xPathExpr, value);
//	}
	
//	/**
//	 * 删除属性，用法特殊，xPath到其tagName而不是具体的属性名
//	 * xa.removeNodeAttribute("books.xml", "//book", "year"); 
//	 * @param filePath
//	 * @param xPathExpr
//	 * @param aName
//	 */
//	public void removeNodeAttribute(String filePath, String xPathExpr, String aName){
//		SAXReader saxReader = new SAXReader();
//		Document doc = null;
//		try {
//			doc =  saxReader.read(new File(filePath));
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // 读取xml文档
//		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
//		Iterator iter = list.iterator();
//		while (iter.hasNext()) {
//		Element element = (Element) iter.next();
//		element.remove(element.attribute(aName));
//		}
//		
//		try {
//			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
//			writer.write(doc);
//			writer.close();
//			} catch (Exception ex) {
//			ex.printStackTrace();
//			}
//	}
//	
//	public void removeNodeAttribute(String xPathExpr, String aName){
//		removeNodeAttribute(filePath, xPathExpr, aName);
//	}
//	/**
//	 * 
//	 * @param filePath
//	 * @param xPathExpr xPath指向要删除节点的父节点
//	 * @param eName 要删除的节点名称
//	 */
//	public void removeNode(String filePath, String xPathExpr, String eName){
//		SAXReader saxReader = new SAXReader();
//		Document doc = null;
//		try {
//			doc =  saxReader.read(new File(filePath));
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // 读取xml文档
//		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
//		Iterator iter = list.iterator();
//		while (iter.hasNext()) {
//		Element element = (Element) iter.next();
//		
//		Iterator iterator = element.elementIterator(eName);
//		while (iterator.hasNext()) {
//			Element curElement = (Element) iterator.next();
//			element.remove(curElement);
//			}
//		}
//		
//		try {
//			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
//			writer.write(doc);
//			writer.close();
//			} catch (Exception ex) {
//			ex.printStackTrace();
//			}
//	}
//	
//	public void removeNode(String xPathExpr, String eName){
//		removeNode(filePath, xPathExpr, eName);
//	}
//	
	public boolean removeCurNode(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		Element parentNode = element.getParent();
		parentNode.remove(element);
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			return false;
			} finally{
				msd.clear();//清缓存
			}
		return true;
	}
	
	public boolean removeCurNode(String xPathExpr){
		return removeCurNode(filePath, xPathExpr);
	} 
	public String getNodeAttributeNameAndValueString(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		StringBuffer sb = new StringBuffer();
		try {
			if(msd.get(xPathExpr)==null){
			doc =  saxReader.read(new File(filePath));
			msd.put(xPathExpr, doc);
			}
			else doc =msd.get(xPathExpr);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element e = (Element) iter.next();
			
			List<Attribute> l =e.attributes();
			for(Attribute attribute:l){
				sb.append(attribute.getName());
				sb.append("::");
				sb.append(attribute.getValue());
				sb.append(",");
			}
			sb = new StringBuffer(sb.subSequence(0, sb.length()-1));
			sb.append(";");
		}
		if(sb.length()>0){
		return sb.substring(0, sb.length()-1);
		}
		else return "";
	}
	
	public String getNodeAttributeNameAndValueString(String xPathExpr){
		return getNodeAttributeNameAndValueString(filePath, xPathExpr);
	}
	 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XmlAccessor4Mapping xa = new XmlAccessor4Mapping();
		xa.setFilePath("books.xml");
		List<String> s = xa.getNodeAttributeValue("//inventory/book/address/parent::book/@year");
 
		XmlAccessor xaa = new XmlAccessor("WebContent/config/hbaseOracleMapping.xml");
//		//and的正确写法
//		String ls = xaa.getNodeAttributeNameAndValueString("//root/OracleTables/OracleTable[@name='CARTPORT' and @url='jdbc:oracle:thin:@localhost:1521:orcl' and @username='hadoop']");
		String ls = xaa.getNodeAttributeNameAndValueString("//root/HBaseTables/HBaseTable[@name='UUIDTest2']/RowkeyGroup/UUID");
		//	xaa.removeCurNode("//root/Mappings/tableMapping[@HBaseTableName='afb']");
		//		String ls = xaa.getNodeAttributeNameAndValueString(
//				"//HBaseTables/HBaseTable[@HBaseInstance='HBase01' and @name='aca']/indexSpatial");
		System.out.print(ls);
	}

}
