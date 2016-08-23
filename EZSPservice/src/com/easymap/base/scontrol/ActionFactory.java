package com.easymap.base.scontrol;

public class ActionFactory {   
  
    //单例模式：不�?��创建对象   
    private ActionFactory(){   
   }   
    //单实例访问方法，得到ActionFactory对象   
    public static ActionFactory getActionFactory(){   
        if(af == null){   
            af = new ActionFactory();   
        }   
        return af;   
    }   
    /**  
     * 根据具体的Action类名字创建Action对象  
     * @param ActionClassName ：具体的Action类全�? 
     * @return：Action类型对象  
     */  
    public Action getAction(String ActionClassName){   
        Action action = null;   
        try{   
           action = (Action) Class.forName(ActionClassName).newInstance();   
        }catch(Exception e){   
            e.printStackTrace();   
        }   
        return action;   
    }          
   private static ActionFactory af;   
             
}  