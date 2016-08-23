package com.easymap.base.pools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

public class ConvertJDBC {

	public static Connection getConnection(String name) {
		try {
			String className = "com.easymap.base.pools." + name.toUpperCase();
			Class<?> clazz = Class.forName(className);
			Method m = clazz.getMethod("getConnection", null);
			return (Connection) m.invoke(clazz, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
/*	public static void main(String[] args) {
		String name="YW_ZAYZY_RW";
		Connection c =getConnection(name);
		System.out.println(c);
	}*/
}
