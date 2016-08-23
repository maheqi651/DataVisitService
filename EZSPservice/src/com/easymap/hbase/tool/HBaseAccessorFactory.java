package com.easymap.hbase.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.easymap.ezBigData.exceptions.EzBigDataConfigException;
import com.easymap.hbase.util.Constants;


public class HBaseAccessorFactory extends HBaseAccessor{
	private static Map<String, HBaseAccessor> mshaf = 
			new HashMap<String, HBaseAccessor>();
	protected HBaseAccessorFactory(String quorum, String clientPort, String defaultFSName) {
		super(quorum, clientPort, defaultFSName);
		// TODO Auto-generated constructor stub
	}

	public static HBaseAccessor getHBaseAccessor(String hbaseInstName) throws EzBigDataConfigException{
		if(mshaf.get(hbaseInstName)!=null){
			return mshaf.get(hbaseInstName);
		}
		else{
		XmlAccessor xa = new XmlAccessor(Constants.HBASE_INSTANCES_CONFIG_PATH);
		String quorum;
		String clientPort;
		String defaultFSName="";
		List<String> lsQuorum;
		List<String> lsClientPort;
		List<String> lsDFSName;
		lsQuorum = xa.getNodeText("//HBaseInstances/HBase[@name='"+hbaseInstName+"']/QuorumIP");
		lsClientPort = xa.getNodeText("//HBaseInstances/HBase[@name='"+hbaseInstName+"']/QuorumPort");
		lsDFSName = xa.getNodeText("//HBaseInstances/HBase[@name='"+hbaseInstName+"']/defaultFSName");
		if(lsQuorum.size()==0 || lsClientPort.size()==0){
			throw new EzBigDataConfigException("请检查配置文件hbaseInsts.xml中，是否配置了"+hbaseInstName+
					"实例，以及该实例中是否配置了QuorumIP或者QuorumPort。");
		}
		quorum = lsQuorum.get(0);
		clientPort =lsClientPort.get(0);
		if(lsDFSName.size()>0){
			defaultFSName = lsDFSName.get(0);
		}
		
		HBaseAccessor hba = new HBaseAccessorFactory(quorum, clientPort, defaultFSName);
		mshaf.put(hbaseInstName, hba);
		return hba;
		}
	}
	
	public static void main(String[] args){
		Constants.HBASE_INSTANCES_CONFIG_PATH="D:/workspaces/workspace2013_V1.1/.metadata/.plugins/org.eclipse.wst.server.core/tmp1/wtpwebapps/EzDBaaSManager/config/hbaseInsts.xml";
		HBaseAccessor hba;
		try {
			hba = HBaseAccessorFactory.getHBaseAccessor("HBase03");
			HBaseAccessor hba2 = HBaseAccessorFactory.getHBaseAccessor("HBase03");
			HBaseAccessor hba3 = HBaseAccessorFactory.getHBaseAccessor("HBase01");
			System.out.println(hba==hba3);
		} catch (EzBigDataConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

	}
}
