package com.easymap.base.tool;

import java.io.File;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class readXml {
	
	public static Document getreadXml(String p){
		File f = new File(p);
		SAXReader saxReader = new SAXReader();
		org.dom4j.Document document = null;
		 try {
			document = saxReader.read(f);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}
}
