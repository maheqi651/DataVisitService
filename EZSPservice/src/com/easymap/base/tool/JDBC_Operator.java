package com.easymap.base.tool;

public class JDBC_Operator {

	public static String translate_Operator(String op) {
		if (op.equals("eq")) {
			return "=";
		} else if (op.equals("ne")) {
			return "<>";
		} else if (op.equals("gt")) {
			return ">";
		} else if (op.equals("ge")) {
			return ">=";
		} else if (op.equals("lt")) {
			return "<";
		} else if (op.equals("le")) {
			return "<=";
		}
		return op;
	}

}
