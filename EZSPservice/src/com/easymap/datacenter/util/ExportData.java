package com.easymap.datacenter.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 
 * @ClassName   ExportData
 * @Description TODO
 * @author qs
 * @date 2015-10-14
 */
public class ExportData {
	
	/**
	 * @throws IOException 
	 * 
	 * @Title: main 
	 * @Description: 测试类
	 * @param @param args     
	 * @return void     
	 * @throws
	 */
	public static void main(String[] args) throws IOException {
		/*String DEMatchResultFilePath = "public/res/Result";
		exportExcel(DEMatchResultFilePath);*/
		File file = new File("D:/a.txt");
		if(file.exists()){
			file.delete();
			
		}
		file.createNewFile();
	}
	
	public static String DEMatchResultFilePath = "public/res/excel/";
	
	public static File exportExcel(String fileName) {
		
		WritableWorkbook wwb = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
			String currentDateStr = format.format(new Date());
			//fos = new FileOutputStream(fileName + "-" + currentDateStr + ".xls");
			file = new File(DEMatchResultFilePath + fileName + "-" + currentDateStr + ".xls");
			wwb = Workbook.createWorkbook(file);
			WritableSheet ws = wwb.createSheet("数据审核结果", 0);// 创建一个工作表
			
			// 设置单元格的文字格式
			WritableFont wf = new WritableFont(WritableFont.ARIAL,12,WritableFont.NO_BOLD,false,
                    UnderlineStyle.NO_UNDERLINE,Colour.BLUE);
            WritableCellFormat wcf = new WritableCellFormat(wf);
            //wcf.setBackground(Colour.BLUE); //设置背景颜色
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); 
            wcf.setAlignment(Alignment.CENTRE); 
            ws.setRowView(1, 500);
            
                      
            //填充数据的内容
            List<Person> dataList = new ArrayList<Person>();
            Person p = new Person();
            p.setAge(10);
            p.setName("zhangsan");
            
            Person p1 = new Person();
            p1.setAge(20);
            p1.setName("lisi");
            
            Person p2 = new Person();
            p2.setAge(30);
            p2.setName("wangwu");
            dataList.add(p);
            dataList.add(p1);
            dataList.add(p2);
           
            for (int i = 0; i < dataList.size(); i++) {
            	Person data= (Person)dataList.get(i);
                ws.addCell(new Label(1, i + 1, Integer.toString(data.getAge()), wcf));
                ws.addCell(new Label(2, i + 1, data.getName(), wcf));
                if(i == 0) {
                	wcf = new WritableCellFormat();
                }
            }
            
			wwb.write();
            wwb.close();
            
            
            
            //renderBinary(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e){
			e.printStackTrace();
        } catch (WriteException e){
        	e.printStackTrace();
        }
		
		return file;
	}
	/**
	 * 将数据导出到EXCEL
	 * @param fileName 导出的文件名
	 * @param titles Excel的表头
	 * @param keys   数据的key（与表头的顺序一致），用于从InfoList中取数据
	 * @param InfoList 数据
	 * @return
	 */
	public static File exportExcel(String fileName, List<String> titles, List<String> keys, List<Map<String, String>> InfoList) {
		
		WritableWorkbook wwb = null;
		//FileOutputStream fos = null;
		File file = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
			String currentDateStr = format.format(new Date());
			file = new File(DEMatchResultFilePath + fileName + "-" + currentDateStr + ".xls");
			if(file.exists()){
				file.delete();
				file.createNewFile();
			}		
			wwb = Workbook.createWorkbook(file);
			WritableSheet ws = wwb.createSheet("数据审核结果", 0);// 创建一个工作表
			
			// 设置正文单元格的文字格式
			WritableFont wf = new WritableFont(WritableFont.ARIAL,12,WritableFont.NO_BOLD,false,
					UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat wcf = new WritableCellFormat(wf);
			//wcf.setBackground(Colour.YELLOW); //设置背景颜色
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE); 
			wcf.setAlignment(Alignment.LEFT); 
			wcf.setWrap(true);
			ws.setRowView(0, 500);
			wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			// 设置表头文字格式
            WritableFont wftitle = new WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,
            		UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
            WritableCellFormat wcftitle = new WritableCellFormat(wftitle);
            wcftitle.setBackground(Colour.PALE_BLUE   ); //设置背景颜色ICE_BLUE
            wcftitle.setVerticalAlignment(VerticalAlignment.CENTRE); 
            wcftitle.setAlignment(Alignment.LEFT); 
            wcftitle.setBorder(Border.ALL, BorderLineStyle.THIN);
            //ws.setRowView(1, 500);
                        
            int[] columnsWidth = new int[titles.size()];
            int maxColLength = 300;
            
			for(int j=0; j<titles.size();j++){
				ws.addCell(new Label(j, 0, titles.get(j), wcftitle));
				columnsWidth[j] = titles.get(j).length()*3;
            }
			
			int i=1;
			for(int m=0; m<InfoList.size(); m++ ){
				Map<String, String> itemMap = InfoList.get(m);				
				for(int j=0; j<keys.size();j++){
					String value = itemMap.get(keys.get(j));
					if(value == null || "null".equalsIgnoreCase(value)){
						value = "";
					}
					ws.addCell(new Label(j, i, value, wcf));
					if(value.length()*2 > columnsWidth[j]){
						columnsWidth[j] = value.length()*2;
					}
					if(columnsWidth[j] > maxColLength ){
						columnsWidth[j] = maxColLength;
					}
					//ws.setColumnView(j, cellView);
				}
				i++;
			}
			
			
			for(int j=0; j<titles.size();j++){
				ws.setColumnView(j, columnsWidth[j]);
			}
			
			wwb.write();
			wwb.close();
			
			//renderBinary(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e){
			e.printStackTrace();
		} catch (WriteException e){
			e.printStackTrace();
		}
		return file;
	}
	/**
	 * 将数据导出到EXCEL(导出时，在不同的数据类别前面均插入表头)
	 * @param fileName 导出的文件名
	 * @param titles Excel的表头
	 * @param keys   数据的key（与表头的顺序一致），用于从InfoList中取数据， key的第一个数据用于区分不同的数据类别
	 * @param InfoList 数据
	 * @return
	 */
	public static File exportExcelOnEachTitle(String fileName, List<String> titles, List<String> keys, List<Map<String, String>> InfoList) {
		
		WritableWorkbook wwb = null;
		//FileOutputStream fos = null;
		File file = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
			String currentDateStr = format.format(new Date());
			file = new File(DEMatchResultFilePath + fileName + "-" + currentDateStr + ".xls");
			if(file.exists()){
				file.delete();
				file.createNewFile();
			}		
			wwb = Workbook.createWorkbook(file);
			WritableSheet ws = wwb.createSheet("数据审核结果", 0);// 创建一个工作表
			
			// 设置正文单元格的文字格式
			WritableFont wf = new WritableFont(WritableFont.ARIAL,12,WritableFont.NO_BOLD,false,
					UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat wcf = new WritableCellFormat(wf);
			//wcf.setBackground(Colour.YELLOW); //设置背景颜色
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE); 
			wcf.setAlignment(Alignment.LEFT); 
			wcf.setWrap(true);
			ws.setRowView(0, 500);
			wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			// 设置表头文字格式
			WritableFont wftitle = new WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,
					UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat wcftitle = new WritableCellFormat(wftitle);
			wcftitle.setBackground(Colour.PALE_BLUE   ); //设置背景颜色ICE_BLUE
			wcftitle.setVerticalAlignment(VerticalAlignment.CENTRE); 
			wcftitle.setAlignment(Alignment.LEFT); 
			wcftitle.setBorder(Border.ALL, BorderLineStyle.THIN);
			//ws.setRowView(1, 500);
			
			int[] columnsWidth = new int[titles.size()];
			int maxColLength = 300;//列最宽设置
			
			//写表头
			/*for(int j=0; j<titles.size();j++){
				ws.addCell(new Label(j, 0, titles.get(j), wcftitle));
				columnsWidth[j] = titles.get(j).length()*3;
			}*/
			
			String dataCategory = "";//在不同的数据类别前 都要添加表头
			int i=0;
			for(int m=0; m<InfoList.size(); m++ ){
				Map<String, String> itemMap = InfoList.get(m);	
				//key的第一个数据用于区分不同的数据类别
				String dataCategoryNew = itemMap.get(keys.get(0));
				//如果有新的数据类别，则插入表头
				if(!dataCategory.equals(dataCategoryNew)){					
					//写表头
					for(int j=0; j<titles.size();j++){
						String value = titles.get(j);
						ws.addCell(new Label(j, i, value, wcftitle));
						if(value.length()*3 > columnsWidth[j]){//表头的字体较大，所以*3
							columnsWidth[j] = value.length()*3;//统一按照汉字处理，一个汉字占2字符
						}
					}
					dataCategory = dataCategoryNew;
					i++;
				}
				//写数据
				for(int j=0; j<keys.size();j++){
					String value = itemMap.get(keys.get(j));
					if(value == null || "null".equalsIgnoreCase(value)){
						value = "";
					}
					ws.addCell(new Label(j, i, value, wcf));
					if(value.length()*2 > columnsWidth[j]){
						columnsWidth[j] = value.length()*2;//统一按照汉字处理，一个汉字占2字符
					}
					if(columnsWidth[j] > maxColLength ){
						columnsWidth[j] = maxColLength;
					}
					//ws.setColumnView(j, cellView);
				}
				i++;
			}
			
			
			for(int j=0; j<titles.size();j++){
				ws.setColumnView(j, columnsWidth[j]);
			}
			
			wwb.write();
			wwb.close();
			
			//renderBinary(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e){
			e.printStackTrace();
		} catch (WriteException e){
			e.printStackTrace();
		}
		return file;
	}
	
	
	/**
	 * 将数据导出到EXCEL(导出时，在不同的数据类别前面均插入表头)
	 * @param fileName 导出的文件名
	 * @param titles Excel的表头
	 * @param keys   数据的key（与表头的顺序一致），用于从InfoList中取数据， key的第一个数据用于区分不同的数据类别
	 * @param InfoList 数据
	 * @return
	 */
	public static File exportExcelOnEachTitleNew(String pathurl,String fileName, List<String> titles, List<String> keys, List<Map<String, String>> InfoList) {
		
		WritableWorkbook wwb = null;
		File file = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
			String currentDateStr = format.format(new Date());
			//String path1 = pathurl;
			//file = new File(DEMatchResultFilePath + fileName + "-" + currentDateStr + ".xls");
			file = new File(pathurl + "/" + fileName + "-" + currentDateStr + ".xls");
			
			if(file.exists()){
				file.delete();
				file.createNewFile();
			}		
			wwb = Workbook.createWorkbook(file);
			WritableSheet ws = wwb.createSheet("数据元检测结果", 0);// 创建一个工作表
			
			// 设置正文单元格的文字格式
			WritableFont wf = new WritableFont(WritableFont.ARIAL,12,WritableFont.NO_BOLD,false,
					UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat wcf = new WritableCellFormat(wf);
			//wcf.setBackground(Colour.YELLOW); //设置背景颜色
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE); 
			wcf.setAlignment(Alignment.LEFT); 
			wcf.setWrap(true);
			ws.setRowView(0, 500);
			wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			// 设置表头文字格式
			WritableFont wftitle = new WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,
					UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat wcftitle = new WritableCellFormat(wftitle);
			wcftitle.setBackground(Colour.PALE_BLUE   ); //设置背景颜色ICE_BLUE
			wcftitle.setVerticalAlignment(VerticalAlignment.CENTRE); 
			wcftitle.setAlignment(Alignment.LEFT); 
			wcftitle.setBorder(Border.ALL, BorderLineStyle.THIN);
			//ws.setRowView(1, 500);
			
			int[] columnsWidth = new int[titles.size()];
			int maxColLength = 300;//列最宽设置
			
			String dataCategory = "";//在不同的数据类别前 都要添加表头
			int i=0;
			if (InfoList.size() > 0) {
				for(int m=0; m<InfoList.size(); m++ ){
					Map<String, String> itemMap = InfoList.get(m);	
					//key的第一个数据用于区分不同的数据类别
					String dataCategoryNew = itemMap.get(keys.get(0));
					//如果有新的数据类别，则插入表头
					if(!dataCategory.equals(dataCategoryNew)){					
						//写表头
						for(int j=0; j<titles.size();j++){
							String value = titles.get(j);
							ws.addCell(new Label(j, i, value, wcftitle));
							if(value.length()*3 > columnsWidth[j]){//表头的字体较大，所以*3
								columnsWidth[j] = value.length()*3;//统一按照汉字处理，一个汉字占2字符
							}
						}
						dataCategory = dataCategoryNew;
						i++;
					}
					//写数据
					for(int j=0; j<keys.size();j++){
						String value = itemMap.get(keys.get(j));
						if(value == null || "null".equalsIgnoreCase(value)){
							value = "";
						}
						ws.addCell(new Label(j, i, value, wcf));
						if(value.length()*2 > columnsWidth[j]){
							columnsWidth[j] = value.length()*2;//统一按照汉字处理，一个汉字占2字符
						}
						if(columnsWidth[j] > maxColLength ){
							columnsWidth[j] = maxColLength;
						}
						//ws.setColumnView(j, cellView);
					}
					i++;
				}
			} else {
				//写表头
				for(int j=0; j<titles.size();j++){
					String value = titles.get(j);
					ws.addCell(new Label(j, i, value, wcftitle));
					if(value.length()*3 > columnsWidth[j]){//表头的字体较大，所以*3
						columnsWidth[j] = value.length()*3;//统一按照汉字处理，一个汉字占2字符
					}
				}
			}
			
			for(int j=0; j<titles.size();j++){
				ws.setColumnView(j, columnsWidth[j]);
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e){
			e.printStackTrace();
		} catch (WriteException e){
			e.printStackTrace();
		}
		return file;
	}
	
	
	
}























