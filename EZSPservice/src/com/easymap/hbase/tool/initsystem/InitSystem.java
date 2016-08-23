package com.easymap.hbase.tool.initsystem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;












import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.easymap.hbase.util.Constants;



public class InitSystem implements ServletContextListener {
	public static boolean isWSParameterInitialized=false;//用于WebService模式判断
	//com.easymap.ezMDAS.traceQuery.constants.Constants是否获得部署地址和IP
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			String configPath= sce.getServletContext().getInitParameter("configPath");
	 
			Constants.PATH=sce.getServletContext().getRealPath(configPath);
			System.out.println("polluxpath====="+Constants.PATH);
			Constants.HBASE_INSTANCES_CONFIG_PATH = Constants.PATH+"/hbaseInsts.xml";
			//Constants.HBASE_ORACLE_MAPPING_PATH = Constants.PATH+"/hbaseOracleMapping.xml";
			//XmlAccessor4Mapping.getInstance().setFilePath(Constants.HBASE_ORACLE_MAPPING_PATH);
			PropertyConfigurator.configure(Constants.PATH+"/log4j.properties");
			Constants.LOGGER.addAppender(new FileAppender(Constants.PATTERN_LAYOUT,Constants.PATH+"/../logs/EzBigData.log"));
			Constants.LOGGER.setLevel(Constants.RUNNING_LOG_LEVEL);
			Constants.LOGGER.info("日志系统初始化完毕");
			Constants.LOGGER.info("实例配置路径为"+Constants.HBASE_INSTANCES_CONFIG_PATH);
			
			//检查配置文件表是否存在，不存在则创建
		/*	HBaseAccessor ha = HBaseAccessorFactory.getHBaseAccessor(Constants.DEFAULT_HBASE_INSTANCE);
			if(!ha.checkTableExist(Constants.MAPPING_META_DATA_TABLE)){
				List<String> ltemp = new ArrayList<String>();
				ltemp.add(Constants.MAPPING_COLUMN_DATA_FAMILY);
				ltemp.add(Constants.MAPPING_META_DATA_FAMILY);
				ha.createTableWithColumnFamily(
						Constants.MAPPING_META_DATA_TABLE, ltemp);
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//			com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL=
//					sce.getServletContext().;

//		com.easymap.ezMDAS.traceQuery.constants.Constants.HBASE_INSTANCES_CONFIG_PATH
//			=com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL+"config/hbaseInsts.xml";
//		com.easymap.ezMDAS.traceQuery.constants.Constants.HBASE_ORACLE_MAPPING_PATH
//			=com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL+"config/hbaseOracleMapping.xml";
//					
//		System.out.println("polluxpath2====="+com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL);
//		System.out.println("polluxpath3====="+com.easymap.ezMDAS.traceQuery.constants.Constants.HBASE_INSTANCES_CONFIG_PATH);
	}
	
	public static boolean isWSParameterInitialized()
	{
		return isWSParameterInitialized;
	}
	
	 public static void InitializeWSParameters(String URLString){//在Filter中使用？
		Constants.EZDBAAS_URL=URLString;
		com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL =URLString;
		com.easymap.ezMDAS.traceQuery.constants.Constants.HBASE_INSTANCES_CONFIG_PATH = com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL
				+ "config/hbaseInsts.xml";
		com.easymap.ezMDAS.traceQuery.constants.Constants.HBASE_ORACLE_MAPPING_PATH = com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL
				+ "config/hbaseOracleMapping.xml";
		com.easymap.ezMDAS.kvQuery.constants.Constants.EZDBAAS_URL =URLString;
		com.easymap.ezMDAS.kvQuery.constants.Constants.HBASE_INSTANCES_CONFIG_PATH = com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL
				+ "config/hbaseInsts.xml";
		com.easymap.ezMDAS.kvQuery.constants.Constants.HBASE_ORACLE_MAPPING_PATH = com.easymap.ezMDAS.traceQuery.constants.Constants.EZDBAAS_URL
				+ "config/hbaseOracleMapping.xml";
		isWSParameterInitialized=true;
	} 
	
	public static void InitializeWSParameters(String protocal, String ipOrHost,
			int port, String projectName)
	{
	     InitializeWSParameters(protocal+ "://" + ipOrHost + ":" + port + "/" + projectName + "/");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 }

}
