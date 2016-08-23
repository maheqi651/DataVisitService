package com.easymap.base.hcontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.easymap.base.readdatabase.ConnectionDB;


public class MakeResultSet {
	private static Connection conn;
    private static PreparedStatement ps;
    private ResultSet rs;
    ConnectionDB con = new ConnectionDB();
	public  ResultSet makeResultSet(String sql){
		if(sql!=null&&!sql.equals("")){
		try {
			conn = con.getConnection();
			if (conn != null) {
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				rs.close();
				ps.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
			}
		}
		}else{
			System.err.println("无可执行sql");
		}
		return rs;
	}
}
