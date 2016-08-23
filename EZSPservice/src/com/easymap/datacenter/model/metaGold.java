package com.easymap.datacenter.model;

import java.util.TreeSet;


public class metaGold{
	public String deCode= "";
	public String tag= "";
	public String dataType = "";
	public String format = "";
	public String num = "";
	public String chname = "";
	public String releation = "";
	public TreeSet<String> synonymset = new TreeSet<String>();
	// 加入处理后的
	public TreeSet<String> RefienSet = new TreeSet<String>();
	
	public float samevaule = 0;
	public String  xtype = "";// 更加format得到的类型
	
	public void metaGold(){
		  deCode= "";
		  tag= "";
		  dataType = "";
		  format = "";
		  num = "";
		  chname = "";
		  synonymset = new TreeSet<String>();
		  samevaule = 0;
	}

	
	public void addSynoymset(String asy){
		this.synonymset.add(asy);
	}
	
	
	public String GetNum(boolean ifdebug){
		String x = format;
	
		if(ifdebug)
			System.out.println(format);
		if(x==null || x.equals("")){
			num = "标准未规定";
			return "标准未规定";
		}
		x=x.replaceAll("，", ",");
		x = x.replaceAll("。",".");
		x = x.trim();
		
		// 字符处理
			if(x.startsWith("c")){
				if(x.contains(".."))
					num = x.split("\\.\\.")[1];
				else{
					num = x.split("c")[1];
				}
					
			}
			// 数字处理
			else if(x.startsWith("n")){
				if(x.contains("..")){
					num = x.split("\\.\\.")[1];
					if(x.contains(",")){
						num = num.split(",")[0];
					}
				}
				else{
					num = x.split("n")[1];
					if(x.contains(",")){
						num = num.split(",")[0];
				}
				}
			}
			// 日期处理
			else if(x.startsWith("d")){
				if(x.contains("d8"))
					num = "8";
				else if(x.contains("d14"))
					num = "14";
			}
			else{
				num = "未能解析标长度设置："+this.format;
			}
			
			return num;
	}
	
	// 得到数据元的类型，根据format来确定的
	public void GetXtype(){
		
		if(this.format==null||this.format.equals("")||this.num==null
				||this.num.equals("")||this.num.startsWith("未能解析")
				||this.num.startsWith("标准未")){
			this.xtype = "";
		}
		else    if(this.format.startsWith("c")){
			xtype =CheckHelper.String_DT;//"字符型"
		}
		else 	if(this.format.startsWith("n")){
			xtype =CheckHelper.Value_DT;//"数值型"
		}
		else 	if(this.format.startsWith("bn")){
			xtype =CheckHelper.Bianry_DT;// "二进制型"
		} 
		else 	if(this.format.startsWith("d")){
			xtype ="日期";
		}
		else 	if(this.format.startsWith("bl")){
			xtype ="布尔型";
		}
	}
	/*
	 * 用我（一个标准的数据元），来检查进来的这个元数据数据aMD，并且把aMD给填充了
	 */
	public boolean RuleMatch(metaData aMD){
		
		boolean ifallmatch = true;
		/// 默认都可以
		
//		
//		if(this.num.equals(""))
//			this.GetNum();
	
		// 读ameta，看怎么判断
		aMD.Code.fixed = ""+this.deCode+"("+this.chname+")";

		// 201510115 fix
		// 如果在数据元里面，format字段没找到，默认不检查
		if(this.format==null||this.format.equals("")||this.num==null
				||this.num.equals("")||this.num.startsWith("未能解析")
				||this.num.startsWith("标准未")){
			aMD.MaxSize.ifMatch = true;
			aMD.DataType.ifMatch = true;
			return true;
		}
		
		
		// 第一步检查类型

				
		if(aMD.DataType.source.contains(xtype)){
			aMD.DataType.ifMatch = true;
		}
		
		// 特殊处理 c1也可以是boolean
		else 	if(this.format.equals("c1")){
			if(aMD.DataType.source.equals("布尔型"))
				aMD.DataType.ifMatch = true;// 这样也算对
		}
		
		else{
			aMD.DataType.fixed= "应该为:"+this.dataType;
			aMD.DataType.ifMatch = false;
			ifallmatch = false;
		}
		
		
	
		
	
		//第二步，检查长度
		
		// 特殊处理一下
		
		if(this.format.equals("bn")){
			// 二进制
			aMD.MaxSize.ifMatch = true;
		}
		// 特殊处理长度
		// 日期的也不用管
		else  if(this.xtype.contains("日期"))
		{
			aMD.MaxSize.ifMatch = true;
		}
		
		else  if(this.xtype.contains(CheckHelper.Bianry_DT))
		{
			aMD.MaxSize.ifMatch = true;
		}
		
		else
		if(aMD.MaxSize.source==null||!aMD.MaxSize.source.equals(this.num)){
			aMD.MaxSize.fixed = "应该为:"+this.num;
			aMD.MaxSize.ifMatch = false;
			return false;
		}
		
		
				aMD.MaxSize.ifMatch= true;
				
				return ifallmatch;				
	}

	// 添加同义词 151027
	
	public void calSameDegree(String des,boolean ifRefine){
		
		// 对传入的字串进行处理,如果需要增加这个功能的话
		if(ifRefine)
		{
			for(String ry:CheckHelper.getInstance().ruleKeyWords){
				des=des.replaceAll(ry, "");
			}
			if(des.equals("")){
				this.samevaule = 0.0f;
				return ;
			}
		}
		// 计算，找到重合度最高的一个
		this.samevaule = 0;
		float temp = 0.0f;
		//((float)this.comSize(this.chname, des) / this.chname.length());
		TreeSet<String> asySet =null;
		if(ifRefine)
			asySet = this.RefienSet;
		else
			asySet = this.synonymset;
		
	
		for(String x:asySet)	
		{
			temp =  ((float)this.comSize(x, des) / x.length()) ;
			if(x.length()<4){
				// 目标匹配值太小，比如只有两三个字
				if(temp<0.6)
					temp =  ( temp * 0.1f);// 对其进行衰减
			}
			if( temp > this.samevaule){
				this.samevaule = temp;
			}
			
			// 增加新的规则,如果是以数据元结尾的话
			if(des.endsWith(x))
				this.samevaule = 1;// 如果包含了
		}
	}
	
	// 计算共现的字的个数
	public  int comSize(String a,String b){
		int sum = 0;
		for(int i=0;i<a.length();i++){
			
			if(b.indexOf(a.charAt(i))!=-1)
				sum++;
		}
		return sum;
	}
	
	
	public void refine(TreeSet<String> ruleKeyWords) {
		// TODO Auto-generated method stub
		
		/** * @author  Zhangxt
			* @date 创建时间：2015年10月30日 上午10:40:45 
			* @version 1.0 
			* @parameter  
			* @since 
		 	* @return 
		 */
		for(String x:this.synonymset){
			for(String ry:ruleKeyWords){
				x=x.replaceAll(ry, "");
			}
			if(!x.equals(""))
				this.RefienSet.add(x);
		}
	}


	public void addchname() {
		// TODO Auto-generated method stub
		
		/** * @author  Zhangxt
			* @date 创建时间：2015年10月30日 上午11:36:39 
			* @version 1.0 
			* @parameter  
			* @since 
		 	* @return 
		 */
		this.synonymset.add(this.chname);
	}
	

}