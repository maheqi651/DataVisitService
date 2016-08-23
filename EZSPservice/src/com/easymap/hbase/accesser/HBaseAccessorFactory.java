package com.easymap.hbase.accesser;

import java.util.List;

import com.easymap.ezBigData.exceptions.EzBigDataConfigException;
import com.easymap.hbase.tool.XmlAccessor;
import com.easymap.hbase.util.Constants;



public class HBaseAccessorFactory extends HBaseAccessor{
	protected HBaseAccessorFactory(String quorum, String clientPort, String tableName) {
		super(quorum, clientPort, tableName);
		// TODO Auto-generated constructor stub
	}

	public static HBaseAccessor getHBaseAccessor(String hbaseInstName, String hbaseTableName) throws EzBigDataConfigException{
		XmlAccessor xa = new XmlAccessor(Constants.HBASE_INSTANCES_CONFIG_PATH);
		List<String> lsQuorum;
		List<String> lsClientPort;
		lsQuorum = xa.getNodeText("//HBaseInstances/HBase[@name='"+hbaseInstName+"']/QuorumIP");
		lsClientPort = xa.getNodeText("//HBaseInstances/HBase[@name='"+hbaseInstName+"']/QuorumPort");
		String quorum;
		String clientPort;
		if(lsQuorum.size()==0 || lsClientPort.size()==0){
			throw new EzBigDataConfigException("请检查配置文件hbaseInsts.xml中，是否配置了"+hbaseInstName+
					"实例，以及该实例中是否配置了QuorumIP或者QuorumPort。");
		}else{
		quorum =lsQuorum.get(0);
		clientPort =lsClientPort.get(0);
		return new HBaseAccessorFactory(quorum, clientPort, hbaseTableName);
		}
	}
	
	public static void main(String[] args){

	}
}
