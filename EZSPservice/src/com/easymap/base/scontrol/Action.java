package com.easymap.base.scontrol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {
    /**  
     * �?��的具体Action实现这个接口  
     * @param request 请求对象  
     * @param response 应答对象  
     * @return ：结果页�? 
     */  
    public void execute(HttpServletRequest request, HttpServletResponse response);   
    

}