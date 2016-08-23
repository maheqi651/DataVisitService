package com.easymap.memcached.hessian.servlet;

import com.easymap.base.queue.AddSub;
import com.easymap.base.queue.ResultSub;

public interface HessianHelloWord {
       public ResultSub get(String senderID,String ServiceID,String Id,String themeCode,String tableCode);
       public boolean set(AddSub addSub);
}
