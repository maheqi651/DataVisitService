package com.easymap.datacenter.util;

import java.util.Locale;
import java.util.ResourceBundle;

public interface Constant {
	static final String DB_driver = ResourceBundle.getBundle("parameters").getString("driver");
	static final String DB_url = ResourceBundle.getBundle("parameters").getString("url");
	static final String DB_dbuser = ResourceBundle.getBundle("parameters").getString("dbuser");
	static final String DB_dbpwd = ResourceBundle.getBundle("parameters").getString("dbpwd");
	
	static final String EZSPATIAL_dbuser = ResourceBundle.getBundle("parameters").getString("dbuser");
	static final String EZSPATIAL_pwd = ResourceBundle.getBundle("parameters").getString("dbpwd");
	static final String DE_SYSTEM_NAME = ResourceBundle.getBundle("parameters").getString("DESystemName");
	static final String EZ_MANAGER_LOCATION = ResourceBundle.getBundle("parameters").getString("ezManagerLocation");

	//系统业务数据库连接
	static final String DE_USER = ResourceBundle.getBundle("parameters").getString("de_user");
	static final String DE_PASSW0RD = ResourceBundle.getBundle("parameters").getString("de_pwd");
	static final String DE_URL = ResourceBundle.getBundle("parameters").getString("de_url");
	static final String DE_DRIVER = ResourceBundle.getBundle("parameters").getString("de_driver");
	
	//系统可使用的时间
	static final String SYS_USER_TIME = ResourceBundle.getBundle("parameters").getString("SysUseTime");
	static final String FIRST_USE_TIME = ResourceBundle.getBundle("parameters").getString("firstUseTime");
	
	//网关配置
	static final String AUTHURL = ResourceBundle.getBundle("parameters").getString("authURL");
	static final String APPID = ResourceBundle.getBundle("parameters").getString("appId");
	static final String PKIApp = ResourceBundle.getBundle("parameters").getString("PKIApp");
	
	//运维库
	static final String EZ_URL = ResourceBundle.getBundle("parameters").getString("ez_url");
	static final String EZ_DRIVER = ResourceBundle.getBundle("parameters").getString("ez_driver");
	static final String EZ_USER = ResourceBundle.getBundle("parameters").getString("ez_user");
	static final String EZ_PWD = ResourceBundle.getBundle("parameters").getString("ez_pwd");
	
	//关键字
	static final String RuleKeyWords = ResourceBundle.getBundle("parameters").getString("RuleKeyWords");
	
	//表用户
	static final String USEROFTABLE = ResourceBundle.getBundle("parameters").getString("userOfTable");

	
}
	

