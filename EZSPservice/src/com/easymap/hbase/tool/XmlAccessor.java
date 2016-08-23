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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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



public class XmlAccessor {
	String filePath;
	public String getFilePath() {
		return filePath;
	}

	OutputFormat of= OutputFormat.createPrettyPrint();
	public XmlAccessor(){
		
	}
	public XmlAccessor(String filePath){
		
		this.filePath = filePath;
	}
	/**
	 * 获取Node属性aa
	 * @param filePath
	 * @param xPathExpr
	 * @return
	 */
	public List<String> getNodeAttributeValue(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		List<String> result = new ArrayList<String>();
		try {
			doc =  saxReader.read(new File(filePath));
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
			doc =  saxReader.read(new File(filePath));
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
			doc =  saxReader.read(new File(filePath));
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
	
	/**
	 * 
	 * @param filePath
	 * @param xPathExpr
	 * @param value
	 */
	public void setNodeText(String filePath, String xPathExpr, String value){
		//也可用于新增节点
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		element.setText(value);
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			}
	}
	
	public void setNodeText(String xPathExpr, String value){
		setNodeText(filePath, xPathExpr, value);
	}
	
	/**
	 * 删除属性，用法特殊，xPath到其tagName而不是具体的属性名
	 * xa.removeNodeAttribute("books.xml", "//book", "year"); 
	 * @param filePath
	 * @param xPathExpr
	 * @param aName
	 */
	public void removeNodeAttribute(String filePath, String xPathExpr, String aName){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		element.remove(element.attribute(aName));
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			}
	}
	
	public void removeNodeAttribute(String xPathExpr, String aName){
		removeNodeAttribute(filePath, xPathExpr, aName);
	}
	/**
	 * 
	 * @param filePath
	 * @param xPathExpr xPath指向要删除节点的父节点
	 * @param eName 要删除的节点名称
	 */
	public void removeNode(String filePath, String xPathExpr, String eName){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		
		Iterator iterator = element.elementIterator(eName);
		while (iterator.hasNext()) {
			Element curElement = (Element) iterator.next();
			element.remove(curElement);
			}
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			}
	}
	
	public void removeNode(String xPathExpr, String eName){
		removeNode(filePath, xPathExpr, eName);
	}
	
	public void removeCurNode(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			}
	}
	
	public void removeCurNode(String xPathExpr){
		removeCurNode(filePath, xPathExpr);
	}
	/**
	 * 
	 * @param filePath
	 * @param xPathExpr xPath指向要添加属性的tag
	 * @param aName
	 * @param aValue
	 */
	public void addNodeAttribute(String filePath, String xPathExpr, String aName, String aValue){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		element.addAttribute(aName, aValue);
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			}
	}
	
	public void addNodeAttribute(String xPathExpr, String aName, String aValue){
		addNodeAttribute(filePath, xPathExpr, aName, aValue);
	}
	/**
	 * 
	 * @param filePath
	 * @param xPathExpr xPath指向要添加tag的tag
	 * @param eValue
	 */
	public void addNode(String filePath, String xPathExpr, String eName){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		element.addElement(eName);
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			}
	}
	
	public void addNode(String xPathExpr, String eName){
		addNode(filePath, xPathExpr, eName);
	}
	/**
	 * 
	 * @param filePath
	 * @param xPathExpr 要添加的位置
	 * @param eName 要添加的tagName
	 * @param eValue 要添加的tagValue
	 */
	public void addNodeAndText(String filePath, String xPathExpr, String eName, String eValue){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		Element subElement = element.addElement(eName);
		subElement.setText(eValue);
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			}
	}
	
	public void addNodeAndText(String xPathExpr, String eName, String eValue){
		addNodeAndText(filePath, xPathExpr, eName, eValue);
	}
	
/**
 * 添加节点和节点属性
 * @param filePath
 * @param xPathExpr
 * @param eName
 * @param attrs 节点属性集合
 */
	public void addNodeAndAttribute(String filePath, String xPathExpr, String eName, Map<String,String> attrs){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);// 用xpath查找节点的属性
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Element element = (Element) iter.next();
		Element subElement = element.addElement(eName);
			for (Entry<String,String> ess: attrs.entrySet()){
				
				
				subElement.addAttribute(ess.getKey(), ess.getValue());
			}
		
		}
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filePath)), of);
			writer.write(doc);
			writer.close();
			} catch (Exception ex) {
			ex.printStackTrace();
			}
	}
	
	public void addNodeAndAttribute(String xPathExpr, String eName, Map<String,String> attrs){
		addNodeAndAttribute(filePath, xPathExpr, eName, attrs);
	}
	
	/**
	 * 获取Node属性名和属性值的String,xPath位置指定到标签，不使用@标识属性，
	 * 双冒号分隔Name与Attribute，逗号分隔不同属性，分号分隔不同节点
	 * @param filePath
	 * @param xPathExpr
	 * @return
	 */
	public String getNodeAttributeNameAndValueString(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		StringBuffer sb = new StringBuffer();
		try {
			doc =  saxReader.read(new File(filePath));
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
	 * 获取Node属性名和属性值的String,xPath位置指定到标签，不使用@标识属性，
	 * 双冒号分隔Name与Attribute，逗号分隔不同属性，分号分隔不同节点
	 * @param filePath
	 * @param xPathExpr
	 * @return
	 */
	public JSONArray getJSONNodeAttributeNameAndValue(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
//		StringBuffer sb = new StringBuffer();
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);
		Iterator iter = list.iterator();
		JSONArray ja = new JSONArray();
		while (iter.hasNext()) {
			Element e = (Element) iter.next();
			
			List<Attribute> l =e.attributes();
			JSONObject jo = new JSONObject();
			for(Attribute attribute:l){
				jo.put(attribute.getName(), attribute.getValue());
//				sb.append(attribute.getName());
//				sb.append("::");
//				sb.append(attribute.getValue());
//				sb.append(",");
			}
			ja.add(jo);
//			sb = new StringBuffer(sb.subSequence(0, sb.length()-1));
//			sb.append(";");
		}

	 return ja;
	}
	
	public JSONArray getJSONNodeAttributeNameAndValue(String xPathExpr){
		return getJSONNodeAttributeNameAndValue(filePath, xPathExpr);
	}
	
	/**
	 * 获取Node属性名的Set
	 * @param filePath
	 * @param xPathExpr
	 * @return
	 */
	public Set<String> getNodeAttributeName(String filePath, String xPathExpr){
		SAXReader saxReader = new SAXReader();
		Document doc = null;
		Set<String> result = new HashSet<String>();
		try {
			doc =  saxReader.read(new File(filePath));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取xml文档
		List list = doc.selectNodes(xPathExpr);
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		Attribute attribute = (Attribute) iter.next();
		result.add(attribute.getName());
		}
		return result;
	}
	
	public Set<String> getNodeAttributeName(String xPathExpr){
		return getNodeAttributeName(filePath, xPathExpr);
	}

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XmlAccessor xa = new XmlAccessor("book.xml");
//		List<String> s = xa.getNodeAttributeValue("//inventory/book/address/parent::book/@year");
//		List<String> s = xa.getNodeAttributeValue
//		("//root/OracleTables/OracleTable[@name='"
//		+ "CARTPORT1"
//		+ "' and @url='"
//		+ "jdbc:oracle:thin:@172.18.70.128:1521:orcl"
//		+ "' and @username='hcdata1']/attribute::timeColumn");
//		System.out.println(s.get(0));
		System.out.print(xa.getNodeText("books.xml", "//book[@year='2000']/title1"));
//		System.out.print(xa.getNodeAttributeValue("books.xml", "//book/@*"));
//		xa.setNodeAttribute("books.xml", "//book[@year='2001']/@year", "2000");
//		xa.setNodeText("books.xml", "//book/price", "xxxxx");
//		xa.removeNodeAttribute("books.xml", "//book", "year");
//		xa.removeNode("books.xml", "//book", "isbn");
//		xa.addNodeAttribute("books.xml", "//book/author", "age", "65");
//		xa.addNode("books.xml", "//book","age" );
//		xa.addNodeAndText("books.xml", "//book","age","65" );
//		xa.addNodeAndText("books.xml", "//book","age","66" );
//		Map<String, String> m = new HashMap<String, String>();
//		m.put("country", "UK");
//		m.put("city","Manchester");
//		xa.addNodeAndAttribute("books.xml", "//book", "address", m);
//		XmlAccessor xaa = new XmlAccessor("WebContent/config/hbaseOracleMapping.xml");
//		//and的正确写法
//		String ls = xaa.getNodeAttributeNameAndValueString("//root/OracleTables/OracleTable[@name='CARTPORT' and @url='jdbc:oracle:thin:@localhost:1521:orcl' and @username='hadoop']");
//	xaa.removeCurNode("//root/Mappings/tableMapping[@HBaseTableName='afb']");
		//		String ls = xaa.getNodeAttributeNameAndValueString(
//				"//HBaseTables/HBaseTable[@HBaseInstance='HBase01' and @name='aca']/indexSpatial");
//		System.out.print(ls);
	}

}
