package com.easymap.memcached.hessian.servlet;

import com.caucho.hessian.server.HessianServlet;
import com.easymap.base.queue.AddSub;
import com.easymap.base.queue.ResultSub;

public class HessianHelloWordImpl extends HessianServlet implements
		HessianHelloWord {

	@Override
	public ResultSub get(String senderID, String ServiceID, String Id,
			String themeCode, String tableCode) {
		return ProxyHessianFactory.getInstance().get(senderID, ServiceID, Id, themeCode, tableCode);
	}

	@Override
	public boolean set(AddSub addSub) {
		if(addSub!=null)
		    return ProxyHessianFactory.getInstance().set(addSub);
		else
			return false;
	}
}
