package com.easymap.base.tool;

public class JDBC_ColumnTypeFileds {

	/**
	 * ���SQL��ȡд�뷽ʽ
	 * 
	 * @param typeName
	 * @param dataType
	 * @return
	 */
	public static String translate_InteractType(int dataType) {
		switch (dataType) {
	 
		case 1: // CHAR
			return "short";
		case 2: // NUMERIC
			return "long";
		case 3: // INTEGER
			return "float";
		case 4: // INTEGER
			return "double";
		case 5: // SMALLINT
			return "string";
		case 6: return "binary";
		case 7: // REAL
			return "date";
		case 8: // VARCHAR
			return "pixel";
		default:
			return "string";
		}
	}

}
