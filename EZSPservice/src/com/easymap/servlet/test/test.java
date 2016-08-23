package com.easymap.servlet.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File f = new File("d:/6.jpg");
		BASE64Encoder encoder = new sun.misc.BASE64Encoder(); 
		BufferedImage bi;      
        try {      
            bi = ImageIO.read(f);      
            ByteArrayOutputStream baos = new ByteArrayOutputStream();      
            ImageIO.write(bi, "jpg", baos);      
            byte[] bytes = baos.toByteArray();      
                  
            System.out.println(encoder.encodeBuffer(bytes).trim());       
        } catch (IOException e) {      
            e.printStackTrace();      
        }      
		
		
		
		
		
		/*System.out.println(file.length());
		int sizes=(int) file.length();
		byte[] bbb =  new byte[sizes];
		try {
			InputStream a = new FileInputStream(file);
			a.read(bbb,0,sizes);
			 
			System.out.println(new String(bbb));
			 
			System.out.println(Integer.toBinaryString(bbb[0]));	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	}


