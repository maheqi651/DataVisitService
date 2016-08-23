package com.easymap.base.tool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ReadProperties {
	public static ResourceBundle bundle;

	public static final String get(String key) {
		return bundle.getString(key);
	}

	private String fileName = "/jdbc.xml";
	private String driver = "";
	private String url = "";
	private String username = "";
	private String password = "";
	private String themecode = "";
	private List<JDBCProperty> jdbcs;

	public ReadProperties() {
		InputStream in = ReadProperties.class.getResourceAsStream(fileName);
		jdbcs = new ArrayList<JDBCProperty>();
		SAXReader saxReader = new SAXReader();
		Document document;
		try {
			document = saxReader.read(in);
			Element root = document.getRootElement();
			Element mainConnect = root.element("MainConnect");
			this.driver = mainConnect.element("Driver").getTextTrim();
			this.url = mainConnect.element("URL").getTextTrim();
			this.username = mainConnect.element("UserName").getTextTrim();
			this.password = mainConnect.element("PassWord").getTextTrim();
			this.themecode = mainConnect.element("ThemeCode").getTextTrim();
			List<Element> connects = root.element("Connects").elements(
					"Connect");
			for (int i = 0; i < connects.size(); i++) {
				Element connect = connects.get(i);
				JDBCProperty jdbc = new JDBCProperty();
				jdbc.setDriver(connect.element("Driver").getTextTrim());
				jdbc.setPassword(connect.element("PassWord").getTextTrim());
				jdbc.setUrl(connect.element("URL").getTextTrim());
				jdbc.setUsername(connect.element("UserName").getTextTrim());
				jdbcs.add(jdbc);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public String getFileName() {
		return fileName;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public List<JDBCProperty> getJdbcs() {
		return jdbcs;
	}

	public String getThemecode() {
		return themecode;
	}

}