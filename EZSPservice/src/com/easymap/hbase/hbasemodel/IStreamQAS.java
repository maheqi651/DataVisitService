package com.easymap.hbase.hbasemodel;

import com.easymap.ezBigData.exceptions.EzBigDataConfigException;
import com.easymap.ezBigData.exceptions.QueryConditionsConstructException;
import com.easymap.ezMDAS.kvQuery.pojos.KvPage;

public interface IStreamQAS {
	public KvPage execute() throws EzBigDataConfigException,  QueryConditionsConstructException, Exception;
}
