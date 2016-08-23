package com.easymap.base.tool;

public class JDBC_ColumnType {

	/**
	 * 获得SQL读取写入方式
	 * 
	 * @param typeName
	 * @param dataType
	 * @return
	 */
	public static String translate_InteractType(int dataType) {
		switch (dataType) {
		case -102: // TIMESTAMP WITH LOCAL TIME ZONE
			return "date";
		case -101: // TIMESTAMP WITH TIME ZONE
			return "date";
		case -7: // BIT
			return "boolean";
		case -6: // TINYINT
			return "short";
		case -5: // BIGINT
			return "long";
		case -4: // LONGVARBINARY
		case -3: // VARBINARY
			return "byte";
		case -1: // LONGVARCHAR
			return "string";
		case 1: // CHAR
			return "string";
		case 2: // NUMERIC
			return "double";
		case 4: // INTEGER
			return "long";
		case 5: // SMALLINT
			return "int";
		case 6: // FLOAT
		case 7: // REAL
			return "double";
		case 12: // VARCHAR
			return "string";
		case 91: // DATE
			return "date";
		case 92: // TIME
			return "date";
		case 93: // TIMESTAMP
			return "date";
		default:
			return "string";
		}
	}

}
